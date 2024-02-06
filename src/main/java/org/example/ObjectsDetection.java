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

    static ArrayList<Hand> history = new ArrayList<>();
    private static int tolerance = 80;

    private ApparateFunctions apparateFunctions;

    private Functionality move_left;
    private Functionality move_right;
    private Functionality move_up;
    private Functionality move_down;
    private Functionality convergence;
    private Functionality divergence;
    private boolean isPaused = false;
    private ImageIcon pauseIcon;
    private int threshold = 150;


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
    public void detectObjects() throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String modelWeights = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.weights";
        String modelConfiguration = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.cfg";
        VideoCapture cap = new VideoCapture(0);

        if (!cap.isOpened()) {
            System.out.println("Unable to open webcam");
            System.exit(-1);
        }

        Mat frame = new Mat();
        JFrame jframe = new JFrame("Video");
        JLabel vidpanel = new JLabel();
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

        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);
        Size sz = new Size(512, 512);

        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = getOutputNames(net);

        while (true) {

            if (isPaused) {
                //vidpanel.setIcon(iconLabel);
                //vidpanel.repaint();
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
                            confs.add((float) confidence);

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
                        Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(0, 0, 255), 2);
                        //Imgproc.putText(frame, box.getName(), box.tl(), 2, 5.0, new Scalar(255, 0, 0));
                        Imgproc.putText(frame,box.tl() + "", box.tl(), 2, 5.0, new Scalar(255, 0, 0));
                        System.out.println(box.getName());
                    }
                }

                ImageIcon image = new ImageIcon(Mat2BufferedImage(frame));
                vidpanel.setIcon(image);
                vidpanel.repaint();
            }
        }
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
}