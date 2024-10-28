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
    List<StateData> statesData;
    Double [] means;

    public ChartPanel()
    {
        statesData = new ArrayList<StateData>();
        means = new Double[4];

        setPreferredSize(new Dimension(300, 400));

        JPanel panel = new JPanel();

        this.add(panel);
    }

    public ChartPanel(List<StateData> data, Double[] means)
    {
        statesData = data;
        this.means = means;

        setPreferredSize(new Dimension(800, 400));

        JPanel panel = new JPanel();

        this.add(panel);

    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        String[] labels = {"Murder", "Assault", "Rape", "% Urban Pop."};

        int x = 300;
        int y = 180;

        double max = 0;
        for (StateData state : statesData)
        {
            if (state.assault() > max)
            {
                max = state.assault();
            }
        }

        for (int i = 0; i < 4; i++)
        {
            g.drawString(labels[i], x, y);

            //MAXIMUM BAR HEIGHT = 250
            double heightPercentage = means[i]/max;
            int height = (int) (heightPercentage*250);

            g.drawRect(x+3,y-height-15, 30, height);
            g.drawString(String.valueOf(means[i]), x+2, y-height-20);



            x += 50;
        }
    }
}

