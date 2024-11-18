import javax.swing.*;
import java.awt.*;

public class DataViewer extends JFrame
{
    final int PANEL_WIDTH = 900;
    final int PANEL_HEIGHT = 700;

    public DataViewer()
    {
        //OPEN THE FRAME
        this.setTitle("Data Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TOP OF GUI (HEADER)
        JLabel topLabel = new JLabel("U.S. Violent Crime Data Viewer",10);
        topLabel.setFont(new Font("Stencil",Font.BOLD,18));

        JPanel top = new JPanel();
        top.add(topLabel);

        //MAIN PART OF THE GUI
        JPanel main = new JPanel();
        TablePanel tablePanel = new TablePanel();



        main.add(tablePanel);

        //ADD COMPONENTS TO THE GUI
        this.add(top, BorderLayout.NORTH);
        this.add(main, BorderLayout.CENTER);

        //COLOR :)
        top.setBackground(new Color(40, 145, 255));
        main.setBackground(new Color(40, 145, 255));
        this.setBackground(new Color(40, 145, 255));

        //FINALIZE SIZE AND DISPLAY
        this.setSize(PANEL_WIDTH,PANEL_HEIGHT);
        this.setVisible(true);
    }

    //START THE PROGRAM
    public static void main(String[] args)
    {
        DataViewer dataViewer = new DataViewer();
    }
}
