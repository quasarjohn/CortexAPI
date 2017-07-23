package utils;

import models.training_models.ReturnableTrainingProcess;
import models.training_models.TrainingProcess;

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

        return returnableTrainingProcess;
    }
}
