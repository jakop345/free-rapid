package cz.vity.freerapid.plugins.services.openload;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;

class AADecoder {
    private static final String BLOCK_START_MARKER = "(ﾟДﾟ)[ﾟεﾟ]+";
    private static final String HEX_HASH_MARKER = "(oﾟｰﾟo)+ ";
    private static final String[] BYTES = {"(c^_^o)", "(ﾟΘﾟ)", "((o^_^o) - (ﾟΘﾟ))", "(o^_^o)", "(ﾟｰﾟ)", "((ﾟｰﾟ) + (ﾟΘﾟ))", "((o^_^o) +(o^_^o))", "((ﾟｰﾟ) + (o^_^o))", "((ﾟｰﾟ) + (ﾟｰﾟ))", "((ﾟｰﾟ) + (ﾟｰﾟ) + (ﾟΘﾟ))", "(ﾟДﾟ) .ﾟωﾟﾉ", "(ﾟДﾟ) .ﾟΘﾟﾉ", "(ﾟДﾟ) ['c']", "(ﾟДﾟ) .ﾟｰﾟﾉ", "(ﾟДﾟ) .ﾟДﾟﾉ", "(ﾟДﾟ) [ﾟΘﾟ]"};

    public String decode(String js) throws PluginImplementationException {
        js = js.trim().replace("/*´∇｀*/", "");

        Matcher matcher = PlugUtils.matcher("\\(ﾟДﾟ\\)\\[ﾟoﾟ\\]\\+ (.+?)\\(ﾟДﾟ\\)\\[ﾟoﾟ\\]\\)", js);
        if (!matcher.find()) {
            throw new PluginImplementationException("AAEncode block not found");
        }
        String data = matcher.group(1);

        StringBuilder out = new StringBuilder();
        while (!data.isEmpty()) {
            int index = data.indexOf(BLOCK_START_MARKER);
            if (index != 0) {
                throw new PluginImplementationException("AAEncode block start marker not found");
            }
            data = data.substring(BLOCK_START_MARKER.length());
            String encodedBlock;
            index = data.indexOf(BLOCK_START_MARKER);
            if (index == -1) {
                encodedBlock = data;
                data = "";
            } else {
                encodedBlock = data.substring(0, index);
                data = data.substring(encodedBlock.length());
            }

            int radix = 8;
            if (encodedBlock.indexOf(HEX_HASH_MARKER) == 0) {
                encodedBlock = encodedBlock.substring(HEX_HASH_MARKER.length());
                radix = 16;
            }
            String uniCodeNumString = decodeBlock(encodedBlock, radix);
            if (uniCodeNumString.isEmpty()) {
                throw new PluginImplementationException("AAEncode bad decoding for " + encodedBlock);
            }
            out.append((char) Integer.parseInt(uniCodeNumString, radix));
        }
        return out.toString();
    }

    private String decodeBlock(String encodedBlock, int radix) throws PluginImplementationException {
        for (int i = 0; i < BYTES.length; i++) {
            encodedBlock = encodedBlock.replace(BYTES[i], String.valueOf(i));
        }

        StringBuilder exp = new StringBuilder();
        ArrayList<String> expressions = new ArrayList<String>();
        int braceCount = 0;

        for (int i = 0; i < encodedBlock.length(); i++) {
            char c = encodedBlock.charAt(i);

            if (c == '(') {
                if (exp.length() > 0 && braceCount == 0) {
                    expressions.add(exp.toString());
                    exp.setLength(0);
                }
                braceCount++;
                if (!Character.isWhitespace(c)) {
                    exp.append(c);
                }
            } else if (c == ')') {
                braceCount--;
                if (!Character.isWhitespace(c)) {
                    exp.append(c);
                }
            } else {
                if (!Character.isWhitespace(c)) {
                    exp.append(c);
                } else if (i > 0 && c == ' ' && encodedBlock.charAt(i - 1) == '+') {
                    // block end
                    if (braceCount == 0) {
                        if (exp.length() > 0) {
                            expressions.add(exp.toString());
                            exp.setLength(0);
                        }
                    }

                }
            }
        }
        if (exp.length() > 0) {
            expressions.add(exp.toString());
            exp.setLength(0);
        }

        StringBuilder ret = new StringBuilder();
        for (String expression : expressions) {
            expression = expression.trim().replaceAll("\\+$", "");
            try {
                ret.append(Integer.toString((int) eval(expression), radix));
            } catch (Exception e) {
                throw new PluginImplementationException("AAEncode failed to evaluate", e);
            }
        }
        return ret.toString();
    }

    // http://stackoverflow.com/questions/3422673/evaluating-a-math-expression-given-in-string-form
    // public domain
    private static double eval(final String str) {
        return new Object() {
            int pos = -1;
            int ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `~` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus
                if (eat('~')) return ~(int) Math.floor(parseFactor());

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}