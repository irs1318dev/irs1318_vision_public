package frc1318.vision.leds;

import frc1318.ws2812.Color;

/*
 * An LED animation that involves a rainbow (ROYGBIV) that chases down the strip.
 */
public class ChasingRainbowAnimation implements ILEDAnimation
{
    private static final Color[] RainbowSequence =
        new Color[]
        {
            Colors.Violet,
            Colors.Indigo,
            Colors.Blue,
            Colors.Green,
            Colors.Yellow,
            Colors.Orange,
            Colors.Red,
        };

    private final int ledCount;
    private final long fullPeriodMs;
    private final double updatePeriodMs;

    private long prevElapsedMs;

    /**
     * Initializes a new instance of the ChasingRainbowAnimation class.
     * @param ledCount number of LEDs in the strip
     * @param fullPeriodMs the period (in milliseconds) of a full cycle of chasing the rainbow down the strip (must make sense in 20ms increments)
     */
    public ChasingRainbowAnimation(int ledCount, long fullPeriodMs)
    {
        if (fullPeriodMs < 1)
        {
            throw new RuntimeException("periodMs too low!");
        }

        this.ledCount = ledCount;
        this.fullPeriodMs = fullPeriodMs;
        this.updatePeriodMs = (double)fullPeriodMs / (double)ledCount;
    }

    @Override
    public void apply(ILedStrip ledStrip)
    {
        // start "off" the strip, with the full strip blanked
        this.prevElapsedMs = (long)(-ChasingRainbowAnimation.RainbowSequence.length * this.updatePeriodMs);
        ledStrip.blank(0, this.ledCount);
        ledStrip.render();
    }

    @Override
    public boolean update(long elapsedMs, ILedStrip ledStrip)
    {
        long currElapsedMs = (this.prevElapsedMs + elapsedMs) % this.fullPeriodMs;

        int prevCycleIndex = (int)(this.prevElapsedMs / this.updatePeriodMs);
        int currCycleIndex = (int)(currElapsedMs / this.updatePeriodMs);

        this.prevElapsedMs = currElapsedMs;

        if (prevCycleIndex != currCycleIndex)
        {
            for (int i = prevCycleIndex; i != currCycleIndex; i = (i + 1) % this.ledCount)
            {
                if (i >= 0)
                {
                    ledStrip.blank(i, 1);
                }
            }

            for (int i = 0; i < ChasingRainbowAnimation.RainbowSequence.length; i++)
            {
                int index = (currCycleIndex + i) % this.ledCount;
                if (index >= 0)
                {
                    ledStrip.setColor(index, ChasingRainbowAnimation.RainbowSequence[i]);
                }
            }

            // only call render when we are changing the LEDs for efficiency
            ledStrip.render();
            return true;
        }

        return false;
    }
}
