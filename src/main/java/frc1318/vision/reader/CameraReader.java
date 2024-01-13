package frc1318.vision.reader;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import frc1318.vision.CameraSettings;
import frc1318.vision.IFrameReader;
import frc1318.vision.Program;

public class CameraReader implements IFrameReader
{
    private static final int USE_AUTO_EXPOSURE = 3;
    private static final int USE_MANUAL_EXPOSURE = 1;

    private final String videoUrl;
    private final int usbId;

    private Object frameLock;
    private Object settingsLock;
    private Mat currentFrame;
    private boolean frameReady;
    private boolean stop;
    private CameraSettings newSettings;

    private boolean opened;
    private VideoCapture vc;

    /**
     * Initializes a new instance of the CameraReader class.
     * @param videoUrl to use to retrieve frame data from an IP camera
     */
    public CameraReader(String videoUrl)
    {
        this.videoUrl = videoUrl;
        this.usbId = -1;

        this.frameLock = new Object();
        this.settingsLock = new Object();
        this.currentFrame = null;
        this.frameReady = false;
        this.stop = false;

        this.opened = false;
        this.vc = null;
    }

    /**
     * Initializes a new instance of the CameraReader class.
     * @param usbId to use to identify a local USB camera
     */
    public CameraReader(int usbId)
    {
        this.usbId = usbId;
        this.videoUrl = null;

        this.frameLock = new Object();
        this.settingsLock = new Object();
        this.currentFrame = null;
        this.frameReady = false;
        this.stop = false;

        this.opened = false;
        this.vc = null;
    }

    /**
     * Opens the camera reader
     * @return true if we were able to open the camera reader
     */
    @Override
    public boolean open()
    {
        this.vc = new VideoCapture();

        int apiPreference = Videoio.CAP_ANY;
        if (Program.IsLinux)
        {
            apiPreference = Videoio.CAP_V4L2;
        }

        if (this.videoUrl != null)
        {
            this.opened = this.vc.open(this.videoUrl, apiPreference);
        }
        else
        {
            this.opened = this.vc.open(this.usbId, apiPreference);
        }


        return this.opened;
    }

    @Override
    public void close()
    {
        if (this.vc != null)
        {
            this.vc.release();
            this.vc = null;
        }
    }

    @Override
    public void setSettings(CameraSettings settings)
    {
        synchronized (this.settingsLock)
        {
            this.newSettings = settings;
        }
    }

    /**
     * Run the thread that captures frames and buffers the most recently retrieved frame so that an pipeline can use it.
     */
    @Override
    public void run()
    {
        if (this.opened)
        {
            Mat image;
            while (!this.stop)
            {
                CameraSettings settings;
                synchronized (this.settingsLock)
                {
                    settings = this.newSettings;
                    this.newSettings = null;
                }

                if (settings != null)
                {
                    this.vc.set(Videoio.CAP_PROP_FRAME_WIDTH, settings.ResolutionX);
                    this.vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, settings.ResolutionY);
                    if (settings.Exposure > 0)
                    {
                        this.vc.set(Videoio.CAP_PROP_AUTO_EXPOSURE, USE_MANUAL_EXPOSURE);
                        this.vc.set(Videoio.CAP_PROP_EXPOSURE, settings.Exposure);
                    }
                    else
                    {
                        this.vc.set(Videoio.CAP_PROP_AUTO_EXPOSURE, USE_AUTO_EXPOSURE);
                    }

                    this.vc.set(Videoio.CAP_PROP_BRIGHTNESS, settings.Brightness);
                    this.vc.set(Videoio.CAP_PROP_FPS, settings.FramesPerSecond);
                }

                image = new Mat();
                if (this.vc.read(image))
                {
                    this.setCurrentFrame(image);
                }
            }
        }
    }

    /**
     * stop retrieving frames
     */
    @Override
    public void stop()
    {
        this.stop = true;
    }

    /**
     * Retrieve the most recent image frame from the MJPEG Camera
     * @return frame of an image
     * @throws InterruptedException
     */
    @Override
    public Mat getCurrentFrame()
        throws InterruptedException
    {
        synchronized (this.frameLock)
        {
            while (!this.frameReady && !this.stop)
            {
                this.frameLock.wait(100);
            }

            if (this.stop)
            {
                return null;
            }

            this.frameReady = false;
            Mat image = this.currentFrame;
            this.currentFrame = null;
            return image;
        }
    }

    /**
     * set the current frame as the current frame
     * @param frame to set as current
     */
    private void setCurrentFrame(Mat frame)
    {
        synchronized (this.frameLock)
        {
            if (this.currentFrame != null)
            {
                // clean up previous frame
                this.currentFrame.release();
                this.currentFrame = null;
            }

            // hold current frame
            this.currentFrame = frame;
            this.frameReady = true;

            // notify another lock holder
            this.frameLock.notify();
        }
    }
}
