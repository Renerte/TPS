package de.shiirroo.tps.tasks;

import lombok.Getter;

import java.util.logging.Logger;

public class WatchGuardTask implements Runnable {

    private static final int MAX_RETRIES = 3;
    @Getter
    private final TpsTaskRunnable task;
    private final Logger logger = Logger.getLogger(WatchGuardTask.class.getName());
    private int retryCount = 0;


    public WatchGuardTask(TpsTaskRunnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        try {
            task.run();
        } catch (Exception e) {
            logger.severe("Task " + task + " failed: " + e.getMessage());
            retry();
        }
    }

    private void retry() {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            logger.info("Retrying task " + task + " (attempt " + retryCount + ")");
            run();
        } else {
            logger.severe("Task " + task + " failed after " + MAX_RETRIES + " attempts.");
        }
    }

}
