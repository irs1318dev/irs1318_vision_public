package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTablePointWriter extends NetworkTableResultWriterBase<Point>
{
    private final String component;

    private DoublePublisherWrapper xEntry;
    private DoublePublisherWrapper yEntry;

    public NetworkTablePointWriter(
        String component,
        String debugStreamName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(debugStreamName, streamResolutionX, streamResolutionY);

        this.component = component;

        this.xEntry = null;
        this.yEntry = null;
    }

    @Override
    protected void createEntries(NetworkTable table)
    {
        this.xEntry = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".pointX").publish());
        this.yEntry = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".pointY").publish());
    }

    @Override
    public void write(Point point, long captureTime, Mat sourceFrame)
    {
        this.write(point, captureTime);
    }

    @Override
    public void write(Point point, long captureTime)
    {
        if (point == null)
        {
            this.xEntry.set(VisionConstants.MAGIC_NULL_VALUE);
            this.yEntry.set(VisionConstants.MAGIC_NULL_VALUE);
        }
        else
        {
            this.xEntry.set(point.x);
            this.yEntry.set(point.y);
        }

        NetworkTableHelper.flush();

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            if (point != null)
            {
                Logger.write(String.format("Point: %f, %f", point.x, point.y));
            }
            else
            {
                Logger.write("Point not found");
            }
        }
    }
}
