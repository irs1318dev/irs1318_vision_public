package frc1318.vision.calculator;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import frc1318.vision.IResultWriter;
import frc1318.vision.helpers.Angle;

public class OffsetDistanceAngleVisionCalculator implements IResultWriter<Point>
{
    private final IResultWriter<OffsetDistanceAngleMeasurements> writer;

    private final double centerX;
    private final double centerY;
    private final double focalX;
    private final double focalY;

    private final double cameraYaw;
    private final double cameraPitch;
    private final double cameraHorizontalOffset;
    private final double cameraVerticalOffset;
    private final double cameraDepthOffset;

    private final double targetHorizontalOffset;
    private final double targetVerticalOffset;

    private final double cameraToTargetVerticalOffset;
    private final double cameraToTargetHorizontalOffset;

    public OffsetDistanceAngleVisionCalculator(
        IResultWriter<OffsetDistanceAngleMeasurements> writer,
        double centerX,
        double centerY,
        double focalX,
        double focalY,
        double cameraYaw,
        double cameraHorizontalOffset,
        double cameraPitch,
        double cameraVerticalOffset,
        double cameraDepthOffset,
        double targetHorizontalOffset,
        double targetVerticalOffset)
    {
        this.writer = writer;

        this.centerX = centerX;
        this.centerY = centerY;
        this.focalX = focalX;
        this.focalY = focalY;
        this.cameraYaw = cameraYaw;
        this.cameraHorizontalOffset = cameraHorizontalOffset;
        this.cameraPitch = cameraPitch;
        this.cameraVerticalOffset = cameraVerticalOffset;

        this.cameraDepthOffset = cameraDepthOffset;

        this.targetHorizontalOffset = targetHorizontalOffset;
        this.targetVerticalOffset = targetVerticalOffset;

        this.cameraToTargetVerticalOffset = this.targetVerticalOffset - this.cameraVerticalOffset;
        this.cameraToTargetHorizontalOffset = this.cameraHorizontalOffset - this.targetHorizontalOffset;
    }

    public OffsetDistanceAngleMeasurements calculate(Point center)
    {
        if (center == null)
        {
            return null;
        }

        double x = center.x;
        double y = center.y;
        double xOffset = x - this.centerX;
        double yOffset = this.centerY - y;
        double measuredAngleX = Angle.atand(xOffset / this.focalX) - this.cameraYaw;
        double measuredAngleY = Angle.atand(yOffset / this.focalY);

        double measuredCameraDistance = this.cameraToTargetVerticalOffset / Angle.atand(measuredAngleY + this.cameraPitch);
        double measuredRobotDistance = measuredCameraDistance * Angle.cosd(measuredAngleX) - this.cameraDepthOffset;
        
        double desiredAngleX = Angle.asind(this.cameraToTargetHorizontalOffset / measuredCameraDistance);
        return new OffsetDistanceAngleMeasurements(x, y, xOffset, yOffset, measuredAngleX, measuredAngleY, measuredCameraDistance, measuredRobotDistance, desiredAngleX);
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
    public void write(Point result, long captureTime, Mat mat)
    {
        this.writer.write(this.calculate(result), captureTime, mat);
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
