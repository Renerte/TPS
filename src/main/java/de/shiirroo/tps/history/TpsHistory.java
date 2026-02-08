package de.shiirroo.tps.history;

import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.Tps;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TpsHistory {

    @Getter
    private final ConcurrentHashMap<String, TpsWorldHistory> history = new ConcurrentHashMap<>();

    public TpsHistory() {
    }

    public void addMetrics(World world, MetricsTime time, WorldMetrics metrics) {
        addMetrics(world.getName(), world.getWorldConfig().getUuid(), time, metrics);
    }

    public void addMetrics(String worldName, UUID worldUUID, MetricsTime time, WorldMetrics metrics) {
        history.computeIfAbsent(worldName, k -> new TpsWorldHistory(worldName, worldUUID)).addWorldMetrics(time, metrics);
    }

    public String getQueryMetricsAsJson(String query) {
        if (query == null || query.isEmpty()) return asJson();
        if (query.equals("latest")) {
            return latestMetricsAsJson();
        }
        return asJson();
    }

    public String asJson() {
        return history.values().stream().map(TpsWorldHistory::toJson).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    public String latestMetricsAsJson() {
        return history.values().stream().map(TpsWorldHistory::getLatestMetrics).map(TpsWorldHistory::toJson).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

    public static TpsHistory getTPSHistory() {
       return Tps.get().getTpsManager().getTaskManager().getMetricsTask().getTpsHistory();
    }


}
