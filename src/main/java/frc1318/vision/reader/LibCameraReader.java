package frc1318.vision.reader;

import org.opencv.core.Mat;

import frc1318.libcamera.Camera;
import frc1318.libcamera.CameraManager;
import frc1318.libcamera.ImageFormat;
import frc1318.vision.CameraSettings;
import frc1318.vision.IController;
import frc1318.vision.IRunnableFrameReader;
import frc1318.vision.helpers.Pair;

public class LibCameraReader implements IRunnableFrameReader
{
    private static final Object cmLock = new Object();
    private static CameraManager cameraManager = null;
    private static int cameraCount = 0;

    private final IController controller;
    private final String id;

    private Object frameLock;
    private Object settingsLock;
    private Mat currentFrame;
    private long captureTime;
    private boolean frameReady;
    private boolean stop;
    private CameraSettings newSettings;

    private boolean opened;
    private boolean configured;
    private boolean started;
    private Camera cam;

    /**
     * Initializes a new instance of the LibCameraReader class.
     * @param controller to use to determine if we should keep collecting frames
     * @param id to use to retrieve frame data from the LibCamera library
     */
    public LibCameraReader(IController controller, String id)
    {
        this.controller = controller;

        this.id = id;

        this.frameLock = new Object();
        this.settingsLock = new Object();
        this.currentFrame = null;
        this.frameReady = false;
        this.stop = false;

        this.opened = false;
        this.configured = false;
        this.started = false;
        this.cam = null;
    }

    /**
     * Enumerate the camera ids
     * (Assumes that no other camera manager is being used)
     * @return array of camera ids
     */
    public static String[] enumerateCameraIds()
    {
        CameraManager camManager = CameraManager.create();
        try
        {
            return camManager.getCameraIds();
        }
        finally
        {
            camManager.release();
        }
    }

    /**
     * Enumerate the camera controls for a particular camera id
     * (Assumes that no other camera manager is being used)
     * @param id of the camera to enumerate possible control values
     * @return string containing the control values
     */
    public static String enumerateCameraControls(String id)
    {
        CameraManager camManager = null;
        Camera cam = null;
        try
        {
            camManager = CameraManager.create();
            cam = camManager.getCamera(id);

            return cam.getControls();
        }
        finally
        {
            if (cam != null)
            {
                cam.release();
            }

            if (camManager != null)
            {
                camManager.release();
            }
        }
    }

    /**
     * Opens the camera reader
     * @return true if we were able to open the camera reader
     */
    @Override
    public boolean open()
    {
        this.cam = LibCameraReader.getCamera(this.id);

        this.opened = this.cam != null;
        return this.opened;
    }

    @Override
    public void close()
    {
        if (this.cam != null)
        {
            if (this.started)
            {
                this.cam.stop();
                this.started = false;
            }

            LibCameraReader.releaseCamera(this.cam);
            this.cam = null;
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
            int currentResX = -1;
            int currentResY = -1;
            ImageFormat currentFormat = ImageFormat.Gray;

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
                    if (!this.configured ||
                        currentResX != settings.ResolutionX ||
                        currentResY != settings.ResolutionY ||
                        currentFormat != ImageFormat.Gray)
                    {
                        if (this.cam.configure(settings.ResolutionX, settings.ResolutionY, settings.FramesPerSecond, ImageFormat.Gray))
                        {
                            currentResX = settings.ResolutionX;
                            currentResY = settings.ResolutionY;
                            currentFormat = ImageFormat.Gray;

                            this.configured = true;
                        }
                    }

                    this.cam.updateSettings((float)settings.Brightness, (float)settings.Exposure);                    
                }

                if (!this.controller.isEnabled())
                {
                    if (this.started)
                    {
                        this.cam.stop();
                        this.started = false;
                    }

                    try
                    {
                        Thread.sleep(2500);
                    }
                    catch (InterruptedException ex)
                    {
                    }

                    continue;
                }

                if (this.configured && !this.started)
                {
                    if (this.cam.start())
                    {
                        this.started = true;
                    }
                }

                if (this.started)
                {
                    long captureTime = System.currentTimeMillis();
                    image = this.cam.read();
                    if (image != null)
                    {
                        this.setCurrentFrame(image, captureTime);
                    }
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

    private static Camera getCamera(String id)
    {
        synchronized (LibCameraReader.cmLock)
        {
            // manage cameraManager singleton:
            if (LibCameraReader.cameraManager == null)
            {
                LibCameraReader.cameraManager = CameraManager.create();
            }

            LibCameraReader.cameraCount++;

            return LibCameraReader.cameraManager.getCamera(id);
        }
    }

    private static void releaseCamera(Camera cam)
    {
        synchronized (LibCameraReader.cmLock)
        {
            // we should have the singleton by now if we are releasing a camera...
            if (LibCameraReader.cameraManager == null)
            {
                throw new RuntimeException("CameraManager is null when we are trying to release a camera?");
            }

            cam.release();

            // manage cameraManager singleton:
            LibCameraReader.cameraCount--;
            if (LibCameraReader.cameraCount == 0)
            {
                LibCameraReader.cameraManager.release();
                LibCameraReader.cameraManager = null;
            }
        }
    }
}
