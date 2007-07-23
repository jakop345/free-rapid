package cz.cvut.felk.timejuggler.gui;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.dao.CalendarEventDAO_DummyImpl;
import cz.cvut.felk.timejuggler.entity.CalendarEvent;
import cz.cvut.felk.timejuggler.swing.calendar.CalendarConfig;
import cz.cvut.felk.timejuggler.swing.calendar.CalendarGrid;
import cz.cvut.felk.timejuggler.swing.calendar.CalendarView;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Sprava a vytvoreni hlavniho panelu
 * @author Vity
 */
public class MainPanelManager {
    private JPanel contentPanel = new JPanel(new BorderLayout());
    private static final String LEFT_TOP = "left.top";
    private static final String RIGHT_TOP = "right.top";
    private static final String LEFT_BOTTOM = "left.bottom";
    private static final String RIGHT_BOTTOM = "right.bottom";
    private final MenuManager menuManager;
    private StatusBarManager statusBarManager;
    private ToolbarManager toolbarManager;
    private CalendarGrid calendarGrid;
    private final ApplicationContext context;

    public MainPanelManager(ApplicationContext context) {
        this.context = context;
        this.menuManager = new MenuManager(context);
        initComponents();
    }


    public JXMultiSplitPane getMultiSplitPane() {
        return multiSplitPane;
    }

    private JXMultiSplitPane multiSplitPane;


    private void initComponents() {
        multiSplitPane = new JXMultiSplitPane();
        multiSplitPane.setDividerSize(4);
        
        multiSplitPane.setContinuousLayout(true);
        multiSplitPane.getMultiSplitLayout().setModel(new DefaultSplitPaneModel());
        multiSplitPane.add(new SmallCalendarManager().getComponent(), LEFT_TOP);
        multiSplitPane.add(getTaskList(), LEFT_BOTTOM);
        multiSplitPane.add(new EventsListManager(context).getComponent(), RIGHT_TOP);

        // testovaci blok
        Date startDate = new Date(0);
        startDate.setHours(9);
        startDate.setMinutes(15);

        Date endDate = new Date(0);
        endDate.setHours(13);
        endDate.setMinutes(30);

        Date todayDate = new Date(0);

        CalendarConfig calendarConfig = new CalendarConfig();

        CalendarEventDAO_DummyImpl calendarEventDAO = new CalendarEventDAO_DummyImpl();
        calendarGrid = new CalendarGrid(calendarEventDAO, calendarConfig);
        calendarGrid.setStartDate(todayDate);

        calendarGrid.setCalendarView(CalendarView.WEEK);

        CalendarEvent ce = new CalendarEvent();
        ce.setName("Test udalost");
        ce.setStartDate(startDate);
        ce.setEndDate(endDate);
        calendarEventDAO.saveCalendarEvent(ce);

        startDate = new Date(0);
        startDate.setHours(13);
        startDate.setMinutes(15);

        endDate = new Date(0);
        endDate.setHours(14);
        endDate.setMinutes(38);
        ce = new CalendarEvent();
        ce.setName("Test udalost 2");
        ce.setStartDate(startDate);
        ce.setEndDate(endDate);

        calendarEventDAO.saveCalendarEvent(ce);

        startDate = new Date(0);
        startDate.setHours(11);
        startDate.setMinutes(15);
        startDate.setDate(2);

        endDate = new Date(0);
        endDate.setHours(14);
        endDate.setMinutes(55);
        endDate.setDate(2);

        ce = new CalendarEvent();
        ce.setName("Dalsi den");
        ce.setStartDate(startDate);
        ce.setEndDate(endDate);

        calendarEventDAO.saveCalendarEvent(ce);

        calendarGrid.refreshCalendarEvents();

        // testovaci blok


        multiSplitPane.add(calendarGrid, RIGHT_BOTTOM);

        contentPanel.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        contentPanel.add(multiSplitPane, BorderLayout.CENTER);
        contentPanel.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);
    }

    private StatusBarManager getStatusBarManager() {
        if (statusBarManager == null)
            statusBarManager = new StatusBarManager(context);
        return statusBarManager;
    }

    public JComponent getComponent() {
        return contentPanel;
    }

    private JComponent getTaskList() {
        return new TaskListManager().getComponent();
    }

    public ToolbarManager getToolbarManager() {
        if (toolbarManager == null)
            toolbarManager = new ToolbarManager(context);
        return toolbarManager;

    }

    /**
     * A simplified SplitPaneLayout for common split pane needs. A common multi splitpane need is:
     */
    private static class DefaultSplitPaneModel extends MultiSplitLayout.Split {
        public DefaultSplitPaneModel() {
            MultiSplitLayout.Split col2 = new MultiSplitLayout.Split();
            MultiSplitLayout.Split col1 = new MultiSplitLayout.Split();
            col1.setRowLayout(false);
            //col1.setWeight(0.9);
            col2.setRowLayout(false);
            setChildren(col1, new MultiSplitLayout.Divider(), col2);
            col1.setChildren(new MultiSplitLayout.Leaf(LEFT_TOP), new MultiSplitLayout.Divider(), new MultiSplitLayout.Leaf(LEFT_BOTTOM));
            col2.setChildren(new MultiSplitLayout.Leaf(RIGHT_TOP), new MultiSplitLayout.Divider(), new MultiSplitLayout.Leaf(RIGHT_BOTTOM));
        }
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public CalendarGrid getCalendarGrid() {
        return calendarGrid;
    }
}
