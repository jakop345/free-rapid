package cz.omnicom.ermodeller.conc2obj.interfaces;

/**
 * Interface describes objects, which produce SQL script in String.
 */
public interface SubObjProducer {
    /**
     * Returns SQL string countTabs intended from left side.
     *
     * @param int number of intendations
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs);
}
