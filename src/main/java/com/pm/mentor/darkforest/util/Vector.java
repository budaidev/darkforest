package com.pm.mentor.darkforest.util;

import lombok.val;

public class Vector {
	
	private static final Vector North = new Vector(0, 1);
	
	public final double x;
	public final double y;
	public final double magnitude;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;

		magnitude = Math.sqrt(x*x + y*y);
	}
	
	public double dotProduct(Vector other) {
		return other.x*x + other.y*y;
	}
	
	public Vector normal() {
		return new Vector(x/magnitude, y/magnitude);
	}
	
	public Angle angleBetween(Vector other) {
		return new Angle(Math.acos(Math.min(normal().dotProduct(other.normal()), 1)));
	}
	
	public Angle angleToNorth() {
		val angle = North.angleBetween(this);
		
		return x < 0
			? new Angle(2*Math.PI-angle.rad)
			: angle;
	}
}
