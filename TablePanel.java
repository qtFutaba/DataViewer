import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class TablePanel extends JPanel //implements ListSelectionListener, ActionListener, ItemListener
{
    List<List<String>> crimeData;
    DefaultListModel stateList;
    JList stateListDisplay;

    JPanel sortPanel;
    JPanel filterPanel;
    JPanel listPanel;

    String sortType[] = {"Murder", "Assault", "Rape", "% Of Urban Pop."};
    String orders[] = {"Ascending", "Descending"};
    String greaterOrLesser[] = {"<", ">"};

    JComboBox sortOptionDropDown1;
    JComboBox sortOptionDropDown2;
    JComboBox orderDropDown;
    JComboBox greaterthanDropDown;
    JSpinner filterNumberSpinner;

    JCheckBox filterList;
    JCheckBox sortList;

    StatsPanel statsPanel;
    ChartPanel chartPanel;
    DetailsPanel detailsPanelPanel;

    //DEFAULT CONSTRUCTOR
    public TablePanel()
    {
        //-----------------------------------------------------------------------------
        //SORTING CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));

        JPanel sortLine1 = new JPanel();
        sortList = new JCheckBox("Sort");
        sortLine1.add(sortList);

        JPanel sortLine2 = new JPanel();
        sortOptionDropDown1 = new JComboBox(sortType);
        sortLine2.add(sortOptionDropDown1);

        JPanel sortLine3 = new JPanel();
        orderDropDown = new JComboBox(orders);
        sortLine3.add(sortOptionDropDown1);

        sortPanel.add(sortLine1);
        sortPanel.add(sortLine2);
        sortPanel.add(sortLine3);

        //-----------------------------------------------------------------------------
        //FILTER CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

        JPanel filterLine1 = new JPanel();
        filterList = new JCheckBox("Filter");
        filterLine1.add(filterList);

        JPanel filterLine2 = new JPanel();
        sortOptionDropDown2 = new JComboBox(sortType);
        greaterthanDropDown = new JComboBox(greaterOrLesser);
        filterNumberSpinner = new JSpinner();

        filterLine2.add(sortOptionDropDown2);
        filterLine2.add(greaterthanDropDown);
        filterLine2.add(filterNumberSpinner);

        filterPanel.add(filterLine1);
        filterPanel.add(filterLine2);

        //-----------------------------------------------------------------------------
        //TABLE OF STATES
        //-----------------------------------------------------------------------------
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));

        stateListDisplay = new JList(stateList);
        JScrollPane stateListScroller = new JScrollPane(stateListDisplay);

        statePanel.add(new JScrollPane(stateListScroller));
    }

    public void readData()
    {
        //CREATE LIST OF STRINGS TO REPRESENT EACH ITEM'S VALUES (CITY, COUNTRY, ETC.)
        crimeData = new ArrayList<>();

        //CREATE A BUFFERED READER
        try (BufferedReader br = new BufferedReader(new FileReader("US_violent_crime.csv")))
        {
            //FOR EACH LINE...
            String line;
            //UNTIL REACHING THE END OF THE FILE (NULL)
            while ((line = br.readLine()) != null)
            {
                //CREATE STRINGS FOR EACH VALUE SPLIT BY COMMAS
                String[] values = line.split(",");

                //ADD TO CITY INFO LIST
                crimeData.add(Arrays.asList(values));
            }
        }

        //EXCEPTION IF FILE IS NOT PROPERLY READ
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}

