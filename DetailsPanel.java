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

public class DetailsPanel extends JPanel
{
    JLabel[] labels;
    String[] labelStrings = {"Arrests Per 10,000 Residents: "};

    JLabel[] values;

    //DEFAULT CONSTRUCTOR
    public DetailsPanel()
    {
        labels = new JLabel[1];
        values = new JLabel[8];

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

        this.setLayout(new GridLayout(1, 5));

        //FIRST ROW: ARRESTS
        this.add(labels[0]);
        this.add(values[0]); //STATE MURDER RATE
        this.add(values[1]); //STATE ASSAULT RATE
        this.add(values[2]); //STATE RAPE RATE
        this.add(values[3]); //STATE URBAN POPULATION PERCENTAGE

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setValue(String value, int index)
    {
        values[index].setText(value);
    }
}
