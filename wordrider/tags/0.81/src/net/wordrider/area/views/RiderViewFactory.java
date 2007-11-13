package net.wordrider.area.views;

import javax.swing.text.*;

/**
 * @author Vity
 */
public final class RiderViewFactory implements ViewFactory {
    public final View create(final Element elem) {
        final String kind = elem.getName();
        if (kind != null)
            if (kind.equals(AbstractDocument.ContentElementName)) {
                return new RiderLabelView(elem);
            } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                return new RiderParagraphView(elem);
            } else if (kind.equals(AbstractDocument.SectionElementName)) {
                return new RiderSectionView(elem);
            } else if (kind.equals(StyleConstants.ComponentElementName)) {
                return new RiderComponentView(elem);
            } else if (kind.equals(StyleConstants.IconElementName)) {
                return new RiderIconView(elem);
            }
        // default
        return new RiderLabelView(elem);
    }

    static boolean isInSelection(final JTextComponent textComponent, final Element el) {
        final int selStart = textComponent.getSelectionStart();
        final int selEnd = textComponent.getSelectionEnd();
        if (selStart != selEnd) {
            // Something is selected, does p0 - p1 fall in that range?
            final int pMin;
            final int pMax;
            final int p0 = el.getStartOffset();
            final int p1 = el.getEndOffset();
            if (selStart <= p0)
                pMin = p0;
            else
                pMin = Math.min(selStart, p1);
            if (selEnd >= p1)
                pMax = p1;
            else
                pMax = Math.max(selEnd, p0);
            // If pMin == pMax (also == p0), selection isn't in this
            // block.
            if (pMin != pMax)
                return true;

        }
        return false;
    }


}