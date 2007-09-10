package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PeriodsEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.PeriodEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:42:40 Hotovo
 *
 * trida reprezentujici seznam Period (casovych useku)
 * ma interface
 */
public class Periods extends DbElement implements Iterable<PeriodEntity>, PeriodsEntity {
    private final static Logger logger = Logger.getLogger(Periods.class.getName());

    private List<PeriodEntity> periods;

    public Periods() {
        periods = new ArrayList<PeriodEntity>();
    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException  {
        if (getId() > 0) {
            //bez Update
        } else {
            logger.info("Database - Insert: Periods[]...");
            String insertQuery = "INSERT INTO Periods (periodsID) VALUES (DEFAULT)";
            template.executeUpdate(insertQuery, null);
            setId(template.getGeneratedId());
        }

        for (PeriodEntity period : periods) {
            ((Period)period).setPeriodsId(getId());
            logger.info("Periods: generated ID:" + getId());
            ((Period)period).saveOrUpdate(template);
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        for (PeriodEntity period : periods) {
            ((Period)period).delete(template);
        }

        if (getId() > 0) {
            String deleteQuery = "DELETE FROM Periods WHERE periodsID = ? ";
            Object params[] = {getId()};
            template.executeUpdate(deleteQuery, params);
            setId(-1);
        }
    }

    /**
     * Method addPeriod
     */
    public void addPeriod(PeriodEntity period) {
        periods.add(period);
    }

    /**
     * Method iterator
     * @return
     */
    public Iterator<PeriodEntity> iterator() {
        return periods.iterator();
    }


    @Deprecated
    public List<PeriodEntity> getPeriods() {
        return periods;
    }
}