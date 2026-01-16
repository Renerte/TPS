package de.shiirroo.tps.history;

import org.jetbrains.annotations.NotNull;

public record TpsTimeRange(long startTime, long endTime) implements Comparable<TpsTimeRange> {

    @Override
    public int compareTo(@NotNull TpsTimeRange o) {
        return Long.compare(this.startTime, o.startTime) + Long.compare(this.endTime, o.endTime);
    }

    public boolean isWithinRange(long timestamp) {
        return timestamp >= startTime && timestamp <= endTime;
    }
}