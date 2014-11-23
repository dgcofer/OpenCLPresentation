import static org.lwjgl.opencl.CL10.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.Util;

@SuppressWarnings("serial")
public class OpenCLMain extends Canvas
{
    // Gets screen resolution
    private static final Dimension SCREEN_DIM = Toolkit.getDefaultToolkit().getScreenSize();
    private static final int WIDTH = SCREEN_DIM.width;
    private static final int HEIGHT = SCREEN_DIM.height;

    private static final double MILLION = 1000000.0;

    private BufferedImage image;
    private byte[] rgbs;
    private Pixel[] pixels;

    // OpenCL fields
    private CLPlatform platform;
    private List<CLDevice> devices;
    private CLContext context;
    private CLCommandQueue queue;
    private CLProgram program;

    public OpenCLMain()
    {
        try
        {
            image = ImageIO.read(new File("img/ramen_pic.jpg"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        // Pixels in the form B, G, R, B, G, R,...
        rgbs = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        pixels = new Pixel[rgbs.length / 3];
        for (int i = 0; i < pixels.length; i++)
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
        System.out.println("Total Pixels: " + pixels.length);
    }

    /**
     * Builds the OpenCL program.
     */
    private void buildProgram()
    {
        try
        {
            CL.create();
            platform = CLPlatform.getPlatforms().get(0);
            devices = platform.getDevices(CL_DEVICE_TYPE_GPU);
            context = CLContext.create(platform, devices, null, null, null);
            queue = clCreateCommandQueue(context, devices.get(0), CL_QUEUE_PROFILING_ENABLE, null);

            String source = UtilCL.getResourceAsString("cl/kernels.cl");

            program = clCreateProgramWithSource(context, source, null);
//            Util.checkCLError(clBuildProgram(program, devices.get(0), "", null));

            // Uncomment to DEBUG
             clBuildProgram(program, devices.get(0), "", null);
             System.out.println(program.getBuildInfoString(devices.get(0), CL_PROGRAM_BUILD_LOG));

            System.out.println("OpenCL program build successful\n");
        } catch (LWJGLException | IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Cleans up OpenCL resources
     */
    private void cleanUp()
    {
        clReleaseProgram(program);
        clReleaseCommandQueue(queue);
        clReleaseContext(context);
        CL.destroy();
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
     * Converts to the original image
     */
    public void showOriginal()
    {
        for (int i = 0; i < pixels.length; i++)
        {
            int blueIndex = i * 3;
            int greenIndex = (i * 3) + 1;
            int redIndex = (i * 3) + 2;

            rgbs[blueIndex] = pixels[i].getBlue();
            rgbs[greenIndex] = pixels[i].getGreen();
            rgbs[redIndex] = pixels[i].getRed();
        }
    }

    /**
     * Inverts the pixels of the image
     */
    public double invert()
    {
        long start = System.nanoTime();
        for (int i = 0; i < pixels.length; i++)
        {
            int blueIndex = i * 3;
            int greenIndex = (i * 3) + 1;
            int redIndex = (i * 3) + 2;
            int blue = Byte.toUnsignedInt(pixels[i].getBlue());
            int green = Byte.toUnsignedInt(pixels[i].getGreen());
            int red = Byte.toUnsignedInt(pixels[i].getRed());
            rgbs[blueIndex] = (byte) (255 - blue);
            rgbs[greenIndex] = (byte) (255 - green);
            rgbs[redIndex] = (byte) (255 - red);
        }
        long end = System.nanoTime();
        return (end - start) / MILLION;
    }

    /**
     * Converts the image to grayscale.
     */
    public double grayScale()
    {
        long start = System.nanoTime();
        for (int i = 0; i < pixels.length; i++)
        {
            int blueIndex = i * 3;
            int greenIndex = (i * 3) + 1;
            int redIndex = (i * 3) + 2;
            byte blue = pixels[i].getBlue();
            byte green = pixels[i].getGreen();
            byte red = pixels[i].getRed();

            byte avg = (byte) (blue * 0.114 + green * 0.587 + red * 0.299); // luminosity
            // byte avg = (byte) ((blue + green + red) / 3); //average

            rgbs[blueIndex] = avg;
            rgbs[greenIndex] = avg;
            rgbs[redIndex] = avg;
        }
        long end = System.nanoTime();
        return (end - start) / MILLION;
    }

    /**
     * Embosses the picture
     */
    public double emboss()
    {
        long start = System.nanoTime();
        for (int i = 0; i < pixels.length; i++)
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

            // Check if pixel on first row or first column
            if (i < imageWidth || i % imageWidth == 0)
                v = 128;
            else
            {
                redDiff = red - pixels[i - imageWidth - 1].getRed();
                greenDiff = green - pixels[i - imageWidth - 1].getGreen();
                blueDiff = blue - pixels[i - imageWidth - 1].getBlue();

                int maxDiff = 0;
                if (Math.abs(redDiff) >= Math.abs(greenDiff))
                    maxDiff = redDiff;
                else if (Math.abs(greenDiff) >= Math.abs(blueDiff))
                    maxDiff = greenDiff;
                else
                    maxDiff = blueDiff;

                v = 128 + maxDiff;
                if (v < 0)
                    v = 0;
                if (v > 255)
                    v = 255;
            }

            rgbs[blueIndex] = (byte) v;
            rgbs[greenIndex] = (byte) v;
            rgbs[redIndex] = (byte) v;
        }
        long end = System.nanoTime();
        return (end - start) / MILLION;
    }
    
    /**
     * Executes an OpenCL kernel.
     * 
     * @param kernelName
     *            name of the kernel to execute
     * @return time take to execute and read from a kernel
     */
    public double openCL(String kernelName)
    {
        int[] rgbArray = Pixel.getRGBArray(pixels);
        IntBuffer rgbBuffer = UtilCL.toIntBuffer(rgbArray);
        IntBuffer ansBuffer = BufferUtils.createIntBuffer(rgbArray.length);

        CLMem bufMem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, rgbBuffer, null);
        clEnqueueWriteBuffer(queue, bufMem, 1, 0, rgbBuffer, null, null);
        CLMem ansMem = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_COPY_HOST_PTR, ansBuffer, null);
        clFinish(queue);

        CLKernel kernel = clCreateKernel(program, kernelName, null);

        PointerBuffer kernel1DGlobalWorkSize = BufferUtils.createPointerBuffer(1);
        kernel1DGlobalWorkSize.put(0, rgbBuffer.capacity());
        kernel1DGlobalWorkSize.rewind();

        kernel.setArg(0, bufMem);
        kernel.setArg(1, ansMem);
        if(kernelName.equals("emboss"))//Need to pass width for emboss kernel to work
            kernel.setArg(2, image.getWidth());
        
        PointerBuffer eventPointer = BufferUtils.createPointerBuffer(1);

        long start = System.nanoTime();
        clEnqueueNDRangeKernel(queue, kernel, 1, null, kernel1DGlobalWorkSize, null, null, eventPointer);

        // Execution time profiling
        CLEvent event = queue.getCLEvent(eventPointer.get(0));
        clFinish(queue);
        clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_START, null, eventPointer);
        clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_END, null, eventPointer);
        long exeStart = event.getProfilingInfoLong(CL_PROFILING_COMMAND_START);
        long exeEnd = event.getProfilingInfoLong(CL_PROFILING_COMMAND_END);
        System.out.println("OpenCL execution time: " + (exeEnd - exeStart) / MILLION + " ms");

        clEnqueueReadBuffer(queue, ansMem, 1, 0, ansBuffer, null, null);
        
        clFinish(queue);

        writeBufferToImage(ansBuffer);
        
        long end = System.nanoTime();

        clReleaseEvent(event);
        clReleaseKernel(kernel);
        clReleaseMemObject(bufMem);
        clReleaseMemObject(ansMem);

        return (end - start) / MILLION;
    }

    /**
     * Writes and IntBuffer to the byte array of the image
     * @param buff
     */
    private void writeBufferToImage(IntBuffer buff)
    {
        for (int i = 0; i < buff.capacity(); i++)
        {
            int pix = buff.get(i);
            byte red = (byte) ((pix >> 16) & 0xFF);
            byte green = (byte) ((pix >> 8) & 0xFF);
            byte blue = (byte) ((pix) & 0xFF);

            int blueIndex = i * 3;
            int greenIndex = (i * 3) + 1;
            int redIndex = (i * 3) + 2;

            rgbs[blueIndex] = blue;
            rgbs[greenIndex] = green;
            rgbs[redIndex] = red;
        }
    }

    public static void main(String[] args)
    {
        OpenCLMain m = new OpenCLMain();
        m.buildProgram();

        Thread t = new Thread(() -> m.cleanUp());
        Runtime.getRuntime().addShutdownHook(t);

        JFrame frame = new JFrame("OpenCL Presentation");
        frame.setSize(SCREEN_DIM);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(m, BorderLayout.CENTER);
        frame.add(new ButtonPanel(m), BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
