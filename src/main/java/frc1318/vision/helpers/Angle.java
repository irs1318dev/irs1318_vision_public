package frc1318.vision.helpers;

import frc1318.vision.VisionConstants;

public class Angle
{
    public static double tand(double angleDeg)
    {
        return Math.tan(angleDeg * VisionConstants.RADIANS_PER_DEGREE);
    }

    public static double atan2d(double y, double x)
    {
        return Math.atan2(y, x) * VisionConstants.DEGREES_PER_RADIAN;
    }

    public static double atand(double value)
    {
        return Math.atan(value) * VisionConstants.DEGREES_PER_RADIAN;
    }

    public static double cosd(double angleDeg)
    {
        return Math.cos(angleDeg * VisionConstants.RADIANS_PER_DEGREE);
    }

    public static double acosd(double value)
    {
        return Math.acos(value) * VisionConstants.DEGREES_PER_RADIAN;
    }

    public static double sind(double angleDeg)
    {
        return Math.sin(angleDeg * VisionConstants.RADIANS_PER_DEGREE);
    }

    public static double asind(double value)
    {
        return Math.asin(value) * VisionConstants.DEGREES_PER_RADIAN;
    }
}
