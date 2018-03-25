package poid.utils;

import lombok.Getter;
import lombok.Setter;

public class Brightness extends Process {
    @Getter @Setter
    private int offset = 0;

    @Override
    protected void enumeratePixelsArray() {
        for (int i = 0; i < super.pixelsArray.length; i++) {
            super.pixelsArray[i] = i + offset;
            if (super.pixelsArray[i] > 255) {
                super.pixelsArray[i] = 255;
            } else if (super.pixelsArray[i] < 0) {
                super.pixelsArray[i] = 0;
            }
        }
    }
}
