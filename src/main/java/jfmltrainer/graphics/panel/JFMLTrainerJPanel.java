package jfmltrainer.graphics.panel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class JFMLTrainerJPanel<T> extends JPanel {

    protected T base;

    protected JFMLTrainerJPanel(T base) {
        this.base = base;
    }

    public void saveImage() {
        try {
            BufferedImage img = new Robot().createScreenCapture(this.bounds());
            this.paint(img.createGraphics());
            ImageIO.write(img, "png", new File("images/" + getImageName()));
        } catch (Exception e) {
            System.out.println("The image " + getImageName() + " could not be saved.");
        }
    }

    protected abstract String getImageName();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBase((Graphics2D) g);
    }

    protected abstract void paintBase(Graphics2D g);

    protected void drawPoint(Graphics2D g, int x, int y) {
        g.drawLine(x, y, x, y);
    }
}
