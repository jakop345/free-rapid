package cz.cvut.felk.timejuggler.db;

/**
 * Vyjimka indikujici selhani inicializace defaultni databaze z db.zip
 * @author Jan Struz
 * @author Vity
 * @version 0.1
 * @created 14-IV-2007 16:20:17
 */
public class InitiateDatabaseException extends Exception {

    public InitiateDatabaseException(Throwable cause) {
        super(cause);
    }

    public InitiateDatabaseException(String message) {
        super(message);
    }

    public InitiateDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}