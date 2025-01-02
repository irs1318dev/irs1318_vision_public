package frc1318.vision;

import org.opencv.core.Mat;

import frc1318.vision.helpers.Pair;

public interface IFrameReader extends IOpenable
{
    /**
     * Update camera settings
     * @param setting value to use
     */
    public void setSettings(CameraSettings settings);

    /**
     * Retrieve an image frame
     * @return frame of an image and when it was captured
     * @throws InterruptedException
     */
    public Pair<Mat, Long> getCurrentFrame()
        throws InterruptedException;
}
