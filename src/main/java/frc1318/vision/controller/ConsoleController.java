package frc1318.vision.controller;

import java.util.List;
import java.util.Scanner;

import frc1318.vision.IController;
import frc1318.vision.Logger;

public class ConsoleController implements IController
{
    private int mode;

    public ConsoleController()
    {
        this(1);
    }

    public ConsoleController(int initialMode)
    {
        this.mode = initialMode;
    }

    @Override
    public boolean open()
    {
        return true;
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean getStreamEnabled()
    {
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public int getProcessingMode()
    {
        return this.mode;
    }

    @Override
    public List<Integer> getDesiredTarget()
    {
        return null;
    }

    public void run()
    {
        try (Scanner console = new Scanner(System.in))
        {
            while (true)
            {
                String input = console.nextLine();
                if (input != null)
                {
                    input = input.trim();
                    if (input.equalsIgnoreCase("quit"))
                    {
                        return;
                    }

                    if (input.startsWith("mode:"))
                    {
                        try
                        {
                            this.mode = Integer.parseInt(input.substring("mode:".length()).trim());
                        }
                        catch (NumberFormatException nfe)
                        {
                            Logger.writeError(String.format("Unknown number after 'mode:' %s", input));
                            ConsoleController.printUsage();
                        }
                    }
                    else if (input.equalsIgnoreCase("?") || input.equalsIgnoreCase("help"))
                    {
                        ConsoleController.printUsage();
                    }
                }
            }
        }
    }

    private static void printUsage()
    {
        Logger.write("Usage:");
        Logger.write("'mode:##'       -- switch to mode '##'");
        Logger.write("'quit'          -- quits the loop");
        Logger.write("'?', 'help'     -- display usage");
    }
}
