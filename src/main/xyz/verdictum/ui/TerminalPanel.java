package verdictum.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TerminalPanel extends JPanel {
    private final List<String> lines = new ArrayList<>();               // 已完全显示的文本行
    private final List<String> incomingLines = new ArrayList<>();       // 正在插入的新行（可能有多行）
    private float scrollOffset = 0f;
    private float animStartScrollOffset;
    private float animTargetScrollOffset;
    private boolean animating = false;
    private Timer animationTimer;
    private long animationStart;
    private static final long ANIM_DURATION = 500;
    private boolean cursorVisible = true;
    private Timer cursorTimer;
    private Font terminalFont = new Font("JetBrains Mono", Font.PLAIN, 14);
    private final Color textColor = new Color(0, 255, 0);
    private final Color backgroundColor = Color.BLACK;

    public TerminalPanel() {
        setBackground(backgroundColor);
        cursorTimer = new Timer(500, e -> {
            cursorVisible = !cursorVisible;
            repaint();
        });
        cursorTimer.start();
    }

    public void setTerminalFont(Font font) {
        this.terminalFont = font;
    }

    /**
     * 追加文本。支持多行，所有新行会作为一个整体执行滚入动画。
     */
    public void append(String text) {
        if (text == null || text.isEmpty()) return;
        String[] newLines = text.split("\\n", -1);
        // 过滤掉开头的全空行（避免初始空白）
        int startIdx = 0;
        while (startIdx < newLines.length && newLines[startIdx].isEmpty() && lines.isEmpty()) {
            startIdx++;
        }
        boolean hasContent = false;
        for (int i = startIdx; i < newLines.length; i++) {
            incomingLines.add(newLines[i]);
            hasContent = true;
        }
        if (hasContent) {
            startScrollAnimation();
        }
    }

    private void startScrollAnimation() {
        if (animating) {
            finishAnimation(); // 强制完成上一次动画，把之前的新行合并到 lines
        }
        animStartScrollOffset = scrollOffset;
        int lineHeight = getLineHeight();
        animTargetScrollOffset = animStartScrollOffset + incomingLines.size() * lineHeight;
        animating = true;
        animationStart = System.currentTimeMillis();
        if (animationTimer == null) {
            animationTimer = new Timer(16, e -> updateAnimation());
            animationTimer.start();
        } else {
            animationTimer.restart();
        }
    }

    private int getLineHeight() {
        Graphics2D g2 = (Graphics2D) getGraphics();
        if (g2 != null) {
            FontMetrics fm = g2.getFontMetrics(terminalFont);
            g2.dispose();
            return fm.getHeight() + 2;
        }
        return 18;
    }

    private void updateAnimation() {
        long elapsed = System.currentTimeMillis() - animationStart;
        float progress = Math.min(1.0f, elapsed / (float) ANIM_DURATION);
        float eased = 1.0f - (1.0f - progress) * (1.0f - progress);
        scrollOffset = animStartScrollOffset + (animTargetScrollOffset - animStartScrollOffset) * eased;
        if (progress >= 1.0f) {
            finishAnimation();
        } else {
            repaint();
        }
    }

    private void finishAnimation() {
        // 将 incomingLines 全部移入 lines
        lines.addAll(incomingLines);
        incomingLines.clear();
        scrollOffset = animTargetScrollOffset;
        animating = false;
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(terminalFont);
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight() + 2;
        int panelHeight = getHeight();
        int bottomMargin = 10;

        // 总行数 = 已固定行 + 正在滑入的行
        int totalLines = lines.size() + incomingLines.size();
        float viewTop = scrollOffset;

        // 绘制已固定行
        for (int i = 0; i < lines.size(); i++) {
            int indexFromBottom = totalLines - 1 - i;
            float y = panelHeight - bottomMargin - (indexFromBottom + 1) * lineHeight - viewTop;
            if (y + lineHeight < 0 || y > panelHeight) continue;
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(textColor);
            g2.drawString(lines.get(i), 10, y);
        }

        // 绘制正在滑入的行（可能有多个），整体透明度根据动画进度变化
        if (!incomingLines.isEmpty()) {
            float alpha = 1.0f;
            if (animating) {
                long elapsed = System.currentTimeMillis() - animationStart;
                float progress = Math.min(1.0f, elapsed / (float) ANIM_DURATION);
                alpha = progress; // 0 → 1
            }
            for (int i = 0; i < incomingLines.size(); i++) {
                int indexFromBottom = incomingLines.size() - 1 - i;
                float y = panelHeight - bottomMargin - (indexFromBottom + 1) * lineHeight - viewTop;
                if (y + lineHeight < 0 || y > panelHeight) continue;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setColor(textColor);
                g2.drawString(incomingLines.get(i), 10, y);
            }
        }

        // 光标
        if (cursorVisible && totalLines > 0) {
            String lastText;
            if (!incomingLines.isEmpty()) {
                lastText = incomingLines.get(incomingLines.size() - 1);
            } else {
                lastText = lines.get(lines.size() - 1);
            }
            int lastIndexFromBottom = 0;
            float lastY = panelHeight - bottomMargin - (lastIndexFromBottom + 1) * lineHeight - viewTop;
            int cursorX = 10 + fm.stringWidth(lastText) + 2;
            int cursorY = (int) lastY - fm.getAscent();
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setColor(textColor);
            g2.fillRect(cursorX, cursorY, 8, lineHeight);
        }

        g2.dispose();
    }

    public void clear() {
        lines.clear();
        incomingLines.clear();
        scrollOffset = 0;
        animating = false;
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        repaint();
    }

    public void stopCursor() {
        cursorVisible = false;
        repaint();
    }

    public void startCursor() {
        cursorVisible = true;
        repaint();
    }
}