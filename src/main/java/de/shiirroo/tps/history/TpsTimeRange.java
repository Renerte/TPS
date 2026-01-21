package de.shiirroo.tps.history;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class TpsTimeRange implements Comparable<TpsTimeRange> {

    @Getter
    private final LocalTime startTime;
    @Getter
    private final LocalTime endTime;
    @Getter
    private final long interval;
    @Getter
    private final long rangeIndex;


    public TpsTimeRange(long startTime, long endTime){
        this.startTime = LocalTime.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.systemDefault());
        this.endTime = LocalTime.ofInstant(Instant.ofEpochSecond(endTime), ZoneId.systemDefault());
        this.rangeIndex = startTime / ((endTime - startTime) + 1);
        this.interval = (endTime - startTime) + 1;
    }

    public TpsTimeRange(int secondOfDay, int interval){
        this.rangeIndex = secondOfDay / interval;
        long rangeStartSecond = rangeIndex * interval;
        long rangeEndSecond = rangeStartSecond + interval - 1;
        this.interval = interval;
        this.startTime = LocalTime.ofSecondOfDay(rangeStartSecond);
        this.endTime   = LocalTime.ofSecondOfDay(rangeEndSecond);
    }


    @Override
    public int compareTo(@NotNull TpsTimeRange o) {
        return this.startTime.compareTo(o.startTime) + this.endTime.compareTo(o.endTime);
    }


    @Override
    public String toString() {
        return "TpsTimeRange{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", interval=" + interval +
                ", rangeIndex=" + rangeIndex +
                '}';
    }
}