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
import frc1318.vision.helpers.HSVFilter;
import frc1318.vision.helpers.ImageUndistorter;
import frc1318.vision.pipeline.*;
import frc1318.vision.reader.*;
import frc1318.vision.writer.*;

public class Program
{
    private enum Mode
    {
        None,
        HSV,
        AprilTag,
        Switched,
        ImageSaver,
        DeviceEnueration,
        Calibrate,
    }

    private static String MaskPath = "/mask.png";

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

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

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
        String cameraUrl = null;
        String sourceFileName = null;
        String targetFileName = null;
        String maskFileName = null;
        boolean diagnostic = false;
        boolean show = false;
        boolean skipUndistort = false;
        boolean sb2 = false;
        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];
            if (arg.equalsIgnoreCase("/?") ||
                arg.equalsIgnoreCase("/h"))
            {
                printUsage();
                return;
            }

            if (arg.startsWith("/c:"))
            {
                if (cameraUrl != null)
                {
                    System.err.println("Error: multiple /c arguments");
                    printUsage();
                    return;
                }

                cameraUrl = arg.substring("/c:".length());
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

                selectedMode = Mode.DeviceEnueration; 
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

                selectedMode = Mode.AprilTag; 
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

                if (cameraUrl == null)
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
                    System.err.println("Error: /diagnostic not supported for HSV mode");
                    printUsage();
                    return;
                }

                break;

            case AprilTag:
            case Switched:
                break;

            case DeviceEnueration:
                if (cameraUrl != null || sourceFileName != null || targetFileName == null || maskFileName != null || diagnostic || show)
                {
                    System.err.println("Error: /deviceEnueration requires /t:directory and no other parameter");
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

                break;
        }

        if (sourceFileName != null && cameraUrl != null)
        {
            System.err.println("Error: don't support /c and /s together");
            printUsage();
            return;
        }

        if (selectedMode == Mode.DeviceEnueration)
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
            return;
        }

        ConsoleController controller = new ConsoleController();

        IFrameReader frameReader = null;
        if (cameraUrl != null)
        {
            // parse cameraId, if relevant:
            int cameraId = -1;
            boolean cameraIsNumeric = true;
            for (int c = 0; c < cameraUrl.length(); c++)
            {
                if (!Character.isDigit(cameraUrl.charAt(c)))
                {
                    cameraIsNumeric = false;
                    break;
                }
            }

            if (cameraIsNumeric)
            {
                cameraId = Integer.parseInt(cameraUrl);
                cameraUrl = null;

                frameReader = new CameraReader(cameraId);
            }
            else
            {
                frameReader = new CameraReader(cameraUrl);
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
        else
        {
            System.err.println("Error: unknown image source");
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
                    mask = maskReader.getCurrentFrame();
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
        if (selectedMode != Mode.Calibrate)
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
        if (!skipUndistort)
        {
            undistorter = new ImageUndistorter(
                sb2 ? VisionConstants.SB2_CAMERA_RESOLUTION_X : VisionConstants.ELP_CAMERA_RESOLUTION_X,
                sb2 ? VisionConstants.SB2_CAMERA_RESOLUTION_Y : VisionConstants.ELP_CAMERA_RESOLUTION_Y,
                sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_CAMERA_A_CENTER_X,
                sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_CAMERA_A_CENTER_Y,
                sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_X,
                sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_Y,
                sb2 ? VisionConstants.SB2_CAMERA_A_DIFF_COEF : VisionConstants.ELP_CAMERA_A_DIFF_COEF);
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

        if (selectedMode == Mode.AprilTag || selectedMode == Mode.Switched)
        {
            if (diagnostic)
            {
                AprilTagDiagnosticWriter aprilTagWriter =
                    new AprilTagDiagnosticWriter(
                        pointWriter,
                        VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_X,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_Y,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_CAMERA_A_CENTER_X,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_CAMERA_A_CENTER_Y,
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
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_X : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_X,
                        sb2 ? VisionConstants.SB2_CAMERA_A_FOCAL_LENGTH_Y : VisionConstants.ELP_CAMERA_A_FOCAL_LENGTH_Y,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_X : VisionConstants.ELP_CAMERA_A_CENTER_X,
                        sb2 ? VisionConstants.SB2_CAMERA_A_CENTER_Y : VisionConstants.ELP_CAMERA_A_CENTER_Y,
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

        if (outputs.size() == 0 || framePipelines.size() == 0)
        {
            System.err.println(String.format("unknown mode '%s'!", selectedMode.toString()));
            return;
        }

        if (!frameReader.open())
        {
            System.err.println(String.format("unable to open frame reader '%s'!", cameraUrl));
            System.exit(1);
        }

        CameraSettings primarySettings =
            new CameraSettings(
                VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE,
                VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                VisionConstants.PRIMARY_CAMERA_FPS);

        CameraSettings secondarySettings =
            new CameraSettings(
                VisionConstants.EXAMPLE_PIPELINE_VISION_EXPOSURE,
                VisionConstants.EXAMPLE_PIPELINE_VISION_BRIGHTNESS,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                VisionConstants.PRIMARY_CAMERA_FPS);

        frameReader.setSettings(primarySettings);

        for (IResultWriter<?> output : outputs)
        {
            if (!output.open())
            {
                System.err.println("unable to open output");
                frameReader.stop();
                System.exit(1);
            }
        }

        if (!controller.open())
        {
            System.err.println("unable to open controller!");
            frameReader.stop();
            System.exit(1);
        }

        VisionSystemBase visionSystem;
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
        else
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

        if (!visionSystem.open())
        {
            System.err.println("unable to open vision system!");
            frameReader.stop();
            controller.close();
            System.exit(1);
        }

        Thread cameraThread = new Thread(frameReader);
        cameraThread.start();

        Thread visionThread = new Thread(visionSystem);
        visionThread.start();

        controller.run();

        frameReader.stop();
        visionSystem.stop();
        visionSystem.close();

        frameReader.close();
        controller.close();
        for (IResultWriter<?> output : outputs)
        {
            output.close();
        }

        if (pointWriter != null)
        {
            pointWriter.close();
        }

        cameraThread.interrupt();
        visionThread.interrupt();
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
        System.out.println("VisionSystem.jar                                                -- run full primary vision system pipelines");
        System.out.println("VisionSystem.jar [/hsv] /s:file [/t:directory] [/show] [/mask]         -- test hsv filtering for that file, outputting result to console (or directory)");
        System.out.println("VisionSystem.jar [/hsv] /s:directory [/t:directory] [/show] [/mask]    -- test hsv filtering for files in that directory, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar [/hsv] /c:camera [/t:directory] [/show] [/mask]       -- test hsv filtering using the provided camera, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar /switched /c:camera [/t:directory] [/diagnostic] [/show] [/mask]    -- test switched camera functionality for both apriltag filtering and hsv filtering using the provided camera, results to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /c:camera [/t:directory] [/diagnostic] [/show] [/mask]    -- test apriltag filtering using the provided camera, results to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /s:file [/t:directory] [/diagnostic] [/show] [/mask]      -- test apriltag filtering for that file, outputting result to console (or directory)");
        System.out.println("VisionSystem.jar /apriltag /s:directory [/t:directory] [/diagnostic] [/show] [/mask] -- test apriltag filtering for files in that directory, outputting results to console (or directory)");
        System.out.println("VisionSystem.jar /calibrate /c:camera [/t:directory] [/show]    -- calibrate camera based on the provided camera");
        System.out.println("VisionSystem.jar /calibrate /s:directory [/t:directory] [/show] -- calibrate camera based on the provided images");
        System.out.println("VisionSystem.jar /deviceEnumeration /t:directory                -- run device enumeration");
        System.out.println("VisionSystem.jar /imagesaver /c:camera /t:directory             -- save images from webcam to the provided directory");
    }

    private static void runVisionSystem()
    {
        IFrameReader cameraReader = new CameraReader(VisionConstants.PRIMARY_CAMERA_ID);

        String cameraStringPrimary = "" + VisionConstants.PRIMARY_CAMERA_ID;

        if (!cameraReader.open())
        {
            System.err.println(String.format("unable to open primary camera reader '%s'!", cameraStringPrimary));
            System.exit(1);
        }

        CameraSettings primarySettings =
            new CameraSettings(
                VisionConstants.PRIMARY_PIPELINE_VISION_EXPOSURE,
                VisionConstants.PRIMARY_PIPELINE_VISION_BRIGHTNESS,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
                VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y,
                VisionConstants.PRIMARY_CAMERA_FPS);

        cameraReader.setSettings(primarySettings);

        IResultWriter<DistancesAnglesIdMeasurements> primaryWriter =
            new NetworkTableDistancesAnglesIdWriter(
                "at",
                VisionConstants.PRIMARY_DEBUG_STREAM_NAME,
                VisionConstants.PRIMARY_STREAM_RESOLUTION_X,
                VisionConstants.PRIMARY_STREAM_RESOLUTION_Y);

        if (!primaryWriter.open())
        {
            System.err.println("unable to open primary writer!");
            cameraReader.close();
            System.exit(1);
        }

        IController controller = 
            new NetworkTableController();
        if (!controller.open())
        {
            System.err.println("unable to open controller!");
            cameraReader.close();
            primaryWriter.close();
            System.exit(1);
        }

        IResultWriter<AprilTagDetection> primaryCalculator =
            new DistancesAnglesIdVisionCalculator(
                primaryWriter,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_SIZE,
                VisionConstants.PRIMARY_CAMERA_CENTER_X,
                VisionConstants.PRIMARY_CAMERA_CENTER_Y,
                VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_X,
                VisionConstants.PRIMARY_CAMERA_FOCAL_LENGTH_Y,
                VisionConstants.PRIMARY_CAMERA_YAW,
                VisionConstants.PRIMARY_CAMERA_PITCH,
                VisionConstants.PRIMARY_CAMERA_ROLL,
                VisionConstants.PRIMARY_CAMERA_FORWARD_OFFSET,
                VisionConstants.PRIMARY_CAMERA_PORT_OFFSET,
                VisionConstants.PRIMARY_CAMERA_VERTICAL_OFFSET);
                
        if (!primaryCalculator.open())
        {
            System.err.println("unable to open primary calculator!");
            cameraReader.close();
            primaryWriter.close();
            controller.close();
            System.exit(1);
        }

        // scan through the list of USB devices until we find one to log images to
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
                        File imagesSubdirectory = new File(deviceRootDirectory, "images");
                        imagesSubdirectory.mkdir();
                        System.out.println("Writing to " + imagesSubdirectory);

                        imageLoggingDirectory = imagesSubdirectory;
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

        HeartbeatWriter heartbeat = new HeartbeatWriter();
        if (!heartbeat.open())
        {
            System.err.println("unable to open heartbeat writer!");
            cameraReader.close();
            primaryWriter.close();
            controller.close();
            primaryCalculator.close();
            System.exit(1);
        }

        Mat mask = null;
        InputStream maskStream = Program.class.getResourceAsStream(Program.MaskPath);
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
                    System.out.println("Couldn't load mask");
                }
                else
                {
                    System.out.println("read mask, size " + mask.size() + " type: " + CvType.typeToString(mask.type()));
                }
            }
            catch (IOException ex)
            {
                System.out.println("Error loading mask");
                ex.printStackTrace(System.err);
            }
        }
        else
        {
            System.out.println("Mask resource not found");
        }

        ImageUndistorter undistorter = new ImageUndistorter(
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
                VisionConstants.PRIMARY_PIPELINE_SHOULD_MASK ? mask : null,
                VisionConstants.PRIMARY_PIPELINE_SHOULD_UNDISTORT ? undistorter : null,
                new LargestAprilTagFilter(
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_AREA,
                    VisionConstants.PRIMARY_PIPELINE_APRILTAG_MIN_DECISION_MARGIN),
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_DETECTION_FAMILY,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_MAX_HAMMING_DISTANCE,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_PROCESSING_THREADS,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_DECIMATE,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_QUAD_SIGMA,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_REFINE_EDGES,
                VisionConstants.PRIMARY_PIPELINE_APRILTAG_DECODE_SHARPENING);

        // optionally log frames from pipelines
        if (imageLoggingDirectory != null)
        {
            primaryFramePipeline = new LoggingPipeline("primary.", imageLoggingDirectory, primaryFramePipeline);
        }

        SwitchedVisionSystem visionSystem = new SwitchedVisionSystem(
            cameraReader,
            controller,
            new IFramePipeline[] { primaryFramePipeline },
            new int[] { VisionConstants.PRIMARY_PIPELINE_VISION_MODE },
            new CameraSettings[] { primarySettings },
            primarySettings,
            VisionConstants.PRIMARY_CAMERA_STREAM_NAME,
            VisionConstants.PRIMARY_CAMERA_RESOLUTION_X,
            VisionConstants.PRIMARY_CAMERA_RESOLUTION_Y);

        if (!visionSystem.open())
        {
            System.err.println("unable to open vision system!");
            cameraReader.close();
            primaryWriter.close();
            heartbeat.close();
            controller.close();
            primaryCalculator.close();
            System.exit(1);
        }

        Thread visionThread = new Thread(visionSystem);
        visionThread.start();

        Thread heartbeatThread = new Thread(heartbeat);
        heartbeatThread.start();

        Thread cameraThread = new Thread(cameraReader);
        cameraThread.start();

        // attempt to cleanly stop if we receive a shutdown signal:
        Runtime.getRuntime().addShutdownHook(
            new Thread()
            {
                @Override
                public void run()
                {
                    cameraReader.stop();
                    heartbeat.stop();
                    visionSystem.stop();
                    cameraReader.close();
                    primaryWriter.close();
                    heartbeat.close();
                    controller.close();
                    primaryCalculator.close();

                    visionThread.interrupt();
                }
            });

        Thread.yield();
        while (true)
        {
            try
            {
                Thread.sleep(1000);
                if (Thread.interrupted())
                {
                    break;
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }

        cameraReader.stop();

        heartbeat.stop();
        visionSystem.stop();
        cameraReader.close();
        primaryWriter.close();
        heartbeat.close();
        controller.close();
        primaryCalculator.close();
    }
}
