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

    public Point(long x, long y) {
    	this.x = (int)x;
        this.y = (int)y;
	}

	public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

    public Point move(double degreeRad, double distance) {
        double adjustedDegreeRad = degreeRad - Math.PI / 2; // Adjusting 90 degrees clockwise

        while (adjustedDegreeRad < 0) {
            adjustedDegreeRad += 2 * Math.PI;
        }

        int x = (int) (this.getX() + distance * Math.cos(adjustedDegreeRad));
        int y = (int) (this.getY() + distance * Math.sin(adjustedDegreeRad));
        return new Point(x, y);
    }
    
    public Vector minus(Point other) {
    	return new Vector(x-other.getX(), y-other.getY());
    }

    public double distance(Point other) {
        return Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
    }

    public double distance(long x, long y) {
        return Math.sqrt(Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2));
    }
}
