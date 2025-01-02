package frc1318.vision.calculator;

import org.opencv.core.Mat;

import frc1318.apriltag.*;
import frc1318.opencv.*;
import frc1318.vision.FieldLayout;
import frc1318.vision.IResultWriter;
import frc1318.vision.Logger;
import frc1318.vision.VisionConstants;

public class AbsolutePositionVisionCalculator implements IResultWriter<AprilTagDetection>
{
    private final IResultWriter<AbsolutePositionMeasurement> writer;

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

    private Mat4 t_robot_rel_camera;

    /**
     * Initializes a new instance of the AbsolutePositionVisionCalculator class.
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
    public AbsolutePositionVisionCalculator(
        IResultWriter<AbsolutePositionMeasurement> writer,
        double tagSize,
        double cameraCenterX,
        double cameraCenterY,
        double cameraFocalX,
        double cameraFocalY,
        double cameraRoll,
        double cameraPitch,
        double cameraYaw,
        double cameraXOffset,
        double cameraYOffset,
        double cameraZOffset)
    {
        this.writer = writer;

        this.tagSize = tagSize;
        this.cameraCenterX = cameraCenterX;
        this.cameraCenterY = cameraCenterY;
        this.cameraFocalX = cameraFocalX;
        this.cameraFocalY = cameraFocalY;
        this.cameraRoll = cameraRoll;
        this.cameraPitch = cameraPitch;
        this.cameraYaw = cameraYaw;
        this.cameraXOffset = cameraXOffset;
        this.cameraYOffset = cameraYOffset;
        this.cameraZOffset = cameraZOffset;

        this.offset = new double[3];
        this.ypr = new double[3];
    }

    public AbsolutePositionMeasurement calculate(AprilTagDetection detection)
    {
        if (detection == null)
        {
            return null;
        }

        if (this.t_robot_rel_camera == null)
        {
            Mat4 t_camera_rel_robot = Mat4.createAffine(
                this.cameraYaw,
                this.cameraPitch,
                this.cameraRoll,
                this.cameraXOffset,
                this.cameraYOffset,
                this.cameraZOffset,
                1);

            this.t_robot_rel_camera = t_camera_rel_robot.invertAffine();
            t_camera_rel_robot.release();
        }

        Mat4 t_apriltag_rel_field = FieldLayout.AprilTagIdToAffineTransformationMap.get(detection.getId());

        AprilTagPose robotPose = detection.estimateAbsolutePose(
            this.tagSize,
            this.cameraFocalX,
            this.cameraFocalY,
            this.cameraCenterX,
            this.cameraCenterY,
            this.t_robot_rel_camera,
            t_apriltag_rel_field,
            this.offset,
            this.ypr);

        Mat4 t_robot_rel_field = robotPose.getTransformation();
        if (VisionConstants.DEBUG_PRINT_OUTPUT)
        {
            Logger.write(String.format("Affine Transformation: %s, error: %f", t_robot_rel_field.toString(), robotPose.getError()));
            Logger.write(String.format("Offsets: (%f, %f, %f), Yaw: %f, Pitch: %f, Roll: %f", this.offset[0], this.offset[1], this.offset[2], this.ypr[0], this.ypr[1], this.ypr[2]));
        }

        t_robot_rel_field.release();
        return new AbsolutePositionMeasurement(this.offset[0], this.offset[1], this.offset[2], this.ypr[0], this.ypr[1], this.ypr[2], detection.getId(), detection.getDecisionMargin(), robotPose.getError());
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public void close()
    {
        if (this.t_robot_rel_camera != null)
        {
            this.t_robot_rel_camera.release();
            this.t_robot_rel_camera = null;
        }
    }

    @Override
    public void write(AprilTagDetection result, long captureTime, Mat mat)
    {
        this.writer.write(this.calculate(result), captureTime, mat);
    }

    @Override
    public void write(AprilTagDetection result, long captureTime)
    {
        this.writer.write(this.calculate(result), captureTime);
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
        this.writer.outputDebugFrame(frame);
    }
}
