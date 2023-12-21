package org.example;

import org.example.list.ObjectTracker;
import org.opencv.core.Rect2d;

import java.util.LinkedList;

public class Hand extends Rect2d {
    float X;
    float Y;

    ObjectTracker coordinatesX;
    ObjectTracker  coordinatesY;

    public Hand(double x, double y, double width, double height) {
        super(x, y, width, height);
        coordinatesX = new ObjectTracker(3);
        coordinatesY = new ObjectTracker(3);
    }

    public void addCoordinate(float x, float y) {
        coordinatesX.addCoordinate(x);
        coordinatesY.addCoordinate(y);
    }

    public float getCoordinateX(){
        return coordinatesX.getCoordinates().stream().reduce(0.0f, (a, b) -> a + b);
    }
}
