package com.taskreminder.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private long id;
    private String title;
    private String description;
    private LocalDateTime dueAt; // ISO_LOCAL_DATE_TIME
    private boolean completed;
    private boolean reminderSent;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Task() {}

    public Task(long id, String title, String description, LocalDateTime dueAt, boolean completed, boolean reminderSent) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.completed = completed;
        this.reminderSent = reminderSent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public String toCsvLine() {
        // id,title,description,dueAt,completed,reminderSent
        String safeTitle = title == null ? "" : title.replace("\n", " ").replace(",", " ");
        String safeDesc = description == null ? "" : description.replace("\n", " ").replace(",", " ");
        String due = dueAt == null ? "" : dueAt.format(FMT);
        return id + "," + safeTitle + "," + safeDesc + "," + due + "," + completed + "," + reminderSent;
    }

    public static Task fromCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",", -1);
        try {
            long id = Long.parseLong(parts[0]);
            String title = parts.length > 1 ? parts[1] : "";
            String desc = parts.length > 2 ? parts[2] : "";
            LocalDateTime due = null;
            if (parts.length > 3 && !parts[3].isEmpty()) {
                due = LocalDateTime.parse(parts[3], FMT);
            }
            boolean completed = parts.length > 4 && Boolean.parseBoolean(parts[4]);
            boolean reminderSent = parts.length > 5 && Boolean.parseBoolean(parts[5]);
            return new Task(id, title, desc, due, completed, reminderSent);
        } catch (Exception e) {
            // bad line
            return null;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", dueAt=" + (dueAt == null ? "-" : dueAt.format(FMT)) +
                ", completed=" + completed +
                '}';
    }
}
