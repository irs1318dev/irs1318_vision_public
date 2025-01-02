package frc1318.vision.pipeline;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import frc1318.vision.IFramePipeline;
import frc1318.vision.VisionConstants;

public class LoggingPipeline implements IFramePipeline
{
    private final File imageLoggingDirectory;
    private final IFramePipeline next;
    private final String prefix;

    private int count;

    /**
     * Initializes a new instance of the LoggingPipeline class.
     * LoggingPipeline logs occasional frames to the output directory
     * @param prefix                to add to the filename
     * @param imageLoggingDirectory to log images to
     * @param next                  the next step in the pipeline
     */
    public LoggingPipeline(
        String prefix,
        File imageLoggingDirectory,
        IFramePipeline next)
    {
        this.imageLoggingDirectory = imageLoggingDirectory;
        this.next = next;
        this.prefix = prefix;

        this.count = 0;
    }

    /**
     * Process a single image frame
     * 
     * @param sourceFrame image to process
     * @param captureTime when the image was captured
     */
    @Override
    public void process(Mat sourceFrame, long captureTime)
    {
        if (sourceFrame != null)
        {
            this.count++;
            if (this.imageLoggingDirectory != null && this.count % VisionConstants.FRAME_OUTPUT_GAP == 0)
            {
                File newFile = new File(this.imageLoggingDirectory, String.format("%simage%d.jpg", this.prefix, this.count));
                if (newFile.exists())
                {
                    newFile.delete();
                }

                Imgcodecs.imwrite(newFile.getAbsolutePath(), sourceFrame);
            }
        }

        if (this.next != null)
        {
            this.next.process(sourceFrame, captureTime);
        }
    }
}
