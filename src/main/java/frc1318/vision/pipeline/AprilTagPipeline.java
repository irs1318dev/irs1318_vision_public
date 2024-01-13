package frc1318.vision.pipeline;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import frc1318.apriltag.*;
import frc1318.vision.IAprilTagFilter;
import frc1318.vision.IFramePipeline;
import frc1318.vision.IResultWriter;
import frc1318.vision.helpers.ImageUndistorter;

public class AprilTagPipeline<T> implements IFramePipeline
{
    private final IResultWriter<T> output;

    private final Mat mask;
    private final ImageUndistorter undistorter;
    private final AprilTagDetector aprilTagDetector;
    private final IAprilTagFilter<T> tagFilter;

    private final Mat maskedFrame;
    private final Mat frameUndistort;
    private final Mat gray;

    /**
     * Initializes a new instance of the AprilTagPipeline class.
     * 
     * @param output                 output writer
     * @param mask                   to use for removing selected parts of the image
     * @param undistorter            frame undistorter
     * @param processingEnabledValue value indicating when processing is enabled
     * @param tagFilter              AprilTag filter
     * @param tagFamily              AprilTag family
     * @param tagMaxHammingDistance  How many bit errors to accept for AprilTag detection
     * @param tagThreads             How many threads should be used for AprilTag detection
     * @param tagQuadDecimate        AprilTag detection of quads can be done on a lower-resolution image, improving speed at a cost of pose accuracy and a slight decrease in detection rate. Decoding the binary payload is still done at full resolution.
     * @param tagQuadSigma           What Gaussian blur should be applied to the segmented image (used for quad detection?).  Parameter is the standard deviation in pixels.  Very noisy images benefit from non-zero values (e.g. 0.8).
     * @param tagRefineEdges         whether the edges of the each quad are adjusted to "snap to" strong gradients nearby. This is useful when decimation is employed, as it can increase the quality of the initial quad estimate substantially. Generally recommended to be on (true). Very computationally inexpensive. Option is ignored if quad_decimate = 1.
     * @param tagDecodeSharpening    How much sharpening should be done to decoded images? This can help decode small tags but may or may not help in odd lighting conditions or low light conditions. The default value is 0.25.
     */
    public AprilTagPipeline(
        IResultWriter<T> output,
        Mat mask,
        ImageUndistorter undistorter,
        IAprilTagFilter<T> tagFilter,
        AprilTagFamily tagFamily,
        int tagMaxHammingDistance,
        int tagThreads,
        float tagQuadDecimate,
        float tagQuadSigma,
        boolean tagRefineEdges,
        double tagDecodeSharpening)
    {
        this.output = output;

        if (undistorter != null)
        {
            this.frameUndistort = new Mat();
            this.undistorter = undistorter;
        }
        else
        {
            this.frameUndistort = null;
            this.undistorter = null;
        }

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


        this.tagFilter = tagFilter;

        this.aprilTagDetector = AprilTag.create(tagFamily, tagMaxHammingDistance, tagThreads, tagQuadDecimate, tagQuadSigma, tagRefineEdges, tagDecodeSharpening, false);

        this.gray = new Mat();
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
            this.undistorter.undistortFrame(frameToUse, this.frameUndistort);
            frameToUse = this.frameUndistort;
        }

        // third, convert BGR to Gray
        Imgproc.cvtColor(frameToUse, this.gray, Imgproc.COLOR_BGR2GRAY);

        // fourth, detect tags
        AprilTagDetection[] detectedTags = this.aprilTagDetector.detect(this.gray);

        // filter the detected tags
        T result = this.tagFilter.filter(detectedTags);

        // finally, output the result
        this.output.write(result, frameToUse);

        if (result != null)
        {
            if (result instanceof Mat)
            {
                ((Mat)result).release();
                result = null;
            }
            else if (result instanceof AprilTagDetection)
            {
                ((AprilTagDetection)result).destroy();
                result = null;
            }
        }
    }
}
