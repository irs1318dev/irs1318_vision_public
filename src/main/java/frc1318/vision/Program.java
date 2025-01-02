package frc1318.vision;

import java.io.*;
import java.util.*;

import edu.wpi.first.cscore.CameraServerCvJNI;
import edu.wpi.first.cscore.CameraServerJNI;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager;
import net.samuelcampos.usbdrivedetector.USBStorageDevice;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.videoio.*;

import frc1318.apriltag.AprilTagDetection;
import frc1318.vision.calculator.*;
import frc1318.vision.controller.*;
import frc1318.vision.filters.*;
import frc1318.vision.helpers.*;
import frc1318.vision.leds.*;
import frc1318.vision.pipeline.*;
import frc1318.vision.reader.*;
import frc1318.vision.writer.*;

public class Program
{
    private enum Mode
    {
        None,
        HSV,
        AprilTagRelative,
        AprilTagAbsolute,
        Switched,
        ImageSaver,
        DeviceEnumeration,
        Calibrate,
        GetControls,
    }

    private static String PrimaryMaskPath = "/primarymask.png";
    private static String SecondaryMaskPath = "/secondarymask.png";
    private static String TertiaryMaskPath = "/tertiarymask.png";

    /**
     * Checks whether the OS is linux or not.
     */
    public static boolean IsLinux;

    /**
     * Main entrypoint for Vision System.
     * 
     * @param args from commandline input
     */
    public static void main(String[] args)
    {
        String osName = System.getProperty("os.name");
        Program.IsLinux = osName.startsWith("Linux") || osName.startsWith("LINUX");

        System.loadLibrary("opencv_java4100"); // Core.NATIVE_LIBRARY_NAME

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerJNI.Helper.setExtractOnStaticLoad(false);
        CameraServerCvJNI.Helper.setExtractOnStaticLoad(false);

        try
        {
            CombinedRuntimeLoader.loadLibraries(Program.class, "wpiutiljni", "ntcorejni", "cscorejni");
        }
        catch (IOException ex)
        {
            System.out.println("Unable to start - couldn't load WPILib dependencies");
            ex.printStackTrace(System.err);
            System.exit(1);
        }

        if (args == null || args.length == 0)
        {
            runVisionSystem();
            return;
        }

        Mode selectedMode = Mode.None;
        String cameraId = null;
        String sourceFileName = null;
        String targetFileName = null;
        String maskFileName = null;
        boolean leds = false;
        boolean grayscaleCamera = false;
        boolean useLibCamera = false;
        boolean diagnostic = false;
        boolean show = false;
        boolean skipUndistort = false;
        boolean sb2 = false;
        boolean skipApplyingSettings = false;
        double exposure = VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE;
        double brightness = VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS;
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (arg.equalsIgnoreCase("/?") ||
                arg.equalsIgnoreCase("/h"))
            {
                printUsage();
                return;
            }

            if (arg.equalsIgnoreCase("/leds"))
            {
                if (leds)
                {
                    System.err.println("Error: multiple /leds arguments");
                    printUsage();
                    return;
                }

                leds = true;
                continue;
            }

            if (arg.startsWith("/c:"))
            {
                if (cameraId != null)
                {
                    System.err.println("Error: multiple /c arguments");
                    printUsage();
                    return;
                }

                cameraId = arg.substring("/c:".length());
                continue;
            }

            if (arg.equalsIgnoreCase("/gray"))
            {
                if (grayscaleCamera)
                {
                    System.out.println("Warning: multiple /gray arguments");
                }

                grayscaleCamera = true;
                continue;
            }

            if (arg.equalsIgnoreCase("/libcamera"))
            {
                if (useLibCamera)
                {
                    System.out.println("Warning: multiple /libcamera arguments");
                }

                useLibCamera = true;
                continue;
            }

            if (arg.startsWith("/s:"))
            {
                if (sourceFileName != null)
                {
                    System.err.println("Error: multiple /s arguments");
                    printUsage();
                    return;
                }

                sourceFileName = arg.substring("/s:".length());
                continue;
            }

            if (arg.startsWith("/t:"))
            {
                if (targetFileName != null)
                {
                    System.err.println("Error: multiple /t arguments");
                    printUsage();
                    return;
                }

                targetFileName = arg.substring("/t:".length());
                continue;
            }

            if (arg.startsWith("/mask:"))
            {
                if (maskFileName != null)
                {
                    System.err.println("Error: multiple /mask arguments");
                    printUsage();
                    return;
                }

                maskFileName = arg.substring("/mask:".length());
                continue;
            }

            if (arg.equalsIgnoreCase("/show"))
            {
                if (show)
                {
                    System.out.println("Warning: multiple /show arguments");
                }

                show = true;
                continue;
            }

            if (arg.equalsIgnoreCase("/diagnostic"))
            {
                if (diagnostic)
                {
                    System.out.println("Warning: multiple /diagnostic arguments");
                }

                diagnostic = true;
                continue;
            }

            if (arg.equalsIgnoreCase("/skipundistort"))
            {
                if (skipUndistort)
                {
                    System.out.println("Warning: multiple /skipUndistort arguments");
                }

                skipUndistort = true;
                continue;
            }

            if (arg.equalsIgnoreCase("/controls"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.GetControls;
                continue;
            }

            if (arg.equalsIgnoreCase("/imageSaver"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.ImageSaver;
                continue;
            }

            if (arg.equalsIgnoreCase("/deviceEnumeration"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.DeviceEnumeration; 
                continue;
            }

            if (arg.equalsIgnoreCase("/calibrate"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.Calibrate;
                continue;
            }

            if (arg.equalsIgnoreCase("/hsv"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.HSV; 
                continue;
            }

            if (arg.equalsIgnoreCase("/apriltag"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.AprilTagRelative; 
                continue;
            }

            if (arg.equalsIgnoreCase("/absolute"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.AprilTagAbsolute; 
                continue;
            }

            if (arg.equalsIgnoreCase("/switched"))
            {
                if (selectedMode != Mode.None)
                {
                    System.err.println("Error: multiple mode arguments!");
                    printUsage();
                    return;
                }

                selectedMode = Mode.Switched; 
                continue;
            }

            if (arg.equalsIgnoreCase("/sb2"))
            {
                if (sb2)
                {
                    System.out.println("Warning: multiple /sb2 arguments");
                }

                sb2 = true;
                continue;
            }

            if (arg.equalsIgnoreCase("/skipApplyingSettings"))
            {
                if (skipApplyingSettings)
                {
                    System.out.println("Warning: multiple /skipApplyingSettings arguments");
                }

                skipApplyingSettings = true;
                continue;
            }

            if (arg.startsWith("/brightness:"))
            {
                if (brightness != VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS)
                {
                    System.out.println("Warning: multiple /brightness arguments");
                }

                String brightnessString = arg.substring("/brightness:".length());
                brightness = Double.parseDouble(brightnessString);
                continue;
            }

            if (arg.startsWith("/exposure:"))
            {
                if (exposure != VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE)
                {
                    System.out.println("Warning: multiple /exposure arguments");
                }

                String exposureString = arg.substring("/exposure:".length());
                exposure = Double.parseDouble(exposureString);
                continue;
            }

            System.err.println("Error: unknown argument " + arg);
            printUsage();
            return;
        }

        // validate arguments matching mode:
        switch (selectedMode)
        {
            case HSV:
            case None:
                if (diagnostic)
                {
                    System.err.println("Error: /diagnostic not supported for HSV mode");
                    printUsage();
                    return;
                }

                break;

            case ImageSaver:
                if (sourceFileName != null)
                {
                    System.err.println("Error: don't support /s and /imageSaver together");
                    printUsage();
                    return;
                }

                if (cameraId == null)
                {
                    System.err.println("Error: /imagesaver requires a camera source");
                    printUsage();
                    return;
                }

                if (targetFileName == null)
                {
                    System.err.println("Error: /imagesaver requires a target directory");
                    printUsage();
                    return;
                }

                if (maskFileName != null)
                {
                    System.err.println("Error: don't support /mask and /imageSaver together");
                    printUsage();
                    return;
                }

                if (diagnostic)
                {
                    System.err.println("Error: /diagnostic not supported for imageSaver mode");
                    printUsage();
                    return;
                }

                if (leds)
                {
                    System.err.println("Error: /leds not supported for imageSaver mode");
                    printUsage();
                    return;
                }

                break;

            case AprilTagRelative:
            case AprilTagAbsolute:
            case Switched:
                break;

            case DeviceEnumeration:
                if (cameraId != null || sourceFileName != null || maskFileName != null || diagnostic || show || ((targetFileName == null) == !useLibCamera) || leds)
                {
                    System.err.println("Error: /deviceEnueration requires /t:directory OR /libcamera and no other parameter");
                    printUsage();
                    return;
                }

                break;

            case Calibrate:
                if (maskFileName != null)
                {
                    System.err.println("Error: /calibrate doesn't support /mask");
                    printUsage();
                    return;
                }

                if (diagnostic)
                {
                    System.err.println("Error: /calibrate doesn't support /diagnostic");
                    printUsage();
                    return;
                }

                if (leds)
                {
                    System.err.println("Error: /calibrate doesn't support /leds");
                    printUsage();
                    return;
                }

                break;

            case GetControls:
                if (cameraId == null)
                {
                    System.err.println("Error: /controls requires camera id");
                    printUsage();
                    return;
                }

                if (!useLibCamera)
                {
                    System.err.println("Error: /controls only supports libcamera");
                    printUsage();
                    return;
                }

                if (sourceFileName != null || maskFileName != null || diagnostic || show || targetFileName != null || leds)
                {
                    System.err.println("Error: /controls doesn't support most other parameters");
                    printUsage();
                    return;
                }

                break;
        }

        if (sourceFileName != null && cameraId != null)
        {
            System.err.println("Error: don't support /c and /s together");
            printUsage();
            return;
        }

        System.out.println(String.format("Starting %s mode", selectedMode));

        if (selectedMode == Mode.DeviceEnumeration)
        {
            if (useLibCamera)
            {
                Program.libcameraEnumeration();
            }
            else
            {
                File file = new File(targetFileName);
                if (!file.exists())
                {
                    System.err.println("Error: no such directory " + targetFileName);
                    printUsage();
                    return;
                }

                if (!file.isDirectory())
                {
                    System.err.print("Error: not a directory " + targetFileName);
                    printUsage();
                    return;
                }

                Program.deviceEnumeration(targetFileName);
            }

            return;
        }

        if (selectedMode == Mode.GetControls)
        {
            Program.libcameraGetControls(cameraId);
            return;
        }

        ConsoleController controller = new ConsoleController();

        IFrameReader frameReader = null;
        if (cameraId != null)
        {
            if (useLibCamera)
            {
                frameReader = new LibCameraReader(controller, cameraId);
            }
            else
            {
                // parse cameraId, if relevant:
                int cameraIdNum = -1;
                boolean cameraIsNumeric = true;
                for (int c = 0; c < cameraId.length(); c++)
                {
                    if (!Character.isDigit(cameraId.charAt(c)))
                    {
                        cameraIsNumeric = false;
                        break;
                    }
                }

                if (cameraIsNumeric)
                {
                    cameraIdNum = Integer.parseInt(cameraId);
                    cameraId = null;

                    frameReader = new CameraReader(controller, cameraIdNum);
                }
                else
                {
                    frameReader = new CameraReader(controller, cameraId);
                }
            }
        }
        else if (sourceFileName != null)
        {
            File file = new File(sourceFileName);
            if (!file.exists())
            {
                System.err.println("Error: no such directory " + sourceFileName);
                return;
            }

            if (file.isDirectory())
            {
                frameReader = new LocalImageDirectoryReader(sourceFileName);
            }
            else
            {
                if (selectedMode == Mode.Calibrate)
                {
                    System.err.println("Error: calibration requires multiple images");
                    printUsage();
                    return;
                }

                frameReader = new LocalImageFileReader(sourceFileName);
            }
        }
        else if (!leds)
        {
            System.err.println("Error: unknown image source, and not in LEDs mode");
            printUsage();
            return;
        }

        Mat mask = null;
        if (maskFileName != null)
        {
            File file = new File(maskFileName);
            if (!file.exists())
            {
                System.err.println("Error: no such directory " + maskFileName);
                return;
            }

            if (file.isDirectory())
            {
                System.err.println("Error: mask should be a single image");
                printUsage();
                return;
            }
            else
            {
                LocalImageFileReader maskReader = new LocalImageFileReader(maskFileName);
                if (!maskReader.open())
                {
                    System.err.println("Error: mask is not requires multiple images");
                    printUsage();
                    return;
                }

                try
                {
                    Pair<Mat, Long> pair = maskReader.getCurrentFrame();
                    mask = pair.first;
                }
                catch (InterruptedException ex)
                {
                    System.err.println("Error: couldn't read mask");
                    ex.printStackTrace(System.err);
                }

                maskReader.close();
            }
        }

        IResultWriter<Point> pointWriter = null;
        if (selectedMode != Mode.Calibrate &&
            selectedMode != Mode.None)
        {
            if (targetFileName != null)
            {
                File file = new File(targetFileName);
                if (!file.exists())
                {
                    System.err.println("Error: no such directory " + targetFileName);
                    return;
                }

                if (!file.isDirectory())
                {
                    System.err.println("Error: not a directory " + targetFileName);
                    return;
                }

                pointWriter = new ImagePointWriter(targetFileName);
            }
            else if (show)
            {
                pointWriter = new ImagePointDrawer();
            }
            else
            {
                pointWriter = new DebugPointWriter();
            }
        }

        ImageUndistorter undistorter = null;
        if (selectedMode != Mode.None && !skipUndistort)
        {
            undistorter = new ImageUndistorter(
                sb2 ? VisionConstants.SB2_CAMERA_RESOLUTION_X : VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_X_720P,
                sb2 ? VisionConstants.SB2_CAMERA_RESOLUTION_Y : VisionConstants.ELP_GS_COLOR_110DEG_CAMERA_RESOLUTION_Y_720P,
                sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_X_720P,
                sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P,
                sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P,
                sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P,
                sb2 ? VisionConstants.SB2_CAMERA_A_DIFF_COEF : VisionConstants.ELP_GS_COLOR_CAMERA_C_DIFF_COEF_720P);
        }

        List<IResultWriter<?>> outputs = new ArrayList<IResultWriter<?>>();
        List<IFramePipeline> framePipelines = new ArrayList<IFramePipeline>();
        if (selectedMode == Mode.ImageSaver)
        {
            framePipelines.add(new PassThroughPipeline<Point>(pointWriter));
            outputs.add(pointWriter);
        }

        if (selectedMode == Mode.Calibrate)
        {
            Size chessboardSize = new Size(9, 6);
            ChessboardDrawer chessboardDrawer = new ChessboardDrawer(chessboardSize);
            framePipelines.add(new CalibrationPipeline(chessboardDrawer, chessboardSize));
            outputs.add(chessboardDrawer);
        }

        if (selectedMode == Mode.AprilTagRelative || selectedMode == Mode.Switched || selectedMode == Mode.AprilTagAbsolute)
        {
            if (selectedMode == Mode.AprilTagAbsolute)
            {
                IResultWriter<AbsolutePositionMeasurement> positionWriter = new DebugAbsolutePositionWriter();
                IResultWriter<AprilTagDetection> aprilTagCalculator =
                    new AbsolutePositionVisionCalculator(
                        positionWriter,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P,
                        VisionConstants.PRIMARY_CAMERA_ROLL,
                        VisionConstants.PRIMARY_CAMERA_PITCH,
                        VisionConstants.PRIMARY_CAMERA_YAW,
                        VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);
                framePipelines.add(
                    new AprilTagPipeline<AprilTagDetection>(
                        aprilTagCalculator,
                        mask,
                        undistorter,
                        grayscaleCamera,
                        new LargestAprilTagFilter(
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING));

                outputs.add(aprilTagCalculator);
            }
            else if (diagnostic)
            {
                AprilTagDiagnosticWriter aprilTagWriter =
                    new AprilTagDiagnosticWriter(
                        pointWriter,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P,
                        VisionConstants.PRIMARY_CAMERA_ROLL,
                        VisionConstants.PRIMARY_CAMERA_PITCH,
                        VisionConstants.PRIMARY_CAMERA_YAW,
                        VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);
                outputs.add(aprilTagWriter);
                framePipelines.add(
                    new AprilTagPipeline<AprilTagDetection>(
                        aprilTagWriter,
                        mask,
                        undistorter,
                        grayscaleCamera,
                        new LargestAprilTagFilter(
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING));

                outputs.add(pointWriter);
            }
            else if (show)
            {
                framePipelines.add(
                    new AprilTagPipeline<Point>(
                        pointWriter,
                        mask,
                        undistorter,
                        grayscaleCamera,
                        new LargestAprilTagCenterFilter(
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING));

                outputs.add(pointWriter);
            }
            else
            {
                IResultWriter<DistancesAnglesMeasurements> daWriter = new DebugDistancesAnglesWriter();
                IResultWriter<AprilTagDetection> aprilTagCalculator =
                    new DistancesAnglesVisionCalculator(
                        daWriter,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_CENTER_Y_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_X_720P,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_GS_COLOR_CAMERA_C_FOCAL_LENGTH_Y_720P,
                        VisionConstants.PRIMARY_CAMERA_ROLL,
                        VisionConstants.PRIMARY_CAMERA_PITCH,
                        VisionConstants.PRIMARY_CAMERA_YAW,
                        VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);
                framePipelines.add(
                    new AprilTagPipeline<AprilTagDetection>(
                        aprilTagCalculator,
                        mask,
                        undistorter,
                        grayscaleCamera,
                        new LargestAprilTagFilter(
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING));

                outputs.add(aprilTagCalculator);
            }
        }

        if (selectedMode == Mode.HSV || selectedMode == Mode.Switched)
        {
            IContourFilter<Point> frameFilter = new LargestCenterFilter(VisionConstants.EXAMPLE_VISIONTARGET_CONTOUR_MIN_AREA);
            framePipelines.add(
                new HSVPipeline<Point>(
                    pointWriter,
                    mask,
                    undistorter,
                    new HSVFilter(
                        VisionConstants.EXAMPLE_VISIONTARGET_HSV_FILTER_LOW,
                        VisionConstants.EXAMPLE_VISIONTARGET_HSV_FILTER_HIGH),
                    frameFilter));

            outputs.add(pointWriter);
        }

        if (selectedMode != Mode.None && (outputs.size() == 0 || framePipelines.size() == 0))
        {
            System.err.println(String.format("unknown mode '%s'!", selectedMode.toString()));
            return;
        }

        if (selectedMode != Mode.None && !frameReader.open())
        {
            System.err.println(String.format("unable to open frame reader '%s'!", cameraId));
            System.exit(1);
        }

        CameraSettings primarySettings = null;
        CameraSettings secondarySettings = null;
        if (selectedMode != Mode.None && !skipApplyingSettings)
        {
            primarySettings =
                new CameraSettings(
                    exposure,
                    brightness,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                    VisionConstants.PRIMARY_CAMERA_FPS);

            secondarySettings =
                new CameraSettings(
                    VisionConstants.EXAMPLE_PIPELINE_VISION_EXPOSURE,
                    VisionConstants.EXAMPLE_PIPELINE_VISION_BRIGHTNESS,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                    VisionConstants.PRIMARY_CAMERA_FPS);

            frameReader.setSettings(primarySettings);
        }

        for (IResultWriter<?> output : outputs)
        {
            if (!output.open())
            {
                System.err.println("unable to open output");
                if (frameReader instanceof IRunnableFrameReader)
                {
                    ((IRunnableFrameReader)frameReader).stop();
                }

                System.exit(1);
            }
        }

        if (!controller.open())
        {
            System.err.println("unable to open controller!");
            if (frameReader instanceof IRunnableFrameReader)
            {
                ((IRunnableFrameReader)frameReader).stop();
            }

            System.exit(1);
        }

        VisionSystemBase visionSystem = null;
        if (selectedMode == Mode.Switched)
        {
            visionSystem =
                new SwitchedVisionSystem(
                    frameReader,
                    controller,
                    framePipelines.toArray(new IFramePipeline[2]),
                    new int[] { 1, 2 },
                    new CameraSettings[] { primarySettings, secondarySettings },
                    primarySettings,
                    VisionConstants.PRIMARY_CAMERA_STREAM_NAME,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y);
        }
        else if (selectedMode != Mode.None)
        {
            visionSystem =
                new SimpleVisionSystem(
                    frameReader,
                    controller,
                    framePipelines.get(0),
                    VisionConstants.PRIMARY_CAMERA_STREAM_NAME,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y);
        }

        if (selectedMode != Mode.None && !visionSystem.open())
        {
            System.err.println("unable to open vision system!");
            if (frameReader instanceof IRunnableFrameReader)
            {
                ((IRunnableFrameReader)frameReader).stop();
            }

            controller.close();
            System.exit(1);
        }

        Thread cameraThread = null;
        if (frameReader instanceof IRunnableFrameReader)
        {
            cameraThread = new Thread((IRunnableFrameReader)frameReader);
            cameraThread.start();
        }

        Thread visionThread = null;
        if (visionSystem != null)
        {
            visionThread = new Thread(visionSystem);
            visionThread.start();
        }

        LEDStripManager ledManager = null;
        Thread ledThread = null;
        if (leds)
        {
            ledManager = new LEDStripManager(controller, 60, new UILedStrip(60));
            if (!ledManager.open())
            {
                ledManager.close();
                ledManager = null;
            }

            ledThread = new Thread(ledManager);
            ledThread.start();
        }

        controller.run();

        if (frameReader instanceof IRunnableFrameReader)
        {
            ((IRunnableFrameReader)frameReader).stop();
        }

        if (visionSystem != null)
        {
            visionSystem.stop();
            visionSystem.close();
        }

        if (ledManager != null)
        {
            ledManager.stop();
        }

        if (frameReader != null)
        {
            frameReader.close();
        }

        controller.close();
        for (IResultWriter<?> output : outputs)
        {
            output.close();
        }

        if (pointWriter != null)
        {
            pointWriter.close();
        }

        try
        {
            Thread.sleep(20);
        }
        catch (InterruptedException ex)
        {
        }

        if (cameraThread != null)
        {
            cameraThread.interrupt();
        }

        if (visionThread != null)
        {
            visionThread.interrupt();
        }

        if (ledThread != null)
        {
            ledThread.interrupt();
        }
    }

    private static void libcameraEnumeration()
    {
        String[] libCameraIds = LibCameraReader.enumerateCameraIds();
        for (int i = 0; i < libCameraIds.length; i++)
        {
            System.out.println(String.format("%d: %s", i, libCameraIds[i]));
        }
    }

    private static void libcameraGetControls(String cameraId)
    {
        System.out.println(LibCameraReader.enumerateCameraControls(cameraId));
    }

    private static void deviceEnumeration(String targetLocation)
    {
        Mat img = new Mat();
        for (int i = 0; i < VisionConstants.MAX_POTENTIAL_CAMERA_VALUE; i++)
        {
            System.out.println(i);
            VideoCapture vc = new VideoCapture();
            if (vc.open(i))
            {
                System.out.println("opened " + i);
                if (vc.read(img))
                {
                    System.out.println("read image from " + i);
                    Imgcodecs.imwrite(targetLocation + "device" + i + ".jpg", img);
                }

                vc.release();
            }
        }

        img.release();
    }

    private static void printUsage()
    {
        System.out.println("Usage:");
        System.out.println("VisionSystem.jar                                                       -- run full primary vision system pipelines");
        System.out.println("VisionSystem.jar [/hsv] /s:file         -- test hsv filtering for that file, outputting result to console (or directory)");
        System.out.println("VisionSystem.jar [/hsv] /s:directory    -- test hsv filtering for files in that directory, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar [/hsv] /c:camera       -- test hsv filtering using the provided camera, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar /switched /c:camera [/diagnostic] [/gray]   -- test switched camera functionality for both apriltag filtering and hsv filtering using the provided camera, results to console (or directory)");
        System.out.println("VisionSystem.jar /absolute /c:camera [/gray]            -- test absolute position detection from apriltags using the provided camera, results to console (or directory)");
        System.out.println("VisionSystem.jar /absolute /s:file [/gray]              -- test absolute position detection from apriltags for that file, outputting result to console (or directory)");
        System.out.println("VisionSystem.jar /absolute /s:directory [/gray]         -- test absolute position detection from apriltags for files in that directory, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /c:camera [/diagnostic] [/gray]   -- test apriltag detection using the provided camera, results to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /s:file [/diagnostic] [/gray]      -- test apriltag detection for that file, outputting result to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /s:directory [/diagnostic] [/gray] -- test apriltag detection for files in that directory, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar /calibrate /c:camera            -- calibrate camera based on the provided camera");
        System.out.println("VisionSystem.jar /calibrate /s:directory        -- calibrate camera based on the provided images");
        System.out.println("VisionSystem.jar /deviceEnumeration        -- run device enumeration");
        System.out.println("VisionSystem.jar /imagesaver /c:camera /t:directory       -- save images from webcam to the provided directory");
        System.out.println("VisionSystem.jar /libcamera /c:camera /controls                        -- shows supported camera controls from libcamera");
        System.out.println("Other common parameters:");
        System.out.println("[/leds]         -- control LEDs");
        System.out.println("[/libcamera]    -- use Libcamera library for interacting with a camera");
        System.out.println("[/t:directory]  -- save output images to a directory");
        System.out.println("[/show]         -- attempt to display output images");
        System.out.println("[/mask]         -- ignore certain areas of input image");

    }

    private static void runVisionSystem()
    {
        List<IOpenable> toClose = new ArrayList<IOpenable>();

        // scan through the list of USB devices until we find one to log images to
        File logsDirectory = null;
        File imageLoggingDirectory = null;
        USBDeviceDetectorManager driveDetector = new USBDeviceDetectorManager();
        List<USBStorageDevice> devices = driveDetector.getRemovableDevices();
        if (devices != null && devices.size() > 0)
        {
            System.out.println(String.format("Found %d USB devices.", devices.size()));
            for (USBStorageDevice device : devices)
            {
                if (device.canWrite())
                {
                    System.out.println("Found writable device");
                    File deviceRootDirectory = device.getRootDirectory();
                    if (deviceRootDirectory.isDirectory() && deviceRootDirectory.getFreeSpace() > 1L)
                    {
                        if (VisionConstants.LOG_IMAGES)
                        {
                            File imagesSubdirectory = new File(deviceRootDirectory, "images");
                            if (imagesSubdirectory.exists() || imagesSubdirectory.mkdir())
                            {
                                System.out.println("Writing images " + imagesSubdirectory);

                                imageLoggingDirectory = imagesSubdirectory;
                            }
                        }

                        File loggingSubdir = new File(deviceRootDirectory, "irs1318vision_logs");
                        if (loggingSubdir.exists() || loggingSubdir.mkdir())
                        {
                            System.out.println("Writing logs to " + loggingSubdir);

                            logsDirectory = loggingSubdir;
                        }

                        break;
                    }
                }
                else
                {
                    System.out.println("Found non-writable device");
                }
            }
        }
        else
        {
            System.out.println("No USB found.");
        }

        Logger logger = new Logger(logsDirectory);
        if (!logger.open())
        {
            System.out.println("Unable to open logger!");
            for (IOpenable openable : toClose)
            {
                openable.close();
            }

            System.exit(1);
        }

        toClose.add(logger);

        HeartbeatWriter heartbeat = new HeartbeatWriter();
        if (!heartbeat.open())
        {
            Logger.writeError("unable to open heartbeat writer!");
            for (IOpenable openable : toClose)
            {
                openable.close();
            }

            System.exit(1);
        }

        toClose.add(heartbeat);

        IController controller =
            new NetworkTableController(heartbeat);
        if (!controller.open())
        {
            Logger.writeError("unable to open controller!");
            for (IOpenable openable : toClose)
            {
                openable.close();
            }

            System.exit(1);
        }

        toClose.add(controller);

        IRunnableFrameReader primaryCameraReader;
        CameraSettings primarySettings = null;
        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            primaryCameraReader = new CameraReader(controller, VisionConstants.PRIMARY_CAMERA_ID);
            String cameraStringPrimary = "" + VisionConstants.PRIMARY_CAMERA_ID;
            if (!primaryCameraReader.open())
            {
                Logger.writeError(String.format("unable to open primary camera reader '%s'!", cameraStringPrimary));
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            primarySettings =
                new CameraSettings(
                    VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE,
                    VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                    VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                    VisionConstants.PRIMARY_CAMERA_FPS);

            primaryCameraReader.setSettings(primarySettings);
            toClose.add(primaryCameraReader);
        }
        else
        {
            primaryCameraReader = null;
        }

        IRunnableFrameReader secondaryCameraReader;
        CameraSettings secondarySettings = null;
        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            secondaryCameraReader = new CameraReader(controller, VisionConstants.SECONDARY_CAMERA_ID);
            String cameraStringSecondary = "" + VisionConstants.SECONDARY_CAMERA_ID;
            if (!secondaryCameraReader.open())
            {
                Logger.writeError(String.format("unable to open secondary camera reader '%s'!", cameraStringSecondary));
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            secondarySettings =
                new CameraSettings(
                    VisionConstants.SECONDARY_PIPELINE_VISION_EXPOSURE,
                    VisionConstants.SECONDARY_PIPELINE_VISION_BRIGHTNESS,
                    VisionConstants.SECONDARY_CAMERA_RESOLUTION_X,
                    VisionConstants.SECONDARY_CAMERA_RESOLUTION_Y,
                    VisionConstants.SECONDARY_CAMERA_FPS);

            secondaryCameraReader.setSettings(secondarySettings);
            toClose.add(secondaryCameraReader);
        }
        else
        {
            secondaryCameraReader = null;
        }

        IRunnableFrameReader tertiaryCameraReader;
        CameraSettings tertiarySettings = null;
        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            tertiaryCameraReader = new CameraReader(controller, VisionConstants.TERTIARY_CAMERA_ID);
            String cameraStringTertiary = "" + VisionConstants.TERTIARY_CAMERA_ID;
            if (!tertiaryCameraReader.open())
            {
                Logger.writeError(String.format("unable to open tertiary camera reader '%s'!", cameraStringTertiary));
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            tertiarySettings =
                new CameraSettings(
                    VisionConstants.TERTIARY_PIPELINE_VISION_EXPOSURE,
                    VisionConstants.TERTIARY_PIPELINE_VISION_BRIGHTNESS,
                    VisionConstants.TERTIARY_CAMERA_RESOLUTION_X,
                    VisionConstants.TERTIARY_CAMERA_RESOLUTION_Y,
                    VisionConstants.TERTIARY_CAMERA_FPS);

            tertiaryCameraReader.setSettings(tertiarySettings);
            toClose.add(tertiaryCameraReader);
        }
        else
        {
            tertiaryCameraReader = null;
        }

        IResultWriter<DistancesAnglesIdMeasurements> primaryWriter;
        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            primaryWriter =
                new NetworkTableDistancesAnglesIdWriter(
                    "atr",
                    VisionConstants.PRIMARY_DEBUG_STREAM_NAME,
                    VisionConstants.PRIMARY_STREAM_RESOLUTION_X,
                    VisionConstants.PRIMARY_STREAM_RESOLUTION_Y);

            if (!primaryWriter.open())
            {
                Logger.writeError("unable to open primary writer!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(primaryWriter);
        }
        else
        {
            primaryWriter = null;
        }

        IResultWriter<DistancesAnglesIdMeasurements> secondaryWriter;
        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            secondaryWriter =
                new NetworkTableDistancesAnglesIdWriter(
                    "atf",
                    VisionConstants.SECONDARY_DEBUG_STREAM_NAME,
                    VisionConstants.SECONDARY_STREAM_RESOLUTION_X,
                    VisionConstants.SECONDARY_STREAM_RESOLUTION_Y);

            if (!secondaryWriter.open())
            {
                Logger.writeError("unable to open secondary writer!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(secondaryWriter);
        }
        else
        {
            secondaryWriter = null;
        }

        IResultWriter<DistancesAnglesIdMeasurements> tertiaryWriter;
        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            tertiaryWriter =
                new NetworkTableDistancesAnglesIdWriter(
                    "atfr",
                    VisionConstants.TERTIARY_DEBUG_STREAM_NAME,
                    VisionConstants.TERTIARY_STREAM_RESOLUTION_X,
                    VisionConstants.TERTIARY_STREAM_RESOLUTION_Y);

            if (!tertiaryWriter.open())
            {
                Logger.writeError("unable to open tertiary writer!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(tertiaryWriter);
        }
        else
        {
            tertiaryWriter = null;
        }

        IResultWriter<AbsolutePositionMeasurement> absolutePositionWriter;
        if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
        {
            absolutePositionWriter =
                new NetworkTableAbsolutePositionWriter(
                    "abs",
                    null,
                    0,
                    0);

            if (!absolutePositionWriter.open())
            {
                Logger.writeError("unable to open tertiary writer!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(absolutePositionWriter);
        }
        else
        {
            absolutePositionWriter = null;
        }

        IResultWriter<AprilTagDetection> primaryCalculator;
        IResultWriter<AprilTagDetection> primaryAbsolutePositionCalculator;
        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            primaryCalculator =
                new DistancesAnglesIdVisionCalculator(
                    primaryWriter,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                    VisionConstants.PRIMARY_CAMERA_CENTER_X,
                    VisionConstants.PRIMARY_CAMERA_CENTER_Y,
                    VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_X,
                    VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_Y,
                    VisionConstants.PRIMARY_CAMERA_ROLL,
                    VisionConstants.PRIMARY_CAMERA_PITCH,
                    VisionConstants.PRIMARY_CAMERA_YAW,
                    VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                    VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                    VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);

            if (!primaryCalculator.open())
            {
                Logger.writeError("unable to open primary calculator!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(primaryCalculator);

            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                primaryAbsolutePositionCalculator =
                    new AbsolutePositionVisionCalculator(
                        absolutePositionWriter,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_SIZE,
                        VisionConstants.PRIMARY_CAMERA_CENTER_X,
                        VisionConstants.PRIMARY_CAMERA_CENTER_Y,
                        VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_X,
                        VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_Y,
                        VisionConstants.PRIMARY_CAMERA_ROLL,
                        VisionConstants.PRIMARY_CAMERA_PITCH,
                        VisionConstants.PRIMARY_CAMERA_YAW,
                        VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                        VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);

                if (!primaryAbsolutePositionCalculator.open())
                {
                    Logger.writeError("unable to open primary absolute position calculator!");
                    for (IOpenable openable : toClose)
                    {
                        openable.close();
                    }

                    System.exit(1);
                }

                toClose.add(primaryAbsolutePositionCalculator);
            }
            else
            {
                primaryAbsolutePositionCalculator = null;
            }
        }
        else
        {
            primaryCalculator = null;
            primaryAbsolutePositionCalculator = null;
        }

        IResultWriter<AprilTagDetection> secondaryCalculator;
        IResultWriter<AprilTagDetection> secondaryAbsolutePositionCalculator;
        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            secondaryCalculator =
                new DistancesAnglesIdVisionCalculator(
                    secondaryWriter,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_SIZE,
                    VisionConstants.SECONDARY_CAMERA_CENTER_X,
                    VisionConstants.SECONDARY_CAMERA_CENTER_Y,
                    VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_X,
                    VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_Y,
                    VisionConstants.SECONDARY_CAMERA_ROLL,
                    VisionConstants.SECONDARY_CAMERA_PITCH,
                    VisionConstants.SECONDARY_CAMERA_YAW,
                    VisionConstants.SECONDARY_CAMERA_FORWARD_OFFSET,
                    VisionConstants.SECONDARY_CAMERA_PORT_OFFSET,
                    VisionConstants.SECONDARY_CAMERA_VERTICAL_OFFSET);

            if (!secondaryCalculator.open())
            {
                Logger.writeError("unable to open secondary calculator!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(secondaryCalculator);

            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                secondaryAbsolutePositionCalculator =
                    new AbsolutePositionVisionCalculator(
                        absolutePositionWriter,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_SIZE,
                        VisionConstants.SECONDARY_CAMERA_CENTER_X,
                        VisionConstants.SECONDARY_CAMERA_CENTER_Y,
                        VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_X,
                        VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_Y,
                        VisionConstants.SECONDARY_CAMERA_ROLL,
                        VisionConstants.SECONDARY_CAMERA_PITCH,
                        VisionConstants.SECONDARY_CAMERA_YAW,
                        VisionConstants.SECONDARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.SECONDARY_CAMERA_PORT_OFFSET,
                        VisionConstants.SECONDARY_CAMERA_VERTICAL_OFFSET);
    
                if (!secondaryAbsolutePositionCalculator.open())
                {
                    Logger.writeError("unable to open secondary absolute position calculator!");
                    for (IOpenable openable : toClose)
                    {
                        openable.close();
                    }

                    System.exit(1);
                }

                toClose.add(secondaryAbsolutePositionCalculator);
            }
            else
            {
                secondaryAbsolutePositionCalculator = null;
            }
        }
        else
        {
            secondaryCalculator = null;
            secondaryAbsolutePositionCalculator = null;
        }

        IResultWriter<AprilTagDetection> tertiaryCalculator;
        IResultWriter<AprilTagDetection> tertiaryAbsolutePositionCalculator;
        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            tertiaryCalculator =
                new DistancesAnglesIdVisionCalculator(
                    tertiaryWriter,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_SIZE,
                    VisionConstants.TERTIARY_CAMERA_CENTER_X,
                    VisionConstants.TERTIARY_CAMERA_CENTER_Y,
                    VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_X,
                    VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_Y,
                    VisionConstants.TERTIARY_CAMERA_ROLL,
                    VisionConstants.TERTIARY_CAMERA_PITCH,
                    VisionConstants.TERTIARY_CAMERA_YAW,
                    VisionConstants.TERTIARY_CAMERA_FORWARD_OFFSET,
                    VisionConstants.TERTIARY_CAMERA_PORT_OFFSET,
                    VisionConstants.TERTIARY_CAMERA_VERTICAL_OFFSET);

            if (!tertiaryCalculator.open())
            {
                Logger.writeError("unable to open tertiary calculator!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(tertiaryCalculator);

            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                tertiaryAbsolutePositionCalculator =
                    new AbsolutePositionVisionCalculator(
                        absolutePositionWriter,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_SIZE,
                        VisionConstants.TERTIARY_CAMERA_CENTER_X,
                        VisionConstants.TERTIARY_CAMERA_CENTER_Y,
                        VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_X,
                        VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_Y,
                        VisionConstants.TERTIARY_CAMERA_ROLL,
                        VisionConstants.TERTIARY_CAMERA_PITCH,
                        VisionConstants.TERTIARY_CAMERA_YAW,
                        VisionConstants.TERTIARY_CAMERA_FORWARD_OFFSET,
                        VisionConstants.TERTIARY_CAMERA_PORT_OFFSET,
                        VisionConstants.TERTIARY_CAMERA_VERTICAL_OFFSET);

                if (!tertiaryAbsolutePositionCalculator.open())
                {
                    Logger.writeError("unable to open tertiary absolute position calculator!");
                    for (IOpenable openable : toClose)
                    {
                        openable.close();
                    }

                    System.exit(1);
                }

                toClose.add(tertiaryAbsolutePositionCalculator);
            }
            else
            {
                tertiaryAbsolutePositionCalculator = null;
            }
        }
        else
        {
            tertiaryCalculator = null;
            tertiaryAbsolutePositionCalculator = null;
        }

        SwitchedVisionSystem primaryVisionSystem;
        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            Mat primaryCameraMask = loadMask(Program.PrimaryMaskPath);

            ImageUndistorter primaryUndistorter = new ImageUndistorter(
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                VisionConstants.PRIMARY_CAMERA_CENTER_X,
                VisionConstants.PRIMARY_CAMERA_CENTER_Y,
                VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_X,
                VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_Y,
                VisionConstants.PRIMARY_CAMERA_DIFF_COEF);

            IFramePipeline primaryFramePipeline =
                new AprilTagPipeline<AprilTagDetection>(
                    primaryCalculator,
                    VisionConstants.PRIMARY_PIPELINE_SHOULD_MASK ? primaryCameraMask : null,
                    VisionConstants.PRIMARY_PIPELINE_SHOULD_UNDISTORT ? primaryUndistorter : null,
                    VisionConstants.PRIMARY_CAMERA_GRAYSCALE,
                    new DesiredAprilTagFilter(
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN,
                        controller),
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING);

            // optionally log frames from pipelines
            if (imageLoggingDirectory != null && VisionConstants.LOG_IMAGES)
            {
                primaryFramePipeline = new LoggingPipeline("primary.", imageLoggingDirectory, primaryFramePipeline);
            }

            IFramePipeline primaryAbsolutePositionFramePipeline;
            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                primaryAbsolutePositionFramePipeline =
                    new AprilTagPipeline<AprilTagDetection>(
                        primaryAbsolutePositionCalculator,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_MASK ? primaryCameraMask : null,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_UNDISTORT ? primaryUndistorter : null,
                        VisionConstants.PRIMARY_CAMERA_GRAYSCALE,
                        new LargestAprilTagFilter(
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DECODE_SHARPENING);
            }
            else
            {
                primaryAbsolutePositionFramePipeline = null;
            }

            primaryVisionSystem = new SwitchedVisionSystem(
                primaryCameraReader,
                controller,
                new IFramePipeline[] { primaryFramePipeline, primaryAbsolutePositionFramePipeline },
                new int[] { VisionConstants.PRIMARY_PIPELINE_VISION_MODE, VisionConstants.ABSOLUTE_PIPELINE_VISION_MODE },
                new CameraSettings[] { primarySettings, primarySettings },
                primarySettings,
                VisionConstants.PRIMARY_CAMERA_STREAM_NAME,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y);

            if (!primaryVisionSystem.open())
            {
                Logger.writeError("unable to open vision system!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(primaryVisionSystem);
        }
        else
        {
            primaryVisionSystem = null;
        }

        SwitchedVisionSystem secondaryVisionSystem;
        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            Mat secondaryCameraMask = loadMask(Program.SecondaryMaskPath);

            ImageUndistorter secondaryUndistorter = new ImageUndistorter(
                VisionConstants.SECONDARY_CAMERA_RESOLUTION_X,
                VisionConstants.SECONDARY_CAMERA_RESOLUTION_Y,
                VisionConstants.SECONDARY_CAMERA_CENTER_X,
                VisionConstants.SECONDARY_CAMERA_CENTER_Y,
                VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_X,
                VisionConstants.SECONDARY_CAMERA_FOCAL_LENGTH_Y,
                VisionConstants.SECONDARY_CAMERA_DIFF_COEF);

            IFramePipeline secondaryFramePipeline =
                new AprilTagPipeline<AprilTagDetection>(
                    secondaryCalculator,
                    VisionConstants.SECONDARY_PIPELINE_SHOULD_MASK ? secondaryCameraMask : null,
                    VisionConstants.SECONDARY_PIPELINE_SHOULD_UNDISTORT ? secondaryUndistorter : null,
                    VisionConstants.SECONDARY_CAMERA_GRAYSCALE,
                    new DesiredAprilTagFilter(
                        VisionConstants.SECONDARY_PIPELINE_APRILTAG_MIN_AREA,
                        VisionConstants.SECONDARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN,
                        controller),
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_REFINE_EDGES,
                    VisionConstants.SECONDARY_PIPELINE_APRILTAG_DECODE_SHARPENING);

            // optionally log frames from pipelines
            if (imageLoggingDirectory != null && VisionConstants.LOG_IMAGES)
            {
                secondaryFramePipeline = new LoggingPipeline("secondary.", imageLoggingDirectory, secondaryFramePipeline);
            }

            IFramePipeline secondaryAbsolutePositionFramePipeline;
            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                secondaryAbsolutePositionFramePipeline =
                    new AprilTagPipeline<AprilTagDetection>(
                        secondaryAbsolutePositionCalculator,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_MASK ? secondaryCameraMask : null,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_UNDISTORT ? secondaryUndistorter : null,
                        VisionConstants.SECONDARY_CAMERA_GRAYSCALE,
                        new LargestAprilTagFilter(
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DECODE_SHARPENING);
            }
            else
            {
                secondaryAbsolutePositionFramePipeline = null;
            }

            secondaryVisionSystem = new SwitchedVisionSystem(
                secondaryCameraReader,
                controller,
                new IFramePipeline[] { secondaryFramePipeline, secondaryAbsolutePositionFramePipeline },
                new int[] { VisionConstants.SECONDARY_PIPELINE_VISION_MODE, VisionConstants.ABSOLUTE_PIPELINE_VISION_MODE },
                new CameraSettings[] { secondarySettings, secondarySettings },
                secondarySettings,
                VisionConstants.SECONDARY_CAMERA_STREAM_NAME,
                VisionConstants.SECONDARY_CAMERA_RESOLUTION_X,
                VisionConstants.SECONDARY_CAMERA_RESOLUTION_Y);

            if (!secondaryVisionSystem.open())
            {
                Logger.writeError("unable to open vision system!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(secondaryVisionSystem);
        }
        else
        {
            secondaryVisionSystem = null;
        }

        SwitchedVisionSystem tertiaryVisionSystem;
        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            Mat tertiaryCameraMask = loadMask(Program.TertiaryMaskPath);

            ImageUndistorter tertiaryUndistorter = new ImageUndistorter(
                VisionConstants.TERTIARY_CAMERA_RESOLUTION_X,
                VisionConstants.TERTIARY_CAMERA_RESOLUTION_Y,
                VisionConstants.TERTIARY_CAMERA_CENTER_X,
                VisionConstants.TERTIARY_CAMERA_CENTER_Y,
                VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_X,
                VisionConstants.TERTIARY_CAMERA_FOCAL_LENGTH_Y,
                VisionConstants.TERTIARY_CAMERA_DIFF_COEF);

            IFramePipeline tertiaryFramePipeline =
                new AprilTagPipeline<AprilTagDetection>(
                    tertiaryCalculator,
                    VisionConstants.TERTIARY_PIPELINE_SHOULD_MASK ? tertiaryCameraMask : null,
                    VisionConstants.TERTIARY_PIPELINE_SHOULD_UNDISTORT ? tertiaryUndistorter : null,
                    VisionConstants.TERTIARY_CAMERA_GRAYSCALE,
                    new DesiredAprilTagFilter(
                        VisionConstants.TERTIARY_PIPELINE_APRILTAG_MIN_AREA,
                        VisionConstants.TERTIARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN,
                        controller),
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_REFINE_EDGES,
                    VisionConstants.TERTIARY_PIPELINE_APRILTAG_DECODE_SHARPENING);

            // optionally log frames from pipelines
            if (imageLoggingDirectory != null && VisionConstants.LOG_IMAGES)
            {
                tertiaryFramePipeline = new LoggingPipeline("tertiary.", imageLoggingDirectory, tertiaryFramePipeline);
            }

            IFramePipeline tertiaryAbsolutePositionFramePipeline;
            if (VisionConstants.USE_ABSOLUTE_POSITION_PIPELINES)
            {
                tertiaryAbsolutePositionFramePipeline =
                    new AprilTagPipeline<AprilTagDetection>(
                        tertiaryAbsolutePositionCalculator,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_MASK ? tertiaryCameraMask : null,
                        VisionConstants.ABSOLUTE_PIPELINE_SHOULD_UNDISTORT ? tertiaryUndistorter : null,
                        VisionConstants.TERTIARY_CAMERA_GRAYSCALE,
                        new LargestAprilTagFilter(
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_AREA,
                            VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DETECTION_FAMILY,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_PROCESSING_THREADS,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_DECIMATE,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_QUAD_SIGMA,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_REFINE_EDGES,
                        VisionConstants.ABSOLUTE_PIPELINE_APRILTAG_DECODE_SHARPENING);
            }
            else
            {
                tertiaryAbsolutePositionFramePipeline = null;
            }

            tertiaryVisionSystem = new SwitchedVisionSystem(
                tertiaryCameraReader,
                controller,
                new IFramePipeline[] { tertiaryFramePipeline, tertiaryAbsolutePositionFramePipeline },
                new int[] { VisionConstants.TERTIARY_PIPELINE_VISION_MODE, VisionConstants.ABSOLUTE_PIPELINE_VISION_MODE },
                new CameraSettings[] { tertiarySettings, tertiarySettings },
                tertiarySettings,
                VisionConstants.TERTIARY_CAMERA_STREAM_NAME,
                VisionConstants.TERTIARY_CAMERA_RESOLUTION_X,
                VisionConstants.TERTIARY_CAMERA_RESOLUTION_Y);

            if (!tertiaryVisionSystem.open())
            {
                Logger.writeError("unable to open vision system!");
                for (IOpenable openable : toClose)
                {
                    openable.close();
                }

                System.exit(1);
            }

            toClose.add(tertiaryVisionSystem);
        }
        else
        {
            tertiaryVisionSystem = null;
        }

        Thread heartbeatThread = new Thread(heartbeat);
        heartbeatThread.start();

        Thread primaryVisionThread;
        Thread primaryCameraThread;
        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            primaryVisionThread = new Thread(primaryVisionSystem);
            primaryVisionThread.start();

            primaryCameraThread = new Thread(primaryCameraReader);
            primaryCameraThread.start();
        }
        else
        {
            primaryVisionThread = null;
            primaryCameraThread = null;
        }

        Thread secondaryVisionThread;
        Thread secondaryCameraThread;
        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            secondaryVisionThread = new Thread(secondaryVisionSystem);
            secondaryVisionThread.start();

            secondaryCameraThread = new Thread(secondaryCameraReader);
            secondaryCameraThread.start();
        }
        else
        {
            secondaryVisionThread = null;
            secondaryCameraThread = null;
        }

        Thread tertiaryVisionThread;
        Thread tertiaryCameraThread;
        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            tertiaryVisionThread = new Thread(tertiaryVisionSystem);
            tertiaryVisionThread.start();

            tertiaryCameraThread = new Thread(tertiaryCameraReader);
            tertiaryCameraThread.start();
        }
        else
        {
            tertiaryVisionThread = null;
            tertiaryCameraThread = null;
        }

        // attempt to cleanly stop if we receive a shutdown signal:
        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                @Override
                public void run()
                {
                    if (VisionConstants.USE_PRIMARY_CAMERA)
                    {
                        primaryCameraReader.stop();
                        primaryVisionSystem.stop();
                    }

                    if (VisionConstants.USE_SECONDARY_CAMERA)
                    {
                        secondaryCameraReader.stop();
                        secondaryVisionSystem.stop();
                    }

                    if (VisionConstants.USE_TERTIARY_CAMERA)
                    {
                        tertiaryCameraReader.stop();
                        tertiaryVisionSystem.stop();
                    }

                    heartbeat.stop();
                    for (IOpenable openable : toClose)
                    {
                        openable.close();
                    }

                    if (VisionConstants.USE_PRIMARY_CAMERA)
                    {
                        primaryVisionThread.interrupt();
                        primaryCameraThread.interrupt();
                    }

                    if (VisionConstants.USE_SECONDARY_CAMERA)
                    {
                        secondaryVisionThread.interrupt();
                        secondaryCameraThread.interrupt();
                    }

                    if (VisionConstants.USE_TERTIARY_CAMERA)
                    {
                        tertiaryVisionThread.interrupt();
                        tertiaryCameraThread.interrupt();
                    }
                }
            });

        Thread.yield();
        while (true)
        {
            try
            {
                Thread.sleep(5000);
                if (Thread.interrupted())
                {
                    break;
                }

                Logger.flush();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }

        if (VisionConstants.USE_PRIMARY_CAMERA)
        {
            primaryCameraReader.stop();
            primaryVisionSystem.stop();
        }

        if (VisionConstants.USE_SECONDARY_CAMERA)
        {
            secondaryCameraReader.stop();
            secondaryVisionSystem.stop();
        }

        if (VisionConstants.USE_TERTIARY_CAMERA)
        {
            tertiaryCameraReader.stop();
            tertiaryVisionSystem.stop();
        }

        heartbeat.stop();
        for (IOpenable openable : toClose)
        {
            openable.close();
        }
    }

    private static Mat loadMask(String maskPath)
    {
        Mat mask = null;
        InputStream maskStream = Program.class.getResourceAsStream(maskPath);
        if (maskStream != null)
        {
            try
            {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int bytesRead;
                byte[] data = new byte[16384];
                while ((bytesRead = maskStream.read(data, 0, data.length)) != -1)
                {
                    buffer.write(data, 0, bytesRead);
                }

                buffer.flush();
                byte[] temporaryImageInMemory = buffer.toByteArray();
                buffer.close();
                maskStream.close();

                mask = Imgcodecs.imdecode(new MatOfByte(temporaryImageInMemory), Imgcodecs.IMREAD_COLOR);
                if (mask == null)
                {
                    System.out.println("Couldn't decode mask " + maskPath);
                }
                else
                {
                    System.out.println("read mask "  + maskPath + ", size " + mask.size() + " type: " + CvType.typeToString(mask.type()));
                }

                return mask;
            }
            catch (IOException ex)
            {
                System.out.println("Error loading mask " + maskPath);
                ex.printStackTrace(System.err);
            }
        }
        else
        {
            System.out.println("Mask resource " + maskPath + " not found");
        }

        return null;
    }
}
