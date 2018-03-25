package poid.view;

import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageSelectorPane extends JPanel implements ActionListener, MouseListener {
    private static final int imagePreviewSize = 100;
    private static final int imageBigPreviewSize = 256;
    private static final int imagePreviewPaneSizeH = 1000;
    private static final int imagePreviewPaneSizeV = 700;
    public static final String defaultImagesPath = "src/main/resources/images";

    private final ArrayList<JButton> imagePreviewButtons;
    private final Main main;

    private JPanel buttonPanel;
    @Getter
    private String imageDirectoryURL = defaultImagesPath;
    @Getter
    private BufferedImagePane chosenPanelTab;
    private JButton newPath;
    private JButton reload;
    private JScrollPane scrollPane;
    private JLabel imageTextName;

    ImageSelectorPane(Main main) {
        this.imagePreviewButtons = new ArrayList();
        this.main = main;
        createImageSelectorTab();
    }

    private void createImageSelectorTab() {
        createBigImagePreview();
        createChangePathButton();
        createPreviewImagesSelector();
    }

    private void createBigImagePreview() {
        chosenPanelTab = new BufferedImagePane(null, imageBigPreviewSize, imageBigPreviewSize);
        chosenPanelTab.setLocation(imagePreviewPaneSizeH + 20, 30);
        chosenPanelTab.addMouseListener(this);
        this.add(chosenPanelTab);
        imageTextName = new JLabel();
        imageTextName.setSize(300, 25);
        imageTextName.setLocation(imagePreviewPaneSizeH + 20, 5);
        this.add(imageTextName);
        reload = new JButton("Reload");
        reload.setSize(120, 30);
        reload.setLocation(imagePreviewPaneSizeH + 430, 5);
        reload.addActionListener(this);
        this.add(reload);
    }

    private void createPreviewImagesSelector() {
        int yLocationIcon;
        int xLocationIcon = yLocationIcon = 10;
        File directoryImage = new File(imageDirectoryURL);
        for (File fileEntry : directoryImage.listFiles()) {
            BufferedImage imageToChoice = null;
            try {
                imageToChoice = ImageIO.read(fileEntry);
            } catch (IOException e) {
                System.err.println("Image reading error");
            }
            double divider = (double) imageToChoice.getHeight() / (double) imagePreviewSize;
            double iconWidth = imageToChoice.getWidth() / divider;
            Image scaled = imageToChoice.getScaledInstance((int) iconWidth, imagePreviewSize, Image.SCALE_DEFAULT);

            buttonPanel = new JPanel();
            buttonPanel.setLayout(null);

            JButton buttonWithIcon = new JButton(fileEntry.getName());

            if (xLocationIcon + iconWidth > imagePreviewPaneSizeH) {
                yLocationIcon += imagePreviewSize + 30;
                xLocationIcon = 10;
            }

            buttonWithIcon.setSize((int) (iconWidth - 10), imagePreviewSize - 10);
            buttonWithIcon.setLocation(xLocationIcon, yLocationIcon);
            buttonWithIcon.setIcon(new ImageIcon(scaled));
            buttonWithIcon.addActionListener(this);

            xLocationIcon += iconWidth - 10;

            imagePreviewButtons.add(buttonWithIcon);
            this.add(buttonWithIcon);
        }

        for (JButton imagePreviewButton : imagePreviewButtons) {
            buttonPanel.add(imagePreviewButton);
            JLabel imageName = new JLabel(imagePreviewButton.getText());
            imageName.setToolTipText(imagePreviewButton.getText());
            imageName.setSize(imagePreviewButton.getWidth() - 5, 25);
            imageName.setLocation(imagePreviewButton.getX(), imagePreviewButton.getY() + imagePreviewButton.getHeight());
            buttonPanel.add(imageName);
        }

        buttonPanel.setPreferredSize(
                new Dimension(imagePreviewPaneSizeH, (int) imagePreviewButtons
                        .get(imagePreviewButtons.size() - 1)
                        .getLocation().getY() + imagePreviewButtons.get(imagePreviewButtons.size() - 1).getHeight() + 40));
        scrollPane = new JScrollPane(buttonPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setSize(imagePreviewPaneSizeH, imagePreviewPaneSizeV);
        scrollPane.setLocation(10, 25);
        this.add(scrollPane);
        imagePreviewButtons.get(0).doClick();
    }

    private void createChangePathButton() {
        newPath = new JButton("Change path");
        newPath.setSize(1000, 20);
        newPath.setLocation(10, 5);
        newPath.addActionListener(this);
        this.add(newPath);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (JButton imagePreviewButton : imagePreviewButtons) {
            if (e.getSource() == imagePreviewButton) {
                BufferedImage selectedImage = null;
                BufferedImage readImage = null;
                File imageFile = new File(imageDirectoryURL + "\\" + imagePreviewButton.getText());
                try {
                    readImage = ImageIO.read(imageFile);
                } catch (IOException ex) {
                    System.err.println("Image reading error" + ex);
                }
                selectedImage = readImage;
                if (selectedImage.getType() != BufferedImage.TYPE_BYTE_GRAY && selectedImage.getType() != BufferedImage.TYPE_BYTE_BINARY) {
                    //zmiana typu obrazu na RGB
                    selectedImage = new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                    selectedImage.getGraphics().drawImage(readImage, 0, 0, null);
                }

                chosenPanelTab.setBufferedImage(selectedImage);
                chosenPanelTab.setHeight(imageBigPreviewSize);
                chosenPanelTab.setWidth(imageBigPreviewSize);
                imageTextName.setText(imagePreviewButton.getText());
                if (selectedImage.getWidth() > imageBigPreviewSize && selectedImage.getHeight() > imageBigPreviewSize) {
                    double divider = (double) selectedImage.getHeight() / (double) imageBigPreviewSize;
                    chosenPanelTab.setHeight((int) (selectedImage.getHeight() / divider));
                    chosenPanelTab.setWidth((int) (selectedImage.getWidth() / divider));
                }
                chosenPanelTab.repaint();
                this.repaint();

                main.refreshTabs();
            }
        }

        // zmiana folderu obrazów
        if (e.getSource() == newPath) {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setCurrentDirectory(new File(imageDirectoryURL));
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = directoryChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = directoryChooser.getSelectedFile();
                imageDirectoryURL = selectedFile.getPath();
                reload();
            }
        }

        if (e.getSource() == reload) {
            reload();
        }
    }

    private void reload() {
        this.remove(scrollPane);
        imagePreviewButtons.clear();
        buttonPanel.removeAll();
        createPreviewImagesSelector();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == chosenPanelTab) {
            new ImageViewerFrame(chosenPanelTab.getBufferedImage());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
