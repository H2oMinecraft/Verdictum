// RuleConstants.java
package verdictum.config;

public class RuleConstants {
    // 轻罪从宽保护阈值
    public static final double LIGHT_CRIME_MIN_FACTOR = 0.82;
    // 重罪从宽衰减触发值
    public static final double HEAVY_CRIME_THRESHOLD = 1.20;
    // 死刑否定条款上限
    public static final double DEATH_PENALTY_CAP = 0.95;
    // 自首衰减值
    public static final double SURRENDER_DECAY_K = 0.98;
    public static final double SURRENDER_CANCEL_K = 1.00;
    // 重罪衰减调整值
    public static final double GUILTY_PLEA_HEAVY = 0.96;
    public static final double COMPENSATION_FORGIVENESS_HEAVY = 0.95;
    public static final double COMPENSATION_NO_FORGIVENESS_HEAVY = 0.98;
    // 多人死亡调整值
    public static final double GUILTY_PLEA_MULTI_DEATH = 0.98;
    public static final double COMPENSATION_MULTI_DEATH = 1.00;

    public static String mapToPenalty(double r) {
        if (r >= 1.0) return "死刑";
        if (r >= 0.80) return "长期徒刑/无期徒刑";
        if (r >= 0.60) return "中长期有期徒刑（3-15年）";
        if (r >= 0.40) return "拘役/短期有期徒刑（1个月-3年）";
        if (r >= 0.20) return "管制/缓刑";
        if (r >= 0.01) return "免予刑事处罚";
        return "无罪";
    }
}