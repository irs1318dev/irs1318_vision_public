package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;
import frc1318.vision.calculator.DistanceAngleMeasurements;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableDistanceAngleWriter extends NetworkTableResultWriterBase<DistanceAngleMeasurements>
{
    private final String component;

    private DoublePublisherWrapper distance;
    private DoublePublisherWrapper horizontalAngle;

    public NetworkTableDistanceAngleWriter(
        String component,
        String debugStreamName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(debugStreamName, streamResolutionX, streamResolutionY);

        this.component = component;

        this.distance = null;
        this.horizontalAngle = null;
    }

    @Override
    protected void createEntries(NetworkTable table)
    {
        this.distance = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".distance").publish());
        this.horizontalAngle = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".horizontalAngle").publish());
    }

    @Override
    public void write(DistanceAngleMeasurements measurements, long captureTime, Mat sourceFrame)
    {
        this.write(measurements, captureTime);
    }

    @Override
    public void write(DistanceAngleMeasurements measurements, long captureTime)
    {
        if (measurements == null)
        {
            this.distance.set(VisionConstants.MAGIC_NULL_VALUE);
            this.horizontalAngle.set(VisionConstants.MAGIC_NULL_VALUE);
        }
        else
        {
            this.distance.set(measurements.getDistance());
            this.horizontalAngle.set(measurements.getHorizontalAngle());
        }

        NetworkTableHelper.flush();

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            if (measurements != null)
            {
                Logger.write(String.format("Distance: %f", measurements.getDistance()));
                Logger.write(String.format("Angle: %f", measurements.getHorizontalAngle()));
            }
            else
            {
                Logger.write("Rect not found");
            }
        }
    }
}
