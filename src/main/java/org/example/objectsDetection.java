package org.example;

import org.example.apparate.Volume;
import org.example.model.Hand;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.FONT_HERSHEY_SIMPLEX;
import static org.opencv.imgproc.Imgproc.putText;

public class objectsDetection {
    static List<Hand> objects = new ArrayList<>();

    private static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();

        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();

        outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));//unfold and create R-CNN layers from the loaded YOLO model//
        return names;
    }
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

       /* int frameCount = 0;
        long lastTime = System.currentTimeMillis();

        Volume volume = new Volume();
        volume.changeVolume(0.5f);*/

        String modelWeights = "D:\\project\\handtracking\\src\\main\\resources\\yolo\\cross-hands.weights"; //Download and load only wights for YOLO , this is obtained from official YOLO site//
        String modelConfiguration = "D:\\project\\handtracking\\src\\main\\resources\\yolo\\cross-hands.cfg";//Download and load cfg file for YOLO , can be obtained from official site//
        String filePath = "C:\\Users\\sabit\\OneDrive\\untitled\\openCV\\src\\main\\java\\org\\example\\WIN_20230610_19_29_42_Pro.mp4"; //My video  file to be analysed//
        VideoCapture cap = new VideoCapture(0); // Use 0 for default webcam. You can change the parameter if you have multiple webcams.

        if (!cap.isOpened()) {
            System.out.println("Unable to open webcam");
            System.exit(-1);
        }

        Mat frame = new Mat();
        JFrame jframe = new JFrame("Video");
        JLabel vidpanel = new JLabel();
        jframe.setContentPane(vidpanel);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(600, 600);
        jframe.setVisible(true);

        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);

        Size sz = new Size(288, 288);

        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = getOutputNames(net);
        Mat blob = new Mat();

        while (true) {
            if (cap.read(frame)) {
                Imgproc.resize(frame, frame, new Size(256, 256)); // Измените размер кадра
                blob = Dnn.blobFromImage(frame, 0.00392, sz, new Scalar(0), true, false);
                net.setInput(blob);
                net.forward(result, outBlobNames);
                float confThreshold = 0.5f;
                List<Integer> clsIds = new ArrayList<>();
                List<Float> confs = new ArrayList<>();
                //confs.add(0f);
                List<Rect2d> rects = new ArrayList<>();
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
                            Hand hand = new Hand(left, top, width, height);
                            rects.add(hand);
                            //System.out.println(hand.getName());
                        }
                    }
                }
                float nmsThresh = 0.3f;
                if(!confs.isEmpty()) {
                    MatOfFloat confidences = new MatOfFloat(Converters.vector_float_to_Mat(confs));
                    Rect2d[] boxesArray = rects.toArray(new Rect2d[0]);
                    MatOfRect2d boxes = new MatOfRect2d();
                    boxes.fromArray(boxesArray);
                    MatOfInt indices = new MatOfInt();
                    Dnn.NMSBoxes(boxes, confidences, confThreshold, nmsThresh, indices);

                    int[] ind = indices.toArray();

                    for (int i = 0; i < ind.length; ++i) {
                        int idx = ind[i];
                        Hand box = (Hand) boxesArray[idx];
                        Hand existingHand = findExistingHand(box);
                        if (existingHand != null) {
                            box = existingHand;
                        }else {
                            objects.add(box);
                        }
                        String name = box.getName(); // Получаем имя объекта7
                        Imgproc.rectangle(frame, box.tl(), box.br(), new Scalar(0, 0, 255), 2);
                        putText(frame, name, new Point(box.tl().x, box.tl().y - 5), FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255)); // Рисуем имя
                        box.addCoordinate(box.tl().x, box.br().y);
                        System.out.print("X= " +box.tl().x + " : ");
                        System.out.println("Y = " + box.br().y);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private static Hand findExistingHand(Hand newHand) {
        final int errorMargin = 40; // Величина погрешности в пикселях

        for (Hand hand : objects) {
            boolean leftInRange = hand.x >= newHand.x - errorMargin &&
                    hand.x <= newHand.x + errorMargin;
            boolean topInRange = hand.y >= newHand.y - errorMargin &&
                    hand.y <= newHand.y + errorMargin;
            boolean widthInRange = hand.width >= newHand.width - errorMargin &&
                    hand.width <= newHand.width + errorMargin;
            boolean heightInRange = hand.height >= newHand.height - errorMargin &&
                    hand.height <= newHand.height + errorMargin;

            if (leftInRange && topInRange && widthInRange && heightInRange) {
                hand.update(newHand); // Обновляем данные о руке
                System.out.println("Find!");
                return hand;            }
        }
        return null;
    }
}