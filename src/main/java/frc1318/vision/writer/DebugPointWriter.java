package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;

public class DebugPointWriter implements IResultWriter<Point>
{
    public DebugPointWriter()
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
    public void write(Point point, long captureTime, Mat sourceFrame)
    {
        this.write(point, captureTime);
    }

    @Override
    public void write(Point point, long captureTime)
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

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
