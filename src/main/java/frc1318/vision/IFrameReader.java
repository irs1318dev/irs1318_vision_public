package frc1318.vision;

import org.opencv.core.Mat;

public interface IFrameReader extends Runnable, IOpenable
{
    /**
     * Update camera settings
     * @param setting value to use
     */
    public void setSettings(CameraSettings settings);

    /**
     * Retrieve an image frame
     * @return frame of an image
     * @throws InterruptedException
     */
    public Mat getCurrentFrame()
        throws InterruptedException;

    /**
     * stop retrieving frames
     */
    public void stop();
}
