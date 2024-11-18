import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

//OBSERVER CLASS
public class DataController implements ListSelectionListener, ItemListener
{
    // Private data field for storing the container.
    private TablePanel tablePanel;

    // Constructor for the initial controller.
    public DataController(TablePanel tablePanel)
    {
        this.tablePanel = tablePanel;
    }

    @Override
    public void valueChanged(ListSelectionEvent e)
    {
        //SELECTED STATE - TAKE DATA FROM LIST
        if (tablePanel.stateListDisplay.isSelectionEmpty() == false) {
            StateData detaildata = tablePanel.stateList.getElementAt(tablePanel.stateListDisplay.getSelectedIndex());


            tablePanel.stateShowing.setText(detaildata.getName());

            //SET VALUES FOR DATA
            tablePanel.detailsPanel.setValue(String.valueOf(detaildata.getMurder()), 0); //MURDER
            tablePanel.detailsPanel.setValue(String.valueOf(detaildata.getAssault()), 1); //ASSAULT
            tablePanel.detailsPanel.setValue(String.valueOf(detaildata.getRape()), 2); //RAPE
            tablePanel.detailsPanel.setValue(String.valueOf(detaildata.getUrbanPop()), 3); //URBAN POPULATION

        }
        //NO STATE SELECTED - DEFAULT
        else
        {
            tablePanel.stateShowing.setText("No State Selected");

            //SET VALUES FOR DATA
            tablePanel.detailsPanel.setValue(String.valueOf(0), 0); //MURDER
            tablePanel.detailsPanel.setValue(String.valueOf(0), 1); //ASSAULT
            tablePanel.detailsPanel.setValue(String.valueOf(0), 2); //RAPE
            tablePanel.detailsPanel.setValue(String.valueOf(0), 3); //URBAN POPULATION
        }

        //REPAINT
        tablePanel.detailsPanel.revalidate();
        tablePanel.detailsPanel.repaint();
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        //SORT THE DATA
        if (e.getSource().equals(tablePanel.sortList))
        {
            //CLEAR LIST SELECTION TO AVOID WEIRD ISSUES WITH THE SHOWN DETAILS PANEL
            tablePanel.stateListDisplay.clearSelection();

            //SORTING ACTIVE
            if (tablePanel.sortList.isSelected())
            {
                tablePanel.sortOptionDropDown1.setEnabled(false);
                tablePanel.orderDropDown.setEnabled(false);

                String sortselection = (String) tablePanel.sortOptionDropDown1.getSelectedItem();
                String orderselection = (String) tablePanel.orderDropDown.getSelectedItem();

                Stream statesStream = Arrays.stream(tablePanel.stateList.toArray());

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

                tablePanel.stateList.clear();
                for (Object state : statesStream.toList())
                {
                    StateData stateData = (StateData) state;
                    tablePanel.stateList.addElement(stateData);
                }
            }
            //SORTING INACTIVE - RETURN TO DEFAULT
            else
            {
                //RESET CONTROLS
                tablePanel.sortOptionDropDown1.setEnabled(true);
                tablePanel.orderDropDown.setEnabled(true);
                tablePanel.stateList.clear();
                tablePanel.readData();
            }
        }

        //FILTER THE DATA
        else if (e.getSource().equals(tablePanel.filterList))
        {
            //CLEAR LIST SELECTION TO AVOID WEIRD ISSUES WITH THE SHOWN DETAILS PANEL
            tablePanel.stateListDisplay.clearSelection();

            //FILTER ACTIVE
            if (tablePanel.filterList.isSelected())
            {
                tablePanel.sortOptionDropDown2.setEnabled(false);
                tablePanel.greaterthanDropDown.setEnabled(false);
                tablePanel.filterNumberSpinner.setEnabled(false);

                String filterselection = (String) tablePanel.sortOptionDropDown2.getSelectedItem();
                String directionselection = (String) tablePanel.greaterthanDropDown.getSelectedItem();
                int filterComparator = (int) tablePanel.filterNumberSpinner.getValue();

                //USING STREAMS
                Stream statesStream = Arrays.stream(tablePanel.stateList.toArray());

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
                tablePanel.stateList.clear();

                //ADD FILTERED STREAM LIST TO DISPLAY LIST
                for (Object state : statesStream.toList())
                {
                    StateData stateData = (StateData) state;
                    tablePanel.stateList.addElement(stateData);
                }

                //UPDATE AGGREGATE DATA WITH FILTERS
                tablePanel.data = java.util.List.of(tablePanel.stateList.toArray());
                tablePanel.updateStatsData(tablePanel.data);

                //RESET CHART
                tablePanel.remove(tablePanel.chartPanel);
                tablePanel.chartPanel = new ChartPanel(tablePanel.data, tablePanel.means);
                tablePanel.add(tablePanel.chartPanel);
            }
            //FILTER INACTIVE - RETURN TO DEFAULT
            else
            {
                //RESET CONTROLS
                tablePanel.sortOptionDropDown2.setEnabled(true);
                tablePanel.greaterthanDropDown.setEnabled(true);
                tablePanel.filterNumberSpinner.setEnabled(true);
                tablePanel.stateList.clear();
                tablePanel.readData();

                //MUST UPDATE THE AGGREGATE DATA TO DEFAULT
                tablePanel.data = List.of(tablePanel.stateList.toArray());
                tablePanel.updateStatsData(tablePanel.data);

                //RESET CHART
                tablePanel.remove(tablePanel.chartPanel);
                tablePanel.chartPanel = new ChartPanel(tablePanel.data, tablePanel.means);
                tablePanel.add(tablePanel.chartPanel);
            }
        }
    }
}
