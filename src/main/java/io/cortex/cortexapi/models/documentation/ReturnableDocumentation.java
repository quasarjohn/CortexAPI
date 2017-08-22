package io.cortex.cortexapi.models.documentation;

import java.util.ArrayList;

public class ReturnableDocumentation {

    private String url, request, json_result;
    private ArrayList<Parameter> parameters;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getJson_result() {
        return json_result;
    }

    public void setJson_result(String json_result) {
        this.json_result = json_result;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Parameter> parameters) {
        this.parameters = parameters;
    }
}
