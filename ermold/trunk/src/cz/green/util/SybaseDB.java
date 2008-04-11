package cz.green.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This type was created in VisualAge.
 */
public class SybaseDB {
    /**
     * SybaseDB constructor comment.
     */
    public SybaseDB() {
        super();
    }

    /**
     * This method was created in VisualAge.
     *
     * @param date Date
     * @return java.lang.String
     */
    public static String convertTimestamp(Date date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return formatDate.format(date);
    }
}
