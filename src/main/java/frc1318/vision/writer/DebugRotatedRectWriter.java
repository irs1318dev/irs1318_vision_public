package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import frc1318.vision.IResultWriter;

public class DebugRotatedRectWriter implements IResultWriter<RotatedRect>
{
    public DebugRotatedRectWriter()
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
    public void write(RotatedRect rotatedRect, Mat sourceFrame)
    {
        this.write(rotatedRect);
    }

    @Override
    public void write(RotatedRect rotatedRect)
    {
        if (rotatedRect != null)
        {
            System.out.println(String.format("Center: %f, %f", rotatedRect.center.x, rotatedRect.center.y));
            System.out.println(String.format("Size: %f, %f", rotatedRect.size.width, rotatedRect.size.height));
            System.out.println(String.format("Angle: %f", rotatedRect.angle));
        }
        else
        {
            System.out.println("Rect not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
