package de.shiirroo.tps.tasks;

import de.shiirroo.tps.Tps;
import lombok.Getter;

public enum Tasks {
    HUD(1000),
    GUI(1000),
    METRICS(1000),
    WARNING(1000);

    @Getter
    private final Integer defaultTaskUpdateInterval;

    Tasks(Integer defaultTaskUpdateInterval) {
        this.defaultTaskUpdateInterval = defaultTaskUpdateInterval;
    }


    public static Integer getTaskUpdateInterval(TpsTaskRunnable taskRunnable) {
        return Tps.get().getConfig().get().getMetricsConfig().getTaskUpdateInterval(taskRunnable.getTask());
    }
}
