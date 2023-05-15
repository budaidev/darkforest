package com.pm.mentor.darkforest.util;

import java.util.HashMap;
import java.util.Map;

import lombok.val;

public class PointToPointDistanceCache {

	private static final int MaskX1 = 0xFF000000;
	private static final int MaskY1 = 0x00FF0000;
	private static final int MaskX2 = 0x0000FF00;
	private static final int MaskY2 = 0x000000FF;
	
	private static Map<Integer, Double> cache = new HashMap<>();
	
	public static double distance(Point a, Point b) {
		val key = calculateKey(a.getX(), a.getY(), b.getX(), b.getY());
		
		if (!cache.containsKey(key)) {
			cache.put(key, a.distance(b));
		}
		
		return cache.get(key);
	}
	
	public static double distance(Point p1, long x2, long y2) {
		val p2 = new Point(x2, y2);
		
		return distance(p1, p2);
	}
	
	private static int calculateKey(int x1, int y1, int x2, int y2 ) {
		return (x1 << 24 & MaskX1) | (y1 << 16 & MaskY1) | (x2 << 8 & MaskX2) | (y2 & MaskY2) ;
	}
}
