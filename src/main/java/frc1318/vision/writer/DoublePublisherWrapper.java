package frc1318.vision.writer;

import edu.wpi.first.networktables.DoublePublisher;
import frc1318.vision.VisionConstants;

class DoublePublisherWrapper
{
    private final DoublePublisher wrappedObject;
    private final double defaultValue;

    private double lastValue;
    private int sameValueCount;

    DoublePublisherWrapper(DoublePublisher wrappedObject)
    {
        this(wrappedObject, VisionConstants.MAGIC_NULL_VALUE);
    }

    DoublePublisherWrapper(DoublePublisher wrappedObject, double defaultValue)
    {
        this.wrappedObject = wrappedObject;
        this.defaultValue = defaultValue;
        this.lastValue = this.defaultValue;
        this.sameValueCount = 0;
    }

    public void set(double value)
    {
        // don't keep repeatedly publishing the default value to the network table
        // just re-publish it every once in a while (every SAME_VALUE_PUBLISHING_INTERVAL)
        if (value != this.defaultValue ||
            this.lastValue != this.defaultValue)
        {
            this.wrappedObject.set(value);
            this.sameValueCount = 0;
        }
        else
        {
            if ((this.sameValueCount % VisionConstants.SAME_VALUE_PUBLISHING_INTERVAL) == 0)
            {
                this.wrappedObject.set(value);
            }

            this.sameValueCount++;
        }

        this.lastValue = value;
    }
}
