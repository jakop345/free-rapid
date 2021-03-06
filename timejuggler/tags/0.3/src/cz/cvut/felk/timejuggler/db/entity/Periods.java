package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PeriodsEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:42:40 Hotovo
 */
public class Periods extends DbElement implements Iterable<Period>, PeriodsEntity {
    private final static Logger logger = Logger.getLogger(Periods.class.getName());

    private List<Period> periods;

    public Periods() {
        periods = new ArrayList<Period>();
    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            //bez Update
        } else {
            logger.info("Database - Insert: Periods[]...");
            String insertQuery = "INSERT INTO Periods (periodsID) VALUES (DEFAULT)";
            try {
                template.executeUpdate(insertQuery, null);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(template.getGeneratedId());
        }

        for (Period period : periods) {
            period.setPeriodsId(getId());
            logger.info("Periods: generated ID:" + getId());
            period.saveOrUpdate(template);
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        for (Period period : periods) {
            period.delete(template);
        }

        if (getId() > 0) {
            String deleteQuery = "DELETE FROM Periods WHERE periodsID = ? ";
            Object params[] = {getId()};
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }

    /**
     * Method addPeriod
     */
    public void addPeriod(Period period) {
        periods.add(period);
    }

    /**
     * Method iterator
     * @return
     */
    public Iterator<Period> iterator() {
        return periods.iterator();
    }


    @Deprecated
    public List<Period> getPeriods() {
        return periods;
    }
}