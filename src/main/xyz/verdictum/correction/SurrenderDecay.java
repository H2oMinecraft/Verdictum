// SurrenderDecay.java
package verdictum.correction;

import verdictum.config.RuleConstants;
import verdictum.model.Action;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public class SurrenderDecay implements CorrectionRule {
    @Override
    public List<Crime> filterCrimes(List<Crime> crimes) {
        return crimes;
    }

    @Override
    public List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors) {
        boolean hasPremeditation = crimes.stream()
                .flatMap(c -> c.actions.stream())
                .anyMatch(a -> a.description.contains("预谋"));
        boolean hasCrueltyAction = crimes.stream()
                .flatMap(c -> c.actions.stream())
                .anyMatch(a -> a.description.contains("手段特别残忍"));
        boolean hasCrueltyFactor = factors.stream()
                .anyMatch(f -> f.factor_name.contains("特别残忍手段"));
        boolean hasMalice = factors.stream()
                .anyMatch(f -> f.factor_name.contains("主观恶意极其卑劣"));

        if (hasPremeditation && (hasCrueltyAction || hasCrueltyFactor) && hasMalice) {
            factors.forEach(f -> {
                if (f.factor_name.contains("自首")) {
                    f.k = RuleConstants.SURRENDER_DECAY_K;
                }
            });
        }
        return factors;
    }

    @Override
    public double correctRInter(List<Crime> crimes, double rInter) {
        return rInter;
    }
}