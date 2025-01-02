package frc1318.vision.leds;

public interface ILEDAnimation extends ILEDDesign
{
    /**
     * Update the design for the LED Strip
     * @param elapsedMs the amount of time that has elapsed since the last update, in milliseconds
     * @param ledStrip to apply the design to
     * @return true if we updated the image, otherwise false
     */
    boolean update(long elapsedMs, ILedStrip ledStrip);
}
