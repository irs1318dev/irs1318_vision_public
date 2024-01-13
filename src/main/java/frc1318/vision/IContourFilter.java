package frc1318.vision;

import java.util.List;

import org.opencv.core.MatOfPoint;

public interface IContourFilter<TResult>
{
    /**
     * Filter the items in the list and select one based on some criteria
     * @param list list of items to filter
     * @return result of the filter (or null)
     */
    public TResult filter(List<MatOfPoint> list);
}
