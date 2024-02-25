package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.list.Functionality;
//import org.example.settingMenu.Example;

import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static HashMap<String, Functionality> mapComposeFunc;
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {

        mapComposeFunc = new HashMap<>();
        mapComposeFunc.put("Увеличие громекости",Functionality.upVolume);
        mapComposeFunc.put("Уменьшение громекости",Functionality.downVolume);
        mapComposeFunc.put("Уменьшение яркости",Functionality.downBrightness);
        mapComposeFunc.put("Увеличение яркости",Functionality.upBrightness);
        mapComposeFunc.put("Стрелочка вверх",Functionality.upArrow);
        mapComposeFunc.put("Стрелочка вниз",Functionality.downArrow);
        mapComposeFunc.put("Стрелочка вправо",Functionality.arrow_is_right);
        mapComposeFunc.put("Стрелочка влево",Functionality.arrow_is_left);
        mapComposeFunc.put("Увеличение зума",Functionality.zoom_in);
        mapComposeFunc.put("Уменьшение зума",Functionality.zoom_out);
        mapComposeFunc.put("Следующая вкладка",Functionality.nextTab);
        mapComposeFunc.put("Предыдущая вкладка",Functionality.previousTab);
        mapComposeFunc.put("Список вкладок",Functionality.outToGeneralTabs);
        mapComposeFunc.put("Выбрать вкладку",Functionality.selectTab);

        //new Example(mapComposeFunc);


        ObjectsDetection objectsDetection = new ObjectsDetection(mapComposeFunc.get("Увеличение яркости"),mapComposeFunc.get("Уменьшение яркости"),
                mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"),
                mapComposeFunc.get("Увеличие громекости"),mapComposeFunc.get("Уменьшение громекости"));
        objectsDetection.detectObjects();

        logger.info("Start app");
        //ApparateFunctions apparateFunctions = new ApparateFunctions();
        //apparateFunctions.startFunction(nextTab);


    }

}
