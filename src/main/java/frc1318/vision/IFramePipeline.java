package frc1318.vision;

import org.opencv.core.Mat;

public interface IFramePipeline
{
    /**
     * Process a single image frame
     * @param frame image to process, or null if this pipeline is disabled
     */
    public void process(Mat frame);
}
