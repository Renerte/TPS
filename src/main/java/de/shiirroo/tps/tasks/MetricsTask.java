package de.shiirroo.tps.tasks;

import com.hypixel.hytale.server.core.universe.Universe;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.history.TpsHistory;
import de.shiirroo.tps.history.TpsMetrics;
import de.shiirroo.tps.history.WorldMetrics;
import de.shiirroo.tps.kumo.TPSWebsocket;
import de.shiirroo.tps.kumo.TpsData;
import lombok.Getter;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.EnumMap;

public class MetricsTask implements TpsTaskRunnable {

    @Getter
    private final EnumMap<MetricsTime, Integer> metricsUpdateTime = new EnumMap<>(MetricsTime.class);
    private TPSWebsocket tpsWebsocket;
    @Getter
    private final TpsHistory tpsHistory = new TpsHistory();
    @Getter
    private final Tasks task = Tasks.METRICS;

    private boolean checkRange(int currentIndex, MetricsTime mt) {
        Integer last = metricsUpdateTime.getOrDefault(mt, 0);
        boolean isInRange = currentIndex  <= last;
        if (!isInRange) metricsUpdateTime.put(mt, currentIndex);
        return isInRange;
    }


    @Override
    public void run() {
        if (Tps.get().getConfig().get().getMetricsConfig().isEnableMetrics())  updateTpsHistory();
    }


    private void updateTpsHistory() {
        try {
            int secondOfDay = LocalTime.now().toSecondOfDay();
            ZonedDateTime now = ZonedDateTime.now();
            ArrayList<TpsMetrics> newRecords = new ArrayList<>();
            MetricsTime[] metricsTimes = getMetricsTimes(secondOfDay);
            if (metricsTimes.length > 0)  newRecords = getLatestMetrics(metricsTimes, now);
            TpsData dataDTO = new TpsData(newRecords);
            handleWebsocket(dataDTO);


        } catch (Exception e) {
            Tps.getLog().severe("TPS History update failed: " + e.getMessage());
        }
    }

    private void handleWebsocket(TpsData dataDTO) {
        if (Tps.get().getConfig().get().getKumoConfig().isEnableKumoSupport()) {
            if (tpsWebsocket != null && tpsWebsocket.isOpen()) {
                tpsWebsocket.sendTPS(dataDTO);
            }
        }
    }


    private MetricsTime[] getMetricsTimes(int secondOfDay) {
        MetricsTime[] metricsTimes = new MetricsTime[MetricsTime.values().length];
        for (int i = 0; i < MetricsTime.values().length; i++) {
            MetricsTime mt = MetricsTime.values()[i];
            int currentIndex = secondOfDay / mt.getSeconds();
            boolean range = checkRange(currentIndex, mt);
            if (!range) metricsTimes[i] = mt;
        }
        return metricsTimes;
    }


    private ArrayList<TpsMetrics> getLatestMetrics(MetricsTime[] metricsTimes, ZonedDateTime now) {
        ArrayList<TpsMetrics> latestMetrics = new ArrayList<>();
        Universe.get().getWorlds().forEach((worldName, world) -> {
            TpsMetrics tpsMetrics = new TpsMetrics(worldName, world.getWorldConfig().getUuid(), now);
            for (MetricsTime time : metricsTimes) {
                if (time == null) continue;
                double tps = time.getTps(world);
                double mspt = time.getMspt(world);
                WorldMetrics worldMetrics = new WorldMetrics(world, time);
                tpsHistory.addMetrics(world, time, worldMetrics);
                tpsMetrics.addTpsMspt(time, tps, mspt);
            }
            latestMetrics.add(tpsMetrics);
        });
        return latestMetrics;
    }

}
