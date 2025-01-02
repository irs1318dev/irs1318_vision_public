package frc1318.vision.reader;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import frc1318.vision.CameraSettings;
import frc1318.vision.IController;
import frc1318.vision.IRunnableFrameReader;
import frc1318.vision.Logger;
import frc1318.vision.Program;
import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.Pair;

public class CameraReader implements IRunnableFrameReader
{
    private static final int USE_AUTO_EXPOSURE = 3;
    private static final int USE_MANUAL_EXPOSURE = 1;

    private final IController controller;
    private final String videoUrl;
    private final int usbId;

    private Object frameLock;
    private Object settingsLock;
    private Mat currentFrame;
    private long captureTime;
    private boolean frameReady;
    private boolean stop;
    private CameraSettings newSettings;

    private boolean opened;
    private VideoCapture vc;

    /**
     * Initializes a new instance of the CameraReader class.
     * @param controller to use to determine if we should keep collecting frames
     * @param videoUrl to use to retrieve frame data from an IP camera
     */
    public CameraReader(IController controller, String videoUrl)
    {
        this.controller = controller;

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
     * @param controller to use to determine if we should keep collecting frames
     * @param usbId to use to identify a local USB camera
     */
    public CameraReader(IController controller, int usbId)
    {
        this.controller = controller;

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
                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_FRAME_WIDTH, settings.ResolutionX), "ResolutionX");
                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_FRAME_HEIGHT, settings.ResolutionY), "ResolutionY");
                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_FPS, settings.FramesPerSecond), "FPS");
                    if (settings.Exposure > 0)
                    {
                        CameraReader.printError(this.vc.set(Videoio.CAP_PROP_AUTO_EXPOSURE, USE_MANUAL_EXPOSURE), "UseManualExposure");
                        CameraReader.printError(this.vc.set(Videoio.CAP_PROP_EXPOSURE, (int)settings.Exposure), "Exposure");
                    }
                    else
                    {
                        CameraReader.printError(this.vc.set(Videoio.CAP_PROP_AUTO_EXPOSURE, USE_AUTO_EXPOSURE), "UseAutoExposure");
                    }

                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_BRIGHTNESS, (int)settings.Brightness), "Brightness");
                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_FOURCC, VideoWriter.fourcc('M', 'J', 'P', 'G')), "fourCC");
                    CameraReader.printError(this.vc.set(Videoio.CAP_PROP_FPS, settings.FramesPerSecond), "FPS");

                    if (VisionConstants.DEBUG)
                    {
                        Logger.write("FOURCC: " + CameraReader.fourccToString((int)this.vc.get(Videoio.CAP_PROP_FOURCC)));
                        Logger.write("ResX: " + this.vc.get(Videoio.CAP_PROP_FRAME_WIDTH));
                        Logger.write("ResY: " + this.vc.get(Videoio.CAP_PROP_FRAME_HEIGHT));
                        Logger.write("FPS: " + this.vc.get(Videoio.CAP_PROP_FPS));
                        Logger.write("AutoExposure: " + this.vc.get(Videoio.CAP_PROP_AUTO_EXPOSURE));
                        Logger.write("Exposure: " + this.vc.get(Videoio.CAP_PROP_EXPOSURE));
                        Logger.write("Brightness: " + this.vc.get(Videoio.CAP_PROP_BRIGHTNESS));
                    }
                }

                if (!this.controller.isEnabled())
                {
                    try
                    {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException ex)
                    {
                    }
                }

                image = new Mat();
                long captureTime = System.currentTimeMillis();
                if (this.vc.read(image))
                {
                    this.setCurrentFrame(image, captureTime);
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
     * @return frame of an image and when it was captured
     * @throws InterruptedException
     */
    @Override
    public Pair<Mat, Long> getCurrentFrame()
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
            if (image == null)
            {
                return null;
            }

            return new Pair<Mat, Long>(image, this.captureTime);
        }
    }

    /**
     * set the current frame as the current frame
     * @param frame to set as current
     * @param captureTime when the image was captured
     */
    private void setCurrentFrame(Mat frame, long captureTime)
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
            this.captureTime = captureTime;
            this.frameReady = true;

            // notify another lock holder
            this.frameLock.notify();
        }
    }

    private static void printError(boolean set, String what)
    {
        if (!set)
        {
            Logger.writeError("Could not set " + what);
        }
    }

    private static String fourccToString(int fourcc)
    {
        char c1 = (char)(fourcc & 0xff);
        char c2 = (char)((fourcc >> 8) & 0xff);
        char c3 = (char)((fourcc >> 16) & 0xff);
        char c4 = (char)((fourcc >> 24) & 0xff);
        return new String(new char[] { c1, c2, c3, c4 });
    }
}
