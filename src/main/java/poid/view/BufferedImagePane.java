package poid.view;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class BufferedImagePane extends JPanel implements Cloneable {
    @Getter
    @Setter
    private BufferedImage bufferedImage;
    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;

    BufferedImagePane(BufferedImage bufferedImage, int width, int height) {
        this.bufferedImage = bufferedImage;
        this.width = width;
        this.height = height;
//        this.addMouseListener(this);
        Dimension dimension = new Dimension(width, height);
        setSize(dimension);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.drawImage(bufferedImage, 0, 0, width, height, this);
    }

    @Override
    protected Object clone() {
        BufferedImagePane copy = null;
        try {
            copy = (BufferedImagePane) super.clone();
        } catch (CloneNotSupportedException e) {
            System.err.println("Cloning error" + e);
        }
        return copy;
    }

//    @Override
//    public void mouseClicked(MouseEvent e) {
//         new ImageViewerFrame(getBufferedImage());
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//
//    }
}
