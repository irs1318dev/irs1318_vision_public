package frc1318.vision;

import org.opencv.core.*;

public class SimpleVisionSystem extends VisionSystemBase
{
    private IFramePipeline framePipeline;

    /**
     * Initializes a new instance of the SimpleVisionSystem class.
     * 
     * @param frameReader       that reads frames from some sourcee
     * @param controller        to check for streaming being enabled
     * @param framePipeline     that processs frames from some sourc
     * @param cameraName        name of the camera to use for the raw stream
     * @param streamResolutionX X-resolution of the raw stream
     * @param streamResolutionY Y-resolution of the raw stream
     */
    public SimpleVisionSystem(
        IFrameReader frameReader,
        IController controller,
        IFramePipeline framePipeline,
        String cameraName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(frameReader, controller, cameraName, streamResolutionX, streamResolutionY);

        this.framePipeline = framePipeline;
    }

    /**
     * Extension method for processing the image (without disposing it)
     * @param image to process
     */
    @Override
    protected void process(Mat image)
    {
        this.framePipeline.process(image);
    }
}
