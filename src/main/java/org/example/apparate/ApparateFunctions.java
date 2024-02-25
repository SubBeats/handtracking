package org.example.apparate;

import org.example.list.Functionality;

import java.io.IOException;

public class ApparateFunctions {

    private final Runtime runtime = Runtime.getRuntime();
    private static final int increaseBy = 10;


    private void increaseBrightness(){
        try {
            String[] increaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};
            runtime.exec(increaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decreaseBrightness(){
        try {
            String[] decreaseBrightness = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 144", "-e", "end tell"};
            runtime.exec(decreaseBrightness);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void increaseVolume(){
        try {
            String increaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + increaseBy + ")\"";
            runtime.exec(increaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void decreaseVolume(){
        try {
            String decreaseVolume = "osascript -e \"set volume output volume (output volume of (get volume settings) + " + -increaseBy + ")\"";
            runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void arrowUp() {
        try {
            //Thread.sleep(1000);
            String[] arg = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 126", "-e", "end tell"};
            runtime.exec(arg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void arrowDown() {
        try {
            //Thread.sleep(1000);
            String[] arg = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 125", "-e", "end tell"};
            runtime.exec(arg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void arrowLeft() {
        try {
            //Thread.sleep(1000);
            String[] arg = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 123", "-e", "end tell"};
            runtime.exec(arg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void arrowRight() {
        try {
            //Thread.sleep(1000);
            String[] arg = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 124", "-e", "end tell"};
            runtime.exec(arg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scaleChangeUP() {
        try {
        String[] argPlus = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 55 & 24", "-e", "end tell"};
        runtime.exec(argPlus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void scaleChangeDown() {
        try {
            String[] argMinus = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 55 & 27", "-e", "end tell"};
            runtime.exec(argMinus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void previousTab(){
        try {
            String[] decreaseVolume = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 123 using control down", "-e", "end tell"};
            runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void nextTab(){
        try {
            String[] decreaseVolume = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 124 using control down", "-e", "end tell"};
            runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void outToGeneralTabs(){
        try {
            String[] decreaseVolume = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 125 using control down", "-e", "end tell"};
            runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void SelectTab(){
        try {
            String[] decreaseVolume = {"osascript", "-e", "tell application \"System Events\"", "-e", "key code 126 using control down", "-e", "end tell"};
            runtime.exec(decreaseVolume);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openNewTabInSafari(){
        try {
            String arg = """
                tell application "Safari"
                   if (count of windows) = 0 then
                        activate
                   else
                        tell window 1
                            set current tab to (make new tab with properties {URL:"https://www.google.ru/?client=safari&channel=mac_bm"})
                        end tell
                   end if
                   reopen
                end tell
                """;
            String[] scriptCommand = { "osascript", "-e",arg};
            runtime.exec(scriptCommand);
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
                arrowUp();
            }
            case downArrow -> {
                arrowDown();
            }
            case arrow_is_right -> {
                arrowRight();
            }
            case arrow_is_left -> {
                arrowLeft();
            }
            case zoom_in -> {
                scaleChangeUP();
            }
            case zoom_out -> {
                scaleChangeDown();
            }
            case previousTab -> {
                previousTab();
            }
            case nextTab -> {
                nextTab();
            }
            case outToGeneralTabs -> {
                outToGeneralTabs();
            }
            case selectTab -> {
                SelectTab();
            }
            case openSafari -> {
                openNewTabInSafari();
            }
        }
    }
}
