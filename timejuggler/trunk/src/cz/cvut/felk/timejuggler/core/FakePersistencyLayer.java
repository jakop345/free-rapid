package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vity
 */
public class FakePersistencyLayer implements PersitencyLayer {
    FakePersistencyLayer() {
    }

    public List<VCalendar> getCalendars() throws PersistencyLayerException {
        return Arrays.asList(new VCalendar("Franta"), new VCalendar("Pepa"), new VCalendar("Antonin"));
    }

    public void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException {
        //do nothing
    }

    public List<Category> getCategories() throws PersistencyLayerException {
        return Arrays.asList(new Category("Svatky"), new Category("Ukoly"), new Category("Antonin"));
    }
}
