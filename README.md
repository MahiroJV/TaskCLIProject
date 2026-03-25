# Task Tracker CLI — Java Edition

A command-line task manager built in **pure Java** (no external libraries).
This project is designed to be read and understood, not just copied.

---

## Project Structure

```
task-tracker/
├── src/
│   ├── TaskCli.java      ← Entry point. Reads your command and routes it.
│   ├── Task.java         ← Model class. Represents one task (id, description, status…)
│   ├── TaskManager.java  ← Business logic. Add, update, delete, list tasks.
│   └── JsonStore.java    ← File I/O. Reads/writes tasks.json manually.
├── out/                  ← Compiled .class files go here (created by javac)
├── compile.sh            ← Convenience script to compile everything
└── tasks.json            ← Created automatically when you add your first task
```

### Why 4 files?
Each file has **one responsibility** — this is called *Separation of Concerns*:
| File | Job |
|------|-----|
| `TaskCli.java` | Parse command-line arguments |
| `Task.java` | Define what a task looks like |
| `TaskManager.java` | Enforce business rules |
| `JsonStore.java` | Talk to the filesystem |

---

## Setup & Compile

You need **Java 11+** installed. Check with:
```bash
java -version
javac -version
```

Compile all source files:
```bash
# Option 1 — use the script
chmod +x compile.sh
./compile.sh

# Option 2 — do it manually (same thing, more visible)
mkdir -p out
javac -d out src/*.java
```

This creates `.class` files in `out/`. The `compile.sh` also generates a `./task-cli` wrapper so you don't have to type `java -cp out TaskCli` every time.

---

## Usage

```bash
# Add a task
./task-cli add "Buy groceries"
# → Task added successfully (ID: 1)

./task-cli add "Read Java book"
# → Task added successfully (ID: 2)

# List all tasks
./task-cli list

# Mark as in-progress
./task-cli mark-in-progress 1

# Mark as done
./task-cli mark-done 1

# Update description
./task-cli update 2 "Read Effective Java book"

# Delete a task
./task-cli delete 1

# List by status
./task-cli list todo
./task-cli list in-progress
./task-cli list done
```

---

## What the JSON file looks like

After adding a task, `tasks.json` is created in the current directory:

```json
[
  {
    "id": 1,
    "description": "Buy groceries",
    "status": "todo",
    "createdAt": "2024-03-17T10:00:00",
    "updatedAt": "2024-03-17T10:00:00"
  }
]
```

---

## Key Java Concepts Used — Learning Guide

### 1. `main(String[] args)` — Entry point
Every Java program starts here. `args` holds what the user typed:
```
java TaskCli add "Buy milk"
args[0] = "add"
args[1] = "Buy milk"
```

### 2. `switch` statement — Command routing
```java
switch (command) {
    case "add": manager.addTask(...); break;
    case "delete": manager.deleteTask(...); break;
    // ...
}
```

### 3. Classes and objects — `Task.java`
```java
Task t = new Task(1, "Buy milk", "todo", "2024-03-17T10:00:00", "...");
t.getDescription(); // "Buy milk"
t.setStatus("done");
```

### 4. `List<Task>` — Storing multiple tasks
`ArrayList` is a resizable array. You can add, remove, and iterate over it.
```java
List<Task> tasks = new ArrayList<>();
tasks.add(new Task(...));
tasks.removeIf(t -> t.getId() == id);
```

### 5. Streams — Filtering and processing lists
```java
tasks.stream()
     .filter(t -> t.getStatus().equals("done"))
     .forEach(System.out::println);
```
This is like: "give me only the tasks that are done, then print each one."

### 6. `Files.readAllBytes` / `Files.write` — Reading and writing files
```java
String content = new String(Files.readAllBytes(Paths.get("tasks.json")));
Files.write(Paths.get("tasks.json"), content.getBytes());
```

### 7. `StringBuilder` — Building strings efficiently
Instead of `"a" + "b" + "c"` (slow), use:
```java
StringBuilder sb = new StringBuilder();
sb.append("a").append("b").append("c");
String result = sb.toString(); // "abc"
```

### 8. `LocalDateTime` — Working with dates
```java
String now = LocalDateTime.now()
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
```

---

## Things to Try Next (extend the project yourself!)

- Add a `mark-todo` command to reset a task back to todo
- Add task **priorities** (low / medium / high) as a new field
- Add `search <keyword>` to find tasks by description
- Color-code output (green = done, yellow = in-progress, white = todo) using ANSI codes
- Add a `--help` flag on every command

---

## Running Without the Script

If you don't want to use `compile.sh`:
```bash
# Compile
mkdir -p out
javac -d out src/*.java

# Run (always from the task-tracker/ folder so tasks.json ends up here)
java -cp out TaskCli add "My first task"
java -cp out TaskCli list
```
