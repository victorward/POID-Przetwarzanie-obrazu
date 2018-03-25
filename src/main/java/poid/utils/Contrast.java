package poid.utils;

import lombok.Getter;
import lombok.Setter;

public class Contrast extends Process {
    @Setter
    @Getter
    private double ratioA = 1;
    @Setter
    @Getter
    private double ratioB;

    public void setRatios(double ratioA) {
        this.ratioA = ratioA / 100;
        this.ratioB = 177 * (1 - this.ratioA);
    }

    @Override
    protected void enumeratePixelsArray() {
        for (int i = 0; i < super.pixelsArray.length; i++) {
            super.pixelsArray[i] = (int) (ratioA * i + ratioB);
            if (super.pixelsArray[i] > 255) {
                super.pixelsArray[i] = 255;
            } else if (super.pixelsArray[i] < 0) {
                super.pixelsArray[i] = 0;
            }
        }
    }
}
