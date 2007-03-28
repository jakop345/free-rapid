package net.wordrider.area.views;

import net.wordrider.area.ColorStyles;
import net.wordrider.area.RiderStyles;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class RiderLabelView extends GlyphView implements TabableView {
    private boolean vector = false;
    private boolean dotted = false;
    private boolean _underline = false;
    private boolean wordWrap = false;
    private boolean inverse = false;
    private boolean nextElementVector = false;
    //private boolean isBigSize = false;
    private Font font;
    private Color fg;
    private Color bg;
    private boolean underline = false;
    private boolean strike = false;
    private boolean superscript = false;
    private boolean subscript = false;
    private boolean conjugate = false;
    private final static Logger logger = Logger.getLogger(RiderLabelView.class.getName());
    
    public RiderLabelView(final Element elem) {
        super(elem);
    }

    public final Color getForeground() {
        sync();
        return fg;
    }

    public final View breakView(final int axis, final int p0, final float pos, final float len) {
        if (wordWrap)
            return super.breakView(axis, p0, pos, len);
        //wordwrap is on
        if (axis == View.X_AXIS) {
            final int p1 = this.getGlyphPainter().getBoundedPosition(this,
                    p0, pos, len);
            return createFragment(p0, p1);
        }
        return this;
    }


    public final Font getFont() {
        sync();
        //  return (!font.getFamily().equals(RiderStyles.FONT_FAMILY)) ? RiderStyles.getAreaFont() : font;
        return font;
    }

    private void setPropertiesFromAttributes() {
        final AttributeSet attr = getAttributes();
        if (attr != null) {
            boolean math = RiderStyles.isMath(getElement());
            final StyledDocument doc = (StyledDocument) getDocument();

            if (!math) {
                font = doc.getFont(attr);
                fg = doc.getForeground(attr);
                underline = RiderStyles.isUnderLine(attr);
                strike = RiderStyles.isStrikeOut(attr);
                superscript = RiderStyles.isExposant(attr);
                subscript = RiderStyles.isSubscript(attr);
                if (subscript && font.isBold()) {
                    font = RiderStyles.maxiFontNoBold;
                }
                wordWrap = RiderStyles.isWordWrap(attr);
                //bold = StyleConstants.isBold(attr);
                // doubleline = (attr.getAttribute("2UNDERLINE") != null);
                dotted = RiderStyles.isUnderLineDotted(attr);
                vector = RiderStyles.isVector(attr);
                inverse = RiderStyles.isInvert(attr);
                if (attr.isDefined(StyleConstants.Background) && inverse) {
                    bg = doc.getBackground(attr);
                } else {
                    bg = null;
                }
                conjugate = RiderStyles.isConjugate(attr);
                if (vector) {
                    nextElementVector = isNextElementVector(getElement(), getDocument());
                }
                //isBigSize = RiderStyles.isSize(attr, RiderStyles.SIZE_MAXI);
                _underline = RiderStyles.isUnderLine(attr);
                //vector = net.wordrider.area.RiderStyles.isVector(attr);
                //dotted = (net.wordrider.area.RiderStyles.isUnderLineDotted(attr));
            } else {
                font = doc.getFont(RiderStyles.mathStyle);
                fg = ColorStyles.getColor(ColorStyles.COLOR_MATH_STYLE);
                //bg = ColorStyles.getColor(ColorStyles.COLOR_AREA_BG);
                bg = null;
                underline = strike = wordWrap = dotted = vector = inverse = _underline = nextElementVector = superscript = subscript = conjugate = false;
            }
        }
    }

    private static boolean isNextElementVector(final Element el, final Document doc) {
        final Element parent = el.getParentElement();
        final int elCount = parent.getElementCount();
        boolean foundElement = false;
        //ElementIterator aa = new ElementIterator();
        Element itEl;
        for (int i = 0; i < elCount; ++i) {
            itEl = parent.getElement(i);
            if (foundElement) {
                if (RiderStyles.isVector(itEl.getAttributes())) {
                    if (i + 1 == elCount) {
                        try {
                            return !doc.getText(itEl.getStartOffset(), itEl.getEndOffset() - itEl.getStartOffset()).equals("\n");
                        } catch (BadLocationException e) {
                            LogUtils.processException(logger, e);
                            return false;
                        }
                    } else
                        return true;
                } else
                    return false;
            } else if (itEl.equals(el))
                foundElement = true;
        }
        return false;
    }

    private void fixDrawBug(final Graphics g, final int x1, final int yStart, final int yHigh) {
        g.setPaintMode();
        final JTextComponent component = (JTextComponent) getContainer();
        if (RiderViewFactory.isInSelection(component, getElement()))
            g.setColor(component.getSelectionColor());
        else
            g.setColor(component.getBackground());
//        g.drawLine(x1, yStart + 1, x1, yHigh + 1);
    }

    public final void paint(final Graphics g, final Shape a) {
        // g.setFont(font);
        if (Swinger.antialiasing) {
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
        }
        super.paint(g, a);
        //Color bg = getBackground();

        //            if (bg != null) {
        //                g.setSelectedValue(bg);
        //                g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
        //            }

        //            final boolean strike = isStrikeThrough();
        if (_underline || dotted || vector || conjugate || inverse) {
            final int p0 = getStartOffset();
            final int p1 = getEndOffset();
            final Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
            g.setColor(getForeground());
            checkPainter();
            final GlyphPainter painter = getGlyphPainter();
            //painter.paint(this, g, a, p0, p1);
            //if (underline || strike || vector || dotted) {
            //
            //                    View parent = getParent();
            //                    if ((parent != null) && (parent.getEndOffset() == p1)) {	// strip whitespace on end
            //                        Segment s = getText(p0, p1);
            //                        while ((s.count > 0) && (Character.isSpace(s.array[s.count - 1]))) {
            //                            p1 -= 1;
            //                            s.count -= 1;
            //                        }
            //                    }

            int x0 = alloc.x;
            final int p = getStartOffset();
            if (p != p0) x0 += (int) painter.getSpan(this, p, p0, getTabExpander(), x0);
            final int x1 = x0 + (int) painter.getSpan(this, p0, p1, getTabExpander(), x0);
            // calculate y coordinate
            final int d = (int) painter.getDescent(this);
            int yTmp = alloc.y + alloc.height - d;

            if (inverse && x0 == x1)
                //repaint fix bug
                fixDrawBug(g, x0, yTmp + 1, alloc.y + alloc.height - (int) painter.getAscent(this) - 4);

            //            if (bold) {
            //                final Graphics2D g2 = (Graphics2D) g;
            //                g2.setStroke(new BasicStroke(2.0f));
            //            }

            Stroke stroke = null;
            //if (_underline || dotted) {
            if (dotted) {
                final Graphics2D g2 = (Graphics2D) g;
                stroke = g2.getStroke();
                g2.setStroke(RiderStyles.DOTTED_STROKE);
            }
            if (dotted) {
                ++yTmp;
                //  if (bold) yTmp++;
                g.drawLine(x0, yTmp, x1, yTmp);
                //                    if (doubleline) {
                //                        yTmp += 2;
                //                        if (bold) yTmp++;
                //                        g.drawLine(x0, yTmp, x1, yTmp);
                //                    }
            } else if (_underline && (x1 == x0))
                fixDrawBug(g, x0, yTmp, yTmp);

            if (stroke != null) {
                final Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(stroke);
            }
            //                if (strike) {
            //                    yTmp -= (int) (painter.getAscent(this) * 0.3f);
            //                    g.drawLine(x0, yTmp, x1, yTmp);
            //                }
            if (vector || conjugate) {
                yTmp = alloc.y + alloc.height - (int) painter.getAscent(this) - 1;
                if (x0 != x1)
                    g.drawLine(x0, yTmp, x1, yTmp);
                if (vector && x1 != x0 && !nextElementVector) {
                    g.drawLine(x1 + 1, yTmp, x1 - 4, yTmp - 2);
                    g.drawLine(x1 + 1, yTmp, x1 - 4, yTmp + 2);
                }
                //                    if (doubleover) {
                //                        yTmp -= 2;
                //                        if (bold) yTmp--;
                //                        g.drawLine(x0, yTmp, x1, yTmp);
                //                    }
            }

        }
    }


    /**
     * Synchronize the view's cached values with the model. This causes the font, metrics, color, etc to be re-cached if
     * the cache has been invalidated.
     */
    private void sync() {
        if (font == null) {
            setPropertiesFromAttributes();
        }
    }

    /**
     * Fetches the <code>FontMetrics</code> used for this view.
     * @deprecated FontMetrics are not used for glyph rendering when running in the Java2 SDK.
     */
    protected FontMetrics getFontMetrics() {
        sync();
        Container c = getContainer();
        return (c != null) ? c.getFontMetrics(font) :
                Toolkit.getDefaultToolkit().getFontMetrics(font);
    }

    /**
     * Fetches the background color to use to render the glyphs. This is implemented to return a cached background
     * color, which defaults to <code>null</code>.
     * @return the cached background color
     */
    public Color getBackground() {
        sync();
        return bg;
    }

    /**
     * Determines if the glyphs should be underlined.  If true, an underline should be drawn through the baseline.  This
     * is implemented to return the cached underline property.
     * <p/>
     * <p>When you request this property, <code>LabelView</code> re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>. If <code>Element</code>'s <code>AttributeSet</code> does not
     * have this property set, it will revert to false.
     * @return the value of the cached <code>underline</code> property
     */
    public boolean isUnderline() {
        sync();
        return underline;
    }

    /**
     * Determines if the glyphs should have a strikethrough line.  If true, a line should be drawn through the center of
     * the glyphs.  This is implemented to return the cached <code>strikeThrough</code> property.
     * <p/>
     * <p>When you request this property, <code>LabelView</code> re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>. If <code>Element</code>'s <code>AttributeSet</code> does not
     * have this property set, it will revert to false.
     * @return the value of the cached <code>strikeThrough</code> property
     */
    public boolean isStrikeThrough() {
        sync();
        return strike;
    }

    /**
     * Determines if the glyphs should be rendered as superscript.
     * @return the value of the cached <code>subscript</code> property
     */
    public boolean isSubscript() {
        sync();
        return subscript;
    }

    /**
     * Determines if the glyphs should be rendered as subscript.
     * <p/>
     * <p>When you request this property, <code>LabelView</code> re-syncs its state with the properties of the
     * <code>Element</code>'s <code>AttributeSet</code>. If <code>Element</code>'s <code>AttributeSet</code> does not
     * have this property set, it will revert to false.
     * @return the value of the cached <code>superscript</code> property
     */
    public boolean isSuperscript() {
        sync();
        return superscript;
    }

    /**
     * Gives notification from the document that attributes were changed in a location that this view is responsible
     * for.
     * @param e the change information from the associated document
     * @param a the current allocation of the view
     * @param f the factory to use to rebuild if the view has children
     * @see View#changedUpdate
     */
    public void changedUpdate(final DocumentEvent e, final Shape a, final ViewFactory f) {
        font = null;
        super.changedUpdate(e, a, f);
    }
}