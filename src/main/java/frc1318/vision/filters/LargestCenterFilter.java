package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

import frc1318.vision.IContourFilter;
import frc1318.vision.helpers.ContourHelper;

public class LargestCenterFilter implements IContourFilter<Point>
{
    private final IContourFilter<MatOfPoint> innerFilter;

    public LargestCenterFilter(double minArea)
    {
        this.innerFilter = new LargestContourFilter(minArea);
    }

    @Override
    public Point filter(List<MatOfPoint> contourList)
    {
        // find the largest contour...
        MatOfPoint largestContour = this.innerFilter.filter(contourList);
        if (largestContour == null)
        {
            return null;
        }

        Point result = ContourHelper.findCenterOfMass(largestContour);
        largestContour.release();
        return result;
    }
}
