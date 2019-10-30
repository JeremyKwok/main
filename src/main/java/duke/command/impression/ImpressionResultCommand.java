package duke.command.impression;

import duke.DukeCore;
import duke.command.ArgCommand;
import duke.command.ArgSpec;
import duke.exception.DukeException;

public class ImpressionResultCommand extends ArgCommand {

    @Override
    protected ArgSpec getSpec() {
        return ImpressionResultSpec.getSpec();
    }

    @Override
    public void execute(DukeCore core) throws DukeException {
        //Impression impression = ImpressionUtils.getImpression(core);
        // TODO: find by name or index
    }

}
