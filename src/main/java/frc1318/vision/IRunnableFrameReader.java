package frc1318.vision;

public interface IRunnableFrameReader extends IFrameReader, Runnable
{
    /**
     * stop retrieving frames
     */
    public void stop();
}
