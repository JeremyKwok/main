package tests;

import duke.data.Impression;
import duke.data.Investigation;
import duke.data.Medicine;
import duke.data.Observation;
import duke.data.Patient;
import duke.data.PatientData;
import duke.data.Plan;
import duke.data.Result;
import duke.exception.DukeException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import templates.CommandTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * JUnit class testing the class GsonStorage.
 */

/* @@author JacobToresson */
public class GsonStorageTest extends CommandTest {

    /**
     * A dummy patient used for testing. In this context a dummy patient refers to a patient object that only has
     * a name, a bed number and one allergy.
     */
    private Patient dummy1 = new Patient("dummy1", "A100", "nuts", 0, 0, 0,
            0, "", "");

    /**
     * A dummy patient used for testing.
     */
    private Patient dummy2 = new Patient("dummy2", "A200", "", 0, 0, 0,
            0, "", "");

    /**
     * A dummy patient used for testing.
     */
    private Patient dummy3 = new Patient("dummy3", "A300", "cats", 0, 0, 0,
            0, "", "");

    /**
     * The expected Json representation of dummy1, dummy2, dummy3 and the complex patient that is created with
     * the createComplexPatient method.
     */
    private static String expected;

    /**
     * Loads the expected Json representation of the test patients from the data/expected.json file.
     */
    @BeforeAll
    public static void loadExpected() {
        try {
            expected = new Scanner(new File("data" + File.separator + "expected.json"))
                    .useDelimiter("\\Z").next();
        } catch (IOException excp) {
            fail("Could not load expected values for test!");
        }
    }

    /**
     * Creates a patient object and assign values to all of its attributes - used to test if the nesting works.
     *
     * @return the created patient object.
     */
    private Patient createComplexPatient() throws DukeException {
        Patient complexPatient = new Patient("testCPatient", "C1", "test allergies", 123,
                456, 100, 6582447, "test address", "test history");
        Impression impression1 = new Impression("test imp 1", "test description 1", complexPatient);
        Impression impression2 = new Impression("test imp 2", "test description 2", complexPatient);
        Observation observation1 = new Observation("test obs 1", impression1, 0,
                "test summary 1", false);
        Observation observation2 = new Observation("test obs 2", impression2, 1,
                "test summary 2", true);
        Result result1 = new Result("test result 1", impression2, 2,
                "test summary 1");
        Result result2 = new Result("test result 2", impression2, 3,
                "test summary 2");
        impression1.addNewEvidence(observation1);
        impression1.addNewEvidence(observation2);
        impression1.addNewEvidence(result1);
        impression1.addNewEvidence(result2);
        complexPatient.addNewImpression(impression1);
        complexPatient.addNewImpression(impression2);
        Plan plan1 = new Plan("test plan 1", impression1, 0, "1",
                "test summary 2");
        Plan plan2 = new Plan("test plan 2", impression2, 1, "2",
                "test summary 2");
        impression1.addNewTreatment(plan1);
        impression2.addNewTreatment(plan2);
        Investigation investigation1 = new Investigation("test inv 1", impression1,
                0, "1", "test summary 1");
        Investigation investigation2 = new Investigation("test inv 2", impression2,
                2, "0", "test summary 2");
        impression1.addNewTreatment(investigation1);
        impression2.addNewTreatment(investigation2);
        Medicine medicine1 = new Medicine("test medicine 2", impression1, 0, "1",
                "test dose 1", "test start date", "test duration");
        Medicine medicine2 = new Medicine("test medicine 1", impression2, 2, "2",
                "test dose 2", "test start date", "test duration");
        impression2.addNewTreatment(medicine1);
        impression2.addNewTreatment(medicine2);
        complexPatient.setPrimaryDiagnosis(impression1.getName());
        return complexPatient;
    }

    /**
     * Compares all the attributes of two patients and returns true if they all are the same, otherwise it returns
     * false.
     *
     * @return A boolean stating if the storage function is working properly or not.
     */
    private boolean identical(Patient patient1, Patient patient2) {
        if (!(patient1.getBedNo().equals(patient2.getBedNo()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getAllergies(), patient2.getAllergies()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getHeight(), patient2.getHeight()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getWeight(), patient2.getWeight()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getAge(), patient2.getAge()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getNumber(), patient2.getNumber()))) {
            return false;
        } else if (!(java.util.Objects.equals(patient1.getAddress(), patient2.getAddress()))) {
            return false;
        } else {
            return java.util.Objects.equals(patient1.getHistory(), patient2.getHistory());
        }
    }

    /**
     * Tests if patients are transformed from the json file to the hash map properly.
     */
    @Test
    public void loadPatientHashMapTest() throws DukeException, IOException {
        core.patientData = core.storage.resetAllData();
        FileWriter fileWriter = new FileWriter(testFilePath);
        fileWriter.write(expected);
        fileWriter.close();
        core.patientData = new PatientData(core.storage);
        assertTrue(identical(core.patientData.getPatientByBed("A100"), dummy1));
        assertTrue(identical(core.patientData.getPatientByBed("A200"), dummy2));
        assertTrue(identical(core.patientData.getPatientByBed("A300"), dummy3));
        assertTrue(identical(core.patientData.getPatientByBed("C1"), createComplexPatient()));
    }

    /**
     * Tests if patients are transformed from the hash map to the json file properly.
     */
    @Test
    public void writeJsonFileTest() throws IOException, DukeException {
        core.patientData = core.storage.resetAllData();
        core.patientData = core.storage.resetAllData();
        core.patientData.addPatient(dummy1);
        core.patientData.addPatient(dummy2);
        core.patientData.addPatient(dummy3);
        core.patientData.addPatient(createComplexPatient());
        core.storage.writeJsonFile(core.patientData.getPatientList());
        String json = Files.readString(Paths.get(testFilePath), StandardCharsets.US_ASCII);
        printDifferences(expected, json);
        assertEquals(expected, json);
    }

    /**
     * Creates the Json representation of a dummy patient. Then recreates the patient objects based on what
     * is in the json file. When the recreation is done it checks if the first patients are identical to the new one.
     */
    @Test
    public void identicalDummyPatient() throws IOException, DukeException {
        core.patientData = core.storage.resetAllData();
        core.patientData.addPatient(dummy1);
        core.storage.writeJsonFile(core.patientData.getPatientList());
        core.patientData = new PatientData(core.storage);
        Patient dummyPatientRecreated = core.patientData.getPatientByBed(dummy1.getBedNo());
        boolean equals = identical(dummy1, dummyPatientRecreated);
        assertTrue(equals);
        core.patientData = core.storage.resetAllData();
    }

    /**
     * Creates the Json representation of a complex patient and then recreates the patient objects based on what
     * is in the json file. When the recreation is done it checks if the first patients are identical to the new one.
     */
    @Test
    public void identicalComplexPatient() throws IOException, DukeException {
        core.patientData = core.storage.resetAllData();
        Patient complexPatient = createComplexPatient();
        core.patientData.addPatient(complexPatient);
        core.storage.writeJsonFile(core.patientData.getPatientList());
        core.storage.loadPatients();
        Patient complexPatientRecreated = core.patientData.getPatientByBed(complexPatient.getBedNo());
        boolean equals = identical(complexPatient, complexPatientRecreated);
        assertTrue(equals);
    }

    /**
     * Function to print out the characters that differ between two strings that should be identical.
     */
    private void printDifferences(String str1, String str2) {
        for (int i = 0; i < str1.length() && i < str2.length(); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                System.out.println("index " + i);
                System.out.println(str1.charAt(i) + "  " + str2.charAt(i));
            }
        }
        if (str1.length() != str2.length()) {
            System.out.println("str1 " + str1.length() + " str2 " + str2.length());
            System.out.println(str1);
        }
    }
}
