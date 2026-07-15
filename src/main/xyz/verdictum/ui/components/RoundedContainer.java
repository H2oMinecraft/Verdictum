package verdictum.ui.components;

import javax.swing.*;
import java.awt.*;

public class RoundedContainer extends JPanel {
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(222, 226, 230);
    private int radius = 10;
    private int borderThickness = 1;

    public RoundedContainer() {
        setOpaque(false); // 面板自身透明，由 paintComponent 绘制背景
    }

    public void setBackgroundColor(Color bg) { this.backgroundColor = bg; repaint(); }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; repaint(); }
    public void setRadius(int radius) { this.radius = radius; repaint(); }
    public void setBorderThickness(int borderThickness) { this.borderThickness = borderThickness; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆角背景
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // 绘制圆角边框
        if (borderThickness > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.drawRoundRect(borderThickness / 2, borderThickness / 2,
                    getWidth() - borderThickness, getHeight() - borderThickness, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * 便捷方法：将内容组件放入容器，仅设置布局，不修改透明度
     */
    public void wrap(JComponent content) {
        setLayout(new BorderLayout());
        add(content, BorderLayout.CENTER);
    }
}