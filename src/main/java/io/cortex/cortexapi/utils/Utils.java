package io.cortex.cortexapi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cortex.cortexapi.models.training_models.ReturnableTrainingProcess;
import io.cortex.cortexapi.models.training_models.TrainingProcess;
import io.cortex.cortexapi.models.user.CortexClassifier;

import java.io.*;
import java.util.Scanner;

public class Utils {

    //TODO verification of api_key
    public static boolean userExists(String api_key) {

        return true;
    }

    public static boolean userIsTraining(TrainingProcess trainingProcess) {
        if (trainingProcess == null ||
                trainingProcess.getStatus() == TrainingProcess.TrainingStatus.TRAINING_COMPLETE)
            return false;
        return true;
    }

    public static String getModelPath(String model_key) {
        String model_path = null;

        return model_path;
    }

    public static ReturnableTrainingProcess mapTrainingProcess(TrainingProcess trainingProcess) {
        ReturnableTrainingProcess returnableTrainingProcess = new ReturnableTrainingProcess();

        returnableTrainingProcess.setLabels(trainingProcess.getLabels());
        returnableTrainingProcess.setModel_name(trainingProcess.getModel_name());
        returnableTrainingProcess.setRuntime(trainingProcess.getProcess().info().totalCpuDuration().get().toSeconds() + "");
        returnableTrainingProcess.setStatus(trainingProcess.getStatus());
        returnableTrainingProcess.setFile_count(trainingProcess.getFile_count());
        returnableTrainingProcess.setSteps(trainingProcess.getSteps());

        return returnableTrainingProcess;
    }

    public static void writeMetaData(TrainingProcess trainingProcess, String path) throws IOException {
        CortexClassifier classifier = new CortexClassifier();
        classifier.setTitle(trainingProcess.getModel_name());
        classifier.setAccuracy(readValidationAccuracy(trainingProcess.getUser(),
                trainingProcess.getModel_name()));
        classifier.setTraining_steps(trainingProcess.getSteps());
        classifier.setFile_count(trainingProcess.getFile_count());
        classifier.setLabels(trainingProcess.getLabels());

        ObjectMapper mapper = new ObjectMapper();

        writeFile(mapper.writeValueAsString(classifier), path);
    }

    public static String readValidationAccuracy(String user, String classifier) throws FileNotFoundException {
        //read the validation accuracy from single log file
        String path = String.format(SystemPaths.CORTEX_USER_MODELS_PATH, user, classifier) + "/single_log";

        Scanner scanner = new Scanner(new File(path));
        while (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return null;
    }

    public static void writeFile(String txt, String path) throws IOException {
        FileWriter writer = new FileWriter(new File(path));
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write(txt);
        bufferedWriter.close();
    }
}
