package org.example.list;

import java.util.LinkedList;
import java.util.List;

public class ObjectTracker {

    private LinkedList<Float> coordinates;
    private int size;

    public ObjectTracker(int size) {
        this.size = size;
        this.coordinates = new LinkedList<>();
    }

    public void addCoordinate(float x) {
        coordinates.add(x);
        if (coordinates.size() > size) {
            coordinates.removeFirst();
        }
    }

    public List<Float> getCoordinates() {
        return coordinates;
    }
}