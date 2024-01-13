package frc1318.vision.helpers;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class HSVFilter
{
    private final Scalar lowerBoundOne;
    private final Scalar upperBoundOne;
    private final Scalar lowerBoundTwo;
    private final Scalar upperBoundTwo;

    private final Mat frameHSV;
    private final Mat tempResult1;
    private final Mat tempResult2;

    /**
     * Initializes a new instance of the HSVFilter class.
     * @param lowerBound of HSV to filter
     * @param upperBound of HSV to filter
     */
    public HSVFilter(Scalar lowerBound, Scalar upperBound)
    {
        this.lowerBoundOne = lowerBound;
        this.upperBoundOne = upperBound;
        this.lowerBoundTwo = null;
        this.upperBoundTwo = null;

        this.frameHSV = new Mat();
        this.tempResult1 = null;
        this.tempResult2 = null;
    }

    public HSVFilter(Scalar lowerBound1, Scalar upperBound1, Scalar lowerBound2, Scalar upperBound2)
    {
        this.lowerBoundOne = lowerBound1;
        this.upperBoundOne = upperBound1;
        this.lowerBoundTwo = lowerBound2;
        this.upperBoundTwo = upperBound2;

        this.frameHSV = new Mat();
        this.tempResult1 = new Mat();
        this.tempResult2 = new Mat();
    }

    /**
     * Filter the provided frame for HSVs within the provider bounds.
     * @param sourceFrame to convert into HSV and then filter
     * @param targetFrame to hold the result of the filtering
     */
    public void filterHSV(Mat sourceFrame, Mat targetFrame)
    {
        Imgproc.cvtColor(sourceFrame, this.frameHSV, Imgproc.COLOR_BGR2HSV);
        if (lowerBoundTwo == null)
        {
            Core.inRange(this.frameHSV, this.lowerBoundOne, this.upperBoundOne, targetFrame);
        }
        else
        {
            Core.inRange(this.frameHSV, this.lowerBoundOne, this.upperBoundOne, this.tempResult1);
            Core.inRange(this.frameHSV, this.lowerBoundTwo, this.upperBoundTwo, this.tempResult2);
            Core.bitwise_or(this.tempResult1, this.tempResult2, targetFrame);
        }
    }
}
