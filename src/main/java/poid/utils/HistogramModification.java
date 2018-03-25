package poid.utils;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;

public class HistogramModification extends Process {
    @Getter @Setter
    private double gMin = 0;
    @Getter @Setter
    private double gMax = 255;
    private double[][][] originalHistogram;
    private double[][] sumHistogram;
    private double[][] modificationTable;

    @Override
    protected void changePixel(double[] pixel) {
        for (int i = 0; i < 3; i++) {
            pixel[i] = modificationTable[i][(int) pixel[i]];
        }
    }

    public void createModificationTable(BufferedImage image) {
        sumHistogram = new double[4][256];
        modificationTable = new double[4][256];
        for (int p = 0; p < 4; p++) {
            for (int i = 1; i < 256; i++) {
                sumHistogram[p][i] += sumHistogram[p][i - 1] + originalHistogram[p][1][i];
                modificationTable[p][i] = (gMin + (gMax - gMin) * sumHistogram[p][i])
                        / (image.getWidth() * image.getHeight());
            }
        }
    }

    public double[][][] createHistogramImage(BufferedImage image) {
        originalHistogram = new double[4][2][256];
        for (int p = 0; p < 4; p++) {
            for (int i = 0; i < 256; i++) {
                originalHistogram[p][0][i] = i;
                originalHistogram[p][1][i] = 0;
            }
        }

        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                double[] pixel = new double[3];
                image.getRaster().getPixel(col, row, pixel);

                originalHistogram[0][1][(int) pixel[0]]++;
                originalHistogram[1][1][(int) pixel[1]]++;
                originalHistogram[2][1][(int) pixel[2]]++;

                int luminance = (int) (0.299 * pixel[0] + 0.587 * pixel[1] + 0.114 * pixel[2]);
                originalHistogram[3][1][luminance]++;
            }
        }
        return originalHistogram;
    }
}
