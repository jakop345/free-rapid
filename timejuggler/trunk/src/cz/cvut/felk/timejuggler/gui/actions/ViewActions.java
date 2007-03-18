package cz.cvut.felk.timejuggler.gui.actions;

import application.Action;

import java.awt.*;

/**
 * @author Vity
 */

public class ViewActions extends Component {
    public static final int DAY_VIEW = 0;
    public static final int WEEK_VIEW = 1;
    public static final int MULTIWEEK_VIEW = 2;
    public static final int MONTH_VIEW = 3;


    public ViewActions() {

    }

    @Action
    public void showStatusBar() {
        //final boolean selected = ((AbstractButton) e.getSource()).isSelected();        
    }

    @Action
    public void dayView(javax.swing.Action action) {
    }

    @Action
    public void weekView() {

    }

    @Action
    public void multiWeekView() {

    }

    @Action
    public void monthView() {

    }

    @Action
    public void showToolbar() {

    }

    @Action
    public void showSearchBar() {

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
