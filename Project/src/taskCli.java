public class taskCli {
    public static void main(String[] args) {


        if(args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();
        taskManager mg = new taskManager();


        switch (command) {

            case "add":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli add \"<description>\"");
                    break;
                }
                mg.addTask(args[1]);
                break;

            case "update":
                if(args.length < 3) {
                    System.out.println("Usage: task-cli update <id> \"<new description>\"");
                    break;
                }
                mg.updateTask(parseId(args[1]), args[2]);
                break;

            case "delete":
                if(args.length < 2) {
                    System.out.println("Usage: task-cli delete <id>");
                    break;
                }
                mg.deleteTask(parseId(args[1]));
                break;

            case "mark-in-progress":
                if(args.length < 2) {
                    System.out.println("Usage: task-cli mark-in-progress <id>");
                    break;
                }
                mg.markTask(parseId(args[1]), "in-progress");
                break;

            case "done":
                if (args.length < 2) {
                    System.out.println("Usage: task-cli mark-done <id>");
                    break;
                }
                mg.markTask(parseId(args[1]), "done");
                break;

            case "ls":
                String filter = (args.length >= 2) ? args[1].toLowerCase() : "all";
                mg.listTasks(filter);
                break;

            default:
                System.out.println("Unknown command: " + command);
                printUsage();
        }


    }



    private static int parseId(String value) {
        try {
            return Integer.parseInt(value);
        }catch (NumberFormatException e) {
            System.out.println("Error: ID must be a number. You provided: \"" + value + "\"");
            System.exit(1);
            return -1;
        }
    }



    private static void printUsage() {
        System.out.println("=== Task Tracker CLI ===");
        System.out.println("Commands:");
        System.out.println("  add \"<desc>\"               Add a new task");
        System.out.println("  update <id> \"<desc>\"       Update a task's description");
        System.out.println("  delete <id>                Delete a task");
        System.out.println("  mark-in-progress <id>      Mark a task as in-progress");
        System.out.println("  mark-done <id>             Mark a task as done");
        System.out.println("  list                       List all tasks");
        System.out.println("  list todo                  List only todo tasks");
        System.out.println("  list in-progress           List only in-progress tasks");
        System.out.println("  list done                  List only done tasks");
    }
}
