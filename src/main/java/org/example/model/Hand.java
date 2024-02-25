package org.example.model;

import org.example.list.Movements;
import org.opencv.core.Rect2d;

import java.util.UUID;

public class Hand extends Rect2d {
    private final String name;
    private final ObjectTracker coordinatesX;
    private final ObjectTracker  coordinatesY;
    private final int sizeOfListOfCoordinates = 5;
    private final int threshold;
    private final int errorRate = 50;


    public Hand(int x, int y, double width, double height, int threshold) {
        super(x, y, width, height);
        this.name = "Object " + UUID.randomUUID().toString().substring(0, 5); // Генерируем случайное имя
        coordinatesX = new ObjectTracker(sizeOfListOfCoordinates);
        coordinatesY = new ObjectTracker(sizeOfListOfCoordinates);
        addCoordinate(x,y);
        this.threshold = threshold;
    }

    public Movements addCoordinate(int x, int y) {

        coordinatesX.addCoordinate(x);
        coordinatesY.addCoordinate(y);

        this.x = x;
        this.y = y;

        int diffX = this.getCoordinateX();
        int diffY = this.getCoordinateY();

        if(diffX!= 0 && diffX>threshold) {
            return (Movements.left);
        } else if (diffX!= 0 && diffX<-threshold ) {
            return (Movements.right);
        }else if(diffY!= 0 && diffY>threshold) {
            return (Movements.up);
        } else if (diffY!= 0 && diffY<-threshold) {
            return (Movements.down);
        }
        return null;
    }

    public int getCoordinateX(){
        if(coordinatesX.getSize() == sizeOfListOfCoordinates) {
            return coordinatesX.getCoordinates();
        }
        return 0;
    }

    public int getCoordinateY(){
        if(coordinatesY.getSize() == sizeOfListOfCoordinates) {
            return coordinatesY.getCoordinates();
        }
        return 0;
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


    //здесь добавлена новая погрешность(errorRate) эта погрешность нужна чтобы не ввонить новые коорлинаты в лист если их разница слишком мала
    public Movements update(int left, int top, int width, int height) {
        if((getCoordinateX() == left || getCoordinateY() == top) ||
                (Math.abs(getCoordinateX() - left) <= errorRate && Math.abs(getCoordinateY() - top) <= errorRate) ||
                (coordinatesX.getSize() ==0 || coordinatesY.getSize() ==0 ))
        {
            return null;
        }
        else {
            this.width = width;
            this.height = height;
            var move = addCoordinate(left, top);
            return move;
        }
    }

    private void compareCoor(int width, int height){
        System.out.println("Update object \n");
        System.out.printf("OLD left coor is - %d \n",getCoordinateX());
        System.out.printf("OLD top coor is - %d \n",getCoordinateY());
        System.out.printf("OLD wight coor is - %d \n",width);
        System.out.printf("OLD height coor is - %d \n",height);
        System.out.println("O____________________0");
        System.out.printf("NEW left coor is - %d \n",getCoordinateX());
        System.out.printf("NEW top coor is - %d \n",getCoordinateY());
        System.out.printf("NEW wight coor is - %d \n",width);
        System.out.printf("NEW height coor is - %d \n",height);
    }
}
