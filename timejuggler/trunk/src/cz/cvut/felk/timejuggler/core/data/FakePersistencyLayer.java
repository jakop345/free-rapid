package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fake vrstva poskytujici testovaci data.
 * @author Vity
 */
class FakePersistencyLayer implements PersitencyLayer {
    FakePersistencyLayer() {
    }

    public List<VCalendar> getCalendars() throws PersistencyLayerException {
        return Arrays.asList(new VCalendar("Franta"), new VCalendar("Pepa"), new VCalendar("Antonin"));
    }

    public List<EventTask> getEvents() throws PersistencyLayerException {
//    	List<CategoryEntity> cats = getCategories();

        EventTask event1 = new EventTask();
        event1.setSummary("MujEvent");
        event1.setDescription("Pokus");

        //event1.addCategory(...)
        return Arrays.asList(event1);
    }

    public void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException {
        //do nothing
    }


    public List<CategoryEntity> getCategories() throws PersistencyLayerException {
        final List<CategoryEntity> list = new ArrayList<CategoryEntity>();
        list.add(new Category("Svatky", Color.BLUE));
        list.add(new Category("Ukoly", Color.GREEN));
        list.add(new Category("Skola"));
        list.add(new Category("PARy"));
        return list;
    }

    public CategoryEntity getNewCategory() {
        return new Category();
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        final Category cat = (Category) category;
        cat.setChanged(false);
    }
}
