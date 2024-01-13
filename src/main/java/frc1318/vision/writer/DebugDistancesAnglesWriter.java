package frc1318.vision.writer;

import org.opencv.core.Mat;

import frc1318.vision.IResultWriter;
import frc1318.vision.calculator.DistancesAnglesMeasurements;

public class DebugDistancesAnglesWriter implements IResultWriter<DistancesAnglesMeasurements>
{
    public DebugDistancesAnglesWriter()
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
    public void write(DistancesAnglesMeasurements measurements, Mat sourceFrame)
    {
        this.write(measurements);
    }

    @Override
    public void write(DistancesAnglesMeasurements measurements)
    {
        if (measurements != null)
        {
            System.out.println(String.format("Offset xyz: (%f, %f, %f), Angle ypr: (%f, %f, %f)", measurements.getX(), measurements.getY(), measurements.getZ(), measurements.getYaw(), measurements.getPitch(), measurements.getRoll()));
        }
        else
        {
            System.out.println("Offset/Angle not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
