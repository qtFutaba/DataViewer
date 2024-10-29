import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class StatsPanel extends JPanel
{
    JLabel[] labels;
    String[] labelStrings = {"       ","Murder", "Assault", "Rape", "% Of Urban Population", "Mean", "Median", "Standard Deviation"};

    JLabel[] values;

    //DEFAULT CONSTRUCTOR
    public StatsPanel()
    {
        labels = new JLabel[8];
        values = new JLabel[12];

        //INITIALIZE THE LABELS.
        for (int i = 0; i < labels.length; i++)
        {
            labels[i] = new JLabel(labelStrings[i]);
        }

        //INITIALIZE THE VALUES.
        for (int i = 0; i < values.length; i++)
        {
            values[i] = new JLabel("0");
        }


        this.setLayout(new GridLayout(4, 5));

        //FIRST ROW: CRIME LABELS

        this.add(labels[0]);
        this.add(labels[1]);
        this.add(labels[2]);
        this.add(labels[3]);
        this.add(labels[4]);

        //SECOND ROW: MEAN
        this.add(labels[5]); //-- MEAN LABEL --
        this.add(values[0]); //MURDER MEAN
        this.add(values[1]); //ASSAULT MEAN
        this.add(values[2]); //RAPE MEAN
        this.add(values[3]); //URBAN POP MEAN

        //THIRD ROW: MEDIAN
        this.add(labels[6]); //-- MEDIAN LABEL --
        this.add(values[4]); //MURDER MEDIAN
        this.add(values[5]); //ASSAULT MEDIAN
        this.add(values[6]); //RAPE MEDIAN
        this.add(values[7]); //URBAN POP. MEDIAN

        //FOURTH ROW: STANDARD DEVIATION
        this.add(labels[7]); //-- STANDARD DEVIATION LABEL --
        this.add(values[8]); //MURDER STANDARD DEVIATION
        this.add(values[9]); //ASSAULT STANDARD DEVIATION
        this.add(values[10]); //RAPE STANDARD DEVIATION
        this.add(values[11]); //URBAN POP. STANDARD DEVIATION

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //COLOR :)
        this.setBackground(Color.WHITE);
    }

    public void setValue(String value, int index)
    {
        values[index].setText(value);
    }
}
