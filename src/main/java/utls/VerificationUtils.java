package utls;

import models.training_models.TrainingProcess;

public class VerificationUtils {

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
}
