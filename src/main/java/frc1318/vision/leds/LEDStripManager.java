package frc1318.vision.leds;

import frc1318.vision.IController;
import frc1318.vision.IOpenable;
import frc1318.vision.Logger;

public class LEDStripManager implements Runnable, IOpenable
{
    private static final int CHECK_PERIOD_MS = 20;
    private static final LEDMode[] POSSIBLE_MODES = LEDMode.values();

    private final IController controller;
    private final int ledCount;

    private ILedStrip ledStrip;

    private boolean shouldStop;

    public LEDStripManager(IController controller, int ledCount, ILedStrip ledStrip)
    {
        this.controller = controller;
        this.ledCount = ledCount;

        this.ledStrip = ledStrip;
        this.shouldStop = false;
    }

    @Override
    public boolean open()
    {
        return this.ledStrip != null && this.ledStrip.open();
    }

    @Override
    public void close()
    {
        if (this.ledStrip != null)
        {
            this.ledStrip.release();
            this.ledStrip = null;
        }
    }

    public void stop()
    {
        this.shouldStop = true;
    }

    @Override
    public void run()
    {
        try
        {
            long lastUpdateMs = System.currentTimeMillis();
            LEDMode currentMode = LEDMode.Blank;

            System.out.println("Starting in mode " + currentMode);
            ILEDDesign currentDesign = new BlankDesign(this.ledCount);
            currentDesign.apply(this.ledStrip);

            while (!this.shouldStop && !Thread.interrupted())
            {
                long currentTimeMs = System.currentTimeMillis();
                long elapsedTimeMs = currentTimeMs - lastUpdateMs;

                LEDMode newMode = currentMode;
                int newModeInt = this.controller.getLedMode();
                if (newModeInt != currentMode.ordinal())
                {
                    // check if we are really changing mode...
                    newMode = LEDMode.Blank;
                    if (newModeInt >= 0 && newModeInt < LEDStripManager.POSSIBLE_MODES.length)
                    {
                        newMode = LEDStripManager.POSSIBLE_MODES[newModeInt];
                    }
                }

                // we are actually changing the mode - let's switch
                boolean updated = false;
                if (newMode != currentMode)
                {
                    System.out.println("Switching to mode " + newMode);
                    switch (newMode)
                    {
                        case Yellow:
                            currentDesign = new SingleColorDesign(this.ledCount, Colors.Yellow);
                            break;
                        case FlashingGreen:
                            currentDesign = new FlashingColorAnimation(this.ledCount, 1000, Colors.Green);
                            break;
                        case FlashingRed:
                            currentDesign = new FlashingColorAnimation(this.ledCount, 1000, Colors.Red);
                            break;
                        case ChasingRainbow:
                            currentDesign = new ChasingRainbowAnimation(this.ledCount, 6000);
                            break;
                        default:
                        case Blank:
                            currentDesign = new BlankDesign(this.ledCount);
                            break;
                    }

                    currentDesign.apply(this.ledStrip);
                    updated = true;
                    currentMode = newMode;
                }

                // check if our current mode is some sort of animation, and apply any update if so
                if (currentDesign instanceof ILEDAnimation)
                {
                    ILEDAnimation currentAnimation = (ILEDAnimation)currentDesign;
                    updated = currentAnimation.update(elapsedTimeMs, this.ledStrip);
                }

                if (!updated)
                {
                    Thread.sleep(LEDStripManager.CHECK_PERIOD_MS);
                }

                lastUpdateMs = currentTimeMs;
            }
        }
        catch (InterruptedException ex)
        {
            Logger.write("LEDStripManager thread interrupted.");
            ex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
