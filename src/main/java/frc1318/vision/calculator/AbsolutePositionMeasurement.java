package frc1318.vision.calculator;

public class AbsolutePositionMeasurement
{
    private final double x;
    private final double y;
    private final double z;
    private final double roll;
    private final double pitch;
    private final double yaw;
    private final int tagId;
    private final double decisionMargin;
    private final double error;

    /**
     * Initialize a new instance of the AbsolutePositionMeasurement class.
     * @param x robot offset on field
     * @param y robot offset on field
     * @param z robot offset on field
     * @param yaw angle around z axis (counter-clockwise, degrees)
     * @param pitch angle around y axis (counter-clockwise, degrees)
     * @param roll angle around x axis (counter-clockwise, degrees)
     * @param tagId identity of the AprilTag
     * @param decisionMargin when detecting AprilTag
     * @param error when finding relative position of the AprilTag initially.
     */
    public AbsolutePositionMeasurement(
        double x,
        double y,
        double z,
        double yaw,
        double pitch,
        double roll,
        int tagId,
        double decisionMargin,
        double error)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        this.tagId = tagId;
        this.decisionMargin = decisionMargin;
        this.error = error;
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

    public int getId()
    {
        return this.tagId;
    }

    public double getDecisionMargin()
    {
        return this.decisionMargin;
    }

    public double getError()
    {
        return this.error;
    }
}