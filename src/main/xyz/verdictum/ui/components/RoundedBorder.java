package verdictum.ui.components;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
    private final Color borderColor;
    private final Color backgroundColor;
    private final int radius;
    private final int thickness;

    public RoundedBorder(Color borderColor, int radius) {
        this(borderColor, null, radius, 1);
    }

    public RoundedBorder(Color borderColor, Color backgroundColor, int radius, int thickness) {
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.radius = radius;
        this.thickness = thickness;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
            int fillX = thickness;
            int fillY = thickness;
            int fillW = width - 2 * thickness;
            int fillH = height - 2 * thickness;
            g2.fillRoundRect(fillX, fillY, fillW, fillH, radius, radius);
        }

        if (borderColor != null && thickness > 0) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset, width - thickness, height - thickness, radius, radius);
        }

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness, thickness, thickness, thickness);
    }
}