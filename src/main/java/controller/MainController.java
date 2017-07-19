package controller;

import com.google.common.util.concurrent.*;
import models.classification_models.Classification;
import models.classification_models.ClassificationService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.training_models.ImageTrainingService;
import models.training_models.ImageTrainingProcess;


import javax.annotation.Nullable;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 7/15/2017.
 */

@RestController
public class MainController {

    //monitor process
    //UID, Process Object
    private static HashMap<String, ImageTrainingProcess> processes = new HashMap();

    //cached thread pool creates as many threads as possible for required tasks
    //and uses free threads once a task is done
    private static ExecutorService service = Executors.newCachedThreadPool();

    //guava api listening executor service for callbacks
    //notifies processes hashmap when a process is complete
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(service);

    @RequestMapping("/api/classifier/{key}/{path}")
    public ArrayList<Classification> classify(@PathVariable String key, @PathVariable String path) {

        //api key is used for verification

        ClassificationService classifier = new ClassificationService();
        ArrayList<Classification> classifications = classifier.classifyImage(path);
        return classifications;
    }

    @RequestMapping("/api/training/{key}/{category}/{training_steps}")
    public String training(@PathVariable String key, @PathVariable String category,
                           @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        //user may train models only one at a time
        //start models.training_models only if user is not training currently
        if (!userIsTraining(processes.get(key))) {
            //create new process
            service.execute(() -> {
                ImageTrainingService trainingService = new ImageTrainingService();
                //pass reference to processes hashmap for completion notification
                trainingService.setProcessMap(processes);
                Process process = trainingService.startTraining(key, category, training_steps);

                //submit process in the listening executor service for update when process is completed
                ListenableFuture<Process> listenableProcess = listeningExecutorService.submit(new Callable<Process>() {
                    @Override
                    public Process call() throws Exception {
                        process.waitFor();
                        processes.get(key).setStatus(ImageTrainingProcess.TrainingStatus.COMPLETE);
                        return null;
                    }
                });

                Futures.addCallback(listenableProcess, new FutureCallback<Process>() {
                    @Override
                    public void onSuccess(@Nullable Process process) {
                        System.out.println("TRAINING SUCCESS");
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        System.out.println("TRAINING FAILED");
                    }
                });

                //add process to monitored trainings so user can stop them later
                ImageTrainingProcess trainingProcess = new ImageTrainingProcess();
                trainingProcess.setProcess(process);
                trainingProcess.setCategory(category);
                trainingProcess.setStatus(ImageTrainingProcess.TrainingStatus.ONGOING);
                processes.put(key, trainingProcess);
            });
            return "Training Started";
        }

        return "User is already models.training_models a model";
    }

    private boolean userIsTraining(ImageTrainingProcess imageTrainingProcess) {
        if (imageTrainingProcess == null ||
                imageTrainingProcess.getStatus() == ImageTrainingProcess.TrainingStatus.COMPLETE)
            return false;
        return true;
    }

    //checks the status of the training
    @RequestMapping("/api/training/status/{key}")
    public String status(@PathVariable String key) {

        System.out.println(processes.size());

        ImageTrainingProcess process = processes.get(key);
        if (process == null)
            return "User is not training a model";
        else if (process.getStatus() == ImageTrainingProcess.TrainingStatus.ONGOING)
            return "Ongoing training";
        else if (process.getStatus() == ImageTrainingProcess.TrainingStatus.COMPLETE)
            return "Training is complete";
        return "XX";
    }

    //stops current training
    @RequestMapping("/api/training/stop/{key}")
    public String stop(@PathVariable String key) {

        ImageTrainingProcess trainingProcess = processes.get(key);

        if (trainingProcess == null || trainingProcess.getProcess() == null)
            return "User is not models.training_models a model";
        else
            trainingProcess.getProcess().destroy();
        return "Training stopped";
    }

    //get runtime of process in seconds
    @RequestMapping("/api/training/runtime/{key}")
    public String runtime(@PathVariable String key) {
        ImageTrainingProcess process = processes.get(key);

        if (process == null)
            return "Not found";
        else {

            return process.getProcess().info().totalCpuDuration().toString();
        }
    }
}
