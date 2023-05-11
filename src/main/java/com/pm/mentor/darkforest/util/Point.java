package com.pm.mentor.darkforest.util;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public Point move(double degreeRad, double distance) {
        int x = (int) (this.getX() + distance * Math.cos(degreeRad));
        int y = (int) (this.getY() + distance * Math.sin(degreeRad));
        return new Point(x, y);
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
    }
}
