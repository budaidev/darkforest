package com.pm.mentor.darkforest.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.RequiredArgsConstructor;
import lombok.val;

public class VectorTests {
	private final double testPrecision = 0.0001;

	@ParameterizedTest
	@MethodSource("testSource")
	public void angleToNorthTest(VectorAnglesData item) {
		val vec = new Vector(item.x1, item.y1);
		
		val result = vec.angleToNorth();
		
		assertTrue(Math.abs(item.explectedAngle-result.deg) < testPrecision);
	}
	
	private static Stream<VectorAnglesData> testSource() {
		return Stream.of(
				new VectorAnglesData(0, -1, 0),
				new VectorAnglesData(1, 0, 90),
				new VectorAnglesData(0, 1, 180),
				new VectorAnglesData(-1, 0, 270)
		);		
	}
	
	@RequiredArgsConstructor
	private static class VectorAnglesData {
		public final double x1;
		public final double y1;
		
		public final double explectedAngle;
	}
}
