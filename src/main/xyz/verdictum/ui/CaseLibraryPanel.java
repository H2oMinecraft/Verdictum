package verdictum.ui;

import verdictum.model.CaseRecord;
import verdictum.storage.CaseStorage;
import verdictum.ui.components.RoundedBorder;
import verdictum.ui.components.RoundedContainer;
import verdictum.util.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.List;

public class CaseLibraryPanel extends JPanel {
    private DefaultListModel<CaseRecord> listModel;
    private JList<CaseRecord> caseList;
    private JTextArea detailArea;
    private JComboBox<String> categoryFilter;

    private static final Color DANGER_COLOR = new Color(200, 60, 60);
    private static final Font SANS_FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);

    public CaseLibraryPanel() {
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // 顶部筛选栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        topPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("筛选罪名：");
        filterLabel.setFont(SANS_FONT);
        topPanel.add(filterLabel);

        categoryFilter = UIUtils.createFlatComboBox(new String[]{"全部"});
        categoryFilter.addActionListener(e -> applyFilter());
        topPanel.add(categoryFilter);

        add(topPanel, BorderLayout.NORTH);

        // 案件列表（左侧，圆角容器）
        listModel = new DefaultListModel<>();
        caseList = new JList<>(listModel);
        caseList.setFont(SANS_FONT);
        caseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                CaseRecord r = (CaseRecord) value;
                String label = String.format("%s | R=%.4f | %s", r.category, r.rFinal, r.createdAt.toLocalDate());
                JLabel c = (JLabel) super.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
                c.setFont(SANS_FONT);
                c.setBorder(new EmptyBorder(4, 8, 4, 8));
                c.setOpaque(false);
                return new RoundedListCell(c, isSelected);
            }
        });
        caseList.addListSelectionListener(e -> showDetail(caseList.getSelectedValue()));

        JScrollPane listScroll = new JScrollPane(caseList);
        listScroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedContainer listCard = new RoundedContainer();
        listCard.setBackgroundColor(Color.WHITE);
        listCard.setBorderColor(new Color(222, 226, 230));
        listCard.setRadius(10);
        listCard.setBorderThickness(1);
        listCard.setPreferredSize(new Dimension(260, 0));
        listCard.wrap(listScroll);
        add(listCard, BorderLayout.WEST);

        // 详情区（右侧，圆角容器）
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        detailArea.setOpaque(false);
        detailArea.setBackground(new Color(0, 0, 0, 0));
        detailArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailScroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedContainer detailCard = new RoundedContainer();
        detailCard.setBackgroundColor(new Color(250, 250, 252));
        detailCard.setBorderColor(new Color(222, 226, 230));
        detailCard.setRadius(10);
        detailCard.setBorderThickness(1);
        detailCard.wrap(detailScroll);
        add(detailCard, BorderLayout.CENTER);

        // 删除按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        JButton deleteBtn = UIUtils.createFlatButton("删除选中", DANGER_COLOR, true);
        deleteBtn.setFont(SANS_FONT);
        deleteBtn.addActionListener(e -> deleteSelected());
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshData() {
        List<CaseRecord> all = CaseStorage.loadAll();
        categoryFilter.removeAllItems();
        categoryFilter.addItem("全部");
        all.stream().map(r -> r.category).distinct().forEach(c -> categoryFilter.addItem(c));
        applyFilter();
    }

    private void applyFilter() {
        listModel.clear();
        String selected = (String) categoryFilter.getSelectedItem();
        List<CaseRecord> all = CaseStorage.loadAll();
        if (selected == null || "全部".equals(selected)) {
            all.forEach(listModel::addElement);
        } else {
            all.stream().filter(r -> r.category.equals(selected)).forEach(listModel::addElement);
        }
    }

    private void showDetail(CaseRecord r) {
        if (r == null) { detailArea.setText(""); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("案件ID: ").append(r.id).append("\n");
        sb.append("分类: ").append(r.category).append("\n");
        sb.append("重罪度 R_final: ").append(String.format("%.4f", r.rFinal)).append("\n");
        sb.append("刑罚: ").append(r.penalty).append("\n");
        sb.append("时间: ").append(r.createdAt).append("\n\n");
        sb.append("--- 案情 ---\n").append(r.caseText).append("\n\n");
        sb.append("--- 罪名提取结果 ---\n").append(r.crimesJson).append("\n\n");
        sb.append("--- 干扰素 ---\n").append(r.factorsJson);
        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    private void deleteSelected() {
        CaseRecord selected = caseList.getSelectedValue();
        if (selected != null) {
            CaseStorage.delete(selected.id);
            refreshData();
            detailArea.setText("");
        }
    }

    // 圆角列表项渲染包装器
    private static class RoundedListCell extends JPanel {
        RoundedListCell(JLabel label, boolean isSelected) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(2, 2, 2, 2));
            add(label, BorderLayout.CENTER);
            if (isSelected) {
                setBackground(new Color(52, 119, 182));
                label.setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                label.setForeground(new Color(33, 37, 41));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}