package cz.omnicom.ermodeller.conc2obj;

/**
 * Creates intends added to left side of SQL string.
 *
 * @see cz.omnicom.ermodeller.sql.SubSQLProducer#createSubSQL
 */
class TabCreatorObj {
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
