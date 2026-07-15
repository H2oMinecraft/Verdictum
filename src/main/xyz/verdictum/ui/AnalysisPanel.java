package verdictum.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import verdictum.client.AIServiceClient;
import verdictum.config.RuleConstants;
import verdictum.engine.CalculationEngine;
import verdictum.loader.RuleLoader;
import verdictum.model.CaseRecord;
import verdictum.model.Crime;
import verdictum.model.InterferenceFactor;
import verdictum.model.ModelConfig;
import verdictum.parser.JsonResponseParser;
import verdictum.storage.CaseStorage;
import verdictum.ui.components.RoundedContainer;
import verdictum.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AnalysisPanel extends JPanel {
    private static final String VIEW_SETUP = "SETUP";
    private static final String VIEW_PROGRESS = "PROGRESS";

    private CardLayout viewLayout;
    private JPanel viewContainer;

    private JTextArea inputArea;
    private JComboBox<ModelConfig> modelCombo;
    private JCheckBox autoSaveCheck;
    private JButton startButton;

    private TerminalPanel terminalPanel;
    private JButton finishButton;
    private JLabel finalResultLabel;

    private String lastCrimesJson;
    private String lastFactorsJson;
    private double lastR;
    private String lastPenalty;
    private List<Crime> lastCrimes;
    private String caseText;

    private final RuleLoader ruleLoader;
    private final AIServiceClient aiClient;
    private final CalculationEngine engine;

    private List<ModelConfig> modelConfigs;

    private static final Color PRIMARY_COLOR = new Color(52, 119, 182);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 37, 41);

    public AnalysisPanel(RuleLoader ruleLoader, AIServiceClient aiClient, CalculationEngine engine) {
        this.ruleLoader = ruleLoader;
        this.aiClient = aiClient;
        this.engine = engine;
        loadModelConfigs();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(16, 16, 16, 16));
        viewLayout = new CardLayout();
        viewContainer = new JPanel(viewLayout);
        viewContainer.add(buildSetupView(), VIEW_SETUP);
        viewContainer.add(buildProgressView(), VIEW_PROGRESS);
        add(viewContainer, BorderLayout.CENTER);
        viewLayout.show(viewContainer, VIEW_SETUP);
    }

    private void loadModelConfigs() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/models.json"), StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<ModelConfig>>(){}.getType();
            modelConfigs = new Gson().fromJson(reader, listType);
        } catch (Exception e) {
            modelConfigs = new ArrayList<>();
            ModelConfig fallback = new ModelConfig();
            fallback.setDisplayName("默认模型");
            fallback.setApiModel("gpt-3.5-turbo");
            fallback.setPlatform("openai");
            modelConfigs.add(fallback);
        }
    }

    private JPanel buildSetupView() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);

        // 左侧：案情输入
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        JLabel inputLabel = new JLabel("案情描述");
        inputLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        inputLabel.setForeground(TEXT_DARK);
        leftPanel.add(inputLabel, BorderLayout.NORTH);

        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        inputArea.setOpaque(true);
        inputArea.setBackground(Color.WHITE);
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());
        RoundedContainer inputCard = new RoundedContainer();
        inputCard.setBackgroundColor(Color.WHITE);
        inputCard.setBorderColor(new Color(222, 226, 230));
        inputCard.setRadius(10);
        inputCard.setBorderThickness(1);
        inputCard.setBorder(new EmptyBorder(4, 4, 4, 4));
        inputCard.wrap(inputScroll);
        leftPanel.add(inputCard, BorderLayout.CENTER);

        // 右侧：设置面板
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 16, 0, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        // AI 模型选择
        gbc.gridy = 0;
        JLabel modelLabel = new JLabel("AI 模型");
        modelLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        rightPanel.add(modelLabel, gbc);
        gbc.gridy++;
        modelCombo = UIUtils.createFlatModelComboBox(modelConfigs);
        rightPanel.add(modelCombo, gbc);

        // 自动保存
        gbc.gridy++;
        gbc.insets = new Insets(12, 0, 4, 0);
        JLabel autoLabel = new JLabel("自动保存");
        autoLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        rightPanel.add(autoLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 4, 0);
        autoSaveCheck = UIUtils.createFlatCheckBox("分析完成后自动保存到案件库");
        rightPanel.add(autoSaveCheck, gbc);

        // 开始分析按钮
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        startButton = UIUtils.createFlatButton("开始分析", PRIMARY_COLOR, true);
        startButton.setPreferredSize(new Dimension(0, 36));
        startButton.addActionListener(e -> startAnalysis());
        rightPanel.add(startButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        rightPanel.add(new JLabel(), gbc);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildProgressView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel progressTitle = new JLabel("分析进度");
        progressTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        progressTitle.setForeground(TEXT_DARK);
        panel.add(progressTitle, BorderLayout.NORTH);

        terminalPanel = new TerminalPanel();
        // 你可以在这里自定义终端字体，例如：terminalPanel.setTerminalFont(new Font("Consolas", Font.PLAIN, 14));
        RoundedContainer terminalCard = new RoundedContainer();
        terminalCard.setBackgroundColor(Color.BLACK);
        terminalCard.setBorderColor(new Color(222, 226, 230));
        terminalCard.setRadius(10);
        terminalCard.setBorderThickness(1);
        terminalCard.setBorder(new EmptyBorder(4, 4, 4, 4));
        terminalCard.wrap(terminalPanel);
        panel.add(terminalCard, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
        bottomPanel.setOpaque(false);
        finalResultLabel = new JLabel("");
        finalResultLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        finalResultLabel.setForeground(PRIMARY_COLOR);
        bottomPanel.add(finalResultLabel, BorderLayout.CENTER);

        finishButton = UIUtils.createFlatButton("完成", PRIMARY_COLOR, true);
        finishButton.setVisible(false);
        finishButton.addActionListener(e -> onFinish());
        bottomPanel.add(finishButton, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void startAnalysis() {
        caseText = inputArea.getText().trim();
        if (caseText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入案情描述", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ModelConfig selected = (ModelConfig) modelCombo.getSelectedItem();
        if (selected != null) {
            aiClient.setModelConfig(selected);
        }
        boolean autoSave = autoSaveCheck.isSelected();

        viewLayout.show(viewContainer, VIEW_PROGRESS);
        terminalPanel.clear();
        terminalPanel.startCursor();
        finalResultLabel.setText("");
        finishButton.setVisible(false);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish(">> Semantic decomposition: extracting crimes and action coefficients...\n");

                String crimePrompt = ruleLoader.getPrompt("crime_extraction", caseText);
                String crimeJson = aiClient.call(crimePrompt);
                lastCrimesJson = crimeJson;
                lastCrimes = JsonResponseParser.parseCrimes(crimeJson);

                StringBuilder crimeReport = new StringBuilder(">> Extracted Crimes\n");
                for (Crime c : lastCrimes) {
                    crimeReport.append(String.format("  %s (Base: %.2f)\n", c.crime_name, c.base_value));
                    for (var a : c.actions) {
                        crimeReport.append(String.format("    - %s: %+.2f\n", a.description, a.coefficient));
                    }
                }
                publish(crimeReport.toString());

                publish("\n>> Identifying sentencing interference factors...\n");
                String factorPrompt = ruleLoader.getPrompt("interference_extraction", caseText);
                String factorJson = aiClient.call(factorPrompt);
                lastFactorsJson = factorJson;
                List<InterferenceFactor> factors = JsonResponseParser.parseInterference(factorJson);

                StringBuilder factorReport = new StringBuilder(">> Interference Factors\n");
                double kTotal = 1.0;
                for (InterferenceFactor f : factors) {
                    factorReport.append(String.format("  %s: k=%.2f\n", f.factor_name, f.k));
                    kTotal *= f.k;
                }
                factorReport.append(String.format("  Total Factor: %.4f\n", kTotal));
                publish(factorReport.toString());

                publish("\n>> Executing gravity calculation and correction chain...\n");
                double rFinal = engine.calculate(lastCrimes, factors);
                lastR = rFinal;
                String penalty = RuleConstants.mapToPenalty(rFinal);
                lastPenalty = penalty;

                String resultText = String.format(">> Final Gravity R_final = %.4f\n>> Penalty Mapping: %s", rFinal, penalty);
                publish(resultText);

                if (autoSave) {
                    saveToLibrary();
                    publish("\n>> Auto-saved to case library.\n");
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    terminalPanel.append(chunk);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    finalResultLabel.setText("分析完成！");
                    terminalPanel.stopCursor();
                    terminalPanel.append("\n==== MISSION ACCOMPLISHED ====\n");
                } catch (Exception e) {
                    terminalPanel.append("\n>> Analysis failed: " + e.getMessage() + "\n");
                    finalResultLabel.setText("分析出现错误");
                    terminalPanel.stopCursor();
                } finally {
                    finishButton.setVisible(true);
                }
            }
        };
        worker.execute();
    }

    private void saveToLibrary() {
        if (lastCrimesJson == null) return;
        CaseRecord record = new CaseRecord();
        record.caseText = caseText;
        record.crimesJson = lastCrimesJson;
        record.factorsJson = lastFactorsJson;
        record.rFinal = lastR;
        record.penalty = lastPenalty;
        record.category = lastCrimes.isEmpty() ? "未分类" : lastCrimes.get(0).crime_name;
        CaseStorage.save(record);
    }

    private void onFinish() {
        if (!autoSaveCheck.isSelected()) {
            saveToLibrary();
            terminalPanel.append("\n>> Saved to case library.\n");
        }
        viewLayout.show(viewContainer, VIEW_SETUP);
        inputArea.setText("");
        terminalPanel.clear();
    }
}