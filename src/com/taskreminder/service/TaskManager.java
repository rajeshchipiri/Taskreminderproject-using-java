package com.taskreminder.service;

import com.taskreminder.model.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TaskManager {
    private final Path storageFile;
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(0);

    public TaskManager(Path storageFile) {
        this.storageFile = storageFile;
        load();
    }

    private void load() {
        tasks.clear();
        if (!Files.exists(storageFile)) return;
        try (BufferedReader br = Files.newBufferedReader(storageFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                Task t = Task.fromCsvLine(line);
                if (t != null) {
                    tasks.add(t);
                    idGen.updateAndGet(prev -> Math.max(prev, t.getId()));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load tasks: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(storageFile, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Task t : tasks) {
                bw.write(t.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save tasks: " + e.getMessage());
        }
    }

    public Task addTask(String title, String description, LocalDateTime dueAt) {
        long id = idGen.incrementAndGet();
        Task t = new Task(id, title, description, dueAt, false, false);
        tasks.add(t);
        save();
        return t;
    }

    public List<Task> listAll() {
        return tasks.stream().sorted(Comparator.comparingLong(Task::getId)).collect(Collectors.toList());
    }

    public Optional<Task> findById(long id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    public boolean delete(long id) {
        Optional<Task> opt = findById(id);
        if (opt.isPresent()) {
            tasks.remove(opt.get());
            save();
            return true;
        }
        return false;
    }

    public boolean markComplete(long id) {
        Optional<Task> opt = findById(id);
        if (opt.isPresent()) {
            Task t = opt.get();
            t.setCompleted(true);
            save();
            return true;
        }
        return false;
    }

    public List<Task> dueBefore(LocalDateTime time) {
        return tasks.stream()
                .filter(t -> !t.isCompleted())
                .filter(t -> !t.isReminderSent())
                .filter(t -> t.getDueAt() != null && (t.getDueAt().isBefore(time) || t.getDueAt().isEqual(time)))
                .collect(Collectors.toList());
    }

    public void markReminderSent(Task t) {
        t.setReminderSent(true);
        save();
    }
}
