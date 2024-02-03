package org.example;

import org.example.model.Hand;
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class objectsDetection {

    static ArrayList<Hand> history = new ArrayList<>();
    private static int tolerance = 40;


    private static List<String> getOutputNames(Net net) {
        List<String> names = new ArrayList<>();

        List<Integer> outLayers = net.getUnconnectedOutLayers().toList();
        List<String> layersNames = net.getLayerNames();

        outLayers.forEach((item) -> names.add(layersNames.get(item - 1)));//unfold and create R-CNN layers from the loaded YOLO model//
        return names;
    }
    public static void main(String[] args) throws InterruptedException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String modelWeights = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.weights"; //Download and load only wights for YOLO , this is obtained from official YOLO site//
        String modelConfiguration = "/Users/bulat/IdeaProjects/openCV/src/main/java/org/example/cross-hands-tiny-prn.cfg";//Download and load cfg file for YOLO , can be obtained from official site//
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
        jframe.setSize(256, 256);
        jframe.setVisible(true);

        Net net = Dnn.readNetFromDarknet(modelConfiguration, modelWeights);

        Size sz = new Size(288, 288);

        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = getOutputNames(net);

        while (true) {
            if (cap.read(frame)) {
                Mat blob = Dnn.blobFromImage(frame, 0.00392, sz, new Scalar(0), true, false);
                net.setInput(blob);
                net.forward(result, outBlobNames);
                float confThreshold = 0.4f;
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
                                // Используйте найденный объект из history
                                rects.add(existingHand);
                            } else {
                                // Создайте новый объект, так как существующий объект не был найден в history
                                rects.add(new Hand(left, top, width, height));
                            }
                        }
                    }
                }

                history.clear(); // Очистить историю перед обновлением
                history.addAll(rects);

                float nmsThresh = 0.5f;
                if(!confs.isEmpty()) {
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
                        Imgproc.putText(frame,box.getName(),box.tl(),2,5.0,new Scalar(255, 0, 0));
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
            Image resizedImage = img.getScaledInstance(256, 256, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
            return bufferedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    private static Hand isSameHand(int left, int top, int width, int height) {
        for (Hand oldHand : history) {
            if (Math.abs(oldHand.getX() - left) <= tolerance && Math.abs(oldHand.getY() - top) <= tolerance) {
                oldHand.update(left,top, width, height);
                return oldHand;
            }
        }
        return null;
    }
}