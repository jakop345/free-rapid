package cz.cvut.felk.timejuggler.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 * @author Vity
 */
public class Consts {
    /**
     * verze a jmeno programu
     */
    public static final String APPVERSION = "TimeJuggler 0.1";

    /**
     * uzivatelske jmeno pro pristup do databaze
     */
    public static final String DB_USERNAME = "timejuggler";
    /**
     * uzivatelske heslo pro pristup do databaze
     */
    public static final String DB_PASSWORD = DB_USERNAME;

    /**
     * cesta k souboru pro nastaveni logovani - debug
     */
    public static final String LOGDEBUG = "logdebug.properties";

    /**
     * cesta k souboru pro nastaveni logovani - default info
     */
    public static final String LOGDEFAULT = "logdefault.properties";


    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
