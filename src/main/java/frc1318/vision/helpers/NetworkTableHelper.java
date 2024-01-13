package frc1318.vision.helpers;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc1318.vision.VisionConstants;

public class NetworkTableHelper
{
    private static NetworkTableInstance instance;
    private static NetworkTable smartDashboardTable;

    public static NetworkTableInstance getInstance()
    {
        if (NetworkTableHelper.instance == null)
        {
            NetworkTableInstance inst = NetworkTableInstance.getDefault();
            inst.startClient4("IRS1318 Vision");
            inst.setServerTeam(VisionConstants.TEAM_NUMBER);
            // set refresh rate: (VisionConstants.NETWORK_UPDATE_RATE);
            NetworkTableHelper.instance = inst;
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

    public static void flush()
    {
        NetworkTableHelper.getInstance().flush();
    }
}