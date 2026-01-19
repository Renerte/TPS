package de.shiirroo.tps.hud;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.hud.adapter.HudAdapterSelector;
import de.shiirroo.tps.page.TpsGuiPage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TpsManager implements Runnable {

    private final List<UUID> playersWithHud = new ArrayList<>();
    private final ConcurrentHashMap<UUID, TpsGuiPage> playersWithGui = new ConcurrentHashMap<>();
    private final static String HUD_ID = "TpsHud";

    public void update() {
        updateGuiHud();
        updatePlayerHud();
    }

    private void updatePlayerHud() {
        if (playersWithHud.isEmpty()) return;
        for (UUID uuid : playersWithHud) {
            PlayerRef playerRef = Universe.get().getPlayer(uuid);
            if (playerRef == null) continue;
            if (playerRef.getWorldUuid() == null) continue;
            World world = Universe.get().getWorld(playerRef.getWorldUuid());
            if (world == null) continue;
            world.execute(() -> {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref != null) {
                    Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                    if (player != null) {
                        HudAdapterSelector.get().updatePlayerHud(player, HUD_ID);
                    }
                }
            });
        }
    }
    private void updateGuiHud() {
        if (playersWithGui.isEmpty()) return;
        for (UUID uuid : playersWithGui.keySet()) {
            PlayerRef playerRef = Universe.get().getPlayer(uuid);
            if (playerRef == null) continue;
            if (playerRef.getWorldUuid() == null) continue;
            World world = Universe.get().getWorld(playerRef.getWorldUuid());
            if (world == null) continue;
            world.execute(() -> {
                Ref<EntityStore> ref = playerRef.getReference();
                if (ref != null) {
                    Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                    if (player != null) {
                        TpsGuiPage currentPage = playersWithGui.get(uuid);
                        currentPage.updatePlayerHud(world);
                    }
                }
            });
        }

    }

    private void updateTpsHistory() {

    }

    public void addGui(Player player, PlayerRef playerRef, TpsGuiPage gui) {
        if (playersWithGui.get(playerRef.getUuid()) != null) return;
        playersWithGui.put(playerRef.getUuid(), gui);
        Tps.getLog().info("Player " + playerRef.getUuid() + " added to history");
    }

    public void removeGui(Player player, PlayerRef playerRef) {
        if (playersWithGui.get(playerRef.getUuid()) == null) return;
        playersWithGui.remove(playerRef.getUuid());
        Tps.getLog().info("Player " + playerRef.getUuid() + " removed from history");
    }

    public void setupPlayer(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef.getUuid())) return;
        HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new TpsHud(playerRef));
        playersWithHud.add(playerRef.getUuid());
    }

    public void removePlayerRef(PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return;
        playersWithHud.remove(playerRef.getUuid());
    }

    public boolean removePlayerHud(Player player, PlayerRef playerRef) {
        if (!playersWithHud.contains(playerRef.getUuid())) return false;
        HudAdapterSelector.get().setCustomHud(player, playerRef, HUD_ID, new NoneHud(playerRef));
        playersWithHud.remove(playerRef.getUuid());
        return true;
    }

    public boolean toggleHud(Player player, PlayerRef playerRef) {
        if (playersWithHud.contains(playerRef.getUuid())) {
            removePlayerHud(player, playerRef);
            return false;
        } else {
            setupPlayer(player, playerRef);
            return true;
        }
    }

    @Override
    public void run() {
        update();
    }
}
