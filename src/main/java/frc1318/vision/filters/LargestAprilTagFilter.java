package frc1318.vision.filters;

import frc1318.apriltag.AprilTagDetection;
import frc1318.vision.IAprilTagFilter;
import frc1318.vision.helpers.AreaHelper;

public class LargestAprilTagFilter implements IAprilTagFilter<AprilTagDetection>
{
    private final double minArea;
    private final double minDecisionMargin;

    public LargestAprilTagFilter(double minArea, double minDecisionMargin)
    {
        this.minArea = minArea;
        this.minDecisionMargin = minDecisionMargin;
    }

    @Override
    public AprilTagDetection filter(AprilTagDetection[] detections)
    {
        // find the largest contour...
        double maxArea = 0.0;
        AprilTagDetection largestTag = null;
        for (AprilTagDetection tagDetection : detections)
        {
            if (tagDetection.getDecisionMargin() >= this.minDecisionMargin)
            {
                double area = AreaHelper.findArea(tagDetection.getVertices());
                if (area >= this.minArea &&
                    area > maxArea)
                {
                    if (largestTag != null)
                    {
                        largestTag.destroy();
                    }

                    maxArea = area;
                    largestTag = tagDetection;
                }
            }
        }

        if (largestTag == null)
        {
            return null;
        }

        return largestTag;
    }
}
