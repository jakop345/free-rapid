/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core.data;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import org.izvin.client.desktop.ui.util.UIBeanEnhancer;

import java.util.List;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private static DataProvider ourInstance = new DataProvider();
    private ArrayListModel<CategoryEntity> categories;
    private ArrayListModel<VCalendar> calendars;
    PersitencyLayer persitencyLayer;

    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
        calendars = null; //lazy inicializace
        categories = null; //lazy inicializace
    }

    public void init() {
        persitencyLayer = PersistencyLayerFactory.getInstance().getDefaultPersitencyLayer();
        //persitencyLayer = new FakePersistencyLayer();
    }

    //utilita, bude pozdeji presunuto, az jestli tohle bude potreba a budu vedet kam ;-)
    private <C> ArrayListModel<C> enhanceToBeans(List<C> list) {
        final ArrayListModel<C> listModel = new ArrayListModel<C>();
        for (C item : list) {
            listModel.add(UIBeanEnhancer.enhance(item));
        }
        return listModel;
    }

    public synchronized ArrayListModel<CategoryEntity> getCategoriesListModel() {
        if (categories == null) {
            categories = enhanceToBeans(getPersitencyLayer().getCategories());
        }
        return categories;
    }

    public synchronized ArrayListModel<VCalendar> getCalendarsListModel() throws PersistencyLayerException {
        if (calendars == null) {
            calendars = enhanceToBeans(getPersitencyLayer().getCalendars());
        }
        return calendars;
    }

    public synchronized void addCalendar(VCalendar calendar) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCalendar(calendar);
        addNewCalendar(calendar);
    }

    public synchronized void addCategory(CategoryEntity category) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCategory(category);
        addNewCategory(category);
    }

    public void deleteCalendarsListModel() {//jen test
        calendars.clear();//jen test
    }


    private void addNewCalendar(VCalendar calendar) {
        if (calendars != null)
            calendars.add(UIBeanEnhancer.enhance(calendar));
    }

    private void addNewCategory(CategoryEntity category) {
        if (categories != null)
            categories.add(UIBeanEnhancer.enhance(category));
    }

    private PersitencyLayer getPersitencyLayer() {
        return persitencyLayer;
    }
}
