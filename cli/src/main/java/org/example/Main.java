package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static Map<Integer, Task> tasksMap = new HashMap<>();
    private static int id;
    private static Scanner scanner = new Scanner(System.in);
    private static List<Task> taskList = new ArrayList<>();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length > 0) {
            String input = args[0].trim();

            if (input.equalsIgnoreCase("--add")) {
                addTask();
            }
            if (input.equalsIgnoreCase("--list")) {
                showAllTasks();
            }
            if(input.equalsIgnoreCase("--help")){
                showHelp();
            }
            if(input.equalsIgnoreCase("--list-done")){
                showAllDone();
            }
            if(input.equalsIgnoreCase("--list-in-progress")){
                showAllInProgress();
            }
            if(input.equalsIgnoreCase("--list-todo")){
                showAllTodos();
            }

            if(input.equalsIgnoreCase("--update")){
                int id = -1;
                String filed = null;
                String fieldValue = null;

                if(args[1] != null || args[1].length()>0){
                    try{
                        id = Integer.parseInt(args[1].trim());
                    }catch (Exception e){
                        System.out.println("Invalid task id: use --list to see all todos.");
                    }
                }
                if(args[2] != null || args[2].length()>0){
                    filed = args[2];
                    if(args[3] != null || args[3].length()>0){
                        fieldValue = args[3];
                        if(updateTodo(id,filed,fieldValue));
                    }else{
                        System.out.println("Invalid update: format --update [id] [field] [field-value]");
                    }
                }else{
                    System.out.println("Invalid description or empty value.");
                }

            }

            if(input.equalsIgnoreCase("--delete")){
                int arguments = -1;
                if(args[1] != null || args[1].length()>0){
                    try{
                        arguments = Integer.parseInt(args[1].trim());
                    }catch (Exception e){
                        System.out.println("Invalid task id: use --list to see all todos.");
                    }
                }
                if(deleteTodo(arguments)){
                    System.out.println("Deleted successfully.");
                }
            }
        }
    }

    public static boolean updateTodo(int id, String field, String fieldValue){
        boolean flag = false;
        loadPreviousData();
        // check if the task exists with the id
        if(tasksMap.containsKey(id)){
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Define the date and time format
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            // Format the date and time
            String date = currentDateTime.format(dateFormatter);
            String time = currentDateTime.format(timeFormatter);

            // Combine date and time into a single string
            String dateTime = date + " " + time;
            Task t = tasksMap.get(id);
            t.setUpdatedAt(dateTime);
            if(field.equalsIgnoreCase("status")){
                t.setStatus(fieldValue);
            }
            else if(field.equalsIgnoreCase("description")){
                t.setDescription(fieldValue);
            }
            else{
                System.out.println("Invalid field "+field+". Options [status | description]");
            }
            tasksMap.put(id,t);
            if(saveToFile(true)){
                flag = true;
            }

        }

        return flag;
    }

    public static boolean deleteTodo(int id) {
        if (id < 0) {
            System.out.println("Invalid ID.");
            return false;
        }

        loadPreviousData(); // Load previous data into tasksMap
        tasksMap.forEach((k,v) -> System.out.println(k+" : "+v));
        if (tasksMap.containsKey(id)) {
            // Remove the task from tasksMap and taskList
            tasksMap.remove(id); // Remove from taskList
            taskList = tasksMap.values().stream().collect(Collectors.toList());
            // Save remaining tasks to the file
            if (saveToFile(true)) { // Set override to true to save all tasks
                System.out.println("Deleted successfully.");
                return true;
            }
        } else {
            System.out.println("Task with ID " + id + " does not exist.");
        }
        return false;
    }



    public static boolean saveToFile(boolean override) {
        boolean flag = false;

        try {
            String currentPath = "C:\\Tasker\\";
            File directory = new File(currentPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(currentPath + "tasks.json");

            // Prepare the tasks to save
            List<Task> tasksToSave = new ArrayList<>();

            if (override) {
                // If overriding, save all tasks (this works fine)
                tasksToSave.addAll(tasksMap.values()); // Collect all the tasks from tasksMap
                objectMapper.writeValue(file, tasksToSave); // Write the tasks as an array
                flag = true;
                System.out.println("Tasks overwritten in JSON file successfully.");
            } else {
                // Read existing tasks if the file exists
                if (file.exists()) {
                    // Load existing tasks
                    List<Task> existingTasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});

                    // Add existing tasks to the tasksToSave list, ensuring no duplicates
                    tasksToSave.addAll(existingTasks);

                    // Now add only NEW tasks from tasksMap that are not already in the existing list
                    for (Task newTask : tasksMap.values()) {
                        boolean taskExists = false;

                        // Check if this newTask already exists in the existingTasks
                        for (Task existingTask : existingTasks) {
                            if (newTask.getId() == existingTask.getId()) {
                                taskExists = true;
                                break;
                            }
                        }

                        // If the task doesn't exist, add it
                        if (!taskExists) {
                            tasksToSave.add(newTask);
                        }
                    }
                } else {
                    // If the file doesn't exist, just add all tasks from tasksMap
                    tasksToSave.addAll(tasksMap.values());
                }

                // Save the final list (existing + new)
                objectMapper.writeValue(file, tasksToSave); // Write the updated list back to the file as an array
                flag = true;
                System.out.println("Tasks saved to JSON file successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error while saving tasks: " + e.getMessage());
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
        if (tasksMap != null && !tasksMap.isEmpty()) {
            tasksMap.forEach((k, v) -> System.out.println(v)); // Print task object directly
        } else {
            System.out.println("No tasks to show...");
        }
    }

    public static boolean isValidInput(String input) {
        return input != null && input.length() > 0;
    }

    public static void showAllTodos(){
        loadPreviousData();
        if (tasksMap != null && tasksMap.size() > 0) {
            tasksMap.entrySet().stream()
                    .filter(entry -> entry.getValue().getStatus().equalsIgnoreCase("TODO"))
                    .map(entry -> {
                        int id = entry.getValue().getId();
                        String status = entry.getValue().getStatus();
                        String createdAt = entry.getValue().getCreatedAt();
                        String updatedAt = entry.getValue().getUpdatedAt();
                        String description = entry.getValue().getDescription();
                        Task t = new Task();
                        t.setId(id);
                        t.setUpdatedAt(updatedAt);
                        t.setCreatedAt(createdAt);
                        t.setDescription(description);
                        t.setStatus(status);

                        return t;
                    })
                    .collect(Collectors.toList())
                    .forEach(task -> System.out.println(task));
        } else {
            System.out.println("No tasks to show...");
        }
    }

    public static void showAllInProgress(){
        loadPreviousData();
        if (tasksMap != null && tasksMap.size() > 0) {
            tasksMap.entrySet().stream()
                    .filter(entry -> entry.getValue().getStatus().equalsIgnoreCase("IN-PROGRESS"))
                    .map(entry -> {
                        int id = entry.getValue().getId();
                        String status = entry.getValue().getStatus();
                        String createdAt = entry.getValue().getCreatedAt();
                        String updatedAt = entry.getValue().getUpdatedAt();
                        String description = entry.getValue().getDescription();
                        Task t = new Task();
                        t.setId(id);
                        t.setUpdatedAt(updatedAt);
                        t.setCreatedAt(createdAt);
                        t.setDescription(description);
                        t.setStatus(status);

                        return t;
                    })
                    .collect(Collectors.toList())
                    .forEach(task -> System.out.println(task));
        } else {
            System.out.println("No tasks to show...");
        }
    }

    public static void showAllDone(){
        loadPreviousData();
        if (tasksMap != null && tasksMap.size() > 0) {
            tasksMap.entrySet().stream()
                    .filter(entry -> entry.getValue().getStatus().equalsIgnoreCase("DONE"))
                    .map(entry -> {
                        int id = entry.getValue().getId();
                        String status = entry.getValue().getStatus();
                        String createdAt = entry.getValue().getCreatedAt();
                        String updatedAt = entry.getValue().getUpdatedAt();
                        String description = entry.getValue().getDescription();
                        Task t = new Task();
                        t.setId(id);
                        t.setUpdatedAt(updatedAt);
                        t.setCreatedAt(createdAt);
                        t.setDescription(description);
                        t.setStatus(status);

                        return t;
                    })
                    .collect(Collectors.toList())
                    .forEach(task -> System.out.println(task));
        } else {
            System.out.println("No tasks to show...");
        }
    }

    public static void addTask() {
        System.out.print("Enter the description: ");
        String description = scanner.nextLine();
        System.out.println("Enter the status (leave blank for default TODO): ");
        String status = scanner.nextLine();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the date and time format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Format the date and time
        String date = currentDateTime.format(dateFormatter);
        String time = currentDateTime.format(timeFormatter);

        // Combine date and time into a single string
        String dateTime = date + " " + time;

        // Check for empty description
        if (!isValidInput(description)) {
            System.out.println("Invalid input: Description cannot be empty. The default status is TODO: options [TODO, IN-PROGRESS, DONE]");
            return;
        }

        // Check for existing task with the same description
        loadPreviousData(); // Load existing tasks before adding a new one
        for (Task task : taskList) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                System.out.println("Task with this description already exists.");
                return;
            }
        }

        // Create and set up the new task
        Task task = new Task();
        task.setId(getKey());
        task.setDescription(description);

        // Set status to TODO if not provided
        if (status == null || status.isEmpty()) {
            task.setStatus("TODO");
        } else {
            switch (status.toUpperCase().trim()) {
                case "TODO":
                case "IN-PROGRESS":
                case "DONE":
                    task.setStatus(status.toUpperCase().trim());
                    break;
                default:
                    System.out.println("Invalid status option: valid options are [TODO, IN-PROGRESS, DONE]");
                    return;
            }
        }

        task.setCreatedAt(dateTime);
        task.setUpdatedAt(dateTime);

        // Add the new task to the list and map
        taskList.add(task);

        taskList.forEach(t -> System.out.println(t));
        tasksMap.put(task.getId(), task); // Also add to the tasks map for easy access



        // Save the updated task list to the file
        if (saveToFile(false)) {
            System.out.println("Task added successfully.");
        } else {
            System.out.println("Failed to save the task.");
        }
    }



}
