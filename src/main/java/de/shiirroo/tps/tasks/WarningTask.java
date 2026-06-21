package de.shiirroo.tps.tasks;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.TpsHelper;
import de.shiirroo.tps.history.WorldMetrics;
import lombok.Getter;

import java.awt.*;
import java.util.UUID;

public class WarningTask implements TpsTaskRunnable {

    private long lastWarningTime = 0;
    private static final long WARNING_COOLDOWN_MS = 59 * 1000;
    @Getter
    private final Tasks task = Tasks.WARNING;

    @Override
    public void run() {
        if (Tps.get().getConfig().get().isEnableTPSWarning()) {
            sendTPSWarning();
        }
    }

    private void sendTPSWarning(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWarningTime < WARNING_COOLDOWN_MS) return;

        Universe.get().getWorlds().forEach((worldName, world) -> {
            world.execute(() -> {
                WorldMetrics currentMetrics = new WorldMetrics(world, MetricsTime.TEN_SECONDS);
                if (currentMetrics.getTps() < Tps.get().getConfig().get().getWarningThreshold()) {
                    for (PlayerRef playerRef : world.getPlayerRefs()) {
                        Ref<EntityStore> ref = playerRef.getReference();
                        if (ref == null) continue;
                        Player player = ref.getStore().getComponent(ref, Player.getComponentType());
                        if (player == null) continue;
                        UUID playerUuid = playerRef.getUuid();
                        if (PermissionsModule.get().hasPermission(playerUuid, "*") || PermissionsModule.get().hasPermission(playerUuid, "tps.command.tps.warning")){
                            sendWarningToPlayer(playerRef, worldName, currentMetrics.getTps());
                        }
                    }
                }
            });
        });
        lastWarningTime = currentTime;
    }


    private void sendWarningToPlayer(PlayerRef playerRef, String worldName, double tps) {
        Message first = Message.raw("TPS Warning: ").color(Color.getHSBColor(0, 0.75f, 0.90f));
        Message second = Message.raw("The TPS of world ").color(Color.LIGHT_GRAY);
        Message worldMessage = Message.raw(worldName).color(Color.ORANGE);
        Message third = Message.raw(" was below the threshold! The Last 1 minute average TPS is: ").color(Color.LIGHT_GRAY);
        Message tpsMessage = TpsHelper.colorizeTps(tps, tps);
        playerRef.sendMessage(first.insert(second).insert(worldMessage).insert(third).insert(tpsMessage));
    }
}
