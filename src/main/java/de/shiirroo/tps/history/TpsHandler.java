package de.shiirroo.tps.history;

import com.hypixel.hytale.server.core.universe.Universe;
import de.shiirroo.tps.MetricsTime;
import lombok.Getter;

import java.util.EnumMap;
import java.util.HashMap;

public class TpsHandler implements Runnable {

    @Getter
    private final HashMap<String, EnumMap<MetricsTime, TpsWorldHistory>> worldTpsHistory = new HashMap<>();

    public TpsHandler() {
        Universe.get().getWorlds().forEach((worldName, world) -> {
            EnumMap<MetricsTime, TpsWorldHistory> metricsTimeEnumMap = new EnumMap<>(MetricsTime.class);
            for(MetricsTime metricsTime : MetricsTime.values()) {
                metricsTimeEnumMap.put(metricsTime, new TpsWorldHistory(worldName, metricsTime));
            }
            worldTpsHistory.put(worldName, metricsTimeEnumMap);
        });

    }

    @Override
    public void run() {

    }


}
