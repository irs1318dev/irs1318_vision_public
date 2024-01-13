package frc1318.vision.calculator;

public class OffsetDistanceAngleMeasurements
{
    private final double x;
    private final double y;
    private final double xOffset;
    private final double yOffset;
    private final double measuredAngleX;
    private final double measuredAngleY;
    private final double measuredCameraDistance;
    private final double measuredRobotDistance;
    private final double desiredAngleX;

    public OffsetDistanceAngleMeasurements(
        double x,
        double y,
        double xOffset,
        double yOffset,
        double measuredAngleX,
        double measuredAngleY,
        double measuredCameraDistance,
        double measuredRobotDistance,
        double desiredAngleX)
    {
        this.x = x;
        this.y = y;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.measuredAngleX = measuredAngleX;
        this.measuredAngleY = measuredAngleY;
        this.measuredCameraDistance = measuredCameraDistance;
        this.measuredRobotDistance = measuredRobotDistance;
        this.desiredAngleX = desiredAngleX;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getXOffset()
    {
        return this.xOffset;
    }

    public double getYOffset()
    {
        return this.yOffset;
    }

    public double getMeasuredAngleX()
    {
        return this.measuredAngleX;
    }

    public double getMeasuredAngleY()
    {
        return this.measuredAngleY;
    }

    public double getMeasuredCameraDistance()
    {
        return this.measuredCameraDistance;
    }

    public double getMeasuredRobotDistance()
    {
        return this.measuredRobotDistance;
    }

    public double getDesiredAngleX()
    {
        return this.desiredAngleX;
    }
}