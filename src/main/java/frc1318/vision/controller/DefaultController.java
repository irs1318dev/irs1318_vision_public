package frc1318.vision.controller;

import frc1318.vision.IController;

public class DefaultController implements IController
{
    private final int mode;

    public DefaultController()
    {
        this(1);
    }

    public DefaultController(int mode)
    {
        this.mode = mode;
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean getStreamEnabled()
    {
        return false;
    }

    @Override
    public int getProcessingMode()
    {
        return this.mode;
    }
}
