package org.example.model;

import org.example.list.ObjectTracker;
import org.opencv.core.Rect2d;

import java.util.LinkedList;
import java.util.UUID;

public class Hand extends Rect2d {
    private String name;
    private ObjectTracker coordinatesX;
    private ObjectTracker  coordinatesY;

    public Hand(double x, double y, double width, double height) {
        super(x, y, width, height);
        this.name = "Object " + UUID.randomUUID().toString().substring(0, 5); // Генерируем случайное имя
        coordinatesX = new ObjectTracker(3);
        coordinatesY = new ObjectTracker(3);
    }

    public void addCoordinate(double x, double y) {
        if(this.getCoordinateX()<-10) {
            System.out.println("moved more then 40 db by x");
        }
        System.out.println(this.getCoordinateX());

        coordinatesX.addCoordinate(x);
        coordinatesY.addCoordinate(y);
    }

    public double getCoordinateX(){
        if(coordinatesX.getLight() == 3) {
            return coordinatesX.getCoordinates().stream().reduce(coordinatesX.get(0), (a, b) -> a - b);
        }
        return 0.0;
    }


    public double getX(){
        return coordinatesX.getLastCoordinate();
    }

    public double getY(){
        return coordinatesY.getLastCoordinate();
    }

    public String getName() {
        return name;
    }

    public void update(Hand newHand) {
        addCoordinate(newHand.x, newHand.y);
        this.width = newHand.width;
        this.height = newHand.height;
    }
}
