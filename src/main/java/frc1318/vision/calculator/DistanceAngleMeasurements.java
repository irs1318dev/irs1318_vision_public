package frc1318.vision.calculator;

public class DistanceAngleMeasurements
{
    private final double distance;
    private final double horizontalAngle;

    public DistanceAngleMeasurements(
        double distance,
        double horizontalAngle)
    {
        this.distance = distance;
        this.horizontalAngle = horizontalAngle;
    }

    public double getDistance()
    {
        return this.distance;
    }

    public double getHorizontalAngle()
    {
        return this.horizontalAngle;
    }
}