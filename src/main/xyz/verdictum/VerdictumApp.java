package verdictum;

import verdictum.client.AIServiceClient;
import verdictum.config.ApiKeyManager;
import verdictum.engine.CalculationEngine;
import verdictum.loader.RuleLoader;
import verdictum.ui.*;
import verdictum.ui.components.RoundedBorder;
import verdictum.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class VerdictumApp extends JFrame {

    // ===== 调试开关 =====
    // 设为 true 时，每次启动都会弹出 API Key 输入框，不检查本地存储
    // 用于验证 Key 是否可用
    private static final boolean DEBUG = false;

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JList<String> navList;

    private RuleLoader ruleLoader;
    private AIServiceClient aiClient;
    private CalculationEngine engine;

    public VerdictumApp() {
        // ---- API Key 检查 ----
        if (DEBUG) {
            // 调试模式：每次都弹出输入框
            showAndSaveApiKey();
        } else {
            // 正常模式：没有保存 Key 时才弹出
            if (!ApiKeyManager.exists()) {
                showAndSaveApiKey();
            }
        }

        // 初始化后端
        try {
            ruleLoader = new RuleLoader();
            aiClient = new AIServiceClient();
            engine = new CalculationEngine();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "初始化失败：" + e.getMessage());
            System.exit(1);
        }

        initUI();
        setTitle("Verdictum - Felony Gravity Assessment System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // 弹出 API Key 输入对话框并保存（如果输入有效）
    private void showAndSaveApiKey() {
        String key = showApiKeyDialog();
        if (key == null || key.isBlank()) {
            JOptionPane.showMessageDialog(null, "必须配置 API Key，程序即将退出。");
            System.exit(1);
        }
        try {
            ApiKeyManager.saveKey(key);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "保存 API Key 失败：" + e.getMessage());
            System.exit(1);
        }
    }

    // 自定义扁平风格的 API Key 输入对话框
    private String showApiKeyDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("API Key 配置");
        dialog.setModal(true);
        dialog.setResizable(false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));
        panel.setBackground(new Color(248, 249, 250));

        JLabel label = new JLabel("请输入您的 API Key：");
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        panel.add(label, BorderLayout.NORTH);

        JPasswordField pwdField = new JPasswordField(30);
        pwdField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        pwdField.setOpaque(false);
        pwdField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(222, 226, 230), Color.WHITE, 8, 1),
                new EmptyBorder(4, 8, 4, 8)));
        panel.add(pwdField, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton cancelBtn = UIUtils.createFlatButton("取消", new Color(200, 60, 60), true);
        JButton okBtn = UIUtils.createFlatButton("确定", new Color(52, 119, 182), true);

        btnPanel.add(cancelBtn);
        btnPanel.add(okBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        final String[] result = {null};

        okBtn.addActionListener(e -> {
            result[0] = new String(pwdField.getPassword());
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return result[0];
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        // 全局扁平滚动条
        UIManager.put("ScrollBarUI", "verdictum.ui.components.FlatScrollBarUI");

        // 导航列表
        String[] navItems = {"主页", "分析", "案件库"};
        navList = new JList<>(navItems);
        navList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        navList.setFixedCellHeight(40);
        navList.setSelectedIndex(0);
        navList.setBackground(new Color(250, 250, 252));
        navList.setSelectionBackground(new Color(52, 119, 182));
        navList.setSelectionForeground(Color.WHITE);

        navList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(0, 10, 0, 10));
                return label;
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.add(new HomePanel(), "home");
        mainPanel.add(new AnalysisPanel(ruleLoader, aiClient, engine), "analysis");
        mainPanel.add(new CaseLibraryPanel(), "library");

        navList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = navList.getSelectedIndex();
                switch (idx) {
                    case 0: cardLayout.show(mainPanel, "home"); break;
                    case 1: cardLayout.show(mainPanel, "analysis"); break;
                    case 2: cardLayout.show(mainPanel, "library"); break;
                }
            }
        });

        JScrollPane navScroll = new JScrollPane(navList);
        navScroll.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(222, 226, 230)));
        navScroll.setPreferredSize(new Dimension(150, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navScroll, mainPanel);
        splitPane.setDividerLocation(150);
        splitPane.setDividerSize(0);
        add(splitPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerdictumApp().setVisible(true));
    }
}