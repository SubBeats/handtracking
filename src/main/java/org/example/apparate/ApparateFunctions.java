package org.example.apparate;

import org.example.list.Functionality;

import java.io.IOException;

public class ApparateFunctions {

    private Runtime runtime = Runtime.getRuntime();
    private static final int increaseBy = 10;


    private void increaseBrightness(){
        try {
            String[] increaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};
            Process process  = runtime.exec(increaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decreaseBrightness(){
        try {
            String[] decreaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};
            Process process  = runtime.exec(decreaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void increaseVolume(){
        try {
            String increaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + increaseBy + ")\"";
            Process process  = runtime.exec(increaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decreaseVolume(){
        try {
            String decreaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + -increaseBy + ")\"";
            Process process  = runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startFunction(Functionality functionality){
        switch (functionality) {
            case upVolume -> {
                increaseVolume();
            }
            case downVolume -> {
                decreaseVolume();
            }
            case upBrightness -> {
                increaseBrightness();
            }
            case downBrightness -> {
                decreaseBrightness();
            }
            case upArrow -> {
            }
            case arrow_is_right -> {
            }
            case arrow_is_left -> {
            }
            case zoom_in -> {
            }
            case zoom_out -> {
            }
        }
    }
}
