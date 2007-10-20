/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core.data;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.value.ValueHolder;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.io.File;
import java.util.Date;
import java.util.List;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private ArrayListModel<CategoryEntity> categories;
    private ArrayListModel<VCalendarEntity> calendars;
    private ArrayListModel<EventTaskEntity> events;
    PersistencyLayer persistencyLayer;
    private boolean categoriesInit = false;
    private boolean calendarsInit = false;
    private boolean eventsInit = false;
    private Object newCalendar;
    private ValueHolder currentDateHolder;
    private Object newEvent;

    public DataProvider() {
        categories = new ArrayListModel<CategoryEntity>();
        calendars = new ArrayListModel<VCalendarEntity>();
        events = new ArrayListModel<EventTaskEntity>();
        currentDateHolder = new ValueHolder(new Date());
    }

    public void init() throws PersistencyLayerException {
        persistencyLayer = PersistencyLayerFactory.getInstance().getDefaultPersitencyLayer();
        persistencyLayer.init();
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
    public void synchronizeCategoriesFromList(List<CategoryEntity> items) throws PersistencyLayerException {
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
        resetCategories();
        //TODO update eventu, kterych se ty kategorie tykaji
    }

    private void resetCategories() throws PersistencyLayerException {
        categories.clear();//smaze vsechny kategorie v globalnim seznamu
        categoriesInit = false;
        getCategoriesListModel();
    }


    public synchronized ArrayListModel<CategoryEntity> getCategoriesListModel() throws PersistencyLayerException {
        if (!categoriesInit) {
            categories.addAll(getPersitencyLayer().getCategories());
            categoriesInit = true;
        }
        return categories;
    }

    public synchronized ArrayListModel<EventTaskEntity> getEventsListModel() throws PersistencyLayerException {
        if (!eventsInit) {
            events.addAll(getPersitencyLayer().getAllEventsFromSelectedCalendars());
            eventsInit = true;
        }
        return events;
    }

    public synchronized ArrayListModel<VCalendarEntity> getCalendarsListModel() throws PersistencyLayerException {
        if (!calendarsInit) {
            calendars.addAll(getPersitencyLayer().getCalendars());
            calendarsInit = true;
        }
        return calendars;
    }

    public synchronized void addCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCalendar(calendar);
        calendars.add(calendar);
        resetEvents();//prozatim
    }

    public synchronized void addCategory(CategoryEntity category) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCategory(category);
        categories.add(category);
    }


    public void importCalendarFromICS(File file) throws PersistencyLayerException {
        final VCalendarEntity calendarEntity = getPersitencyLayer().importICS(file);
        calendars.add(calendarEntity);
    }

    private void resetCalendars() throws PersistencyLayerException {
        calendars.clear();//smaze vsechny kategorie v globalnim seznamu
        calendarsInit = false;
        getCalendarsListModel();
    }

    private void resetEvents() throws PersistencyLayerException {
        events.clear();//smaze vsechny kategorie v globalnim seznamu
        eventsInit = false;
        getEventsListModel();
    }

    public VCalendarEntity getNewCalendar() {
        return getPersitencyLayer().getNewCalendar();
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        getPersitencyLayer().saveOrUpdateCalendar(calendar);
        calendars.fireContentsChanged(calendars.indexOf(calendar));
    }

    public void removeCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        getPersitencyLayer().removeCalendar(calendar);
        calendars.remove(calendar);
        resetEvents();
    }

    public void updateCalendarActive(VCalendarEntity calendarEntity) throws PersistencyLayerException {
        saveOrUpdateCalendar(calendarEntity);
        resetEvents();
    }


    private PersistencyLayer getPersitencyLayer() {
        return persistencyLayer;
    }

    public ValueHolder getCurrentDateHolder() {
        return currentDateHolder;
    }

    public EventTaskEntity getNewEvent() {
        return getPersitencyLayer().getNewEvent();
    }

    public EventTaskEntity getNewToDo() {
        return getPersitencyLayer().getNewToDo();
    }
}
