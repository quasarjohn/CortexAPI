package io.cortex.cortexapi.controller;

import io.cortex.cortexapi.models.classification_models.Classification;
import io.cortex.cortexapi.service.OnlineClassificationService;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import io.cortex.cortexapi.service.UserService;
import io.cortex.cortexapi.utils.SystemPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClassificationController {

    @Autowired
    UserService userService;

    @Autowired
    OnlineClassificationService classificationService;

    @CrossOrigin(origins = SystemPaths.CROSS_ORIGINS)
    @GetMapping("/{api_key}/classifier/classify_image/{model_key}")
    public ReturnObject classify_image(@PathVariable String api_key,
                                       @PathVariable String model_key,
                                       @RequestParam(value = "img_url") String img_url,
                                       @RequestParam(value = "max_results", defaultValue = "5", required = false)
                                               Optional<Integer> max_results,
                                       @RequestParam(value = "order", defaultValue = "probability_desc", required = false)
                                               Optional<String> order)
            throws UnsupportedEncodingException {


        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        returnObject.setContent(classificationService.classifyImage(api_key, model_key, img_url, max_results.get(), order.get()));

        return returnObject;
    }

    private static String UPLOADED_FOLDER = SystemPaths.CORTEX_CLASSIFICATION_TEMP_PATH;

    @PostMapping("/{api_key}/classifier/upload_classify_image/{model_key}")
    @CrossOrigin(origins = SystemPaths.CROSS_ORIGINS)
    public ResponseEntity<?> upload_classify_model(
            @PathVariable(value = "api_key") String api_key,
            @PathVariable(value = "model_key") String model_key,
            @RequestParam("extraField") String extraField,
            @RequestParam("files") MultipartFile[] uploadfiles) throws IOException {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.BAD_REQUEST);
        // Get file name
        String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity(returnObject, HttpStatus.OK);
        }

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

            List<Classification> classifications = classificationService.
                    classifyImage(api_key, model_key, "file:///" + file_path, 5, "probability_desc");
            returnObject.setCode(ReturnCode.OK);
            returnObject.setContent(classifications);
        }
        return new ResponseEntity(returnObject, HttpStatus.OK);
    }
}
