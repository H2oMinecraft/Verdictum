// CorrectionRule.java
package verdictum.correction;

import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public interface CorrectionRule {
    List<Crime> filterCrimes(List<Crime> crimes);
    List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors);
    double correctRInter(List<Crime> crimes, double rInter);
}