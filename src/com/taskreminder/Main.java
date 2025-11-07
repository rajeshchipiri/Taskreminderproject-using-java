package com.taskreminder;

import com.taskreminder.model.Task;
import com.taskreminder.service.ReminderScheduler;
import com.taskreminder.service.TaskManager;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public static void main(String[] args) {
        Path storage = Path.of("tasks.csv");
        TaskManager manager = new TaskManager(storage);
        ReminderScheduler scheduler = new ReminderScheduler(manager);
        scheduler.start();

        System.out.println("=== Task Reminder (Core Java) ===");
        try (Scanner sc = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                System.out.print("Choose> ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        System.out.print("Title: ");
                        String title = sc.nextLine().trim();
                        System.out.print("Description: ");
                        String desc = sc.nextLine().trim();
                        System.out.print("Due (YYYY-MM-DDTHH:MM) or leave blank: ");
                        String due = sc.nextLine().trim();
                        LocalDateTime dueAt = null;
                        if (!due.isEmpty()) {
                            try {
                                dueAt = LocalDateTime.parse(due, FMT);
                            } catch (Exception e) {
                                System.out.println("Invalid date format. Task saved without due date.");
                            }
                        }
                        Task t = manager.addTask(title, desc, dueAt);
                        System.out.println("Task added: " + t);
                    }
                    case "2" -> {
                        List<Task> list = manager.listAll();
                        if (list.isEmpty()) System.out.println("No tasks.");
                        else list.forEach(System.out::println);
                    }
                    case "3" -> {
                        System.out.print("Task id to complete: ");
                        String idStr = sc.nextLine().trim();
                        try {
                            long id = Long.parseLong(idStr);
                            boolean ok = manager.markComplete(id);
                            System.out.println(ok ? "Marked complete." : "Task not found.");
                        } catch (Exception e) {
                            System.out.println("Invalid id.");
                        }
                    }
                    case "4" -> {
                        System.out.print("Task id to delete: ");
                        String idStr = sc.nextLine().trim();
                        try {
                            long id = Long.parseLong(idStr);
                            boolean ok = manager.delete(id);
                            System.out.println(ok ? "Deleted." : "Task not found.");
                        } catch (Exception e) {
                            System.out.println("Invalid id.");
                        }
                    }
                    case "5" -> {
                        System.out.println("Exiting...");
                        running = false;
                    }
                    default -> System.out.println("Unknown option.");
                }
            }
        } finally {
            scheduler.stop();
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add Task");
        System.out.println("2. List Tasks");
        System.out.println("3. Mark Complete");
        System.out.println("4. Delete Task");
        System.out.println("5. Exit");
    }
}
