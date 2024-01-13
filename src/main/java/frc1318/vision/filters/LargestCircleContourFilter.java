package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;

public class LargestCircleContourFilter implements IContourFilter<MatOfPoint>
{
    private final double minArea;
    private final double minRatio;

    private final MatOfPoint2f mop2f;
    private final Point center;
    private final float[] radius;

    public LargestCircleContourFilter(double minArea, double minRatio)
    {
        this.minArea = minArea;
        this.minRatio = minRatio;

        this.mop2f = new MatOfPoint2f();
        this.center = new Point();
        this.radius = new float[1];
    }

    @Override
    public MatOfPoint filter(List<MatOfPoint> contourList)
    {
        // find the largest contour...
        double largestContourArea = 0.0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contourList)
        {
            double area = Imgproc.contourArea(contour);
            if (area >= this.minArea && area > largestContourArea)
            {
                contour.convertTo(this.mop2f, CvType.CV_32FC2);

                Imgproc.minEnclosingCircle(this.mop2f, this.center, this.radius);
                double minCircleArea = this.radius[0] * this.radius[0] * Math.PI;
                double ratio = area / minCircleArea;
                if (ratio >= this.minRatio)
                {
                    if (largestContour != null)
                    {
                        largestContour.release();
                    }

                    largestContour = contour;
                    largestContourArea = area;
                }
                else
                {
                    contour.release();
                }
            }
            else
            {
                contour.release();
            }
        }

        return largestContour;
    }
}
