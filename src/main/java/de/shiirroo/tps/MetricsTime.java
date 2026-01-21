package de.shiirroo.tps;

import com.hypixel.hytale.server.core.universe.world.World;
import lombok.Getter;

public enum MetricsTime {
    TEN_SECONDS(0, 10, 10, "10s"),
    ONE_MINUTE(1, 60, 10, "1m "),
    FIVE_MINUTES(2, 300, 10, "5m ")

    ;

    @Getter
    private final int index;
    @Getter
    private final int seconds;
    @Getter
    private final int maxRecords;
    @Getter
    private final String display;

    MetricsTime(int index, int seconds, int maxRecords, String display) {
        this.index = index;
        this.seconds = seconds;
        this.maxRecords = maxRecords;
        this.display = display;
    }

    public double getTps(World world) {
       return TpsHelper.getTPS(world, this);
    }

    public double getMspt(World world) {
        return TpsHelper.getMspt(world, this);
    }

}
