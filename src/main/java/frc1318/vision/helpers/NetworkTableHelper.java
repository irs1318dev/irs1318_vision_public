package frc1318.vision.helpers;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc1318.vision.VisionConstants;

public class NetworkTableHelper
{
    private static final Object lock = new Object();

    private static NetworkTableInstance instance;
    private static NetworkTable smartDashboardTable;
    private static NetworkTable akroTable;

    public static NetworkTableInstance getInstance()
    {
        if (NetworkTableHelper.instance == null)
        {
            synchronized(NetworkTableHelper.lock)
            {
                if (NetworkTableHelper.instance == null)
                {
                    NetworkTableInstance inst = NetworkTableInstance.getDefault();
                    inst.startClient4("IRS1318 Vision");
                    inst.setServerTeam(VisionConstants.TEAM_NUMBER);
                    // set refresh rate: (VisionConstants.NETWORK_UPDATE_RATE);
                    NetworkTableHelper.instance = inst;
                }
            }
        }

        return NetworkTableHelper.instance;
    }

    public static NetworkTable getSmartDashboard()
    {
        if (NetworkTableHelper.smartDashboardTable == null)
        {
            NetworkTableHelper.smartDashboardTable = NetworkTableHelper.getInstance().getTable("SmartDashboard");
        }

        return NetworkTableHelper.smartDashboardTable;
    }

    public static NetworkTable getAdvKitRealOutputs()
    {
        if (NetworkTableHelper.akroTable == null)
        {
            NetworkTableHelper.akroTable = NetworkTableHelper.getInstance().getTable("AdvantageKit").getSubTable("RealOutputs");
        }

        return NetworkTableHelper.akroTable;
    }

    public static NetworkTable getRobotOutputTable()
    {
        if (VisionConstants.USE_ADVANTAGE_KIT)
        {
            return NetworkTableHelper.getAdvKitRealOutputs();
        }
        else
        {
            return NetworkTableHelper.getSmartDashboard();
        }
    }

    public static void flush()
    {
        NetworkTableHelper.getInstance().flush();
    }

    public static boolean isConnected()
    {
        return NetworkTableHelper.getInstance().isConnected();
    }
}