package frc1318.vision.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.IContourFilter;
import frc1318.vision.IFramePipeline;
import frc1318.vision.IResultWriter;
import frc1318.vision.helpers.HSVFilter;
import frc1318.vision.helpers.ImageUndistorter;

public class HSVPipeline<T> implements IFramePipeline
{
    private final IResultWriter<T> output;

    private final Mat mask;
    private final ImageUndistorter undistorter;
    private final HSVFilter hsvFilter;
    private final IContourFilter<T> contourFilter;

    private final Mat maskedFrame;
    private final Mat undistortedFrame;
    private final Mat filteredFrame;
    private final Mat hierarchy;

    /**
     * Initializes a new instance of the HSVPipeline class.
     * 
     * @param output        output writer
     * @param mask          to use for removing selected parts of the image
     * @param undistorter   helper for undistorting the image
     * @param hsvFilter     HSV Filtering helper
     * @param contourFilter filter for selecting a single contour to be picked
     */
    public HSVPipeline(
        IResultWriter<T> output,
        Mat mask,
        ImageUndistorter undistorter,
        HSVFilter hsvFilter,
        IContourFilter<T> contourFilter)
    {
        this.output = output;

        if (mask != null)
        {
            this.mask = mask;
            this.maskedFrame = new Mat();
        }
        else
        {
            this.mask = null;
            this.maskedFrame = null;
        }

        if (undistorter != null)
        {
            this.undistortedFrame = new Mat();
            this.undistorter = undistorter;
        }
        else
        {
            this.undistortedFrame = null;
            this.undistorter = null;
        }

        this.hsvFilter = hsvFilter;
        this.contourFilter = contourFilter;

        this.filteredFrame = new Mat();
        this.hierarchy = new Mat();
    }

    /**
     * Process a single image frame
     * 
     * @param sourceFrame image to process
     */
    @Override
    public void process(Mat sourceFrame)
    {
        if (sourceFrame == null)
        {
            this.output.write(null);
            return;
        }

        // first, mask the image.
        Mat frameToUse = sourceFrame;
        if (this.mask != null)
        {
            Core.bitwise_and(frameToUse, this.mask, this.maskedFrame);
            frameToUse = this.maskedFrame;
        }

        // second, undistort the image.
        if (this.undistorter != null)
        {
            this.undistorter.undistortFrame(frameToUse, this.undistortedFrame);
            frameToUse = this.undistortedFrame;
        }

        // third, filter HSV
        this.hsvFilter.filterHSV(frameToUse, this.filteredFrame);

        // third, find the contours.
        // find the contours using OpenCV API...
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(this.filteredFrame, contours, this.hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_KCOS);

        // filter the contours
        T result = this.contourFilter.filter(contours);

        // finally, output the result
        this.output.write(result, frameToUse);

        if (result != null && result instanceof Mat)
        {
            ((Mat)result).release();
            result = null;
        }
    }
}
