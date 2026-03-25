import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class taskManager {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final jsonStore store;

    public taskManager() {
        //this.store = new jsonStore("/home/koktail/Documents/GitHub/JavaLearning/TaskTrackerCLI/tasks.json");
        this.store = new jsonStore("$HOME/Documents/tasks.json");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  ADD
    // ─────────────────────────────────────────────────────────────────────

    public void addTask(String description) {
        if (description.isBlank()) {
            System.out.println("Error: description cannot be empty.");
            return;
        }

        List<task> tasks = store.load();
        int newId = tasks.stream().mapToInt(task::getId).max().orElse(0) + 1;

        String now = now();
        task newTask = new task(newId, description, "todo", now, now);

        tasks.add(newTask);
        store.save(tasks);

        System.out.println("Task added successfully (ID: " + newId + ")");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  UPDATE
    // ─────────────────────────────────────────────────────────────────────

    public void updateTask(int id, String newDescription) {
        if(newDescription.isBlank()) {
            System.out.println("Error: new description cannot be empty.");
            return;
        }

        List<task> tasks = store.load();
        task newTask = findById(tasks, id);

        if (newTask == null) {
            System.out.println("Error: No task found with ID " + id);
            return;
        }

        newTask.setDescription(newDescription);
        newTask.setUpdatedAt(now());
        store.save(tasks);

        System.out.println("Task "+ id + " updated successfully.");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  DELETE
    // ─────────────────────────────────────────────────────────────────────

    public void deleteTask(int id) {
        List<task> tasks = store.load();

        if(findById(tasks, id) == null) {
            System.out.println("Error: No task found with ID:"+ id);
            return;
        }

        tasks.removeIf(t -> t.getId() == id);
        store.save(tasks);

        System.out.println("Task " + id + " deleted successfully.");
    }
    // ─────────────────────────────────────────────────────────────────────
    //  MARK STATUS
    // ─────────────────────────────────────────────────────────────────────

    public void markTask(int id, String status) {
        List<task> tasks = store.load();
        task newTask = findById(tasks, id);

        if (newTask == null) {
            System.out.println("Error: No task found with ID: "+id);
            return;
        }

        newTask.setStatus(status);
        newTask.setUpdatedAt(now());
        store.save(tasks);

        System.out.println("Task " + id + " marked as " + status + ".");
    }

    // ─────────────────────────────────────────────────────────────────────
    //  LIST
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Lists tasks, optionally filtered by status.
     *
     * filter values:
     *   "all"         → show every task
     *   "todo"        → only todo tasks
     *   "in-progress" → only in-progress tasks
     *   "done"        → only done tasks
     *
     * WHAT IS a stream + filter + forEach?
     * Java Streams let you process collections in a pipeline:
     *   list.stream()           → start a stream
     *       .filter(condition)  → keep only matching items
     *       .forEach(action)    → do something with each remaining item
     * It's like a conveyor belt with filters.
     */

    public void listTasks(String filter) {
        List<String> validFilters = Arrays.asList("all", "todo", "in-progress", "done");
        if (!validFilters.contains(filter)) {
            System.out.println("Error: Unknown filter \"" + filter + "\". " +
                    "Use: all, todo, in-progress, done");
            return;
        }

        List<task> tasks = store.load();

        if(tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        //header
        String header = filter.equals("all") ? "All tasks" : "Tasks with status: " + filter;
        System.out.println("\n── " + header + " ──");

        long count = tasks.stream()
                .filter(t -> filter.equals("all") || t.getStatus().equals(filter))
                .peek(System.out::println)
                .count();

        if(count == 0) {
            System.out.println("  (none)");
        }else {
            System.out.println("Total: "+ count);
        }
        System.out.println();
    }



    // ─────────────────────────────────────────────────────────────────────
    //  HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Searches the task list for a task with the given ID.
     * Returns null if not found.
     *
     * WHAT IS Optional?
     * Stream.filter().findFirst() returns an Optional<Task>.
     * An Optional is a wrapper that either contains a value or is empty —
     * it's Java's safer alternative to returning null.
     * .orElse(null) unwraps it, giving null if nothing was found.
     */
    private task findById(List<task> tasks, int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
    /** Returns the current date-time as a formatted string. */
    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
