package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;
import frc1318.vision.calculator.AbsolutePositionMeasurement;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableAbsolutePositionWriter extends NetworkTableResultWriterBase<AbsolutePositionMeasurement>
{
    private final Object lock = new Object();

    private final String component;

    private DoublePublisherWrapper xOffset;
    private DoublePublisherWrapper yOffset;
    private DoublePublisherWrapper zOffset;
    private DoublePublisherWrapper rollAngle;
    private DoublePublisherWrapper pitchAngle;
    private DoublePublisherWrapper yawAngle;
    private DoublePublisherWrapper tagId;
    private DoublePublisherWrapper decisionMargin;
    private DoublePublisherWrapper error;
    // private DoublePublisherWrapper captureTime;

    private long lastCaptureTime;

    public NetworkTableAbsolutePositionWriter(
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
        this.decisionMargin = null;
        this.error = null;
        // this.captureTime = null;

        this.lastCaptureTime = 0L;
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
        this.decisionMargin = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".decisionMargin").publish());
        this.error = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".error").publish());
        // this.captureTime = new DoublePublisherWrapper(table.getDoubleTopic(this.component + ".captureTime").publish());
    }

    @Override
    public void write(AbsolutePositionMeasurement measurement, long captureTime, Mat sourceFrame)
    {
        this.write(measurement, captureTime);
    }

    @Override
    public void write(AbsolutePositionMeasurement measurement, long captureTime)
    {
        synchronized (this.lock)
        {
            if (measurement == null)
            {
                if (lastCaptureTime > this.lastCaptureTime + VisionConstants.ABSOLUTE_POSITION_AVOID_NULL_GAP_TIME)
                {
                    this.xOffset.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.yOffset.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.zOffset.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.rollAngle.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.pitchAngle.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.yawAngle.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.tagId.set((int)VisionConstants.MAGIC_NULL_VALUE);
                    this.decisionMargin.set(VisionConstants.MAGIC_NULL_VALUE);
                    this.error.set(VisionConstants.MAGIC_NULL_VALUE);
                    // this.captureTime.set(captureTime);
                    this.lastCaptureTime = captureTime;
                }
            }
            else
            {
                this.xOffset.set(measurement.getX());
                this.yOffset.set(measurement.getY());
                this.zOffset.set(measurement.getZ());
                this.rollAngle.set(measurement.getRoll());
                this.pitchAngle.set(measurement.getPitch());
                this.yawAngle.set(measurement.getYaw());
                this.tagId.set(measurement.getId());
                this.decisionMargin.set(measurement.getDecisionMargin());
                this.error.set(measurement.getError());
                // this.captureTime.set(captureTime);
                this.lastCaptureTime = captureTime;
            }

            NetworkTableHelper.flush();

            if (VisionConstants.DEBUG && VisionConstants.DEBUG_PRINT_OUTPUT)
            {
                if (measurement != null)
                {
                    Logger.write(String.format("Offset xyz: (%f, %f, %f), Angle ypr: (%f, %f, %f), Tag Id: %d, Decision Margin: %f, Error: %f", measurement.getX(), measurement.getY(), measurement.getZ(), measurement.getYaw(), measurement.getPitch(), measurement.getRoll(), measurement.getId(), measurement.getDecisionMargin(), measurement.getError()));
                    
                }
                else
                {
                    Logger.write("Offset/Angle/Id not found");
                }
            }
        }
    }
}
