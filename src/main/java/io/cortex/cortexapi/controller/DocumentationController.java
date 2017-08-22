package io.cortex.cortexapi.controller;

import io.cortex.cortexapi.models.documentation.Parameter;
import io.cortex.cortexapi.models.documentation.ReturnableDocumentation;
import io.cortex.cortexapi.models.return_models.ReturnCode;
import io.cortex.cortexapi.models.return_models.ReturnObject;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/docs")
public class DocumentationController {

    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/classifier/classify-from-url")
    public ReturnObject getDocClassifyFromURL() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>GET</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/classifier/classify_image" +
                "/<span class=\"red-span\">>model_key</span>?img_url=" +
                "<span class=\"red-span\">{img_url}</span>/");

        documentation.setJson_result("{\n" +
                "\"code\":\"200 OK\",\n" +
                "\"content\":[\n" +
                "\t{\"label\":\"100 pesos\",\"probability\":0.557856},\n" +
                "\t{\"label\":\"1000 pesos\",\"probability\":0.265987},\n" +
                "\t{\"label\":\"200 pesos\",\"probability\":0.0758789},\n" +
                "\t{\"label\":\"10 pesos\",\"probability\":0.028565468},\n" +
                "\t{\"label\":\"500 pesos\",\"probability\":0.02411317}]\n" +
                "}");
        documentation.setRequest("import javax.ws.rs.client.Client;\n" +
                "import javax.ws.rs.client.ClientBuilder;\n" +
                "import javax.ws.rs.client.Entity;\n" +
                "import javax.ws.rs.core.Response;\n" +
                "import javax.ws.rs.core.MediaType;\n" +
                "\n" +
                "Client client = ClientBuilder.newClient();\n" +
                "Response response = client.target(\"http://192.168.0.149:8091/api/" +
                "api_key/classifier/classify_image/" +
                "model_key</span>?img_url={img_url}\")\n" +
                ".request(MediaType.TEXT_PLAIN_TYPE)\n" +
                ".get();\n" +
                "\n" +
                "System.out.println(\"status: \" + response.getStatus());\n" +
                "System.out.println(\"headers: \" + response.getHeaders());\n" +
                "System.out.println(\"body:\" + response.readEntity(String.class));");

        Parameter parameter = new Parameter();
        parameter.setTitle("img_url");
        parameter.setDescription("URL of the image you want to classify");
        parameter.setParamType(Parameter.ParamType.String);

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        Parameter parameter2 = new Parameter();
        parameter2.setTitle("model_key");
        parameter2.setDescription("Key of the classifier model.");
        parameter2.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        parameters.add(parameter1);
        parameters.add(parameter2);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }


    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/classifier/upload-classify")
    public ReturnObject getDocUploadAndClassify() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/classifier/upload_classify_image" +
                "/<span class=\"red-span\">model_key</span>?img_url=" +
                "<span class=\"red-span\">{img_url}</span>/");

        documentation.setJson_result("{\n" +
                "\"code\":\"200 OK\",\n" +
                "\"content\":[\n" +
                "\t{\"label\":\"100 pesos\",\"probability\":0.557856},\n" +
                "\t{\"label\":\"1000 pesos\",\"probability\":0.265987},\n" +
                "\t{\"label\":\"200 pesos\",\"probability\":0.0758789},\n" +
                "\t{\"label\":\"10 pesos\",\"probability\":0.028565468},\n" +
                "\t{\"label\":\"500 pesos\",\"probability\":0.02411317}]\n" +
                "}");

        documentation.setRequest("");

        Parameter parameter = new Parameter();
        parameter.setTitle("file");
        parameter.setDescription("Image from POST request");
        parameter.setParamType(Parameter.ParamType.MultiPartFile);

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        Parameter parameter2 = new Parameter();
        parameter2.setTitle("model_key");
        parameter2.setDescription("Key of the classifier model.");
        parameter2.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        parameters.add(parameter1);
        parameters.add(parameter2);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }


    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/trainer/train")
    public ReturnObject getDocTrain() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/trainer/upload_train_model/" +
                "<span class=\"red-span\">classifier_name</span>/<span class=\"red-span\">training_steps</span>");

        documentation.setJson_result("{\n" +
                "\t\"code\":\"200 OK\",\n" +
                "\t\"content\":{\"percentage\":\"0.57\",\"log\":\"Processing image 33\"}\n" +
                "}");

        documentation.setRequest("");

        Parameter parameter = new Parameter();
        parameter.setTitle("file");
        parameter.setDescription("Zip file from POST request containing the training images");
        parameter.setParamType(Parameter.ParamType.MultiPartFile);

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        Parameter parameter2 = new Parameter();
        parameter2.setTitle("classifier_name");
        parameter2.setDescription("Your chosen name for the classifier.");
        parameter2.setParamType(Parameter.ParamType.String);

        Parameter parameter3 = new Parameter();
        parameter3.setTitle("training_steps");
        parameter3.setDescription("Number of training steps.");
        parameter3.setParamType(Parameter.ParamType.Integer);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter);
        parameters.add(parameter1);
        parameters.add(parameter2);
        parameters.add(parameter3);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }


    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/trainer/status")
    public ReturnObject getStatus() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/trainer/status");

        documentation.setJson_result("{\n" +
                "\t\"code\":\"200 OK\",\n" +
                "\t\"content\":{\"percentage\":\"1.17\",\"log\":\"Processing image 68\"}\n" +
                "}");

        documentation.setRequest("import javax.ws.rs.client.Client;\\n\" +\n" +
                "                \"import javax.ws.rs.client.ClientBuilder;\\n\" +\n" +
                "                \"import javax.ws.rs.client.Entity;\\n\" +\n" +
                "                \"import javax.ws.rs.core.Response;\\n\" +\n" +
                "                \"import javax.ws.rs.core.MediaType;\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"Client client = ClientBuilder.newClient();\\n\" +\n" +
                "                \"Response response = client.target(\\\"http://192.168.0.149:8091/api/user1/trainer/status/\" +\n" +
                "                \"model_key</span>?img_url={img_url}\\\")\\n\" +\n" +
                "                \".request(MediaType.TEXT_PLAIN_TYPE)\\n\" +\n" +
                "                \".get();\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"System.out.println(\\\"status: \\\" + response.getStatus());\\n\" +\n" +
                "                \"System.out.println(\\\"headers: \\\" + response.getHeaders());\\n\" +\n" +
                "                \"System.out.println(\\\"body:\\\" + response.readEntity(String.class));");

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }


    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/trainer/info")
    public ReturnObject getTrainingInfo() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/trainer/info");

        documentation.setJson_result("{\n" +
                "\t\"code\":\"200 OK\",\n" +
                "\t\"content\":{\"model_name\":\"hand_gestures\",\"status\":\"TRAINING\",\"runtime\":\"99\",\"labels\":[\"1 Peso\",\"10 Pesos\",\"100 Pesos\",\"1000 Pesos\",\"20 Pesos\",\"200 Pesos\",\"25 Cents\",\"5 Pesos\",\"500 Pesos\"],\"file_count\":1798,\"steps\":4000}\n" +
                "}");

        documentation.setRequest("import javax.ws.rs.client.Client;\\n\" +\n" +
                "                \"import javax.ws.rs.client.ClientBuilder;\\n\" +\n" +
                "                \"import javax.ws.rs.client.Entity;\\n\" +\n" +
                "                \"import javax.ws.rs.core.Response;\\n\" +\n" +
                "                \"import javax.ws.rs.core.MediaType;\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"Client client = ClientBuilder.newClient();\\n\" +\n" +
                "                \"Response response = client.target(\\\"http://192.168.0.149:8091/api/user1/trainer/info/\" +\n" +
                "                \"model_key</span>?img_url={img_url}\\\")\\n\" +\n" +
                "                \".request(MediaType.TEXT_PLAIN_TYPE)\\n\" +\n" +
                "                \".get();\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"System.out.println(\\\"status: \\\" + response.getStatus());\\n\" +\n" +
                "                \"System.out.println(\\\"headers: \\\" + response.getHeaders());\\n\" +\n" +
                "                \"System.out.println(\\\"body:\\\" + response.readEntity(String.class));");

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }


    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/trainer/logs")
    public ReturnObject getTraningLogs() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/trainer/logs");

        documentation.setJson_result("TODO");

        documentation.setRequest("import javax.ws.rs.client.Client;\\n\" +\n" +
                "                \"import javax.ws.rs.client.ClientBuilder;\\n\" +\n" +
                "                \"import javax.ws.rs.client.Entity;\\n\" +\n" +
                "                \"import javax.ws.rs.core.Response;\\n\" +\n" +
                "                \"import javax.ws.rs.core.MediaType;\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"Client client = ClientBuilder.newClient();\\n\" +\n" +
                "                \"Response response = client.target(\\\"http://192.168.0.149:8091/api/user1/trainer/logs/\" +\n" +
                "                \"model_key</span>?img_url={img_url}\\\")\\n\" +\n" +
                "                \".request(MediaType.TEXT_PLAIN_TYPE)\\n\" +\n" +
                "                \".get();\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"System.out.println(\\\"status: \\\" + response.getStatus());\\n\" +\n" +
                "                \"System.out.println(\\\"headers: \\\" + response.getHeaders());\\n\" +\n" +
                "                \"System.out.println(\\\"body:\\\" + response.readEntity(String.class));");

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }

    @CrossOrigin(origins = "http://192.168.0.149:8090")
    @GetMapping("/trainer/stop")
    public ReturnObject stopTraining() {

        ReturnObject returnObject = new ReturnObject();
        returnObject.setCode(ReturnCode.OK);

        ReturnableDocumentation documentation = new ReturnableDocumentation();

        documentation.setUrl("<code>POST</code>http://192.168.0.149:8091/api/" +
                "<span class=\"red-span\">api_key</span>/trainer/stop");

        documentation.setJson_result("TODO");

        documentation.setRequest("import javax.ws.rs.client.Client;\\n\" +\n" +
                "                \"import javax.ws.rs.client.ClientBuilder;\\n\" +\n" +
                "                \"import javax.ws.rs.client.Entity;\\n\" +\n" +
                "                \"import javax.ws.rs.core.Response;\\n\" +\n" +
                "                \"import javax.ws.rs.core.MediaType;\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"Client client = ClientBuilder.newClient();\\n\" +\n" +
                "                \"Response response = client.target(\\\"http://192.168.0.149:8091/api/user1/trainer/stop/\" +\n" +
                "                \"model_key</span>?img_url={img_url}\\\")\\n\" +\n" +
                "                \".request(MediaType.TEXT_PLAIN_TYPE)\\n\" +\n" +
                "                \".get();\\n\" +\n" +
                "                \"\\n\" +\n" +
                "                \"System.out.println(\\\"status: \\\" + response.getStatus());\\n\" +\n" +
                "                \"System.out.println(\\\"headers: \\\" + response.getHeaders());\\n\" +\n" +
                "                \"System.out.println(\\\"body:\\\" + response.readEntity(String.class));");

        Parameter parameter1 = new Parameter();
        parameter1.setTitle("api_key");
        parameter1.setDescription("Your API key");
        parameter1.setParamType(Parameter.ParamType.String);

        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(parameter1);

        documentation.setParameters(parameters);

        returnObject.setContent(documentation);
        return returnObject;
    }
}



