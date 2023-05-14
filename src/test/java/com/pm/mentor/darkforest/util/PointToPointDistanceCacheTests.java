package com.pm.mentor.darkforest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import lombok.val;

public class PointToPointDistanceCacheTests {
	
	@Test
	public void test1() {
		Point P1 = new Point(12, 45);
		Point P2 = new Point(3, 6);
		
		val result1 = PointToPointDistanceCache.distance(P1, P2);
		assertEquals(P1.distance(P2), result1);
		val result2 = PointToPointDistanceCache.distance(P1, P2);
		assertEquals(P1.distance(P2), result2);
	}
	
	@Test
	public void test2() {
		Point P2 = new Point(3, 6);
		Point P3 = new Point(-12, 45);
		
		val result3 = PointToPointDistanceCache.distance(P3, P2);
		assertEquals(P3.distance(P2), result3);
		val result4 = PointToPointDistanceCache.distance(P3, P2);
		assertEquals(P3.distance(P2), result4);
	}
	
	@Test
	public void test3() {
		Point P1 = new Point(12, 45);
		Point P2 = new Point(3, 6);
		Point P3 = new Point(-12, 45);
		
		val result1 = PointToPointDistanceCache.distance(P1, P2);
		assertEquals(P1.distance(P2), result1);
		
		val result2 = PointToPointDistanceCache.distance(P2, P3);
		assertEquals(P3.distance(P2), result2);
		
		val result3 = PointToPointDistanceCache.distance(P3, P2);
		assertEquals(P3.distance(P2), result3);
	}
	
	@Disabled
	@Test
	public void collisionDetection() {
		for (int x1 = 0; x1 < 127; x1++) {
			for (int y1 = 0; y1 < 63; y1++) {
				for (int x2 = 0; x2 < 127; x2++) {
					for (int y2 = 0; y2 < 63; y2++) {
						val P1 = new Point(x1, y1);
						val P2 = new Point(x2, y2);
						val result = PointToPointDistanceCache.distance(P1, P2);
						assertEquals(P1.distance(P2), result);
					}
				}
			}
		}
	}
}
