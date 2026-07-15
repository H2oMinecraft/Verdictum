package verdictum.model;

import java.util.List;

public class Crime {
    public String crime_name;
    public double base_value;
    public List<Action> actions;

    @Override
    public String toString() {
        return String.format("%s (Base=%.2f, 动作数=%d)",
                crime_name, base_value, actions.size());
    }
}
