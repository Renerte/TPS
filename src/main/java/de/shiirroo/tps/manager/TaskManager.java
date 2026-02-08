package de.shiirroo.tps.manager;

import com.hypixel.hytale.server.core.HytaleServer;
import de.shiirroo.tps.Tps;
import de.shiirroo.tps.tasks.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TaskManager implements IManager {

    @Getter
    private final GuiTask guiTask;
    @Getter
    private final HudTask hudTask;
    @Getter
    private final MetricsTask metricsTask;
    @Getter
    private final WarningTask warningTask;
    @Getter
    private final HashMap<TpsTaskRunnable, ScheduledFuture<?>> tasks = new HashMap<>();

    public TaskManager() {
        this.guiTask = new GuiTask();
        this.hudTask = new HudTask();
        this.metricsTask = new MetricsTask();
        this.warningTask = new WarningTask();
    }


    public void initialize() {
        var config = Tps.get().getConfig().get();
        createTask(guiTask);
        createTask(hudTask);
        if (config.getMetricsConfig().isEnableMetrics())  createTask(metricsTask);
        if (config.isEnableTPSWarning()) createTask(warningTask);

    }

    @Override
    public void shutdown() {
        tasks.values().forEach(task -> task.cancel(true));
        tasks.clear();
    }


    private void createTask(TpsTaskRunnable task) {
        var se = HytaleServer.SCHEDULED_EXECUTOR;
        WatchGuardTask watchGuardTask = new WatchGuardTask(task);
        try {
            ScheduledFuture<?> future = se.scheduleAtFixedRate(watchGuardTask, 0, Tasks.getTaskUpdateInterval(task),  TimeUnit.MILLISECONDS);
            tasks.put(task, future);
        } catch (Exception e) {
            Tps.getLog().severe("Failed to start task: " + e.getMessage());
        }
    }


    public void updateTasks() {
        var settings = Tps.get().getConfig().get();
        updateTask(() -> settings.getMetricsConfig().isEnableMetrics(), metricsTask);
        updateTask(settings::isEnableTPSWarning, warningTask);
    }

    public void updateTask(Boolean bool, TpsTaskRunnable task) {
        updateTask(() -> bool, task);
    }

    private void updateTask(Supplier<Boolean> supplier, TpsTaskRunnable task) {
        if (supplier.get() && !tasks.containsKey(task)) {
            createTask(task);
        } else if (!supplier.get() && tasks.containsKey(task)) {
            tasks.get(task).cancel(true);
            tasks.remove(task);
        }


    }



}
