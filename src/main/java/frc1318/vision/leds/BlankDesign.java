package frc1318.vision.leds;

/*
 * An LED design that sets all LEDs in the strip to blank (black).
 */
public class BlankDesign implements ILEDDesign
{
    private final int ledCount;

    /**
     * Initializes a new instance of the BlankDesign class.
     * @param ledCount number of LEDs in the strip
     */
    public BlankDesign(int ledCount)
    {
        this.ledCount = ledCount;
    }

    @Override
    public void apply(ILedStrip ledStrip)
    {
        ledStrip.blank(0, this.ledCount);
        ledStrip.render();
    }
}
