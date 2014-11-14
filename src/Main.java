import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;


@SuppressWarnings("serial")
public class Main extends Canvas
{
    //Gets screen resolution
    private static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = SCREEN_DIM.width;
    private static final int HEIGHT = SCREEN_DIM.height;
    
    private BufferedImage image;
    private byte[] rgbs;
    private Pixel[] pixels;
    
    public Main()
    {
        showOriginal();
    }
    
    /**
     * Paints the image on the JFrame.
     */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();
    }
    
    /**
     * Inverts the pixels of the image
     */
    public void invert()
    {
        for(int i = 0; i < rgbs.length; i++)
        {
            int pix = Byte.toUnsignedInt(rgbs[i]);
            rgbs[i] = (byte) (255 - pix);
        }
    }
    
    /** TODO
     * This is freezing the UI needs to be reimplemented
     */
    public void showOriginal()
    {
        try {
            image = ImageIO.read(new File("img/tron_lambo.jpg"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //Pixels in the form B, G, R, B, G, R,...
        rgbs = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        pixels = new Pixel[rgbs.length / 3];
        for(int i = 0; i < pixels.length; i++)
        {
            byte blue = rgbs[i * 3];
            byte green = rgbs[(i * 3) + 1];
            byte red = rgbs[(i * 3) + 2];
            pixels[i] = new Pixel(red, green, blue);
            
        }
        int iW = image.getWidth();
        int iH = image.getHeight();
        System.out.println("Image Width: " + iW);
        System.out.println("Image Height: " + iH);
        System.out.println(pixels.length);
    }
    
    /**
     * Converts the image to grayscale.
     */
    public void grayScale()
    {
        for(int i = 0; i < rgbs.length; i+=3)
        {
            byte blue = rgbs[i];
            byte green = rgbs[i + 1];
            byte red = rgbs[i + 2];
            
            byte avg = (byte) (blue * 0.114 + green * 0.587 + red * 0.299); //luminosity
//            byte avg = (byte) ((blue + green + red) / 3); //average
            
            rgbs[i] = avg;
            rgbs[i + 1] = avg;
            rgbs[i + 2] = avg;
        }
    }
    
    /**
     * Embosses the picture
     */
    public void emboss()
    {
        for(int i = 0; i < pixels.length; i++)
        {
            int blueIndex = i * 3;
            int greenIndex = (i * 3) + 1;
            int redIndex = (i * 3) + 2;
            int red = Byte.toUnsignedInt(pixels[i].getRed());
            int green = Byte.toUnsignedInt(pixels[i].getGreen());
            int blue = Byte.toUnsignedInt(pixels[i].getBlue());
            int redDiff = 0, greenDiff = 0, blueDiff = 0;
            
            int v = 0;
            int imageWidth = image.getWidth();
            
            if(i < imageWidth || i % imageWidth == 0)
                v = 128;
            else
            {
                redDiff = red - pixels[i - imageWidth - 1].getRed();
                greenDiff = green - pixels[i - imageWidth - 1].getGreen();
                blueDiff = blue - pixels[i - imageWidth - 1].getBlue();
                
                int maxDiff = 0;
                if(Math.abs(redDiff) >= Math.abs(greenDiff))
                    maxDiff = redDiff;
                else if(Math.abs(greenDiff) >= Math.abs(blueDiff))
                    maxDiff = greenDiff;
                else
                    maxDiff = blueDiff;
                
                v = 128 + maxDiff;
                if(v < 0)
                    v = 0;
                if(v > 255)
                    v = 255;
            }
            
            rgbs[blueIndex] = (byte) v;
            rgbs[greenIndex] = (byte) v;
            rgbs[redIndex] = (byte) v;
        }
    }
    
    public static void main(String[] args)
    {
        Main m = new Main();
        JFrame frame = new JFrame("OpenCL Presentation");
        frame.setSize(SCREEN_DIM);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(m, BorderLayout.CENTER);
        frame.add(new ButtonPanel(m), BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
