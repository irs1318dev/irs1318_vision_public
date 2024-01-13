package frc1318.vision;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;

import frc1318.vision.filters.LargestCenterFilter;
import frc1318.vision.helpers.HSVFilter;
import frc1318.vision.pipeline.HSVPipeline;

public class HSVPipelineTest
{
    private static final String RepoPath = "src/test/resources/";

    //@Test
    public void testLoadImage()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        this.testImagePath("Capture1.PNG", 226.51737451737452, 68.58751608751608);
        this.testImagePath("Capture2.PNG", 166.3274074074074, 67.59629629629629);
        this.testImagePath("Capture3.PNG", 155.2574139976275, 79.27995255041517);
        this.testImagePath("Capture4.PNG", 180.40930869267623, 23.529089664613277);
        this.testImagePath("Capture5.PNG", 158.16500332667997, 22.772455089820358);
        this.testImagePath("Capture6.PNG", 254.771847690387, 107.28870162297129);
        this.testImagePath("Capture7.PNG", 41.716626698641086, 52.47881694644284);
    }

    private void testImagePath(String imagePath, double x, double y)
    {
        @SuppressWarnings("unchecked")
        IResultWriter<Point> pointWriter = (IResultWriter<Point>)mock(IResultWriter.class);
        IController controller = (IController)mock(IController.class);
        IFrameReader frameReader = mock(IFrameReader.class);

        long ranCaptureAndProcess = 0L;
        try
        {
            Mat mat = Imgcodecs.imread(HSVPipelineTest.RepoPath + imagePath);
            doReturn(mat).when(frameReader).getCurrentFrame();

            IContourFilter<Point> filter = new LargestCenterFilter(0.0);
            IFramePipeline pipeline = new HSVPipeline<Point>(pointWriter, null, null, new HSVFilter(VisionConstants.EXAMPLE_VISIONTARGET_HSV_FILTER_LOW, VisionConstants.EXAMPLE_VISIONTARGET_HSV_FILTER_HIGH), filter);

            SimpleVisionSystem vs = new SimpleVisionSystem(frameReader, controller, pipeline, null, 0, 0);
            ranCaptureAndProcess = vs.captureAndProcess();

            verify(pointWriter).write(eq(new Point(x, y)));
            verify(pointWriter).outputDebugFrame(anyObject());
            verify(frameReader).getCurrentFrame();

            verifyNoMoreInteractions(pointWriter);
            verifyNoMoreInteractions(frameReader);
        }
        catch (Exception ex)
        {
            fail(ex.toString());
        }

        assertNotEquals(0L, ranCaptureAndProcess);
    }
}