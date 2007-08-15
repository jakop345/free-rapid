package cz.cvut.felk.timejuggler.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 * @author Vity
 */
public class Consts {
    /**
     * verze a jmeno programu
     */
    public static final String APPVERSION = "TimeJuggler 0.4";

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
     * relativni cesta k souboru v adresari aplikace, kde lze nalezt default databazi
     */
    public static final String DB_DEFAULT = "db_init/db.zip";

    /**
     * relativni cesta k souboru v adresari aplikace, kde lze nalezt default databazi
     */
    public static final String DB_LOCALDIR = "db";


    /**
     * jmeno adresare v resources, kde je sound
     */
    public static final String SOUNDS_DIR = "sound";
    /**
     * cesta k adresari s look&feely
     */
    public static final String LAFSDIR = "lookandfeel";

    /**
     * cesta k properties fajlu
     */
    public static final String LAFSDIRFILE = LAFSDIR + "/lookandfeels.properties";
    /**
     * port na kterem bezi aplikace, aby se zamezilo dvojimu spousteni - kvuli konexeni k databazi
     */
    public static final int ONE_INSTANCE_SERVER_PORT = 28879;


    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
