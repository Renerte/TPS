package de.shiirroo.tps.history;

import de.shiirroo.tps.MetricsTime;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TpsWorldHistory {

    @Getter
    private final ConcurrentHashMap<MetricsTime, List<TpsData>> tpsData = new ConcurrentHashMap<>();

    @Getter
    private final String worldName;

    public TpsWorldHistory(String worldName) {
        this.worldName = worldName;
    }

    public void addTpsRecord(MetricsTime metricsTime, TpsTimeRange range, double tps, double mspt) {
        if (!tpsData.containsKey(metricsTime)) {
            tpsData.put(metricsTime, new java.util.ArrayList<>());
        }
        if (tpsData.get(metricsTime).size() >= metricsTime.getMaxRecords()) {
            tpsData.get(metricsTime).removeFirst();
        }
        tpsData.get(metricsTime).add(new TpsData(range, tps, mspt));
    }

    public List<TpsData> getTpsData(MetricsTime metricsTime) {
        return tpsData.getOrDefault(metricsTime, java.util.Collections.emptyList());
    }

    public double getAverageTps(MetricsTime metricsTime) {
        List<TpsData> dataList = tpsData.get(metricsTime);
        if (dataList == null || dataList.isEmpty()) return 0.0;
        return dataList.stream().mapToDouble(TpsData::tps).average().orElse(0.0);
    }

    public double getAverageMspt(MetricsTime metricsTime) {
        List<TpsData> dataList = tpsData.get(metricsTime);
        if (dataList == null || dataList.isEmpty()) return 0.0;
        return dataList.stream().mapToDouble(TpsData::mspt).average().orElse(0.0);
    }



}
