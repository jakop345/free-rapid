package net.wordrider.area;

import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;

import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class RiderStyles {
    public static final String TI89MAXLENGTHSTRING = "01234567890123456789012345";
    public static final String TI92MAXLENGTHSTRING = "0123456789012345678901234567890123456789";
    public final static Stroke DOTTED_STROKE = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{2f}, 0f);

    private static final String STYLE_INVERTED = "INVERT";
    private static final String STYLE_UNDERLINEDOTTED = "UNDERLINEDOTTED";
    public static final String STYLE_BOOKMARK = "BOOKMARK";
    private static final String STYLE_VECTOR = "VECTOR";
    private static final String STYLE_CONJUGATE = "CONJUGATE";
    private static final String STYLE_MATH = "MATH";
    private static final String STYLE_WORDWRAP = "WORDWRAP";
    public static final String STYLE_ALIGMENT_LEFT = "aligmentLeft";
    public static final String STYLE_ALIGMENT_RIGHT = "aligmentRight";
    public static final String STYLE_ALIGMENT_CENTER = "aligmentCenter";
    public static final String STYLE_FONT_MINI = "miniSize";
    public static final String STYLE_FONT_NORMAL = "normalSize";
    // --Commented out by Inspection (4.8.05 17:28): public static final String STYLE_FONT_NORMAL_EXPONENT_FREE = "normalSizeExpFree";
    public static final String STYLE_FONT_MAXI = "maxiSize";
    public static final String STYLE_MARGIN10 = "margin10";
    public static final String STYLE_MARGIN20 = "margin20";
    public static final String STYLE_MARGIN30 = "margin30";
    // --Commented out by Inspection (4.8.05 17:29): public static final String STYLE_MARGINX = "marginx";


    public static final int ALIGN_LEFT = StyleConstants.ALIGN_LEFT;
    public static final int ALIGN_RIGHT = StyleConstants.ALIGN_RIGHT;
    public static final int ALIGN_CENTER = StyleConstants.ALIGN_CENTER;
    public static final int SIZE_MINI = 11;
    public static final int SIZE_NORMAL = 13;
    public static final int SIZE_MAXI = 16;
    public static final int MARGIN_0 = 0;
    public static final int MARGIN_10 = 10;
    public static final int MARGIN_20 = 20;
    public static final int MARGIN_30 = 30;

    private static final int MAX_MARGIN_X = 240;


    public static final int SINGLE_LINE = SeparatorLine.SINGLE_LINE;
    public static final int DOUBLE_LINE = SeparatorLine.DOUBLE_LINE;
    public static final int EMPTY_LINE = 10;

    public static final String FONT_NAME = "Ti-92p.ttf";
    public static final String FONT_FAMILY = "Ti92Pluspc";

    private static final Font font = readDefaultFont();
    private static final Font maxiFont = font.deriveFont(Font.BOLD, 15F);
    public static final Font maxiFontNoBold = maxiFont.deriveFont(Font.PLAIN);
    public static Style normalStyle;
    public static Style miniStyle;
    public static Style maxiStyle;
    private static Style superscriptStyle;
    private static Style subscriptStyle;
    public static Style margin10Style;
    public static Style margin20Style;
    public static Style margin30Style;
    public static Style alignmentLeftStyle;
    public static Style alignmentRightStyle;
    public static Style alignmentCenteredStyle;
    private static Style underlineStyle;
    private static Style strikethroughStyle;
    private static Style underlineDottedStyle;
    private static Style basicStyle;
    private static Style invertStyle;
    public static Style mathStyle;
    private static Style vectorStyle;
    private static Style conjugateStyle;
    private static Style wordwrapStyle;
    public static SimpleAttributeSet bookmarkStyle;
    private static StyleContext styleContext;

    public static final byte P_UNDERLINE = 0;
    public static final byte P_VECTOR = 1;
    public static final byte P_INVERT = 2;
    public static final byte P_UNDERLINE_DOTTED = 3;
    public static final byte P_STRIKETHROUGHT = 4;
    public static final byte P_EXPOSANT = 5;
    public static final byte P_WORDWRAP = 6;
    public static final byte P_CONJUGATE = 7;
    public static final byte P_SUBSCRIPT = 8;
    private static final Object[] ATTRIBUTE_NAMES = {StyleConstants.Underline, STYLE_VECTOR, STYLE_INVERTED, STYLE_UNDERLINEDOTTED, StyleConstants.StrikeThrough, StyleConstants.Superscript, STYLE_WORDWRAP, STYLE_CONJUGATE, StyleConstants.Subscript};

    private static final Set<Integer> aditionalMarginX = new TreeSet<Integer>();
    private final static Logger logger = Logger.getLogger(RiderStyles.class.getName());

    private RiderStyles() {
    }


    private static Font readDefaultFont() {
        //check for fontfamily in system
        Font result;
        if ((result = new Font(FONT_FAMILY, Font.PLAIN, SIZE_NORMAL)).getFamily().equalsIgnoreCase(FONT_FAMILY))
            return result;
        InputStream stream = null;
        try {
            stream = (RiderStyles.class.getClassLoader()).getResourceAsStream(Consts.FONTDIR + FONT_NAME);
            if (stream != null) {
                result = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, SIZE_NORMAL);
            }
        } catch (FontFormatException e) {
            LogUtils.processException(logger, e);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
        return result;
    }

    public static boolean isCorrectFont() {
        return font.getFamily().equalsIgnoreCase(FONT_FAMILY);
    }

    private static StyleContext createDefaultStyles(final StyleContext sc) {
        final Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, font.getFamily());
        StyleConstants.setFontSize(defaultStyle, SIZE_NORMAL);
        basicStyle = sc.addStyle("basic", null);
        StyleConstants.setFontFamily(basicStyle, font.getFamily());
        //StyleConstants.setFontSize(basicStyle, SIZE_NORMAL);

        //StyleConstants.setLeftIndent(basicStyle,0);
        //StyleConstants.setAlignment(basicStyle,StyleConstants.ALIGN_LEFT);
        normalStyle = sc.addStyle(STYLE_FONT_NORMAL, basicStyle);
        StyleConstants.setFontSize(normalStyle, SIZE_NORMAL);
        StyleConstants.setBold(normalStyle, false);
        //StyleConstants.setSuperscript(normalStyle, false);
        miniStyle = sc.addStyle(STYLE_FONT_MINI, basicStyle);
        StyleConstants.setFontSize(miniStyle, SIZE_MINI);
        //     StyleConstants.setSuperscript(miniStyle, false);
        StyleConstants.setBold(miniStyle, false);
        maxiStyle = sc.addStyle(STYLE_FONT_MAXI, basicStyle);
        StyleConstants.setFontSize(maxiStyle, SIZE_MAXI);
        StyleConstants.setBold(maxiStyle, true);
        StyleConstants.setSuperscript(maxiStyle, false);
//        final Style exponentFree = sc.addStyle(STYLE_FONT_NORMAL_EXPONENT_FREE, basicStyle);
//        StyleConstants.setFontSize(exponentFree, SIZE_NORMAL);
//        StyleConstants.setSuperscript(exponentFree, false);
//        StyleConstants.setBold(exponentFree, false);

        superscriptStyle = sc.addStyle("superscript", miniStyle);
        StyleConstants.setSuperscript(superscriptStyle, true);
        StyleConstants.setSubscript(superscriptStyle, false);

        subscriptStyle = sc.addStyle("subscript", miniStyle);
        StyleConstants.setSubscript(subscriptStyle, true);
        StyleConstants.setSuperscript(subscriptStyle, false);
        //superscriptStyle.addAttributes(normalStyle);

        alignmentLeftStyle = sc.addStyle(STYLE_ALIGMENT_LEFT, basicStyle);
        StyleConstants.setLeftIndent(alignmentLeftStyle, 0);
        //setProperty(aligmentLeftStyle,STYLE_MATH, false);
        StyleConstants.setAlignment(alignmentLeftStyle, StyleConstants.ALIGN_LEFT);
        alignmentRightStyle = sc.addStyle(STYLE_ALIGMENT_RIGHT, alignmentLeftStyle);
        StyleConstants.setAlignment(alignmentRightStyle, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setLeftIndent(alignmentRightStyle, 0);
        alignmentCenteredStyle = sc.addStyle(STYLE_ALIGMENT_CENTER, alignmentLeftStyle);
        StyleConstants.setAlignment(alignmentCenteredStyle, StyleConstants.ALIGN_CENTER);
        StyleConstants.setLeftIndent(alignmentCenteredStyle, 0);

        margin10Style = sc.addStyle(STYLE_MARGIN10, alignmentLeftStyle);
        StyleConstants.setLeftIndent(margin10Style, 10);
        StyleConstants.setAlignment(margin10Style, StyleConstants.ALIGN_LEFT);
        margin20Style = sc.addStyle(STYLE_MARGIN20, alignmentLeftStyle);
        StyleConstants.setLeftIndent(margin20Style, 20);
        StyleConstants.setAlignment(margin20Style, StyleConstants.ALIGN_LEFT);
        margin30Style = sc.addStyle(STYLE_MARGIN30, alignmentLeftStyle);
        StyleConstants.setLeftIndent(margin30Style, 30);
        StyleConstants.setAlignment(margin30Style, StyleConstants.ALIGN_LEFT);

        underlineStyle = sc.addStyle("underline", alignmentLeftStyle); //underline cannot to rewrite underline-dotted styleCode on false
        //setProperty(underlineStyle, STYLE_UNDERLINEDOTTED, false);

        StyleConstants.setUnderline(underlineStyle, true);
        strikethroughStyle = sc.addStyle("strikethrough", basicStyle);
        StyleConstants.setStrikeThrough(strikethroughStyle, true);


        underlineDottedStyle = sc.addStyle("underlineDotted", basicStyle);
        //!dont delete! problems StyleConstants.setBackground(underlineDottedStyle, Color.WHITE); problems
        StyleConstants.setUnderline(underlineDottedStyle, false);
        setProperty(underlineDottedStyle, STYLE_UNDERLINEDOTTED);


        invertStyle = sc.addStyle("invert", basicStyle);
        setProperty(invertStyle, STYLE_INVERTED);

        mathStyle = sc.addStyle("math", normalStyle);
        setProperty(mathStyle, STYLE_MATH);
        mathStyle.addAttributes(alignmentLeftStyle);
        //StyleConstants.setLeftIndent(mathStyle, 0);
        //setProperty(aligmentLeftStyle,STYLE_MATH, false);
        //StyleConstants.setAlignment(mathStyle, StyleConstants.ALIGN_LEFT);
        //mathStyle = (Style) setProperty(mathStyle, STYLE_WORDWRAP, false);


        vectorStyle = sc.addStyle("vector", basicStyle);
        setProperty(vectorStyle, STYLE_VECTOR);
        setProperty(vectorStyle, STYLE_CONJUGATE, false);

        conjugateStyle = sc.addStyle("conjugate", basicStyle);
        setProperty(conjugateStyle, STYLE_CONJUGATE);
        setProperty(conjugateStyle, STYLE_VECTOR, false);

        wordwrapStyle = sc.addStyle("wordwrap", wordwrapStyle);
        setProperty(wordwrapStyle, STYLE_WORDWRAP);

        bookmarkStyle = new SimpleAttributeSet();
        //bookmarkStyle.addAttribute(STYLE_BOOKMARK,new Boolean(true));
        updateColorsForStyles(sc);
        return sc;
    }

    private static void updateColorsForStyles(final StyleContext sc) {
        if (styleContext == null)
            return;
        final Style defaultStyle = sc.getStyle(StyleContext.DEFAULT_STYLE);
        final Color fg = ColorStyles.getColor(ColorStyles.COLOR_AREA_FG);
        final Color bg = ColorStyles.getColor(ColorStyles.COLOR_AREA_BG);
        StyleConstants.setForeground(defaultStyle, fg);
        StyleConstants.setBackground(defaultStyle, bg);
        StyleConstants.setForeground(basicStyle, fg);
        StyleConstants.setBackground(basicStyle, bg);
        StyleConstants.setBackground(invertStyle, fg);
        StyleConstants.setForeground(invertStyle, bg);
        //      StyleConstants.setBackground(mathStyle, bg);
    }

    public static void updateColorsForStyles() {
        updateColorsForStyles(styleContext);
    }

    private static MutableAttributeSet setProperty(final MutableAttributeSet attributes, final Object style, final boolean value) {
        attributes.addAttribute(style, value);
        return attributes;
    }

    private static void setProperty(final MutableAttributeSet attributes, final String style) {
        attributes.addAttribute(style, true);
    }


    public static boolean isReadonlySection(final Element paraElement) {
        final int count = paraElement.getElementCount();
        String elName;
        for (int j = 0; j < count; ++j) {
            elName = paraElement.getElement(j).getName();
            if (elName.equals(StyleConstants.ComponentElementName) || elName.equals(StyleConstants.IconElementName))
                return true;

        }
        return false;
    }

// --Commented out by Inspection START (4.8.05 17:29):
//    public static AreaImage isMoveableComponent(final Element paraElement) {
//        final int count = paraElement.getElementCount();
//        String elName;
//        Element childElement;
//        for (int j = 0; j < count; ++j) {
//            childElement = paraElement.getElement(j);
//            elName = childElement.getName();
//            if (elName.equals(StyleConstants.IconElementName))
//                return getImage(childElement);
//
//        }
//        return null;
//    }
// --Commented out by Inspection STOP (4.8.05 17:29)


    public static boolean isUnderLine(final AttributeSet attributes) {
        return StyleConstants.isUnderline(attributes);
    }

    public static boolean isUnderLineDotted(final AttributeSet attributes) {
        final Object o = attributes.getAttribute(STYLE_UNDERLINEDOTTED);
        return o != null && (Boolean) o;
    }

    public static boolean isExposant(final AttributeSet attributes) {
        return StyleConstants.isSuperscript(attributes);
    }

    public static boolean isSubscript(final AttributeSet attributes) {
        return StyleConstants.isSubscript(attributes);
    }

    public static boolean isInvert(final AttributeSet attributes) {
        final Object o = attributes.getAttribute(STYLE_INVERTED);
        return o != null && (Boolean) o;
    }

    public static boolean isVector(final AttributeSet attributes) {
        final Object o = attributes.getAttribute(STYLE_VECTOR);
        return o != null && (Boolean) o;
    }

    public static boolean isConjugate(final AttributeSet attributes) {
        final Object o = attributes.getAttribute(STYLE_CONJUGATE);
        return o != null && (Boolean) o;
    }

    public static boolean isSize(final AttributeSet attributes, final int size) {
        return StyleConstants.getFontSize(attributes) == size;
    }

    public static boolean isMath(final Element el) {
        final Object obj = el.getAttributes().getAttribute(STYLE_MATH);
        return obj != null && (Boolean) obj;
    }

    public static boolean isStrikeOut(final AttributeSet attributes) {
        return StyleConstants.isStrikeThrough(attributes);
    }

    // --Commented out by Inspection START (4.2.05 16:14):
    //    public static final boolean isAligment(final AttributeSet attributes, final int aligment) {
    //        return StyleConstants.getAlignment(attributes) == aligment;
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:14)

// --Commented out by Inspection START (4.8.05 17:29):
//    public static boolean isMargin(final AttributeSet attributes, final int indent) {
//        return (int) StyleConstants.getLeftIndent(attributes) == indent;
//    }
// --Commented out by Inspection STOP (4.8.05 17:29)

    public static boolean isWordWrap(final AttributeSet attributes) {
        final Object o = attributes.getAttribute(STYLE_WORDWRAP);
        return o != null && (Boolean) o;
    }


    public static boolean isSupportedComponent(final Element el) {
        return el.getName().equals(StyleConstants.ComponentElementName) && (el.getAttributes().getAttribute(StyleConstants.ComponentAttribute) instanceof SeparatorLine);
    }

    public static boolean isComponentLine(final Element el, final int lineType) {
        if (el.getName().equals(StyleConstants.ComponentElementName)) {
            final Object obj = el.getAttributes().getAttribute(StyleConstants.ComponentAttribute);
            if (obj instanceof SeparatorLine)
                if (((SeparatorLine) obj).getLineType() == lineType)
                    return true;
        }
        return false;
    }

//    public static final boolean isSupportedImage(final Element el) {
//        if (el.getName().equals(StyleConstants.IconElementName)) {
//            final ImageIcon icon = (ImageIcon) el.getAttributes().getAttribute(StyleConstants.IconAttribute);
//            //final ImageProducer imageProducer = icon.getImage().getSource();
//          //  if (imageProducer instanceof TIImageDecoder)
//            return icon != null;
//        }
//        return false;
//    }

    public static AreaImage getImage(final Element el) {
        return (AreaImage) el.getAttributes().getAttribute(StyleConstants.IconAttribute);
    }

    public static boolean isImage(final Element el) {
        if (el.getName().equals(StyleConstants.IconElementName)) {
            final Object icon = el.getAttributes().getAttribute(StyleConstants.IconAttribute);
            //final ImageProducer imageProducer = icon.getImage().getSource();
            //  if (imageProducer instanceof TIImageDecoder)
            return icon != null;
        }
        return false;
    }

    public static boolean isBookmark(final Element el) {
        final Object obj = el.getAttributes().getAttribute(STYLE_BOOKMARK);
        return obj != null && obj.equals(el);
    }

    //    public static final boolean isBookmark(final AttributeSet attributes) {
    //        final Object o = attributes.getAttribute(STYLE_BOOKMARK);
    //        return o != null && ((Boolean)o).booleanValue();
    //    }


    public static int getAlignment(final AttributeSet attributes) {
        return StyleConstants.getAlignment(attributes);
    }

    public static short getMargin(final AttributeSet attributes) {
        return (short) StyleConstants.getLeftIndent(attributes);
    }

    public static int getFontSize(final AttributeSet attributes) {
        return StyleConstants.getFontSize(attributes);
    }


    public static Font getAreaFont() {
        return font;
    }

    public static Font getAreaBigFont() {
        return maxiFont;
    }

    public static Style getMarginXStyle(int margin) {
        margin = Math.min(margin, MAX_MARGIN_X);
        final String styleName = "STYLE_MARGINX" + margin;
        Style marginXStyle = styleContext.getStyle(styleName);
        if (marginXStyle != null)
            return marginXStyle;
        else {
            marginXStyle = styleContext.addStyle(styleName, alignmentLeftStyle);
            StyleConstants.setLeftIndent(marginXStyle, margin);
            StyleConstants.setAlignment(marginXStyle, StyleConstants.ALIGN_LEFT);
            aditionalMarginX.add(margin);
            return marginXStyle;
        }
    }

    public static MutableAttributeSet updateVectorAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isVector(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), STYLE_VECTOR, false) : vectorStyle;
    }

    public static MutableAttributeSet updateConjugateAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isConjugate(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), STYLE_CONJUGATE, false) : conjugateStyle;
    }

    public static MutableAttributeSet updateUnderlineAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isUnderLine(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), StyleConstants.Underline, false) : underlineStyle;
    }

    public static MutableAttributeSet flipUnderlineAttributes(final MutableAttributeSet inputAttributeSet) {
        final MutableAttributeSet set = (isUnderLine(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), StyleConstants.Underline, false) : new SimpleAttributeSet(underlineStyle.copyAttributes());
        return setProperty(set, STYLE_UNDERLINEDOTTED, false);
    }

    public static MutableAttributeSet flipUnderlineDottedAttributes(final MutableAttributeSet inputAttributeSet) {
        final MutableAttributeSet set = (isUnderLineDotted(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), STYLE_UNDERLINEDOTTED, false) : new SimpleAttributeSet(underlineDottedStyle.copyAttributes());
        StyleConstants.setUnderline(set, false);
        return set;
    }

    public static MutableAttributeSet updateUnderlineDottedAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isUnderLineDotted(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), STYLE_UNDERLINEDOTTED, false) : underlineDottedStyle;
    }

    public static MutableAttributeSet updateWordWrapAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isWordWrap(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), STYLE_WORDWRAP, false) : wordwrapStyle;
    }

    public static MutableAttributeSet updateInvertAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isInvert(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(basicStyle), STYLE_INVERTED, false) : invertStyle;
    }

    public static MutableAttributeSet updateStrikedAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isStrikeOut(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), StyleConstants.StrikeThrough, false) : strikethroughStyle;
    }

    public static MutableAttributeSet updateExposantAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isExposant(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), StyleConstants.Superscript, false) : superscriptStyle;
    }

    public static MutableAttributeSet updateSubscriptAttributes(final MutableAttributeSet inputAttributeSet) {
        return (isSubscript(inputAttributeSet)) ? setProperty(new SimpleAttributeSet(), StyleConstants.Subscript, false) : subscriptStyle;
    }

    public static MutableAttributeSet updateBookmark(final Element el) {
        bookmarkStyle.addAttribute(STYLE_BOOKMARK, el);
        return bookmarkStyle;
    }


    public static StyleContext getDefaultStyleContext() {
        if (styleContext == null) {
            return createDefaultStyles(styleContext = new StyleContext());
        } else return styleContext;
    }

// --Commented out by Inspection START (4.8.05 17:29):
//    public static void setBookmark(final Element paraElement) {
//        ((MutableAttributeSet) paraElement.getAttributes()).addAttribute(STYLE_BOOKMARK, paraElement);
//    }
// --Commented out by Inspection STOP (4.8.05 17:29)

    public static boolean isSet(final AttributeSet attributes, final int propertyCode) {
        final Object o = attributes.getAttribute(ATTRIBUTE_NAMES[propertyCode]);
        return o != null && (Boolean) o;
    }

    public static Object[] getVariableMargins() {
        return aditionalMarginX.toArray();
    }

    public static boolean isText(Element elem) {
        final String elName = elem.getName();
        return elName.equals(AbstractDocument.ContentElementName) && !(elName.equals(StyleConstants.ComponentElementName) || elName.equals(StyleConstants.IconElementName));
    }
}
