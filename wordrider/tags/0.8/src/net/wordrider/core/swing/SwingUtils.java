package net.wordrider.core.swing;

import java.util.regex.PatternSyntaxException;

/**
 * @author Vity
 */
public class SwingUtils {
    private SwingUtils() {
    }

    public static String getMessage(final PatternSyntaxException e, final String desc, final String desc2) {
        StringBuilder sb = new StringBuilder();
        sb.append(desc);
        final int index = e.getIndex();
        if (index >= 0) {
            sb.append(' ');
            sb.append(desc2);
            sb.append(' ');
            sb.append(index);
        }
        sb.append('\n');
        sb.append(e.getPattern());
        if (index >= 0) {
            sb.append('\n');
            for (int i = 0; i < index; i++) sb.append(' ');
            sb.append('^');
        }
        return sb.toString();
    }
}
