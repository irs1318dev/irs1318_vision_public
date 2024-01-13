package frc1318.vision.helpers;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ContourHelper
{
    /**
     * Find the center of mass for a contour using Moments.
     * http://docs.opencv.org/3.1.0/d8/d23/classcv_1_1Moments.html
     * @param contour to use
     * @return point representing the center of the contour
     */
    public static Point findCenterOfMass(MatOfPoint contour)
    {
        Moments moments = Imgproc.moments(contour);
        if (moments.get_m00() == 0.0)
        {
            return null;
        }

        return new Point(moments.get_m10() / moments.get_m00(), moments.get_m01() / moments.get_m00());
    }
}
