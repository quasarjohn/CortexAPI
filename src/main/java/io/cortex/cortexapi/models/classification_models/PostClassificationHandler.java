package io.cortex.cortexapi.models.classification_models;

/**
 * Created by John on 7/24/2017.
 */

import io.cortex.cortexapi.models.return_models.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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

/*
normally, an image from a url will be classified from a url. But when an image is uploaded
for classification, this class takes care of that
 */
@RestController
@RequestMapping("/api/upload")
public class PostClassificationHandler {

    private final Logger logger = LoggerFactory.getLogger(PostClassificationHandler.class);

    private static String UPLOADED_FOLDER = "Z://temp//";

    //origins is the ip address of the web server. this explicitly tells the api server
    //to allow requests from this specific address
    @PostMapping("/multi")
    @CrossOrigin(origins = "http://192.168.0.149:8090")
    public ResponseEntity<?> uploadFileMulti(
            @RequestParam("extraField") String extraField,
            @RequestParam("files") MultipartFile[] uploadfiles) {

        logger.debug("Multiple file upload!");

        // Get file name
        String uploadedFileName = Arrays.stream(uploadfiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {

            saveUploadedFiles(Arrays.asList(uploadfiles));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode("OKAY");

        Classification classification = new Classification();
        classification.setLabel("A");
        classification.setProbability(50);

        returnObject.setContent(classification);

        return new ResponseEntity(returnObject, HttpStatus.OK);
    }

    @PostMapping("/multi/model")
    @CrossOrigin(origins = "http://192.168.0.149:8090")
    public ResponseEntity<?> multiUploadFileModel(@ModelAttribute UploadModel model) {

        try {

            saveUploadedFiles(Arrays.asList(model.getFiles()));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Successfully uploaded!", HttpStatus.OK);

    }

    //save file
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

        }

    }
}