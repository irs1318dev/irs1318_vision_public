package frc1318.vision;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Logger implements IOpenable
{
    private static Logger instance = new Logger(null);

    private final File directory;
    private File logFile;
    private FileOutputStream stream;

    public Logger(File directory)
    {
        this.directory = directory;
        Logger.instance = this;
    }

    public static void write(String content)
    {
        Logger.instance.internalWrite(content);
    }

    public static void writeError(String content)
    {
        Logger.instance.internalWriteError(content);
    }

    public static void flush()
    {
        Logger.instance.internalFlush();
    }

    @Override
    public boolean open()
    {
        if (this.directory != null)
        {
            this.logFile = new File(directory, String.format("%d.log.txt", System.currentTimeMillis()));
            if (this.logFile.exists())
            {
                this.logFile.delete();
            }

            try
            {
                this.stream = new FileOutputStream(this.logFile.getAbsolutePath());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                this.stream = null;
                return false;
            }
        }

        return true;
    }

    @Override
    public void close()
    {
        if (this.stream != null)
        {
            try
            {
                this.stream.flush();
                this.stream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            this.stream = null;
        }
    }

    private void internalWrite(String content)
    {
        System.out.println(content);
        if (this.stream != null)
        {
            try
            {
                this.stream.write(content.getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void internalWriteError(String content)
    {
        System.err.println(content);
        if (this.stream != null)
        {
            try
            {
                this.stream.write(content.getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void internalFlush()
    {
        if (this.stream != null)
        {
            try
            {
                this.stream.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
