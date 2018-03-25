package poid.view;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import poid.utils.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BaseOperationsPane extends JPanel implements ActionListener, ChangeListener, MouseListener {
    private final Main main;

    @Getter
    @Setter
    private BufferedImagePane chosenImage;
    @Getter
    @Setter
    private BufferedImagePane modifiedPane;

    private BufferedImage originalImage;
    private BufferedImage modifiedImage;

    private JSlider brightnessSlider;
    private JSlider contrastSlider;
    private JButton negativeButton;
    private JComboBox maskDimensionList;
    private JButton loadOriginal;
    private JButton filterArithmeticMeanButton;
    private JButton filterMedianButton;
    private JButton filterDetailsExtractionButton;
    private JButton saveImage;
    private ButtonGroup matrixGroup;
    private JButton filterUolisOperatorButton;
    private PTextArea customArea;
    @Getter
    @Setter
    private String imageDirectoryUrl;

    @Getter
    private Brightness brightness;
    @Getter
    private Contrast contrast;
    @Getter
    private Negative negative;
    @Getter
    private FilterArithmeticMean filterArithmeticMean;
    @Getter
    private FilterMedian filterMedian;
    @Getter
    private FilterDetailsExtraction filterDetailsExtraction;
    @Getter
    private FilterUolisOperator filterUolisOperator;

    private MeanSquaredError meanSquaredError;
    private JPanel meanSquaredErrorPanel;
    private JPanel meanSquaredErrorCustomImagePanel;
    private List<JLabel> channels;
    private List<JLabel> customImageChannels;
    private JLabel mseErrorLabel;
    private JLabel mseErrorCustomImageLabel;
    private JButton meanSquaredErrorButton;
    private JCheckBox normalizationCheckbox;

    private HistogramModification histogramModification;
    private double[][][] originalHistogram;
    // Red
    private DefaultXYDataset redColorDataset;
    private XYPlot redColorPlot;
    private JFreeChart redColorChart;
    private ChartPanel redColorChartPanel;
    // Green
    private DefaultXYDataset greenColorDataset;
    private XYPlot greenColorPlot;
    private JFreeChart greenColorChart;
    private ChartPanel greenColorChartPanel;
    // Blue
    private DefaultXYDataset blueColorDataset;
    private XYPlot blueColorPlot;
    private JFreeChart blueColorChart;
    private ChartPanel blueColorChartPanel;
    // Brightness
    private DefaultXYDataset brighnessDataset;
    private XYPlot brighnessPlot;
    private JFreeChart brighnessChart;
    private ChartPanel brighnessChartPanel;

    BaseOperationsPane(BufferedImagePane chosenPaneTab, Main main) {
        this.chosenImage = chosenPaneTab;
        this.main = main;
        createTab();
    }

    private void createTab() {
        addImagesPreview();
        initUtils();
        initElements();
        createHistogram();
    }

    private void initUtils() {
        brightness = new Brightness();
        contrast = new Contrast();
        negative = new Negative();
        filterArithmeticMean = new FilterArithmeticMean();
        filterMedian = new FilterMedian();
        filterDetailsExtraction = new FilterDetailsExtraction();
        filterUolisOperator = new FilterUolisOperator();
        histogramModification = new HistogramModification();
        meanSquaredError = new MeanSquaredError();
    }

    private void addImagesPreview() {
        chosenImage.setLocation(10, 360);
        this.add(chosenImage);
        modifiedPane = (BufferedImagePane) chosenImage.clone();
        this.add(modifiedPane);
        modifiedPane.setLocation(10, 50);
        modifiedPane.addMouseListener(this);
        chosenImage.addMouseListener(this);

        BufferedImage imageToConvert = chosenImage.getBufferedImage();
        originalImage = new BufferedImage(imageToConvert.getWidth(),
                imageToConvert.getHeight(), BufferedImage.TYPE_INT_RGB);
        originalImage.getGraphics().drawImage(imageToConvert, 0, 0, null);
    }

    private void initElements() {
        channels = new ArrayList();
        customImageChannels = new ArrayList<>();
        loadBaseOperations();
        loadLinearFilter();
        loadConvolutionLinearFilter();
        loadUnlinearFilter();
    }

    private void loadUnlinearFilter() {
        filterUolisOperatorButton = new JButton("Execute Uolis operator");
        filterUolisOperatorButton.setLocation(410, 150);
        filterUolisOperatorButton.setSize(150, 30);
        filterUolisOperatorButton.addActionListener(this);
        add(filterUolisOperatorButton);
    }

    private void loadLinearFilter() {
        JPanel jPanel = new JPanel(new FlowLayout());
        jPanel.setLocation(270, 190);
        jPanel.setSize(200, 40);
        JLabel textMaskDimension = new JLabel("Mask size");
        textMaskDimension.setSize(100, 20);
        jPanel.add(textMaskDimension);

        String[] maskDimensionStrings = {"3x3", "5x5", "7x7", "9x9"};
        maskDimensionList = new JComboBox(maskDimensionStrings);
        maskDimensionList.setSelectedIndex(0);
        maskDimensionList.setSize(50, 30);
        maskDimensionList.addActionListener(this);
        jPanel.add(maskDimensionList);

        JLabel pixelsName = new JLabel("pixels");
        textMaskDimension.setSize(50, 20);
        jPanel.add(pixelsName);
        this.add(jPanel);

        filterArithmeticMeanButton = new JButton("Filter with arithmetic mean");
        filterArithmeticMeanButton.setLocation(570, 150);
        filterArithmeticMeanButton.setSize(170, 30);
        filterArithmeticMeanButton.addActionListener(this);
        add(filterArithmeticMeanButton);

        filterMedianButton = new JButton("Median filter");
        filterMedianButton.setLocation(750, 150);
        filterMedianButton.setSize(100, 30);
        filterMedianButton.addActionListener(this);
        add(filterMedianButton);
    }

    private void loadConvolutionLinearFilter() {
        JLabel detailsExtractionText = new JLabel("Filter base on convolution. Background details extraction (S4)");
        detailsExtractionText.setLocation(860, 20);
        detailsExtractionText.setSize(400, 30);
        add(detailsExtractionText);

        ParametersMatrixPane south = new ParametersMatrixPane(filterDetailsExtraction, 0);
        south.setLocation(867, 60);
        add(south);

        ParametersMatrixPane southWest = new ParametersMatrixPane(filterDetailsExtraction, 1);
        southWest.setLocation(987, 60);
        add(southWest);

        ParametersMatrixPane west = new ParametersMatrixPane(filterDetailsExtraction, 2);
        west.setLocation(1107, 60);
        add(west);

        ParametersMatrixPane northWest = new ParametersMatrixPane(filterDetailsExtraction, 3);
        northWest.setLocation(1227, 60);
        add(northWest);

        normalizationCheckbox = new JCheckBox("Normalization");
        normalizationCheckbox.setLocation(1180, 150);
        normalizationCheckbox.setSize(100, 30);
        normalizationCheckbox.addActionListener(this);
        this.add(normalizationCheckbox);

        JRadioButton southRadio = new JRadioButton();
        southRadio.setLocation(850, 75);
        southRadio.setSize(15, 15);
        southRadio.setSelected(true);
        add(southRadio);
        southRadio.addActionListener(this);
        southRadio.setActionCommand("0");

        JRadioButton southWestRadio = new JRadioButton();
        southWestRadio.setLocation(970, 75);
        southWestRadio.setSize(15, 15);
        southWestRadio.setSelected(true);
        add(southWestRadio);
        southWestRadio.addActionListener(this);
        southWestRadio.setActionCommand("1");

        JRadioButton westRadio = new JRadioButton();
        westRadio.setLocation(1090, 75);
        westRadio.setSize(15, 15);
        add(westRadio);
        westRadio.addActionListener(this);
        westRadio.setActionCommand("2");

        JRadioButton northWestRadio = new JRadioButton();
        northWestRadio.setLocation(1210, 75);
        northWestRadio.setSize(15, 15);
        add(northWestRadio);
        northWestRadio.addActionListener(this);
        northWestRadio.setActionCommand("3");

        JRadioButton customRadio = new JRadioButton();
        customRadio.setLocation(865, 155);
        customRadio.setSize(15, 15);
        add(customRadio);
        customRadio.addActionListener(this);
        customRadio.setActionCommand("4");

        customArea = new PTextArea("Custom mask");
        customArea.setSize(100, 80);
        customArea.setLocation(880, 120);
        add(customArea);

        matrixGroup = new ButtonGroup();
        matrixGroup.add(southRadio);
        matrixGroup.add(southWestRadio);
        matrixGroup.add(westRadio);
        matrixGroup.add(northWestRadio);
        matrixGroup.add(customRadio);

        filterDetailsExtractionButton = new JButton("Transform image");
        filterDetailsExtractionButton.setLocation(1000, 150);
        filterDetailsExtractionButton.setSize(150, 30);
        filterDetailsExtractionButton.addActionListener(this);
        add(filterDetailsExtractionButton);
    }

    private void loadBaseOperations() {
        JLabel brightnessLabel = new JLabel("Brightness");
        brightnessLabel.setLocation(300, 20);
        brightnessLabel.setSize(100, 20);
        this.add(brightnessLabel);

        brightnessSlider = new JSlider(JSlider.HORIZONTAL, -255, 255, 0);
        brightnessSlider.setMajorTickSpacing(25);
        brightnessSlider.setMinorTickSpacing(5);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.setLocation(360, 15);
        brightnessSlider.setSize(500, 50);
        brightnessSlider.addChangeListener(this);
        this.add(brightnessSlider);
        /* ^^^ Brightness ^^^ */
        JLabel contrastLabel = new JLabel("Contrast");
        contrastLabel.setLocation(300, 100);
        contrastLabel.setSize(300, 20);
        this.add(contrastLabel);

        contrastSlider = new JSlider(JSlider.HORIZONTAL, 0, 1500, 100);

        Hashtable<Integer, JLabel> contrastValues = new Hashtable<>();
        contrastValues.put(0, new JLabel("0"));
        for (int i = 0; i < 1501; i = i + 100) {
            contrastValues.put(i, new JLabel(Integer.toString(i / 100)));
        }
        contrastSlider.setLabelTable(contrastValues);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        contrastSlider.setLocation(360, 90);
        contrastSlider.setSize(500, 50);
        contrastSlider.addChangeListener(this);
        this.add(contrastSlider);
        /* ^^^ Contrast ^^^ */
        negativeButton = new JButton("Negative");
        negativeButton.setLocation(300, 150);
        negativeButton.setSize(100, 30);
        negativeButton.addActionListener(this);
        this.add(negativeButton);
        /* ^^^ Negative ^^^ */
        loadOriginal = new JButton("Reset changes");
        loadOriginal.setLocation(10, 10);
        loadOriginal.setSize(260, 30);
        loadOriginal.addActionListener(this);
        this.add(loadOriginal);
        /* ^^^ Load original ^^^ */
        meanSquaredErrorPanel = new JPanel();
        meanSquaredErrorPanel.setLayout(null);
        meanSquaredErrorPanel.setSize(250, 90);
        meanSquaredErrorPanel.setLocation(300, 220);
        createChanelLabels();
        /* ^^^ ^^^ */
        meanSquaredErrorCustomImagePanel = new JPanel();
        meanSquaredErrorCustomImagePanel.setLayout(null);
        meanSquaredErrorCustomImagePanel.setSize(250, 90);
        meanSquaredErrorCustomImagePanel.setLocation(700, 220);
        createCustomChannelLabels();

        meanSquaredErrorButton = new JButton("Count MSE with custom image");
        meanSquaredErrorButton.setLocation(640, 190);
        meanSquaredErrorButton.setSize(200, 30);
        meanSquaredErrorButton.addActionListener(this);
        this.add(meanSquaredErrorButton);
        /* ^^^ ^^^ */
        saveImage = new JButton("Save modified image");
        saveImage.setLocation(10, 650);
        saveImage.setSize(260, 30);
        saveImage.addActionListener(this);
        this.add(saveImage);
    }

    private void resetSliders() {
        main.refreshTabs();
        brightnessSlider.setValue(0);
        brightnessSlider.repaint();
        contrastSlider.setValue(100);
        contrastSlider.repaint();
        resetHistogram();
        resetErrors();
        modifiedImage = null;
    }

    private void resetErrors() {
        for (int i = 0; i < channels.size(); i++) {
            channels.get(i).setText("MSE for channel " + i + " : ");
        }
        mseErrorLabel.setText("MSE : ");
        for (int i = 0; i < customImageChannels.size(); i++) {
            customImageChannels.get(i).setText("MSE for channel " + i + " : ");
        }
        mseErrorCustomImageLabel.setText("II. MSE : ");

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == negativeButton) {
            resetSliders();
            modifiedImage = chosenImage.getBufferedImage();
            modifiedImage = negative.changeImage(modifiedImage);
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
            updateHistogram(modifiedImage);
        } else if (e.getSource() == loadOriginal) {
            resetSliders();
        } else if (e.getSource() == filterArithmeticMeanButton) {
            resetSliders();
            originalImage = chosenImage.getBufferedImage();
            modifiedImage = modifiedPane.getBufferedImage();
            modifiedImage = filterArithmeticMean.filter(originalImage, "ArithmeticMean");
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
            updateHistogram(modifiedImage);
        } else if (e.getSource() == filterMedianButton) {
            resetSliders();
            originalImage = chosenImage.getBufferedImage();
            modifiedImage = modifiedPane.getBufferedImage();
            modifiedImage = filterMedian.filter(originalImage, "Median");
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
            updateHistogram(modifiedImage);
        } else if (e.getSource() == maskDimensionList) {
            filterArithmeticMean.setMaskDimension(
                    Integer.parseInt(((String) maskDimensionList.getSelectedItem()).substring(0, 1)));
            filterMedian.setMaskDimension(
                    Integer.parseInt(((String) maskDimensionList.getSelectedItem()).substring(0, 1)));
            filterUolisOperator.setMaskDimension(
                    Integer.parseInt(((String) maskDimensionList.getSelectedItem()).substring(0, 1)));
            filterDetailsExtraction.setMaskDimension(
                    Integer.parseInt(((String) maskDimensionList.getSelectedItem()).substring(0, 1)));
        } else if (e.getSource() == filterDetailsExtractionButton) {
            resetSliders();
            originalImage = chosenImage.getBufferedImage();
            modifiedImage = modifiedPane.getBufferedImage();
            if (Integer.parseInt(matrixGroup.getSelection().getActionCommand()) == 4) {
                Integer[][] mappedCustomInput = createMaskFromCustomInput();
                if (mappedCustomInput == null) {
                    customArea.setText(customArea.getText() + "\nWrong input");
                    return;
                } else {
                    filterDetailsExtraction.getMasks()[4] = mappedCustomInput;
                }
            }
            filterDetailsExtraction.setSelectedMask(Integer.parseInt(matrixGroup.getSelection().getActionCommand()));
            if (normalizationCheckbox.isSelected())
                modifiedImage = filterDetailsExtraction.filterWithNormalization(originalImage);
            else
                modifiedImage = filterDetailsExtraction.filter(originalImage, "Details");
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
            updateHistogram(modifiedImage);
        } else if (e.getSource() == filterUolisOperatorButton) {
            resetSliders();
            originalImage = chosenImage.getBufferedImage();
            modifiedImage = modifiedPane.getBufferedImage();
            modifiedImage = filterUolisOperator.filterWithNormalization(originalImage);
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
            updateHistogram(modifiedImage);
        } else if (e.getSource() == saveImage) {
            saveModifiedImage();
        } else if (e.getSource() == meanSquaredErrorButton) {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setCurrentDirectory(new File(imageDirectoryUrl));
            int returnValue = directoryChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = directoryChooser.getSelectedFile();
                String originalImageUrl = selectedFile.getPath();
                double[] error = meanSquaredError.countError(originalImageUrl, modifiedPane.getBufferedImage());
                int errorAll = 0;
                for (int i = 0; i < customImageChannels.size(); i++) {
                    customImageChannels.get(i).setText("MSE for channel " + i + " : " + error[i]);
                    errorAll += error[i];
                }
                errorAll = (int) Math.floor(errorAll / channels.size());
                mseErrorCustomImageLabel.setText("II. MSE : " + errorAll);

            }
        }
        countMSE();
    }

    private void saveModifiedImage() {
        File save = new File(ImageSelectorPane.defaultImagesPath + "/saved_tmp_image.bmp");
        try {
            ImageIO.write(modifiedImage, "bmp", save);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void countMSE() {
        double[] error = meanSquaredError.countError(chosenImage.getBufferedImage(), modifiedPane.getBufferedImage());
        int errorAll = 0;
        for (int i = 0; i < channels.size(); i++) {
            channels.get(i).setText("MSE for channel " + i + " : " + error[i]);
            errorAll += error[i];
        }
        errorAll = (int) Math.floor(errorAll / channels.size());
        mseErrorLabel.setText("MSE : " + errorAll);
    }

    private Integer[][] createMaskFromCustomInput() {
        String[] spitedText = customArea
                .getText()
                .replaceAll("[^-?0-9]+", " ")
                .trim()
                .split(" ");
        Integer maskDimension = Integer.parseInt(((String) maskDimensionList.getSelectedItem()).substring(0, 1));
        Integer[][] splitInt = new Integer[maskDimension][maskDimension];
        int count = 0;
        if (spitedText.length != maskDimension * maskDimension) return null;

        for (int i = 0; i < maskDimension; i++) {
            for (int j = 0; j < maskDimension; j++) {
                if (count == spitedText.length) break;
                try {
                    splitInt[i][j] = Integer.parseInt(spitedText[count]);
                } catch (Exception ex) {
                    return null;
                }
                count++;
            }
        }

        return splitInt;
    }

    private void createChanelLabels() {
        channels.clear();
        meanSquaredErrorPanel.removeAll();
        mseErrorLabel = new JLabel("MSE : ");
        mseErrorLabel.setLocation(300, 220);
        mseErrorLabel.setSize(200, 20);
        this.add(mseErrorLabel);

        createChannels(channels);
        for (JLabel channel : channels) {
            meanSquaredErrorPanel.add(channel);
        }
        this.add(meanSquaredErrorPanel);
    }

    private void createCustomChannelLabels() {
        customImageChannels.clear();
        meanSquaredErrorCustomImagePanel.removeAll();
        mseErrorCustomImageLabel = new JLabel("II. MSE : ");
        mseErrorCustomImageLabel.setLocation(700, 220);
        mseErrorCustomImageLabel.setSize(200, 20);
        this.add(mseErrorCustomImageLabel);
        createChannels(customImageChannels);
        for (JLabel channel : customImageChannels) {
            meanSquaredErrorCustomImagePanel.add(channel);
        }
        this.add(meanSquaredErrorCustomImagePanel);
    }

    private void createChannels(List<JLabel> channels) {
        for (int i = 0; i < meanSquaredError.getPixelTableLength(); i++) {
            JLabel channel = new JLabel("MSE for channel " + i + ":");
            channel.setLocation(0, i * 20 + 20);
            channel.setSize(300, 20);
            channels.add(channel);
        }
    }


    private void createHistogram() {
        originalHistogram = histogramModification.createHistogramImage(originalImage);
        redColorDataset = new DefaultXYDataset();
        greenColorDataset = new DefaultXYDataset();
        blueColorDataset = new DefaultXYDataset();
        brighnessDataset = new DefaultXYDataset();

        redColorDataset.addSeries("Red", originalHistogram[0]);
        greenColorDataset.addSeries("Green", originalHistogram[1]);
        blueColorDataset.addSeries("Blue", originalHistogram[2]);
        brighnessDataset.addSeries("Brightness", originalHistogram[3]);
        redColorChart = ChartFactory.createXYLineChart("Red channel", null, null,
                redColorDataset, PlotOrientation.VERTICAL, false, false, false);
        greenColorChart = ChartFactory.createXYLineChart("Green channel", null, null,
                greenColorDataset, PlotOrientation.VERTICAL, false, false, false);
        blueColorChart = ChartFactory.createXYLineChart("Blue channel", null, null,
                blueColorDataset, PlotOrientation.VERTICAL, false, false, false);
        brighnessChart = ChartFactory.createXYLineChart("Brightness", null, null,
                brighnessDataset, PlotOrientation.VERTICAL, false, false, false);

        redColorPlot = (XYPlot) redColorChart.getPlot();
        greenColorPlot = (XYPlot) greenColorChart.getPlot();
        blueColorPlot = (XYPlot) blueColorChart.getPlot();
        brighnessPlot = (XYPlot) brighnessChart.getPlot();
        redColorPlot.getRenderer().setSeriesPaint(0, Color.RED);
        greenColorPlot.getRenderer().setSeriesPaint(0, Color.GREEN);
        blueColorPlot.getRenderer().setSeriesPaint(0, Color.BLUE);
        brighnessPlot.getRenderer().setSeriesPaint(0, Color.YELLOW);

        redColorChartPanel = new ChartPanel(redColorChart);
        redColorChartPanel.setLocation(300, 350);
        redColorChartPanel.setSize(500, 300);
        this.add(redColorChartPanel);

        greenColorChartPanel = new ChartPanel(greenColorChart);
        greenColorChartPanel.setLocation(800, 350);
        greenColorChartPanel.setSize(500, 300);
        this.add(greenColorChartPanel);

        blueColorChartPanel = new ChartPanel(blueColorChart);
        blueColorChartPanel.setLocation(1300, 350);
        blueColorChartPanel.setSize(500, 300);
        this.add(blueColorChartPanel);

        brighnessChartPanel = new ChartPanel(brighnessChart);
        brighnessChartPanel.setLocation(1330, 50);
        brighnessChartPanel.setSize(500, 300);
        this.add(brighnessChartPanel);
    }

    private void updateHistogram() {
        if (modifiedImage != null)
            originalImage = modifiedImage;
        else
            originalImage = chosenImage.getBufferedImage();

        originalHistogram = histogramModification.createHistogramImage(originalImage);

        redColorDataset.addSeries("Red", originalHistogram[0]);
        greenColorDataset.addSeries("Green", originalHistogram[1]);
        blueColorDataset.addSeries("Blue", originalHistogram[2]);
        brighnessDataset.addSeries("Brightness", originalHistogram[3]);
    }

    private void updateHistogram(BufferedImage modifiedImage) {
        originalHistogram = histogramModification.createHistogramImage(modifiedImage);
        redColorDataset.addSeries("Red", originalHistogram[0]);
        greenColorDataset.addSeries("Green", originalHistogram[1]);
        blueColorDataset.addSeries("Blue", originalHistogram[2]);
        brighnessDataset.addSeries("Brightness", originalHistogram[3]);
    }

    void resetHistogram() {
        originalImage = chosenImage.getBufferedImage();
        originalHistogram = histogramModification.createHistogramImage(originalImage);

        redColorDataset.addSeries("Red", originalHistogram[0]);
        greenColorDataset.addSeries("Green", originalHistogram[1]);
        blueColorDataset.addSeries("Blue", originalHistogram[2]);
        brighnessDataset.addSeries("Brightness", originalHistogram[3]);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        modifiedImage = chosenImage.getBufferedImage();
        if (e.getSource() == brightnessSlider) {
            contrastSlider.setValue(100);
            contrastSlider.repaint();
            brightness.setOffset(brightnessSlider.getValue());
            modifiedImage = brightness.changeImage(modifiedImage);
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
        } else if (e.getSource() == contrastSlider) {
            brightnessSlider.setValue(0);
            brightnessSlider.repaint();
            contrast.setRatios(contrastSlider.getValue());
            modifiedImage = contrast.changeImage(modifiedImage);
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
        }
        countMSE();
        updateHistogram();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == modifiedPane) {
            if (modifiedImage == null)
                new ImageViewerFrame(chosenImage.getBufferedImage());
            else
                new ImageViewerFrame(modifiedImage);
        } else if (e.getSource() == chosenImage) {
            new ImageViewerFrame(chosenImage.getBufferedImage());
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
