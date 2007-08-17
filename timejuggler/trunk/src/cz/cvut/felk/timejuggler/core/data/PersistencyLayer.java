package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.io.File;
import java.util.List;

/**
 * @author Vity
 */
public interface PersistencyLayer {

    List<VCalendarEntity> getCalendars() throws PersistencyLayerException;

    List<EventTaskEntity> getEventsByCalendar(VCalendarEntity cal) throws PersistencyLayerException;

    List<EventTaskEntity> getToDosByCalendar(VCalendarEntity cal) throws PersistencyLayerException;

    List<EventTask> getEvents() throws PersistencyLayerException;

    VCalendarEntity importICS(File filePath) throws PersistencyLayerException;

    void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException;

    void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException;

    void saveOrUpdateEventTask(EventTaskEntity event) throws PersistencyLayerException;

    List<CategoryEntity> getCategories() throws PersistencyLayerException;

    VCalendarEntity getNewCalendar();

    EventTaskEntity getNewEvent();

    EventTaskEntity getNewToDo();

    CategoryEntity getNewCategory();

    void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException;

    void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException;

    void removeEventTask(EventTaskEntity eventEntity) throws PersistencyLayerException;

    void init() throws PersistencyLayerException;
}
