package frc1318.vision.leds;

import java.awt.Container;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import frc1318.ws2812.Color;

public class UILedStrip implements ILedStrip
{
    private static final boolean DEBUG = false;
    private static final int LED_WIDTH = 5;
    private static final int LED_HEIGHT = 5;

    private final int ledCount;

    private JFrame frame;
    private BufferedImage image;
    private ImageIcon imageIcon;
    private JLabel label;

    private Color[] colors;

    public UILedStrip(int ledCount)
    {
        this.ledCount = ledCount;

        this.colors = new Color[ledCount];
    }

    /**
     * Open the LED strip for writing
     * @return true if the strip was opened, otherwise false
     */
    public boolean open()
    {
        if (this.frame == null)
        {
            this.frame = new JFrame();

            Container contentPane = this.frame.getContentPane();
            contentPane.setSize(this.ledCount * LED_WIDTH, LED_HEIGHT);

            this.image = new BufferedImage(this.ledCount * LED_WIDTH, LED_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
            this.paintImage();
            this.imageIcon = new ImageIcon(this.image);
            this.label = new JLabel(this.imageIcon);
            this.label.setSize(this.ledCount * LED_WIDTH, LED_HEIGHT);

            contentPane.add(this.label);
            contentPane.setSize(this.ledCount * LED_WIDTH, LED_HEIGHT);
            this.frame.setVisible(true);
            this.frame.pack();
        }

        return true;
    }

    /**
     * Release the resources for using the LED strip
     */
    @Override
    public void release()
    {
        if (this.frame != null)
        {
            this.frame.setVisible(false);
            this.frame.dispose();
            this.frame = null;
        }
    }

    /**
     * Clear the specified range of LEDs
     * @param startIndex the index of the first LED to clear
     * @param count the number of LEDs to clear
     */
    public void blank(int startIndex, int count)
    {
        if (DEBUG)
        {
            System.out.println("blank(" + startIndex + ", " + count + ")");
        }

        for (int i = startIndex; i < startIndex + count; i++)
        {
            this.colors[i] = Colors.Black;
        }
    }

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param color the color to set the LED to
     */
    public void setColor(int index, Color color)
    {
        if (DEBUG)
        {
            System.out.println("setColor(" + index + ", " + color + ")");
        }

        this.colors[index] = color;
    }

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param color the color to set the LEDs to
     */
    public void setColor(int startIndex, int count, Color color)
    {
        if (DEBUG)
        {
            System.out.println("setColor(" + startIndex + ", " + count + ", " + color + ")");
        }

        for (int i = startIndex; i < startIndex + count; i++)
        {
            this.colors[i] = color;
        }
    }

    /**
     * Set the color of the specified LED
     * @param index the index of the LED to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    public void setColor(int index, byte r, byte g, byte b)
    {
        if (DEBUG)
        {
            System.out.println("setColor(" + index + ", " + r + ", " + g + ", " + b + ")");
        }

        this.colors[index] = new Color(r, g, b);
    }

    /**
     * Set the color of the specified range of LEDs
     * @param startIndex the index of the first LED to set
     * @param count the number of LEDs to set
     * @param r the red component of the color
     * @param g the green component of the color
     * @param b the blue component of the color
     */
    public void setColor(int startIndex, int count, byte r, byte g, byte b)
    {
        if (DEBUG)
        {
            System.out.println("setColor(" + startIndex + ", " + count + ", " + r + ", " + g + ", " + b + ")");
        }

        for (int i = startIndex; i < startIndex + count; i++)
        {
            this.colors[i] = new Color(r, g, b);
        }
    }

    /**
     * Render the LEDs to the strip
     */
    public void render()
    {
        this.paintImage();

        this.imageIcon.setImage(this.image);
        this.label.updateUI();
        this.frame.pack();
    }

    /**
     * Paint the stored colors to the buffered image for rendering
     */
    private void paintImage()
    {
        for (int i = 0; i < this.ledCount; i++)
        {
            int colorInt = 0;

            Color color = this.colors[i];
            if (color != null)
            {
                colorInt = color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
            }

            int xOffset = LED_WIDTH * i;
            for (int w = 0; w < LED_WIDTH; w++)
            {
                for (int h = 0; h < LED_HEIGHT; h++)
                {
                    this.image.setRGB(xOffset + w, h, colorInt);
                }
            }
        }
    }
}
