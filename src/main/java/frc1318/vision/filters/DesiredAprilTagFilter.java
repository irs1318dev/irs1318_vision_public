package frc1318.vision.filters;

import java.util.List;

import frc1318.apriltag.AprilTagDetection;
import frc1318.vision.IAprilTagFilter;
import frc1318.vision.IController;
import frc1318.vision.helpers.AreaHelper;

public class DesiredAprilTagFilter implements IAprilTagFilter<AprilTagDetection>
{
    private final double minArea;
    private final double minDecisionMargin;
    private final IController controller;

    public DesiredAprilTagFilter(double minArea, double minDecisionMargin, IController controller)
    {
        this.controller = controller;
        this.minArea = minArea;
        this.minDecisionMargin = minDecisionMargin;
    }

    @Override
    public AprilTagDetection filter(AprilTagDetection[] detections)
    {
        // check the desired april tag...
        // if it is 0, then we want to retrieve any tag
        List<Integer> desiredAprilTagIds = this.controller.getDesiredTarget();

        // find the largest contour...
        double maxArea = 0.0;
        AprilTagDetection largestTag = null;
        for (AprilTagDetection tagDetection : detections)
        {
            if (tagDetection.getDecisionMargin() >= this.minDecisionMargin &&
                (desiredAprilTagIds == null || desiredAprilTagIds.contains(tagDetection.getId())))
            {
                double area = AreaHelper.findArea(tagDetection.getVertices());
                if (area >= this.minArea &&
                    area > maxArea)
                {
                    if (largestTag != null)
                    {
                        largestTag.release();
                    }

                    maxArea = area;
                    largestTag = tagDetection;
                }
            }
        }

        return largestTag;
    }
}

