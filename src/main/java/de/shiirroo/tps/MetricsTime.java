package de.shiirroo.tps;

import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.Config;
import de.shiirroo.tps.config.TPSConfig;
import de.shiirroo.tps.helper.TpsHelper;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

public enum MetricsTime {
    NOW(0, 1, 20, "Now"),
    TEN_SECONDS(0, 10, 10, "10s"),
    ONE_MINUTE(1, 60, 10, "1m "),
    FIVE_MINUTES(2, 300, 10, "5m ");

    @Getter
    private final int index;
    @Getter
    private final int seconds;
    @Getter
    private final int defaultMaxRecords;
    @Getter
    private final String display;

    MetricsTime(int index, int seconds, int defaultMaxRecords, String display) {
        this.index = index;
        this.seconds = seconds;
        this.defaultMaxRecords = defaultMaxRecords;
        this.display = display;
    }

    public static List<MetricsTime> getMetricsTimeList() {
        return List.of(MetricsTime.values());
    }

    public static HashMap<Integer, Double[]> getAllTpsMpst(World world) {
        HashMap<Integer, Double[]> tpsMap = new HashMap<>();
        for (MetricsTime mt : MetricsTime.values()) {
            tpsMap.put(mt.getSeconds(), new Double[]{
                    TpsHelper.getTPS(world, mt),
                    TpsHelper.getMspt(world, mt)
            });
        }
        return tpsMap;
    }

    public static HashMap<Integer, Double[]> getAllTpsMpst(MetricsTime ignore, World world) {
        HashMap<Integer, Double[]> tpsMap = new HashMap<>();
        for (MetricsTime mt : MetricsTime.values()) {
            if (mt == ignore) continue;
            tpsMap.put(mt.getSeconds(), new Double[]{
                    TpsHelper.getTPS(world, mt),
                    TpsHelper.getMspt(world, mt)
            });
        }
        return tpsMap;
    }

    public double getTps(World world) {
        return TpsHelper.getTPS(world, this);
    }

    public double getMspt(World world) {
        return TpsHelper.getMspt(world, this);
    }

    public int maxRecords(Config<TPSConfig> config) {
        return config.get().getMetricsConfig().getMetricsHistorySize(this);
    }

    public Double[] getTpsMspt(World world) {
        return new Double[]{
                TpsHelper.getTPS(world, this),
                TpsHelper.getMspt(world, this)
        };
    }


}
