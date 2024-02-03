package org.example.apparate;

import java.io.IOException;

public class Brightness {

    //Brightness change
    private static Runtime runtime = Runtime.getRuntime();
    private final static String[] increaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};
    private final static String[] decreaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};

    public static void increase(){
        try {
            Process process  = runtime.exec(increaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void decrease(){
        try {
            Process process  = runtime.exec(decreaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
