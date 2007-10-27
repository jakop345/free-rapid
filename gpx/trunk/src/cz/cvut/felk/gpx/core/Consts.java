package cz.cvut.felk.gpx.core;

/**
 * Trida pro ukladani globalnich konstant v aplikaci
 * @author Vity
 */
public class Consts {

    /**
     * Product name
     */
    public static final String PRODUCT = "GPXConverter";

    /**
     * Version
     */
    public static final String VERSION = "1.0";

    /**
     * verze a jmeno programu
     */
    public static final String APPVERSION = PRODUCT + " " + VERSION;

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
    public static final int ONE_INSTANCE_SERVER_PORT = 28879;

    /**
     * URL adresa, kam se posilaji reporty o chybach
     */
    public static final String WEBURL_SUBMIT_ERROR = "http://wordrider.net/error.php";
    //   public static final String WEBURL_SUBMIT_ERROR = "http://localhost/wordrider/web/error.php";

    /**
     * Od teto tridy se nebudou delat zadne instance
     */
    private Consts() {
    }
}
