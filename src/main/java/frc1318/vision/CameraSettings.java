package frc1318.vision;

public class CameraSettings
{
    public final int Exposure;
    public final int Brightness;
    public final int ResolutionX;
    public final int ResolutionY;
    public final int FramesPerSecond;

    /**
     * Initializes a new instance of the CameraSettings class.
     * @param exposure
     * @param brightness
     * @param resolutionX
     * @param resolutionY
     * @param framesPerSecond
     */
    public CameraSettings(
        int exposure,
        int brightness,
        int resolutionX,
        int resolutionY,
        int framesPerSecond)
    {
        this.Exposure = exposure;
        this.Brightness = brightness;
        this.ResolutionX = resolutionX;
        this.ResolutionY = resolutionY;
        this.FramesPerSecond = framesPerSecond;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            // try reference equality
            return true;
        }

        if (obj == null)
        {
            // special-case null
            return false;
        }

        if (!(obj instanceof CameraSettings))
        {
            // not equal if the other isn't a CameraSettings object...
            return false;
        }

        CameraSettings other = (CameraSettings)obj;
        return other.Exposure == this.Exposure &&
            other.Brightness == this.Brightness &&
            other.ResolutionX == this.ResolutionX &&
            other.ResolutionY == this.ResolutionY &&
            other.FramesPerSecond == this.FramesPerSecond;
    }
}
