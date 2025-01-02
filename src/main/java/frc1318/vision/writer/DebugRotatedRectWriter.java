package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;

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
    public void write(RotatedRect rotatedRect, long captureTime, Mat sourceFrame)
    {
        this.write(rotatedRect, captureTime);
    }

    @Override
    public void write(RotatedRect rotatedRect, long captureTime)
    {
        if (rotatedRect != null)
        {
            Logger.write(String.format("Center: %f, %f", rotatedRect.center.x, rotatedRect.center.y));
            Logger.write(String.format("Size: %f, %f", rotatedRect.size.width, rotatedRect.size.height));
            Logger.write(String.format("Angle: %f", rotatedRect.angle));
        }
        else
        {
            Logger.write("Rect not found");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
