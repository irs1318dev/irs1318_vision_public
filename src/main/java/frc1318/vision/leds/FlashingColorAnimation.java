package frc1318.vision.leds;

import frc1318.ws2812.Color;

/*
 * An LED animation that alternates between blanking the LEDs and applying a single color to all LEDs in the strip over a certain period.
 */
public class FlashingColorAnimation implements ILEDAnimation
{
    private final int ledCount;
    private final long periodMs;
    private final Color color;

    private long prevElapsedMs;

    /**
     * Initializes a new instance of the FlashingColorAnimation class.
     * @param ledCount number of LEDs in the strip
     * @param periodMs the period (in milliseconds) of a full cycle (off then on), must be >= 2
     * @param color the color to set to all LEDs
     */
    public FlashingColorAnimation(int ledCount, long periodMs, Color color)
    {
        if (periodMs < 2)
        {
            throw new RuntimeException("periodMs too low!");
        }

        this.ledCount = ledCount;
        this.periodMs = periodMs;
        this.color = color;
    }

    @Override
    public void apply(ILedStrip ledStrip)
    {
        this.prevElapsedMs = 0L;
        ledStrip.blank(0, this.ledCount);
        ledStrip.render();
    }

    @Override
    public boolean update(long elapsedMs, ILedStrip ledStrip)
    {
        long currElapsedMs = (this.prevElapsedMs + elapsedMs) % this.periodMs;

        boolean prevBlank = this.prevElapsedMs < (this.periodMs / 2);
        boolean currBlank = currElapsedMs < (this.periodMs / 2);

        this.prevElapsedMs = currElapsedMs;

        if (prevBlank != currBlank)
        {
            if (currBlank)
            {
                ledStrip.blank(0, this.ledCount);
            }
            else
            {
                ledStrip.setColor(0, this.ledCount, this.color);
            }

            // only call render when we are changing the LEDs for efficiency
            ledStrip.render();
            return true;
        }

        return false;
    }
}
