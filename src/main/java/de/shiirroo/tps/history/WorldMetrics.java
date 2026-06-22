package de.shiirroo.tps.history;

import com.hypixel.hytale.server.core.universe.world.World;
import de.shiirroo.tps.MetricsTime;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class WorldMetrics {

    @Getter
    private final double tps;
    @Getter
    private final double mspt;
    @Getter
    private final String time;

    public WorldMetrics(double tps, double mspt, String time) {
        this.tps = tps;
        this.mspt = mspt;
        this.time = time;
    }

    public WorldMetrics(double tps, double mspt) {
        this.tps = tps;
        this.mspt = mspt;
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public WorldMetrics(World world, MetricsTime time) {
        this.tps = time.getTps(world);
        this.mspt = time.getMspt(world);
        this.time = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
