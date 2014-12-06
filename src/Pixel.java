/**
 * Represents a RGB pixel.
 * @author derek
 *
 */
public class Pixel
{
    private int pixel;
    private byte red;
    private byte green;
    private byte blue;
    
    public Pixel(byte red, byte green, byte blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.pixel = (0xFF << 24) + (red << 16) + (green << 8) + blue;
    }

    public byte getRed()
    {
        return red;
    }

    public byte getGreen()
    {
        return green;
    }

    public byte getBlue()
    {
        return blue;
    }
    
    public int getValue()
    {
        return pixel;
    }
    
    public String toString()
    {
        return "0x" + Integer.toHexString(pixel);
    }
    
    public static int[] getRGBArray(Pixel[] pixels)
    {
        int[] buf = new int[pixels.length];
        for(int i = 0; i < pixels.length; i++)
        {
            buf[i] = pixels[i].getValue();
        }
        return buf;
    }
}
