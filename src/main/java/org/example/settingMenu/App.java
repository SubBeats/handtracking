package org.example.settingMenu;

import org.example.Main;
import org.example.list.Functionality;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class App extends JPanel {
    private final JComboBox<String> swipeRightHand;
    private final JComboBox<String> swipeUpHand;
    private final JComboBox<String> swipeDownHand;
    private final JComboBox<String> convergenceHands;
    private final JComboBox<String> spreadingHands;
    private final JComboBox<String> swipeLeftHand;
    private String path = "src/main/resources/properties.txt";

    public App() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 205, 222));
        panel.setLayout(new GridLayout(9, 1));
        setBackground(new Color(173, 205, 222));

        JLabel swipeLeftHandLabel = new JLabel("Тип управления свайпом влево рукой:");
        swipeLeftHand = createComboBox();
        panel.add(createLabelComboBoxPair(swipeLeftHandLabel, swipeLeftHand));

        JLabel swipeRightHandLabel = new JLabel("Тип управления свайпом вправо рукой:");
        swipeRightHand = createComboBox();
        panel.add(createLabelComboBoxPair(swipeRightHandLabel, swipeRightHand));

        JLabel swipeUpHandLabel = new JLabel("Тип управления свайпом вверх рукой:");
        swipeUpHand = createComboBox();
        panel.add(createLabelComboBoxPair(swipeUpHandLabel, swipeUpHand));

        JLabel swipeDownHandLabel = new JLabel("Тип управления свайпом вниз рукой:");
        swipeDownHand = createComboBox();
        panel.add(createLabelComboBoxPair(swipeDownHandLabel, swipeDownHand));

        JLabel convergenceHandsLabel = new JLabel("Тип управления схождением рук:");
        convergenceHands = createComboBox();
        panel.add(createLabelComboBoxPair(convergenceHandsLabel, convergenceHands));

        JLabel spreadingHandsLabel = new JLabel("Тип управления разжиманием рук:");
        spreadingHands = createComboBox();
        panel.add(createLabelComboBoxPair(spreadingHandsLabel, spreadingHands));

        JButton saveButton = new JButton("Сохранить");
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(Color.BLACK);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    writeInSettingFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(saveButton);

        add(panel);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        add(scrollPane);

        setSize(650, 450);

        setVisible(true);
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        addItems(comboBox);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        return comboBox;
    }

    private JPanel createLabelComboBoxPair(JLabel label, JComboBox<String> comboBox) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 205, 222));
        panel.setLayout(new FlowLayout());
        panel.add(label);
        panel.add(comboBox);
        return panel;
    }

    private void addItems(JComboBox<String> comboBox){
        Main.mapComposeFunc.forEach((key, value)-> {
            comboBox.addItem(key);
        });
    }


    private boolean writeInSettingFile() throws IOException {
        File file = new File(path);
        try (FileWriter fileWriter = new FileWriter(file.getAbsoluteFile())){
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Left_Swipe=" + swipeLeftHand.getSelectedItem()+"\n");
            bufferedWriter.write("Right_Swipe=" + swipeRightHand.getSelectedItem()+"\n");
            bufferedWriter.write("Up_Swipe=" + swipeUpHand.getSelectedItem()+"\n");
            bufferedWriter.write("Down_Swipe=" + swipeDownHand.getSelectedItem()+"\n");
            bufferedWriter.write("Swipe_In=" + convergenceHands.getSelectedItem()+"\n");
            bufferedWriter.write("Swipe_Out=" + spreadingHands.getSelectedItem()+"\n");
            bufferedWriter.flush();
        }
        return false;
    }
}