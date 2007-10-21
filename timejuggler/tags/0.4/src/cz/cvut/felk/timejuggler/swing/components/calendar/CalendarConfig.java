package cz.cvut.felk.timejuggler.swing.components.calendar;

/**
 * Trida poskytuje informace o nastaveni kalendare.
 * @author Jerry!
 */
public class CalendarConfig {
    /**
     * V kolik zacina den
     */
    int dayStartTime = 8;

    /**
     * V kolik den konci
     */
    int dayEndTime = 16;

    public int getDayEndTime() {
        return dayEndTime;
    }

    public void setDayEndTime(int dayEndTime) {
        this.dayEndTime = dayEndTime;
    }

    public int getDayStartTime() {
        return dayStartTime;
    }

    public void setDayStartTime(int dayStartTime) {
        this.dayStartTime = dayStartTime;
    }

}
