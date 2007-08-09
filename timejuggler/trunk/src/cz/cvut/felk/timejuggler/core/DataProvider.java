/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.DbElement;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import org.izvin.client.desktop.ui.util.UIBeanEnhancer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private static DataProvider ourInstance = new DataProvider();
    private List<Category> categories;
    private ArrayListModel<VCalendar> calendars;
    private DbDataStore dbStore;

    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
        dbStore = new DbDataStore();
        calendars = new ArrayListModel<VCalendar>();
        categories = Arrays.asList(new Category("Birthday", Color.YELLOW), new Category("Annivesary", Color.BLUE), new Category("Annivesary", Color.BLUE));

        calendars = null; //lazy inicializace
    }


    //utilita, bude pozdeji presunuto, az jestli tohle bude potreba a budu vedet kam ;-)
    private <C> ArrayListModel<C> enhanceToBeans(List<C> list) {
        final ArrayListModel<C> listModel = new ArrayListModel<C>();
        for (C item : list) {
            listModel.add((C) UIBeanEnhancer.enhance(item));
        }
        return listModel;
    }


    public List<Category> getCategories() {
        return categories;
    }

    public synchronized ArrayListModel<VCalendar> getCalendars() {
        if (calendars == null) {
            //List<VCalendar> normalFromDBList = Arrays.asList(new VCalendar("VityCalendar"), new VCalendar("JohnnyCalendar"), new VCalendar("SvatkyCalendar"));
            calendars = enhanceToBeans(dbStore.getCalendars());
        }
        return calendars;
    }

    public void deleteCalendars() {
        calendars.clear();
    }

    public synchronized void addCalendar(VCalendar calendar) throws DatabaseException {
        try {
            saveOrUpdate(calendar);
            addNewCalendar(calendar);
        } catch (DatabaseException e) {
            throw e;
        }
    }

    private void addNewCalendar(VCalendar calendar) {
        if (calendars != null)
            calendars.add((VCalendar) UIBeanEnhancer.enhance(calendar));
    }


    /**
     * Method saveOrUpdate
     * <p/>
     * Ulozi entitu do databaze
     */
    public <C extends DbElement> void saveOrUpdate(C entity) throws DatabaseException {
        TimeJugglerJDBCTemplate template = new TimeJugglerJDBCTemplate();
        entity.saveOrUpdate(template);
        template.commit();
    }
}
