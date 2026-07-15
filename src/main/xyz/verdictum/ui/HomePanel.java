package verdictum.ui;

import verdictum.model.CaseRecord;
import verdictum.storage.CaseStorage;
import verdictum.ui.components.RoundedContainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class HomePanel extends JPanel {
    private JLabel statsLabel;
    private JTextArea bulletinArea;

    public HomePanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(40, 60, 40, 60));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Verdictum 重罪度分析系统", JLabel.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 119, 182));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("基于多维度量刑要素的智能推演工具", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);

        // 公告卡片
        RoundedContainer bulletinCard = new RoundedContainer();
        bulletinCard.setBackgroundColor(Color.WHITE);
        bulletinCard.setBorderColor(new Color(222, 226, 230));
        bulletinCard.setRadius(10);
        bulletinCard.setBorderThickness(1);
        bulletinCard.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel bulletinInner = new JPanel(new BorderLayout());
        bulletinInner.setOpaque(false);
        JLabel bulletinTitle = new JLabel("系统公告");
        bulletinTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        bulletinTitle.setForeground(new Color(33, 37, 41));
        bulletinInner.add(bulletinTitle, BorderLayout.NORTH);

        bulletinArea = new JTextArea(getBulletinText());
        bulletinArea.setEditable(false);
        bulletinArea.setLineWrap(true);
        bulletinArea.setWrapStyleWord(true);
        bulletinArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        bulletinArea.setOpaque(true);
        bulletinArea.setBackground(new Color(0, 0, 0, 0));
        JScrollPane bulletinScroll = new JScrollPane(bulletinArea);
        bulletinScroll.setBorder(BorderFactory.createEmptyBorder());
        bulletinInner.add(bulletinScroll, BorderLayout.CENTER);

        bulletinCard.wrap(bulletinInner);
        contentPanel.add(bulletinCard);

        // 统计卡片
        RoundedContainer statsCard = new RoundedContainer();
        statsCard.setBackgroundColor(Color.WHITE);
        statsCard.setBorderColor(new Color(222, 226, 230));
        statsCard.setRadius(10);
        statsCard.setBorderThickness(1);
        statsCard.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel statsInner = new JPanel(new BorderLayout());
        statsInner.setOpaque(false);
        JLabel statsTitle = new JLabel("今日统计");
        statsTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        statsTitle.setForeground(new Color(33, 37, 41));
        statsInner.add(statsTitle, BorderLayout.NORTH);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        statsLabel.setForeground(new Color(33, 37, 41));
        statsLabel.setOpaque(false);
        statsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        refreshStats();
        statsInner.add(statsLabel, BorderLayout.CENTER);

        statsCard.wrap(statsInner);
        contentPanel.add(statsCard);

        add(contentPanel, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("请使用左侧导航栏「分析」开始新案件分析", JLabel.CENTER);
        footerLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(173, 181, 189));
        add(footerLabel, BorderLayout.SOUTH);
    }

    private String getBulletinText() {
        return "欢迎使用 Verdictum 重罪度分析系统。\n\n"
                + "【近期更新】\n"
                + "• 新增多平台AI支持（OpenAI、Anthropic、Ollama等）。\n"
                + "• 优化圆角界面，视觉更统一。\n"
                + "• 支持本地模型，保护数据隐私。\n\n"
                + "【使用提示】\n"
                + "1. 在「分析」页输入详细案情，点击“开始分析”获取重罪度。\n"
                + "2. 分析完成后可保存至案件库，随时回顾。\n"
                + "3. 案件库支持按罪名筛选与删除。\n\n"
                + "如有建议或问题，欢迎反馈。";
    }

    private void refreshStats() {
        try {
            List<CaseRecord> records = CaseStorage.loadAll();
            int total = records.size();
            String lastTime = "暂无";
            if (!records.isEmpty()) {
                LocalDateTime latest = records.get(0).createdAt;
                lastTime = latest.toLocalDate().toString() + " " + latest.toLocalTime().toString().substring(0, 5);
            }
            String statsHtml = String.format(
                    "<html>案件总数：<b>%d</b><br>最近分析：<b>%s</b></html>",
                    total, lastTime);
            statsLabel.setText(statsHtml);
        } catch (Exception e) {
            statsLabel.setText("统计信息暂时无法获取");
        }
    }
}