// LightCrimeSafeguard.java
package verdictum.correction;

import verdictum.config.RuleConstants;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public class LightCrimeSafeguard implements CorrectionRule {
    @Override
    public List<Crime> filterCrimes(List<Crime> crimes) {
        return crimes;
    }

    @Override
    public List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors) {
        boolean allLight = crimes.stream().allMatch(c -> c.base_value <= 0.45);
        boolean noHarm = crimes.stream().flatMap(c -> c.actions.stream())
                .noneMatch(a -> a.description.contains("重伤") || a.description.contains("死亡"));

        if (allLight && noHarm) {
            double total = factors.stream().mapToDouble(f -> f.k).reduce(1.0, (a, b) -> a * b);
            if (total < RuleConstants.LIGHT_CRIME_MIN_FACTOR) {
                double scale = RuleConstants.LIGHT_CRIME_MIN_FACTOR / total;
                factors.forEach(f -> f.k = Math.min(f.k * scale, 1.0));
            }
        }
        return factors;
    }

    @Override
    public double correctRInter(List<Crime> crimes, double rInter) {
        return rInter;
    }
}