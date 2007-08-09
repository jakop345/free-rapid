package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;

import java.util.List;

/**
 * @author Vity
 */
public interface PersitencyLayer {

    List<VCalendar> getCalendars() throws PersistencyLayerException;

    void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException;

    List<Category> getCategories() throws PersistencyLayerException;
}
