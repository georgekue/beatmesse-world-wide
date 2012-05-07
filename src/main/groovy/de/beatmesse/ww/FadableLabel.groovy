package de.beatmesse.ww

import javax.swing.*
import java.awt.*

public class FadeableLabel extends JLabel {
    
    private float intensity = 1.0f
    
    public FadeableLabel() {
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity
        repaint()
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g)
        Graphics2D g2 = (Graphics2D) g
        final Composite oldComposite = g2.getComposite()
        g2.setComposite(AlphaComposite.SrcOver)
        final Color c = getBackground()
        final Color color = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (255 * (1.0f - intensity)))
        g2.setColor(color)
        g2.fillRect(0, 0, getWidth(), getHeight())
        g2.setComposite(oldComposite)
    }
    
}