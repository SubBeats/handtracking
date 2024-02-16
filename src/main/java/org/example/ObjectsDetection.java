package org.example;

import org.example.FileManipulation.FileManipulation;
import org.example.apparate.ApparateFunctions;
import org.example.list.Functionality;
import org.example.model.Hand;
import org.example.settingMenu.App;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.example.Main.mapComposeFunc;

public class ObjectsDetection {

    static ArrayList<Hand> history = new ArrayList<>();
    private String path = "src/main/resources/properties.txt";
    private static final int tolerance = 100;

    private final ApparateFunctions apparateFunctions;

    private Functionality move_left;
    private Functionality move_right;
    private Functionality move_up;
    private Functionality move_down;
    private Functionality convergence;
    private Functionality divergence;
    private boolean isPaused = false;
    private final ImageIcon pauseIcon;
    private final int threshold = 150;
    String modelWeights = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.weights";
    String modelConfiguration = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.cfg";

    Mat frame;
    JFrame jframe = new JFrame("Video");
    private JFrame settingsFrame;

    JLabel vidpanel = new JLabel();


    public ObjectsDetection(Functionality left, Functionality move_right,
                            Functionality move_up, Functionality move_down, Functionality convergence, Functionality divergence) {
        apparateFunctions = new ApparateFunctions();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        frame = new Mat();
        this.move_left = left;
        this.move_right = move_right;
        this.move_up = move_up;
        this.move_down = move_down;
        this.convergence = convergence;
        this.divergence = divergence;
        pauseIcon = new ImageIcon("/Users/bulat/IdeaProjects/openCV/src/main/resources/img/Vector-Pause-Button-PNG-Clipart.png");
    }

    private static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();

        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();

        outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));//unfold and create R-CNN layers from the loaded YOLO model//
        return names;
    }

    private void configuration(){
        jframe.setContentPane(vidpanel);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(512, 512);
        jframe.setVisible(true);
        jframe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isPaused = !isPaused;
                if (isPaused) {
                    JLabel pauseLabel = new JLabel(pauseIcon);
                    pauseLabel.setBounds(0, 0, 512, 512);
                    vidpanel.add(pauseLabel);
                } else {
                    vidpanel.removeAll();
                }
                vidpanel.repaint();
            }
        });

        JButton settingsButton = new JButton("Settings");
        settingsButton.setBounds(10, 10, 100, 30);
        jframe.add(settingsButton);

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jframe.setVisible(false); // Скрываем текущее окно

                settingsFrame = new JFrame("Settings Window");
                settingsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                settingsFrame.setContentPane(new App());
                JButton backButton = new JButton("Back to Main");
                backButton.setBounds(10, 10, 150, 30);
                settingsFrame.add(backButton);

                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        jframe.setVisible(true); // Показываем основное окно
                        settingsFrame.dispose(); // Закрываем окно настроек
                        initMovements();
                    }
                });

                settingsFrame.setSize(400, 300);
                settingsFrame.setVisible(true);
            }
        });

        jframe.setSize(400, 300);
        jframe.setVisible(true);
    }
    public void detectObjects() throws InterruptedException {
        configuration();

        VideoCapture cap = new VideoCapture(0);

        if (!cap.isOpened()) {
            System.out.println("Unable to open webcam");
            System.exit(-1);
        }

        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);
        Size sz = new Size(512, 512);

        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = getOutputNames(net);

        int timer = 6000;

        while (true) {
            if (isPaused) {
                if(timer == 0) System.err.println("Жизнь движется а мы нет !");
                timer--;
                Thread.sleep(10);
                continue;
            }
            if (cap.read(frame)) {
                //Thread.sleep(1000);
                Mat blob = Dnn.blobFromImage(frame, 0.00392, sz, new Scalar(0), true, false);
                net.setInput(blob);
                net.forward(result, outBlobNames);
                float confThreshold = 0.5f;
                List<Integer> clsIds = new ArrayList<>();
                List<Float> confs = new ArrayList<>();
                List<Hand> rects = new ArrayList<>();

                for (int i = 0; i < result.size(); ++i) {
                    Mat level = result.get(i);
                    for (int j = 0; j < level.rows(); ++j) {
                        Mat row = level.row(j);
                        Mat scores = row.colRange(5, level.cols());
                        Core.MinMaxLocResult mm = Core.minMaxLoc(scores);
                        float confidence = (float) mm.maxVal;
                        Point classIdPoint = mm.maxLoc;

                        if (confidence > confThreshold) {
                            int centerX = (int) (row.get(0, 0)[0] * frame.cols());
                            int centerY = (int) (row.get(0, 1)[0] * frame.rows());
                            int width = (int) (row.get(0, 2)[0] * frame.cols());
                            int height = (int) (row.get(0, 3)[0] * frame.rows());
                            int left = centerX - width / 2;
                            int top = centerY - height / 2;

                            clsIds.add((int) classIdPoint.x);
                            confs.add(confidence);

                            Hand existingHand = isSameHand(left, top, width, height);

                            if (existingHand != null) {
                                rects.add(existingHand);
                            } else {
                                rects.add(new Hand(left, top, width, height,threshold));
                            }
                        }
                    }
                }

                history.clear();
                history.addAll(rects);

                float nmsThresh = 0.6f;

                if (!confs.isEmpty()) {
                    MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
                    Hand[] boxesArray = rects.toArray(new Hand[0]);
                    MatOfRect2d boxes = new MatOfRect2d();
                    boxes.fromArray(boxesArray);
                    MatOfInt indices = new MatOfInt();
                    Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);

                    int[] ind = indices.toArray();

                    for (int i = 0; i < ind.length; ++i) {
                        int idx = ind[i];
                        Hand box = boxesArray[idx];
                        Imgproc.rectangle(frame, new Point(box.getX(),box.getY()), box.br(), new Scalar(0, 0, 255), 2);
                        Imgproc.putText(frame, box.getName(), box.tl(), 2, 5.0, new Scalar(255, 0, 0));
                    }
                }
                updateImage(frame);

            }
        }
    }

    private void updateImage(Mat frame) {
        ImageIcon image = new ImageIcon(Mat2BufferedImage(frame));
        vidpanel.setIcon(image);
        vidpanel.repaint();
    }


    private static BufferedImage Mat2BufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
            Image resizedImage = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private @Nullable Hand isSameHand(int left, int top, int width, int height) {
        for (Hand oldHand : history) {
            if (Math.abs(oldHand.getX() - left) <= tolerance && Math.abs(oldHand.getY() - top) <= tolerance) {
                var action = oldHand.update(left,top, width, height);
                if(action != null) {

                    switch (action) {
                        case left -> apparateFunctions.startFunction(move_left);
                        case right -> apparateFunctions.startFunction(move_right);
                        case up -> apparateFunctions.startFunction(move_up);
                        case down -> apparateFunctions.startFunction(move_down);
                        case convergence -> apparateFunctions.startFunction(convergence);
                        case divergence -> apparateFunctions.startFunction(divergence);
                    }
                }
                return oldHand;
            }
        }
        return null;
    }

    private void initMovements() {
        ArrayList<Functionality> arrayFromSettingFile = FileManipulation.readConfigFile(0);
        move_left = arrayFromSettingFile.get(0);
        move_right = arrayFromSettingFile.get(1);
        move_up = arrayFromSettingFile.get(2);
        move_down = arrayFromSettingFile.get(3);
        convergence = arrayFromSettingFile.get(4);
        divergence = arrayFromSettingFile.get(5);
    }
}