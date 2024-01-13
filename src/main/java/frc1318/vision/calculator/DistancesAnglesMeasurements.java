package frc1318.vision.calculator;

public class DistancesAnglesMeasurements
{
    private final double x;
    private final double y;
    private final double z;
    private final double roll;
    private final double pitch;
    private final double yaw;

    /**
     * Initialize a new instance of the DistancesAnglesMeasurements class.
     * @param x offset of target from robot
     * @param y offset of target from robot
     * @param z offset of target from robot
     * @param yaw angle around z axis (counter-clockwise, degrees)
     * @param pitch angle around y axis (counter-clockwise, degrees)
     * @param roll angle around x axis (counter-clockwise, degrees)
     */
    public DistancesAnglesMeasurements(
        double x,
        double y,
        double z,
        double yaw,
        double pitch,
        double roll)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public double getX()
    {
        return this.x;
    }

    public double getY()
    {
        return this.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public double getYaw()
    {
        return this.yaw;
    }

    public double getPitch()
    {
        return this.pitch;
    }

    public double getRoll()
    {
        return this.roll;
    }
}