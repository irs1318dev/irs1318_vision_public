package frc1318.vision;

import java.util.HashMap;
import java.util.Map;

import frc1318.opencv.Mat4;

public class FieldLayout
{
    //=========================================== 2024 AprilTag Location guide ==============================================
    //// | TAG                                 |  ID  |    X    |    Y    |   Z    | THETA |
    //// |-------------------------------------|------|---------|---------|--------|-------|
    //// | APRILTAG_BLUE_SOURCE_RIGHT_ID       |   1  |  268.07 |    6.26 |  53.38 | 120.0 |
    //// | APRILTAG_BLUE_SOURCE_LEFT_ID        |   2  |  311.59 |   31.37 |  53.38 | 120.0 |
    //// | APRILTAG_RED_SPEAKER_OFFCENTER_ID   |   3  |  327.12 |  192.75 |  57.13 | 180.0 |
    //// | APRILTAG_RED_SPEAKER_CENTER_ID      |   4  |  327.12 |  215.0  |  57.13 | 180.0 |
    //// | APRILTAG_RED_AMP_ID                 |   5  |  253.16 |  319.58 |  53.38 | 270.0 |
    //// | APRILTAG_BLUE_AMP_ID                |   6  | -253.16 |  319.58 |  53.38 | 270.0 |
    //// | APRILTAG_BLUE_SPEAKER_CENTER_ID     |   7  | -327.12 |  215.0  |  57.13 |   0.0 |
    //// | APRILTAG_BLUE_SPEAKER_OFFCENTER_ID  |   8  | -327.12 |  192.75 |  57.13 |   0.0 |
    //// | ARPILTAG_RED_SOURCE_RIGHT_ID        |   9  | -311.59 |   31.37 |  53.38 |  60.0 |
    //// | APRILTAG_RED_SOURCE_LEFT_ID         |  10  | -268.07 |    6.26 |  53.38 |  60.0 |
    //// | APRILTAG_RED_STAGE_LEFT_ID          |  11  |  143.0  |  142.77 |  52.0  | 300.0 |
    //// | APRILTAG_RED_STAGE_RIGHT_ID         |  12  |  143.0  |  173.68 |  52.0  |  60.0 |
    //// | APRILTAG_RED_CENTER_STAGE_ID        |  13  |  116.13 |  158.5  |  52.0  | 180.0 |
    //// | APRILTAG_BLUE_CENTER_STAGE_ID       |  14  | -116.13 |  158.5  |  52.0  |   0.0 |
    //// | APRILTAG_BLUE_STAGE_LEFT_ID         |  15  | -143.0  |  173.68 |  52.0  | 120.0 |
    //// | APRILTAG_BLUE_STAGE_RIGHT_ID        |  16  | -143.0  |  142.77 |  52.0  | 240.0 |
    //// 
    //// Conversion from FIRST's published values: (x - 325.615, y - ~3.42, z, rot)
    //// Also, we will add 180deg to each of the theta Yaw values because we have a 0 yaw when looking at an april tag that is facing us.
    //// (So we are effectively tracking the direction of the rear face of the tag.)
    public static Map<Integer, Mat4> AprilTagIdToAffineTransformationMap;

    static
    {
        FieldLayout.AprilTagIdToAffineTransformationMap = new HashMap<Integer, Mat4>(16);
        FieldLayout.AprilTagIdToAffineTransformationMap.put(1,  Mat4.createAffine(180.0 + 120.0, 0.0, 0.0,  268.07,   6.26, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(2,  Mat4.createAffine(180.0 + 120.0, 0.0, 0.0,  311.59,  31.37, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(3,  Mat4.createAffine(180.0 + 180.0, 0.0, 0.0,  327.12, 192.75, 57.13, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(4,  Mat4.createAffine(180.0 + 180.0, 0.0, 0.0,  327.12, 215.00, 57.13, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(5,  Mat4.createAffine(180.0 + 270.0, 0.0, 0.0,  253.16, 319.58, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(6,  Mat4.createAffine(180.0 + 270.0, 0.0, 0.0, -253.16, 319.58, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(7,  Mat4.createAffine(180.0 +   0.0, 0.0, 0.0, -327.12, 215.00, 57.13, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(8,  Mat4.createAffine(180.0 +   0.0, 0.0, 0.0, -327.12, 192.75, 57.13, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(9,  Mat4.createAffine(180.0 +  60.0, 0.0, 0.0, -311.59,  31.37, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(10, Mat4.createAffine(180.0 +  60.0, 0.0, 0.0, -268.07,   6.26, 53.38, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(11, Mat4.createAffine(180.0 + 300.0, 0.0, 0.0,  143.00, 142.77, 52.00, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(12, Mat4.createAffine(180.0 +  60.0, 0.0, 0.0,  143.00, 173.68, 52.00, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(13, Mat4.createAffine(180.0 + 180.0, 0.0, 0.0,  116.13, 158.50, 52.00, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(14, Mat4.createAffine(180.0 +   0.0, 0.0, 0.0, -116.13, 158.50, 52.00, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(15, Mat4.createAffine(180.0 + 120.0, 0.0, 0.0, -143.00, 173.68, 52.00, 1));
        FieldLayout.AprilTagIdToAffineTransformationMap.put(16, Mat4.createAffine(180.0 + 240.0, 0.0, 0.0, -143.00, 142.77, 52.00, 1));
    }
}
