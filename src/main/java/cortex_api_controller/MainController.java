package cortex_api_controller;

import classification_models.Classification;
import classification_models.Classifier;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import training.ImageTrainer;
import training.ImageTrainingProcess;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 7/15/2017.
 */

@RestController
public class MainController {

    //monitor process
    //UID, Process Object
    private HashMap<String, ImageTrainingProcess> processes = new HashMap();

    private static ExecutorService service = Executors.newSingleThreadExecutor();

    @RequestMapping("/api/classify/{key}/{path}")
    public ArrayList<Classification> classify(@PathVariable String key, @PathVariable String path) {

        //api key is used for verification

        Classifier classifier = new Classifier();
        ArrayList<Classification> classifications = classifier.classifyImage(path);
        return classifications;
    }

    @RequestMapping("/api/train/{key}/{category}/{training_steps}")
    public String train(@PathVariable String key, @PathVariable String category,
                        @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        //user may train models only one at a time
        //start training only if process is null
        if (processes.get(key) == null) {
            //create new process
            service.execute(new Runnable() {
                @Override
                public void run() {
                    Process process = new ImageTrainer().startTraining(key, category, training_steps);
                    //add process to monitored trainings so user can stop them later
                    ImageTrainingProcess trainingProcess = new ImageTrainingProcess();
                    trainingProcess.setProcess(process);
                    trainingProcess.setCategory(category);
                    processes.put(key, trainingProcess);
                }
            });
            return "Training Started";
        }

        return "User is already training a model";
    }

    @RequestMapping("/api/stop/{key}")
    public String stop(@PathVariable String key) {

        ImageTrainingProcess trainingProcess = processes.get(key);

        if (trainingProcess == null || trainingProcess.getProcess() == null)
            return "User is not training a model";
        else
            trainingProcess.getProcess().destroy();
        return "Training stopped";
    }

    //get runtime of process in seconds
    @RequestMapping("/api/status/runtime/{key}")
    public String runtime(@PathVariable String key) {
        ImageTrainingProcess process = processes.get(key);

        if (process == null)
            return "Not found";
        else {

            return process.getProcess().info().totalCpuDuration().toString();
        }
    }
}
