package frc1318.vision.helpers;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ImageUndistorter
{
    private Mat mapX;
    private Mat mapY;

    /**
     * Initializes a new instance of the ImageUndistorter class.
     * For background, see http://docs.opencv.org/3.1.0/d4/d94/tutorial_camera_calibration.html
     */
    public ImageUndistorter(
        double resolutionX,
        double resolutionY,
        double centerX,
        double centerY,
        double focalLengthX,
        double focalLengthY,
        double[] diffCoef)
    {
        Size size = new Size(resolutionX, resolutionY);
        Mat intrinsicMatrix = ImageUndistorter.buildIntrinsic(centerX, centerY, focalLengthX, focalLengthY);
        Mat distCoeffs = ImageUndistorter.buildDistortion(diffCoef);

        // initialize mapX and mapY
        Mat mapX = new Mat(size, CvType.CV_32FC1);
        Mat mapY = new Mat(size, CvType.CV_32FC1);

        // unused:
        Mat R = new Mat();
        Mat newCameraMatrix = new Mat();

        Calib3d.initUndistortRectifyMap(intrinsicMatrix, distCoeffs, R, newCameraMatrix, size, CvType.CV_32FC1, mapX, mapY);

        this.mapX = mapX;
        this.mapY = mapY;

        intrinsicMatrix.release();
        distCoeffs.release();

        R.release();
        newCameraMatrix.release();
    }

    /**
     * Undistort the frame so that straight lines appear straight in the image
     * @param soruceFrame to undirsort
     * @param targetFrame to contain undistorted data
     */
    public void undistortFrame(Mat sourceFrame, Mat targetFrame)
    {
        Imgproc.remap(sourceFrame, targetFrame, this.mapX, this.mapY, Imgproc.INTER_LINEAR, Imgproc.WARP_FILL_OUTLIERS, new Scalar(0));
    }

    /**
     * Build an intrinsic matrix
     * @param focalLengthY
     * @param focalLengthX
     * @param centerY
     * @param centerX
     * @return an intrinsic matrix
     */
    private static Mat buildIntrinsic(double centerX, double centerY, double focalLengthX, double focalLengthY)
    {
        Mat intrinsicMatrix = new Mat(3, 3, CvType.CV_32FC1);

        intrinsicMatrix.put(0, 0, focalLengthX); // focal length x
        intrinsicMatrix.put(0, 1, 0.0);
        intrinsicMatrix.put(0, 2, centerX); // center x

        intrinsicMatrix.put(1, 0, 0.0);
        intrinsicMatrix.put(1, 1, focalLengthY); // focal length y
        intrinsicMatrix.put(1, 2, centerY); // center y

        intrinsicMatrix.put(2, 0, 0.0);
        intrinsicMatrix.put(2, 1, 0.0);
        intrinsicMatrix.put(2, 2, 1.0); // flat z

        return intrinsicMatrix;
    }

    /**
     * Build a distortion coefficient matrix
     * @param diffCoef
     * @return a distortion matrix
     */
    private static Mat buildDistortion(double[] diffCoef)
    {
        Mat distortionCoeffs = new Mat(1, 5, CvType.CV_32FC1);

        distortionCoeffs.put(0, 0, diffCoef[0]);
        distortionCoeffs.put(0, 1, diffCoef[1]);
        distortionCoeffs.put(0, 2, diffCoef[2]);
        distortionCoeffs.put(0, 3, diffCoef[3]);
        distortionCoeffs.put(0, 4, diffCoef[4]);

        return distortionCoeffs;
    }
}
