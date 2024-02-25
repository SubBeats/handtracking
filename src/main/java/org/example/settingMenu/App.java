package org.example.settingMenu;

import org.example.FileManipulation.FileManipulation;
import org.example.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class App extends JPanel {
    private final JComboBox<String> swipeRightHand;
    private final JComboBox<String> swipeUpHand;
    private final JComboBox<String> swipeDownHand;
    private final JComboBox<String> convergenceHands;
    private final JComboBox<String> spreadingHands;
    private final JComboBox<String> swipeLeftHand;
    private String path = "src/main/resources/properties.txt";
    ArrayList arr;
    int index = 0;


    public App() {

        JPanel panel = new JPanel();
        panel.setBackground(new Color(173, 205, 222));
        panel.setLayout(new GridLayout(9, 1));
        setBackground(new Color(173, 205, 222));

        arr = FileManipulation.readConfigFile(1);

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
                    index = 0;
                    FileManipulation.writeInSettingFile(swipeLeftHand.getSelectedItem(),swipeRightHand.getSelectedItem(),swipeUpHand.getSelectedItem(),
                            swipeDownHand.getSelectedItem(),convergenceHands.getSelectedItem(),spreadingHands.getSelectedItem());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panel.add(saveButton);

        add(panel);

        JScrollPane scrollPane = new JScrollPane(panel);
        //scrollPane.setPreferredSize(new Dimension(600, 400));

        add(scrollPane);

        setSize(300, 400);

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
        selectItem(comboBox);
    }

    private void selectItem(JComboBox<String> comboBox){
        comboBox.setSelectedItem(arr.get(index));
        index++;
    }

}