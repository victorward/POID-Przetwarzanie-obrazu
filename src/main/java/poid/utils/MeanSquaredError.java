package poid.utils;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MeanSquaredError {
    @Setter
    @Getter
    int pixelTableLength = 3;

    public double[] countError(BufferedImage originalImage, BufferedImage changedImage) {
        double[] error = {0, 0, 0};
        for (int col = 0; col < originalImage.getWidth(); col++) {
            for (int row = 0; row < originalImage.getHeight(); row++) {
                double[] pixelOriginal = new double[pixelTableLength];
                originalImage.getRaster().getPixel(col, row, pixelOriginal);
                double[] pixelChanged = new double[pixelTableLength];
                changedImage.getRaster().getPixel(col, row, pixelChanged);
                for (int p = 0; p < pixelTableLength; p++) {
                    error[p] = error[p] + Math.pow((pixelOriginal[p] - pixelChanged[p]), 2);
                }
            }
        }
        for (int p = 0; p < pixelTableLength; p++) {
            error[p] = error[p] / (originalImage.getWidth() * originalImage.getHeight());
        }
        return error;
    }

    public double[] countError(String originalImageUrl, BufferedImage changedImage) {
        BufferedImage originalImage = loadImage(originalImageUrl);
        double[] error = countError(originalImage, changedImage);
        return error;
    }

    private BufferedImage loadImage(String originalImageUrl) {
        File directoryImage = new File(originalImageUrl);
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(directoryImage);
        } catch (IOException e) {
            System.err.println("Reading file error");
        }
        return originalImage;
    }
}
