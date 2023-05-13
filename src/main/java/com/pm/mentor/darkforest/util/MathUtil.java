package com.pm.mentor.darkforest.util;

public class MathUtil {

    static double area(Point p1, Point p2, Point p3)
    {
        return Math.abs((p1.getX()*(p2.getY()-p3.getY()) + p2.getX()*(p3.getY()-p1.getY())+
                p3.getX()*(p1.getY()-p2.getY()))/2.0);
    }

    public static boolean isInsideTheTriangle(Point p1, Point p2, Point p3, Point pt)
    {
        System.out.println("p1: " + p1 + " p2: " + p2 + " p3: " + p3 + " pt: " + pt);
        /* Calculate area of triangle ABC */
        double A = area (p1, p2, p3);

        /* Calculate area of triangle PBC */
        double A1 = area (pt, p2, p3);

        /* Calculate area of triangle PAC */
        double A2 = area (p1, pt, p3);

        /* Calculate area of triangle PAB */
        double A3 = area (p1, p2, pt);

        /* Check if sum of A1, A2 and A3 is same as A */
        return (A == A1 + A2 + A3);
    }

    public static Point getLineRectangleIntersection(Point start, double angle, int width, int height) {
        int x1 = start.getX();
        int y1 = start.getY();
        double x2 = Math.round(Math.cos(angle));
        double y2 = Math.round(Math.sin(angle));

        // Calculate intersection with top line of rectangle
        int topIntersectionX = (int) Math.round(x1 + (y1 / y2) * (x2 - x1));
        if (topIntersectionX < 0 || topIntersectionX > width) {
            topIntersectionX = Integer.MIN_VALUE;
        }

        // Calculate intersection with left line of rectangle
        int leftIntersectionY = (int) Math.round(y1 + (x1 / x2) * (y2 - y1));
        if (leftIntersectionY < 0 || leftIntersectionY > height) {
            leftIntersectionY = Integer.MIN_VALUE;
        }

        // Calculate intersection with right line of rectangle
        int rightIntersectionX = (int) Math.round(x1 + ((height - y1) / (double) (y2 - y1)) * (x2 - x1));
        int rightIntersectionY = height;
        if (rightIntersectionX < 0 || rightIntersectionX > width) {
            rightIntersectionX = Integer.MIN_VALUE;
        }

        // Calculate intersection with bottom line of rectangle
        int bottomIntersectionX = width;
        int bottomIntersectionY = (int) Math.round(y1 + ((width - x1) / (double) (x2 - x1)) * (y2 - y1));
        if (bottomIntersectionY < 0 || bottomIntersectionY > height) {
            bottomIntersectionY = Integer.MIN_VALUE;
        }



        // Choose the intersection point that is within the rectangle
        Point intersection;
        if (topIntersectionX == Integer.MIN_VALUE && leftIntersectionY == Integer.MIN_VALUE && rightIntersectionX == Integer.MIN_VALUE && bottomIntersectionY == Integer.MIN_VALUE) {
            intersection = null; // Line does not intersect rectangle
        } else if (topIntersectionX != Integer.MIN_VALUE) {
            intersection = new Point(topIntersectionX, 0);
        } else if (leftIntersectionY != Integer.MIN_VALUE) {
            intersection = new Point(0, leftIntersectionY);
        } else if (rightIntersectionX != Integer.MIN_VALUE) {
            intersection = new Point(rightIntersectionX, height);
        } else {
            intersection = new Point(width, bottomIntersectionY);
        }

        return intersection;
    }
}
