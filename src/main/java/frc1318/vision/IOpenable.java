package frc1318.vision;

public interface IOpenable
{
    /**
     * Opens any connections required by the object
     * @return true if the connections were successfully opened
     */
    public boolean open();

    /**
     * Attempts to close any connection required by the object
     */
    public void close();
}
