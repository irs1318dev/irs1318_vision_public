package frc1318.vision.writer;

import org.opencv.core.Mat;

import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;
import frc1318.vision.calculator.AbsolutePositionMeasurement;

public class DebugAbsolutePositionWriter implements IResultWriter<AbsolutePositionMeasurement>
{
    public DebugAbsolutePositionWriter()
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
    public void write(AbsolutePositionMeasurement measurements, long captureTime, Mat sourceFrame)
    {
        this.write(measurements, captureTime);
    }

    @Override
    public void write(AbsolutePositionMeasurement measurements, long captureTime)
    {
        if (measurements != null)
        {
            Logger.write(String.format("Offset xyz: (%f, %f, %f), Angle ypr: (%f, %f, %f)", measurements.getX(), measurements.getY(), measurements.getZ(), measurements.getYaw(), measurements.getPitch(), measurements.getRoll()));
        }
        else
        {
            Logger.write("Offset/Angle not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
