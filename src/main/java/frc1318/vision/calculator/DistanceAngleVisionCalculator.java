package frc1318.vision.calculator;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import frc1318.vision.IResultWriter;
import frc1318.vision.helpers.Angle;

public class DistanceAngleVisionCalculator implements IResultWriter<Point>
{
    private final IResultWriter<DistanceAngleMeasurements> writer;

    private final double centerX;
    private final double centerY;
    private final double focalX;
    private final double focalY;

    private final double cameraYaw;
    private final double cameraPitch;
    private final double cameraVerticalOffset;
    private final double cameraDepthOffset;

    private final double targetVerticalOffset;

    private final double cameraToTargetVerticalOffset;

    public DistanceAngleVisionCalculator(
        IResultWriter<DistanceAngleMeasurements> writer,
        double centerX,
        double centerY,
        double focalX,
        double focalY,
        double cameraYaw,
        double cameraPitch,
        double cameraVerticalOffset,
        double cameraDepthOffset,
        double targetVerticalOffset)
    {
        this.writer = writer;

        this.centerX = centerX;
        this.centerY = centerY;
        this.focalX = focalX;
        this.focalY = focalY;
        this.cameraYaw = cameraYaw;
        this.cameraPitch = cameraPitch;
        this.cameraVerticalOffset = cameraVerticalOffset;

        this.cameraDepthOffset = cameraDepthOffset;

        this.targetVerticalOffset = targetVerticalOffset;

        this.cameraToTargetVerticalOffset = this.targetVerticalOffset - this.cameraVerticalOffset;
    }

    public DistanceAngleMeasurements calculate(Point center)
    {
        if (center == null)
        {
            return null;
        }

        double x = center.x;
        double y = center.y;
        double xOffset = x - this.centerX;
        double yOffset = this.centerY - y;
        double horizontalAngle = Angle.atan2d(xOffset, this.focalX) - this.cameraYaw;
        double verticalAngle = Angle.atan2d(yOffset, this.focalY);

        double distance = (this.cameraToTargetVerticalOffset / Angle.tand(verticalAngle + this.cameraPitch)) - this.cameraDepthOffset;

        return new DistanceAngleMeasurements(distance, horizontalAngle);
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public void close()
    {
    }

    @Override
    public void write(Point result, long captureTime, Mat sourceFrame)
    {
        this.writer.write(this.calculate(result), captureTime, sourceFrame);
    }

    @Override
    public void write(Point result, long captureTime)
    {
        this.writer.write(this.calculate(result), captureTime);
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
        this.writer.outputDebugFrame(frame);
    }
}
