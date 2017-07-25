package io.cortex.cortexapi.models.classification_models;

import java.util.Comparator;

/**
 * Created by John on 7/14/2017.
 */
public class Classification implements Comparable<Classification> {

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

    @Override
    public int compareTo(Classification o) {
        float compareProb = o.getProbability();
        return (int) (this.probability - compareProb);
    }

    public static Comparator<Classification> ClassificationLabelComparatorAsc = (o1, o2) -> {
        String label1 = o1.getLabel();
        String label2 = o2.getLabel();

        return label1.compareTo(label2);
    };

    public static Comparator<Classification> ClassificationLabelComparatorDesc = (o1, o2) -> {
        String label1 = o1.getLabel();
        String label2 = o2.getLabel();

        return label2.compareTo(label1);
    };

    public static Comparator<Classification> ProbabilityComparatorAsc = (o1, o2) -> {
        float prob1 = o1.getProbability() * 10000;
        float prob2 = o2.getProbability() * 10000;

        return (int) (prob1 - prob2);
    };

    public static Comparator<Classification> ProbabilityComparatorDesc = (o1, o2) -> {
        float prob1 = o1.getProbability() * 10000;
        float prob2 = o2.getProbability() * 10000;

        return (int) (prob2 - prob1);
    };
}
