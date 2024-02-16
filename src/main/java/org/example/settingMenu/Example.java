//package org.example.settingMenu;
//
//import org.example.ObjectsDetection;
//import org.example.list.Functionality;
//import org.opencv.core.Mat;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class Example {
//    private static Lock lockTab1 = new ReentrantLock();
//    private static Lock lockTab2 = new ReentrantLock();
//    private String path = "src/main/resources/properties.txt";
//    HashMap<String, Functionality> mapComposeFunc;
//    private Mat frame;
//    private JFrame jframe;
//    private JLabel vidpanel;
//    private JLabel settingsLabel;
//    private ObjectsDetection video;
//    private JTabbedPane tabbedPane;
//    App setting;
//
//    public Example(HashMap<String, Functionality> mapComposeFunc) throws InterruptedException {
//        this.mapComposeFunc = mapComposeFunc;
//        loadMainPage();
//    }
//
//    public void loadMainPage() throws InterruptedException {
//        configMainWindow();
//        configVideoDetectionWindow();
//        configSettingWindow(mapComposeFunc);
//        addListener();
//    }
//
//    private void configMainWindow() {
//
//        jframe = new JFrame("Video");
//        vidpanel = new JLabel();
//        settingsLabel = new JLabel();
//
//        tabbedPane = new JTabbedPane();
//
//        // Создаем вкладку "Видео"
//        JPanel videoPanel = new JPanel();
//        videoPanel.setLayout(new BorderLayout());
//        videoPanel.add(vidpanel, BorderLayout.CENTER);
//        tabbedPane.addTab("Video", videoPanel);
//
//        // Создаем вкладку "Настройки"
//        JPanel settingsPanel = new JPanel();
//        settingsPanel.setLayout(new BorderLayout());
//        settingsPanel.add(settingsLabel, BorderLayout.CENTER);
//        tabbedPane.addTab("Настройки", settingsPanel);
//    }
//
//    private void addListener(){
//        jframe.add(tabbedPane);
//        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jframe.setSize(new Dimension(300, 200));
//        jframe.setVisible(true);
//
//        tabbedPane.addChangeListener(e -> {
//            int tabIndex = tabbedPane.getSelectedIndex();
//            if (tabIndex == 0) {
//                lockTab2.lock(); // Блокировка второй вкладки при переключении на первую
//                lockTab1.unlock(); // Разблокировка первой вкладки
//                try {
//                    vidpanel.setVisible(true);
//                    settingsLabel.setVisible(false);
//                    while (true) {
//                        vidpanel.setIcon(video.scanCamera(vidpanel));
//                        vidpanel.repaint();
//                    }
//                } catch (InterruptedException ex) {
//                    throw new RuntimeException(ex);
//                }
//            } else {
//                lockTab1.lock(); // Блокировка первой вкладки при переключении на вторую
//                lockTab2.unlock(); // Разблокировка второй вкладки
//                settingsLabel.setVisible(true);
//                vidpanel.setVisible(false);
//            }
//        });
//        lockTab2.lock(); // Изначально блокируем вторую вкладку
//
//    }
//
//    private void configVideoDetectionWindow() throws InterruptedException {
//        ArrayList<Functionality> functionalityArray = readConfigFile();
//        video = new ObjectsDetection(functionalityArray.get(0),
//                functionalityArray.get(1),
//                functionalityArray.get(2),
//                functionalityArray.get(3),
//                functionalityArray.get(4),
//                functionalityArray.get(5));
//
//        video.configurationObject(jframe,vidpanel,settingsLabel);
//    }
//
//    private void configSettingWindow(HashMap<String, Functionality> mapComposeFunc) {
//        setting = new App(mapComposeFunc);
//    }
//
//    private ArrayList<Functionality> readConfigFile() {
//        ArrayList<Functionality> arrayList = new ArrayList<>();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split("=");
//                if (parts.length >= 2) {
//                    String key = parts[0];
//                    String value = parts[1];
//                    //System.out.println("Key: " + key + ", Value: " + value);
//                    arrayList.add(mapComposeFunc.get(value));
//                }
//            }
//            if (arrayList.size() - 1 != 6) {
//                for (int i = 0; i < 5; i++) {
//                    arrayList.add(mapComposeFunc.get(0));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return arrayList;
//    }
//}
//
