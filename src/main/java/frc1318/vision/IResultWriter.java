package frc1318.vision;

import org.opencv.core.Mat;

public interface IResultWriter<T> extends IOpenable
{
    /**
     * Write a result
     * @param result to write
     * @param sourceFrame that it came from
     */
    public void write(T result, Mat sourceFrame);

    /**
     * Write a result
     * @param result to write
     */
    public void write(T result);

    /**
     * Output a debug camera frame
     * @param frame to output
     */
    public void outputDebugFrame(Mat frame);
}
