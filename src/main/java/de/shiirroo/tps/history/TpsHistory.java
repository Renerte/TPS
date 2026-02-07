package de.shiirroo.tps.history;

import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.MetricsTime;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TpsHistory {

    @Getter
    private final ConcurrentHashMap<String, TpsWorldHistory> history = new ConcurrentHashMap<>();
    private static final TpsHistory instance = new TpsHistory();

    public TpsHistory() {
    }

    public void addMetrics(World world, MetricsTime time, WorldMetrics metrics) {
        addMetrics(world.getName(), world.getWorldConfig().getUuid(), time, metrics);
    }

    public void addMetrics(String worldName, UUID worldUUID, MetricsTime time, WorldMetrics metrics) {
        history.computeIfAbsent(worldName, k -> new TpsWorldHistory(worldName, worldUUID)).addWorldMetrics(time, metrics);
    }

    public static TpsHistory get() {
        return instance;
    }

    public String asJson() {
        return history.values().stream().map(TpsWorldHistory::toJson).collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

}
