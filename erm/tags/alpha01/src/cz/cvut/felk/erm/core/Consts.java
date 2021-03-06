package cz.cvut.felk.erm.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 * @author Ladislav Vitasek
 */
public class Consts {

    /**
     * Product name
     */
    public static final String PRODUCT = "ER Modeller";

    /**
     * Version
     */
    public static final String VERSION = "5.0";

    /**
     * verze a jmeno programu
     */
    public static final String APPVERSION = PRODUCT + " " + VERSION;

    /**
     * uzivatelske jmeno pro pristup do databaze
     */
    public static final String DB_USERNAME = "erm";

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
    public static final int ONE_INSTANCE_SERVER_PORT = 28878;

    /**
     * URL adresa, kam se posilaji reporty o chybach
     */
    public static final String WEBURL_SUBMIT_ERROR = "http://wordrider.net/error.php";
    public static final int DEFAULT_RECENT_FILES_MAX_COUNT = 10;
    //   public static final String WEBURL_SUBMIT_ERROR = "http://localhost/wordrider/web/error.php";

    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
