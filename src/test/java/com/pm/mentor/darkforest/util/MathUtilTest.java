package com.pm.mentor.darkforest.util;

import static com.pm.mentor.darkforest.util.MathUtil.getLineRectangleIntersection;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MathUtilTest {

    @Test
    public void testPointIsInsideOrNot(){
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 10);
        Point p3 = new Point(10, 0);
        Point p4 = new Point(5, 5);
        Point p5 = new Point(5, 15);
        Point p6 = new Point(15, 5);
        Point p7 = new Point(15, 15);

        assertTrue(MathUtil.isInsideTheTriangle(p1, p2, p3, p4));
        assertFalse(MathUtil.isInsideTheTriangle(p1, p2, p3, p5));
        assertFalse(MathUtil.isInsideTheTriangle(p1, p2, p3, p6));
        assertFalse(MathUtil.isInsideTheTriangle(p1, p2, p3, p7));

    }

    @Test
    public void testIntersectionForLine(){
        Point result = getLineRectangleIntersection(new Point(5,5), Math.PI *1.5, 10, 10);
        System.out.println(result);
    }
}
