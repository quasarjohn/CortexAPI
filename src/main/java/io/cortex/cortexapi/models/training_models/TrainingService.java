package io.cortex.cortexapi.models.training_models; /**
 * Created by John on 7/15/2017.
 */


import io.cortex.cortexapi.utils.SystemPaths;

import java.io.File;


public class TrainingService {



    public Process startTraining(String user, String category, int trainingSteps) {
        Process p = null;
        try {
            System.out.println("Training Started");
            String temp_path = String.format(SystemPaths.CORTEX_TRAINING_TEMP, user);
            String output_path = String.format(SystemPaths.CORTEX_USER_MODELS_PATH, user, category);
            String training_logs_dir = String.format(SystemPaths.TRAINING_STEPS_LOG, user, category);

            //make sure the path to assets exits to avoid errors in training
            File f = new File(temp_path);
            f.mkdirs();

            f = new File(output_path);
            f.mkdirs();

            //bottlenecks
            String bottlenecks_path = temp_path + "/bottlenecks";
            //model dir
            String inception_path = SystemPaths.INCEPTION_PATH;
            //output graph
            String output_model = output_path + "/retrained_graph.pb";
            //output labels
            String output_labels = output_path + "/retrained_labels.txt";
            //image dir
            String training_images_path = temp_path + "/" + category;
            System.out.println("BOTTLENECKS " + bottlenecks_path + "\n" +
                    "INCEPTION: " + inception_path + "\n" +
                    "OUTPUT MODEL: " + output_model + "\n" +
                    "OUTPUT LABELS: " + output_labels + "\n" +
                    "TRAINING IMAGES: " + training_images_path + "\n");

            ProcessBuilder builder = new ProcessBuilder("python", "retrain.py",
                    "--bottleneck_dir", bottlenecks_path,
                    "--how_many_training_steps", trainingSteps + "", "" +
                    "--model_dir", inception_path,
                    "--output_graph", output_model,
                    "--output_labels", output_labels,
                    "--image_dir", training_images_path,
                    "--training_logs_dir",training_logs_dir );
            p = builder.inheritIO().start();

//            System.out.println("TRAINING COMPLETED FOR " + user);
//
//            //once training is done, remove process from hash map to allow user to train new io.cortex.cortexapi.models
//            ImageTrainingProcess process = processMap.get(user);
//            process.setStatus(ImageTrainingProcess.TrainingStatus.COMPLETE);
//            process.setRuntime(process.getProcess().info().totalCpuDuration().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}
