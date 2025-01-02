package frc1318.vision.helpers;

import frc1318.vision.Logger;

public class Assert
{
    public static void IsNotNull(Object value, String name)
    {
        if (value == null)
        {
            String errorMessage = "Expected " + name + " to be non-null!";
            Logger.writeError(errorMessage);

            throw new RuntimeException(errorMessage);
        }
    }

    public static <T> void Equals(T first, T second)
    {
        Assert.Equals(first, second, null);
    }

    public static <T> void Equals(T first, T second, String message)
    {
        if (first != second &&
            !first.equals(second))
        {
            if (message == null)
            {
                message = String.format("expected %s to equal %s", first == null ? "null" : first.toString(), second == null ? "null" : second.toString());
            }

            Logger.writeError(message);

            throw new RuntimeException(message);
        }
    } 
}
