package frc1318.vision;

import org.opencv.core.Scalar;

import frc1318.apriltag.AprilTagFamily;

public class VisionConstants
{
    //================================================= Normal settings ======================================================

    public static final int TEAM_NUMBER = 1318;
    public static final double NETWORK_UPDATE_RATE = 0.02; // note that we expect to flush far more frequently than this, but it is good to have a backstop of 50Hz just in case
    public static final double MAGIC_NULL_VALUE = -1318.0;

    // Conversion constants...
    public static final double PI = Math.PI;
    public static final double RADIANS_PER_DEGREE = (VisionConstants.PI / 180.0f);
    public static final double DEGREES_PER_RADIAN = (180.0f / VisionConstants.PI);
    public static final double INCHES_PER_METER = 39.3700787;
    public static final double METERS_PER_INCH = 1.0 / VisionConstants.INCHES_PER_METER;

    // Debug/output settings:
    public static final int STREAMING_COMPRESSION = 80; // value between 0 and 100, -1 for "default"
    public static final double MAX_STREAM_FPS = 25.0;
    public static final long STREAM_FRAME_GAP_MILLIS = (long)(1000.0 * (1.0 / VisionConstants.MAX_STREAM_FPS));
    public static final int FRAME_OUTPUT_GAP = 30; // the number of frames to wait between saving images to file system
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_FRAME_RATE = true;
    public static final boolean DEBUG_PRINT_OUTPUT = VisionConstants.DEBUG && false;
    public static final boolean DEBUG_PRINT_PIPELINE_DATA = VisionConstants.DEBUG && false;
    public static final int DEBUG_FPS_AVERAGING_INTERVAL = 100;
    public static final boolean DEBUG_FRAME_STREAM = VisionConstants.DEBUG && false;
    public static final boolean DEBUG_FRAME_OUTPUT = VisionConstants.DEBUG && false;
    public static final String DEBUG_OUTPUT_FOLDER = "/home/irs/vision/";

    // Information about Microsoft LifeCam HD-3000 USB-based camera:
    public static final int LIFECAM_CAMERA_RESOLUTION_X = 1280;
    public static final int LIFECAM_CAMERA_RESOLUTION_Y = 720;
    public static final double LIFECAM_CAMERA_CENTER_WIDTH = VisionConstants.LIFECAM_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double LIFECAM_CAMERA_CENTER_HEIGHT = VisionConstants.LIFECAM_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double LIFECAM_CAMERA_FIELD_OF_VIEW_X = 61.37272; // 16:9 field of view along x axis https://vrguy.blogspot.com/2013/04/converting-diagonal-field-of-view-and.html to convert from 68.5 degrees diagonal.
    public static final double LIFECAM_CAMERA_FIELD_OF_VIEW_Y = 36.91875; // 16:9 field of view along y axis
    public static final double LIFECAM_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.LIFECAM_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double LIFECAM_CAMERA_FOCAL_LENGTH_X = 1078.4675; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double LIFECAM_CAMERA_FOCAL_LENGTH_Y = 1078.4675; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int LIFECAM_CAMERA_OPERATOR_BRIGHTNESS = 35;
    public static final int LIFECAM_CAMERA_FPS = 30; // Max supported value is 30

    // Basic Information about ELP ELP-USBFHD08S-L29 USB-based camera:
    public static final int ELP_CAMERA_RESOLUTION_X = 1280;
    public static final int ELP_CAMERA_RESOLUTION_Y = 720;
    public static final double ELP_CAMERA_CENTER_WIDTH = VisionConstants.ELP_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_CAMERA_CENTER_HEIGHT = VisionConstants.ELP_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double ELP_CAMERA_FIELD_OF_VIEW_X = 87.1; // 16:9 field of view along x axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_CAMERA_FIELD_OF_VIEW_Y = 56.3; // 16:9 field of view along y axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.ELP_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double ELP_CAMERA_FOCAL_LENGTH_X = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double ELP_CAMERA_FOCAL_LENGTH_Y = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int ELP_CAMERA_OPERATOR_BRIGHTNESS = 35;
    public static final int ELP_CAMERA_FPS = 120; // Max supported value is 260 at 640x360

    // Basic Information about the Surface Book 2 front-facing camera:
    public static final int SB2_CAMERA_RESOLUTION_X = 1280;
    public static final int SB2_CAMERA_RESOLUTION_Y = 720;
    public static final double SB2_CAMERA_CENTER_WIDTH = VisionConstants.SB2_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double SB2_CAMERA_CENTER_HEIGHT = VisionConstants.SB2_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double SB2_CAMERA_FIELD_OF_VIEW_X = 61.6; // 16:9 field of view along x axis (calculated ~70 degree diagonal field of view?)
    public static final double SB2_CAMERA_FIELD_OF_VIEW_Y = 35.0; // 16:9 field of view along y axis (calculated ~70 degree diagonal field of view?)
    public static final double SB2_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.SB2_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double SB2_CAMERA_FOCAL_LENGTH_X = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double SB2_CAMERA_FOCAL_LENGTH_Y = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int SB2_CAMERA_OPERATOR_BRIGHTNESS = 35;
    public static final int SB2_CAMERA_FPS = 50;

    // Information about particular SB2 front-facing camera camera:
    public static final double SB2_CAMERA_A_CENTER_X = 640.9292525020591; // location of center pixel (x)
    public static final double SB2_CAMERA_A_CENTER_Y = 313.4866020859673; // location of center pixel (y)
    public static final double SB2_CAMERA_A_FOCAL_LENGTH_X = 1073.4690095376443; // focal_length (x)
    public static final double SB2_CAMERA_A_FOCAL_LENGTH_Y = 1140.4205001041244; // focal_length (y)
    public static final double[] SB2_CAMERA_A_DIFF_COEF = new double[] { 0.18357655558590572, 0.15623448338635104, -0.048078236293767646, -0.004665653888361999, -0.9151821690454246 }; // differential coefficients

    // Information about particular ELP ELP-USBFHD08S-L29 USB-based camera labelled A:
    public static final double ELP_CAMERA_A_CENTER_X = 646.8081671313663; // location of center pixel (x)
    public static final double ELP_CAMERA_A_CENTER_Y = 375.1807646907449; // location of center pixel (y)
    public static final double ELP_CAMERA_A_FOCAL_LENGTH_X = 720.284887685355; // focal_length (x)
    public static final double ELP_CAMERA_A_FOCAL_LENGTH_Y = 720.2313616878075; // focal_length (y)
    public static final double[] ELP_CAMERA_A_DIFF_COEF = new double[] { -0.35741328801517136, 0.17616089967234086, -4.1961486249762574E-4, 8.253159707452646E-4, -0.04946351176916509 }; // differential coefficients

    // Information about particular ELP ELP-USBFHD08S-L29 USB-based camera labelled B:
    public static final double ELP_CAMERA_B_CENTER_X = 608.734090039098; // location of center pixel (x)
    public static final double ELP_CAMERA_B_CENTER_Y = 357.34319404600797; // location of center pixel (y)
    public static final double ELP_CAMERA_B_FOCAL_LENGTH_X = 722.9499866539463; // focal_length (x)
    public static final double ELP_CAMERA_B_FOCAL_LENGTH_Y = 722.3269838290583; // focal_length (y)
    public static final double[] ELP_CAMERA_B_DIFF_COEF = new double[] { -0.36442680815585954, 0.2005920317892369, -8.493085640825166E-4, 0.0018313434500726454, -0.06934131324135161 }; // differential coefficients

    // pieces for example HSV pipeline
    public static final Scalar EXAMPLE_VISIONTARGET_HSV_FILTER_LOW = new Scalar(60, 100, 90); // 2022: new Scalar(60, 90, 70);
    public static final Scalar EXAMPLE_VISIONTARGET_HSV_FILTER_HIGH = new Scalar(95, 255, 255); // 2022: new Scalar(90, 255, 255);
    public static final int EXAMPLE_VISIONTARGET_CONTOUR_MIN_AREA = 0;
    public static final int EXAMPLE_PIPELINE_VISION_BRIGHTNESS = 1;
    public static final int EXAMPLE_PIPELINE_VISION_EXPOSURE = 1;

    //================================================= Yearly settings ======================================================

    // Primary camera
    public static final String PRIMARY_CAMERA_STREAM_NAME = "IRS-raw1";
    public static final int PRIMARY_CAMERA_ID = 0;
    public static final int PRIMARY_CAMERA_RESOLUTION_X = VisionConstants.ELP_CAMERA_RESOLUTION_X;
    public static final int PRIMARY_CAMERA_RESOLUTION_Y = VisionConstants.ELP_CAMERA_RESOLUTION_Y;
    public static final int PRIMARY_CAMERA_FPS = VisionConstants.ELP_CAMERA_FPS;
    public static final boolean PRIMARY_CAMERA_SHOULD_REPROCESS = false;
    public static final double PRIMARY_CAMERA_PITCH = 0.0; // pitch in degrees, degrees camera is mounted from level/horizontal line parallel to floor
    public static final double PRIMARY_CAMERA_YAW = 0.0; // yaw in degrees, degrees camera is mounted from straight forwards
    public static final double PRIMARY_CAMERA_ROLL = 0.0; // roll in degrees, degrees camera is twisted from level/horizontal line parallel to floor
    public static final double PRIMARY_CAMERA_PORT_OFFSET = 0.0; // "y" distance from center of robot to the center of the camera viewport in inches, left is positive
    public static final double PRIMARY_CAMERA_FORWARD_OFFSET = -2.5; // "x" distance from the center of the robot to the viewport of the camera, forward is positive
    public static final double PRIMARY_CAMERA_VERTICAL_OFFSET = 11.0; // "z" distance from the bottom of the robot to the viewport of the camera, up is positive
    public static final int PRIMARY_CAMERA_DEFAULT_VISION_BRIGHTNESS = VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS;
    public static final int PRIMARY_CAMERA_DEFAULT_VISION_EXPOSURE = VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE;

    public static final double PRIMARY_CAMERA_CENTER_X = VisionConstants.ELP_CAMERA_A_CENTER_X; // location of center pixel (x)
    public static final double PRIMARY_CAMERA_CENTER_Y = VisionConstants.ELP_CAMERA_A_CENTER_Y; // location of center pixel (y)
    public static final double PRIMARY_CAMERA_FOCAL_LENGTH_X = VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_X; // focal_length (x)
    public static final double PRIMARY_CAMERA_FOCAL_LENGTH_Y = VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_Y; // focal_length (y)
    public static final double[] PRIMARY_CAMERA_DIFF_COEF = VisionConstants.ELP_CAMERA_A_DIFF_COEF; // differential coefficients

    // Primary pipeline
    public static final String PRIMARY_DEBUG_STREAM_NAME = "IRS-debug1";
    public static final int PRIMARY_PIPELINE_VISION_MODE = 1;
    public static final boolean PRIMARY_PIPELINE_SHOULD_MASK = true;
    public static final boolean PRIMARY_PIPELINE_SHOULD_UNDISTORT = true;
    public static final AprilTagFamily PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY = AprilTagFamily.tag36h11;
    public static final double PRIMARY_PIPELINE_APRILTAG_SIZE = 6.5; // in inches, 8.125" overall, with a 6.5" internal black square
    public static final int PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE = 0;
    public static final int PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS = 2;
    public static final float PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE = 2.0f;
    public static final float PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA = 0.0f;
    public static final boolean PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES = true;
    public static final double PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING = 0.25;
    public static final double PRIMARY_PIPELINE_APRILTAG_MIN_AREA = 0.0;
    public static final double PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN = 0.0;
    public static final int PRIMARY_PIPELINE_VISION_BRIGHTNESS = 20;
    public static final int PRIMARY_PIPELINE_VISION_EXPOSURE = -1; // auto
    public static final int PRIMARY_STREAM_RESOLUTION_X = VisionConstants.ELP_CAMERA_RESOLUTION_X;
    public static final int PRIMARY_STREAM_RESOLUTION_Y = VisionConstants.ELP_CAMERA_RESOLUTION_Y;

    //============================================ Other settings and helpers =================================================

    public static final int MAX_POTENTIAL_CAMERA_VALUE = 10000; // I haven't ever seen a camera ID value anywhere near this high...
}
