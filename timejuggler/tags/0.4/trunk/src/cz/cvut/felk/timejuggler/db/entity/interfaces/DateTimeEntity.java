package cz.cvut.felk.timejuggler.db.entity.interfaces;

import cz.cvut.felk.timejuggler.db.DatabaseException;

import java.util.Date;

/**
 * @author Jan Struz
 * @version 0.1
 *
 * interface
 */

public interface DateTimeEntity extends EntityElement {
    public void setStartDate(Date startDate);

    public void setCreated(Date created);

    public void setLastModified(Date lastModified);

    public void setPeriods(PeriodsEntity periods);

    public Date getStartDate();

    public Date getCreated();

    public Date getLastModified();

    public PeriodsEntity getPeriods() throws DatabaseException;

    public void setDistinctDates(DistinctDatesEntity distinctDates);

    public DistinctDatesEntity getDistinctDates();

    public void setEndDate(Date endDate);

    public void setEndDate(DurationEntity dur);

    public Date getEndDate();

    /* DB
    public void setPeriodsId(int periodsId);

    public void setDistinctDatesId(int distinctDatesId);

    public int getPeriodsId();

    public int getDistinctDatesId();
    */

}
