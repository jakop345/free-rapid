package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;

import java.util.List;

/**
 * @author Vity
 */
public interface PersistencyLayer {

    List<VCalendar> getCalendars() throws PersistencyLayerException;

    List<EventTask> getEvents() throws PersistencyLayerException;

    void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException;

    void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException;

    List<CategoryEntity> getCategories() throws PersistencyLayerException;

    CategoryEntity getNewCategory();

    void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException;
}
