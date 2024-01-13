package frc1318.vision.filters;

import org.opencv.core.Point;

import frc1318.apriltag.AprilTagDetection;
import frc1318.vision.IAprilTagFilter;

public class LargestAprilTagCenterFilter implements IAprilTagFilter<Point>
{
    private final LargestAprilTagFilter innerFilter;

    public LargestAprilTagCenterFilter(double minArea, double minDecisionMargin)
    {
        this.innerFilter = new LargestAprilTagFilter(minArea, minDecisionMargin);
    }

    @Override
    public Point filter(AprilTagDetection[] detections)
    {
        AprilTagDetection largestTag = this.innerFilter.filter(detections);
        if (largestTag == null)
        {
            return null;
        }

        Point center = largestTag.getCenter();
        largestTag.destroy();
        return center;
    }
}
