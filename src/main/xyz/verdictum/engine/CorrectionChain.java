// CorrectionChain.java
package verdictum.engine;

import verdictum.correction.*;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.ArrayList;
import java.util.List;

public class CorrectionChain {
    private final List<CorrectionRule> rules;

    public CorrectionChain() {
        rules = new ArrayList<>();
        rules.add(new DeathInMurderFilter());
        rules.add(new SurrenderDecay());
        rules.add(new HeavyCrimeDecay());
        rules.add(new LightCrimeSafeguard());
        rules.add(new DeathPenaltyCap());
    }

    public List<Crime> applyCrimeFilters(List<Crime> crimes) {
        for (CorrectionRule rule : rules) {
            crimes = rule.filterCrimes(crimes);
        }
        return crimes;
    }

    public List<InterferenceFactor> applyFactorAdjustments(List<Crime> crimes, List<InterferenceFactor> factors) {
        for (CorrectionRule rule : rules) {
            factors = rule.adjustFactors(crimes, factors);
        }
        return factors;
    }

    public double applyRInterCorrections(List<Crime> crimes, double rInter) {
        for (CorrectionRule rule : rules) {
            rInter = rule.correctRInter(crimes, rInter);
        }
        return rInter;
    }
}