package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ImagePointDrawer extends ImageDrawer<Point>
{
    public ImagePointDrawer()
    {
    }

    @Override
    public void write(Point result, Mat sourceFrame)
    {
        if (result != null)
        {
            Imgproc.circle(sourceFrame, result, 5, new Scalar(0, 0, 255), 2);
        }

        super.write(result, sourceFrame);
    }
}
