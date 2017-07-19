package models.training_models;

public class ImageTrainingProcess {

    private Process process;
    private String category;
    private TrainingStatus status = TrainingStatus.ONGOING;
    private String runtime;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public enum TrainingStatus {
        COMPLETE, ONGOING, STOPPED
    }
}
