package utils;

/**
 * Created by John on 7/15/2017.
 */
public class SystemPaths {
    public static final String CORTEX_TF_FILES_PATH = "Z:/CORTEX_DATA/training/public/%s/tf_files";

    public static final String CORTEX_TRAINING_TEMP =
            CORTEX_TF_FILES_PATH + "/temp";

    public static final String INCEPTION_PATH =
            "E:/Workspaces/CortexAPI/inception";

    public static final String CORTEX_USER_MODELS_PATH =
            CORTEX_TF_FILES_PATH + "/models/%s";


    public static final String TRAINING_STEPS_LOG =
            CORTEX_USER_MODELS_PATH + "/training_steps_log.txt";
}
