package training; /**
 * Created by John on 7/15/2017.
 */


import values.SystemPaths;


public class ImageTrainer {
    public Process startTraining(String user, String category, int trainingSteps) {
        Process p = null;
        try {
            System.out.println("Training Started");
            String temp_path = String.format(SystemPaths.CORTEX_TRAINING_TEMP, user);
            String output_path = String.format(SystemPaths.CORTEX_USER_MODELS_PATH, user, category);

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
                    "--image_dir", training_images_path);
            p = builder.inheritIO().start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}
