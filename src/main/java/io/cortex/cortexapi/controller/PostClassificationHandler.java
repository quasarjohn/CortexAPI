package io.cortex.cortexapi.controller;

/**
 * Created by John on 7/24/2017.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cortex.cortexapi.models.classification_models.Classification;
import io.cortex.cortexapi.models.classification_models.OnlineClassificationService;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import io.cortex.cortexapi.utils.SystemPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostClassificationHandler {

    private static String UPLOADED_FOLDER = SystemPaths.CORTEX_CLASSIFICATION_TEMP_PATH;

    @PostMapping("/{api_key}/classifier/upload_classify_image/{model_key}")
    @CrossOrigin(origins = "http://192.168.0.149:8090")
    public ResponseEntity<?> uploadFileMulti(
            @PathVariable(value = "api_key") String api_key,
            @PathVariable(value = "model_key") String model_key,
            @RequestParam("extraField") String extraField,
            @RequestParam("files") MultipartFile[] uploadfiles) throws JsonProcessingException {

        System.out.println(api_key);

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.BAD_REQUEST);

        // Get file name
        String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {
            List<MultipartFile> files = Arrays.asList(uploadfiles);
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue; //next pls
                }
                String file_path = String.format(UPLOADED_FOLDER, api_key) + "/" + file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                Path path = Paths.get(file_path);
                //wait for the file to be written to disk then classify it
                Files.write(path, bytes);

                List<Classification> classifications = new OnlineClassificationService().
                        classifyImage("file:///" + file_path, 5, "probability_desc");
                returnObject.setCode(ReturnCode.OK);
                returnObject.setContent(classifications);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(returnObject, HttpStatus.OK);
    }

    //save file
    private void saveUploadedFiles(List<MultipartFile> files, String user) throws IOException {


    }
}