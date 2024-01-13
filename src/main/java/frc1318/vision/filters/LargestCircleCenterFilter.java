package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import frc1318.vision.IContourFilter;
import frc1318.vision.helpers.ContourHelper;

public class LargestCircleCenterFilter implements IContourFilter<Point>
{
    private final IContourFilter<MatOfPoint> innerFilter;

    public LargestCircleCenterFilter(double minArea, double minRatio)
    {
        this.innerFilter = new LargestCircleContourFilter(minArea, minRatio);
    }

    @Override
    public Point filter(List<MatOfPoint> contourList)
    {
        // find the largest circle contour...
        MatOfPoint largestCircleContour = this.innerFilter.filter(contourList);
        if (largestCircleContour == null)
        {
            return null;
        }

        Point result = ContourHelper.findCenterOfMass(largestCircleContour);
        largestCircleContour.release();
        return result;
    }
}
