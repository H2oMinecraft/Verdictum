package verdictum.model;

public class Action {
    public String description;
    public double coefficient;

    @Override
    public String toString() {
        return String.format("[%s: %+.2f]", description, coefficient);
    }
}
