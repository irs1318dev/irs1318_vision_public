package frc1318.vision;

import org.opencv.core.*;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.MjpegServer;
import edu.wpi.first.util.PixelFormat;
import frc1318.vision.helpers.Assert;
import frc1318.vision.helpers.Pair;

public abstract class VisionSystemBase implements Runnable, IOpenable
{
    protected final IFrameReader frameReader;
    protected final IController controller;

    private final String cameraName;
    private final int streamResolutionX;
    private final int streamResolutionY;

    private CvSource rawFrameWriter;
    private long lastFrameSent;

    private volatile boolean shouldStop;

    /**
     * Initializes a new instance of the VisionSystemBase class.
     * 
     * @param frameReader       that reads frames from some source
     * @param controller        to check for streaming being enabled
     * @param cameraName        name of the camera to use for the raw stream
     * @param streamResolutionX X-resolution of the raw stream
     * @param streamResolutionY Y-resolution of the raw stream
     */
    public VisionSystemBase(
        IFrameReader frameReader,
        IController controller,
        String cameraName,
        int streamResolutionX,
        int streamResolutionY)
    {
        Assert.IsNotNull(frameReader, "frameReader");
        Assert.IsNotNull(controller, "controller");

        this.frameReader = frameReader;
        this.controller = controller;

        this.cameraName = cameraName;
        this.streamResolutionX = streamResolutionX;
        this.streamResolutionY = streamResolutionY;

        this.rawFrameWriter = null;
        this.lastFrameSent = 0L;

        this.shouldStop = false;
    }

    /**
     * Run the process of capturing and analyzing frames until we have reached the
     * end of the stream.
     */
    @Override
    public void run()
    {
        long processedFrames = 0;

        try
        {
            long lastMeasured = System.currentTimeMillis();
            while (!this.shouldStop && !Thread.interrupted())
            {
                long capturedTime = this.captureAndProcess();
                if (capturedTime == 0)
                {
                    break;
                }

                processedFrames++;
                if (VisionConstants.DEBUG_FRAME_RATE &&
                    processedFrames >= VisionConstants.DEBUG_FPS_AVERAGING_INTERVAL)
                {
                    long elapsedTime = capturedTime - lastMeasured;

                    double framesPerMillisecond = ((double)VisionConstants.DEBUG_FPS_AVERAGING_INTERVAL) / elapsedTime;
                    Logger.write(
                        String.format("Recent Average frame processing rate %f fps", 1000.0 * framesPerMillisecond));

                    lastMeasured = capturedTime;
                    processedFrames = 0;
                }
            }
        }
        catch (InterruptedException ex)
        {
            Logger.write("VisionSystem thread interrupted.");
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void stop()
    {
        this.shouldStop = true;
    }

    public boolean open()
    {
        if (this.cameraName != null)
        {
            this.rawFrameWriter = new CvSource(this.cameraName, PixelFormat.kMJPEG, this.streamResolutionX, this.streamResolutionY, 25);
            MjpegServer mjpegServer = CameraServer.startAutomaticCapture(this.rawFrameWriter);
            mjpegServer.setCompression(VisionConstants.STREAMING_COMPRESSION);
        }

        return true;
    }

    public void close()
    {
    }

    /**
     * Capture a frame from the frame reader and process that frame using the extension method
     * 
     * @return the time that we captured the image from the frame reader, or 0 if we couldn't capture an image
     * @throws InterruptedException
     */
    public long captureAndProcess() throws InterruptedException
    {
        Pair<Mat, Long> image = this.frameReader.getCurrentFrame();
        if (image == null)
        {
            return 0L;
        }

        long currTime = System.currentTimeMillis();
        if (this.rawFrameWriter != null && this.controller.getStreamEnabled())
        {
            long elapsedTime = currTime - this.lastFrameSent;
            if (VisionConstants.MAX_STREAM_FPS <= 0.0 ||
                elapsedTime >= VisionConstants.STREAM_FRAME_GAP_MILLIS)
            {
                this.lastFrameSent = currTime;
                this.rawFrameWriter.putFrame(image.first);
            }
            else if (this.lastFrameSent > currTime + VisionConstants.STREAM_FRAME_GAP_MILLIS)
            {
                // check for weirdness...
                this.lastFrameSent = currTime;
            }
        }

        this.process(image.first, image.second);

        image.first.release();
        return currTime;
    }

    /**
     * Extension method for processing the image (without disposing it)
     * @param image to process
     * @param captureTime when the image was captured
     */
    protected abstract void process(Mat image, long captureTime);
}
