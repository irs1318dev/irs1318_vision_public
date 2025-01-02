package frc1318.vision.reader;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import frc1318.vision.CameraSettings;
import frc1318.vision.IFrameReader;
import frc1318.vision.helpers.Pair;

public class LocalImageFileReader implements IFrameReader
{
    private final boolean readForever;

    private String fileName;

    /**
     * Initializes a new instance of the LocalImageFileReader class.
     * @param fileName of the file to read to select a frame
     */
    public LocalImageFileReader(String fileName)
    {
        this(fileName, false);
    }

    /**
     * Initializes a new instance of the LocalImageFileReader class.
     * @param fileName of the file to read to select a frame
     * @param readForever if we should keep reading the same image forever
     */
    public LocalImageFileReader(String fileName, boolean readForever)
    {
        this.readForever = readForever;

        this.fileName = fileName;
    }

    /**
     * Retrieve an image frame from the local image file
     * @return frame of an image
     * @throws InterruptedException
     */
    @Override
    public Pair<Mat, Long> getCurrentFrame() throws InterruptedException
    {
        if (this.fileName == null)
        {
            return null;
        }

        Mat image = Imgcodecs.imread(this.fileName);
        if (!this.readForever)
        {
            this.fileName = null;
        }

        return new Pair<Mat, Long>(image, System.currentTimeMillis());
    }

    /**
     * Open the frame reader
     * @return true if successful
     */
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
    public void setSettings(CameraSettings settings)
    {
    }
}
