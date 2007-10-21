package cz.cvut.felk.timejuggler.swing.components.calendar;

/**
 * Trida rika, jaky casovy usek je zobrazovan
 * @author Jerry!
 */
public enum CalendarView {
    DAY,
    WEEK,
    MULTI_WEEK,
    MONTH;

    public static CalendarView toCalendarView(int ordinal) {
        return values()[ordinal];
    }
}
