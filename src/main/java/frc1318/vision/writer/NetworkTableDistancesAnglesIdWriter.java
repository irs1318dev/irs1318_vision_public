package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;
import frc1318.vision.calculator.DistancesAnglesIdMeasurements;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableDistancesAnglesIdWriter extends NetworkTableResultWriterBase<DistancesAnglesIdMeasurements>
{
    private final String component;

    private DoublePublisherWrapper xOffset;
    private DoublePublisherWrapper yOffset;
    private DoublePublisherWrapper zOffset;
    private DoublePublisherWrapper rollAngle;
    private DoublePublisherWrapper pitchAngle;
    private DoublePublisherWrapper yawAngle;
    private DoublePublisherWrapper tagId;

    public NetworkTableDistancesAnglesIdWriter(
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
        this.tagId = null;
    }

    @Override
    protected void createEntries(NetworkTable table)
    {
        this.xOffset = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".xOffset").publish());
        this.yOffset = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".yOffset").publish());
        this.zOffset = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".zOffset").publish());
        this.rollAngle = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".rollAngle").publish());
        this.pitchAngle = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".pitchAngle").publish());
        this.yawAngle = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".yawAngle").publish());
        this.tagId = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".tagId").publish());
    }

    @Override
    public void write(DistancesAnglesIdMeasurements measurements, long captureTime, Mat sourceFrame)
    {
        this.write(measurements, captureTime);
    }

    @Override
    public void write(DistancesAnglesIdMeasurements measurements, long captureTime)
    {
        if (measurements == null)
        {
            this.xOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.yOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.zOffset.set(VisionConstants.MAGIC_NULL_VALUE);
            this.rollAngle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.pitchAngle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.yawAngle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.tagId.set((int)VisionConstants.MAGIC_NULL_VALUE);
        }
        else
        {
            this.xOffset.set(measurements.getX());
            this.yOffset.set(measurements.getY());
            this.zOffset.set(measurements.getZ());
            this.rollAngle.set(measurements.getRoll());
            this.pitchAngle.set(measurements.getPitch());
            this.yawAngle.set(measurements.getYaw());
            this.tagId.set(measurements.getId());
        }

        NetworkTableHelper.flush();

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            if (measurements != null)
            {
                Logger.write(String.format("Offset xyz: (%f, %f, %f), Angle ypr: (%f, %f, %f), Tag Id: %d", measurements.getX(), measurements.getY(), measurements.getZ(), measurements.getYaw(), measurements.getPitch(), measurements.getRoll(), measurements.getId()));
                
            }
            else
            {
                Logger.write("Offset/Angle/Id not found");
            }
        }
    }
}
