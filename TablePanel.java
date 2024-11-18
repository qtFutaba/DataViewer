import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

//THE TABLE PANEL: HEART OF THE GUI. CONTAINS THE SORTING AND FILTERING INTERFACES, THE AGGREGATE STAT VIEW, THE TABLE OF VARIABLES, DETAILS, AND CHART.
public class TablePanel extends JPanel
{
    //LISTS OF VARIABLES
    DefaultListModel<StateData> stateList;
    JList stateListDisplay;
    List data;
    Double[] means;

    //MAIN PANELS FOR LAYOUT
    private JPanel controlsPanel;
    private JPanel sortPanel;
    private JPanel filterPanel;
    private JPanel listPanel;

    //LABEL FOR STATE DETAIL PANEL
    JLabel stateShowing;

    //DROP DOWN BOXES LABELS
    private String sortType[] = {"Murder", "Assault", "Rape", "% Of Urban Pop."};
    private String orders[] = {"Ascending", "Descending"};
    private String greaterOrLesser[] = {"<", ">"};

    //CONTROLS
    JComboBox sortOptionDropDown1;
    JComboBox sortOptionDropDown2;
    JComboBox orderDropDown;
    JComboBox greaterthanDropDown;
    JSpinner filterNumberSpinner;

    JCheckBox filterList;
    JCheckBox sortList;

    //SPECIFIC PANELS
    private StatsPanel statsPanel;
    ChartPanel chartPanel;
    DetailsPanel detailsPanel;

    //OBSERVER
    DataController dc;

    //DEFAULT CONSTRUCTOR
    public TablePanel()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        dc = new DataController(this);

        controlsPanel = new JPanel();
        //-----------------------------------------------------------------------------
        //SORTING CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));

        JPanel sortLine1 = new JPanel();
        sortList = new JCheckBox("Sort");
        sortList.addItemListener(dc);
        sortLine1.add(sortList);

        JPanel sortLine2 = new JPanel();
        sortOptionDropDown1 = new JComboBox(sortType);
        sortOptionDropDown1.addItemListener(dc);
        sortLine2.add(sortOptionDropDown1);

        JPanel sortLine3 = new JPanel();
        orderDropDown = new JComboBox(orders);
        orderDropDown.addItemListener(dc);
        sortLine3.add(orderDropDown);

        sortPanel.add(sortLine1);
        sortPanel.add(sortLine2);
        sortPanel.add(sortLine3);

        sortLine1.setBackground(new Color(40, 145, 255));
        sortLine2.setBackground(new Color(40, 145, 255));
        sortLine3.setBackground(new Color(40, 145, 255));

        sortPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sortPanel.setPreferredSize(new Dimension(300,120));

        //-----------------------------------------------------------------------------
        //FILTER CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

        JPanel filterLine1 = new JPanel();
        filterList = new JCheckBox("Filter");
        filterList.addItemListener(dc);

        filterLine1.add(filterList);

        JPanel filterLine2 = new JPanel();
        sortOptionDropDown2 = new JComboBox(sortType);
        sortOptionDropDown2.addItemListener(dc);
        greaterthanDropDown = new JComboBox(greaterOrLesser);
        greaterthanDropDown.addItemListener(dc);
        filterNumberSpinner = new JSpinner();
        filterNumberSpinner.setPreferredSize(new Dimension(50,26));

        filterLine2.add(sortOptionDropDown2);
        filterLine2.add(greaterthanDropDown);
        filterLine2.add(filterNumberSpinner);

        filterPanel.add(filterLine1);
        filterPanel.add(filterLine2);

        filterLine1.setBackground(new Color(40, 145, 255));
        filterLine2.setBackground(new Color(40, 145, 255));


        filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        filterPanel.setPreferredSize(new Dimension(300,120));

        controlsPanel.add(sortPanel);
        controlsPanel.add(filterPanel);

        sortPanel.setBackground(new Color(40, 145, 255));
        filterPanel.setBackground(new Color(40, 145, 255));
        controlsPanel.setBackground(new Color(40, 145, 255));



        //-----------------------------------------------------------------------------
        //TABLE OF STATES
        //-----------------------------------------------------------------------------
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));

        stateList = new DefaultListModel();

        readData();

        stateListDisplay = new JList(stateList);
        stateListDisplay.addListSelectionListener(dc);

        JScrollPane stateListScroller = new JScrollPane(stateListDisplay);

        statePanel.add(new JScrollPane(stateListScroller));



        //-----------------------------------------------------------------------------
        //AGGREGATE STATISTICS PANEL
        //-----------------------------------------------------------------------------
        JPanel statsDisplayPanel = new JPanel();
        statsDisplayPanel.setLayout(new BoxLayout(statsDisplayPanel, BoxLayout.Y_AXIS));

        JLabel statsLabel = new JLabel("Aggregate Statistics");
        statsPanel = new StatsPanel();


        data = List.of(stateList.toArray());

        updateStatsData(data);

        statsDisplayPanel.add(statsLabel);
        statsDisplayPanel.add(statsPanel);
        statsDisplayPanel.setBackground(new Color(40, 145, 255));
        statsPanel.setBackground(Color.WHITE);

        //-----------------------------------------------------------------------------
        //SPECIFIC DETAILS PANEL
        //-----------------------------------------------------------------------------
        JPanel detailsDisplayPanel = new JPanel();
        detailsDisplayPanel.setLayout(new BoxLayout(detailsDisplayPanel, BoxLayout.Y_AXIS));

        stateShowing = new JLabel("No State Selected");
        detailsPanel = new DetailsPanel();


        detailsDisplayPanel.add(stateShowing);
        detailsDisplayPanel.add(detailsPanel);
        detailsDisplayPanel.setBackground(new Color(40, 145, 255));
        detailsPanel.setBackground(Color.WHITE);

        //-----------------------------------------------------------------------------
        //CHART PANEL
        //-----------------------------------------------------------------------------
        chartPanel = new ChartPanel(data, means);


        //-----------------------------------------------------------------------------
        //ADD ALL TO TABLEPANEL
        //-----------------------------------------------------------------------------
        this.add(controlsPanel);
        this.add(statePanel);
        this.add(statsDisplayPanel);
        this.add(detailsDisplayPanel);
        this.add(chartPanel);

        //COLOR :)
        this.setBackground(new Color(40, 145, 255));
        controlsPanel.setBackground(new Color(40, 145, 255));
        statsDisplayPanel.setBackground(new Color(40, 145, 255));
        chartPanel.setBackground(new Color(40, 145, 255));

        sortList.setBackground(new Color(40, 145, 255));
        filterList.setBackground(new Color(40, 145, 255));
    }

    public void readData()
    {
        //CREATE A BUFFERED READER TO TAKE IN INFORMATION
        try (BufferedReader br = new BufferedReader(new FileReader("US_violent_crime.csv")))
        {
            //FOR EACH LINE...
            String line;
            //UNTIL REACHING THE END OF THE FILE (NULL)
            while ((line = br.readLine()) != null)
            {
                //CREATE STRINGS FOR EACH VALUE SPLIT BY COMMAS
                String[] values = line.split(",");

                //CREATE NEW STATEDATA INSTANCE...
                StateData newState = new StateData(values[0],Double.parseDouble(values[1]),Double.parseDouble(values[2]),Double.parseDouble(values[3]),Double.parseDouble(values[4]));

                //ADD IT TO THE STATE INFO LIST
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
        //CREATE LISTS TO STORE THE VALUES OF EACH ASPECT OF THE STATEDATA CLASS FOR MATH REASONS
        List<Double> murder = new ArrayList<>();
        List<Double> assault = new ArrayList<>();
        List<Double> rape = new ArrayList<>();
        List<Double> urbanPop = new ArrayList<>();

        //ITERATE THROUGH THE STATEDATA LIST AND STORE THE VALUES IN THE RESPECTIVE ARRAYS FOR DOING MATH
        for (StateData state : data)
        {
            murder.add(state.getMurder());
            assault.add(state.getAssault());
            rape.add(state.getRape());
            urbanPop.add(state.getUrbanPop());
        }

        //CREATE A LIST THAT HOLDS THE DIFFERENT AGGREGATE STATISTICS
        List<Double> statistics = new ArrayList<>();

        //AND AN ARRAY FOR STORING THE MEAN FOR EACH VARIABLE (MURDER, ASSAULT, RAPE, URBAN POPULATION PERCENT)
        means = new Double[4];

        //RUN THE STATS FOR MURDER...
        statistics = calcData(murder);
        means[0] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 0);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 4);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 8);

        //RUN THE STATS FOR ASSAULT...
        statistics = calcData(assault);
        means[1] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 1);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 5);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 9);

        //RUN THE STATS FOR RAPE...
        statistics = calcData(rape);
        means[2] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 2);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 6);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 10);

        //RUN THE STATS FOR URBAN POPULATION PERCENTAGE...
        statistics = calcData(urbanPop);
        means[3] = statistics.get(0);
        statsPanel.setValue(String.valueOf(statistics.get(0)), 3);
        statsPanel.setValue(String.valueOf(statistics.get(1)), 7);
        statsPanel.setValue(String.valueOf(statistics.get(2)), 11);
    }

    public List<Double> calcData(List<Double> data)
    {
        //3 AGGREGATE STATISTICS
        double mean;
        double median;
        double standardDeviation;

        //SORT THE NUMERICAL DATA INTO ASCENDING ORDER
        data.sort(null);

        //MEDIAN = AMOUNT OF VARIABLES / 2 - THE MIDDLE
        int medianIndex = data.size()/2;
        median = data.get(medianIndex);

        //INITIALIZE SUM TO CALCULATE MEAN
        double sum = 0;

        //ADD EACH NUMBER IN THE LIST TOGETHER
        for (double number : data)
        {
            sum += number;
        }

        //AND DIVIDE BY THE AMOUNT OF DATA
        mean = sum / data.size();

        //CALCULATE STANDARD DEVIATION
        standardDeviation = Math.sqrt(sum / data.size());

        //SET PRECISION OF DOUBLE TO 3 DECIMAL POINTS FOR EACH VARIABLE
        Double truncatedMean = BigDecimal.valueOf(mean)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        Double truncatedMedian = BigDecimal.valueOf(median)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        Double truncatedSTD = BigDecimal.valueOf(standardDeviation)
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();

        //CREATE A LIST TO RETURN THE RESULTS
        List<Double> result = new ArrayList<>();

        result.add(truncatedMean);
        result.add(truncatedMedian);
        result.add(truncatedSTD);

        return result;
    }
}

