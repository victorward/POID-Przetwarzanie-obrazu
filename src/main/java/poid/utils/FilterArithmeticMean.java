package poid.utils;

import java.awt.image.BufferedImage;

public class FilterArithmeticMean extends Process {
    @Override
    public void changePixel(double[] newPixelValue, int col, int row, BufferedImage image) {
        int maskFloor = (int) Math.floor(maskDimension / 2);
        for (int i = row - maskFloor; i < (row + maskFloor + 1); i++) {
            for (int j = col - maskFloor; j < (col + maskFloor + 1); j++) {
                double[] pixel = new double[super.pixelTableLength];
                image.getRaster().getPixel(j, i, pixel);
                for (int p = 0; p < super.pixelTableLength; p++) {
                    newPixelValue[p] = newPixelValue[p] + pixel[p];
                }
            }
        }
        for (int p = 0; p < super.pixelTableLength; p++) {
            newPixelValue[p] = newPixelValue[p] / (maskDimension * maskDimension);
        }
    }


}
