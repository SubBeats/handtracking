package org.example.settingMenu;

import org.example.ObjectsDetection;
import org.example.list.Functionality;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Example {
    private String path = "src/main/resources/properties.txt";

    HashMap<String, Functionality> mapComposeFunc;
    public Example(HashMap<String, Functionality> mapComposeFunc) throws InterruptedException {
        this.mapComposeFunc = mapComposeFunc;
        loadMainPage();
    }
    public void loadMainPage() throws InterruptedException {
        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        JButton button = new JButton("Start");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Functionality> functionalityArray = readConfigFile();
                ObjectsDetection video = new ObjectsDetection(functionalityArray.get(0),
                        functionalityArray.get(1),
                        functionalityArray.get(2),
                        functionalityArray.get(3),
                        functionalityArray.get(4),
                        functionalityArray.get(5));
                try {
                    video.detectObjects();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel panel1 = new JPanel();
        panel1.add(new App(mapComposeFunc));

        // Add panels to tabs

        tabbedPane.addTab("Tab 1", panel1);
        tabbedPane.addTab("Tab 2", button);

        // Add JTabbedPane to the frame
        JFrame frame = new JFrame();
        frame.add(tabbedPane);
        frame.setResizable(false);
        frame.setSize(650, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private ArrayList<Functionality> readConfigFile() {
        ArrayList<Functionality> arrayList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length >= 2) {
                    String key = parts[0];
                    String value = parts[1];
                    //System.out.println("Key: " + key + ", Value: " + value);
                    arrayList.add(mapComposeFunc.get(value));
                }
            }
            if(arrayList.size()-1 != 6){
                for(int i =0 ;i<5;i++){
                    arrayList.add(mapComposeFunc.get(0));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
