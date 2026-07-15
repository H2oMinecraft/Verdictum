package verdictum.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FlatButtonUI extends BasicButtonUI {
    private final Color normalBg;
    private final Color hoverBg;
    private final Color pressBg;
    private final Color textColor;

    public FlatButtonUI(Color bg, Color textColor) {
        this.normalBg = bg;
        this.textColor = textColor;
        this.hoverBg = brighten(bg, 0.1f);
        this.pressBg = darken(bg, 0.15f);
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JButton button = (JButton) c;
        button.setBackground(normalBg);               // 设置初始背景色
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressBg);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(button.getModel().isRollover() ? hoverBg : normalBg);
            }
        });
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(button.getBackground());
        int r = 8;
        g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), r, r);
        super.paint(g2, c);
        g2.dispose();
    }

    private static Color brighten(Color c, float factor) {
        int r = clamp((int) (c.getRed() + (255 - c.getRed()) * factor));
        int g = clamp((int) (c.getGreen() + (255 - c.getGreen()) * factor));
        int b = clamp((int) (c.getBlue() + (255 - c.getBlue()) * factor));
        return new Color(r, g, b);
    }

    private static Color darken(Color c, float factor) {
        int r = clamp((int) (c.getRed() * (1 - factor)));
        int g = clamp((int) (c.getGreen() * (1 - factor)));
        int b = clamp((int) (c.getBlue() * (1 - factor)));
        return new Color(r, g, b);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}