package org.example;

import org.example.list.Functionality;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        HashMap<String, Functionality> mapComposeFunc = new HashMap<>();
        mapComposeFunc.put("Увеличие громекости",Functionality.upVolume);
        mapComposeFunc.put("Уменьшение громекости",Functionality.downVolume);
        new Main().testInit(mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"));
        ObjectsDetection objectsDetection = new ObjectsDetection(mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"),
        mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"),
        mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"));
        objectsDetection.detectObjects();
    }

    public void testInit(Functionality arg1, Functionality arg2){
        System.out.println(arg1);
        System.out.println(arg2);
    }

}
