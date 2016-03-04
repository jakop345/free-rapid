package cz.vity.freerapid.swing.components;

import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 * @author tong2shot
 */
public class EditorPaneProxyDetector extends JEditorPane {
    private final static Logger logger = Logger.getLogger(EditorPaneProxyDetector.class.getName());
    private final static String EXAMPLE = "";
    private final static Pattern REGEXP_URL = Pattern.compile("(?i)^(\\$SOCKS\\$|SOCKS\\:)?((\\w*)(:(.*?))?@)?(.*?):(\\d{2,5})");
    private final static Pattern PROXY_REGEX_PATTERN = Pattern.compile("((\\w*)(:(.*?))?@)?(.*?):(\\d{2,5})");
    private final static Pattern SOCKS_REGEX_PATTERN = Pattern.compile("(?i)^(\\$SOCKS\\$|SOCKS\\:)");

    public EditorPaneProxyDetector() {
        super();
        final Action copyAction = this.getActionMap().get("copy");
        final Action pasteAction = this.getActionMap().get("paste");
        this.getInputMap().put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_INSERT), pasteAction);
        this.getInputMap().put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_INSERT), copyAction);
        final SyntaxDocument doc = new SyntaxDocument();
        this.setEditorKit(new StyledEditorKit() {
            public Document createDefaultDocument() {
                return doc;
            }
        });

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    if (isEditable())
                        setEditable(false);
                } else {
                    if (!isEditable())
                        setEditable(true);
                }

            }

            public void keyReleased(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    setEditable(true);
                }

            }
        });
        insertExampleProxy(doc);

        this.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                final JTextComponent source = (JTextComponent) e.getSource();
                if (EXAMPLE.equals(source.getText()))
                    source.setText("");
            }

            public void focusLost(FocusEvent e) {
                final JTextComponent source = (JTextComponent) e.getSource();
                if (source.getText().length() <= 0) {
                    insertExampleProxy((StyledDocument) source.getDocument());
                }
            }
        });
    }

    private void insertExampleProxy(StyledDocument doc) {
        SimpleAttributeSet example = new SimpleAttributeSet();
        StyleConstants.setForeground(example, Color.GRAY);
        try {
            doc.insertString(0, EXAMPLE, example);
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
        }
    }


    public void setProxies(List<String> list) {
        if (list.isEmpty())
            return;
        final Document document = this.getDocument();
        String s = "";
        try {
            s = document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            //ignore
        }
        final StringBuilder builder = new StringBuilder();
        s = s.trim();
        builder.append(s);
        if (s.length() > 0) {
            builder.append('\n');
        }
        for (String item : list) {
            builder.append(item).append('\n');
        }
        final String str = builder.toString();
//        if (str.length() > 0)
//            this.setText(""); //pro pripad ze je tam demo
        try {
            this.setText("");
            document.insertString(0, str, null);
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
        }
    }

    public void setProxies(String s) {
        final Pattern pattern = REGEXP_URL;
        final Matcher matcher = pattern.matcher(s);
        final List<String> list = new ArrayList<String>();
        while (validateProxy(s)) {
            final String e = matcher.group();
            if (!EXAMPLE.equals(e))
                list.add(e);
        }
        setProxies(list);
    }

    public List<String> getProxies() {
        final String s = this.getText();
        final String[] proxies = s.split("\n|\t|(?:  )");
        final List<String> proxySet = new ArrayList<String>(proxies.length);
        for (String proxy : proxies) {
            if (!proxy.trim().isEmpty()) {
                if (validateProxy(proxy)) {
                    proxySet.add(proxy);
                }
            }
        }
        return proxySet;
    }

    private boolean validateProxy(String strProxy) {
        final Matcher matcherSocks = SOCKS_REGEX_PATTERN.matcher(strProxy);
        if (matcherSocks.find()) {
            strProxy = strProxy.substring(matcherSocks.group(1).length());
        }
        final Matcher matcher = PROXY_REGEX_PATTERN.matcher(strProxy);
        return matcher.matches();
    }

    class SyntaxDocument extends DefaultStyledDocument {
        private DefaultStyledDocument doc;
        private Element rootElement;

        private MutableAttributeSet normal;
        private MutableAttributeSet keyword;
        private static final String DELIMITERS = "\n\t";


        public SyntaxDocument() {
            doc = this;
            rootElement = doc.getDefaultRootElement();
            putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");

            normal = new SimpleAttributeSet();
            StyleConstants.setForeground(normal, Color.RED);

            keyword = new SimpleAttributeSet();
            StyleConstants.setForeground(keyword, Color.BLUE);

        }

        /*
          *  Override to apply syntax highlighting after the document has been updated
          */
        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
            super.insertString(offset, str, a);
            if (str.equals(EXAMPLE))
                return;
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

            //  Do the actual highlighting

            for (int i = startLine; i <= endLine; i++) {
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


            doc.setCharacterAttributes(startOffset, lineLength, normal, true);

            checkForTokens(content, startOffset, endOffset);
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

                startOffset = getOtherToken(content, startOffset, endOffset);
            }
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

            String token = content.substring(startOffset, endOfToken);

            if (isKeyword(token)) {
                keyword.addAttribute("PROXY", token);
                doc.setCharacterAttributes(startOffset, endOfToken - startOffset, keyword, false);
            }

            return endOfToken + 1;
        }

        /*
          *  Override for other languages
          */
        protected boolean isDelimiter(String character) {

            return DELIMITERS.indexOf(character.charAt(0)) != -1;
//            return Character.isWhitespace(character.charAt(0)) ||
//                    DELIMITERS.indexOf(character) != -1;
        }


        /*
          *  Override for other languages
          */
        protected boolean isKeyword(String token) {
            //return keywords.contains(token);
            //System.out.println("token = " + token);
            return validateProxy(token);
        }
    }
}
