package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.util.List;

/**
 * @author Vity
 */
public interface PersistencyLayer {

    List<VCalendarEntity> getCalendars() throws PersistencyLayerException;

    List<EventTask> getEvents() throws PersistencyLayerException;

    void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException;

    void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException;

    List<CategoryEntity> getCategories() throws PersistencyLayerException;

    VCalendarEntity getNewCalendar();

    CategoryEntity getNewCategory();

    void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException;

    public void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException;

}
