package frc1318.vision.writer;

import org.opencv.core.Mat;

import frc1318.vision.IResultWriter;
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
    public void write(DistanceAngleMeasurements measurements, Mat sourceFrame)
    {
        this.write(measurements);
    }

    @Override
    public void write(DistanceAngleMeasurements measurements)
    {
        if (measurements != null)
        {
            System.out.println(String.format("Distance: %f, Angle: %f", measurements.getDistance(), measurements.getHorizontalAngle()));
        }
        else
        {
            System.out.println("Not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
