package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import frc1318.vision.IResultWriter;
import frc1318.vision.calculator.DistanceAngleMeasurements;

public class ImageDistanceAngleWriter implements IResultWriter<DistanceAngleMeasurements>
{
    private static final int COUNT_BREAK = 25;
    private final String dirName;

    private int count;
    private int i;

    public ImageDistanceAngleWriter(String dirName)
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
    public void write(DistanceAngleMeasurements measurements, Mat sourceFrame)
    {
        if (this.count++ > ImageDistanceAngleWriter.COUNT_BREAK)
        {
            if (!Imgcodecs.imwrite(String.format("%simage%d.png", this.dirName, this.i), sourceFrame))
            {
                System.out.println("failed to write image!");
            }

            this.i++;
            this.count = 0;
        }

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
