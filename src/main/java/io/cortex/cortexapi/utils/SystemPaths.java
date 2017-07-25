package io.cortex.cortexapi.utils;

/**
 * Created by John on 7/15/2017.
 */
public class SystemPaths {
    public static final String CORTEX_TF_FILES_PATH = "E:/CORTEX_DATA/training/public/%s/tf_files";
    public static final String CORTEX_CLASSIFICATION_TEMP_PATH = "E:/CORTEX_DATA/classifier/public/%s/tf_files";

    public static final String CORTEX_TRAINING_TEMP =
            CORTEX_TF_FILES_PATH + "/temp";

    public static final String INCEPTION_PATH =
            "E:/Workspaces/CortexAPI/inception";

    public static final String CORTEX_USER_MODELS_PATH =
            CORTEX_TF_FILES_PATH + "/io/cortex/cortexapi/models/%s";

    public static final String TRAINING_STEPS_LOG =
            CORTEX_USER_MODELS_PATH + "/training_steps_log.txt";

    public static final String MODEL_DIR = "E:/tf_files/";

}
