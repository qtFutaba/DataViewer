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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class TablePanel extends JPanel //implements ListSelectionListener, ActionListener, ItemListener
{
    DefaultListModel stateList;
    JList stateListDisplay;
    List data;
    Double[] means;

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
    DetailsPanel detailsPanel;

    //DEFAULT CONSTRUCTOR
    public TablePanel()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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
        sortLine3.add(orderDropDown);

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

        stateList = new DefaultListModel();

        readData();

        stateListDisplay = new JList(stateList);
        JScrollPane stateListScroller = new JScrollPane(stateListDisplay);

        statePanel.add(new JScrollPane(stateListScroller));



        //-----------------------------------------------------------------------------
        //AGGREGATE STATISTICS PANEL
        //-----------------------------------------------------------------------------
        statsPanel = new StatsPanel();

        data = List.of(stateList.toArray());

        updateStatsData(data);

        //-----------------------------------------------------------------------------
        //SPECIFIC DETAILS PANEL
        //-----------------------------------------------------------------------------
        JPanel detailsDisplayPanel = new JPanel();
        detailsDisplayPanel.setLayout(new BoxLayout(detailsDisplayPanel, BoxLayout.Y_AXIS));

        JLabel detailsLabel = new JLabel("Details: NONE SELECTED");
        detailsPanel = new DetailsPanel();


        detailsDisplayPanel.add(detailsLabel);
        detailsDisplayPanel.add(detailsPanel);

        //-----------------------------------------------------------------------------
        //CHART PANEL
        //-----------------------------------------------------------------------------
        chartPanel = new ChartPanel(data, means);
        //-----------------------------------------------------------------------------
        //ADD ALL TO TABLEPANEL
        //-----------------------------------------------------------------------------
        this.add(sortPanel);
        this.add(filterPanel);
        this.add(statePanel);
        this.add(statsPanel);
        this.add(detailsDisplayPanel);
        this.add(chartPanel);

        setBackground(new Color(40, 145, 255));
    }

    public void readData()
    {
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

                StateData newState = new StateData(values[0],Double.parseDouble(values[1]),Double.parseDouble(values[2]),Double.parseDouble(values[3]),Double.parseDouble(values[4]));

                //ADD TO CITY INFO LIST
                stateList.addElement(newState);
            }
        }

        //EXCEPTION IF FILE IS NOT PROPERLY READ
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void updateStatsData(List<StateData> data)
    {
        List<Double> murder = new ArrayList<>();
        List<Double> assault = new ArrayList<>();
        List<Double> rape = new ArrayList<>();
        List<Double> urbanPop = new ArrayList<>();

        for (StateData state : data)
        {
            murder.add(state.murder());
            assault.add(state.assault());
            rape.add(state.rape());
            urbanPop.add(state.urbanPop());
        }

        List<Double> statistics = new ArrayList<>();
        means = new Double[4];

        statistics = calcData(murder);
        means[0] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 0);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 4);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 8);

        statistics = calcData(assault);
        means[1] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 1);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 5);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 9);

        statistics = calcData(rape);
        means[2] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 2);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 6);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 10);

        statistics = calcData(urbanPop);
        means[3] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 3);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 7);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 11);

    }

    public List<Double> calcData(List<Double> data)
    {
        double mean;
        double median;
        double standardDeviation;

        data.sort(null);
        int medianIndex = data.size()/2;

        median = data.get(medianIndex);

        double sum = 0;

        for (double entry : data)
        {
            sum += entry;
        }

        mean = sum / data.size();
        standardDeviation = Math.sqrt(sum / data.size());

        Double truncatedMean = BigDecimal.valueOf(mean)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        Double truncatedMedian = BigDecimal.valueOf(median)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        Double truncatedSTD = BigDecimal.valueOf(standardDeviation)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
        List<Double> result = new ArrayList<>();


        result.add(truncatedMean);
        result.add(truncatedMedian);
        result.add(truncatedSTD);

        return result;

    }
}

