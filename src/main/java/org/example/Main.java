package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.list.Functionality;

import java.util.HashMap;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        HashMap<String, Functionality> mapComposeFunc = new HashMap<>();
        mapComposeFunc.put("Увеличие громекости",Functionality.upVolume);
        mapComposeFunc.put("Уменьшение громекости",Functionality.downVolume);
        mapComposeFunc.put("Уменьшение яркости",Functionality.downBrightness);
        mapComposeFunc.put("Увеличение яркости",Functionality.upBrightness);
        ObjectsDetection objectsDetection = new ObjectsDetection(mapComposeFunc.get("Увеличение яркости"),mapComposeFunc.get("Уменьшение яркости"),
        mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"),
        mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"));
        //objectsDetection.detectObjects();

        logger.info("Message");
        //ApparateFunctions apparateFunctions = new ApparateFunctions();
        //apparateFunctions.startFunction(nextTab);
    }

}
