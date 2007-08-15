package cz.cvut.felk.timejuggler.gui.actions;

import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.swing.components.calendar.CalendarView;
import application.Action;

/**
 * @author Vity
 */

public class ViewActions {
    public static final int DAY_VIEW = 0;
    public static final int WEEK_VIEW = 1;
    public static final int MULTIWEEK_VIEW = 2;
    public static final int MONTH_VIEW = 3;

    private MainApp app;

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void showStatusBar() {
        //final boolean selected = ((AbstractButton) e.getSource()).isSelected();        
    }

    @Action
    public void dayView(javax.swing.Action action) {
    	app.getMainPanel().getCalendarGrid().setCalendarView(CalendarView.DAY);
    	app.getMainPanel().getCalendarGrid().refreshCalendarEvents();
    }

    @Action
    public void weekView() {
    	app.getMainPanel().getCalendarGrid().setCalendarView(CalendarView.WEEK);
    	app.getMainPanel().getCalendarGrid().refreshCalendarEvents();
    }

    @Action
    public void multiWeekView() {
    	app.getMainPanel().getCalendarGrid().setCalendarView(CalendarView.MULTI_WEEK);
    	app.getMainPanel().getCalendarGrid().refreshCalendarEvents();
    }

    @Action
    public void monthView() {
    	app.getMainPanel().getCalendarGrid().setCalendarView(CalendarView.MONTH);
    	app.getMainPanel().getCalendarGrid().refreshCalendarEvents();
    }

    @Action
    public void showToolbar() {

    }

    @Action
    public void showSearchBar() {

    }

	protected MainApp getApp() {
		return app;
	}

//    public boolean isEnabled() {
//        System.out.println("isenabled");
//        return true;
//    }
//
//    public void setEnabled(boolean value) {
//        System.out.println("set enabled");
//    }
//
//    public boolean isSelectedShowMenu() {
//        System.out.println("check selected");
//        return ((SingleFrameApplication)ApplicationContext.getInstance().getApplication()).getMainFrame().getJMenuBar().isVisible();
//    }
//
//    public void setSelectedShowMenu(final boolean selected) {
//        System.out.println("set selected");
//        final MainPanelManager mainPanel = ((MainApp) ApplicationContext.getInstance().getApplication()).getMainPanel();
//        mainPanel.getStatusBar().setVisible(selected);
//        AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, selected);
//    }

}
