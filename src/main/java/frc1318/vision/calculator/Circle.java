package frc1318.vision.calculator;

import org.opencv.core.Point;

public class Circle
{
    private final Point center;
    private final double radius;

    public Circle(
        Point center,
        double radius)
    {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter()
    {
        return this.center;
    }

    public double getRadius()
    {
        return this.radius;
    }
}