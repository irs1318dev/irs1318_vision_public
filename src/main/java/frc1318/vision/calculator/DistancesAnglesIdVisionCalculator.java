package frc1318.vision.calculator;

import org.opencv.core.Mat;

import frc1318.apriltag.*;

import frc1318.vision.IResultWriter;
import frc1318.vision.VisionConstants;

public class DistancesAnglesIdVisionCalculator implements IResultWriter<AprilTagDetection>
{
    private final IResultWriter<DistancesAnglesIdMeasurements> writer;

    private final double tagSize;
    private final double cameraFocalX;
    private final double cameraFocalY;
    private final double cameraCenterX;
    private final double cameraCenterY;
    private final double cameraYaw;
    private final double cameraPitch;
    private final double cameraRoll;
    private final double cameraXOffset;
    private final double cameraYOffset;
    private final double cameraZOffset;

    private final double[] offset;
    private final double[] ypr;

    private Mat4 t_camera_rel_robot;

    /**
     * Initializes a new instance of the DistancesAnglesVisionCalculator class.
     * @param writer of results
     * @param tagSize in inches
     * @param centerX center point of the image frame (x component)
     * @param centerY center point of the image frame (y component)
     * @param focalX focal length along the x axis
     * @param focalY focal length along the y axis
     * @param cameraRoll mounting twist along the x axis (front to back, front positive) of the camera, in degrees
     * @param cameraPitch mounting tilt along the y axis (left to right, left positive) of the camera, in degrees
     * @param cameraYaw mounting angle along the z axis (up to down, up positive) of the camera, in degrees
     * @param cameraDepthOffset mounting distance of the camera along the x axis (forward positive)
     * @param cameraHorizontalOffset mounting distance of the camera along the y axis (left positive)
     * @param cameraVerticalOffset mounting distance of the camera along the z axis (up positive)
     */
    public DistancesAnglesIdVisionCalculator(
        IResultWriter<DistancesAnglesIdMeasurements> writer,
        double tagSize,
        double cameraCenterX,
        double cameraCenterY,
        double cameraFocalX,
        double cameraFocalY,
        double cameraYaw,
        double cameraPitch,
        double cameraRoll,
        double cameraXOffset,
        double cameraYOffset,
        double cameraZOffset)
    {
        this.writer = writer;

        this.tagSize = tagSize;
        this.cameraFocalX = cameraFocalX;
        this.cameraFocalY = cameraFocalY;
        this.cameraCenterX = cameraCenterX;
        this.cameraCenterY = cameraCenterY;
        this.cameraYaw = cameraYaw;
        this.cameraPitch = cameraPitch;
        this.cameraRoll = cameraRoll;
        this.cameraXOffset = cameraXOffset;
        this.cameraYOffset = cameraYOffset;
        this.cameraZOffset = cameraZOffset;

        this.offset = new double[3];
        this.ypr = new double[3];
    }

    public DistancesAnglesIdMeasurements calculate(AprilTagDetection detection)
    {
        if (detection == null)
        {
            return null;
        }

        if (this.t_camera_rel_robot == null)
        {
            this.t_camera_rel_robot = Mat4.createAffine(
                this.cameraYaw,
                this.cameraPitch,
                this.cameraRoll,
                this.cameraXOffset,
                this.cameraYOffset,
                this.cameraZOffset,
                1);
        }

        AprilTagPose pose = detection.estimateTagPose(
            this.tagSize,
            this.cameraFocalX,
            this.cameraFocalY,
            this.cameraCenterX,
            this.cameraCenterY,
            this.t_camera_rel_robot,
            this.offset,
            this.ypr);

        Mat4 t_apriltag_rel_robot = pose.getTransformation();
        if (VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            System.out.println(String.format("Affine Transformation: %s, error: %f", t_apriltag_rel_robot.toString(), pose.getError()));
            System.out.println(String.format("Offsets: (%f, %f, %f), Yaw: %f, Pitch: %f, Roll: %f", this.offset[0], this.offset[1], this.offset[2], this.ypr[0], this.ypr[1], this.ypr[2]));
        }

        t_apriltag_rel_robot.release();
        return new DistancesAnglesIdMeasurements(this.offset[0], this.offset[1], this.offset[2], this.ypr[0], this.ypr[1], this.ypr[2], detection.getId());
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public void close()
    {
        if (this.t_camera_rel_robot != null)
        {
            this.t_camera_rel_robot.release();
            this.t_camera_rel_robot = null;
        }
    }

    @Override
    public void write(AprilTagDetection result, Mat mat)
    {
        this.writer.write(this.calculate(result), mat);
    }

    @Override
    public void write(AprilTagDetection result)
    {
        this.writer.write(this.calculate(result));
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
        this.writer.outputDebugFrame(frame);
    }
}
