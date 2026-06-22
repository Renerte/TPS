package de.shiirroo.tps.kumo;

import de.shiirroo.tps.helper.GsonHelper;
import de.shiirroo.tps.history.TpsMetrics;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;

public class TpsData implements Serializable {


    @Getter
    private final ArrayList<TpsMetrics> tpsData;

    public TpsData(ArrayList<TpsMetrics> tpsData) {
        this.tpsData = tpsData;
    }

    public static String toJson(TpsMetrics data) {
        return GsonHelper.GSON.toJson(data);
    }

    @Override
    public String toString() {
        return "TpsData{" +
                "tpsData=" + tpsData +
                '}';
    }

    public String toJson() {
        return GsonHelper.GSON.toJson(this);
    }


}
