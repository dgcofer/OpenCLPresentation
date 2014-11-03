import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        try {
            image = ImageIO.read(new File("img/tron_lambo.jpg"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        //Pixels in the form B, G, R, B, G, R,...
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        long start = System.currentTimeMillis();
        
//        invert();
        grayScale();
        
        long end = System.currentTimeMillis();
        System.out.println((end - start) + " ms");
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
    
    
    public static void main(String[] args)
    {
        Main m = new Main();
        JFrame frame = new JFrame("OpenCL Presentation");
        frame.setSize(SCREEN_DIM);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(m);
        frame.setVisible(true);
    }
}
