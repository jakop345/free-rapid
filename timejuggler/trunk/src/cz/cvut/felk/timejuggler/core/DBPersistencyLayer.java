package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;

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

    public void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException {
        dbStore.saveOrUpdate(calendar);
    }

    public List<Category> getCategories() throws PersistencyLayerException {
        return new ArrayList<Category>(); //TODO implementovat volani na dbstore
    }
}
