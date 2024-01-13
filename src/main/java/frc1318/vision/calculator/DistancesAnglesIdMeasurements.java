package frc1318.vision.calculator;

public class DistancesAnglesIdMeasurements extends DistancesAnglesMeasurements
{
    private final int id;

    /**
     * Initialize a new instance of the DistancesAnglesMeasurements class.
     * @param x offset of target from robot
     * @param y offset of target from robot
     * @param z offset of target from robot
     * @param yaw angle around z axis (counter-clockwise, degrees)
     * @param pitch angle around y axis (counter-clockwise, degrees)
     * @param roll angle around x axis (counter-clockwise, degrees)
     */
    public DistancesAnglesIdMeasurements(
        double x,
        double y,
        double z,
        double yaw,
        double pitch,
        double roll,
        int id)
    {
        super(x, y, z, yaw, pitch, roll);
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }
}