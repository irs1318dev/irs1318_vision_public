package frc1318.vision.controller;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import frc1318.vision.IController;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableController implements IController
{
    private BooleanSubscriber streamEnabledEntry;
    private IntegerSubscriber processingModeEntry;

    public NetworkTableController()
    {
    }

    @Override
    public boolean open()
    {
        NetworkTable table = NetworkTableHelper.getSmartDashboard();
        this.streamEnabledEntry = table.getBooleanTopic("vision.enableStream").subscribe(false);
        this.processingModeEntry = table.getIntegerTopic("vision.processingMode").subscribe(0);
        return true;
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean getStreamEnabled()
    {
        return this.streamEnabledEntry.get();
    }

    @Override
    public int getProcessingMode()
    {
        return (int)this.processingModeEntry.get();
    }
}
