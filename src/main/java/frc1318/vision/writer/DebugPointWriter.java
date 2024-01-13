package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import frc1318.vision.IResultWriter;

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
    public void write(Point point, Mat sourceFrame)
    {
        this.write(point);
    }

    @Override
    public void write(Point point)
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

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
