package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;

import frc1318.vision.VisionConstants;
import frc1318.vision.calculator.DistanceAngleMeasurements;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableDistanceAngleWriter extends NetworkTableResultWriterBase<DistanceAngleMeasurements>
{
    private final String component;

    private DoublePublisher distance;
    private DoublePublisher horizontalAngle;

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
        this.distance = table.getDoubleTopic(this.component + ".distance").publish();
        this.horizontalAngle = table.getDoubleTopic(this.component + ".horizontalAngle").publish();
    }

    @Override
    public void write(DistanceAngleMeasurements measurements, Mat sourceFrame)
    {
        this.write(measurements);
    }

    @Override
    public void write(DistanceAngleMeasurements measurements)
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
                System.out.println(String.format("Distance: %f", measurements.getDistance()));
                System.out.println(String.format("Angle: %f", measurements.getHorizontalAngle()));
            }
            else
            {
                System.out.println("Rect not found");
            }
        }
    }
}
