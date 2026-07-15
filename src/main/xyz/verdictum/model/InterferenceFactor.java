package verdictum.model;

public class InterferenceFactor {
    public String factor_name;
    public double k;

    @Override
    public String toString() {
        return String.format("%s (k=%.2f)", factor_name, k);
    }
}
