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
public class TablePanel extends JPanel implements ListSelectionListener, ItemListener
{
    //LISTS OF VARIABLES
    private DefaultListModel<StateData> stateList;
    private JList stateListDisplay;
    private List data;
    private Double[] means;

    //MAIN PANELS FOR LAYOUT
    private JPanel controlsPanel;
    private JPanel sortPanel;
    private JPanel filterPanel;
    private JPanel listPanel;

    //LABEL FOR STATE DETAIL PANEL
    private JLabel stateShowing;

    //DROP DOWN BOXES LABELS
    private String sortType[] = {"Murder", "Assault", "Rape", "% Of Urban Pop."};
    private String orders[] = {"Ascending", "Descending"};
    private String greaterOrLesser[] = {"<", ">"};

    //CONTROLS
    private JComboBox sortOptionDropDown1;
    private JComboBox sortOptionDropDown2;
    private JComboBox orderDropDown;
    private JComboBox greaterthanDropDown;
    private JSpinner filterNumberSpinner;

    private JCheckBox filterList;
    private JCheckBox sortList;

    //SPECIFIC PANELS
    private StatsPanel statsPanel;
    private ChartPanel chartPanel;
    private DetailsPanel detailsPanel;

    //DEFAULT CONSTRUCTOR
    public TablePanel()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        controlsPanel = new JPanel();
        //-----------------------------------------------------------------------------
        //SORTING CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(new BoxLayout(sortPanel, BoxLayout.Y_AXIS));

        JPanel sortLine1 = new JPanel();
        sortList = new JCheckBox("Sort");
        sortList.addItemListener(this);
        sortLine1.add(sortList);

        JPanel sortLine2 = new JPanel();
        sortOptionDropDown1 = new JComboBox(sortType);
        sortOptionDropDown1.addItemListener(this);
        sortLine2.add(sortOptionDropDown1);

        JPanel sortLine3 = new JPanel();
        orderDropDown = new JComboBox(orders);
        orderDropDown.addItemListener(this);
        sortLine3.add(orderDropDown);

        sortPanel.add(sortLine1);
        sortPanel.add(sortLine2);
        sortPanel.add(sortLine3);

        sortPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        sortPanel.setPreferredSize(new Dimension(300,120));

        //-----------------------------------------------------------------------------
        //FILTER CONTROLS PANEL
        //-----------------------------------------------------------------------------
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

        JPanel filterLine1 = new JPanel();
        filterList = new JCheckBox("Filter");
        filterList.addItemListener(this);

        filterLine1.add(filterList);

        JPanel filterLine2 = new JPanel();
        sortOptionDropDown2 = new JComboBox(sortType);
        sortOptionDropDown2.addItemListener(this);
        greaterthanDropDown = new JComboBox(greaterOrLesser);
        greaterthanDropDown.addItemListener(this);
        filterNumberSpinner = new JSpinner();
        filterNumberSpinner.setPreferredSize(new Dimension(50,26));

        filterLine2.add(sortOptionDropDown2);
        filterLine2.add(greaterthanDropDown);
        filterLine2.add(filterNumberSpinner);

        filterPanel.add(filterLine1);
        filterPanel.add(filterLine2);

        filterPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        filterPanel.setPreferredSize(new Dimension(300,120));

        controlsPanel.add(sortPanel);
        controlsPanel.add(filterPanel);

        //-----------------------------------------------------------------------------
        //TABLE OF STATES
        //-----------------------------------------------------------------------------
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BoxLayout(statePanel, BoxLayout.Y_AXIS));

        stateList = new DefaultListModel();

        readData();

        stateListDisplay = new JList(stateList);
        stateListDisplay.addListSelectionListener(this);

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

        //-----------------------------------------------------------------------------
        //SPECIFIC DETAILS PANEL
        //-----------------------------------------------------------------------------
        JPanel detailsDisplayPanel = new JPanel();
        detailsDisplayPanel.setLayout(new BoxLayout(detailsDisplayPanel, BoxLayout.Y_AXIS));

        stateShowing = new JLabel("No State Selected");
        detailsPanel = new DetailsPanel();


        detailsDisplayPanel.add(stateShowing);
        detailsDisplayPanel.add(detailsPanel);

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

        setBackground(new Color(40, 145, 255));
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

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        //SELECTED STATE - TAKE DATA FROM LIST
        if (stateListDisplay.isSelectionEmpty() == false) {
            StateData detaildata = stateList.getElementAt(stateListDisplay.getSelectedIndex());


            stateShowing.setText(detaildata.getName());

            //SET VALUES FOR DATA
            detailsPanel.setValue(String.valueOf(detaildata.getMurder()), 0); //MURDER
            detailsPanel.setValue(String.valueOf(detaildata.getAssault()), 1); //ASSAULT
            detailsPanel.setValue(String.valueOf(detaildata.getRape()), 2); //RAPE
            detailsPanel.setValue(String.valueOf(detaildata.getUrbanPop()), 3); //URBAN POPULATION

        }
        //NO STATE SELECTED - DEFAULT
        else
        {
            stateShowing.setText("No State Selected");

            //SET VALUES FOR DATA
            detailsPanel.setValue(String.valueOf(0), 0); //MURDER
            detailsPanel.setValue(String.valueOf(0), 1); //ASSAULT
            detailsPanel.setValue(String.valueOf(0), 2); //RAPE
            detailsPanel.setValue(String.valueOf(0), 3); //URBAN POPULATION
        }

        //REPAINT
        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        //SORT THE DATA
        if (e.getSource().equals(sortList))
        {
            //CLEAR LIST SELECTION TO AVOID WEIRD ISSUES WITH THE SHOWN DETAILS PANEL
            stateListDisplay.clearSelection();

            //SORTING ACTIVE
            if (sortList.isSelected())
            {
                sortOptionDropDown1.setEnabled(false);
                orderDropDown.setEnabled(false);

                String sortselection = (String) sortOptionDropDown1.getSelectedItem();
                String orderselection = (String) orderDropDown.getSelectedItem();

                Stream statesStream = Arrays.stream(stateList.toArray());

                if (Objects.equals(sortselection, "Murder"))
                {
                    if (Objects.equals(orderselection, "Ascending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getMurder));
                    }
                    else if (Objects.equals(orderselection, "Descending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getMurder).reversed());
                    }
                }
                else if (Objects.equals(sortselection, "Assault"))
                {
                    if (Objects.equals(orderselection, "Ascending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getAssault));
                    }
                    else if (Objects.equals(orderselection, "Descending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getAssault).reversed());
                    }
                }
                else if (Objects.equals(sortselection, "Rape"))
                {
                    if (Objects.equals(orderselection, "Ascending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getRape));
                    }
                    else if (Objects.equals(orderselection, "Descending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getRape).reversed());
                    }
                }
                else if (Objects.equals(sortselection, "% Of Urban Pop."))
                {
                    if (Objects.equals(orderselection, "Ascending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getUrbanPop));
                    }
                    else if (Objects.equals(orderselection, "Descending"))
                    {
                        statesStream = statesStream.sorted(Comparator.comparing(StateData::getUrbanPop).reversed());
                    }
                }

                stateList.clear();
                for (Object state : statesStream.toList())
                {
                    StateData stateData = (StateData) state;
                    stateList.addElement(stateData);
                }
            }
            //SORTING INACTIVE - RETURN TO DEFAULT
            else
            {
                //RESET CONTROLS
                sortOptionDropDown1.setEnabled(true);
                orderDropDown.setEnabled(true);
                stateList.clear();
                readData();
            }
        }

        //FILTER THE DATA
        else if (e.getSource().equals(filterList))
        {
            //CLEAR LIST SELECTION TO AVOID WEIRD ISSUES WITH THE SHOWN DETAILS PANEL
            stateListDisplay.clearSelection();

            //FILTER ACTIVE
            if (filterList.isSelected())
            {
                sortOptionDropDown2.setEnabled(false);
                greaterthanDropDown.setEnabled(false);
                filterNumberSpinner.setEnabled(false);

                String filterselection = (String) sortOptionDropDown2.getSelectedItem();
                String directionselection = (String) greaterthanDropDown.getSelectedItem();
                int filterComparator = (int) filterNumberSpinner.getValue();

                //USING STREAMS
                Stream statesStream = Arrays.stream(stateList.toArray());

                //SETUP FILTER FOR COMPARISON OF > OR <
                Predicate<StateData> filterPredicate = null;

                if (Objects.equals(filterselection, "Murder"))
                {
                    if (Objects.equals(directionselection, ">"))
                    {
                        filterPredicate = state -> state.getMurder() > filterComparator;
                    }
                    else if (Objects.equals(directionselection, "<"))
                    {
                        filterPredicate = state -> state.getMurder() < filterComparator;
                    }
                }
                else if (Objects.equals(filterselection, "Assault"))
                {
                    if (Objects.equals(directionselection, ">"))
                    {
                        filterPredicate = state -> state.getAssault() > filterComparator;
                    }
                    else if (Objects.equals(directionselection, "<"))
                    {
                        filterPredicate = state -> state.getAssault() < filterComparator;
                    }
                }
                else if (Objects.equals(filterselection, "Rape"))
                {
                    if (Objects.equals(directionselection, ">"))
                    {
                        filterPredicate = state -> state.getRape() > filterComparator;
                    }
                    else if (Objects.equals(directionselection, "<"))
                    {
                        filterPredicate = state -> state.getRape() < filterComparator;
                    }
                }
                else if (Objects.equals(filterselection, "% Of Urban Pop."))
                {
                    if (Objects.equals(directionselection, ">"))
                    {
                        filterPredicate = state -> state.getUrbanPop() > filterComparator;
                    }
                    else if (Objects.equals(directionselection, "<"))
                    {
                        filterPredicate = state -> state.getUrbanPop() < filterComparator;
                    }
                }

                //APPLY FILTER
                statesStream = statesStream.filter(filterPredicate);

                //CLEAR LIST TO ALLOW FOR FILTERED LIST
                stateList.clear();

                //ADD FILTERED STREAM LIST TO DISPLAY LIST
                for (Object state : statesStream.toList())
                {
                    StateData stateData = (StateData) state;
                    stateList.addElement(stateData);
                }

                //UPDATE AGGREGATE DATA WITH FILTERS
                data = List.of(stateList.toArray());
                updateStatsData(data);

                //RESET CHART
                this.remove(chartPanel);
                chartPanel = new ChartPanel(data, means);
                this.add(chartPanel);
            }
            //FILTER INACTIVE - RETURN TO DEFAULT
            else
            {
                //RESET CONTROLS
                sortOptionDropDown2.setEnabled(true);
                greaterthanDropDown.setEnabled(true);
                filterNumberSpinner.setEnabled(true);
                stateList.clear();
                readData();

                //MUST UPDATE THE AGGREGATE DATA TO DEFAULT
                data = List.of(stateList.toArray());
                updateStatsData(data);

                //RESET CHART
                this.remove(chartPanel);
                chartPanel = new ChartPanel(data, means);
                this.add(chartPanel);
            }
        }
    }
}

