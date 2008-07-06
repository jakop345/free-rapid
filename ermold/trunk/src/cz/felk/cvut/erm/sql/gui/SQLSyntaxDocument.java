package cz.felk.cvut.erm.sql.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

class SQLSyntaxDocument extends DefaultStyledDocument {
    private DefaultStyledDocument doc;
    private Element rootElement;

    private boolean multiLineComment;
    private MutableAttributeSet normalStyle;
    private MutableAttributeSet keywordStyle;
    private MutableAttributeSet commentStyle;
    private MutableAttributeSet dataTypeStyle;

    private Set<String> keywords;
    private static final String QUOTE_DELIMITERS = "\"'";
    private MutableAttributeSet lineCommentStyle;
    private MutableAttributeSet numberStyle;
    private Set<String> types;

    public SQLSyntaxDocument() {
        doc = this;
        final Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Object driver = drivers.nextElement();
            System.out.println("driver = " + driver);
        }
        rootElement = doc.getDefaultRootElement();
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

        normalStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(normalStyle, Color.black);

        commentStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(commentStyle, Color.gray);
        StyleConstants.setItalic(commentStyle, true);

        lineCommentStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(lineCommentStyle, new Color(128, 128, 128));

        numberStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(numberStyle, Color.BLUE);

        keywordStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(keywordStyle, new Color(0, 0, 128));
        StyleConstants.setBold(keywordStyle, true);

        dataTypeStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(dataTypeStyle, new Color(102, 0, 0));
        StyleConstants.setBold(dataTypeStyle, true);

        keywords = new HashSet<String>();
        types = new HashSet<String>();
        types.add("BFILE");
        types.add("BINARY_INTEGER");
        types.add("BLOB");
        types.add("CHAR");
        types.add("CLOB");
        types.add("DATE");
        types.add("DECIMAL");
        types.add("DOUBLE");
        types.add("FLOAT");
        types.add("INT ");
        types.add("INTEGER");
        types.add("LONG");
        types.add("MLSLABEL");
        types.add("NCHAR");
        types.add("NCLOB");
        types.add("NUMBER");
        types.add("NVARCHAR2");
        types.add("PLS_INTEGER");
        types.add("PRECISION");
        types.add("RAW");
        types.add("ROWID");
        types.add("SMALLINT");
        types.add("TIMESTAMP");
        types.add("UROWID");
        types.add("VARCHAR");
        types.add("VARCHAR2");
        types.add("VARRAY");


        keywords.add("ACCESS");
        keywords.add("ADD");
        keywords.add("ADD");
        keywords.add("ALL");
        keywords.add("ALTER");
        keywords.add("AND");
        keywords.add("AS");
        keywords.add("ASC");
        keywords.add("ASC");
        keywords.add("BY");
        keywords.add("CASCADE");
        keywords.add("CHAR");
        keywords.add("COLUMN");
        keywords.add("CONSTRAINT");
        keywords.add("CONSTRAINTS");
        keywords.add("CREATE");
        keywords.add("CURRENT");
        keywords.add("DEFAULT");
        keywords.add("DELETE");
        keywords.add("DESC");
        keywords.add("DISTINCT");
        keywords.add("DROP");
        keywords.add("ELSE");
        keywords.add("EXISTS");
        keywords.add("FOR");
        keywords.add("FOREIGN");
        keywords.add("FROM");
        keywords.add("GRANT");
        keywords.add("GROUP");
        keywords.add("INDEX");
        keywords.add("INSERT");
        keywords.add("INTERSECT");
        keywords.add("INTO");
        keywords.add("IS");
        keywords.add("KEY");
        keywords.add("LEVEL");
        keywords.add("LIKE");
        keywords.add("MAXEXTENTS");
        keywords.add("MODE BY");
        keywords.add("MODIFY");
        keywords.add("NESTED");
        keywords.add("NOT");
        keywords.add("NULL");
        keywords.add("OBJECT");
        keywords.add("OF");
        keywords.add("OFFLINE");
        keywords.add("OPTION");
        keywords.add("OR");
        keywords.add("ORDER");
        keywords.add("PCTFREE");
        keywords.add("PRIOR");
        keywords.add("PRIMARY");
        keywords.add("PRIVILEGES");
        keywords.add("PUBLIC");
        keywords.add("REF");
        keywords.add("REPLACE");
        keywords.add("REFERENCES");
        keywords.add("RESOURCE");
        keywords.add("REVOKE");
        keywords.add("ROW");
        keywords.add("ROWNUM");
        keywords.add("ROWS");
        keywords.add("SELECT");
        keywords.add("SESSION");
        keywords.add("SET");
        keywords.add("SIZE");
        keywords.add("START");
        keywords.add("SYNONYM");
        keywords.add("TABLE");
        keywords.add("THEN");
        keywords.add("TRIGGER");
        keywords.add("TYPE");
        keywords.add("UID");
        keywords.add("UNION");
        keywords.add("UNIQUE");
        keywords.add("USER");
        keywords.add("VALUES");
        keywords.add("WHENEVER");
        keywords.add("WHERE");
        keywords.add("WITH");

    }

    /*
      *  Override to apply syntax highlighting after the document has been updated
      */
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        if (str.equals("{"))
            str = addMatchingBrace(offset);

        super.insertString(offset, str, a);
        processChangedLines(offset, str.length());
    }

    /*
      *  Override to apply syntax highlighting after the document has been updated
      */
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);
        processChangedLines(offset, 0);
    }

    /*
      *  Determine how many lines have been changed,
      *  then apply highlighting to each line
      */
    public void processChangedLines(int offset, int length)
            throws BadLocationException {
        String content = doc.getText(0, doc.getLength());

        //  The lines affected by the latest document update

        int startLine = rootElement.getElementIndex(offset);
        int endLine = rootElement.getElementIndex(offset + length);

        //  Make sure all commentStyle lines prior to the start line are commented
        //  and determine if the start line is still in a multi line commentStyle

        setMultiLineComment(commentLinesBefore(content, startLine));

        //  Do the actual highlighting

        for (int i = startLine; i <= endLine; i++) {
            applyHighlighting(content, i);
        }

        //  Resolve highlighting to the next end multi line delimiter

        if (isMultiLineComment())
            commentLinesAfter(content, endLine);
        else
            highlightLinesAfter(content, endLine);
    }

    /*
      *  Highlight lines when a multi line commentStyle is still 'open'
      *  (ie. matching end delimiter has not yet been encountered)
      */
    private boolean commentLinesBefore(String content, int line) {
        int offset = rootElement.getElement(line).getStartOffset();

        //  Start of commentStyle not found, nothing to do

        int startDelimiter = lastIndexOf(content, getStartDelimiter(), offset - 2);

        if (startDelimiter < 0)
            return false;

        //  Matching start/end of commentStyle found, nothing to do

        int endDelimiter = indexOf(content, getEndDelimiter(), startDelimiter);

        if (endDelimiter < offset & endDelimiter != -1)
            return false;

        //  End of commentStyle not found, highlight the lines

        doc.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, commentStyle, false);
        return true;
    }

    /*
      *  Highlight commentStyle lines to matching end delimiter
      */
    private void commentLinesAfter(String content, int line) {
        int offset = rootElement.getElement(line).getEndOffset();

        //  End of commentStyle not found, nothing to do

        int endDelimiter = indexOf(content, getEndDelimiter(), offset);

        if (endDelimiter < 0)
            return;

        //  Matching start/end of commentStyle found, commentStyle the lines

        int startDelimiter = lastIndexOf(content, getStartDelimiter(), endDelimiter);

        if (startDelimiter < 0 || startDelimiter <= offset) {
            doc.setCharacterAttributes(offset, endDelimiter - offset + 1, commentStyle, false);
        }
    }

    /*
      *  Highlight lines to start or end delimiter
      */
    private void highlightLinesAfter(String content, int line)
            throws BadLocationException {
        int offset = rootElement.getElement(line).getEndOffset();

        //  Start/End delimiter not found, nothing to do

        int startDelimiter = indexOf(content, getStartDelimiter(), offset);
        int endDelimiter = indexOf(content, getEndDelimiter(), offset);

        if (startDelimiter < 0)
            startDelimiter = content.length();

        if (endDelimiter < 0)
            endDelimiter = content.length();

        int delimiter = Math.min(startDelimiter, endDelimiter);

        if (delimiter < offset)
            return;

        //	Start/End delimiter found, reapply highlighting

        int endLine = rootElement.getElementIndex(delimiter);

        for (int i = line + 1; i < endLine; i++) {
            Element branch = rootElement.getElement(i);
            Element leaf = doc.getCharacterElement(branch.getStartOffset());
            AttributeSet as = leaf.getAttributes();

            if (as.isEqual(commentStyle))
                applyHighlighting(content, i);
        }
    }

    /*
      *  Parse the line to determine the appropriate highlighting
      */
    private void applyHighlighting(String content, int line)
            throws BadLocationException {
        int startOffset = rootElement.getElement(line).getStartOffset();
        int endOffset = rootElement.getElement(line).getEndOffset() - 1;

        int lineLength = endOffset - startOffset;
        int contentLength = content.length();

        if (endOffset >= contentLength)
            endOffset = contentLength - 1;

        //  check for multi line comments
        //  (always set the commentStyle attribute for the entire line)

        if (endingMultiLineComment(content, startOffset, endOffset)
                || isMultiLineComment()
                || startingMultiLineComment(content, startOffset, endOffset)) {
            doc.setCharacterAttributes(startOffset, endOffset - startOffset + 1, commentStyle, false);
            return;
        }

        //  set normalStyle attributes for the line

        doc.setCharacterAttributes(startOffset, lineLength, normalStyle, true);

        //  check for single line commentStyle

        int index = content.indexOf(getSingleLineDelimiter(), startOffset);

        if ((index > -1) && (index < endOffset)) {
            doc.setCharacterAttributes(index, endOffset - index + 1, lineCommentStyle, false);
            endOffset = index - 1;
        }

        //  check for tokens

        checkForTokens(content, startOffset, endOffset);
    }

    /*
      *  Does this line contain the start delimiter
      */
    private boolean startingMultiLineComment(String content, int startOffset, int endOffset)
            throws BadLocationException {
        int index = indexOf(content, getStartDelimiter(), startOffset);

        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(true);
            return true;
        }
    }

    /*
      *  Does this line contain the end delimiter
      */
    private boolean endingMultiLineComment(String content, int startOffset, int endOffset)
            throws BadLocationException {
        int index = indexOf(content, getEndDelimiter(), startOffset);

        if ((index < 0) || (index > endOffset))
            return false;
        else {
            setMultiLineComment(false);
            return true;
        }
    }

    /*
      *  We have found a start delimiter
      *  and are still searching for the end delimiter
      */
    private boolean isMultiLineComment() {
        return multiLineComment;
    }

    private void setMultiLineComment(boolean value) {
        multiLineComment = value;
    }

    /*
      *	Parse the line for tokens to highlight
      */
    private void checkForTokens(String content, int startOffset, int endOffset) {
        while (startOffset <= endOffset) {
            //  skip the delimiters to find the start of a new token

            while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
                if (startOffset < endOffset)
                    startOffset++;
                else
                    return;
            }

            //  Extract and process the entire token

            if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1)))
                startOffset = getQuoteToken(content, startOffset, endOffset);
            else
                startOffset = getOtherToken(content, startOffset, endOffset);
        }
    }

    /*
      *
      */
    private int getQuoteToken(String content, int startOffset, int endOffset) {
        String quoteDelimiter = content.substring(startOffset, startOffset + 1);
        String escapeString = getEscapeString(quoteDelimiter);

        int index;
        int endOfQuote = startOffset;

        //  skip over the escape quotes in this quote

        index = content.indexOf(escapeString, endOfQuote + 1);

        while ((index > -1) && (index < endOffset)) {
            endOfQuote = index + 1;
            index = content.indexOf(escapeString, endOfQuote);
        }

        // now find the matching delimiter

        index = content.indexOf(quoteDelimiter, endOfQuote + 1);

        if ((index < 0) || (index > endOffset))
            endOfQuote = endOffset;
        else
            endOfQuote = index;

        doc.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, dataTypeStyle, false);

        return endOfQuote + 1;
    }

    /*
      *
      */
    private int getOtherToken(String content, int startOffset, int endOffset) {
        int endOfToken = startOffset + 1;

        while (endOfToken <= endOffset) {
            if (isDelimiter(content.substring(endOfToken, endOfToken + 1)))
                break;

            endOfToken++;
        }

        String token = content.substring(startOffset, endOfToken).toUpperCase();

        if (isNumber(token)) {
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, numberStyle, false);
        }


        if (isKeyword(token)) {
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keywordStyle, false);
//            try {
//                doc.replace(startOffset, endOfToken - startOffset, token, keywordStyle);
//            } catch (BadLocationException e) {
//                e.printStackTrace();
//            }
        }

        if (isDataType(token)) {
            doc.setCharacterAttributes(startOffset, endOfToken - startOffset, dataTypeStyle, false);
//            try {
//                doc.replace(startOffset, endOfToken - startOffset, token, keywordStyle);
//            } catch (BadLocationException e) {
//                e.printStackTrace();
//            }
        }

        return endOfToken + 1;
    }

    private boolean isNumber(String token) {
        final char[] chars = token.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

    /*
      *  Assume the needle will the found at the start/end of the line
      */
    private int indexOf(String content, String needle, int offset) {
        int index;

        while ((index = content.indexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();

            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index + 1;
        }

        return index;
    }

    /*
      *  Assume the needle will the found at the start/end of the line
      */
    private int lastIndexOf(String content, String needle, int offset) {
        int index;

        while ((index = content.lastIndexOf(needle, offset)) != -1) {
            String text = getLine(content, index).trim();

            if (text.startsWith(needle) || text.endsWith(needle))
                break;
            else
                offset = index - 1;
        }

        return index;
    }

    private String getLine(String content, int offset) {
        int line = rootElement.getElementIndex(offset);
        Element lineElement = rootElement.getElement(line);
        int start = lineElement.getStartOffset();
        int end = lineElement.getEndOffset();
        return content.substring(start, end - 1);
    }

    /*
      *  Override for other languages
      */
    protected boolean isDelimiter(String character) {
        String operands = ";,:{}()[]+-/%<=>!&|^~*";

        return Character.isWhitespace(character.charAt(0)) ||
                operands.indexOf(character) != -1;
    }

    /*
      *  Override for other languages
      */
    protected boolean isQuoteDelimiter(String character) {

        return QUOTE_DELIMITERS.indexOf(character) >= 0;
    }

    /*
      *  Override for other languages
      */
    protected boolean isKeyword(String token) {
        return keywords.contains(token);
    }


    /*
      *  Override for other languages
      */
    protected boolean isDataType(String token) {
        return types.contains(token);
    }

    /*
      *  Override for other languages
      */
    protected String getStartDelimiter() {
        return "/*";
    }

    /*
      *  Override for other languages
      */
    protected String getEndDelimiter() {
        return "*/";
    }

    /*
      *  Override for other languages
      */
    protected String getSingleLineDelimiter() {
        return "--";
    }

    /*
      *  Override for other languages
      */
    protected String getEscapeString(String quoteDelimiter) {
        return "\\" + quoteDelimiter;
    }

    /*
      *
      */
    protected String addMatchingBrace(int offset) throws BadLocationException {
        StringBuffer whiteSpace = new StringBuffer();
        int line = rootElement.getElementIndex(offset);
        int i = rootElement.getElement(line).getStartOffset();

        while (true) {
            String temp = doc.getText(i, 1);

            if (temp.equals(" ") || temp.equals("\t")) {
                whiteSpace.append(temp);
                i++;
            } else
                break;
        }

        return "{\n" + whiteSpace.toString() + "\t\n" + whiteSpace.toString() + "}";
    }


    public static void main(String a[]) {

        EditorKit editorKit = new StyledEditorKit() {
            public Document createDefaultDocument() {
                return new SQLSyntaxDocument();
            }
        };

        final JEditorPane edit = new JEditorPane();
        edit.setEditorKitForContentType("text/java", editorKit);
        edit.setContentType("text/java");
//		edit.setEditorKit(new StyledEditorKit());
//		edit.setDocument(new SyntaxDocument());

        JButton button = new JButton("Load SyntaxDocument.java");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    //FileInputStream fis = new FileInputStream( "SyntaxDocument.java" );
                    FileInputStream fis = new FileInputStream("c:\\skola\\skola2\\!semestry\\8.semestr\\sql\\create_obj.sql");
                    edit.read(fis, null);
                    edit.requestFocus();
                }
                catch (Exception e2) {
                    //
                }
            }
        });

        JFrame frame = new JFrame("Syntax Highlighting");
        frame.getContentPane().add(new JScrollPane(edit));
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.setVisible(true);
    }
}
