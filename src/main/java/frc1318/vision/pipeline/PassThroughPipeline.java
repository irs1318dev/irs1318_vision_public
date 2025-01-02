package frc1318.vision.pipeline;

import org.opencv.core.Mat;

import frc1318.vision.IFramePipeline;
import frc1318.vision.IResultWriter;

public class PassThroughPipeline<T> implements IFramePipeline
{
    private final IResultWriter<T> output;

    /**
     * Initializes a new instance of the PassThroughPipeline class.
     */
    public PassThroughPipeline(IResultWriter<T> output)
    {
        this.output = output;
    }

    /**
     * Process a single image frame
     * @param sourceFrame image to process
     * @param captureTime when the image was captured
     */
    @Override
    public void process(Mat sourceFrame, long captureTime)
    {
        // finally, output the result
        this.output.write(null, captureTime, sourceFrame);
    }
}
