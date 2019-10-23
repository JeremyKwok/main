package duke.command;

import duke.DukeCore;
import duke.exception.DukeException;
import duke.exception.DukeFatalException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportCommand extends ArgCommand {
    // TODO: save the reports as pdf's instead
    // TODO: crete better looking and more detailed reports

    @Override
    protected ArgSpec getSpec() {
        return ReportSpec.getSpec();
    }

    @Override
    public void execute(DukeCore core) throws DukeException {
        if(core.patientMap.patientExist(getArg())){
            String patientsName = core.patientMap.getPatient(getArg()).getName();
            String patientsBenNo = core.patientMap.getPatient(getArg()).getBedNo();
            File reportFile = new File("reports" + File.separator + patientsName + "-" + patientsBenNo + ".txt");
            try {
                FileWriter fileWriter = new FileWriter("reports" + File.separator + patientsName + "-" + patientsBenNo + ".txt");
                System.out.println("NU SKA EN RAPPORT SKAPAS");
                fileWriter.write("hej");
                fileWriter.close();
            }
            catch(IOException e){
                throw new DukeFatalException("Unable to create rapport! Some data may have been lost,");
            }
        }
        core.patientMap.deletePatient(getArg());
    }
}
