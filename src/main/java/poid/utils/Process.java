package poid.utils;

import lombok.Setter;

import java.awt.image.BufferedImage;

public class Process {
    @Setter
    protected int pixelTableLength = 3;
    @Setter
    protected int maskDimension = 3;
    protected double[] pixelsArray = new double[256];

    public BufferedImage changeImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        enumeratePixelsArray();

        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                double[] pixel = new double[pixelTableLength];
                image.getRaster().getPixel(col, row, pixel);
                changePixel(pixel);
                newImage.getRaster().setPixel(col, row, pixel);
            }
        }
        return newImage;
    }

    public BufferedImage filter(BufferedImage image, String filterName) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int maskFloor = (int) Math.floor(maskDimension / 2);
                if (row >= (maskFloor) & col >= (maskFloor) & row < (height - (maskFloor)) & col < (width - (maskFloor))) {
                    double[] valueTable = new double[maskDimension];
                    changePixel(valueTable, col, row, image);
                    newImage.getRaster().setPixel(col, row, valueTable);
                } else if (!"Details".equals(filterName)) {
                    fillFrame(image, col, row, newImage);
                }
            }
        }
        return newImage;
    }

    private void fillFrame(BufferedImage oldImage, int col, int row, BufferedImage newImage) {
        double[] pixel = new double[pixelTableLength];
        oldImage.getRaster().getPixel(col, row, pixel);
        newImage.getRaster().setPixel(col, row, pixel);
    }

    public void changePixel(double[] valueTable, int col, int row, BufferedImage image) {
    }

    protected void enumeratePixelsArray() {
    }

    protected void changePixel(double[] pixel) {
        for (int i = 0; i < pixelTableLength; i++) {
            pixel[i] = pixelsArray[(int) pixel[i]];
        }
    }

    public BufferedImage filterWithNormalization(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        int width = image.getWidth();
        int height = image.getHeight();

        //pobranie nieznormalizowanej tablicy wartości
        double[][][] allPixel = new double[width][height][pixelTableLength];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int maskFloor = (int) Math.floor(maskDimension / 2);
                if (row >= (maskFloor) & col >= (maskFloor) & row < (height - (maskFloor)) & col < (width - (maskFloor))) {
                    double[] valueTable = new double[maskDimension];
                    changePixel(valueTable, col, row, image);
                    for (int p = 0; p < pixelTableLength; p++) {
                        allPixel[row][col][p] = valueTable[p];
                    }
                }
            }
        }
        for (int p = 0; p < pixelTableLength; p++) {
            int maskFloor = (int) Math.floor(maskDimension / 2);
            double min = allPixel[maskFloor][maskFloor][p];
            double max = allPixel[height - maskFloor][width - maskFloor][p];

            //poszukiwanie minium i maximum na potrzeby normalizacji
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (row >= (maskFloor) & col >= (maskFloor) & row < (height - (maskFloor)) & col < (width - (maskFloor))) {
                        if (allPixel[col][row][p] < min) {
                            min = allPixel[col][row][p];
                        } else if (allPixel[col][row][p] > max) {
                            max = allPixel[col][row][p];
                        }
                    }
                }
            }
            //wyliczenie wartości po normalizacji
            double factorA = 255.0 / Math.abs(min - max);
            double factorB = 255.0 - factorA * max;
            double newMax = 255.0;
            double newMin = 0.0;

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (row >= (maskFloor) & col >= (maskFloor) & row < (height - (maskFloor)) & col < (width - (maskFloor))) {
                        allPixel[col][row][p] = factorA * allPixel[col][row][p] + factorB;
                    } else {
                        allPixel[col][row][p] = factorB; //x=0
                    }
                    // second way
//                    allPixel[col][row][p] = Math.ceil(((newMax - newMin) / (max - min)) * (allPixel[col][row][p] - min) + newMin);
                }
            }
        }
        //utworzenie obrazu
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newImage.getRaster().setPixel(row, col, allPixel[col][row]);
            }
        }
        return newImage;
    }

}
