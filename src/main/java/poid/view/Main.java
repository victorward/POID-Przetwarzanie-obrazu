package poid.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Main extends JFrame implements ChangeListener, ActionListener {
    private JTabbedPane tabsPane;
    private ImageSelectorPane imageSelectorPane;
    private BaseOperationsPane baseOperationPane;
    private HistogramPane originalImageHistogram;
    private HistogramPane modifiedImageHistogram;

    private JPanel mainPanel;

    public Main() {
        init();
    }

    private void init() {
        setTitle("POID");
        setSize(1920, 800);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        getContentPane().add(mainPanel);
        addTabs();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void addTabs() {
        initTabs();
        tabsPane = new JTabbedPane();
        tabsPane.addTab(null, imageSelectorPane);
        tabsPane.addTab(null, baseOperationPane);
        tabsPane.addTab(null, originalImageHistogram);
        tabsPane.addTab(null, modifiedImageHistogram);

        JLabel imageSelectorPaneLabel = new JLabel("Image selector");
        JLabel baseOperationPaneLabel = new JLabel("Base operations");
        JLabel originalImageHistogramPaneLabel = new JLabel("Original image histogram");
        JLabel modifiedImageHistogramPaneLabel = new JLabel("Modification base on image histogram");

        tabsPane.setTabComponentAt(0, imageSelectorPaneLabel);
        tabsPane.setTabComponentAt(1, baseOperationPaneLabel);
        tabsPane.setTabComponentAt(2, originalImageHistogramPaneLabel);
        tabsPane.setTabComponentAt(3, modifiedImageHistogramPaneLabel);

        mainPanel.add(tabsPane, BorderLayout.CENTER);
    }

    private void initTabs() {
        imageSelectorPane = new ImageSelectorPane(this);
        imageSelectorPane.setLayout(null);
        BufferedImagePane imageSelectorPaneClone = (BufferedImagePane) imageSelectorPane.getChosenPanelTab().clone();

        baseOperationPane = new BaseOperationsPane(imageSelectorPaneClone, this);
        baseOperationPane.setLayout(null);
        baseOperationPane.setImageDirectoryUrl(imageSelectorPane.getImageDirectoryURL());

        originalImageHistogram = new HistogramPane((BufferedImagePane) imageSelectorPane.getChosenPanelTab().clone(), false);
        originalImageHistogram.setLayout(null);

        modifiedImageHistogram = new HistogramPane((BufferedImagePane) imageSelectorPane.getChosenPanelTab().clone(), true);
        modifiedImageHistogram.setLayout(null);

    }

    public void refreshTabs() {
        if (imageSelectorPane != null && baseOperationPane != null
                && originalImageHistogram != null && modifiedImageHistogram != null) {
            BufferedImagePane imageSelectorPane =
                    (BufferedImagePane) this.imageSelectorPane.getChosenPanelTab().clone();
            baseOperationPane.setImageDirectoryUrl(this.imageSelectorPane.getImageDirectoryURL());
            baseOperationPane.getChosenImage().setBufferedImage(imageSelectorPane.getBufferedImage());
            baseOperationPane.getModifiedPane().setBufferedImage(imageSelectorPane.getBufferedImage());

            BufferedImage originalImageForOriginalHistogram =
                    new BufferedImage(
                            imageSelectorPane.getBufferedImage().getWidth(),
                            imageSelectorPane.getBufferedImage().getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );
            originalImageForOriginalHistogram.getGraphics().drawImage(imageSelectorPane.getBufferedImage(), 0, 0, null);
            originalImageHistogram.getChosenImage().setBufferedImage(originalImageForOriginalHistogram);

            BufferedImage originalImageForModifiedHistogram = new BufferedImage(
                    imageSelectorPane.getBufferedImage().getWidth(),
                    imageSelectorPane.getBufferedImage().getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            originalImageForModifiedHistogram.getGraphics().drawImage(imageSelectorPane.getBufferedImage(), 0, 0, null);
            modifiedImageHistogram.getChosenImage().setBufferedImage(originalImageForModifiedHistogram);
            modifiedImageHistogram.getModifiedPane().setBufferedImage(originalImageForModifiedHistogram);

            if (imageSelectorPane.getBufferedImage().getType() != BufferedImage.TYPE_INT_RGB) {
                baseOperationPane.getNegative().setPixelTableLength(1);
                baseOperationPane.getBrightness().setPixelTableLength(1);
                baseOperationPane.getContrast().setPixelTableLength(1);
                baseOperationPane.getFilterArithmeticMean().setPixelTableLength(1);
                baseOperationPane.getFilterMedian().setPixelTableLength(1);
                baseOperationPane.getFilterDetailsExtraction().setPixelTableLength(1);
                baseOperationPane.getFilterUolisOperator().setPixelTableLength(1);
            } else {
                baseOperationPane.getNegative().setPixelTableLength(3);
                baseOperationPane.getBrightness().setPixelTableLength(3);
                baseOperationPane.getContrast().setPixelTableLength(3);
                baseOperationPane.getFilterArithmeticMean().setPixelTableLength(3);
                baseOperationPane.getFilterMedian().setPixelTableLength(3);
                baseOperationPane.getFilterDetailsExtraction().setPixelTableLength(3);
                baseOperationPane.getFilterUolisOperator().setPixelTableLength(3);
            }

            baseOperationPane.getChosenImage().repaint();
            baseOperationPane.getModifiedPane().repaint();
            baseOperationPane.resetHistogram();
            originalImageHistogram.getChosenImage().repaint();
            originalImageHistogram.updateTab();
            modifiedImageHistogram.getChosenImage().repaint();
            modifiedImageHistogram.updateTab();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }
}
