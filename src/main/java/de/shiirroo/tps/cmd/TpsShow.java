package de.shiirroo.tps.cmd;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TpsShow  extends AbstractPlayerCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsShow(TpsManager tpsManager) {
        super("show", "Display TPS on HUD.", false);
        requirePermission("tps.command.tps.show");
        this.tpsManager = tpsManager;
    }

    @Override
    protected void execute(@NotNull CommandContext context, @NotNull Store store, @NotNull Ref storeRef, @NotNull PlayerRef playerRef, @NotNull World world) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref != null) {
            Player player = ref.getStore().getComponent(ref, Player.getComponentType());
            if (player == null) {
                Tps.getLog().warning("Player not found");
                return;
            }
            boolean toggleTo = tpsManager.toggleHud(player, playerRef);
            if (toggleTo) {
                player.sendMessage(Message.raw("TPS HUD enabled").color(Color.GREEN));
            } else {
                player.sendMessage(Message.raw("TPS HUD disabled").color(Color.RED));
            }

        }
    }

}
