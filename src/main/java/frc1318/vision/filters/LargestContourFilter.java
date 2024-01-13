package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;

public class LargestContourFilter implements IContourFilter<MatOfPoint>
{
    private final double minArea;

    public LargestContourFilter(double minArea)
    {
        this.minArea = minArea;
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

        return largestContour;
    }
}
