package cz.cvut.felk.timejuggler.db.entity.interfaces;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.entity.DistinctDates;
import cz.cvut.felk.timejuggler.db.entity.Duration;
import cz.cvut.felk.timejuggler.db.entity.Periods;

import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.1
 */

public interface DateTimeEntity extends EntityElement {
    public void setStartDate(Date startDate);

    public void setCreated(Date created);

    public void setLastModified(Date lastModified);

    public void setPeriods(Periods periods);

    public Date getStartDate();

    public Date getCreated();

    public Date getLastModified();

    public Periods getPeriods() throws DatabaseException;

    public void setDistinctDates(DistinctDates distinctDates);

    public DistinctDates getDistinctDates();

    public void setEndDate(Date endDate);

    public void setEndDate(Duration dur);

    public Date getEndDate();

    public void setPeriodsId(int periodsId);

    public void setDistinctDatesId(int distinctDatesId);

    public int getPeriodsId();

    public int getDistinctDatesId();

}
