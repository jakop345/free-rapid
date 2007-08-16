/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core.data;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.util.List;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private ArrayListModel<CategoryEntity> categories;
    private ArrayListModel<VCalendarEntity> calendars;
    PersistencyLayer persistencyLayer;
    private boolean categoriesInit = false;
    private boolean calendarsInit = false;


    public DataProvider() {
        categories = new ArrayListModel<CategoryEntity>();
        calendars = new ArrayListModel<VCalendarEntity>();
    }

    public void init() {
        persistencyLayer = PersistencyLayerFactory.getInstance().getDefaultPersitencyLayer();
        //persistencyLayer = new FakePersistencyLayer();
    }

    public CategoryEntity getNewCategory() {
        return getPersitencyLayer().getNewCategory();
    }

    /**
     * Zesynchronizuje seznam kategorii na novy seznam kategorii. Odstranene ze seznamu opravdu, nezmenene necha byt. Na
     * konci provede celkovy reload v databazi. //TBD melo by se to upravovat dualne?
     * @param items novy seznam kategorii
     */
    public void synchronizeCategoriesFromList(List<CategoryEntity> items) {
        final List<CategoryEntity> categoriesListModel = getCategoriesListModel();
        assert categoriesInit;
        for (CategoryEntity categoryEntity : categoriesListModel) {
            if (!items.contains(categoryEntity)) {
                getPersitencyLayer().removeCategory(categoryEntity);
            }
        }
        for (CategoryEntity item : items) {
            if (item.isChanged()) {
                getPersitencyLayer().saveOrUpdateCategory(item);
            }
        }
        categoriesListModel.clear();//smaze vsechny kategorie v globalnim seznamu
        categoriesInit = false;
        getCategoriesListModel();
    }


    public synchronized ArrayListModel<CategoryEntity> getCategoriesListModel() {
        if (!categoriesInit) {
            categories.addAll(getPersitencyLayer().getCategories());
            categoriesInit = true;
        }
        return categories;
    }

    public synchronized ArrayListModel<VCalendarEntity> getCalendarsListModel() throws PersistencyLayerException {
        if (!calendarsInit) {
            calendars.addAll(getPersitencyLayer().getCalendars());
        }
        return calendars;
    }

    public synchronized void addCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCalendar(calendar);
        calendars.add(calendar);
    }

    public synchronized void addCategory(CategoryEntity category) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCategory(category);
        categories.add(category);
    }

    public void deleteCalendarsListModel() {//jen test
        calendars.clear();//jen test
    }


    private PersistencyLayer getPersitencyLayer() {
        return persistencyLayer;
    }
}
