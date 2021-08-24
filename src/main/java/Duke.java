import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    enum TaskType {
        TODO, DEADLINE, EVENT
    }

    private static final ArrayList<Task> tasks = new ArrayList<>();

    public static void addTask(Task task) {
        tasks.add(task);
    }

    private static void printAddedTask(Task task) {
        Message addMessage = new Message("\tGot it. I've added this task:\n\t\t"
                + task
                + "\n\tTask(s) remaining in the list: "
                + tasks.size()
        );
        addMessage.printMessage();
    }

    public static void addTaskByDescription(TaskType type, String description) throws DukeException {
        switch (type) {
            case TODO: {
                if (description.trim().isEmpty()) {
                    throw new InvalidCommandException();
                } else {
                    Task task = new ToDo(description);
                    Duke.addTask(task);
                    printAddedTask(task);
                }
                break;
            }
            case DEADLINE: {
                if (description.contains("/by")) {
                    String[] information = description.split("/by ");
                    try {
                        Task task = new Deadline(information[0], LocalDate.parse(information[1]));
                        Duke.addTask(task);
                        printAddedTask(task);
                    } catch (DateTimeParseException e) {
                        Message errorMessage = new Message("\tPlease use the YYYY-MM-DD format for the time!");
                        errorMessage.printMessage();
                    }
                } else {
                    throw new CommandNotUsedException("/by");
                }
                break;
            }
            case EVENT: {
                if (description.contains("/at")) {
                    String[] information = description.split("/at ");
                    try {
                        Task task = new Event(information[0], LocalDate.parse(information[1]));
                        Duke.addTask(task);
                        printAddedTask(task);
                    } catch (DateTimeParseException e) {
                        Message errorMessage = new Message("\tPlease use the YYYY-MM-DD format for the time!");
                        errorMessage.printMessage();
                    }
                } else {
                    throw new CommandNotUsedException("/at");
                }
                break;
            }
        }
    }

    public static void removeTask(int index) {
        tasks.remove(index);
    }

    public static Message printList() {
        StringBuilder itemList = new StringBuilder("\tHere are the tasks in your list:\n");

        if (tasks.size() == 0) {
            itemList.append("\tNothing here yet...");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                itemList.append("\t\t").append(i + 1).append(". ").append(tasks.get(i));
                if (i < tasks.size() - 1) {
                    itemList.append("\n");
                }
            }
        }

        return new Message(itemList.toString());
    }

    public static Message printList(LocalDate date) {
        ArrayList<Task> targetTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task instanceof Deadline) {
                Deadline deadline = (Deadline) task;
                if (deadline.timeDue.equals(date)) {
                    targetTasks.add(task);
                }
            }
            if (task instanceof Event) {
                Event deadline = (Event) task;
                if (deadline.timeStart.equals(date)) {
                    targetTasks.add(task);
                }
            }
        }

        StringBuilder itemList = new StringBuilder("\tHere is the result:\n");

        if (targetTasks.size() == 0) {
            itemList.append("\tNothing special will happen on this day");
        } else {
            for (int i = 0; i < targetTasks.size(); i++) {
                itemList.append("\t\t").append(i + 1).append(". ").append(targetTasks.get(i));
                if (i < targetTasks.size() - 1) {
                    itemList.append("\n");
                }
            }
        }

        return new Message(itemList.toString());
    }

    public static void applyCommand(String command) throws DukeException {
        if (command.contains(" ")) {
            String commandType = command.split(" ", 2)[0];
            String description = command.split(" ", 2)[1].trim();

            switch (commandType) {
                case "todo": {
                    Duke.addTaskByDescription(TaskType.TODO, description);
                    return;
                }
                case "deadline": {
                    Duke.addTaskByDescription(TaskType.DEADLINE, description);
                    return;
                }
                case "event": {
                    Duke.addTaskByDescription(TaskType.EVENT, description);
                    return;
                }
                case "done": {
                    if (description.matches("\\d+")) {
                        int item = Integer.parseInt(description);
                        if (item == 0) {
                            throw new IndexMismatchException();
                        }
                        if (item > tasks.size()) {
                            throw new IndexOutOfBoundException();
                        }
                        if (Duke.tasks.get(item - 1).isDone) {
                            throw new DukeException("☹ OOPS!!! The task is already done!");
                        }
                        Duke.tasks.get(item - 1).setDone(true);
                        Message doneMessage = new Message("\tNice! I've marked this task as done:\n\t\t"
                                + tasks.get(item - 1)
                        );
                        doneMessage.printMessage();
                    } else {
                        throw new IndexMismatchException();
                    }
                    return;
                }
                case "delete": {
                    if (description.matches("\\d+")) {
                        int item = Integer.parseInt(description);
                        if (item == 0) {
                            throw new IndexMismatchException();
                        }
                        if (item > tasks.size()) {
                            throw new IndexOutOfBoundException();
                        }
                        int numOfTasks = tasks.size() - 1;
                        Message doneMessage = new Message("\tNoted. I've removed this task:\n\t\t"
                                + tasks.get(item - 1)
                                + "\n\tTask(s) remaining in the list: "
                                + numOfTasks
                        );
                        Duke.removeTask(item - 1);
                        doneMessage.printMessage();
                    } else {
                        throw new IndexMismatchException();
                    }
                    return;
                }
                case "query": {
                    try {
                        Duke.printList(LocalDate.parse(description)).printMessage();
                    } catch (DateTimeParseException e) {
                        throw new InvalidCommandException();
                    }
                    return;
                }
                default: {
                    throw new InvalidCommandException();
                }
            }
        }

        switch (command) {
            case "list":
                Duke.printList().printMessage();
                break;
            case "bye":
                break;
            default:
                throw new InvalidCommandException();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in).useDelimiter("\n");

        System.out.println(Message.greet());

        String input = scanner.next();

        while (!input.equals("bye")) {
            try {
                Duke.applyCommand(input);
            } catch (DukeException e) {
                Message errorMessage = new Message("\t" + e.getMessage());
                errorMessage.printMessage();
            } finally {
                input = scanner.next();
            }
        }

        System.out.println(Message.farewell());
    }
}
