package io.cortex.cortexapi.controller;

import io.cortex.cortexapi.db_models.Classifier;
import io.cortex.cortexapi.utils.SystemPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/files")
public class DownloadController {

//    @Autowired
//    ClassifierService classifierService;

    @RequestMapping(value = "/{model_key}/thumbnail", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response,
                             @PathVariable String model_key) throws IOException {

//        Classifier classifier = classifierService.findClassifierByModelKey(model_key);

        //the username and model key determines the path of the image
        String path = SystemPaths.CORTEX_USER_MODELS_PATH;

        /*
        for the meantime, we will use emair@gmair.com because database is not yet
        implemented. Once implemented, we should search for the username/email based
        on the model key so we can determine the path of the classifier and its content
         */

        //TODO change temporary email after you have implemented insert of classifier to database after training
        File file = new File(String.format(path, "emair@gmair.com", model_key) + "/thumb.jpg");

        if (!file.exists()) {
            String errorMessage = "Sorry. The file you are looking for does not exist";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }

        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        response.setContentType(mimeType);

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
}
