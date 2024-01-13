package frc1318.vision.pipeline;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;

import frc1318.vision.IFramePipeline;
import frc1318.vision.IResultWriter;

public class CalibrationPipeline implements IFramePipeline
{
    private static final int CHESSBOARD_FLAGS = Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_NORMALIZE_IMAGE;

    private final IResultWriter<MatOfPoint2f> output;
    private final Size boardSize;

    private MatOfPoint2f currentImagePoints;

    /**
     * Initializes a new instance of the CalibrationPipeline class.
     * @param writer to output frames to
     */
    public CalibrationPipeline(IResultWriter<MatOfPoint2f> writer, Size boardSize)
    {
        this.output = writer;
        this.boardSize = boardSize;

        this.currentImagePoints = new MatOfPoint2f();
    }

    /**
     * Process a single image frame
     * @param frame image to process
     */
    @Override
    public void process(Mat sourceFrame)
    {
        MatOfPoint2f points = this.currentImagePoints;
        boolean isTemplateFound = Calib3d.findChessboardCorners(sourceFrame, this.boardSize, points, CalibrationPipeline.CHESSBOARD_FLAGS);

        if (isTemplateFound)
        {
            this.currentImagePoints = new MatOfPoint2f();
        }
        else
        {
            points = null;
        }

        this.output.write(points, sourceFrame);
    }
}
