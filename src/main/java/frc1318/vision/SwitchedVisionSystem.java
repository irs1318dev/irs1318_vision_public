package frc1318.vision;

import org.opencv.core.*;

import frc1318.vision.helpers.Assert;

public class SwitchedVisionSystem extends VisionSystemBase
{
    private final IFramePipeline[] framePipelines;
    private final int[] pipelineProcessingModes;
    private final CameraSettings[] cameraSettings;
    private final CameraSettings defaultCameraSettings;

    private int prevProcessingMode;
    private CameraSettings currentCameraSettings;

    /**
     * Initializes a new instance of the SwitchedVisionSystem class.
     * 
     * @param frameReader               that reads frames from some source
     * @param controller                to check for pieces being enabled
     * @param framePipelines            that processs frames from some source
     * @param pipelineProcessingModes   that indicates the expected processing setting used to enable the pipeline with the corresponding index
     * @param cameraSettings            camera settings to use for each given pipeline
     * @param defaultCameraSettings     camera settings when we are not using a pipeline
     * @param cameraName                name of the camera to use for the raw stream
     * @param streamResolutionX         X-resolution of the raw stream
     * @param streamResolutionY         Y-resolution of the raw stream
     */
    public SwitchedVisionSystem(
        IFrameReader frameReader,
        IController controller,
        IFramePipeline[] framePipelines,
        int[] pipelineProcessingModes,
        CameraSettings[] cameraSettings,
        CameraSettings defaultCameraSettings,
        String cameraName,
        int streamResolutionX,
        int streamResolutionY)
    {
        super(frameReader, controller, cameraName, streamResolutionX, streamResolutionY);

        Assert.IsNotNull(framePipelines, "framePipelines");
        Assert.IsNotNull(pipelineProcessingModes, "pipelineProcessingModes");
        Assert.IsNotNull(cameraSettings, "cameraSettings");
        Assert.Equals(framePipelines.length, pipelineProcessingModes.length, "Expect framePipelines and pipelineProcessingModes to be the same length");
        Assert.Equals(framePipelines.length, cameraSettings.length, "Expect framePipelines and cameraSettings to be the same length");

        this.framePipelines = framePipelines;
        this.pipelineProcessingModes = pipelineProcessingModes;
        this.cameraSettings = cameraSettings;
        this.defaultCameraSettings = defaultCameraSettings;

        this.prevProcessingMode = 0;
        this.currentCameraSettings = this.defaultCameraSettings;
    }

    /**
     * Extension method for processing the image (without disposing it)
     * @param image to process
     * @param captureTime when the image was captured
     */
    @Override
    protected void process(Mat image, long captureTime)
    {
        int currProcessingMode = this.controller.getProcessingMode();
        boolean updateSettings = false;
        if (this.prevProcessingMode != currProcessingMode)
        {
            updateSettings = true;
            this.prevProcessingMode = currProcessingMode;
        }

        boolean foundMode = false;
        for (int i = 0; i < this.framePipelines.length; i++)
        {
            IFramePipeline currPipeline = this.framePipelines[i];
            if (currPipeline != null)
            {
                if (this.pipelineProcessingModes[i] == currProcessingMode)
                {
                    if (updateSettings)
                    {
                        // note - won't take effect until at least the next frame
                        CameraSettings newCameraSettings = this.cameraSettings[i];
                        if (!this.currentCameraSettings.equals(newCameraSettings))
                        {
                            this.frameReader.setSettings(newCameraSettings);
                            this.currentCameraSettings = newCameraSettings;
                        }
                    }

                    foundMode = true;
                    currPipeline.process(image, captureTime);
                }
                else
                {
                    currPipeline.process(null, captureTime);
                }
            }
        }

        if (!foundMode)
        {
            if (updateSettings)
            {
                if (!this.currentCameraSettings.equals(this.defaultCameraSettings))
                {
                    this.frameReader.setSettings(this.defaultCameraSettings);
                    this.currentCameraSettings = this.defaultCameraSettings;
                }
            }
        }
    }
}
