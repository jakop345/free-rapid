package cz.felk.cvut.erm.sql;

/**
 * Creates intends added to left side of SQL string.
 *
 * @see cz.felk.cvut.erm.sql.interfaces.SubSQLProducer#createSubSQL
 */
public class TabCreator {
    /**
     * @param countTabs int
     * @return java.lang.String
     */
    public static String getTabs(int countTabs) {
        String result = "";
        for (int i = 0; i < countTabs; i++) {
            result += "      ";
        }
        return result;
    }
}
