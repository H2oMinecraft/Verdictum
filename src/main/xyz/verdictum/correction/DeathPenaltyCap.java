// DeathPenaltyCap.java
package verdictum.correction;

import verdictum.config.RuleConstants;
import verdictum.model.Action;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public class DeathPenaltyCap implements CorrectionRule {
    @Override
    public List<Crime> filterCrimes(List<Crime> crimes) {
        return crimes;
    }

    @Override
    public List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors) {
        return factors;
    }

    @Override
    public double correctRInter(List<Crime> crimes, double rInter) {
        if (rInter <= 1.00) return rInter;
        if (!hasDeath(crimes)) {
            return Math.min(rInter, RuleConstants.DEATH_PENALTY_CAP);
        }
        return rInter;
    }

    private boolean hasDeath(List<Crime> crimes) {
        for (Crime c : crimes) {
            if ("故意杀人罪".equals(c.crime_name)) {
                boolean isUnfinished = c.actions.stream().anyMatch(a ->
                        a.description.contains("未遂") || a.description.contains("中止"));
                if (!isUnfinished) return true;
            } else {
                boolean hasDeathAction = c.actions.stream().anyMatch(a ->
                        a.description.contains("造成死亡"));
                if (hasDeathAction) return true;
            }
        }
        return false;
    }
}