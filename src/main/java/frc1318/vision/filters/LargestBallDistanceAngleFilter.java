package frc1318.vision.filters;

import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;
import frc1318.vision.calculator.DistanceAngleMeasurements;
import frc1318.vision.calculator.DistanceAngleVisionCalculator;

public class LargestBallDistanceAngleFilter implements IContourFilter<DistanceAngleMeasurements>
{
    private final double minArea;
    private final double minRatio;
    private final double focalLength;
    private final DistanceAngleVisionCalculator calculator;
    private final double targetDiameter;
    private final double maxDistanceDifference;

    /**
     * Initializes a new instance of the LargestBallDistanceAngleFilter class.
     * @param minArea minimum area for countours
     * @param minRatio min ratio between area of contour and area of surrounding circle
     * @param centerX x center of the image
     * @param centerY y center of the image
     * @param focalX x focal length
     * @param focalY y focal length
     * @param cameraYaw camera mounting yaw
     * @param cameraPitch camera mounting pitch
     * @param cameraVerticalOffset camera mounting vertical offset
     * @param cameraDepthOffset camera mounting depth offset
     * @param targetDiameter diameter of target game piece
     * @param maxDistanceDifference max difference between the distance estimates (based on height vs diameter)
     */
    public LargestBallDistanceAngleFilter(
        double minArea,
        double minRatio,
        double centerX,
        double centerY,
        double focalX,
        double focalY,
        double cameraYaw,
        double cameraPitch,
        double cameraVerticalOffset,
        double cameraDepthOffset,
        double targetDiameter,
        double maxDistanceDifference)
    {
        this.minArea = minArea;
        this.minRatio = minRatio;

        this.focalLength = focalX;
        this.calculator =
            new DistanceAngleVisionCalculator(
                null,
                centerX,
                centerY,
                focalX,
                focalY,
                cameraYaw,
                cameraPitch,
                cameraVerticalOffset,
                cameraDepthOffset,
                targetDiameter / 2.0);

        this.targetDiameter = targetDiameter;
        this.maxDistanceDifference = maxDistanceDifference;
    }

    @Override
    public DistanceAngleMeasurements filter(List<MatOfPoint> contourList)
    {
        MatOfPoint2f mop2f = new MatOfPoint2f();
        Point center = new Point();
        float[] radiusResult = new float[1];

        // find the largest circle that also fits the criteria...
        double largestContourArea = 0.0;
        DistanceAngleMeasurements measurements = null;
        for (MatOfPoint contour : contourList)
        {
            double area = Imgproc.contourArea(contour);
            if (area >= this.minArea && area > largestContourArea)
            {
                contour.convertTo(mop2f, CvType.CV_32FC2);

                Imgproc.minEnclosingCircle(mop2f, center, radiusResult);
                double radius = radiusResult[0];
                double minCircleArea = radius * radius * Math.PI;
                double ratio = area / minCircleArea;
                if (ratio >= this.minRatio)
                {
                    DistanceAngleMeasurements temp = this.calculator.calculate(center);

                    double alternativeDistance = (this.targetDiameter * this.focalLength) / (radius * 2.0);
                    if (Math.abs(temp.getDistance() - alternativeDistance) <= this.maxDistanceDifference)
                    {
                        largestContourArea = area;
                        measurements = temp;
                    }
                }
            }

            contour.release();
        }

        return measurements;
    }
}
