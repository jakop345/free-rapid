package cz.cvut.felk.timejuggler.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 * @author Vity
 */
public class Consts {
    /**
     * uzivatelske jmeno pro pristup do databaze
     */
    public static final String DB_USERNAME = "timejuggler";
    /**
     * uzivatelske heslo pro pristup do databaze
     */
    public static final String DB_PASSWORD = DB_USERNAME;

    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
