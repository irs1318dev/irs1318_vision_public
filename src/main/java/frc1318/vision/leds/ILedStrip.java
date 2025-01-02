package frc1318.vision.leds;

import frc1318.ws2812.Color;

public interface ILedStrip
{
    /**
     * Open the LED strip for writing
     * @return true if the strip was opened, otherwise false
     */
    public boolean open();

    /**
     * Clear the specified range of LEDs
     * @param startIndex the index of the first LED to clear
     * @param count the number of LEDs to clear
     */
    public void blank(int startIndex, int count);

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param color the color to set the LED to
     */
    public void setColor(int index, Color color);

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param color the color to set the LEDs to
     */
    public void setColor(int startIndex, int count, Color color);

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    public void setColor(int index, byte r, byte g, byte b);

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    public void setColor(int startIndex, int count, byte r, byte g, byte b);

    /**
     * Render the LEDs to the strip
     */
    public void render();

    /**
     * Release the resources for using the LED strip
     */
    public void release();
}
