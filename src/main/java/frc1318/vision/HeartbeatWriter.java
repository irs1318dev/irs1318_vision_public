package frc1318.vision;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import frc1318.vision.helpers.NetworkTableHelper;

public class HeartbeatWriter implements Runnable, IOpenable
{
    private NetworkTableEntry heartbeat;

    private volatile boolean stop;

    private volatile boolean isConnected;
    private int currentValue;

    public HeartbeatWriter()
    {
        this.stop = false;
        this.isConnected = false;
        this.currentValue = 0;
    }

    @Override
    public boolean open()
    {
        NetworkTable table = NetworkTableHelper.getSmartDashboard();
        this.heartbeat = table.getEntry("v.heartbeat");
        return true;
    }

    @Override
    public void close()
    {
        this.currentValue = 0;
        if (this.heartbeat != null)
        {
            this.heartbeat.setDouble(VisionConstants.MAGIC_NULL_VALUE);
            this.heartbeat = null;
        }
    }

    public boolean isConnected()
    {
        return this.isConnected;
    }

    /**
     * Run the thread that captures frames and buffers the most recently retrieved frame so that an pipeline can use it.
     */
    @Override
    public void run()
    {
        try
        {
            while (!this.stop)
            {
                this.isConnected = NetworkTableHelper.isConnected();
                this.currentValue = (this.currentValue + 1) % 10000;

                if (!this.heartbeat.setDouble(this.currentValue))
                {
                    Logger.writeError(String.format("Cannot write heartbeat (%s) as double", this.heartbeat.getType()));
                    Thread.sleep(10000);
                }

                Thread.sleep(20);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        this.stop = true;
    }
}
