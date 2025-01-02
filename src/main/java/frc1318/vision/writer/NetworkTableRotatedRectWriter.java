package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableRotatedRectWriter extends NetworkTableResultWriterBase<RotatedRect>
{
    private final String component;

    private DoublePublisherWrapper width;
    private DoublePublisherWrapper height;
    private DoublePublisherWrapper angle;
    private DoublePublisherWrapper pointX;
    private DoublePublisherWrapper pointY;

    public NetworkTableRotatedRectWriter(
        String component,
        String debugStreamName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(debugStreamName, streamResolutionX, streamResolutionY);

        this.component = component;

        this.width = null;
        this.height = null;
        this.angle = null;
        this.pointX = null;
        this.pointY = null;
    }

    @Override
    protected void createEntries(NetworkTable table)
    {
        this.width = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".width").publish());
        this.height = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".height").publish());
        this.angle = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".angle").publish());
        this.pointX = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".pointX").publish());
        this.pointY = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".pointY").publish());
    }

    @Override
    public void write(RotatedRect rotatedRect, long captureTime, Mat sourceFrame)
    {
        this.write(rotatedRect, captureTime);
    }

    @Override
    public void write(RotatedRect rotatedRect, long captureTime)
    {
        if (rotatedRect == null)
        {
            this.pointX.set(VisionConstants.MAGIC_NULL_VALUE);
            this.pointY.set(VisionConstants.MAGIC_NULL_VALUE);
            this.angle.set(VisionConstants.MAGIC_NULL_VALUE);
            this.width.set(VisionConstants.MAGIC_NULL_VALUE);
            this.height.set(VisionConstants.MAGIC_NULL_VALUE);
        }
        else
        {
            this.pointX.set(rotatedRect.center.x);
            this.pointY.set(rotatedRect.center.y);
            this.angle.set(rotatedRect.angle);
            this.width.set(rotatedRect.size.width);
            this.height.set(rotatedRect.size.height);
        }

        NetworkTableHelper.flush();

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            if (rotatedRect != null)
            {
                Logger.write(String.format("Center: %f, %f", rotatedRect.center.x, rotatedRect.center.y));
                Logger.write(String.format("Size: %f, %f", rotatedRect.size.width, rotatedRect.size.height));
                Logger.write(String.format("Angle: %f", rotatedRect.angle));
            }
            else
            {
                Logger.write("Rect not found");
            }
        }
    }
}
