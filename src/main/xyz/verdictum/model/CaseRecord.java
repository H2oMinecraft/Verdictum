package verdictum.model;

import java.time.LocalDateTime;

public class CaseRecord {
    public String id;               // 唯一ID
    public String caseText;         // 原始案情
    public String crimesJson;       // 提取罪名的 JSON 字符串
    public String factorsJson;      // 提取干扰素的 JSON 字符串
    public double rFinal;           // 最终重罪度
    public String penalty;          // 刑罚映射
    public String category;         // 自动分类（如“故意杀人罪”）
    public LocalDateTime createdAt; // 分析时间
}