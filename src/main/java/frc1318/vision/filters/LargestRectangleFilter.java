package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;

public class LargestRectangleFilter implements IContourFilter<RotatedRect>
{
    private final IContourFilter<MatOfPoint> innerFilter;

    private final MatOfPoint2f mop2f;

    public LargestRectangleFilter(double minArea)
    {
        this.innerFilter = new LargestContourFilter(minArea);

        this.mop2f = new MatOfPoint2f();
    }

    @Override
    public RotatedRect filter(List<MatOfPoint> contourList)
    {
        // find the largest contour...
        MatOfPoint largestContour = this.innerFilter.filter(contourList);
        if (largestContour == null)
        {
            return null;
        }

        largestContour.convertTo(this.mop2f, CvType.CV_32FC2);

        RotatedRect largestRectangle = Imgproc.minAreaRect(this.mop2f);

        largestContour.release();

        return largestRectangle;
    }
}
