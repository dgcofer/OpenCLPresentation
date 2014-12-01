import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.lwjgl.LWJGLException;

/**
 * JPanel to hold all the action buttons.
 * 
 * @author derek
 *
 */
@SuppressWarnings("serial")
public class ButtonPanel extends JPanel implements ActionListener
{
    private OpenCLMain m;
    
    private JCheckBox openCL = new JCheckBox("OpenCL");
    private boolean useOpenCL = false;

    private JButton originalBtn = new JButton("Original");
    private JButton invertBtn = new JButton("Invert");
    private JButton grayscaleBtn = new JButton("Grayscale");
    private JButton embossBtn = new JButton("Emboss");
    private JButton blurBtn = new JButton("Blur");

    public ButtonPanel(OpenCLMain m)
    {
        this.m = m;

        openCL.setSelected(useOpenCL);
        openCL.addActionListener((e) -> { useOpenCL = !useOpenCL; });
        
        originalBtn.addActionListener(this);
        invertBtn.addActionListener(this);
        grayscaleBtn.addActionListener(this);
        embossBtn.addActionListener(this);
        blurBtn.addActionListener(this);

        add(openCL);
        add(originalBtn);
        add(invertBtn);
        add(grayscaleBtn);
        add(embossBtn);
        add(blurBtn);
    }
    
    private void printHeader(String name)
    {
        if(useOpenCL)
            System.out.println("\t" + name.toUpperCase() + " - OpenCL");
        else
            System.out.println("\t" + name.toUpperCase());
        
        System.out.println("=================================");
    }
    
    private void printTotalTime(double time)
    {
        System.out.println("Total time taken: " + time + " ms");
        System.out.println();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String btnText = e.getActionCommand();
        if (btnText.equals(originalBtn.getText()))
        {
            m.showOriginal();
        }
        if (btnText.equals(invertBtn.getText()))
        {
            printHeader("Invert");
            double timeTaken = 0.0;
            if(useOpenCL)
                timeTaken = m.openCL("invert", false);
            else
                timeTaken = m.invert();
            printTotalTime(timeTaken);
        }
        if (btnText.equals(grayscaleBtn.getText()))
        {
            printHeader("Grayscale");
            double timeTaken = 0.0;
            if(useOpenCL)
                timeTaken = m.openCL("gray_scale", false);
            else
                timeTaken = m.grayScale();
            printTotalTime(timeTaken);
        }
        if (btnText.equals(embossBtn.getText()))
        {
            printHeader("Emboss");
            double timeTaken = 0.0;
            if(useOpenCL)
                timeTaken = m.openCL("emboss", true);
            else
                timeTaken = m.emboss();
            printTotalTime(timeTaken);
        }
        if(btnText.equals("Blur"))
        {
            printHeader("Blur");
            double timeTaken = 0.0;
            if(useOpenCL){
                timeTaken = m.openCL("blur", true);
            }
            else
                timeTaken = m.blur();
            printTotalTime(timeTaken);
        }
        m.repaint();
    }
}
