package verdictum.util;

import verdictum.model.ModelConfig;
import verdictum.ui.components.FlatButtonUI;
import verdictum.ui.components.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.util.List;

public class UIUtils {

    // ================== 按钮 ==================
    public static JButton createFlatButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text);
        Color textColor = isPrimary ? Color.WHITE : bgColor;
        button.setUI(new FlatButtonUI(bgColor, textColor));
        return button;
    }

    // ================== 下拉框 ==================
    public static JComboBox<String> createFlatComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        applyBasicFlatStyle(combo);
        combo.setRenderer(new SimpleFlatRenderer());
        return combo;
    }

    public static JComboBox<ModelConfig> createFlatModelComboBox(List<ModelConfig> models) {
        JComboBox<ModelConfig> combo = new JComboBox<>(models.toArray(new ModelConfig[0]));
        applyBasicFlatStyle(combo);
        combo.setRenderer(new ModelFlatRenderer());
        return combo;
    }

    private static void applyBasicFlatStyle(JComboBox<?> combo) {
        combo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(new Color(33, 37, 41));  // 显式设置文字颜色，确保可见
        combo.setOpaque(true);  // 确保背景可见
        combo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(222, 226, 230), 8),
                new EmptyBorder(2, 6, 2, 6)));
        // 只替换箭头按钮，其余保持默认
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("\u25BC");
                button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
                button.setForeground(new Color(108, 117, 125));
                button.setBackground(Color.WHITE);
                button.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return button;
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                ListCellRenderer<Object> renderer = (ListCellRenderer<Object>) listBox.getCellRenderer();
                if (renderer == null) {
                    super.paintCurrentValue(g, bounds, hasFocus);
                    return;
                }
                Object value = comboBox.getSelectedItem();
                if (value == null) {
                    super.paintCurrentValue(g, bounds, hasFocus);
                    return;
                }
                Component c = renderer.getListCellRendererComponent(
                        listBox, value, -1, false, false);
                c.setFont(comboBox.getFont());
                // 不覆盖渲染器设置的前景色和背景色，让渲染器完全控制样式
                currentValuePane.paintComponent(g, c, comboBox,
                        bounds.x, bounds.y, bounds.width, bounds.height, true);
            }
        });
    }

    // 字符串渲染器
    private static class SimpleFlatRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            label.setBorder(new EmptyBorder(2, 4, 2, 4));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(new Color(52, 119, 182));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(33, 37, 41));
            }
            return label;
        }
    }

    // ModelConfig 渲染器
    private static class ModelFlatRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            String display = (value instanceof ModelConfig) ? ((ModelConfig) value).getDisplayName() : value.toString();
            JLabel label = (JLabel) super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus);
            label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            label.setBorder(new EmptyBorder(2, 4, 2, 4));
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(new Color(52, 119, 182));
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(new Color(33, 37, 41));
            }
            return label;
        }
    }

    // ================== 复选框（彻底自绘） ==================
    public static JCheckBox createFlatCheckBox(String text) {
        JCheckBox check = new JCheckBox(text);
        check.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        check.setOpaque(false);
        check.setFocusPainted(false);
        check.setUI(new FlatCheckBoxUI());
        return check;
    }

    private static class FlatCheckBoxUI extends BasicCheckBoxUI {
        private static final Color BORDER_COLOR = new Color(180, 180, 190);
        private static final Color CHECK_COLOR = new Color(52, 119, 182);
        private static final Color BG_COLOR = Color.WHITE;

        @Override
        public void installUI(JComponent c) {
            super.installUI(c);
            c.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            ((AbstractButton) c).setIcon(null);
            ((AbstractButton) c).setIconTextGap(6);
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton button = (AbstractButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int boxSize = 14;
            int x = 0;
            int y = (c.getHeight() - boxSize) / 2;

            // 1. 绘制复选框背景
            g2.setColor(BG_COLOR);
            g2.fillRoundRect(x, y, boxSize, boxSize, 4, 4);

            // 2. 绘制边框
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x, y, boxSize, boxSize, 4, 4);

            // 3. 选中时绘制对勾
            if (button.isSelected()) {
                g2.setColor(CHECK_COLOR);
                g2.setStroke(new BasicStroke(2.0f));
                int[] xs = {x + 3, x + 6, x + 11};
                int[] ys = {y + boxSize / 2 + 1, y + boxSize - 3, y + 3};
                g2.drawPolyline(xs, ys, 3);
            }
            g2.dispose();

            // 4. 绘制文字（不依赖父类，自己画）
            g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(button.getFont());
            g2.setColor(button.getForeground());
            FontMetrics fm = g2.getFontMetrics();
            String text = button.getText();
            if (text != null && !text.isEmpty()) {
                int textX = x + boxSize + 6;
                int textY = y + (boxSize - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, textX, textY);
            }
            g2.dispose();

            // 注意：完全没有调用 super.paint，因此不会有任何默认图标
        }
    }
}