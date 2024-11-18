import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartPanel extends JPanel
{
    final int PANEL_WIDTH = 400;
    final int PANEL_HEIGHT = 400;
    final int VARIABLES = 4;

    List<StateData> statesData;
    Double [] means;

    public ChartPanel()
    {
        //INITIALIZE DATA BECAUSE NULL IS BAD USUALLY.
        statesData = new ArrayList<StateData>();
        means = new Double[VARIABLES];

        //CREATE THE PANEL.
        setBackground(new Color(40, 145, 255));
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    }

    public ChartPanel(List<StateData> data, Double[] means)
    {
        //STORE DATA TO USE FOR CHART MAKING.
        statesData = data;
        this.means = means;

        //CREATE THE PANEL.
        setBackground(new Color(40, 145, 255));
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    }

    @Override
    //DRAWING GRAPHICS
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        setBackground(new Color(40, 145, 255));

        //WHAT IT SAYS ON THE TIN: LABELS FOR THE CHART.
        String[] labels = {"Murder", "Assault", "Rape", "% Urban Pop."};

        //INITIALIZE OUR X AND Y COORDINATES FOR THE CHART
        int x = 300;
        int y = 180;

        //FIND THE MAXIMUM VALUE FOR THE STATISTICAL VALUES
        double max = 0;
        for (StateData state : statesData)
        {
            if (state.getAssault() > max)
            {
                max = state.getAssault();
            }
        }

        //FOR EACH VARIABLE, CREATE A BAR
        for (int i = 0; i < 4; i++)
        {
            //LABEL THE BAR
            g.setColor(Color.BLACK);
            g.drawString(labels[i], x, y);

            //MAXIMUM BAR HEIGHT = 250
            double heightPercentage = means[i]/max;
            int height = (int) (heightPercentage*250);

            //RECTANGLE BEGINS AT UPPER LEFT CORNER, START ABOVE THE LABEL AT THE HEIGHT OF THE BAR
            //WIDTH STAYS AT 30.
            g.setColor(Color.WHITE);
            g.fill3DRect(x+3,y-height-15, 30, height, true);

            g.setColor(Color.BLACK);
            //DISPLAY THE VALUE OF THE BAR ABOVE THE BAR.
            g.drawString(String.valueOf(means[i]), x+2, y-height-20);

            //CREATE DISTANCE BETWEEN THE BARS.
            x += 50;
        }
    }
}

