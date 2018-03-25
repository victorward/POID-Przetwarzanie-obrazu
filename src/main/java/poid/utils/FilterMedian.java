package poid.utils;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class FilterMedian extends Process {
    @Override
    public void changePixel(double[] newPixelValue, int col, int row, BufferedImage image) {
        double[][] valueToMedian = new double[super.pixelTableLength][(maskDimension * maskDimension)];
        int iterator = 0;
        int maskFloor = (int) Math.floor(maskDimension / 2);
        for (int i = row - maskFloor; i < (row + maskFloor + 1); i++) {
            for (int j = col - maskFloor; j < (col + maskFloor + 1); j++) {
                double[] pixel = new double[super.pixelTableLength];
                image.getRaster().getPixel(j, i, pixel);
                for (int p = 0; p < super.pixelTableLength; p++) {
                    valueToMedian[p][iterator] = pixel[p];
                }
                iterator++;
            }
        }
        int position = (int) Math.ceil(maskDimension * maskDimension / 2);
        for (int p = 0; p < super.pixelTableLength; p++) {
            Arrays.sort(valueToMedian[p]);
            newPixelValue[p] = valueToMedian[p][position];
        }
    }
}
;