// HeavyCrimeDecay.java
package verdictum.correction;

import verdictum.config.RuleConstants;
import verdictum.model.Action;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;

import java.util.List;

public class HeavyCrimeDecay implements CorrectionRule {
    @Override
    public List<Crime> filterCrimes(List<Crime> crimes) {
        return crimes;
    }

    @Override
    public List<InterferenceFactor> adjustFactors(List<Crime> crimes, List<InterferenceFactor> factors) {
        double rComb = calculateRComb(crimes);
        boolean hasDeath = crimes.stream().flatMap(c -> c.actions.stream())
                .anyMatch(a -> a.description.contains("死亡"));
        long deathCount = crimes.stream().flatMap(c -> c.actions.stream())
                .filter(a -> a.description.contains("每多一名伤亡")).count() + 1;

        if (rComb >= RuleConstants.HEAVY_CRIME_THRESHOLD && hasDeath) {
            factors.forEach(f -> {
                switch (f.factor_name) {
                    case "刚满18周岁":
                    case "刑事和解":
                        f.k = 1.00; break;
                    case "认罪认罚":
                        f.k = RuleConstants.GUILTY_PLEA_HEAVY; break;
                    case "积极赔偿被害人并取得谅解":
                        f.k = RuleConstants.COMPENSATION_FORGIVENESS_HEAVY; break;
                    case "积极赔偿但未取得谅解":
                        f.k = RuleConstants.COMPENSATION_NO_FORGIVENESS_HEAVY; break;
                    case "一般自首":
                        f.k = 0.95; break;
                }
            });
        }
        if (deathCount >= 2) {
            factors.forEach(f -> {
                switch (f.factor_name) {
                    case "认罪认罚":
                        f.k = RuleConstants.GUILTY_PLEA_MULTI_DEATH; break;
                    case "积极赔偿被害人并取得谅解":
                    case "积极赔偿但未取得谅解":
                        f.k = RuleConstants.COMPENSATION_MULTI_DEATH; break;
                    case "刚满18周岁":
                        f.k = 1.00; break;
                }
            });
        }
        return factors;
    }

    private double calculateRComb(List<Crime> crimes) {
        // 简化版：仅用于判断阈值，完整合并公式在 CalculationEngine 中
        return crimes.stream().mapToDouble(c ->
                c.base_value * (1 + c.actions.stream().mapToDouble(a -> a.coefficient).sum())
        ).max().orElse(0);
    }

    @Override
    public double correctRInter(List<Crime> crimes, double rInter) {
        return rInter;
    }
}