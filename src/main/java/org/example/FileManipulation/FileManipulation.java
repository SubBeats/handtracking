package org.example.FileManipulation;

import org.example.list.Functionality;

import java.io.*;
import java.util.ArrayList;

import static org.example.Main.mapComposeFunc;

public class FileManipulation {
    private static String path = "src/main/resources/properties.txt";

    static public ArrayList readConfigFile(int typeOfReading) {
        ArrayList<Functionality> arrayListFromFunctionMap = new ArrayList<>();
        ArrayList<String> arrayListOfValue = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1];
                    //System.out.println("Key: " + key + ", Value: " + value);
                    arrayListFromFunctionMap.add(mapComposeFunc.get(value));
                    arrayListOfValue.add(value);
                }
            }
            if (arrayListFromFunctionMap.size() - 1 != 6) {
                for (int i = 0; i < 5; i++) {
                    arrayListFromFunctionMap.add(mapComposeFunc.get(0));
                    arrayListOfValue.add("Увеличие громекости");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(typeOfReading == 0)  return arrayListFromFunctionMap;
        else return arrayListOfValue;
    }

    public static boolean writeInSettingFile(Object swipeLeftHand, Object swipeRightHand, Object swipeUpHand, Object swipeDownHand, Object convergenceHands, Object spreadingHands) throws IOException {
        java.io.File file = new java.io.File(path);
        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile())){
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Left_Swipe=" + swipeLeftHand +"\n");
            bufferedWriter.write("Right_Swipe=" + swipeRightHand +"\n");
            bufferedWriter.write("Up_Swipe=" + swipeUpHand +"\n");
            bufferedWriter.write("Down_Swipe=" + swipeDownHand +"\n");
            bufferedWriter.write("Swipe_In=" + convergenceHands +"\n");
            bufferedWriter.write("Swipe_Out=" + spreadingHands +"\n");
            bufferedWriter.flush();
        }
        return false;
    }
}
