package classification_models;

import java.util.Comparator;

/**
 * Created by John on 7/14/2017.
 */
public class Classification implements Comparable<Classification>{

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
        return 0;
    }

    public static Comparator<Classification> ProbabilityComparator =
            new Comparator<Classification>() {
                @Override
                public int compare(Classification c1, Classification c2) {
                    return (int) (c1.getProbability() - c2.getProbability());
                }
            };
}
