package net.wordrider.area.views;

import javax.swing.text.BoxView;
import javax.swing.text.Element;
import java.awt.*;

/**
 * @author Vity
 */
final class RiderSectionView extends BoxView {

    // --Commented out by Inspection START (26.2.05 17:37):
    //    //public static int DRAW_PAGE_INSET=15;
    //    public final Insets pageMargins = new Insets(10, 10, 40, 10);
    // --Commented out by Inspection STOP (26.2.05 17:37)
    //    public int pageWidth=300;
    //  public int pageHeight=400;


    public RiderSectionView(final Element e) {
        super(e, Y_AXIS);
        //super.setInsets((short)38, (short)0, (short)0, (short)0);    //call to super
    }

    //        protected void setInsets(short top, short left, short bottom, short right) {
    //            super.setInsets(top, (short)(left + 30), bottom, right);    //call to super
    //        }
    //            public void paint(Graphics g, Shape allocation) {
    //                super.paint(g, allocation);    //call to super
    //                final Rectangle r = (allocation instanceof Rectangle) ? (Rectangle) allocation : allocation.getBounds();
    //
    //                g.drawRect(r.x+38, r.y, r.width, r.height);
    //            }

    //    public float getMinimumSpan(int axis) {
    //        float f = super.getMinimumSpan(axis);
    //        f *= 1.5;
    //        return f;
    //    }
    //
    //    public float getMaximumSpan(int axis) {
    //        float f = super.getMaximumSpan(axis);
    //        f *= 0.5;
    //        return f;
    //    }
    //
    //    public float getPreferredSpan(int axis) {
    //        float f = super.getPreferredSpan(axis);
    //        f *= 0.5;
    //        return f;
    //    }
    //
    //    protected void layout(int width, int height) {
    //        super.layout(width, new Double(height * 0.5).intValue());
    //    }

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
    public final void paintChild(final Graphics g, final Rectangle r, final int n) {
        //System.out.println("paint child section view");
        if (n > 0) {
            final RiderParagraphView child = (RiderParagraphView) this.getView(n - 1);

            final RiderParagraphView current = (RiderParagraphView) this.getView(n);
            current.shift = child.shift + child.childCount;
        }
        //            g.setSelectedValue(Color.BLUE);
        //            g.drawRect(r.x, r.y, r.width, r.height);
        super.paintChild(g, r, n);
    }
}