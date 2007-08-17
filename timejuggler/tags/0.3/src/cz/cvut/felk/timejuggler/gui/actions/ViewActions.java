package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.swing.components.calendar.CalendarView;

/**
 * @author Vity
 */

public class ViewActions {

    private MainApp app;

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void showStatusBar() {
        //final boolean selected = ((AbstractButton) e.getSource()).isSelected();        
    }

    @Action
    public void dayView() {
        updateGrid(CalendarView.DAY);
    }


    @Action
    public void weekView() {
        updateGrid(CalendarView.WEEK);
    }

    @Action
    public void multiWeekView() {
        updateGrid(CalendarView.MULTI_WEEK);
    }

    @Action
    public void monthView() {
        updateGrid(CalendarView.MONTH);
    }

    private void updateGrid(CalendarView view) {
        app.getMainPanel().updateGrid(view);
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
