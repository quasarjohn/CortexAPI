package io.cortex.cortexapi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.util.concurrent.*;

import io.cortex.cortexapi.models.classification_models.Classification;
import io.cortex.cortexapi.models.classification_models.OnlineClassificationService;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import io.cortex.cortexapi.models.training_models.ReturnableTrainingProcess;
import io.cortex.cortexapi.models.training_models.TrainingLog;
import io.cortex.cortexapi.utils.UnzipUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.cortex.cortexapi.models.training_models.TrainingService;
import io.cortex.cortexapi.models.training_models.TrainingProcess;
import io.cortex.cortexapi.utils.ImageDirectoryUtils;
import io.cortex.cortexapi.utils.SystemPaths;
import io.cortex.cortexapi.utils.Utils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by John on 7/15/2017.
 */

@RestController
@RequestMapping("/api")
public class TrainingController {

    private static UnzipUtility unzipUtility = new UnzipUtility();
    //monitor process
    //UID, Process Object
    private static HashMap<String, TrainingProcess> processes = new HashMap();

    //cached thread pool creates as many threads as possible for required tasks
    //and uses free threads once a task is done
    private static ExecutorService service = Executors.newCachedThreadPool();

    //guava api listening executor service for callbacks
    //notifies processes hashmap when a process is complete
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(service);

    @RequestMapping("/{api_key}/trainer/train_model/{category}/{training_steps}")
    public ReturnObject training(@PathVariable String api_key,
                                 @PathVariable String category,
                                 @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        ReturnObject returnObject = new ReturnObject();
        //category is also the title of the classifier
        //increasing the training steps increases the accuracy of the training

        //user may train only one at a time
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

        ReturnableTrainingProcess process_clone;

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
        ArrayList<TrainingLog> trainingLogs = new ArrayList<>();

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


    private static String UPLOADED_FOLDER = SystemPaths.CORTEX_TRAINING_TEMP;

    @PostMapping("/{api_key}/trainer/upload_train_model/{category}/{training_steps}")
    @CrossOrigin(origins = "http://192.168.0.149:8090")
    public ResponseEntity<?> upload_train_model(@PathVariable String api_key,
                                                @PathVariable String category,
                                                @PathVariable int training_steps,
                                                @RequestParam("files") MultipartFile[] uploadFiles) throws IOException {
        /*
        Steps in training model
        1. upload file
        2. unzip file
        3. start training
         */
        ReturnObject returnObject = new ReturnObject();
        //default is bad request. It shall change depending on the result of the training
        returnObject.setCode(ReturnCode.BAD_REQUEST);
        String file_path = String.format(UPLOADED_FOLDER, api_key) + "/" + category + ".zip";

        uploadFile(Arrays.asList(uploadFiles), returnObject, file_path);
        unzipUtility.unzip(file_path, String.format(UPLOADED_FOLDER, api_key));

        return new ResponseEntity<Object>(returnObject, HttpStatus.OK);
    }

    private void uploadFile(List<MultipartFile> files, ReturnObject returnObject, String file_path) throws IOException {
        for (MultipartFile file : files) {

            System.out.println(file.getOriginalFilename());


            InputStream inputStream = file.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(file_path));

            byte[] buffer = new byte[1024];
            int readbytes;
            double transfer = 0.0;
            double size = file.getSize();

            while ((readbytes = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, readbytes);
                transfer += readbytes;
                System.out.println(transfer / size);
            }
            inputStream.close();
            outputStream.close();

            returnObject.setCode(ReturnCode.OK);
        }
    }
}
