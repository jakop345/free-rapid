package cz.felk.cvut.erm.sql.interfaces;

/**
 * Interface describes objects, which produce SQL script in String.
 */
public interface SubSQLProducer {
    /**
     * Returns SQL string countTabs intended from left side.
     *
     * @param int number of intendations
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs);
}
