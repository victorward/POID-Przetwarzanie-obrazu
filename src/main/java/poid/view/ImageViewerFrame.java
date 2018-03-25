package poid.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

class ImageViewerFrame extends JFrame {
    ImageViewerFrame(BufferedImage bufferedImage) {
        setSize(500, 500);
        setLocation(700, 200);
        setVisible(true);
        JLabel imageCanvas = new JLabel(new ImageIcon(bufferedImage));
        JScrollPane imageScroll = new JScrollPane(imageCanvas);
        imageScroll.setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
        getContentPane().add(imageScroll);
    }
}
