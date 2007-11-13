package net.wordrider.files.ti68kformat;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Vity
 */
public class FastTextFileReader extends FastTIFileReader {
    private final static Pattern pictureRegexp = Pattern.compile("^( |\f)&P\\p{Alpha}\\w{0,7}\\\\\\p{Alpha}\\w{0,7}$", Pattern.MULTILINE);

    protected String[] getSupportedHeaders() {
        return TIFileConstants.SUPPORTED_FORMATS_TEXT;
    }


    public void showPictures() {
        String searchString = "";
        try {
            byteContent[7] = '\r';
            searchString = new String(byteContent, 7, byteContent.length - 9, "ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Matcher matcher = pictureRegexp.matcher(searchString);
        while (matcher.find()) {
            System.out.println(matcher.group().trim());
        }
    }

    protected byte getDataType() {
        return TIFileConstants.DATATYPE_TEXT;
    }


    public String getContent() {
        return null;
    }
}
