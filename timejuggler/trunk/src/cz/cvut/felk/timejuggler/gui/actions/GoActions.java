package cz.cvut.felk.timejuggler.gui.actions;

import com.jgoodies.binding.value.ValueHolder;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.ChooseDateDialog;
import org.jdesktop.application.Action;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Vity
 */

public class GoActions {


    public GoActions() {

    }

    @Action
    public void goToday() {
        MainApp.getDProvider().getCurrentDateHolder().setValue(new Date());
    }

    @Action
    public void goToDate() {
        final MainApp app = MainApp.getInstance(MainApp.class);
        final ValueHolder holder = app.getDataProvider().getCurrentDateHolder();
        final ChooseDateDialog dateDialog = new ChooseDateDialog(app.getMainFrame(), holder);
        app.prepareDialog(dateDialog, true);
    }

    @Action
    public void previousDate() {
        rollDay(-1);
    }

    @Action
    public void nextDate() {
        rollDay(1);
    }

    private void rollDay(int rollBy) {
        final ValueHolder holder = MainApp.getDProvider().getCurrentDateHolder();
        final Date date = (Date) holder.getValue();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.roll(Calendar.DAY_OF_YEAR, rollBy);
        holder.setValue(calendar.getTime());
    }

}
