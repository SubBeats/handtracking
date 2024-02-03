package org.example.apparate;

import java.io.IOException;

public class Volume {
    //Brightness change
    private static final int increaseBy = 10;
    private final static String increaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + increaseBy + ")\"";
    private final static String decreaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + -increaseBy + ")\"";
    static Runtime runtime = Runtime.getRuntime();
    public static void increase(){
        try {
            Process process  = runtime.exec(increaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void decrease(){
        try {
            Process process  = runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
