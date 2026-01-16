package de.shiirroo.tps;

public enum MetricsTime {
    TEN_SECONDS(0, 10),
    ONE_MINUTE(1, 60),
    FIVE_MINUTES(2, 300)

    ;

    private final int index;
    private final int seconds;

    MetricsTime(int index, int seconds) {
        this.index = index;
        this.seconds = seconds;
    }

    public int getIndex() {
        return index;
    }

    public int getSeconds() {
        return seconds;
    }

}
