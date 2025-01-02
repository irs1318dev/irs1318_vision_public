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
    public static final int NT_DISCONNECT_SLEEP_PERIOD_MS = 2500; // 2.5s sleep
    public static final int SAME_VALUE_PUBLISHING_INTERVAL = 50;
    public static final boolean USE_ADVANTAGE_KIT = true; // whether the vision system is connected to a robot logging data to AK instead of SmartDashboard
    public static final long ABSOLUTE_POSITION_AVOID_NULL_GAP_TIME = 100; // in ms
    public static final int STREAMING_COMPRESSION = 80; // value between 0 and 100, -1 for "default"
    public static final double MAX_STREAM_FPS = 25.0;
    public static final long STREAM_FRAME_GAP_MILLIS = (long)(1000.0 * (1.0 / VisionConstants.MAX_STREAM_FPS));
    public static final boolean LOG_IMAGES = false;
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
    public static final int ELP_FHD_CAMERA_RESOLUTION_X = 1280;
    public static final int ELP_FHD_CAMERA_RESOLUTION_Y = 720;
    public static final double ELP_FHD_CAMERA_CENTER_WIDTH = VisionConstants.ELP_FHD_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_FHD_CAMERA_CENTER_HEIGHT = VisionConstants.ELP_FHD_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double ELP_FHD_CAMERA_FIELD_OF_VIEW_X = 87.1; // 16:9 field of view along x axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_FHD_CAMERA_FIELD_OF_VIEW_Y = 56.3; // 16:9 field of view along y axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_FHD_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.ELP_FHD_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double ELP_FHD_CAMERA_FOCAL_LENGTH_X = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double ELP_FHD_CAMERA_FOCAL_LENGTH_Y = 673.241; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int ELP_FHD_CAMERA_OPERATOR_BRIGHTNESS = 35;
    public static final int ELP_FHD_CAMERA_FPS = 120; // Max supported value is 260 at 640x360

    // Basic Information about ELP ELP-USBGS720P02-L100 USB-based camera:
    // Original Back Facing Gray-Scale Camera for 2024
    public static final int ELP_GS_GRAY_CAMERA_RESOLUTION_X = 1280;
    public static final int ELP_GS_GRAY_CAMERA_RESOLUTION_Y = 720;
    public static final double ELP_GS_GRAY_CAMERA_CENTER_WIDTH = VisionConstants.ELP_GS_GRAY_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_GS_GRAY_CAMERA_CENTER_HEIGHT = VisionConstants.ELP_GS_GRAY_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double ELP_GS_GRAY_CAMERA_FIELD_OF_VIEW_X = 59.74; // 16:9 field of view along x axis
    public static final double ELP_GS_GRAY_CAMERA_FIELD_OF_VIEW_Y = 35.85; // 16:9 field of view along y axis
    public static final double ELP_GS_GRAY_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.ELP_GS_GRAY_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double ELP_GS_GRAY_CAMERA_FOCAL_LENGTH_X = 1113.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double ELP_GS_GRAY_CAMERA_FOCAL_LENGTH_Y = 1113.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int ELP_GS_GRAY_CAMERA_OPERATOR_BRIGHTNESS = 40;
    public static final int ELP_GS_GRAY_CAMERA_FPS = 60; // Max supported value is 60

    // Basic Information about ELP ELP-USBGS1200P01-H120 USB-based camera:
    // Front Facing Color Camera for 2024
    public static final int ELP_GS_COLOR_CAMERA_RESOLUTION_X = 1280;
    public static final int ELP_GS_COLOR_CAMERA_RESOLUTION_Y = 720;
    public static final double ELP_GS_COLOR_CAMERA_CENTER_WIDTH = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_X / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_GS_COLOR_CAMERA_CENTER_HEIGHT = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_Y / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double ELP_GS_COLOR_CAMERA_FIELD_OF_VIEW_X = 115.27; // 16:9 field of view along x axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_GS_COLOR_CAMERA_FIELD_OF_VIEW_Y = 83.23; // 16:9 field of view along y axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_GS_COLOR_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.ELP_GS_COLOR_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double ELP_GS_COLOR_CAMERA_FOCAL_LENGTH_X = 405.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double ELP_GS_COLOR_CAMERA_FOCAL_LENGTH_Y = 405.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int ELP_GS_COLOR_CAMERA_OPERATOR_BRIGHTNESS = 32;
    public static final int ELP_GS_COLOR_CAMERA_FPS = 90; // Max supported value is 90

    // Basic Information about ELP ELP-USBGS1200P01-H110 USB-based camera:
    // Replacement Back Facing Color Camera for 2024
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_720P = 1280;
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_720P = 720;
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_WIDTH_720P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_720P / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_HEIGHT_720P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_720P / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_1080P = 1920;
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_1080P = 1080;
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_WIDTH_1080P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_1080P / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_HEIGHT_1080P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_1080P / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_1200P = 1920;
    public static final int ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_1200P = 1200;
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_WIDTH_1200P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_1200P / 2.0 - 0.5; // distance from center to left/right sides in pixels
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_HEIGHT_1200P = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_1200P / 2.0 - 0.5; // distance from center to top/bottom in pixels
    public static final double ELP_GS_COLOR_110DEG_CAMERA_FIELD_OF_VIEW_X = 115.27; // 16:9 field of view along x axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_GS_COLOR_110DEG_CAMERA_FIELD_OF_VIEW_Y = 83.23; // 16:9 field of view along y axis (calculated 95 degree diagonal field of view?)
    public static final double ELP_GS_COLOR_110DEG_CAMERA_CENTER_VIEW_ANGLE = VisionConstants.ELP_GS_COLOR_CAMERA_FIELD_OF_VIEW_X / 2.0;
    public static final double ELP_GS_COLOR_110DEG_CAMERA_FOCAL_LENGTH_X = 405.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final double ELP_GS_COLOR_110DEG_CAMERA_FOCAL_LENGTH_Y = 405.0; // focal_length = res_* / (2.0 * tan (FOV_* / 2.0)
    public static final int ELP_GS_COLOR_110DEG_CAMERA_OPERATOR_BRIGHTNESS = 32;
    public static final int ELP_GS_COLOR_110DEG_CAMERA_FPS = 90; // Max supported value is 90

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
    public static final double ELP_FHD_CAMERA_A_CENTER_X = 646.8081671313663; // location of center pixel (x)
    public static final double ELP_FHD_CAMERA_A_CENTER_Y = 375.1807646907449; // location of center pixel (y)
    public static final double ELP_FHD_CAMERA_A_FOCAL_LENGTH_X = 720.284887685355; // focal_length (x)
    public static final double ELP_FHD_CAMERA_A_FOCAL_LENGTH_Y = 720.2313616878075; // focal_length (y)
    public static final double[] ELP_FHD_CAMERA_A_DIFF_COEF = new double[] { -0.35741328801517136, 0.17616089967234086, -4.1961486249762574E-4, 8.253159707452646E-4, -0.04946351176916509 }; // differential coefficients

    // Information about particular ELP ELP-USBFHD08S-L29 USB-based camera labelled B:
    public static final double ELP_FHD_CAMERA_B_CENTER_X = 608.734090039098; // location of center pixel (x)
    public static final double ELP_FHD_CAMERA_B_CENTER_Y = 357.34319404600797; // location of center pixel (y)
    public static final double ELP_FHD_CAMERA_B_FOCAL_LENGTH_X = 722.9499866539463; // focal_length (x)
    public static final double ELP_FHD_CAMERA_B_FOCAL_LENGTH_Y = 722.3269838290583; // focal_length (y)
    public static final double[] ELP_FHD_CAMERA_B_DIFF_COEF = new double[] { -0.36442680815585954, 0.2005920317892369, -8.493085640825166E-4, 0.0018313434500726454, -0.06934131324135161 }; // differential coefficients

    // Information about particular ELP ELP-USBFHD08S-L29 USB-based camera labelled C:
    public static final double ELP_FHD_CAMERA_C_CENTER_X = 624.7857617987912; // location of center pixel (x)
    public static final double ELP_FHD_CAMERA_C_CENTER_Y = 292.8069526753687; // location of center pixel (y)
    public static final double ELP_FHD_CAMERA_C_FOCAL_LENGTH_X = 726.6859868745878; // focal_length (x)
    public static final double ELP_FHD_CAMERA_C_FOCAL_LENGTH_Y = 724.7915978140799; // focal_length (y)
    public static final double[] ELP_FHD_CAMERA_C_DIFF_COEF = new double[] { -0.3499577494693199, 0.1446936238487039, 3.9658801898321747E-4, -1.5139051070648944E-6, -0.019370096382674354 }; // differential coefficients

    // Information about particular ELP ELP-USBGS720P02-L100 USB-based camera labelled A:
    public static final double ELP_GS_GRAY_CAMERA_A_CENTER_X = 652.9183389994762; // location of center pixel (x)
    public static final double ELP_GS_GRAY_CAMERA_A_CENTER_Y = 402.4604761967487; // location of center pixel (y)
    public static final double ELP_GS_GRAY_CAMERA_A_FOCAL_LENGTH_X = 1127.7245961742135; // focal_length (x)
    public static final double ELP_GS_GRAY_CAMERA_A_FOCAL_LENGTH_Y = 1127.2511326359984; // focal_length (y)
    public static final double[] ELP_GS_GRAY_CAMERA_A_DIFF_COEF = new double[] { 0.11626252813427557, -0.08307977160304618, 0.002446149270550793, 0.0038755930518374836, -0.1506401424256616 }; // differential coefficients

    // Information about particular ELP ELP-USBGS1200P01-H120 USB-based camera labelled A:
    public static final double ELP_GS_COLOR_CAMERA_A_CENTER_X = 654.8875059634846; // location of center pixel (x)
    public static final double ELP_GS_COLOR_CAMERA_A_CENTER_Y = 337.0225214846468; // location of center pixel (y)
    public static final double ELP_GS_COLOR_CAMERA_A_FOCAL_LENGTH_X = 405.55910126056716; // focal_length (x)
    public static final double ELP_GS_COLOR_CAMERA_A_FOCAL_LENGTH_Y = 405.21935307319967; // focal_length (y)
    public static final double[] ELP_GS_COLOR_CAMERA_A_DIFF_COEF = new double[] { -0.039110212482325955, 0.11299838957179258, -2.0460690092042808E-4, 0.002064750585888466, -0.09772420184550173 }; // differential coefficients

    // Information about particular ELP ELP-USBGS1200P01-H120 USB-based camera labelled B:
    public static final double ELP_GS_COLOR_CAMERA_B_CENTER_X = 626.9451053990052; // location of center pixel (x)
    public static final double ELP_GS_COLOR_CAMERA_B_CENTER_Y = 343.7560042305908; // location of center pixel (y)
    public static final double ELP_GS_COLOR_CAMERA_B_FOCAL_LENGTH_X = 403.38797378527266; // focal_length (x)
    public static final double ELP_GS_COLOR_CAMERA_B_FOCAL_LENGTH_Y = 402.98615197835835; // focal_length (y)
    public static final double[] ELP_GS_COLOR_CAMERA_B_DIFF_COEF = new double[] { -0.05397056294809021, 0.14939185659696877, 0.0018664708109543838, 0.0012487373688795272, -0.1316587376506204 }; // differential coefficients

    // Information about particular ELP ELP-USBGS1200P01-H110 USB-based camera labelled C at 720P (Note: high error, so maybe re-do this setting for this camera?):
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_X_720P = 642.1502658843697; // location of center pixel (x)
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P = 345.2002685854337; // location of center pixel (y)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P = 489.36320336218097; // focal_length (x)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P = 488.80450192174777; // focal_length (y)
    public static final double[] ELP_GS_COLOR_CAMERA_C_DIFF_COEF_720P = new double[] { 0.003130677643595958, -0.07191123140364064, 2.172219816147537E-4, 8.844203130150819E-4, 0.035023058158021564 }; // differential coefficients

    // Information about particular ELP ELP-USBGS1200P01-H110 USB-based camera labelled C at 1080P:
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_X_1080P = 970.1223602208258; // location of center pixel (x)
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_Y_1080P = 522.1748698711414; // location of center pixel (y)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_1080P = 755.4612817935783; // focal_length (x)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_1080P = 755.1456132869035; // focal_length (y)
    public static final double[] ELP_GS_COLOR_CAMERA_C_DIFF_COEF_1080P = new double[] { -0.027368614352732457, 0.5770692959803827, 0.0017438995540009828, 0.0035634409142661053, -2.48854627901981 }; // differential coefficients

    // Information about particular ELP ELP-USBGS1200P01-H110 USB-based camera labelled C at 1200P:
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_X_1200P = 972.5461877222882; // location of center pixel (x)
    public static final double ELP_GS_COLOR_CAMERA_C_CENTER_Y_1200P = 579.613798836333; // location of center pixel (y)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_1200P = 734.3422768052035; // focal_length (x)
    public static final double ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_1200P = 733.6658886140026; // focal_length (y)
    public static final double[] ELP_GS_COLOR_CAMERA_C_DIFF_COEF_1200P = new double[] { 0.0055678643326264425, -0.08820789770864473, 0.0013796835943846158, 0.0035432604225566536, 0.07223248373746823 }; // differential coefficients

    // pieces for example HSV pipeline
    public static final Scalar EXAMPLE_VISIONTARGET_HSV_FILTER_LOW = new Scalar(60, 100, 90); // 2022: new Scalar(60, 90, 70);
    public static final Scalar EXAMPLE_VISIONTARGET_HSV_FILTER_HIGH = new Scalar(95, 255, 255); // 2022: new Scalar(90, 255, 255);
    public static final int EXAMPLE_VISIONTARGET_CONTOUR_MIN_AREA = 0;
    public static final int EXAMPLE_PIPELINE_VISION_BRIGHTNESS = 1;
    public static final int EXAMPLE_PIPELINE_VISION_EXPOSURE = 1;

    //================================================= Yearly settings ======================================================

    public static final boolean USE_PRIMARY_CAMERA = true; // backward-facing Grayscale camera
    public static final boolean USE_SECONDARY_CAMERA = true; // forward-left-facing Color camera
    public static final boolean USE_TERTIARY_CAMERA = false; // forward-right-facing Color camera
    public static final boolean USE_ABSOLUTE_POSITION_PIPELINES = false; // absolute position

    // Primary camera
    public static final String PRIMARY_CAMERA_STREAM_NAME = "IRS-1";
    public static final int PRIMARY_CAMERA_ID = 0;
    public static final boolean PRIMARY_CAMERA_GRAYSCALE = false;
    // public static final String PRIMARY_CAMERA_ID = "/base/axi/pcie@120000/rp1/i2c@88000/ov9281@60";
    public static final int PRIMARY_CAMERA_RESOLUTION_X = 1280;
    public static final int PRIMARY_CAMERA_RESOLUTION_Y = 800;
    public static final int PRIMARY_CAMERA_FPS = 40;
    public static final boolean PRIMARY_CAMERA_SHOULD_REPROCESS = false;
    public static final double PRIMARY_CAMERA_PITCH = 25.0; // pitch in degrees, degrees camera is mounted from level/horizontal line parallel to floor
    public static final double PRIMARY_CAMERA_YAW = 180.0; // yaw in degrees, degrees camera is mounted from straight forwards
    public static final double PRIMARY_CAMERA_ROLL = 0.0; // roll in degrees, degrees camera is twisted from level/horizontal line parallel to floor
    public static final double PRIMARY_CAMERA_PORT_OFFSET = 0.0; // "y" distance from center of robot to the center of the camera viewport in inches, left is positive
    public static final double PRIMARY_CAMERA_FORWARD_OFFSET = -11.0; // "x" distance from the center of the robot to the viewport of the camera, forward is positive
    public static final double PRIMARY_CAMERA_VERTICAL_OFFSET = 24.25; // "z" distance from the bottom of the robot to the viewport of the camera, up is positive
    public static final float PRIMARY_CAMERA_DEFAULT_VISION_BRIGHTNESS = 0.0f;
    public static final float PRIMARY_CAMERA_DEFAULT_VISION_EXPOSURE = 0.0f;

    public static final double PRIMARY_CAMERA_CENTER_X = VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_X_720P; // location of center pixel (x)
    public static final double PRIMARY_CAMERA_CENTER_Y = VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P; // location of center pixel (y)
    public static final double PRIMARY_CAMERA_FOCAL_LENGTH_X = VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P; // focal_length (x)
    public static final double PRIMARY_CAMERA_FOCAL_LENGTH_Y = VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P; // focal_length (y)
    public static final double[] PRIMARY_CAMERA_DIFF_COEF = VisionConstants.ELP_GS_COLOR_CAMERA_C_DIFF_COEF_720P; // differential coefficients

    // Secondary camera
    public static final String SECONDARY_CAMERA_STREAM_NAME = "IRS-2";
    public static final int SECONDARY_CAMERA_ID = 2;
    public static final boolean SECONDARY_CAMERA_GRAYSCALE = false;
    // public static final String SECONDARY_CAMERA_ID = "/dev/v4l/by-id/usb-yyyy_yyyy_yyyy-video-index0";
    public static final int SECONDARY_CAMERA_RESOLUTION_X = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_X;
    public static final int SECONDARY_CAMERA_RESOLUTION_Y = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_Y;
    public static final int SECONDARY_CAMERA_FPS = 90; //VisionConstants.ELP_CAMERA_FPS;
    public static final boolean SECONDARY_CAMERA_SHOULD_REPROCESS = false;
    public static final double SECONDARY_CAMERA_PITCH = 0.0; // pitch in degrees, degrees camera is mounted from level/horizontal line parallel to floor
    public static final double SECONDARY_CAMERA_YAW = -7.0; //35.0; // yaw in degrees, degrees camera is mounted from straight forwards
    public static final double SECONDARY_CAMERA_ROLL = 0.0; // roll in degrees, degrees camera is twisted from level/horizontal line parallel to floor
    public static final double SECONDARY_CAMERA_PORT_OFFSET = 0.0; //8.75; // "y" distance from center of robot to the center of the camera viewport in inches, left is positive
    public static final double SECONDARY_CAMERA_FORWARD_OFFSET = -9.0; //-14.0; // "x" distance from the center of the robot to the viewport of the camera, forward is positive
    public static final double SECONDARY_CAMERA_VERTICAL_OFFSET = 24.0; //24.5; // "z" distance from the bottom of the robot to the viewport of the camera, up is positive
    public static final float SECONDARY_CAMERA_DEFAULT_VISION_BRIGHTNESS = VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS;
    public static final float SECONDARY_CAMERA_DEFAULT_VISION_EXPOSURE = VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE;

    public static final double SECONDARY_CAMERA_CENTER_X = VisionConstants.ELP_GS_COLOR_CAMERA_A_CENTER_X; // location of center pixel (x)
    public static final double SECONDARY_CAMERA_CENTER_Y = VisionConstants.ELP_GS_COLOR_CAMERA_A_CENTER_Y; // location of center pixel (y)
    public static final double SECONDARY_CAMERA_FOCAL_LENGTH_X = VisionConstants.ELP_GS_COLOR_CAMERA_A_FOCAL_LENGTH_X; // focal_length (x)
    public static final double SECONDARY_CAMERA_FOCAL_LENGTH_Y = VisionConstants.ELP_GS_COLOR_CAMERA_A_FOCAL_LENGTH_Y; // focal_length (y)
    public static final double[] SECONDARY_CAMERA_DIFF_COEF = VisionConstants.ELP_GS_COLOR_CAMERA_A_DIFF_COEF; // differential coefficients

    // Tertiary camera
    public static final String TERTIARY_CAMERA_STREAM_NAME = "IRS-frontright";
    public static final int TERTIARY_CAMERA_ID = 4;
    public static final boolean TERTIARY_CAMERA_GRAYSCALE = false;
    // public static final String TERTIARY_CAMERA_ID = "/dev/v4l/by-id/usb-zzzz_zzzz_zzzz-video-index0";
    public static final int TERTIARY_CAMERA_RESOLUTION_X = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_X;
    public static final int TERTIARY_CAMERA_RESOLUTION_Y = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_Y;
    public static final int TERTIARY_CAMERA_FPS = 90; //VisionConstants.ELP_CAMERA_FPS;
    public static final boolean TERTIARY_CAMERA_SHOULD_REPROCESS = false;
    public static final double TERTIARY_CAMERA_PITCH = 0.0; // pitch in degrees, degrees camera is mounted from level/horizontal line parallel to floor
    public static final double TERTIARY_CAMERA_YAW = -35.0; // yaw in degrees, degrees camera is mounted from straight forwards
    public static final double TERTIARY_CAMERA_ROLL = 0.0; // roll in degrees, degrees camera is twisted from level/horizontal line parallel to floor
    public static final double TERTIARY_CAMERA_PORT_OFFSET = -8.25; // "y" distance from center of robot to the center of the camera viewport in inches, left is positive
    public static final double TERTIARY_CAMERA_FORWARD_OFFSET = -14.0; // "x" distance from the center of the robot to the viewport of the camera, forward is positive
    public static final double TERTIARY_CAMERA_VERTICAL_OFFSET = 24.5; // "z" distance from the bottom of the robot to the viewport of the camera, up is positive
    public static final float TERTIARY_CAMERA_DEFAULT_VISION_BRIGHTNESS = VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS;
    public static final float TERTIARY_CAMERA_DEFAULT_VISION_EXPOSURE = VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE;

    public static final double TERTIARY_CAMERA_CENTER_X = VisionConstants.ELP_GS_COLOR_CAMERA_B_CENTER_X; // location of center pixel (x)
    public static final double TERTIARY_CAMERA_CENTER_Y = VisionConstants.ELP_GS_COLOR_CAMERA_B_CENTER_Y; // location of center pixel (y)
    public static final double TERTIARY_CAMERA_FOCAL_LENGTH_X = VisionConstants.ELP_GS_COLOR_CAMERA_B_FOCAL_LENGTH_X; // focal_length (x)
    public static final double TERTIARY_CAMERA_FOCAL_LENGTH_Y = VisionConstants.ELP_GS_COLOR_CAMERA_B_FOCAL_LENGTH_Y; // focal_length (y)
    public static final double[] TERTIARY_CAMERA_DIFF_COEF = VisionConstants.ELP_GS_COLOR_CAMERA_B_DIFF_COEF; // differential coefficients

    // Primary pipeline
    public static final String PRIMARY_DEBUG_STREAM_NAME = "IRS-reardebug";
    public static final int PRIMARY_STREAM_RESOLUTION_X = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_720P;
    public static final int PRIMARY_STREAM_RESOLUTION_Y = VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_720P;
    public static final int PRIMARY_PIPELINE_VISION_MODE = 1;
    public static final boolean PRIMARY_PIPELINE_SHOULD_MASK = false;
    public static final boolean PRIMARY_PIPELINE_SHOULD_UNDISTORT = true;
    public static final AprilTagFamily PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY = AprilTagFamily.tag36h11;
    public static final double PRIMARY_PIPELINE_APRILTAG_SIZE = 6.5; // in inches, 8.125" overall, with a 6.5" internal black square
    public static final int PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE = 0;
    public static final int PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS = 2;
    public static final float PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE = 1.5f;
    public static final float PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA = 0.5f;
    public static final boolean PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES = true;
    public static final double PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING = 0.25;
    public static final double PRIMARY_PIPELINE_APRILTAG_MIN_AREA = 0.0;
    public static final double PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN = 0.0;
    public static final float PRIMARY_PIPELINE_VISION_BRIGHTNESS = 0.0f;
    public static final float PRIMARY_PIPELINE_VISION_EXPOSURE = 0.0f; // -1 --> auto for native OpenCV

    // Secondary pipeline
    public static final String SECONDARY_DEBUG_STREAM_NAME = "IRS-frontleftdebug";
    public static final int SECONDARY_STREAM_RESOLUTION_X = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_X;
    public static final int SECONDARY_STREAM_RESOLUTION_Y = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_Y;
    public static final int SECONDARY_PIPELINE_VISION_MODE = 2;
    public static final boolean SECONDARY_PIPELINE_SHOULD_MASK = false;
    public static final boolean SECONDARY_PIPELINE_SHOULD_UNDISTORT = true;
    public static final AprilTagFamily SECONDARY_PIPELINE_APRILTAG_DETECTION_FAMILY = AprilTagFamily.tag36h11;
    public static final double SECONDARY_PIPELINE_APRILTAG_SIZE = 6.5; // in inches, 8.125" overall, with a 6.5" internal black square
    public static final int SECONDARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE = 0;
    public static final int SECONDARY_PIPELINE_APRILTAG_PROCESSING_THREADS = 2;
    public static final float SECONDARY_PIPELINE_APRILTAG_QUAD_DECIMATE = 1.5f;
    public static final float SECONDARY_PIPELINE_APRILTAG_QUAD_SIGMA = 0.5f;
    public static final boolean SECONDARY_PIPELINE_APRILTAG_REFINE_EDGES = true;
    public static final double SECONDARY_PIPELINE_APRILTAG_DECODE_SHARPENING = 0.25;
    public static final double SECONDARY_PIPELINE_APRILTAG_MIN_AREA = 0.0;
    public static final double SECONDARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN = 0.0;
    public static final int SECONDARY_PIPELINE_VISION_BRIGHTNESS = 32;
    public static final int SECONDARY_PIPELINE_VISION_EXPOSURE = -1; // auto

    // Tertiary pipeline
    public static final String TERTIARY_DEBUG_STREAM_NAME = "IRS-frontrightdebug";
    public static final int TERTIARY_STREAM_RESOLUTION_X = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_X;
    public static final int TERTIARY_STREAM_RESOLUTION_Y = VisionConstants.ELP_GS_COLOR_CAMERA_RESOLUTION_Y;
    public static final int TERTIARY_PIPELINE_VISION_MODE = 2;
    public static final boolean TERTIARY_PIPELINE_SHOULD_MASK = false;
    public static final boolean TERTIARY_PIPELINE_SHOULD_UNDISTORT = true;
    public static final AprilTagFamily TERTIARY_PIPELINE_APRILTAG_DETECTION_FAMILY = AprilTagFamily.tag36h11;
    public static final double TERTIARY_PIPELINE_APRILTAG_SIZE = 6.5; // in inches, 8.125" overall, with a 6.5" internal black square
    public static final int TERTIARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE = 0;
    public static final int TERTIARY_PIPELINE_APRILTAG_PROCESSING_THREADS = 2;
    public static final float TERTIARY_PIPELINE_APRILTAG_QUAD_DECIMATE = 1.5f;
    public static final float TERTIARY_PIPELINE_APRILTAG_QUAD_SIGMA = 0.5f;
    public static final boolean TERTIARY_PIPELINE_APRILTAG_REFINE_EDGES = true;
    public static final double TERTIARY_PIPELINE_APRILTAG_DECODE_SHARPENING = 0.25;
    public static final double TERTIARY_PIPELINE_APRILTAG_MIN_AREA = 0.0;
    public static final double TERTIARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN = 0.0;
    public static final int TERTIARY_PIPELINE_VISION_BRIGHTNESS = 32;
    public static final int TERTIARY_PIPELINE_VISION_EXPOSURE = -1; // auto

    // Tertiary pipeline
    public static final int ABSOLUTE_PIPELINE_VISION_MODE = 3;
    public static final boolean ABSOLUTE_PIPELINE_SHOULD_MASK = false;
    public static final boolean ABSOLUTE_PIPELINE_SHOULD_UNDISTORT = true;
    public static final AprilTagFamily ABSOLUTE_PIPELINE_APRILTAG_DETECTION_FAMILY = AprilTagFamily.tag36h11;
    public static final double ABSOLUTE_PIPELINE_APRILTAG_SIZE = 6.5; // in inches, 8.125" overall, with a 6.5" internal black square
    public static final int ABSOLUTE_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE = 0;
    public static final int ABSOLUTE_PIPELINE_APRILTAG_PROCESSING_THREADS = 2;
    public static final float ABSOLUTE_PIPELINE_APRILTAG_QUAD_DECIMATE = 1.5f;
    public static final float ABSOLUTE_PIPELINE_APRILTAG_QUAD_SIGMA = 0.5f;
    public static final boolean ABSOLUTE_PIPELINE_APRILTAG_REFINE_EDGES = true;
    public static final double ABSOLUTE_PIPELINE_APRILTAG_DECODE_SHARPENING = 0.25;
    public static final double ABSOLUTE_PIPELINE_APRILTAG_MIN_AREA = 0.0;
    public static final double ABSOLUTE_PIPELINE_APRILTAG_MIN_DECISION_MARGIN = 0.0;
    public static final int ABSOLUTE_PIPELINE_VISION_BRIGHTNESS = 32;
    public static final int ABSOLUTE_PIPELINE_VISION_EXPOSURE = -1; // auto

    //============================================ Other settings and helpers =================================================

    public static final int MAX_POTENTIAL_CAMERA_VALUE = 10000; // I haven't ever seen a camera ID value anywhere near this high...
}
