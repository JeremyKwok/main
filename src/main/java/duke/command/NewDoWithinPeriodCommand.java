package duke.command;

import duke.DukeCore;
import duke.exception.DukeException;
import duke.task.DoWithinPeriodTask;
import duke.task.TimedTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Class responsible for executing Command to create a new DoWithinPeriod task.
 */
public class NewDoWithinPeriodCommand extends NewTimedTaskCommand {

    LocalDateTime endDatetime;
    /**
     * Creates a new Command object that can be executed to create a new DoWithinPeriod task.
     * argc will throw an error if the number of arguments is less than 3.
     */

    public NewDoWithinPeriodCommand() {
        argc = 3;
        delim = "(/between|/and)";
        invalidArgMsg = "Invalid period! I need to know the date and time that it is /between.";
        emptyArgMsg = "You didn't tell me anything about the task!";
    }

    /**
     * Split the input string into the elements of the argv array using MultiArgCommand's parse, then load the task
     * with argv[0] as the description and argv[1], argv[2] as the date and time in the TimedTask data format.
     *
     * @throws DukeException If task description is empty, or if date and time are invalid.
     * @see ArgCommand
     */
    @Override
    public void parse(String inputStr) throws DukeException {
        super.parse(inputStr);
        if (argv[0].length() == 0) {
            argv = null;
            throw new DukeException("The task description cannot be empty!");
        }

        try {
            taskDateTime = LocalDateTime.parse(argv[1], TimedTask.getPatDatetime());
            endDatetime = LocalDateTime.parse(argv[2], TimedTask.getPatDatetime());
        } catch (DateTimeParseException excp) {
            throw new DukeException("Date and time must be given as e.g. "
                    + LocalDateTime.now().format(TimedTask.getPatDatetime()) + ".");
        }
    }

    @Override
    public void execute(DukeCore core) throws DukeException {
        super.execute(core);
        String addStr = core.taskList.addTask(new DoWithinPeriodTask(argv[0], taskDateTime, endDatetime));
        core.storage.writeTaskFile(core.taskList.getFileStr());
        core.ui.print(core.taskList.getAddReport(System.lineSeparator() + "  " + addStr, 1));
    }
}
