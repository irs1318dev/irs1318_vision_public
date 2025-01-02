package frc1318.vision;

import java.util.List;

public interface IController extends IOpenable
{
    /**
     * Gets whether streaming the camera images is enabled
     * @return true if enabled
     */
    public boolean getStreamEnabled();

    /**
     * Gets whether the vision system is enabled
     * @return true if we should process camera images, otherwise false
     */
    boolean isEnabled();

    /**
     * Gets whether processing is enabled
     * @return 0 if no processing is enabled, 1 if vision target is enabled, 2 if power cell is enabled 
     */
    public int getProcessingMode();

    /**
     * Gets the specified April Tag Target
     * @return April Tag ID of Desired April Tag
     */
    public List<Integer> getDesiredTarget();
}
