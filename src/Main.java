import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

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
    private byte[] pixels;
    
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
        for(int i = 0; i < pixels.length; i++)
        {
            int pix = Byte.toUnsignedInt(pixels[i]);
            pixels[i] = (byte) (255 - pix);
        }
    }
    
    public void showOriginal()
    {
        try {
            image = ImageIO.read(new File("img/tron_lambo.jpg"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //Pixels in the form B, G, R, B, G, R,...
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }
    
    /**
     * Converts the image to grayscale.
     */
    public void grayScale()
    {
        for(int i = 0; i < pixels.length; i+=3)
        {
            byte blue = pixels[i];
            byte green = pixels[i + 1];
            byte red = pixels[i + 2];
            
            byte avg = (byte) (blue * 0.114 + green * 0.587 + red * 0.299); //luminosity
//            byte avg = (byte) ((blue + green + red) / 3); //average
            
            pixels[i] = avg;
            pixels[i + 1] = avg;
            pixels[i + 2] = avg;
        }
    }
    
    public void emboss()
    {
        for(int i = image.getWidth(); i < pixels.length; i+=3)
        {
            int blueIndex = i;
            int greenIndex = i + 1;
            int redIndex = i + 2;
            int blue = Byte.toUnsignedInt(pixels[blueIndex]);
            int green = Byte.toUnsignedInt(pixels[greenIndex]);
            int red = Byte.toUnsignedInt(pixels[redIndex]);
            int redDiff = 0;
            int greenDiff = 0;
            int blueDiff = 0;
            
            int v = 0;
            
            if(i <= image.getWidth() * 3)
            {
                v = 128;
            }
            else
            {
                redDiff = red - pixels[redIndex - image.getWidth() * 3 - 1];
                greenDiff = green - pixels[greenIndex - image.getWidth() * 3 - 1];
                blueDiff = blue - pixels[blueIndex - image.getWidth() * 3 - 1];
            }
            
            int maxDiff = Math.max(redDiff, Math.max(greenDiff, blueDiff));
            
            v = 128 + maxDiff;
            
            if(v < 0)
                v = 0;
            if(v > 255)
                v = 255;
            
            pixels[blueIndex] = (byte) v;
            pixels[greenIndex] = (byte) v;
            pixels[redIndex] = (byte) v;
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
