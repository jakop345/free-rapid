package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vity
 */
class DBPersistencyLayer implements PersistencyLayer {
    private DbDataStore dbStore;

    DBPersistencyLayer() {
        dbStore = new DbDataStore();
    }

    public List<VCalendarEntity> getCalendars() throws PersistencyLayerException {
        return dbStore.getCalendars();
    }

    public List<EventTask> getEvents() throws PersistencyLayerException {
        return new ArrayList(); //nikdy nevracet null, bud prazdnej seznam nebo vyjimka
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        dbStore.saveOrUpdate((VCalendar) calendar);
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        dbStore.saveOrUpdate((Category) category);
    }

    public List<CategoryEntity> getCategories() throws PersistencyLayerException {
        return dbStore.getCategories();
    }

    public CategoryEntity getNewCategory() {
        return new Category();
    }

    public void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException {
        dbStore.delete((Category) categoryEntity);
    }

    public VCalendarEntity getNewCalendar() {
        return new VCalendar();
    }

    public void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException {
        dbStore.delete((VCalendar) calendarEntity);
    }
}
