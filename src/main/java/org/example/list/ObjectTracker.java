package org.example.list;

import java.util.LinkedList;
import java.util.List;

public class ObjectTracker {

    private LinkedList<Double> coordinates;
    private int size;

    public ObjectTracker(int size) {
        this.size = size;
        this.coordinates = new LinkedList<>();
    }

    public void addCoordinate(double x) {
        coordinates.add(x);
        if (coordinates.size() > size) {
            coordinates.removeFirst();
        }
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public Double getLastCoordinate(){
        return coordinates.getLast();
    }

    public Double get(int i) {
        return coordinates.get(i);
    }

    public int getLight() {
        return coordinates.size();
    }
}