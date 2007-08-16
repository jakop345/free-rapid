package cz.cvut.felk.timejuggler.core.data;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.EventTask;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.VCalendarEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EventTaskEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Calendar;

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

	public List<EventTaskEntity> getEventsByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
		return new ArrayList();
	}
	
	public List<EventTaskEntity> getToDosByCalendar(VCalendarEntity cal) throws PersistencyLayerException {
		return new ArrayList();
	}

    public List<EventTask> getEvents() throws PersistencyLayerException {

		Calendar cal = Calendar.getInstance();
        EventTask event1 = new EventTask();
        event1.setSummary("MujEvent");
        event1.setDescription("Pokus");
        event1.setStartDate(cal.getTime());
        	cal.add(Calendar.DAY_OF_MONTH, 1);
        event1.setEndDate(cal.getTime());
		Category cat = new Category("Nova kategorie");		
        event1.addCategory(cat);
        
        return Arrays.asList(event1);
    }
    
    public VCalendarEntity importICS(String filePath) throws PersistencyLayerException{
   		return new VCalendar();
    }

    public void saveOrUpdateCalendar(VCalendarEntity calendar) throws PersistencyLayerException {
        //do nothing
    }

    public void saveOrUpdateEventTask(EventTaskEntity event) throws PersistencyLayerException {
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

	public EventTaskEntity getNewEvent(){
		return new EventTask();
	}
	
	public EventTaskEntity getNewToDo(){
		return new EventTask(true);
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
    
    public void removeEventTask(EventTaskEntity eventEntity) throws PersistencyLayerException {
    	//do nothing
    }
}
