// CalculationEngine.java
package verdictum.engine;

import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;
import verdictum.util.MathUtils;

import java.util.List;

public class CalculationEngine {
    private final CorrectionChain corrections;

    public CalculationEngine() {
        this.corrections = new CorrectionChain();
    }

    public double calculate(List<Crime> crimes, List<InterferenceFactor> factors) {
        // 1. 应用修正规则过滤罪名
        crimes = corrections.applyCrimeFilters(crimes);
        factors = corrections.applyFactorAdjustments(crimes, factors);

        // 2. 计算单罪重罪度
        double[] rValues = crimes.stream()
                .mapToDouble(c -> c.base_value * (1 + c.actions.stream()
                        .mapToDouble(a -> a.coefficient).sum()))
                .toArray();

        // 3. 多罪合并（负数保护）
        double rComb = MathUtils.combineCrimes(rValues);

        // 4. 干扰修正
        double kTotal = factors.stream()
                .mapToDouble(f -> f.k).reduce(1.0, (a, b) -> a * b);
        double rInter = rComb * kTotal;

        // 5. 应用R_inter修正
        rInter = corrections.applyRInterCorrections(crimes, rInter);

        // 6. 最终钳制
        return MathUtils.clamp(rInter, 0.0, 1.0);
    }
}