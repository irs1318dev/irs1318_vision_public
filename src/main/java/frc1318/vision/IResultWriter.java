package frc1318.vision;

import org.opencv.core.Mat;

public interface IResultWriter<T> extends IOpenable
{
    /**
     * Write a result
     * @param result to write
     * @param captureTime when the image was captured
     * @param sourceFrame that it came from
     */
    public void write(T result, long captureTime, Mat sourceFrame);

    /**
     * Write a result
     * @param result to write
     * @param captureTime when the image was captured
     */
    public void write(T result, long captureTime);

    /**
     * Output a debug camera frame
     * @param frame to output
     */
    public void outputDebugFrame(Mat frame);
}
