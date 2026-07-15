// DeathInMurderFilter.java
package verdictum.correction;

import verdictum.model.Action;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.*;
import java.util.stream.Collectors;

public class DeathInMurderFilter implements CorrectionRule {
    @Override
    public List<Crime> filterCrimes(List<Crime> crimes) {
        return crimes.stream().map(crime -> {
            if ("故意杀人罪".equals(crime.crime_name)) {
                List<Action> filtered = crime.actions.stream()
                        .filter(a -> !a.description.contains("造成死亡"))
                        .collect(Collectors.toList());
                crime.actions = filtered;
            }
            return crime;
        }).collect(Collectors.toList());
    }

    @Override
    public List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors) {
        return factors;
    }

    @Override
    public double correctRInter(List<Crime> crimes, double rInter) {
        return rInter;
    }
}