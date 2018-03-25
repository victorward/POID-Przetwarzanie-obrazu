package poid.view;

import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import poid.utils.HistogramModification;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class HistogramPane extends JPanel implements ChangeListener, ActionListener, MouseListener {
    @Getter
    @Setter
    private BufferedImagePane chosenImage;
    @Setter
    @Getter
    private BufferedImagePane modifiedPane;
    private BufferedImage originalImage;
    private BufferedImage modifiedImage;

    private JButton reset;

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

    private JSlider gMinSlider;
    private JSlider gMaxSlider;
    private boolean modification = false;

    HistogramPane(BufferedImagePane chosenTab, boolean modification) {
        this.chosenImage = chosenTab;
        this.modification = modification;
        createTab();
    }

    private void createTab() {
        setImage();
        createModifiedHistogram();
        createOriginalHistogram();
        if (modification)
            reset();
    }

    private void setImage() {
        if (modification) {
            chosenImage.setLocation(10, 360);
            chosenImage.addMouseListener(this);
            reset = new JButton("Reset changes");
            reset.setLocation(10, 10);
            reset.setSize(260, 30);
            reset.addActionListener(this);
            this.add(reset);
        } else {
            chosenImage.setLocation(10, 10);
        }
        this.add(chosenImage);

        BufferedImage imageToConvert = chosenImage.getBufferedImage();
        originalImage = new BufferedImage(imageToConvert.getWidth(),
                imageToConvert.getHeight(), BufferedImage.TYPE_INT_RGB);
        originalImage.getGraphics().drawImage(imageToConvert, 0, 0, null);
    }

    private void createModifiedHistogram() {
        histogramModification = new HistogramModification();
        originalHistogram = histogramModification.createHistogramImage(originalImage);
        if (modification) {
            modifiedPane = (BufferedImagePane) chosenImage.clone();
            this.add(modifiedPane);
            modifiedPane.setLocation(10, 50);

            histogramModification.createModificationTable(originalImage);
            modifiedImage = histogramModification.changeImage(originalImage);
            modifiedPane.setBufferedImage(modifiedImage);
            modifiedPane.repaint();
        }
    }

    private void createOriginalHistogram() {
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
        redColorChartPanel.setLocation(300, 10);
        redColorChartPanel.setSize(500, 300);
        this.add(redColorChartPanel);

        greenColorChartPanel = new ChartPanel(greenColorChart);
        greenColorChartPanel.setLocation(800, 10);
        greenColorChartPanel.setSize(500, 300);
        this.add(greenColorChartPanel);

        blueColorChartPanel = new ChartPanel(blueColorChart);
        blueColorChartPanel.setLocation(1300, 10);
        blueColorChartPanel.setSize(500, 300);
        this.add(blueColorChartPanel);

        brighnessChartPanel = new ChartPanel(brighnessChart);
        brighnessChartPanel.setLocation(300, 310);
        brighnessChartPanel.setSize(500, 300);
        this.add(brighnessChartPanel);

        if (modification) {
            JLabel gMinLabel = new JLabel("G min");
            gMinLabel.setLocation(800, 330);
            gMinLabel.setSize(300, 20);
            this.add(gMinLabel);

            gMinSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
            gMinSlider.setMajorTickSpacing(51);
            gMinSlider.setMinorTickSpacing(10);
            gMinSlider.setPaintTicks(true);
            gMinSlider.setPaintLabels(true);
            gMinSlider.setLocation(800, 350);
            gMinSlider.setSize(500, 50);
            gMinSlider.addChangeListener(this);
            this.add(gMinSlider);

            JLabel gMaxLabel = new JLabel("G max");
            gMaxLabel.setLocation(800, 470);
            gMaxLabel.setSize(300, 20);
            this.add(gMaxLabel);

            gMaxSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
            gMaxSlider.setMajorTickSpacing(51);
            gMaxSlider.setMinorTickSpacing(10);
            gMaxSlider.setPaintTicks(true);
            gMaxSlider.setPaintLabels(true);
            gMaxSlider.setLocation(800, 490);
            gMaxSlider.setSize(500, 50);
            gMaxSlider.addChangeListener(this);
            this.add(gMaxSlider);
        }
    }

    public void updateTab() {
        originalImage = chosenImage.getBufferedImage();

        if (modification) {
            modifiedImage = chosenImage.getBufferedImage();
            originalHistogram = histogramModification.createHistogramImage(modifiedImage);

            histogramModification.createModificationTable(originalImage);
            modifiedImage = histogramModification.changeImage(originalImage);
            modifiedPane.setBufferedImage(modifiedImage);
            originalHistogram = histogramModification.createHistogramImage(modifiedImage);
            modifiedPane.repaint();
        } else {
            originalHistogram = histogramModification.createHistogramImage(originalImage);
        }
        redColorDataset.addSeries("Red", originalHistogram[0]);
        greenColorDataset.addSeries("Green", originalHistogram[1]);
        blueColorDataset.addSeries("Blue", originalHistogram[2]);
        brighnessDataset.addSeries("Brightness", originalHistogram[3]);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == gMinSlider) {
            histogramModification.setGMin(gMinSlider.getValue());
            histogramModification.setGMax(gMaxSlider.getValue());
            updateTab();
        } else if (e.getSource() == gMaxSlider) {
            histogramModification.setGMin(gMinSlider.getValue());
            histogramModification.setGMax(gMaxSlider.getValue());
            updateTab();
        }
    }

    private void reset() {
        int gMin = 255;
        int gMax = 0;
        gMinSlider.setValue(gMin);
        gMinSlider.repaint();
        histogramModification.setGMin(gMin);
        gMaxSlider.setValue(0);
        gMaxSlider.repaint();
        histogramModification.setGMax(gMax);
        updateTab();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == reset) {
            reset();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == modifiedPane) {
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
