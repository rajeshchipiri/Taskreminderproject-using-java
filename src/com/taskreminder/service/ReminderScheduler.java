package com.taskreminder.service;

import com.taskreminder.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderScheduler {
    private final TaskManager taskManager;
    private final Timer timer = new Timer(true);

    public ReminderScheduler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() {
        // run every minute
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    checkAndNotify();
                } catch (Exception e) {
                    System.err.println("Scheduler error: " + e.getMessage());
                }
            }
        }, 0L, 60 * 1000L);
    }

    private void checkAndNotify() {
        LocalDateTime window = LocalDateTime.now().plusMinutes(15);
        List<Task> due = taskManager.dueBefore(window);
        for (Task t : due) {
            System.out.println("[REMINDER] Task '" + t.getTitle() + "' is due at " + (t.getDueAt() == null ? "unknown" : t.getDueAt()));
            // mark reminder so we don't repeatedly notify
            taskManager.markReminderSent(t);
        }
    }

    public void stop() {
        timer.cancel();
    }
}
