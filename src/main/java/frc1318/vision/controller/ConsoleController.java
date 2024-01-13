package frc1318.vision.controller;

import java.util.Scanner;

import frc1318.vision.IController;

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
    public int getProcessingMode()
    {
        return this.mode;
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
                            System.err.println(String.format("Unknown number after 'mode:' %s", input));
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
        System.out.println("Usage:");
        System.out.println("'mode:##'       -- switch to mode '##'");
        System.out.println("'quit'          -- quits the loop");
        System.out.println("'?', 'help'     -- display usage");
    }
}
