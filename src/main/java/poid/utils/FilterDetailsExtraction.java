package poid.utils;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class FilterDetailsExtraction extends Process {
    @Setter
    @Getter
    private int selectedMask = 0;
    @Getter
    private final Integer[][][] masks = {
            {
                    {-1, -1, -1},
                    {1, -2, 1},
                    {1, 1, 1}
            },
            {
                    {1, -1, -1},
                    {1, -2, -1},
                    {1, 1, 1}
            },
            {
                    {1, 1, -1},
                    {1, -2, -1},
                    {1, 1, -1}
            },
            {
                    {1, 1, 1},
                    {1, -2, -1},
                    {1, -1, -1}
            },
            {
            },
    };

    @Override
    public void changePixel(double[] valueTable, int col, int row, BufferedImage image) {
        int maskFloor = (int) Math.floor(maskDimension / 2);

        for (int y = row - maskFloor, n = 0; y < (row + maskFloor + 1); y++, n++) {
            for (int x = col - maskFloor, m = 0; x < (col + maskFloor + 1); x++, m++) {
                double[] pixel = new double[pixelTableLength];
                image.getRaster().getPixel(x, y, pixel);
                for (int p = 0; p < pixelTableLength; p++) {
                    valueTable[p] += pixel[p] * masks[selectedMask][m][n];
                }
            }
        }

        for (int p = 0; p < pixelTableLength; p++) {
            if (valueTable[p] > 255) {
                valueTable[p] = 255;
            } else if (valueTable[p] < 0) {
                valueTable[p] = 0;
            }
        }
    }
}
