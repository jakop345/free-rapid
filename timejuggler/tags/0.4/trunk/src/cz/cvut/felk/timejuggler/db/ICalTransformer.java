package cz.cvut.felk.timejuggler.db;

import cz.cvut.felk.timejuggler.db.entity.*;
import net.fortuna.ical4j.model.Property;
/**
 * @author Jan Struz
 * @version 0.1
 * 
 * Tato trida slouzi k trasformaci objektu formatu iCal4j na objekty datoveho modelu Timejuggler
 * - pouziti pri Importu a Exportu ICS
 */

public class ICalTransformer {
	
	private static ICalTransformer instance;
	
	private	ICalTransformer (){
	}
	
	public static ICalTransformer getInstance() {
		if (instance == null) {
			instance = new ICalTransformer();
		}
		return instance;
	}
	
	public Duration makeDuration(net.fortuna.ical4j.model.Dur dur) {
		Duration duration = new Duration();
		
		duration.setSeconds(dur.getSeconds());
		duration.setMinutes(dur.getMinutes());
		duration.setDays(dur.getDays());
		duration.setWeeks(dur.getWeeks());
		duration.setNegative(dur.isNegative());
		
		return duration;
	}
	
	public Duration makeDuration(net.fortuna.ical4j.model.property.Duration dur) {
		return (dur == null ? null : makeDuration(dur.getDuration()));
	}
	
	public Duration makeDuration(net.fortuna.ical4j.model.Property dur) {
		return (dur == null ? null : makeDuration(((net.fortuna.ical4j.model.property.Duration)dur).getDuration()));
	}
	
	public Period makePeriod(net.fortuna.ical4j.model.Period iperiod){
		Period period = new Period();
		
		period.setDuration( iperiod.getDuration() == null ? null : makeDuration(iperiod.getDuration()) );
		period.setStartDate( iperiod.getStart());
		period.setEndDate( iperiod.getEnd()) ;
		//period.setRepetitionRules( per.get )
		
		return period;
	}
	
}
