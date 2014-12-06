import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

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
    private JButton saveBtn = new JButton("Save");
    
    private JButton testBtn = new JButton("Test");
    private ArrayList<Double> list = new ArrayList<>();
    private int counter = 0;
    
    private String algoUsed = "";

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
        saveBtn.addActionListener(this);
        
        testBtn.addActionListener(this);

        add(openCL);
        add(originalBtn);
        add(invertBtn);
        add(grayscaleBtn);
        add(embossBtn);
        add(blurBtn);
//        add(testBtn);
//        add(saveBtn);
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
        list.add(time);
        counter++;
        System.out.println("Total time taken: " + time + " ms");
        System.out.println();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String btnText = e.getActionCommand();
        double timeTaken = 0.0;
        if (btnText.equals(originalBtn.getText()))//Show original image
        {
            m.showOriginal();
            algoUsed = "";
        }
        if (btnText.equals(invertBtn.getText()))//Show inverted image
        {
            printHeader("Invert");
            if(useOpenCL) {
                timeTaken = m.openCL("invert", false);
                algoUsed = "invert_opencl";
            }
            else {
                timeTaken = m.invert();
                algoUsed = "invert_java";
            }
            printTotalTime(timeTaken);
            
        }
        if (btnText.equals(grayscaleBtn.getText()))//Show grayscale image
        {
            printHeader("Grayscale");
            if(useOpenCL) {
                timeTaken = m.openCL("gray_scale", false);
                algoUsed = "grayscale_opencl";
            }
            else {
                timeTaken = m.grayScale();
                algoUsed = "grayscale_java";
            }
            printTotalTime(timeTaken);
        }
        if (btnText.equals(embossBtn.getText()))//Show embossed image
        {
            printHeader("Emboss");
            if(useOpenCL) {
                timeTaken = m.openCL("emboss", true);
                algoUsed = "emboss_opencl";
            }
            else {
                timeTaken = m.emboss();
                algoUsed = "emboss_java";
            }
            printTotalTime(timeTaken);
        }
        if(btnText.equals("Blur"))//Show blurred image
        {
            printHeader("Blur");
            if(useOpenCL){
                timeTaken = m.openCL("blur", true);
                algoUsed = "blur_opencl";
            }
            else {
                timeTaken = m.blur();
                algoUsed = "blur_java";
            }
            printTotalTime(timeTaken);
        }
        if(btnText.equals("Save"))//Save image to img/ folder
        {
            if(!algoUsed.equals(""))
                m.writeImage(algoUsed);
        }
        if(btnText.equals("Test"))//Run test on a button
        {
            list.clear();
            counter = 0;
            for(int i = 0; i < 100; i++)
            {
                grayscaleBtn.doClick();
                originalBtn.doClick();
            }
            double avg = 0;
            for(int i = 0; i < list.size(); i++)
            {
                avg += list.get(i);
            }
            
            System.out.println("Avg: " + avg / list.size() + " Count: " + counter);
        }
        m.repaint();
    }
}
