package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

public class HudManager extends TickingSystem<EntityStore> {

    private final Logger logger = Logger.getLogger("HudManager");
    private final List<PlayerRef> playersWithHud = new ArrayList<>();
    private static final long ONE_SECOND_NANOS = 1_000_000_000L;
    private long lastUpdate = 0;


    public void update() {
        if (playersWithHud.isEmpty()) return;
        for (PlayerRef playerRef : playersWithHud) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref != null) {
                Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                if (player != null) {
                    if (player.getHudManager().getCustomHud() == null) {
                        continue;
                    }
                    player.getHudManager().getCustomHud().update(false, new UICommandBuilder());
                }
            }
        }
    }

    public void setupPlayer(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef)) return;
        try {
            Class<?> multiHudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
            Object multiHudInstance = multiHudClass.getMethod("getInstance").invoke(null);
            multiHudClass.getMethod("setCustomHud", Player.class, PlayerRef.class, String.class, CustomUIHud.class)
                    .invoke(multiHudInstance, player, playerRef, "TpsHud", new TpsHud(playerRef));
        } catch (ClassNotFoundException e) {
            logger.severe("HUD class not found");
            player.getHudManager().setCustomHud(playerRef, new TpsHud(playerRef));
        } catch (Exception e) {
            logger.severe("Error setting up player HUD: " + e.getMessage());
            return;
        }
        playersWithHud.add(playerRef);
    }

    public void removePlayerRef(PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef)) return;
        playersWithHud.remove(playerRef);
    }



    public void removePlayerHud(Player player, PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef)) return;
        playersWithHud.remove(playerRef);
        try {
            Class<?> multiHudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
            Object multiHudInstance = multiHudClass.getMethod("getInstance").invoke(null);;
            multiHudClass.getMethod("setCustomHud", Player.class, PlayerRef.class, String.class, CustomUIHud.class)
                    .invoke(multiHudInstance, player, playerRef, "TpsHud", new NoneHud(playerRef));
        } catch (ClassNotFoundException e) {
           player.getHudManager().setCustomHud(playerRef, new NoneHud(playerRef));
        } catch (Exception e) {
            logger.severe("Error removing player HUD: " + e.getMessage());
            return;
        }
    }


    public boolean toggleHud(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef)) {
            removePlayerHud(player, playerRef);
            return false;
        } else {
            setupPlayer(player, playerRef);
            return true;
        }
    }

    @Override
    public void tick(float value, int i, @NotNull Store<EntityStore> store) {
        long now = System.nanoTime();
        if (now - lastUpdate < ONE_SECOND_NANOS) return;
        lastUpdate = now;
        if (playersWithHud.isEmpty()) return;
        update();
    }

}
