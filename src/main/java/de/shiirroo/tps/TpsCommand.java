package de.shiirroo.tps;

import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.commands.world.perf.WorldPerfCommand;
import io.netty.handler.codec.MessageAggregator;
import org.jetbrains.annotations.NotNull;
import org.jline.utils.Log;

import java.awt.*;
import java.util.logging.Logger;

public class TpsCommand extends CommandBase {

    private final static double MS = 1_000_000.0;
    //Utility Messages
    private final static Message spacer = Message.raw(" | ");
    private final static Message newLine = Message.raw("\n");

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

        Message label = Message.raw("========== Worlds – TPS / MSPT ==========").color(Color.orange);
        context.sendMessage(label);

        universe.getWorlds().forEach((worldName, world) -> {
            double liveTps = getLiveTPS(world);
            double maxMSPT = getMaxMSPT(world);
            //World Name
            Message worldLabel = Message.raw("World: ");
            Message worldNameMessage = Message.raw(worldName).color(Color.ORANGE);
            //Target TPS / MSPT
            Message target = Message.raw("    Target TPS: ");
            Message targetTps = Message.raw(String.format("%.2f ", (double) world.getTps())).color(Color.green);
            Message tpsLabel = Message.raw("| Max MSPT: ");


            Message targetMspt = Message.raw(String.format("%.2f ", getMaxMSPT(world))).color(Color.red);
            Message msptLabel = Message.raw("ms");
            //Live TPS / MSPT
            Message live  = Message.raw("             Live => ");
            Message liveTpsMessage = colorizeTps(liveTps, world.getTps());
            Message liveMsptMessage = colorizeMspt(getLiveMspt(world), maxMSPT);


            context.sendMessage(worldLabel.insert(worldNameMessage));
            context.sendMessage(target.insert(targetTps).insert(tpsLabel).insert(targetMspt).insert(msptLabel));
            context.sendMessage(live.insert(liveTpsMessage).insert(spacer).insert(liveMsptMessage));
            context.sendMessage(getAvgMessage(MetricsTime.TEN_SECONDS, world, maxMSPT));
            context.sendMessage(getAvgMessage(MetricsTime.ONE_MINUTE, world, maxMSPT));
            context.sendMessage(getAvgMessage(MetricsTime.FIVE_MINUTES, world, maxMSPT));

        });
    }

    private Message getAvgMessage(MetricsTime metricsTime, World world, double maxMSPT) {
       return Message.raw("         Ø " + metricsTime.getDisplay() + " => ")
                .insert(colorizeTps(getTPS(world, metricsTime), world.getTps()))
                .insert(Message.raw(" | ").color(Color.WHITE))
                .insert(colorizeMspt(getMspt(world, metricsTime), maxMSPT));
    }


    private Message colorizeTps(double tps, double maxTps) {
        double percent = (tps / maxTps) * 100;
        if (percent >= 85) return Message.raw(String.format("%.2f", tps)).color(Color.GREEN);
        if (percent >= 70) return Message.raw(String.format("%.2f", tps)).color(Color.YELLOW);
        return Message.raw(String.format("%.2f", tps)).color(Color.RED);
    }

    private Message colorizeMspt(double mspt, double maxMspt) {
        double percent = (mspt / maxMspt) * 100;
        if (percent <= 60) return Message.raw(String.format("%.2f ms", mspt)).color(Color.GREEN);
        if (percent <= 90) return Message.raw(String.format("%.2f ms", mspt)).color(Color.YELLOW);
        return Message.raw(String.format("%.2f ms", mspt)).color(Color.RED);
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

    private double getLiveMspt(World world) {
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return metrics.getLastValue() / MS;
    }

    private double getMspt(World world, MetricsTime metricsTime) {
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        double avgTickNanos = metrics.getAverage(metricsTime.getIndex());
        return avgTickNanos / MS;
    }
    private double getMaxMSPT(World world) {
        return world.getTickStepNanos() / MS;
    }
}
