package frc1318.vision.reader;

import java.io.File;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import frc1318.vision.CameraSettings;
import frc1318.vision.IFrameReader;
import frc1318.vision.helpers.Pair;

public class LocalImageDirectoryReader implements IFrameReader
{
    private final File[] files;

    private int index;

    /**
     * Initializes a new instance of the LocalImageDirectoryReader class.
     * @param dirName of the file to read to select a frame
     */
    public LocalImageDirectoryReader(String dirName)
    {
        File file = new File(dirName);
        if (!file.exists())
        {
            throw new RuntimeException("no such file " + dirName);
        }

        if (!file.isDirectory())
        {
            throw new RuntimeException("no such directory " + dirName);
        }

        this.files = file.listFiles();
        this.index = 0;
    }

    /**
     * Retrieve an image frame from the local image file
     * @return frame of an image
     * @throws InterruptedException
     */
    @Override
    public Pair<Mat, Long> getCurrentFrame() throws InterruptedException
    {
        if (this.files == null || this.index >= this.files.length)
        {
            return null;
        }

        return new Pair<Mat, Long>(Imgcodecs.imread(this.files[this.index++].getAbsolutePath()), System.currentTimeMillis());
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
