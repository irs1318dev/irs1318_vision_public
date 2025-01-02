package frc1318.vision.leds;

import frc1318.ws2812.Color;

/*
 * An LED design that applies a single color to all LEDs in the strip.
 */
public class SingleColorDesign implements ILEDDesign
{
    private final int ledCount;
    private final Color color;

    /**
     * Initializes a new instance of the SingleColorDesign class.
     * @param ledCount number of LEDs in the strip
     * @param color the color to set to all LEDs
     */
    public SingleColorDesign(int ledCount, Color color)
    {
        this.ledCount = ledCount;
        this.color = color;
    }

    @Override
    public void apply(ILedStrip ledStrip)
    {
        ledStrip.setColor(0, this.ledCount, this.color);
        ledStrip.render();
    }
}
