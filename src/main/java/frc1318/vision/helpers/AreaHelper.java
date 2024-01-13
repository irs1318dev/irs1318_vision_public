package frc1318.vision.helpers;

import org.opencv.core.Point;

public class AreaHelper
{
    /**
     * Calculate area given points in order using the Trapezoid formula form of the Shoelace formula
     * @param points in order (clockwise or counter-clockwise)
     * @return area
     */
    public static double findArea(Point[] points)
    {
        if (points == null || points.length < 3)
        {
            return 0.0;
        }

        double area = 0.0;
        int j = points.length - 1;
        for (int i = 0; i < points.length; i++)
        {
            area += (points[j].x + points[i].x) * (points[j].y - points[i].y);
            j = i;
        }

        return Math.abs(area) / 2.0;
    }
}
