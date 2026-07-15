package verdictum.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class FlatScrollBarUI extends BasicScrollBarUI {

    private static final Color THUMB_COLOR = new Color(190, 190, 200);
    private static final Color THUMB_HOVER_COLOR = new Color(170, 170, 185);
    private static final Color TRACK_COLOR = new Color(245, 245, 250);

    @Override
    protected void configureScrollBarColors() {
        thumbColor = THUMB_COLOR;
        thumbDarkShadowColor = THUMB_COLOR;
        thumbHighlightColor = THUMB_COLOR;
        thumbLightShadowColor = THUMB_COLOR;
        trackColor = TRACK_COLOR;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroSizeButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroSizeButton();
    }

    private JButton createZeroSizeButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = isThumbRollover() ? THUMB_HOVER_COLOR : THUMB_COLOR;
        g2.setColor(color);
        int w = thumbBounds.width;
        int h = thumbBounds.height;
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y, w - 4, h, 6, 6);
        } else {
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y + 2, w, h - 4, 6, 6);
        }
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(trackColor);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }
}