package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.InitiateDatabaseException;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.utilities.DbHelper;
import net.fortuna.ical4j.data.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
class DBPersistencyLayer implements PersistencyLayer {
    private final static Logger logger = Logger.getLogger(DBPersistencyLayer.class.getName());
    private DbDataStore dbStore;


    DBPersistencyLayer() {
        dbStore = new DbDataStore();
    }

    public void init() throws PersistencyLayerException {
        //TODO: kod pro inicializaci databaze - Presunout
        DbHelper dbHelper = DbHelper.getInstance();
        if (!dbHelper.isDatabasePresent()) {
            try {
                dbHelper.localDbCreate();
            } catch (InitiateDatabaseException e) {
                throw new PersistencyLayerException("Fatal error during initialing database", e);
            }
        }
    }


    public List<VCalendarEntity> getCalendars() throws PersistencyLayerException {
        return dbStore.getCalendars();
    }

    public List<EventTaskEntity> getEventsByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
        return dbStore.getEventsByCalendar((VCalendar) cal);
    }

    public List<EventTaskEntity> getToDosByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
        return dbStore.getToDosByCalendar((VCalendar) cal);
    }

    public List<EventTask> getEvents() throws PersistencyLayerException {
        return new ArrayList(); //nikdy nevracet null, bud prazdnej seznam nebo vyjimka
    }

    public VCalendarEntity importICS(File file) throws PersistencyLayerException {
        //TODO: zpracovani vyjimek.?!? - zpracujeme ve vyssi vrstve
        try {
            return dbStore.importICS(file);
        } catch (IOException e) {
            throw new PersistencyLayerException(e);
        } catch (ParserException e) {
            throw new PersistencyLayerException(e);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException(e);
        }
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        try {
            dbStore.saveOrUpdate((VCalendar) calendar);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during save or update Calendar", e);
        }
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        try {
            dbStore.saveOrUpdate((Category) category);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during save or update Category", e);
        }
    }

    public void saveOrUpdateEventTask(EventTaskEntity event) throws PersistencyLayerException {
        try {
            dbStore.saveOrUpdate((EventTask) event);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during save or update Event Task", e);
        }
    }

    public List<CategoryEntity> getCategories() throws PersistencyLayerException {
        return dbStore.getCategories();
    }

    public CategoryEntity getNewCategory() {
        return new Category();
    }

    public void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException {
        try {
            dbStore.delete((Category) categoryEntity);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during removing Category", e);
        }
    }

    public VCalendarEntity getNewCalendar() {
        return new VCalendar();
    }

    public EventTaskEntity getNewEvent() {
        return new EventTask();
    }

    public EventTaskEntity getNewToDo() {
        return new EventTask(true);
    }

    public void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException {
        try {
            dbStore.delete((VCalendar) calendarEntity);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during removing Calendar", e);
        }
    }

    public void removeEventTask(EventTaskEntity eventEntity) throws PersistencyLayerException {
        try {
            dbStore.delete((EventTask) eventEntity);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException("Error during removing Event Task", e);
        }
    }
}
