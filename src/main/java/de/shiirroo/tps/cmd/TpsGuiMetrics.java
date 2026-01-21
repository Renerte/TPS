package de.shiirroo.tps.cmd;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.hud.TpsManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TpsGuiMetrics extends AbstractAsyncCommand {

    @Getter
    private final TpsManager tpsManager;

    public TpsGuiMetrics(TpsManager tpsManager) {
        super("showMetrics", "Send Metrics to console", false);
        requirePermission("tps.command.tps.showMetrics");
        this.tpsManager = tpsManager;
    }

    private void sendTpsHistoryToConsole() {
        tpsManager.getHistory().forEach((worldName, tpsWorldHistory) -> {
            Tps.getLog().info("TPS History for world: " + worldName);
            tpsWorldHistory.getTpsData().forEach((metricsTime, tpsDataList) -> {
                Tps.getLog().info("  Metrics Time: " + metricsTime + " (Max Records: " + metricsTime.getMaxRecords() + " Records Stored: " + tpsDataList.size() + ")");
                Tps.getLog().info("    Average TPS: " + String.format("%.2f", tpsWorldHistory.getAverageTps(metricsTime)) +
                        " | Average MSPT: " + String.format("%.2f", tpsWorldHistory.getAverageMspt(metricsTime)));
                tpsDataList.forEach(tpsData -> {
                    Tps.getLog().info("    Time Range: " + tpsData.range() + " | TPS: " + String.format("%.2f", tpsData.tps()) + " | MSPT: " + String.format("%.2f", tpsData.mspt()));
                });
            });

        });
    }

    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext paramCommandContext) {
        return CompletableFuture.runAsync(this::sendTpsHistoryToConsole);
    }
}
