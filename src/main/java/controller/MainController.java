package controller;

import com.google.common.util.concurrent.*;
import models.classification_models.Classification;
import models.classification_models.ClassificationService;

import models.training_models.TrainingLog;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import models.training_models.TrainingService;
import models.training_models.TrainingProcess;
import utls.ImageDirectoryUtils;
import utls.SystemPaths;
import utls.VerificationUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 7/15/2017.
 */

@RestController
@RequestMapping("/api")
public class MainController {

    //monitor process
    //UID, Process Object
    private static HashMap<String, TrainingProcess> processes = new HashMap();

    //cached thread pool creates as many threads as possible for required tasks
    //and uses free threads once a task is done
    private static ExecutorService service = Executors.newCachedThreadPool();

    //guava api listening executor service for callbacks
    //notifies processes hashmap when a process is complete
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(service);

    //TODO use specific model based on model_key. Update error return messages. These are temporary
    @RequestMapping("/{api_key}/classifier/classifications/{model_key}/{image_url}")
    public ArrayList<Classification> classify(@PathVariable String api_key,
                                              @PathVariable String model_key,
                                              @PathVariable String image_url) {
        ArrayList<Classification> classifications = new ArrayList<>();

        if (!VerificationUtils.userExists(api_key)) {
            Classification classification = new Classification();
            classification.setStatus("INVALID API KEY");
            return classifications;
        }


        //api key is used for verification
        //model key is used to specify which model to use for classification
        //image_url is the url of the image to classify


        ClassificationService classifier = new ClassificationService();
        classifications = classifier.classifyImage(image_url);
        return classifications;
    }

    @RequestMapping("/{api_key}/trainer/training/{category}/{training_steps}")
    public String training(@PathVariable String api_key,
                           @PathVariable String category,
                           @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        //category is also the title of the classifier
        //increasing the training steps increases the accuracy of the training

        //user may train models only one at a time
        //start training only if user is not training currently
        if (!VerificationUtils.userIsTraining(processes.get(api_key))) {
            //create new process
            service.execute(() -> {
                //get list of labels
                TrainingService trainingService = new TrainingService();
                //pass reference to processes hashmap for completion notification
                Process process = trainingService.startTraining(api_key, category, training_steps);
                //submit process in the listening executor service for update when process is completed
                ListenableFuture<Process> listenableProcess = listeningExecutorService.submit(new Callable<Process>() {
                    @Override
                    public Process call() throws Exception {
                        //wait for completion of training process then update processes hashmap
                        process.waitFor();
                        processes.get(api_key).setStatus(TrainingProcess.TrainingStatus.TRAINING_COMPLETE);
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

                //get the list of labels for the category
                ArrayList<String> labels = ImageDirectoryUtils.getLabelsList(
                        String.format(SystemPaths.CORTEX_TRAINING_TEMP, api_key)
                                + "/" + category);

                //add process to monitored trainings so user can stop them later
                TrainingProcess trainingProcess = new TrainingProcess();
                trainingProcess.setProcess(process);
                trainingProcess.setModel_name(category);
                trainingProcess.setModel_name(category);
                trainingProcess.setLabels(labels);
                trainingProcess.setStatus(TrainingProcess.TrainingStatus.TRAINING);
                processes.put(api_key, trainingProcess);
            });
            return "Training Started";
        }

        return "User is already training a model";
    }


    //checks the status of the training
    @RequestMapping("/{key}/trainer/info")
    public TrainingProcess info(@PathVariable String key) {
        TrainingProcess process = processes.get(key);

        TrainingProcess process_clone = null;

        if (process == null) {
            process_clone = new TrainingProcess();
            process_clone.setStatus(TrainingProcess.TrainingStatus.NULL);
        } else {
            process_clone = new TrainingProcess();
            process_clone.setStatus(process.getStatus());
            process_clone.setLabels(process.getLabels());
            process_clone.setModel_name(process.getModel_name());
            process_clone.setRuntime(process.getRuntime());
            process_clone.setRuntime(process.getProcess().info().totalCpuDuration().get().toSeconds() + "");
        }
        return process_clone;
    }

    @RequestMapping("/{key}/trainer/logs")
    public ArrayList<TrainingLog> logs(@PathVariable String key) {
        String logs = "";
        ArrayList<TrainingLog> trainingLogs = new ArrayList<>();

        System.out.println(processes.size());

        TrainingProcess process = processes.get(key);
        if (process == null) {
            TrainingLog log = new TrainingLog();
            log.setLog("User is already training");
            trainingLogs.add(log);
            return trainingLogs;
        } else {
            try {
                String path = String.format(SystemPaths.TRAINING_STEPS_LOG, key, process.getModel_name());
                Scanner scanner = new Scanner(new File(path));

                while (scanner.hasNextLine()) {
                    TrainingLog log = new TrainingLog();
                    log.setLog(scanner.nextLine());
                    trainingLogs.add(log);
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return trainingLogs;
    }

    //stops current training
    @RequestMapping("/{key}/trainer/stop")
    public String stop(@PathVariable String key) {

        TrainingProcess trainingProcess = processes.get(key);

        if (trainingProcess == null || trainingProcess.getProcess() == null)
            return "User is not models.training_models a model";
        else
            trainingProcess.getProcess().destroy();
        return "Training stopped";
    }


}
