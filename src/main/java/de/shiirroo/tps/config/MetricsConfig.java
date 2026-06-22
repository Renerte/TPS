package de.shiirroo.tps.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.common.util.MapUtil;
import de.shiirroo.tps.MetricsTime;
import de.shiirroo.tps.tasks.Tasks;
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
            .append(new KeyedCodec<>("TaskUpdateIntervals", new EnumMapCodec<>(Tasks.class, Codec.INTEGER)),
                    (MetricsConfig, newMap, extraInfo) -> MetricsConfig.TaskUpdateIntervals = MapUtil.combineUnmodifiable(MetricsConfig.TaskUpdateIntervals, newMap, () -> new EnumMap<>(Tasks.class)),
                    (MetricsConfig, extraInfo) -> MetricsConfig.TaskUpdateIntervals).add()

            .build();
    @Getter
    @Setter
    private boolean EnableMetrics = false;
    @Getter
    private Map<MetricsTime, Integer> MetricsHistorySize = new EnumMap<>(MetricsTime.class);
    @Getter
    private Map<Tasks, Integer> TaskUpdateIntervals = new EnumMap<>(Tasks.class);
    public MetricsConfig() {
        for (MetricsTime time : MetricsTime.values()) {
            MetricsHistorySize.put(time, time.getDefaultMaxRecords());
        }
        for (Tasks task : Tasks.values()) {
            TaskUpdateIntervals.put(task, task.getDefaultTaskUpdateInterval());
        }

    }

    public Integer getMetricsHistorySize(MetricsTime time) {
        return MetricsHistorySize.getOrDefault(time, time.getDefaultMaxRecords());
    }

    public Integer getTaskUpdateInterval(Tasks task) {
        return TaskUpdateIntervals.getOrDefault(task, task.getDefaultTaskUpdateInterval());
    }

}
