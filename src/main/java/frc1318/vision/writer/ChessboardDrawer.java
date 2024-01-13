package frc1318.vision.writer;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point3;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import frc1318.vision.VisionConstants;
import frc1318.vision.helpers.MatrixHelper;

public class ChessboardDrawer extends ImageDrawer<MatOfPoint2f>
{
    private final static int RESULT_GAP = 1;
    private final Size chessboardSize; 

    private Mat viewGray;

    // various variables needed for the calibration
    private Size resolution;
    private int count;
    private List<Mat> imagePoints;
    private List<Mat> objectPoints;
    private MatOfPoint3f obj;

    public ChessboardDrawer(Size chessboardSize)
    {
        super();

        this.chessboardSize = chessboardSize;
        this.viewGray = new Mat();

        this.resolution = null;
        this.count = 0;
        this.imagePoints = new ArrayList<Mat>();
        this.objectPoints = new ArrayList<Mat>();

        this.obj = new MatOfPoint3f();
        for (int i = 0; i < chessboardSize.height; i++)
        {
            for (int j = 0; j < chessboardSize.width; j++)
            {
                this.obj.push_back(new MatOfPoint3f(new Point3(i, j, 0.0f)));
            }
        }
    }

    @Override
    public void close()
    {
        super.close();

        int points = this.objectPoints.size();
        System.out.println(String.format("points: %d", points));
        if (points > 15)
        {
            // init needed variables according to OpenCV docs
            List<Mat> rvecs = new ArrayList<Mat>();
            List<Mat> tvecs = new ArrayList<Mat>();

            Mat cameraMatrix = new Mat(3, 3, CvType.CV_32FC1);
            cameraMatrix.put(0, 0, VisionConstants.ELP_CAMERA_FOCAL_LENGTH_X);
            cameraMatrix.put(0, 2, this.resolution.width / 2.0);
            cameraMatrix.put(1, 1, VisionConstants.ELP_CAMERA_FOCAL_LENGTH_Y);
            cameraMatrix.put(1, 2, this.resolution.height / 2.0);
            cameraMatrix.put(2, 2, 1.0);

            Mat distCoeffs = new Mat();

            double reprojectionError = Calib3d.calibrateCamera(this.objectPoints, this.imagePoints, this.resolution, cameraMatrix, distCoeffs, rvecs, tvecs, Calib3d.CALIB_USE_INTRINSIC_GUESS);
            System.out.println(String.format("resolution: %f x %f", this.resolution.width, this.resolution.height));
            System.out.println(String.format("cameraMatrix: %s", MatrixHelper.toString(cameraMatrix)));
            System.out.println(String.format("distCoeffs: %s", MatrixHelper.toString(distCoeffs)));
            System.out.println(String.format("reprojectionError: %f", reprojectionError));

            cameraMatrix.release();
            distCoeffs.release();
        }
    }

    @Override
    public void write(MatOfPoint2f result, Mat sourceFrame)
    {
        if (result != null)
        {
            Imgproc.cvtColor(sourceFrame, this.viewGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cornerSubPix(this.viewGray, result, new Size(11, 11), new Size(-1, -1), new TermCriteria(TermCriteria.EPS+ TermCriteria.COUNT, 30, 0.1 ));
            Calib3d.drawChessboardCorners(sourceFrame, this.chessboardSize, result, true);

            if ((this.count++ % ChessboardDrawer.RESULT_GAP) == 0)
            {
                if (this.resolution == null)
                {
                    this.resolution = sourceFrame.size();
                }

                this.imagePoints.add(result);
                this.objectPoints.add(this.obj);
            }
        }

        super.write(result, sourceFrame);
    }
}
