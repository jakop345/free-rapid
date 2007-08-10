package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.EventTask;

import java.util.List;

/**
 * @author Vity
 */
public interface PersitencyLayer {

    List<VCalendar> getCalendars() throws PersistencyLayerException;

	List<EventTask> getEvents() throws PersistencyLayerException;
	
    void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException;

	void saveOrUpdateCategory(Category category) throws PersistencyLayerException;
	
    List<Category> getCategories() throws PersistencyLayerException;
}
