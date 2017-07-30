package io.cortex.cortexapi.models.training_models;

import java.util.ArrayList;

public class TrainingProcess {

    private String model_name;
    private TrainingStatus status = TrainingStatus.TRAINING;
    private String runtime;
    private ArrayList<String> labels;
    private Process process;
    private int file_count;
    private int steps;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public TrainingStatus getStatus() {
        return status;
    }

    public void setStatus(TrainingStatus status) {
        this.status = status;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }


    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public enum TrainingStatus {
        TRAINING, TRAINING_STOPPED, TRAINING_COMPLETE, NULL
    }

    public int getFile_count() {
        return file_count;
    }

    public void setFile_count(int file_count) {
        this.file_count = file_count;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
