package org.example;

import org.example.apparate.ApparateFunctions;
import org.example.list.Functionality;
import org.example.model.Hand;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectsDetection {
    Mat frame;
    JFrame jframe;
    JLabel vidpanel;

    static ArrayList<Hand> history = new ArrayList<>();
    private static final int tolerance = 100;

    private final ApparateFunctions apparateFunctions;

    private final Functionality move_left;
    private final Functionality move_right;
    private final Functionality move_up;
    private final Functionality move_down;
    private final Functionality convergence;
    private final Functionality divergence;
    private boolean isPaused = false;
    private final ImageIcon pauseIcon;
    private final int threshold = 150;
    VideoCapture cap;
    List<Mat> result;
    List<String> outBlobNames;
    Net net;
    Size sz;
    JTabbedPane tabbedPane;
    private JLabel sesettingsLabel;


    public ObjectsDetection(Functionality left, Functionality move_right,
                            Functionality move_up, Functionality move_down, Functionality convergence, Functionality divergence) {
        apparateFunctions = new ApparateFunctions();
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

    public void configurationObject(JFrame jframe, JLabel vidpanel, JLabel settingsLabel) throws InterruptedException {

        //this.frame = frame;
        this.jframe = jframe;
        this.vidpanel = vidpanel;
        this.sesettingsLabel = settingsLabel;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        cap = new VideoCapture(0);

        if (!cap.isOpened()) {
            System.out.println("Unable to open webcam");
            System.exit(-1);
        }

        configWindow();
        //scanCamera();
    }

    private void configWindow() {
        String modelWeights = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.weights";
        String modelConfiguration = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.cfg";

        net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);
        sz = new Size(512, 512);

        result = new ArrayList<>();
        outBlobNames = getOutputNames(net);

          frame = new Mat();
//        jframe = new JFrame("Video");
//        vidpanel = new JLabel();
//        JLabel settingsLabel = new JLabel();
//
//        //createTabs(jframe, vidpanel, settingsLabel);
//
//        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        jframe.setSize(800, 600); // Примерный размер окна
//        jframe.setVisible(true);

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
    }

//    private void createTabs(JFrame jframe, JLabel videoLabel, JLabel settingsLabel) {
//        tabbedPane = new JTabbedPane();
//
//        // Создаем вкладку "Видео"
//        JPanel videoPanel = new JPanel();
//        videoPanel.setLayout(new BorderLayout());
//        videoPanel.add(videoLabel, BorderLayout.CENTER);
//        tabbedPane.addTab("Video", videoPanel);
//
//        // Создаем вкладку "Настройки"
//        JPanel settingsPanel = new JPanel();
//        settingsPanel.setLayout(new BorderLayout());
//        settingsPanel.add(settingsLabel, BorderLayout.CENTER);
//        tabbedPane.addTab("Настройки", settingsPanel);
//
//        tabbedPane.addChangeListener(e -> {
//            // Обработка изменения вкладок
//            if (tabbedPane.getSelectedIndex() == 0) {
//                // Отобразить видео Label
//                videoLabel.setVisible(true);
//                settingsLabel.setVisible(false);
//            } else {
//                // Отобразить настройки Label
//                videoLabel.setVisible(false);
//                settingsLabel.setVisible(true);
//            }
//        });
//
//        jframe.add(tabbedPane);
//    }

    public ImageIcon scanCamera(JLabel vidpanel) throws InterruptedException {
        int timer = 6000;

        while (true) {
            if (tabbedPane.getSelectedIndex() == 0) {

                if (isPaused) {
                    if (timer == 0) System.err.println("Жизнь движется а мы нет !");
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
                                    rects.add(new Hand(left, top, width, height, threshold));
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
                            Imgproc.rectangle(frame, new Point(box.getX(), box.getY()), box.br(), new Scalar(0, 0, 255), 2);
                            Imgproc.putText(frame, box.getName(), box.tl(), 2, 5.0, new Scalar(255, 0, 0));
                        }
                    }
                    return updateImage(frame);
                }
            } else {
                Thread.sleep(1000);
            }
        }
    }

    private ImageIcon updateImage(Mat frame) {
        return new ImageIcon(Mat2BufferedImage(frame));
//        vidpanel.setIcon(image);
//        vidpanel.repaint();
    }


    private static BufferedImage Mat2BufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[] bytes = bytemat.toArray();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage img = null;
        try {
            img = ImageIO.read(in);
            Image resizedImage = img.getScaledInstance(512, 512, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
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
                var action = oldHand.update(left, top, width, height);
                if (action != null) {

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

//    public static void main(String[] args) throws InterruptedException {
//        new ObjectsDetection(Functionality.arrow_is_left, Functionality.arrow_is_left, Functionality.arrow_is_left, Functionality.arrow_is_left, Functionality.arrow_is_left, Functionality.arrow_is_left).
//                configurationObject();
//
//    }
}