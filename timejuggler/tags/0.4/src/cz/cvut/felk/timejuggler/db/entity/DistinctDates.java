package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DistinctDatesEntity;
import cz.cvut.felk.timejuggler.db.entity.interfaces.DistinctDateEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:46:34 Hotovo
 */
public class DistinctDates extends DbElement implements Iterable<DistinctDateEntity>, DistinctDatesEntity {
    private final static Logger logger = Logger.getLogger(DistinctDates.class.getName());

    private List<DistinctDateEntity> distinctDates;
    private int distinctDatesId;

    public DistinctDates() {
        distinctDates = new ArrayList<DistinctDateEntity>();
    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    @Override
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) throws DatabaseException {
        if (getId() > 0) {
            //bez Update
        } else {
            logger.info("Database - Insert: DistinctDates[]...");
            String insertQuery = "INSERT INTO DistinctDates (distinctDatesID) VALUES (DEFAULT)";
            template.executeUpdate(insertQuery, null);
            setId(template.getGeneratedId());
        }

        for (DistinctDateEntity date : distinctDates) {
            ((DistinctDate)date).setDistinctDatesId(getId());
            ((DistinctDate)date).saveOrUpdate(template);
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) throws DatabaseException {
        for (DistinctDateEntity date : distinctDates) {
            ((DistinctDate)date).delete(template);
        }
        if (getId() > 0) {
            String deleteQuery = "DELETE FROM DistinctDates WHERE distinctDatesID = ? ";
            Object params[] = {getId()};
            template.executeUpdate(deleteQuery, params);
            setId(-1);
        }
    }

    public void addDate(DistinctDateEntity date) {
        distinctDates.add(date);
    }

    public void setDistinctDatesId(int distinctDatesId) {
        this.distinctDatesId = distinctDatesId;
    }

    public int getDistinctDatesId() {
        return (this.distinctDatesId);
    }

    /**
     * Method iterator
     * @return
     */
    public Iterator<DistinctDateEntity> iterator() {
        return distinctDates.iterator();
    }

    /**
     * Method addDistinctDate
     */
    public void addDistinctDate(DistinctDateEntity date) {
        distinctDates.add(date);
    }

}