package frc1318.vision.writer;

import org.opencv.core.Mat;

import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;
import frc1318.vision.calculator.DistanceAngleMeasurements;

public class DebugDistanceAngleWriter implements IResultWriter<DistanceAngleMeasurements>
{
    public DebugDistanceAngleWriter()
    {
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
    public void write(DistanceAngleMeasurements measurements, long captureTime, Mat sourceFrame)
    {
        this.write(measurements, captureTime);
    }

    @Override
    public void write(DistanceAngleMeasurements measurements, long captureTime)
    {
        if (measurements != null)
        {
            Logger.write(String.format("Distance: %f, Angle: %f", measurements.getDistance(), measurements.getHorizontalAngle()));
        }
        else
        {
            Logger.write("Not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
