package frc1318.vision.writer;

import org.opencv.core.Mat;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.util.PixelFormat;
import edu.wpi.first.cscore.*;

import frc1318.vision.IResultWriter;
import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.NetworkTableHelper;

public abstract class NetworkTableResultWriterBase<T> implements IResultWriter<T>
{
    private final String debugStreamName;
    private final int streamResolutionX;
    private final int streamResolutionY;

    private CvSource debugFrameWriter;

    protected NetworkTableResultWriterBase(
        String debugStreamName,
        int streamResolutionX,
        int streamResolutionY)
    {
        this.debugStreamName = debugStreamName;
        this.streamResolutionX = streamResolutionX;
        this.streamResolutionY = streamResolutionY;

        this.debugFrameWriter = null;
    }

    @Override
    public boolean open()
    {
        NetworkTable table = NetworkTableHelper.getSmartDashboard();
        this.createEntries(table);

        if (VisionConstants.DEBUG && VisionConstants.DEBUG_FRAME_STREAM && this.debugStreamName != null)
        {
            this.debugFrameWriter = new CvSource(this.debugStreamName, PixelFormat.kMJPEG, this.streamResolutionX, this.streamResolutionY, 50);
            MjpegServer mjpegServer = CameraServer.startAutomaticCapture(this.debugFrameWriter);
            mjpegServer.setCompression(VisionConstants.STREAMING_COMPRESSION);
        }

        return true;
    }

    @Override
    public void close()
    {
        if (this.debugFrameWriter != null)
        {
            this.debugFrameWriter.close();
            this.debugFrameWriter = null;
        }
    }

    protected abstract void createEntries(NetworkTable table);

    @Override
    public void outputDebugFrame(Mat frame)
    {
        if (frame != null && 
            VisionConstants.DEBUG &&
            VisionConstants.DEBUG_FRAME_STREAM)
        {
            this.debugFrameWriter.putFrame(frame);
        }
    }
}
