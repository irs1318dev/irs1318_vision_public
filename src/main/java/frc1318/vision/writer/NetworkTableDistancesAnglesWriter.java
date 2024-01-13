package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;

import frc1318.vision.VisionConstants;
import frc1318.vision.calculator.DistancesAnglesMeasurements;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableDistancesAnglesWriter extends NetworkTableResultWriterBase<DistancesAnglesMeasurements>
{
    private final String component;

    private DoublePublisher xOffset;
    private DoublePublisher yOffset;
    private DoublePublisher zOffset;
    private DoublePublisher rollAngle;
    private DoublePublisher pitchAngle;
    private DoublePublisher yawAngle;

    public NetworkTableDistancesAnglesWriter(
        String component,
        String debugStreamName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(debugStreamName, streamResolutionX, streamResolutionY);

        this.component = component;

        this.xOffset = null;
        this.yOffset = null;
        this.zOffset = null;
        this.rollAngle = null;
        this.pitchAngle = null;
        this.yawAngle = null;
    }

    @Override
    protected void createEntries(NetworkTable table)
    {
        this.xOffset = table.getDoubleTopic(this.component + ".xOffset").publish();
        this.yOffset = table.getDoubleTopic(this.component + ".yOffset").publish();
        this.zOffset = table.getDoubleTopic(this.component + ".zOffset").publish();
        this.rollAngle = table.getDoubleTopic(this.component + ".rollAngle").publish();
        this.pitchAngle = table.getDoubleTopic(this.component + ".pitchAngle").publish();
        this.yawAngle = table.getDoubleTopic(this.component + ".yawAngle").publish();
    }

    @Override
    public void write(DistancesAnglesMeasurements measurements, Mat sourceFrame)
    {
        this.write(measurements);
    }

    @Override
    public void write(DistancesAnglesMeasurements measurements)
    {
        if (measurements == null)
        {
            this.xOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.yOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.zOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.rollAngle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.pitchAngle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.yawAngle.set(VisionConstants.MAGIC_NULL_VALUE);
        }
        else
        {
            this.xOffset.set(measurements.getX());
            this.yOffset.set(measurements.getY());
            this.zOffset.set(measurements.getZ());
            this.rollAngle.set(measurements.getRoll());
            this.pitchAngle.set(measurements.getPitch());
            this.yawAngle.set(measurements.getYaw());
        }

        NetworkTableHelper.flush();

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            if (measurements != null)
            {
                System.out.println(String.format("Offset xyz: (%f, %f, %f), Angle ypr: (%f, %f, %f)", measurements.getX(), measurements.getY(), measurements.getZ(), measurements.getYaw(), measurements.getPitch(), measurements.getRoll()));
            }
            else
            {
                System.out.println("Offset/Angle not found");
            }
        }
    }
}
