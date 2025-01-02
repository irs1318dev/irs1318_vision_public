package frc1318.vision.leds;

import frc1318.ws2812.Color;

public class WS2812LedStrip implements ILedStrip
{
    private frc1318.ws2812.WS2812LedStrip ledStrip;

    /**
     * Initializes a new instance of the WS2812LedStrip class.
     * @param ledCount number of LEDs that we will attempt to control on the strip
     */
    public WS2812LedStrip(int ledCount)
    {
        this.ledStrip = frc1318.ws2812.WS2812LedStrip.create(ledCount);
    }

    /**
     * Open the LED strip for writing
     * @return true if the strip was opened, otherwise false
     */
    @Override
    public boolean open()
    {
       return this.ledStrip.open();
    }

    /**
     * Clear the specified range of LEDs
     * @param startIndex the index of the first LED to clear
     * @param count the number of LEDs to clear
     */
    @Override
    public void blank(int startIndex, int count)
    {
        this.ledStrip.blank(startIndex, count);
    }

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param color the color to set the LED to
     */
    @Override
    public void setColor(int index, Color color)
    {
        this.ledStrip.setColor(index, color);
    }

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param color the color to set the LEDs to
     */
    @Override
    public void setColor(int startIndex, int count, Color color)
    {
        this.ledStrip.setColor(startIndex, count, color);
    }

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    @Override
    public void setColor(int index, byte r, byte g, byte b)
    {
        this.ledStrip.setColor(index, r, g, b);
    }

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    @Override
    public void setColor(int startIndex, int count, byte r, byte g, byte b)
    {
        this.ledStrip.setColor(startIndex, count, r, g, b);
    }

    /**
     * Render the LEDs to the strip
     */
    @Override
    public void render()
    {
       this.ledStrip.render();
    }

    /**
     * Release the resources for using the LED strip
     */
    @Override
    public void release()
    {
        this.ledStrip.release();
    }
}
