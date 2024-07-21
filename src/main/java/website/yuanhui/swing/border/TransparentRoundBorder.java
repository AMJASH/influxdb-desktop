package website.yuanhui.swing.border;

import javax.swing.border.AbstractBorder;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;

public class TransparentRoundBorder extends AbstractBorder {
    private final Color borderColor;
    private final int borderWidth;
    private final int alpha;

    public TransparentRoundBorder(Color borderColor, int borderWidth, int alpha) {
        this.borderColor = borderColor;
        this.borderWidth = borderWidth;
        this.alpha = alpha;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.draw(new RoundRectangle2D.Float(x, y, width - 1, height - 1, 10, 10));
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, borderWidth, 0, borderWidth);
    }
}