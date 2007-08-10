package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.VCalendar;
import cz.cvut.felk.timejuggler.db.entity.EventTask;

import java.awt.*;
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

    public List<EventTask> getEvents() throws PersistencyLayerException {
    	List<Category> cats = getCategories();

    	EventTask event1 = new EventTask();
    	event1.setSummary("MujEvent");
    	event1.setDescription("Pokus");
    	
    	//event1.addCategory(...)
        return Arrays.asList(event1);
    }
    
    public void saveOrUpdateCalendar(VCalendar calendar) throws PersistencyLayerException {
        //do nothing
    }

	public void saveOrUpdateCategory(Category category) throws PersistencyLayerException{
	}
	
    public List<Category> getCategories() throws PersistencyLayerException {
        return Arrays.asList(new Category("Svatky", Color.BLUE), new Category("Ukoly", Color.GREEN), new Category("Skola"), new Category("PARy"));
    }
}
