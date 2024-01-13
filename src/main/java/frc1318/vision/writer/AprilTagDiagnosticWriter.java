package frc1318.vision.writer;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import frc1318.apriltag.AprilTagDetection;
import frc1318.apriltag.AprilTagPose;
import frc1318.apriltag.Mat4;
import frc1318.vision.IResultWriter;

public class AprilTagDiagnosticWriter implements IResultWriter<AprilTagDetection>
{
    private final IResultWriter<Point> pointWriter;
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

    private double[] offset;
    private double[] ypr;

    private Mat4 t_camera_rel_robot;

    public AprilTagDiagnosticWriter(
        IResultWriter<Point> pointWriter,
        double tagSize,
        double cameraFocalX,
        double cameraFocalY,
        double cameraCenterX,
        double cameraCenterY,
        double cameraRoll,
        double cameraPitch,
        double cameraYaw,
        double cameraXOffset,
        double cameraYOffset,
        double cameraZOffset)
    {
        this.pointWriter = pointWriter;

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
    public void write(AprilTagDetection detection, Mat sourceFrame)
    {
        this.pointWriter.write(this.writeDiagnostic(detection), sourceFrame);
    }

    @Override
    public void write(AprilTagDetection detection)
    {
        this.pointWriter.write(this.writeDiagnostic(detection));
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
        this.pointWriter.outputDebugFrame(frame);
    }

    private Point writeDiagnostic(AprilTagDetection detection)
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

        Point[] vertices = detection.getVertices();
        System.out.println(
            String.format(
                "Id: %d, DecisionMargin: %.2f, Hamming: %d, Edges: (%.1f, %.1f), (%.1f, %.1f), (%.1f, %.1f), (%.1f, %.1f)",
                detection.getId(),
                detection.getDecisionMargin(),
                detection.getHamming(),
                vertices[0].x,
                vertices[0].y,
                vertices[1].x,
                vertices[1].y,
                vertices[2].x,
                vertices[2].y,
                vertices[3].x,
                vertices[3].y));

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

        System.out.println(String.format("Original: %s, error: %f", t_apriltag_rel_robot.toString(), pose.getError()));
        System.out.println(String.format("Offset: (%f, %f, %f), Yaw: %f, Pitch: %f, Roll: %f", offset[0], offset[1], offset[2], ypr[0], ypr[1], ypr[2]));

        return detection.getCenter();
    }
}
