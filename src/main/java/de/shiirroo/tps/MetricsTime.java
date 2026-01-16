package de.shiirroo.tps;

import lombok.Getter;

public enum MetricsTime {
    TEN_SECONDS(0, 10, 100, "10s"),
    ONE_MINUTE(1, 60, 100, "1m "),
    FIVE_MINUTES(2, 300, 100, "5m ")

    ;

    @Getter
    private final int index;
    @Getter
    private final int seconds;
    @Getter
    private final int max_records;
    @Getter
    private final String display;

    MetricsTime(int index, int seconds, int max_records, String display) {
        this.index = index;
        this.seconds = seconds;
        this.max_records = max_records;
        this.display = display;
    }

}
