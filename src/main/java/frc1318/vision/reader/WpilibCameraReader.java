package frc1318.vision.reader;

import org.opencv.core.Mat;

import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoCamera;
import edu.wpi.first.cameraserver.*;
import frc1318.vision.CameraSettings;
import frc1318.vision.IController;
import frc1318.vision.IRunnableFrameReader;
import frc1318.vision.helpers.Pair;

public class WpilibCameraReader implements IRunnableFrameReader
{
    private final IController controller;
    private final String videoUrl;
    private final int usbId;
    private final String cameraName;
    private final boolean reprocessImage;

    private VideoCamera camera;
    private CvSink cvSink;

    private Object frameLock;
    private Object settingsLock;
    private Mat currentFrame;
    private long captureTime;
    private volatile boolean frameReady;
    private volatile boolean stop;
    private CameraSettings newSettings;

    private boolean opened;

    /**
     * Initializes a new instance of the WpilibCameraReader class.
     * @param controller to use to determine if we should keep collecting frames
     * @param videoUrl to use to retrieve frame data from an IP camera
     * @param cameraName name of the camera
     * @param reprocessImage whether to allow a caller to retrieve the same frame multiple times
     */
    public WpilibCameraReader(IController controller, String videoUrl, String cameraName, boolean reprocessImage)
    {
        this.controller = controller;

        this.videoUrl = videoUrl;
        this.usbId = -1;
        this.cameraName = cameraName;
        this.reprocessImage = reprocessImage;

        this.frameLock = new Object();
        this.settingsLock = new Object();
        this.currentFrame = null;
        this.captureTime = 0L;
        this.frameReady = false;
        this.stop = false;

        this.opened = false;
        this.camera = null;
        this.cvSink = null;
    }

    /**
     * Initializes a new instance of the WpilibCameraReader class.
     * @param controller to use to determine if we should keep collecting frames
     * @param usbId to use to identify a local USB camera
     * @param cameraName name of the camera
     * @param reprocessImage whether to allow a caller to retrieve the same frame multiple times
     */
    public WpilibCameraReader(IController controller, int usbId, String cameraName, boolean reprocessImage)
    {
        this.controller = controller;

        this.usbId = usbId;
        this.videoUrl = null;
        this.cameraName = cameraName;
        this.reprocessImage = reprocessImage;

        this.frameLock = new Object();
        this.settingsLock = new Object();
        this.currentFrame = null;
        this.frameReady = false;
        this.stop = false;

        this.opened = false;
        this.camera = null;
    }

    /**
     * Opens the camera reader
     * 
     * @return true if we were able to open the camera reader
     */
    @Override
    public boolean open()
    {
        this.cvSink = new CvSink("Camera Sink");
        if (this.videoUrl != null)
        {
            this.camera = CameraServer.addAxisCamera(this.cameraName, this.videoUrl);
        }
        else
        {
            UsbCamera usbCamera = new UsbCamera(this.cameraName, this.usbId);
            CameraServer.addCamera(usbCamera);
            this.camera = usbCamera;
        }

        this.cvSink.setSource(this.camera);
        this.opened = true;

        return this.opened;
    }

    @Override
    public void close()
    {
        if (this.cvSink != null)
        {
            this.cvSink.close();
            this.cvSink = null;
        }

        if (this.camera != null)
        {
            this.camera.close();
            this.camera = null;
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
     * Run the thread that captures frames and buffers the most recently retrieved
     * frame so that an pipeline can use it.
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

                if (settings != null && this.camera instanceof UsbCamera)
                {
                    UsbCamera usbCamera = (UsbCamera)this.camera;
                    if (settings.Exposure > 0)
                    {
                        usbCamera.setExposureManual((int)settings.Exposure);
                    }
                    else
                    {
                        usbCamera.setExposureAuto();
                    }

                    usbCamera.setBrightness((int)settings.Brightness);

                    usbCamera.setResolution(settings.ResolutionX, settings.ResolutionY);
                    usbCamera.setFPS(settings.FramesPerSecond);
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
                long result = this.cvSink.grabFrame(image);
                if (result != 0)
                {
                    this.setCurrentFrame(image, result / 1000L);
                }
            }

            this.cvSink.close();
            this.camera.close();
            this.opened = false;
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
     * Retrieve the most recent image frame saved from the Camera
     * 
     * @return frame of an image
     * @throws InterruptedException
     */
    @Override
    public Pair<Mat, Long> getCurrentFrame() throws InterruptedException
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

            if (this.reprocessImage)
            {
                Mat image = new Mat();
                this.currentFrame.copyTo(image);
                return new Pair<Mat, Long>(image, this.captureTime);
            }
            else
            {
                this.frameReady = false;
                Mat image = this.currentFrame;
                this.currentFrame = null;
                return new Pair<Mat, Long>(image, this.captureTime);
            }
        }
    }

    /**
     * set the current frame as the current frame
     * 
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
}
