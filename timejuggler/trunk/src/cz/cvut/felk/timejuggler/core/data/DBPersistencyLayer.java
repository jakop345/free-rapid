package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.DbDataStore;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
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
        }
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        dbStore.saveOrUpdate((VCalendar) calendar);
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        dbStore.saveOrUpdate((Category) category);
    }

    public void saveOrUpdateEventTask(EventTaskEntity event) throws PersistencyLayerException {
        dbStore.saveOrUpdate((EventTask) event);
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

    public EventTaskEntity getNewEvent() {
        return new EventTask();
    }

    public EventTaskEntity getNewToDo() {
        return new EventTask(true);
    }

    public void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException {
        dbStore.delete((VCalendar) calendarEntity);
    }

    public void removeEventTask(EventTaskEntity eventEntity) throws PersistencyLayerException {
        dbStore.delete((EventTask) eventEntity);
    }
}
