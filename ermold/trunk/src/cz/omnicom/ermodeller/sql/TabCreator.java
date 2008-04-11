package cz.omnicom.ermodeller.sql;

/**
 * Creates intends added to left side of SQL string.
 *
 * @see cz.omnicom.ermodeller.sql.interfaces.SubSQLProducer#createSubSQL
 */
public class TabCreator {
    /**
     * @param countTabs int
     * @return java.lang.String
     */
    public final static String getTabs(int countTabs) {
        String result = "";
        for (int i = 0; i < countTabs; i++) {
            result += "      ";
        }
        return result;
    }
}
