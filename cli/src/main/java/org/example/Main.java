package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private static Map<Integer, Task> tasksMap = new HashMap<>();
    private static int id;
    private static Scanner scanner = new Scanner(System.in);
    private static List<Task> taskList = new ArrayList<>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length > 0) {
            String input = args[0].trim();
            if (input.equalsIgnoreCase("add")) {
                addTask();
            }
            if (input.equalsIgnoreCase("show_all")) {
                showAllTasks();
            }
            if(input.equalsIgnoreCase("help")){
                showHelp();
            }
        }
    }

    public static boolean saveToFile() {
        boolean flag = false;

        try {
            String currentPath = "C:\\Tasker\\";
            File directory = new File(currentPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(currentPath + "tasks.json");

            // If the file exists, read existing tasks
            List<Task> existingTasks = new ArrayList<>();
            if (file.exists()) {
                // Read existing tasks from the file and add to the list
                existingTasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});
            }

            // Add new tasks to the existing list
            existingTasks.addAll(taskList);

            // Write the updated list back to the file
            objectMapper.writeValue(file, existingTasks);

            flag = true;
            System.out.println("Tasks appended to JSON file successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }


    public static void showHelp() {
        System.out.println("** WELCOME TO TASKER **");
        System.out.println("==============================================");
        System.out.println("ADD : Add a task.");
        System.out.println("DEL: Delete a task.");
        System.out.println("DEL_ALL: Delete all tasks");
        System.out.println("SHOW_ALL: Show all tasks.");
        System.out.println("SHOW_DONE: Show all completed tasks.");
        System.out.println("SHOW_PENDING: Show all pending tasks.");
        System.out.println("===============================================");
    }

    public static int getKey() {
        int lastKey = 0;
        String currentPath = "C:\\Tasker\\tasks.json";  // File location

        File file = new File(currentPath);
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Task> tasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});

                for (Task task : tasks) {
                    tasksMap.put(task.getId(), task);
                    lastKey = Math.max(lastKey, task.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastKey + 1;  // Return the next available Task ID
    }

    public static void loadPreviousData() {
        String currentPath = "C:\\Tasker\\tasks.json";

        File file = new File(currentPath);
        if (file.exists()) {
            try {
                Task[] tasks = objectMapper.readValue(file, Task[].class);
                for (Task task : tasks) {
                    tasksMap.put(task.getId(), task);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showAllTasks() {
        loadPreviousData();
        if (tasksMap != null && tasksMap.size() > 0) {
            tasksMap.forEach((k, v) -> System.out.println(v)); // Print task object directly
        } else {
            System.out.println("No tasks to show...");
        }
    }

    public static boolean isValidInput(String input) {
        return input != null && input.length() > 0;
    }

    public static void addTask() {
        System.out.print("Enter the title : ");
        String title = scanner.nextLine();
        System.out.println("Enter the task : ");
        String data = scanner.nextLine();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the date and time format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the date and time
        String date = currentDateTime.format(dateFormatter);
        String time = currentDateTime.format(timeFormatter);

        // Combine date and time into a single string
        String dateTime = date + " " + time;
        Task task = new Task();
        task.setId(getKey());
        task.setTitle(title);
        task.setData(data);
        task.setCreationDate(dateTime);
        task.setStatus("NOT DONE");

        if (isValidInput(data)) {
            taskList.add(task);
            saveToFile();  // Save to JSON file after adding a task
            System.out.println("Task added successfully.");
        } else {
            System.out.println("Invalid input: Task cannot be empty.");
        }
    }
}
