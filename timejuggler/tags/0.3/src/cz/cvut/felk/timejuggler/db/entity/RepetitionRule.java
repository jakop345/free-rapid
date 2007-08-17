package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.DatabaseException;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.RepetitionRuleEntity;

import java.util.logging.Logger;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 12-V-2007 18:38:50 Hotovo
 */
public class RepetitionRule extends DbElement implements RepetitionRuleEntity {
    private final static Logger logger = Logger.getLogger(RepetitionRule.class.getName());
    //TODO : Pridat operace pro manipulaci s opakovanim + konstanty

    /* Frequency */
    public static final int SECONDLY = 0;
    public static final int MINUTELY = 1;
    public static final int HOURLY = 2;
    public static final int DAILY = 3;
    public static final int WEEKLY = 4;
    public static final int MONTHLY = 5;
    public static final int YEARLY = 6;

    private int frequency;
    private int interval;
    private int repeat;
    private int weekStart; //TODO: default value = MONDAY

    private String byHour = "";
    private String byWeekNo = "";
    private String byYearDay = "";
    private String bySetPosition = "";
    private String byMonth = "";
    private String byMinute = "";
    private String byMonthDay = "";

    private int repetitionRulesID;
    /*
     public ExceptionRules m_ExceptionRules;
     public RepetitionRules m_RepetitionRules;
     public NumberList m_NumberList;
     public ByDayOfWeek m_ByDayOfWeek;
     */

    public RepetitionRule() {

    }

    public void store() {
    }

    /**
     * Method saveOrUpdate
     * @param template
     */
    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        logger.info("RepetitionRule: freq=" + frequency);
        if (getId() > 0) {
            Object params[] = {repetitionRulesID, frequency, interval, repeat,
                    weekStart, byHour, byWeekNo, byYearDay,
                    bySetPosition, byMonth, byMinute, byMonthDay, getId()};
            String updateQuery = "UPDATE RepetitionRule SET repetitionRulesID=?,frequency=?,interval=?,repeat=?,weekStart=?,byHour=?,byWeekNo=?,byYearDay=?,bySetPosition=?,byMonth=?,byMinute=?,byMonthDay=?) WHERE repetitionRuleID = ? ";
            try {
                template.executeUpdate(updateQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        } else {
            Object params[] = {repetitionRulesID, frequency, interval, repeat,
                    weekStart, byHour, byWeekNo, byYearDay,
                    bySetPosition, byMonth, byMinute, byMonthDay};
            String insertQuery = "INSERT INTO RepetitionRule (repetitionRulesID,frequency,interval,repeat,weekStart,byHour,byWeekNo,byYearDay,bySetPosition,byMonth,byMinute,byMonthDay) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                template.executeUpdate(insertQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(template.getGeneratedId());
        }
    }

    /**
     * Method delete
     * @param template
     */
    public void delete(TimeJugglerJDBCTemplate template) {
        if (getId() > 0) {
            String deleteQuery = "DELETE FROM RepetitionRule WHERE repetitionRuleID = ? ";
            Object params[] = {getId()};
            try {
                template.executeUpdate(deleteQuery, params);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
            setId(-1);
        }
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * @param newVal
     */
    public void setFrequency(int newVal) {
        frequency = newVal;
    }

    public int getInterval() {
        return interval;
    }

    /**
     * @param newVal
     */
    public void setInterval(int newVal) {
        interval = newVal;
    }

    public int getRepeat() {
        return repeat;
    }

    /**
     * @param newVal
     */
    public void setRepeat(int newVal) {
        repeat = newVal;
    }

    public int getWeekStart() {
        return weekStart;
    }

    /**
     * @param newVal
     */
    public void setWeekStart(int newVal) {
        weekStart = newVal;
    }


    public void setRepetitionRulesID(int repetitionRulesID) {
        this.repetitionRulesID = repetitionRulesID;
    }

    public int getRepetitionRulesID() {
        return (this.repetitionRulesID);
    }


    public void setByHour(String byHour) {
        this.byHour = byHour;
    }

    public void setByWeekNo(String byWeekNo) {
        this.byWeekNo = byWeekNo;
    }

    public void setByYearDay(String byYearDay) {
        this.byYearDay = byYearDay;
    }

    public void setBySetPosition(String bySetPosition) {
        this.bySetPosition = bySetPosition;
    }

    public void setByMonth(String byMonth) {
        this.byMonth = byMonth;
    }

    public void setByMinute(String byMinute) {
        this.byMinute = byMinute;
    }

    public void setByMonthDay(String byMonthDay) {
        this.byMonthDay = byMonthDay;
    }

    public String getByHour() {
        return (this.byHour);
    }

    public String getByWeekNo() {
        return (this.byWeekNo);
    }

    public String getByYearDay() {
        return (this.byYearDay);
    }

    public String getBySetPosition() {
        return (this.bySetPosition);
    }

    public String getByMonth() {
        return (this.byMonth);
    }

    public String getByMinute() {
        return (this.byMinute);
    }

    public String getByMonthDay() {
		return (this.byMonthDay); 
	}
	

}