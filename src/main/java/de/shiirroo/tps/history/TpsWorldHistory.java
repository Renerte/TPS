package de.shiirroo.tps.history;

import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.helper.GsonHelper;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TpsWorldHistory implements Serializable {

    @Getter
    private final String worldName;
    @Getter
    private final String worldUUID;

    private final ConcurrentHashMap<MetricsTime, ArrayList<WorldMetrics>> worldMetricsMap = new ConcurrentHashMap<>();

    public TpsWorldHistory(String worldName, UUID worldUUID) {
        this.worldName = worldName;
        this.worldUUID = worldUUID.toString();
    }

    public void addWorldMetrics(MetricsTime metricsTime, WorldMetrics metrics) {
        ArrayList<WorldMetrics> list = worldMetricsMap.computeIfAbsent(metricsTime, k -> new ArrayList<>());
        var configMaxRecords = Tps.get().getConfig().get().getMetricsConfig().getMetricsHistorySize(metricsTime);
        configMaxRecords = configMaxRecords > 0 ? configMaxRecords : metricsTime.getDefaultMaxRecords();
        if (list.size() >= configMaxRecords) list.removeFirst();
        list.add(metrics);
    }

    public ArrayList<WorldMetrics> getWorldMetrics(MetricsTime metricsTime) {
        return worldMetricsMap.get(metricsTime);
    }


    public HashMap<MetricsTime, ArrayList<WorldMetrics>> getWorldMetricsMap() {
        return new HashMap<>(worldMetricsMap);
    }

    public String toJson(){
        return GsonHelper.GSON.toJson(this);
    }
    public static String toJson(TpsMetrics data) {
        return GsonHelper.GSON.toJson(data);
    }

}
