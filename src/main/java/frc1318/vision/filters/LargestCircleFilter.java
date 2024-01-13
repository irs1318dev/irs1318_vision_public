package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;
import frc1318.vision.calculator.Circle;

public class LargestCircleFilter implements IContourFilter<Circle>
{
    private final double minArea;
    private final double minRatio;

    private final MatOfPoint2f mop2f;
    private Point center;
    private float radius;

    public LargestCircleFilter(double minArea, double minRatio)
    {
        this.minArea = minArea;
        this.minRatio = minRatio;

        this.mop2f = new MatOfPoint2f();
        this.center = new Point();
        this.radius = -0.0f;
    }

    @Override
    public Circle filter(List<MatOfPoint> contourList)
    {
        Point tempCenter = new Point();
        float[] radiusArray = new float[1];

        // find the largest contour...
        double largestContourArea = 0.0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contourList)
        {
            double area = Imgproc.contourArea(contour);
            if (area >= this.minArea && area > largestContourArea)
            {
                contour.convertTo(this.mop2f, CvType.CV_32FC2);

                Imgproc.minEnclosingCircle(this.mop2f, tempCenter, radiusArray);
                double minCircleArea =  radiusArray[0] * radiusArray[0] * Math.PI;
                double ratio = area / minCircleArea;
                if (ratio >= this.minRatio)
                {
                    if (largestContour != null)
                    {
                        largestContour.release();
                    }

                    largestContour = contour;
                    largestContourArea = area;
                    this.center = tempCenter.clone();
                    this.radius = radiusArray[0];
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

        return new Circle(this.center, this.radius);
    }
}
