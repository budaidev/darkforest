package com.pm.mentor.darkforest.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.RequiredArgsConstructor;
import lombok.val;

public class AngleTests {
	
	private final double testPrecision = 0.0001;

	@ParameterizedTest
	@MethodSource("testSource")
	public void conversionTests(TestSourceItem item) {
		val angle = new Angle(item.rad);
		
		assertEquals(item.rad, angle.rad);
		assertTrue(Math.abs(item.deg-angle.deg) < testPrecision);
	}
	
	private static Stream<TestSourceItem> testSource() {
		return Stream.of(
				new TestSourceItem(Math.PI, 180),
				new TestSourceItem(Math.PI*2, 360),
				new TestSourceItem(Math.PI/2, 90),
				new TestSourceItem(Math.PI + Math.PI/2, 270)
		);		
	}
	
	@RequiredArgsConstructor
	private static class TestSourceItem {
		public final double rad;
		public final double deg;
	}
}
