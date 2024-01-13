package frc1318.vision.pipeline;

import org.opencv.core.Mat;

import frc1318.vision.IFramePipeline;

public class EmptyPipeline implements IFramePipeline
{
    /**
     * Initializes a new instance of the EmptyPipeline class.
     */
    public EmptyPipeline()
    {
    }

    /**
     * Process a single image frame
     * @param frame image to process
     */
    @Override
    public void process(Mat image)
    {
    }
}
