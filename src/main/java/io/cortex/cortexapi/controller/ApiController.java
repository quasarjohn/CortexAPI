package io.cortex.cortexapi.controller;

import com.google.common.util.concurrent.*;

import io.cortex.cortexapi.models.classification_models.OnlineClassificationService;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import io.cortex.cortexapi.models.training_models.ReturnableTrainingProcess;
import io.cortex.cortexapi.models.training_models.TrainingLog;
import org.springframework.web.bind.annotation.*;

import io.cortex.cortexapi.models.training_models.TrainingService;
import io.cortex.cortexapi.models.training_models.TrainingProcess;
import io.cortex.cortexapi.utils.ImageDirectoryUtils;
import io.cortex.cortexapi.utils.SystemPaths;
import io.cortex.cortexapi.utils.Utils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by John on 7/15/2017.
 */

@RestController
@RequestMapping("/api")
public class ApiController {

    //monitor process
    //UID, Process Object
    private static HashMap<String, TrainingProcess> processes = new HashMap();

    //cached thread pool creates as many threads as possible for required tasks
    //and uses free threads once a task is done
    private static ExecutorService service = Executors.newCachedThreadPool();

    //guava api listening executor service for callbacks
    //notifies processes hashmap when a process is complete
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(service);

    @GetMapping("/{api_key}/classifier/classify_image/{model_key}")
    public ReturnObject classify_image(@PathVariable String api_key,
                                       @PathVariable String model_key,
                                       @RequestParam(value = "img_url") String img_url,
                                       @RequestParam(value = "max_results", defaultValue = "3", required = false)
                                               Optional<Integer> max_results,
                                       @RequestParam(value = "order", defaultValue = "probability_desc", required = false)
                                               Optional<String> order)
            throws UnsupportedEncodingException {

        ReturnObject returnObject = new ReturnObject();


        returnObject.setCode(ReturnCode.OK);
        OnlineClassificationService classificationService = new OnlineClassificationService();
        returnObject.setContent(classificationService.classifyImage(img_url, max_results.get(), order.get()));

        return returnObject;
    }

    @RequestMapping("/{api_key}/trainer/training/{category}/{training_steps}")
    public ReturnObject training(@PathVariable String api_key,
                                 @PathVariable String category,
                                 @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        ReturnObject returnObject = new ReturnObject();
        //category is also the title of the classifier
        //increasing the training steps increases the accuracy of the training

        //user may train io.cortex.cortexapi.models only one at a time
        //start training only if user is not training currently
        if (!Utils.userIsTraining(processes.get(api_key))) {
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
            returnObject.setCode(ReturnCode.OK);
            return returnObject;
        } else {
            returnObject.setCode(ReturnCode.FORBIDDEN);
            return returnObject;
        }
    }


    //checks the status of the training
    @RequestMapping("/{key}/trainer/info")
    public ReturnObject info(@PathVariable String key) {
        ReturnObject returnObject = new ReturnObject();
        TrainingProcess process = processes.get(key);

        ReturnableTrainingProcess process_clone = null;

        if (process == null) {
            returnObject.setCode(ReturnCode.NOT_FOUND);
            return returnObject;
        } else {
            process_clone = Utils.mapTrainingProcess(process);
        }

        returnObject.setCode(ReturnCode.OK);
        returnObject.setContent(process_clone);

        return returnObject;
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
    public ReturnObject stop(@PathVariable String key) {
        ReturnObject returnObject = new ReturnObject();

        TrainingProcess trainingProcess = processes.get(key);

        if (trainingProcess == null || trainingProcess.getProcess() == null) {
            //user is not training a model. no existing process found
            returnObject.setCode(ReturnCode.NOT_FOUND);
            return returnObject;
        } else {
            trainingProcess.getProcess().destroy();
            returnObject.setCode(ReturnCode.OK);
            return returnObject;
        }
    }

    @RequestMapping("/test/{name}")
    public String test(@PathVariable String name, @RequestParam(value = "path", defaultValue = "") String path) {
        return name + " : " + path;

    }
}
