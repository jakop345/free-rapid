package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fake vrstva poskytujici testovaci data.
 * @author Vity
 */
class FakePersistencyLayer implements PersistencyLayer {
    private final List<CategoryEntity> fakeListCategories;
    private final List<VCalendarEntity> fakeListCalendars;
    private static int categoryId = 1;//emulace idcka

    FakePersistencyLayer() {
        fakeListCategories = new ArrayList<CategoryEntity>();
        fakeListCalendars = new ArrayList<VCalendarEntity>();
    }

    public List<VCalendarEntity> getCalendars() throws PersistencyLayerException {
        if (!fakeListCalendars.isEmpty())
            return fakeListCalendars;
        fakeListCalendars.add(new VCalendar("Franta", ++categoryId));
        fakeListCalendars.add(new VCalendar("Pepa", ++categoryId));
        fakeListCalendars.add(new VCalendar("Antonin", ++categoryId));
        return fakeListCalendars;
    }

    public List<EventTask> getEvents() throws PersistencyLayerException {
//    	List<CategoryEntity> cats = getCategories();

        EventTask event1 = new EventTask();
        event1.setSummary("MujEvent");
        event1.setDescription("Pokus");

        //event1.addCategory(...)
        return Arrays.asList(event1);
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        //do nothing
    }


    public List<CategoryEntity> getCategories() throws PersistencyLayerException {
        if (!fakeListCategories.isEmpty())
            return fakeListCategories;
        fakeListCategories.add(new Category("Svatky", Color.BLUE, ++categoryId));//jednoducha simulace databaze
        fakeListCategories.add(new Category("Ukoly", Color.GREEN, ++categoryId));
        fakeListCategories.add(new Category("Skola", null, ++categoryId));
        fakeListCategories.add(new Category("PARy", null, ++categoryId));
        return fakeListCategories;
    }

    public CategoryEntity getNewCategory() {
        return new Category();
    }

    public VCalendarEntity getNewCalendar() {
        return new VCalendar();
    }

    public void saveOrUpdateCategory(CategoryEntity category) throws PersistencyLayerException {
        final Category cat = (Category) category;
        if (!fakeListCategories.contains(category)) {
            fakeListCategories.add(category);
            cat.setId(++categoryId);
        }
        cat.setChanged(false);
    }

    public void removeCategory(CategoryEntity categoryEntity) throws PersistencyLayerException {
        fakeListCategories.remove(categoryEntity);
    }


    public void removeCalendar(VCalendarEntity calendarEntity) throws PersistencyLayerException {
        fakeListCalendars.remove(calendarEntity);
    }
}
