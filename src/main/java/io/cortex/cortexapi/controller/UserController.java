package io.cortex.cortexapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import io.cortex.cortexapi.models.user.CortexClassifier;
import io.cortex.cortexapi.utils.SystemPaths;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @RequestMapping("{username}/classifiers")
    @CrossOrigin(origins = SystemPaths.CROSS_ORIGINS)
    public ReturnObject getUserClassifiers(@PathVariable String username) {

        System.out.println("REQUEST FROM " + username);

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.NOT_FOUND);

        ArrayList<CortexClassifier> classifiers = new ArrayList<>();
        String user_classifiers_path = String.format(SystemPaths.CORTEX_TF_FILES_PATH, username) + "/models";

        //check every folder of the user containing a classifier model
        File folder_files = new File(user_classifiers_path);

        File[] files = folder_files.listFiles();
        if (files != null) {
            //for every folder, check metadata and map it to a classifier object
            for (File f : files) {
                CortexClassifier classifier;
                ObjectMapper mapper = new ObjectMapper();
                try {
                    classifier = mapper.readValue(new File(f.getPath() + "/metadata"), CortexClassifier.class);
                    classifiers.add(classifier);
                } catch (IOException e) {
                    e.printStackTrace();
                    return returnObject;
                }
            }
        }

        returnObject.setCode(ReturnCode.OK);
        returnObject.setContent(classifiers);

        return returnObject;
    }

    public void getUserInfo() {

    }

    public void getUserQuestions() {

    }

}
