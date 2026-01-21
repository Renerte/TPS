package de.shiirroo.tps.history;

public record TpsData(TpsTimeRange range, double tps, double mspt) {

    @Override
    public String
    toString() {
        return "TpsData{" +
                "range=" + range +
                ", tps=" + tps +
                ", mspt=" + mspt +
                '}';
    }
}