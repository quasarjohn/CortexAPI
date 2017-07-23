package models.classification_models;

import java.util.Comparator;

/**
 * Created by John on 7/14/2017.
 */
public class Classification {

    private String label;
    private float probability;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }
}
