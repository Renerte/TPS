package de.shiirroo.tps.history;

import de.shiirroo.tps.MetricsTime;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TpsWorldHistory {

    @Getter
    private final List<TpsData> tpsData = new ArrayList<>();

    @Getter
    private final String worldName;

    @Getter
    private final MetricsTime metricsTime;
    public TpsWorldHistory(String worldName, MetricsTime metricsTime) {
        this.worldName = worldName;
        this.metricsTime = metricsTime;
    }

    public void addTpsRecord(TpsTimeRange range, double tps) {
        for(TpsData data : tpsData) {
            if(data.range().compareTo(range) == 0) {
                return;
            }
        }
        tpsData.add(new TpsData(range, tps));
    }

    public Optional<TpsData> getTpsRecord(long timestamp) {
        for(TpsData data : tpsData) {
            if(data.range().isWithinRange(timestamp)) {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }




}
