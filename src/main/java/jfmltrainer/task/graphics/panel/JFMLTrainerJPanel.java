package jfmltrainer.task.graphics.panel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;

public abstract class JFMLTrainerJPanel<T> extends JPanel {

    protected T base;
    protected Optional<T> newBase;

    protected JFMLTrainerJPanel(T base, Optional<T> newBase) {
        this.base = base;
        this.newBase = newBase;
    }

    public void saveImage(String instant) {
        try {
            BufferedImage img = new Robot().createScreenCapture(this.bounds());
            this.paint(img.createGraphics());
            String filename = "images/" + instant + "-" + getImageName() + ".png";
            ImageIO.write(img, "png", new File(filename));
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
