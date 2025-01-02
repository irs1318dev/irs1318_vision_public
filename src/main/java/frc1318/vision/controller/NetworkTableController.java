package frc1318.vision.controller;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.StringSubscriber;
import frc1318.vision.HeartbeatWriter;
import frc1318.vision.IController;
import frc1318.vision.Logger;
import frc1318.vision.helpers.NetworkTableHelper;

public class NetworkTableController implements IController
{
    private final HeartbeatWriter heartbeatWriter;

    private BooleanSubscriber streamEnabledEntry;
    private IntegerSubscriber processingModeEntry;
    private StringSubscriber desiredTargetEntry;
    private IntegerSubscriber ledModeEntry;

    private String lastDesiredTargetString;
    private List<Integer> lastDesiredTarget;

    public NetworkTableController(HeartbeatWriter heartbeatWriter)
    {
        this.heartbeatWriter = heartbeatWriter;
    }

    @Override
    public boolean open()
    {
        NetworkTable table = NetworkTableHelper.getRobotOutputTable();
        this.streamEnabledEntry = table.getBooleanTopic("vision/enableStream").subscribe(false);
        this.processingModeEntry = table.getIntegerTopic("vision/processingMode").subscribe(0);
        this.desiredTargetEntry = table.getStringTopic("vision/desiredTarget").subscribe(null);
        this.ledModeEntry = table.getIntegerTopic("vision/ledMode").subscribe(0);
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
    public boolean isEnabled()
    {
        return this.heartbeatWriter.isConnected();
    }

    @Override
    public int getProcessingMode()
    {
        return (int)this.processingModeEntry.get();
    }

    @Override
    public List<Integer> getDesiredTarget()
    {
        String desiredTargetString = this.desiredTargetEntry.get();
        if (desiredTargetString == null && this.lastDesiredTargetString == null)
        {
            return null;
        }

        if (desiredTargetString == null || desiredTargetString.length() == 0)
        {
            this.lastDesiredTargetString = null;
            this.lastDesiredTarget = null;
            return null;
        }

        // use our cached array if unchanged
        if (this.lastDesiredTargetString != null && this.lastDesiredTargetString.equals(desiredTargetString))
        {
            return this.lastDesiredTarget;
        }

        // actually parse desiredTarget
        String[] splitDesiredTargetString = desiredTargetString.split(",");
        if (splitDesiredTargetString == null)
        {
            this.lastDesiredTargetString = desiredTargetString;
            this.lastDesiredTarget = null;
            return null;
        }

        List<Integer> desiredTarget = new ArrayList<Integer>(splitDesiredTargetString.length);
        for (String targetStr : splitDesiredTargetString)
        {
            try
            {
                int target = Integer.parseInt(targetStr);
                desiredTarget.add(target);
            }
            catch (NumberFormatException ex)
            {
                Logger.writeError(String.format("Unknown number \"%s\". %s", targetStr, ex.toString()));
                this.lastDesiredTargetString = desiredTargetString;
                this.lastDesiredTarget = null;
                return null;
            }
        }

        this.lastDesiredTargetString = desiredTargetString;
        this.lastDesiredTarget = desiredTarget;
        return desiredTarget;
    }

    @Override
    public int getLedMode()
    {
        return (int)this.ledModeEntry.get();
    }
}
