package frc1318.vision;

public interface IController extends IOpenable
{
    /**
     * Gets whether streaming the camera images is enabled
     * @return true if enabled
     */
    public boolean getStreamEnabled();

    /**
     * Gets whether processing is enabled
     * @return 0 if no processing is enabled, 1 if vision target is enabled, 2 if power cell is enabled 
     */
    public int getProcessingMode();
}
