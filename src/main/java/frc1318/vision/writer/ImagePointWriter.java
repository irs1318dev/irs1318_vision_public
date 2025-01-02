package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;

public class ImagePointWriter implements IResultWriter<Point>
{
    private static final int COUNT_BREAK = 0;
    private final String dirName;

    private int count;
    private int i;

    public ImagePointWriter(String dirName)
    {
        if (dirName.endsWith("/"))
        {
            this.dirName = dirName;
        }
        else
        {
            this.dirName = dirName + "/";
        }

        this.count = 0;
        this.i = 0;
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
        if (this.count++ > ImagePointWriter.COUNT_BREAK)
        {
            if (!Imgcodecs.imwrite(String.format("%simage%d.png", this.dirName, this.i), sourceFrame))
            {
                Logger.write("failed to write image!");
            }
            else
            {
                if (point != null)
                {
                    Imgproc.circle(sourceFrame, point, 5, new Scalar(0, 0, 255), 2);

                    if (!Imgcodecs.imwrite(String.format("%simage%d.redrawn.png", this.dirName, this.i), sourceFrame))
                    {
                        Logger.write("failed to write redrawn image!");
                    }
                }
            }

            this.i++;
            this.count = 0;
        }

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
