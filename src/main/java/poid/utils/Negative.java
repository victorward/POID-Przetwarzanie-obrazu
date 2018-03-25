package poid.utils;

public class Negative extends Process {
    @Override
    protected void enumeratePixelsArray() {
        for (int i = 0; i < super.pixelsArray.length; i++) {
            super.pixelsArray[i] = 255-i ;
        }
    }
}
