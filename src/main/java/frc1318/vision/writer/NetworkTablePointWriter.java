package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;

import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTablePointWriter extends NetworkTableResultWriterBase<Point>
{
    private final String component;

    private DoublePublisher xEntry;
    private DoublePublisher yEntry;

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
        this.xEntry = table.getDoubleTopic(this.component + ".pointX").publish();
        this.yEntry = table.getDoubleTopic(this.component + ".pointY").publish();
    }

    @Override
    public void write(Point point, Mat sourceFrame)
    {
        this.write(point);
    }

    @Override
    public void write(Point point)
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
                System.out.println(String.format("Point: %f, %f", point.x, point.y));
            }
            else
            {
                System.out.println("Point not found");
            }
        }
    }
}
