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

    private ArrayListModel<CategoryEntity> categories;
    private ArrayListModel<VCalendar> calendars;
    PersistencyLayer persistencyLayer;
    private boolean categoriesInit = false;
    private boolean calendarsInit = false;


    public DataProvider() {
        categories = new ArrayListModel<CategoryEntity>();
    }

    public void init() {
        persistencyLayer = PersistencyLayerFactory.getInstance().getDefaultPersitencyLayer();
        //persistencyLayer = new FakePersistencyLayer();
    }

    //utilita, bude pozdeji presunuto, az jestli tohle bude potreba a budu vedet kam ;-)
    @Deprecated
    private <C> ArrayListModel<C> enhanceToBeans(List<C> list) {
        final ArrayListModel<C> listModel = new ArrayListModel<C>();
        for (C item : list) {
            listModel.add(UIBeanEnhancer.enhance(item));
        }
        return listModel;
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

    private PersistencyLayer getPersitencyLayer() {
        return persistencyLayer;
    }
}
