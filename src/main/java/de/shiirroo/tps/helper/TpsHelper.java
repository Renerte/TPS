package de.shiirroo.tps.helper;

import com.hypixel.hytale.metrics.metric.HistoricMetric;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.commands.world.perf.WorldPerfCommand;
import de.shiirroo.tps.MetricsTime;

import java.awt.*;
import java.util.logging.Logger;

public class TpsHelper {

    public final static double MS = 1_000_000.0;
    //Utility Messages
    public final static Message spacer = Message.raw(" | ");
    public final static Message newLine = Message.raw("\n");
    private static final Logger logger = Logger.getLogger("TpsHelper");

    public static Message getAvgMessage(MetricsTime metricsTime, World world, double maxMSPT) {
        return Message.raw("         Ø " + metricsTime.getDisplay() + " => ")
                .insert(colorizeTps(getTPS(world, metricsTime), world.getTps()))
                .insert(Message.raw(" | ").color(Color.WHITE))
                .insert(colorizeMspt(getMspt(world, metricsTime), maxMSPT));
    }


    public static Message colorizeTps(double tps, double maxTps) {
        double percent = (tps / maxTps) * 100;
        if (percent >= 85) return Message.raw(String.format("%.2f", tps)).color(Color.GREEN);
        if (percent >= 70) return Message.raw(String.format("%.2f", tps)).color(Color.YELLOW);
        return Message.raw(String.format("%.2f", tps)).color(Color.RED);
    }

    public static Message colorizeMspt(double mspt, double maxMspt) {
        double percent = (mspt / maxMspt) * 100;
        if (percent <= 60) return Message.raw(String.format("%.2f ms", mspt)).color(Color.GREEN);
        if (percent <= 90) return Message.raw(String.format("%.2f ms", mspt)).color(Color.YELLOW);
        return Message.raw(String.format("%.2f ms", mspt)).color(Color.RED);
    }

    public static double getTPS(World world, MetricsTime metricsTime) {
        if (metricsTime == MetricsTime.NOW) return getLiveTPS(world);
        final var tickStepNanos = world.getTickStepNanos();
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return WorldPerfCommand.tpsFromDelta(metrics.getAverage(metricsTime.getIndex()), tickStepNanos);
    }

    public static double getTPSAvg(World world, MetricsTime metricsTime) {
        if (metricsTime == MetricsTime.NOW) return getLiveTPS(world);
        return getTPS(world, metricsTime);
    }

    public static double getMSPTAvg(World world, MetricsTime metricsTime) {
        if (metricsTime == MetricsTime.NOW) return getLiveMspt(world);
        return getMspt(world, metricsTime);
    }

    public static double getLiveTPS(World world) {
        final var tickStepNanos = world.getTickStepNanos();
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return WorldPerfCommand.tpsFromDelta(metrics.getLastValue(), tickStepNanos);
    }

    public static double getLiveMspt(World world) {
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        return metrics.getLastValue() / MS;
    }

    public static double getMspt(World world, MetricsTime metricsTime) {
        if (metricsTime == MetricsTime.NOW) return getLiveMspt(world);
        HistoricMetric metrics = world.getBufferedTickLengthMetricSet();
        double avgTickNanos = metrics.getAverage(metricsTime.getIndex());
        return avgTickNanos / MS;
    }
    public static double getMaxMSPT(World world) {
        return world.getTickStepNanos() / MS;
    }

    public static Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static Integer parseLong(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }




}
