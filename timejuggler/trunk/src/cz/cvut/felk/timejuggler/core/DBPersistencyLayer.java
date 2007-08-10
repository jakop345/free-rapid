package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.EventTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vity
 */
public class DBPersistencyLayer implements PersitencyLayer {
    private DbDataStore dbStore;

    DBPersistencyLayer() {
        dbStore = new DbDataStore();
    }

    public List<VCalendar> getCalendars() throws PersistencyLayerException {
        return dbStore.getCalendars();
    }
    
    public List<EventTask> getEvents() throws PersistencyLayerException {
    	return null; //...
    }

    public void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException {
        dbStore.saveOrUpdate(calendar);
    }
    public void saveOrUpdateCategory(Category category) throws PersistencyLayerException {
        dbStore.saveOrUpdate(category);
    }

    public List<Category> getCategories() throws PersistencyLayerException {
        return dbStore.getCategories();
    }
}
