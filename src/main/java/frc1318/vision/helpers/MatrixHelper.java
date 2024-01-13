package frc1318.vision.helpers;

import org.opencv.core.Mat;

public class MatrixHelper
{
    /**
     * Convert the provided matrix into a string
     * @param matrix to print
     * @return string representing the matrix
     */
    public static String toString(Mat matrix)
    {
        if (matrix == null)
        {
            return null;
        }

        int totalRows = matrix.rows();
        int totalCols = matrix.cols();
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(totalRows);
        builder.append('x');
        builder.append(totalCols);
        for (int row = 0; row < matrix.rows(); row++)
        {
            builder.append("\n");

            builder.append('|');
            for (int col = 0; col < matrix.cols(); col++)
            {
                if (col > 0)
                {
                    builder.append(" ");
                }

                double[] values = matrix.get(row, col);
                builder.append("(");
                if (values != null && values.length > 0)
                {
                    builder.append(values[0]);
                    for (int i = 1; i < values.length; i++)
                    {
                        builder.append(", ");
                        builder.append(values[i]);
                    }
                }

                builder.append(")");
            }

            builder.append("|");
        }

        return builder.toString();
    }
}
