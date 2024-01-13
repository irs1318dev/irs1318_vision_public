package frc1318.vision.writer;

import java.awt.Container;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

import frc1318.vision.IResultWriter;

public class ImageDrawer<T> implements IResultWriter<T>
{
    private JFrame frame;
    private ImageIcon imageIcon;
    private JLabel label;

    public ImageDrawer()
    {
    }

    @Override
    public boolean open()
    {
        if (this.frame == null)
        {
            this.frame = new JFrame(); 
            this.imageIcon = new ImageIcon();
            this.label = new JLabel(this.imageIcon);
            Container contentPane = this.frame.getContentPane();
            contentPane.add(this.label);
            contentPane.setSize(1280, 720);
            this.frame.setVisible(true);
            this.frame.pack();
        }

        return true;
    }

    @Override
    public void close()
    {
        if (this.frame != null)
        {
            this.frame.setVisible(false);
            this.frame.dispose();
            this.frame = null;
        }
    }

    @Override
    public void write(T result, Mat sourceFrame)
    {
        if (this.frame != null)
        {
            Image resultImage = HighGui.toBufferedImage(sourceFrame);

            this.imageIcon.setImage(resultImage);
            this.label.updateUI();
            this.frame.pack();
        }
    }

    @Override
    public void write(T result)
    {
        if (result != null)
        {
            throw new RuntimeException("don't expect to see write(T)");
        }
    }

    @Override
    public void outputDebugFrame(Mat frame)
    {
    }
}
