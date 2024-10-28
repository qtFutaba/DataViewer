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
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TablePanel extends JPanel implements ListSelectionListener, ItemListener
{
    private DefaultListModel<StateData> stateList;
    private JList stateListDisplay;
    private List aggregateData;
    private Double[] means;

    private JPanel sortPanel;
    private JPanel filterPanel;
    private JPanel listPanel;

    private JLabel stateShowing;

    private String sortType[] = {"Murder", "Assault", "Rape", "% Of Urban Pop."};
    private String orders[] = {"Ascending", "Descending"};
    private String greaterOrLesser[] = {"<", ">"};

    private JComboBox sortOptionDropDown1;
    private JComboBox sortOptionDropDown2;
    private JComboBox orderDropDown;
    private JComboBox greaterthanDropDown;
    private JSpinner filterNumberSpinner;

    private JCheckBox filterList;
    private JCheckBox sortList;

    private StatsPanel statsPanel;
    private ChartPanel chartPanel;
    private DetailsPanel detailsPanel;

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
        stateListDisplay.addListSelectionListener(this);

        JScrollPane stateListScroller = new JScrollPane(stateListDisplay);

        statePanel.add(new JScrollPane(stateListScroller));


        //-----------------------------------------------------------------------------
        //AGGREGATE STATISTICS PANEL
        //-----------------------------------------------------------------------------
        statsPanel = new StatsPanel();

        aggregateData = List.of(stateList.toArray());

        updateStatsData(aggregateData);

        //-----------------------------------------------------------------------------
        //SPECIFIC DETAILS PANEL
        //-----------------------------------------------------------------------------
        JPanel detailsDisplayPanel = new JPanel();
        detailsDisplayPanel.setLayout(new BoxLayout(detailsDisplayPanel, BoxLayout.Y_AXIS));

        stateShowing = new JLabel("Details: NONE SELECTED");
        detailsPanel = new DetailsPanel();


        detailsDisplayPanel.add(stateShowing);
        detailsDisplayPanel.add(detailsPanel);

        //-----------------------------------------------------------------------------
        //CHART PANEL
        //-----------------------------------------------------------------------------
        chartPanel = new ChartPanel(aggregateData, means);

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

                //ADD TO STATE INFO LIST
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
            murder.add(state.getMurder());
            assault.add(state.getAssault());
            rape.add(state.getRape());
            urbanPop.add(state.getUrbanPop());
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

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        //SELECTED STATE - TAKE DATA FROM LIST
        if (stateListDisplay.isSelectionEmpty() == false) {
            StateData detaildata = stateList.getElementAt(stateListDisplay.getSelectedIndex());


            stateShowing.setText(detaildata.getName());

            //SET VALUES FOR DATA
            detailsPanel.setValue(String.valueOf(detaildata.getMurder()), 0);
            detailsPanel.setValue(String.valueOf(detaildata.getAssault()), 1);
            detailsPanel.setValue(String.valueOf(detaildata.getRape()), 2);
            detailsPanel.setValue(String.valueOf(detaildata.getUrbanPop()), 3);

            //SET VALUES FOR RANKING

            //REPAINT
            detailsPanel.revalidate();
            detailsPanel.repaint();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        //SORT THE DATA
        if (e.getSource().equals(sortList))
        {
            stateListDisplay.clearSelection();


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
                else if (Objects.equals(sortselection, "UrbanPop"))
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
            else
            {
                sortOptionDropDown1.setEnabled(true);
                orderDropDown.setEnabled(true);
                stateList.clear();
                readData();
            }
        };

        if (e.getSource().equals(filterList))
        {
            stateListDisplay.clearSelection();
            sortOptionDropDown2.setEnabled(false);
            greaterthanDropDown.setEnabled(false);
            filterNumberSpinner.setEnabled(false);

            if (filterList.isSelected())
            {
                String filterselection = (String) sortOptionDropDown2.getSelectedItem();
                String greaterselection = (String) greaterthanDropDown.getSelectedItem();
                double comparatorFilter = (double) filterNumberSpinner.getValue();

                Stream statesStream = Arrays.stream(stateList.toArray());

                if (Objects.equals(filterselection, "Murder")) {
                    Predicate<StateData> filterPredicate;

                    if (greaterselection.equals(">")) {
                        filterPredicate = new Predicate<StateData>() {
                            @Override
                            public boolean test(StateData state) {
                                return state.getMurder() > comparatorFilter;
                            }
                        };
                    } else if (greaterselection.equals("<")) {
                        filterPredicate = new Predicate<StateData>() {
                            @Override
                            public boolean test(StateData state) {
                                return state.getMurder() < comparatorFilter;
                            }
                        };
                    } else {
                        // In case of an unrecognized selection, return early or handle it
                        filterPredicate = state -> true; // No filter applied if none is specified
                    }

                    // Apply the predicate as a filter
                    statesStream = statesStream.filter(filterPredicate);
                }

                stateList.clear();
                for (Object state : statesStream.toList())
                {
                    StateData stateData = (StateData) state;
                    stateList.addElement(stateData);
                }
            }
            else
            {
                sortOptionDropDown2.setEnabled(true);
                greaterthanDropDown.setEnabled(true);
                filterNumberSpinner.setEnabled(true);
                stateList.clear();
                readData();
            }
        };
    }
}

