package net.wordrider.area.views;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;
import net.wordrider.utilities.Swinger;

import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import java.awt.*;

final class RiderParagraphView extends javax.swing.text.ParagraphView {
    int childCount = 0;
    int shift = 0;
    private static int rowPosition = -1;
    private static Image breakPointImage = null;
    private static Image mathPointImage = null;

    private boolean isImage = false;
    private boolean isMath = false;

    public RiderParagraphView(final Element e) {
        super(e);
        //   JTextComponent editor = (RiderArea) getContainer();

        if (breakPointImage == null) {
            final Image imageIcon = Swinger.getIconImage("breakpoint.gif");
            if (imageIcon != null)
                breakPointImage = imageIcon;
        }
        if (mathPointImage == null) {
            final Image imageIcon = Swinger.getIconImage("math.gif");
            if (imageIcon != null)
                mathPointImage = imageIcon;
        }

    }

    protected void setPropertiesFromAttributes() {
        super.setPropertiesFromAttributes();    //call to super
        //left = StyleConstants.getLeftIndent();
        if (isImage || RiderStyles.isImage(getElement().getElement(0))) {
            //System.out.println("it's image, setting center");
            isImage = false;
            setJustification(StyleConstants.ALIGN_CENTER);
        }
        if (RiderStyles.isMath(getElement())) {
            //System.out.println("it's image, setting center");
            isMath = true;
            setJustification(StyleConstants.ALIGN_LEFT);

        }
    }

    public final void paint(final Graphics g, final Shape a) {
        childCount = this.getViewCount();
        super.paint(g, a);
        //System.out.println("child count" + childCount);
        //System.out.println("para paint");
        //rowCountInThisParagraph = this.getViewCount(); //<----- YOU HAVE REAL ROW COUNT FOR ONE PARAGRAPH}

//                    Rectangle r = g.getClipBounds();
        //            g.setSelectedValue(new Color(0xC0C0C0));
        //            g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
        final Element element = getElement();
        final int paraY = a.getBounds().y + getTopInset();
        final int y = paraY + getOffset(Y_AXIS, 0); // y pozice 0teho
        final int height = getSpan(Y_AXIS, 0); //height 0
        final int x = y + height / 2;
        if (RiderStyles.isBookmark(element)) {
            if (breakPointImage != null) {
                g.drawImage(breakPointImage, 7, x - 7, null);
            } else {
                g.setColor(Color.RED);
                g.fillOval(6, x - 5, 10, 10);
            }
        } else if (RiderStyles.isMath(element)) {
            //    } else if (isMath) { ne!
            if (mathPointImage != null) {
                g.drawImage(mathPointImage, 4, x - 8, null);
            } else {
                g.setColor(Color.BLUE);
                g.fillOval(6, x - 5, 10, 10);
            }
        } else {
            g.setColor(Color.RED);
            g.fillOval(10, x - 2, 3, 3);
        }

        final JTextComponent editor = (JTextComponent) getContainer();
        int startOffset = element.getStartOffset();
        int endOffset = element.getEndOffset();
        final int curPosition = editor.getCaretPosition();
        if (startOffset <= curPosition && curPosition <= endOffset)
            for (int i = 0; i < childCount; ++i) {
                final int newPosition = shift + i + 1;
                if (newPosition != rowPosition) {
                    final View childView = getView(i);
                    if (childView.getStartOffset() <= curPosition && curPosition <= childView.getEndOffset())
                        editor.firePropertyChange("linePosition", rowPosition, rowPosition = newPosition);
                    rowPosition = newPosition;
                }
            }
    }

//    public final void paintChild(final Graphics g, final Rectangle r, final int n) {
//        super.paintChild(g, r, n);
//        System.out.println("paint child para view");
//        if (n == 0) {
//           // JTextComponent editor = (JTextComponent) getContainer();
//        }
//
//    }

    protected short getLeftInset() {
        if (!isMath) {
            final short inset = super.getLeftInset();
            final int max = ((RiderArea) getContainer()).getMaxMarginWidth();
            if (inset > max)
                return (short) max;
            else return inset;
        } else
            return 0;
    }

//    protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
//        //optimized
//        int preferred = 0;
//        int n = getViewCount();
//        for (int i = 0; i < n; i++) {
//            View v = getView(i);
//            spans[i] = (int) v.getPreferredSpan(axis);
//            offsets[i] = preferred;
//            preferred += spans[i];
//        }
//    }
//
//    protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
//        //optimized
//        int n = getViewCount();
//        for (int i = 0; i < n; i++) {
//            View v = getView(i);
//            int min = (int) v.getMinimumSpan(axis);
//            offsets[i] = 0;
//            spans[i] = Math.max(min, targetSpan);
//        }
//    }
//
//    public int getResizeWeight(int axis) {
//        //optimized
//        return 0;
//    }
//
//    public float getAlignment(int axis) {
//        //opimized
//        return 0;
//    }
//
//    protected View createRow() {
//        //optimized
//        return new OptimizedRow(getElement());
//    }
//
//    class OptimizedRow extends BoxView {
//        SizeRequirements mimorRequirements;
//
//        OptimizedRow(Element elem) {
//            super(elem, View.X_AXIS);
//        }
//
//        protected void loadChildren(ViewFactory f) {
//        }
//
//        public AttributeSet getAttributes() {
//            View p = getParent();
//            return (p != null) ? p.getAttributes() : getElement().getAttributes();
//        }
//
//        public float getAlignment(int axis) {
//            if (axis == View.X_AXIS) {
//                switch (StyleConstants.getAlignment(getAttributes())) {
//                    case StyleConstants.ALIGN_LEFT:
//                        return 0;
//                    case StyleConstants.ALIGN_RIGHT:
//                        return 1;
//                    case StyleConstants.ALIGN_CENTER:
//                    case StyleConstants.ALIGN_JUSTIFIED:
//                        return 0.5f;
//                }
//            }
//            return 0;
//        }
//
//        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
//            Rectangle r = a.getBounds();
//            View v = getViewAtPosition(pos, r);
//            if ((v != null) && (!v.getElement().isLeaf())) {
//                // Don't adjust the height if the view represents a branch.
//                return super.modelToView(pos, a, b);
//            }
//            r = a.getBounds();
//            int height = r.height;
//            int y = r.y;
//            Shape loc = super.modelToView(pos, a, b);
//            r = loc.getBounds();
//            r.height = height;
//            r.y = y;
//            return r;
//        }
//
//        public int getStartOffset() {
//            int offs = Integer.MAX_VALUE;
//            int n = getViewCount();
//            for (int i = 0; i < n; i++) {
//                View v = getView(i);
//                offs = Math.min(offs, v.getStartOffset());
//            }
//            return offs;
//        }
//
//        public int getEndOffset() {
//            int offs = 0;
//            int n = getViewCount();
//            for (int i = 0; i < n; i++) {
//                View v = getView(i);
//                offs = Math.max(offs, v.getEndOffset());
//            }
//            return offs;
//        }
//
//        protected void layoutMinorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
//            baselineLayout(targetSpan, axis, offsets, spans);
//        }
//
//        protected SizeRequirements calculateMinorAxisRequirements(int axis,
//                                                                  SizeRequirements r) {
//            mimorRequirements = baselineRequirements(axis, r);
//            return mimorRequirements;
//        }
//
//        protected SizeRequirements baselineRequirements(int axis, SizeRequirements r) {
//            if (r == null) {
//                r = new SizeRequirements();
//            }
//
//            int n = getViewCount();
//
//            // loop through all children calculating the max of all their ascents and
//            // descents at minimum, preferred, and maximum sizes
//            int span = 0;
//            for (int i = 0; i < n; i++) {
//                View v = getView(i);
//
//                // find the maximum of the preferred ascents and descents
//                span = Math.max((int) v.getPreferredSpan(axis), span);
//            }
//
//            r.preferred = span;
//            r.maximum = span;
//            r.minimum = span;
//
//            return r;
//        }
//
//        protected int getViewIndexAtPosition(int pos) {
//            // This is expensive, but are views are not necessarily layed
//            // out in model order.
//            if (pos < getStartOffset() || pos >= getEndOffset())
//                return -1;
//            for (int counter = getViewCount() - 1; counter >= 0; counter--) {
//                View v = getView(counter);
//                if (pos >= v.getStartOffset() &&
//                        pos < v.getEndOffset()) {
//                    return counter;
//                }
//            }
//            return -1;
//        }
//
//        protected short getLeftInset() {
//            View parentView;
//            int adjustment = 0;
//            if ((parentView = getParent()) != null) { //use firstLineIdent for the first row
//                if (this == parentView.getView(0)) {
//                    adjustment = firstLineIndent;
//                }
//            }
//            return (short) (super.getLeftInset() + adjustment);
//        }
//
//        protected short getBottomInset() {
//            float lineSpacing = StyleConstants.getLineSpacing(getAttributes());
//            return (short) (super.getBottomInset() + mimorRequirements.preferred * lineSpacing);
//        }
//
//        protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
//            //optimized
//            int preferred = 0;
//            int n = getViewCount();
//            for (int i = 0; i < n; i++) {
//                View v = getView(i);
//                spans[i] = (int) v.getPreferredSpan(axis);
//                offsets[i] = preferred;
//                preferred += spans[i];
//            }
//        }
//
//        public int getResizeWeight(int axis) {
//            //optimized
//            return 0;
//        }
//    }

}