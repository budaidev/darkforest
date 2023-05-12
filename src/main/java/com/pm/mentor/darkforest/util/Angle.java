package com.pm.mentor.darkforest.util;

public class Angle {
	public final double rad;
	public final double deg;
	
	public Angle(double rad) {
		this.rad = rad;
		this.deg = rad/Math.PI * 180;
	}
}
