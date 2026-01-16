package de.shiirroo.tps;

import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.commands.world.perf.WorldPerfCommand;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TpsCommand extends CommandBase {

    public TpsCommand() {
        super("tps", "Shows the TPS of all worlds.", false);
    }

    @Override
    protected void executeSync(@NotNull CommandContext context) {
        Universe universe = Universe.get();
        if (universe.getWorlds().isEmpty()) {
            context.sendMessage(Message.raw("No worlds found.").color(Color.RED));
            return;
        }

        Message label = Message.raw("========== Worlds TPS Performance ==========").color(Color.orange);
        context.sendMessage(label);

        universe.getWorlds().forEach((worldName, world) -> {

            double liveTps = getLiveTPS(world);

            Message worldLabel = Message.raw("World: ").color(Color.WHITE);
            Message worldNameMessage = Message.raw(worldName).color(Color.ORANGE);
            Message worldTpsLabel = Message.raw(" | World TPS: ").color(Color.WHITE);
            Message worldTpsValue = Message.raw(String.valueOf(world.getTps())).color(Color.GREEN);
            context.sendMessage(worldLabel.insert(worldNameMessage).insert(worldTpsLabel).insert(worldTpsValue));
            context.sendMessage(Message.raw("  Live TPS: " ).insert(colorize(liveTps, world.getTps())));
            context.sendMessage(Message.raw("  Average TPS (10s): ").insert(colorize(getTPS(world, MetricsTime.TEN_SECONDS), world.getTps())));
            context.sendMessage(Message.raw("  Average TPS (1m): ").insert(colorize(getTPS(world, MetricsTime.ONE_MINUTE), world.getTps())));
            context.sendMessage(Message.raw("  Average TPS (5m): ").insert(colorize(getTPS(world, MetricsTime.FIVE_MINUTES), world.getTps())));
        });
    }

    private Message colorize(double tps, double maxTps) {
        double percent = (tps / maxTps) * 100;
        if (percent >= 85) return Message.raw(String.format("%.2f", tps)).color(Color.GREEN);
        if (percent >= 70) return Message.raw(String.format("%.2f", tps)).color(Color.YELLOW);
        return Message.raw(String.format("%.2f", tps)).color(Color.RED);
    }

    private double getTPS(World world, MetricsTime metricsTime) {
        final var tickStepNanos = world.getTickStepNanos();
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return WorldPerfCommand.tpsFromDelta(metrics.getAverage(metricsTime.getIndex()), tickStepNanos);
    }

    private double getLiveTPS(World world) {
        final var tickStepNanos = world.getTickStepNanos();
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return WorldPerfCommand.tpsFromDelta(metrics.getLastValue(), tickStepNanos);
    }
}
