package frc1318.vision;

import frc1318.apriltag.AprilTagDetection;

public interface IAprilTagFilter<TResult>
{
    /**
     * Filter the items in the array and select one based on some criteria
     * @param detections array of items to filter
     * @return result of the filter (or null)
     */
    public TResult filter(AprilTagDetection[] detections);
}
