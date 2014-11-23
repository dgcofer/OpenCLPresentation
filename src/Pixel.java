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
    
    public static byte[] getByteArray(Pixel[] pixels)
    {
        byte[] b = new byte[pixels.length * 3];
        for(int i = 0; i < pixels.length; i++)
        {
            b[i * 3] = pixels[i].getBlue();
            b[(i * 3) + 1] = pixels[i].getGreen();
            b[(i * 3) + 2] = pixels[i].getRed();
        }
        
        return b;
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
