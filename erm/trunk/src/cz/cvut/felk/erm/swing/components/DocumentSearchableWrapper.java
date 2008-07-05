package cz.cvut.felk.erm.swing.components;

import cz.cvut.felk.erm.utilities.LogUtils;
import org.jdesktop.swingx.Searchable;

import javax.swing.text.*;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 *         Based on JXEditorPane's DocumentSearchable
 */
class DocumentSearchableWrapper implements Searchable {
    private final static Logger logger = Logger.getLogger(DocumentSearchableWrapper.class.getName());

    private final JTextComponent component;

    public DocumentSearchableWrapper(final JTextComponent component) {
        this.component = component;
    }

    private Document getDocument() {
        return component.getDocument();
    }

    private Caret getCaret() {
        return component.getCaret();
    }

    public int search(String searchString) {
        return search(searchString, -1);
    }

    public int search(String searchString, int columnIndex) {
        return search(searchString, columnIndex, false);
    }

    public int search(String searchString, int columnIndex, boolean backward) {
        Pattern pattern = null;
        if (!isEmpty(searchString)) {
            pattern = Pattern.compile(searchString, 0);
        }
        return search(pattern, columnIndex, backward);
    }

    /**
     * checks if the searchString should be interpreted as empty.
     * here: returns true if string is null or has zero length.
     * <p/>
     * TODO: This should be in a utility class.
     *
     * @param searchString
     * @return true if string is null or has zero length
     */
    protected boolean isEmpty(String searchString) {
        return (searchString == null) || searchString.length() == 0;
    }

    public int search(Pattern pattern) {
        return search(pattern, -1);
    }

    public int search(Pattern pattern, int startIndex) {
        return search(pattern, startIndex, false);
    }

    int lastFoundIndex = -1;

    MatchResult lastMatchResult;
    String lastRegEx;

    /**
     * @return start position of matching string or -1
     */
    public int search(Pattern pattern, final int startIndex,
                      boolean backwards) {
        if ((pattern == null)
                || (getDocument().getLength() == 0)
                || ((startIndex > -1) && (getDocument().getLength() < startIndex))) {
            updateStateAfterNotFound();
            return -1;
        }

        int start = startIndex;
        if (maybeExtendedMatch(startIndex)) {
            if (foundExtendedMatch(pattern, start)) {
                return lastFoundIndex;
            }
            start++;
        }

        int length;
        if (backwards) {
            start = 0;
            if (startIndex < 0) {
                length = getDocument().getLength() - 1;
            } else {
                length = -1 + startIndex;
            }
        } else {
            // start = startIndex + 1;
            if (start < 0)
                start = 0;
            length = getDocument().getLength() - start;
        }
        Segment segment = new Segment();

        try {
            getDocument().getText(start, length, segment);
        } catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
        }

        Matcher matcher = pattern.matcher(segment.toString());
        MatchResult currentResult = getMatchResult(matcher, !backwards);
        if (currentResult != null) {
            updateStateAfterFound(currentResult, start);
        } else {
            updateStateAfterNotFound();
        }
        return lastFoundIndex;

    }

    /**
     * Search from same startIndex as the previous search.
     * Checks if the match is different from the last (either
     * extended/reduced) at the same position. Returns true
     * if the current match result represents a different match
     * than the last, false if no match or the same.
     *
     * @param pattern
     * @param start
     * @return true if the current match result represents a different
     *         match than the last, false if no match or the same.
     */
    private boolean foundExtendedMatch(Pattern pattern, int start) {
        // JW: logic still needs cleanup...
        if (pattern.pattern().equals(lastRegEx)) {
            return false;
        }
        int length = getDocument().getLength() - start;
        Segment segment = new Segment();

        try {
            getDocument().getText(start, length, segment);
        } catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
        }
        Matcher matcher = pattern.matcher(segment.toString());
        MatchResult currentResult = getMatchResult(matcher, true);
        if (currentResult != null) {
            // JW: how to compare match results reliably?
            // the group().equals probably isn't the best idea...
            // better check pattern?
            if ((currentResult.start() == 0) &&
                    (!lastMatchResult.group().equals(currentResult.group()))) {
                updateStateAfterFound(currentResult, start);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the startIndex is a candidate for trying a re-match.
     *
     * @param startIndex
     * @return true if the startIndex should be re-matched, false if not.
     */
    private boolean maybeExtendedMatch(final int startIndex) {
        return (startIndex >= 0) && (startIndex == lastFoundIndex);
    }

    /**
     * @param currentResult
     * @param offset
     * @return the start position of the selected text
     */
    private int updateStateAfterFound(MatchResult currentResult, final int offset) {
        int end = currentResult.end() + offset;
        int found = currentResult.start() + offset;
        component.select(found, end);
        getCaret().setSelectionVisible(true);
        lastFoundIndex = found;
        lastMatchResult = currentResult;
        lastRegEx = ((Matcher) lastMatchResult).pattern().pattern();
        return found;
    }

    /**
     * @param matcher
     * @param useFirst whether or not to return after the first match is found.
     * @return <code>MatchResult</code> or null
     */
    private MatchResult getMatchResult(Matcher matcher, boolean useFirst) {
        MatchResult currentResult = null;
        while (matcher.find()) {
            currentResult = matcher.toMatchResult();
            if (useFirst) break;
        }
        return currentResult;
    }

    /**
     */
    private void updateStateAfterNotFound() {
        lastFoundIndex = -1;
        lastMatchResult = null;
        lastRegEx = null;
        component.setCaretPosition(component.getSelectionEnd());
    }

}