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
    	try {
    		return dbStore.getCalendars();
	    }
	    catch (DatabaseException ex) {
	    	throw new PersistencyLayerException(ex);
	    }
    }

    public List<EventTaskEntity> getEventsByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
    	try {
    		return dbStore.getEventsByCalendar((VCalendar) cal);
	    }
	    catch (DatabaseException ex) {
	    	throw new PersistencyLayerException(ex);
	    }
        
    }

    public List<EventTaskEntity> getToDosByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
    	try {
    		return dbStore.getToDosByCalendar((VCalendar) cal);
    	}       
        catch (DatabaseException ex) {
	    	throw new PersistencyLayerException(ex);
	    }
    }

    public List<EventTaskEntity> getAllEventsFromSelectedCalendars() throws PersistencyLayerException {
        //TODO nebylo by lepsi tohle jednim selectem???
        try {
        	//TODO: tady taky vadi Exception..
	        final List<EventTaskEntity> events = new ArrayList<EventTaskEntity>();
	        final List<VCalendarEntity> list = dbStore.getCalendars();

	        for (VCalendarEntity calendarEntity : list) {
	            if (calendarEntity.isActive()) {
	                final List<EventTaskEntity> calendarEvents = dbStore.getEventsByCalendar((VCalendar) calendarEntity);
	                if (calendarEvents != null)
	                    events.addAll(calendarEvents);
	            }
	        }
	        return events;
	    }
	    catch (DatabaseException ex) {
	    	throw new PersistencyLayerException(ex);
	    }
        

    }

    public VCalendarEntity importICS(File file) throws PersistencyLayerException {
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
            throw new PersistencyLayerException(e);
        }
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        try {
            dbStore.saveOrUpdate((Category) category);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException(e);
        }
    }

    public void saveOrUpdateEventTask(EventTaskEntity event) throws PersistencyLayerException {
        try {
            dbStore.saveOrUpdate((EventTask) event);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException(e);
        }
    }

    public List<CategoryEntity> getCategories() throws PersistencyLayerException {
    	try {
    		return dbStore.getCategories();
	    }
	    catch (DatabaseException ex) {
	    	throw new PersistencyLayerException(ex);
	    }
        
    }

    public CategoryEntity getNewCategory() {
        return new Category();
    }

    public void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException {
        try {
            dbStore.delete((Category) categoryEntity);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException(e);
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
            throw new PersistencyLayerException(e);
        }
    }

    public void removeEventTask(EventTaskEntity eventEntity) throws PersistencyLayerException {
        try {
            dbStore.delete((EventTask) eventEntity);
        } catch (DatabaseException e) {
            throw new PersistencyLayerException(e);
        }
    }
}
