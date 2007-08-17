package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 27-IV-2007 22:45:44
 * <p/>
 * trida reprezentujici casovy usek zacatek: startDate, konec: endDate, nebo delka trvani (duration) Hotovo
 */
public class Period extends DbElement {
    private final static Logger logger = Logger.getLogger(Period.class.getName());

    private Timestamp endDate;
    private Timestamp startDate;

    private RepetitionRules repetitionRules;    //rrule - pravidla pro opakovani
    private RepetitionRules exceptionRules;        //exrule - pravidla pro opakovani, kdy se udalost nekona (vyjimky)
    private DistinctDates exceptionDates;        //exdate - data, kdy se udalost nekona (vyjimky)

    private int repetitionRulesId;
    private int exceptionRulesId;
    private int exceptionDatesId;

    private Duration duration;    // delka trvani udalosti
    private int periodsId;

    public Period() {
    }

    public Period(Date startDate, Date endDate) {
        this.startDate = (startDate == null ? null : new Timestamp(startDate.getTime()));
        this.endDate = (endDate == null ? null : new Timestamp(endDate.getTime()));
    }

    public Period(Date startDate, Duration duration) {
        this.startDate = (startDate == null ? null : new Timestamp(startDate.getTime()));
        this.duration = duration;
    }

    public Period(Date startDate) {
        this.startDate = new Timestamp(startDate.getTime());
    }


    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (duration != null) {
            logger.info("Period: duration != null ");
            duration.saveOrUpdate(template);
        }
        if (repetitionRules != null) {
            repetitionRules.saveOrUpdate(template);
        }
        if (exceptionRules != null) {
            exceptionRules.saveOrUpdate(template);
        }
        if (exceptionDates != null) {
            exceptionDates.saveOrUpdate(template);
        }

        if (getId() > 0) {
            Object params[] = {startDate, endDate,
                    (repetitionRules == null ? null : repetitionRules.getId()),
                    (exceptionRules == null ? null : exceptionRules.getId()),
                    (duration == null ? null : duration.getId()), periodsId,
                    (exceptionDates == null ? null : exceptionDates.getId()), getId()};
            String updateQuery = "UPDATE Period SET startDate=?,endDate=?,rrule=?,exrule=?,durationID=?,periodsID=?,distinctDatesID=?) WHERE periodID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            Object params[] = {startDate, endDate,
                    repetitionRules == null ? null : repetitionRules.getId(),
                    exceptionRules == null ? null : exceptionRules.getId(),
                    duration == null ? null : duration.getId(), periodsId,
                    exceptionDates == null ? null : exceptionDates.getId()};
            String insertQuery = "INSERT INTO Period (startDate,endDate,rrule,exrule,durationID,periodsID,distinctDatesID) VALUES (?,?,?,?,?,?,?)";
            try {
                template.executeUpdate(insertQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(template.getGeneratedId());
            logger.info("Period: generated ID:" + getId());
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (repetitionRules != null) repetitionRules.delete(template);
        if (exceptionRules != null) exceptionRules.delete(template);
        if (exceptionDates != null) exceptionDates.delete(template);

        if (getId() > 0) {
            String deleteQuery = "DELETE FROM Period WHERE periodID = ? ";
            Object params[] = {getId()};
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }


    public void setEndDate(Date endDate) {
        this.endDate = (endDate == null ? null : new Timestamp(endDate.getTime()));
    }

    public void setStartDate(Date startDate) {
        this.startDate = (startDate == null ? null : new Timestamp(startDate.getTime()));
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Date getEndDate() {
        return (this.endDate == null ? null : new Date(this.endDate.getTime()));
    }

    public Date getStartDate() {
        return (this.startDate == null ? null : new Date(this.startDate.getTime()));
    }

    public Duration getDuration() {
        return (this.duration);
    }


    public void setPeriodsId(int periodsId) {
        this.periodsId = periodsId;
    }

    public int getPeriodsId() {
        return (this.periodsId);
    }


    public void setRepetitionRules(RepetitionRules repetitionRules) {
        this.repetitionRules = repetitionRules;
    }

    public void setExceptionRules(RepetitionRules exceptionRules) {
        this.exceptionRules = exceptionRules;
    }

    public void setExceptionDates(DistinctDates exceptionDates) {
        this.exceptionDates = exceptionDates;
    }

    public void setRepetitionRulesId(int repetitionRulesId) {
        this.repetitionRulesId = repetitionRulesId;
    }

    public void setExceptionRulesId(int exceptionRulesId) {
        this.exceptionRulesId = exceptionRulesId;
    }

    public void setExceptionDatesId(int exceptionDatesId) {
        this.exceptionDatesId = exceptionDatesId;
    }

    public RepetitionRules getRepetitionRules() {
        //TODO : SELECT FROM RepetitionRule TODO TODO TODO TODO TODO TODO
        return (this.repetitionRules);
    }

    public RepetitionRules getExceptionRules() {
        //TODO : SELECT FROM RepetitionRule
        return (this.exceptionRules);
    }

    public DistinctDates getExceptionDates() {
        //TODO : SELECT FROM DistinctDate
        return (this.exceptionDates);
    }

    public int getRepetitionRulesId() {
        return (this.repetitionRulesId);
    }

    public int getExceptionRulesId() {
        return (this.exceptionRulesId);
    }

    public int getExceptionDatesId() {
        return (this.exceptionDatesId);
	}

}