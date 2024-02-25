package org.example.model;

import java.util.LinkedList;

public class ObjectTracker {

    private LinkedList<Integer> coordinates;
    private int size;

    public ObjectTracker(int size) {
        this.size = size;
        this.coordinates = new LinkedList<>();
    }

    public void addCoordinate(int x) {
        coordinates.add(x);
        if (coordinates.size() > size) {
            coordinates.removeFirst();
        }
    }

    public int getCoordinates() {
        int sum = 0;
        for (int i = 0; i < coordinates.size() - 1; i++) {
            sum += (coordinates.get(i) - coordinates.get(i + 1));
        }
        return sum;
    }
    public int getLastCoordinate(){
        return coordinates.getLast();
    }

    public int get(int i) {
        return coordinates.get(i);
    }

    public int getSize() {
        return coordinates.size();
    }
}