package duke.command;

import duke.Storage;
import duke.TaskList;

/**
 * A class that represents a Duke command.
 */
abstract public class Command {
    /**
     * The main body of the command.
     */
    protected final String commandBody;

    /**
     * Constructs a {@code Command} object.
     *
     * @param commandBody The command body.
     */
    public Command(String commandBody) {
        this.commandBody = commandBody;
    }

    /**
     * Executes the command.
     *
     * @param taskList The task list that may be modified or referenced by the command.
     * @param storage  The storage that may be modified of referenced by the command.
     */
    abstract public void execute(TaskList taskList, Storage storage);

    /**
     * Indicates if the command is an exit command.
     *
     * @return false
     */
    public boolean isExit() {
        return false;
    }
}
