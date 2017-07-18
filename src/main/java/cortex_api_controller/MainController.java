package cortex_api_controller;

import classification_models.Classification;
import classification_models.Classifier;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import training.TrainLocal;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Created by John on 7/15/2017.
 */

@RestController
public class MainController {

    static Process process;
    private ExecutorService service = Executors.newCachedThreadPool();

    @RequestMapping("/hello")
    public String hello() {
        return "HELLO WORLD";
    }

    @RequestMapping("/classify/{user}")
    public ArrayList<Classification> classify(@PathVariable String user) {
        Classifier classifier = new Classifier();
        ArrayList<Classification> classifications = classifier.classifyImage(user);
        return classifications;
    }

    @RequestMapping("/train/{user}/{category}/{training_steps}")
    public String train(@PathVariable String user, @PathVariable String category,
                        @PathVariable int training_steps) throws NoSuchFieldException, IllegalAccessException {

        process = new TrainLocal().startTraining(user, category, training_steps);
        System.out.println("PARENT PROCESS" + process.pid());

        process.children().forEach(v -> {
            System.out.println(v.pid() + " " + v.info().command() + " ");

            v.children().forEach(w-> {
                System.out.println(w.pid());
            });
        });

        return "Training Started";
    }

    @RequestMapping("/stop/{user}")
    public String stop(@PathVariable String user) {
//        process.destroy();
        return process.pid() + "";
    }


}
