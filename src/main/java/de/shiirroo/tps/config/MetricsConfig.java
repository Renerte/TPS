package de.shiirroo.tps.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.common.util.MapUtil;
import de.shiirroo.tps.MetricsTime;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

public class MetricsConfig {


    public static final BuilderCodec<MetricsConfig> CODEC = BuilderCodec.builder(MetricsConfig.class, MetricsConfig::new)
            .append(new KeyedCodec<Boolean>("EnableMetrics", Codec.BOOLEAN),
                    (MetricsConfig, newbool, extraInfo) -> MetricsConfig.EnableMetrics = newbool,
                    (MetricsConfig, extraInfo) -> MetricsConfig.EnableMetrics).add()
            .append(new KeyedCodec<>("MetricsHistorySize", new EnumMapCodec<>(MetricsTime.class, Codec.INTEGER)),
                    (MetricsConfig, newMap, extraInfo) -> MetricsConfig.MetricsHistorySize = MapUtil.combineUnmodifiable(MetricsConfig.MetricsHistorySize, newMap, () -> new EnumMap<>(MetricsTime.class)),
                    (MetricsConfig, extraInfo) -> MetricsConfig.MetricsHistorySize).add()
            .build();



    public MetricsConfig() {
        for (MetricsTime time : MetricsTime.values()) {
            MetricsHistorySize.put(time, time.getDefaultMaxRecords());
        }
    }


    @Getter @Setter
    private boolean EnableMetrics = false;
    @Getter
    private Map<MetricsTime , Integer> MetricsHistorySize = new EnumMap<>(MetricsTime.class);

    public Integer getMetricsHistorySize(MetricsTime time) {
        return MetricsHistorySize.getOrDefault(time, time.getDefaultMaxRecords());
    }
}
