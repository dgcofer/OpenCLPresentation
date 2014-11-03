import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonPanel extends JPanel implements ActionListener
{
    private Main m;
    
    private JButton originalBtn;
    private JButton invertBtn;
    private JButton grayscaleBtn;
    
    public ButtonPanel(Main m)
    {
        this.m = m;
        
        originalBtn = new JButton("Original");
        invertBtn = new JButton("Invert");
        grayscaleBtn = new JButton("Grayscale");
        
        originalBtn.addActionListener(this);
        invertBtn.addActionListener(this);
        grayscaleBtn.addActionListener(this);
        
        add(originalBtn);
        add(invertBtn);
        add(grayscaleBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String btnText = e.getActionCommand();
        if(btnText.equals(originalBtn.getText()))
        {
            System.out.println("Original button clicked");
            m.original();
            m.repaint();
        }
        if(btnText.equals(invertBtn.getText()))
        {
            long start = System.currentTimeMillis();
            m.invert();
            long end = System.currentTimeMillis();
            System.out.println((end - start) + " ms");
            m.repaint();
        }
        if(btnText.equals(grayscaleBtn.getText()))
        {
            long start = System.currentTimeMillis();
            m.grayScale();
            long end = System.currentTimeMillis();
            System.out.println((end - start) + " ms");
            m.repaint();
        }
    }
}
