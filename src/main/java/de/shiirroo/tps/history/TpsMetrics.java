package de.shiirroo.tps.history;

import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.helper.GsonHelper;
import lombok.Getter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;

public class TpsMetrics implements Serializable {
    @Getter
    private final String worldName;
    @Getter
    private final UUID worldUUID;
    @Getter
    private final String time;
    @Getter
    private final HashMap<Integer, Double[]> tpsMstpMap = new HashMap<>();

    public TpsMetrics(String worldName, UUID worldUUID, ZonedDateTime time){
        this.worldName = worldName;
        this.worldUUID = worldUUID;
        this.time = time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public void addTpsMspt(MetricsTime mt, Double tps, Double mspt){
        tpsMstpMap.put(mt.getSeconds(), new Double[]{tps, mspt});
    }


    public Double getTps(MetricsTime mt){
        Double[] tpsMspt = tpsMstpMap.getOrDefault(mt.getSeconds(), new Double[]{-1.0, -1.0});
        return tpsMspt[0];
    }

    public Double getMspt(MetricsTime mt){
        Double[] tpsMspt = tpsMstpMap.getOrDefault(mt.getSeconds(), new Double[]{-1.0, -1.0});
        return tpsMspt[1];
    }

    public String toJson(){
        return GsonHelper.GSON.toJson(this);
    }


    public static String toJson(TpsMetrics data) {
        return GsonHelper.GSON.toJson(data);
    }

    @Override
    public String toString() {
        return "TpsData2{" +
                "worldName='" + worldName + '\'' +
                ", worldUUID=" + worldUUID +
                ", time='" + time + '\'' +
                ", tpsMstpMap=" + tpsMstpMap +
                '}';
    }
}