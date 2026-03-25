import java.io.*;
import java.nio.file.*;
import java.util.*;

public class jsonStore {
    private final String filePath;

    public jsonStore(String filePath) {
        this.filePath = filePath;
    }


    // ─────────────────────────────────────────────────────────────────────
    //  LOADING  (file → List<Task>)
    // ─────────────────────────────────────────────────────────────────────

    public List<task> load() {
        List<task> tasks = new ArrayList<>();

        File file = new File(filePath);

        if(!file.exists()) {
            try {
                file.createNewFile();
                Files.write(Paths.get(filePath), "[]".getBytes());
            } catch (IOException e) {
                System.out.println("Error creating tasks.json: " + e.getMessage());
                System.exit(1);
            }
            return tasks;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            content = content.trim();

            if(!content.startsWith("[") || !content.endsWith("]")) {
                System.out.println("Warning: tasks.json is malformed. Starting fresh.");
                return tasks;
            }

            String inner = content.substring(1, content.length() - 1).trim();
            if (inner.isEmpty()) return tasks;

            List<String> objects = splitJsonObjects(inner);

            for(String obj : objects) {
                task t = parseTask(obj.trim());
                if (t != null) tasks.add(t);
            }

        }catch(IOException e) {
            System.out.println("Error reading " + filePath + ": " +e.getMessage());
        }

        return tasks;
    }

    /**
     * Splits a string like:
     *   { "id": 1, ... },
     *   { "id": 2, ... }
     * into a list of individual object strings.
     *
     * HOW IT WORKS:
     * We count opening { and closing } braces. When the count hits 0,
     * we've found the end of one object and can slice it out.
     */

    private List<String> splitJsonObjects(String text){
        List<String> result = new ArrayList<>();
        int depth = 0;
        int start = -1;

        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if(c == '{') {
                if(depth == 0) start = i; // mark start of a new object
                depth++;
            }else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    result.add(text.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return result;
    }

    /**
     * Parses a single JSON object string like:
     *   { "id": 1, "description": "Buy milk", "status": "todo", ... }
     * and returns a Task object.
     *
     * HOW IT WORKS:
     * We use a helper to extract each field by its key name.
     */

    private task parseTask(String obj) {
        try {
            int id = Integer.parseInt(extractValue(obj, "id"));
            String description = extractValue(obj, "description");
            String status = extractValue(obj, "status");
            String createdAt = extractValue(obj, "createdAt");
            String updatedAt = extractValue(obj, "updatedAt");
            return new task(id, description, status, createdAt, updatedAt); // ← add this!
        } catch(Exception e) {
            System.out.println("Warning: skipping malformed task entry: " + obj);
            return null;
        }
    }

    /**
     * Finds the value for a given key inside a JSON object string.
     *
     * Example:  extractValue("{ \"id\": 42, \"status\": \"todo\" }", "id")
     *           → "42"
     *
     * WHAT IS indexOf?
     * String.indexOf(s) returns the position of s inside the string, or -1 if not found.
     * We use it to locate the key, then grab everything between the following quotes.
     */

    private String extractValue(String json, String key) {

        //Look for key
        String searchKey = "\"" + key +"\"";
        int keyIndex = json.indexOf(searchKey);
        if(keyIndex == -1) throw new RuntimeException("Key not found: " + key);

        // Move past the key and the colon ":"
        int colonIndex = json.indexOf(":",keyIndex);
        if (colonIndex == -1) throw new RuntimeException("No colon after key: " + key);

        //Skip whitespace after the colon
        int valueStart = colonIndex + 1;
        while(valueStart < json.length() && json.charAt(valueStart) == ' ') {
            valueStart++;
        }

        char firstChar = json.charAt(valueStart);

        if(firstChar == '"') {
            int end = valueStart + 1;
            while(end < json.length()) {
                if (json.charAt(end) == '"' && json.charAt(end - 1) != '\\') break;
                end++;
            }
            return json.substring(valueStart + 1, end).replace("\\\"", "\"").replace("\\n", "\n");
        }else {

            int end = valueStart;
            while (end < json.length() && ",}".indexOf(json.charAt(end)) == -1) {
                end++;
            }
            return json.substring(valueStart, end).trim();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  SAVING  (List<Task> → file)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Writes the entire task list back to the JSON file.
     *
     * We build the JSON string manually using a StringBuilder —
     * much like how you'd write the file by hand.
     *
     * WHAT IS StringBuilder?
     * Concatenating strings with + in a loop is slow in Java because each +
     * creates a new String object. StringBuilder is a mutable buffer that
     * lets you append efficiently.
     */

    public void save(List<task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < tasks.size(); i++) {
            task t =tasks.get(i);
            sb.append("  {\n");
            sb.append("    \"id\": ").append(t.getId()).append(",\n");
            sb.append("     \"description\": \"").append(escape(t.getDescription())).append("\",\n");
            sb.append("    \"status\": \"").append(t.getStatus()).append("\",\n");
            sb.append("     \"createdAt\": \"").append(t.getCreatedAt()).append("\",\n");
            sb.append("     \"updatedAt\": \"").append(t.getUpdatedAt()).append("\"\n");
            sb.append("   }");

            if(i < tasks.size() - 1) sb.append(",");
            sb.append("\n");

        }
        sb.append("]");

        try {
            Files.write(Paths.get(filePath), sb.toString().getBytes());
        }catch (IOException e) {
            System.out.println("Error writing "+filePath+": "+ e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Escapes special characters in a string so it's safe inside JSON quotes.
     * e.g.  He said "hello"  →  He said \"hello\"
     */
    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
