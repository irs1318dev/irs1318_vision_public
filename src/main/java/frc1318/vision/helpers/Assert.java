package frc1318.vision.helpers;

public class Assert
{
    public static void IsNotNull(Object value, String name)
    {
        if (value == null)
        {
            throw new RuntimeException("Expected " + name + " to be non-null!");
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

            throw new RuntimeException(message);
        }
    } 
}
