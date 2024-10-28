import javax.swing.*;
import java.awt.*;

public class DataViewer extends JFrame
{
    public DataViewer()
    {
        //OPEN THE FRAME
        this.setTitle("Data Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //TOP OF GUI
        JLabel statusLabel = new JLabel("Status: ", JLabel.CENTER);

        JLabel topLabel = new JLabel("US Violent Crime Data Viewer",10);
        //topLabel.setFont(new Font("Rockwell",Font.BOLD,18));


        JPanel top = new JPanel();
        top.add(topLabel);

        JPanel main = new JPanel();
        StatsPanel statsPanel = new StatsPanel();

        main.add(statsPanel);

        //ADD COMPONENTS TO THE GUI
        this.add(top, BorderLayout.NORTH);
        this.add(main, BorderLayout.CENTER);

        //top.setBackground(new Color(208, 255, 249));
        //main.setBackground(new Color(208, 255, 249));

        this.setSize(500,600);
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        DataViewer dataViewer = new DataViewer();
    }
}
