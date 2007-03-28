package net.wordrider.area;

import javax.swing.text.Element;

/**
 * @author Vity
*/
public final class SelectedElementsIterator {
    // --Commented out by Inspection (4.2.05 16:15): public static final int REVERSE_DIRECTION = 0;
    public static final int FORWARD_DIRECTION = 1;
    private final Element root;
    private Element activeElement;
    private int activeParaIndex, activeLeafIndex, startParaIndex, startLeafIndex;
    private final int endParaIndex;
    private final int endLeafIndex;
    private int activeParaElementsCount;
    //   private final boolean isFirst;


    public SelectedElementsIterator(final Element docRoot, final int startOffset, final int endOffset, final int direction) {
        this.root = docRoot;
        if (direction == FORWARD_DIRECTION) {
            this.activeElement = docRoot.getElement(startParaIndex = activeParaIndex = docRoot.getElementIndex(startOffset));
            this.startLeafIndex = this.activeLeafIndex = this.activeElement.getElementIndex(startOffset);
            this.activeParaElementsCount = this.activeElement.getElementCount();
            this.endParaIndex = docRoot.getElementIndex(endOffset);
            this.endLeafIndex = docRoot.getElement(this.endParaIndex).getElementIndex(endOffset);
        } else {
            this.activeElement = docRoot.getElement(activeParaIndex = docRoot.getElementIndex(endOffset));
            this.activeLeafIndex = this.activeElement.getElementIndex(endOffset);
            this.activeParaElementsCount = this.activeElement.getElementCount();
            this.endParaIndex = docRoot.getElementIndex(startOffset);
            this.endLeafIndex = docRoot.getElement(this.endParaIndex).getElementIndex(startOffset);
        }
        // this.isFirst = true;
    }

    public final Element next() {
        if (activeParaIndex < endParaIndex) {
            if (activeLeafIndex < activeParaElementsCount) {
                return this.activeElement.getElement(activeLeafIndex++);
            } else {
                this.activeElement = root.getElement(++activeParaIndex);
                activeParaElementsCount = this.activeElement.getElementCount();
                activeLeafIndex = 1;
                return this.activeElement.getElement(0);
            }
        } else
            return (activeLeafIndex <= endLeafIndex && activeParaIndex == endParaIndex) ? this.activeElement.getElement(activeLeafIndex++) : null;
    }

    public final boolean isLastLeafElement() {
        return (activeParaIndex == endParaIndex && activeLeafIndex - 1 == endLeafIndex);
    }

    public final boolean isFirstLeafElement() {
        return (activeParaIndex == startParaIndex && activeLeafIndex - 1 == startLeafIndex);
    }

    // --Commented out by Inspection START (4.2.05 16:15):
    //        public final Element previous() {
    //            if (activeParaIndex > endParaIndex) {
    //                if (activeLeafIndex >= 0) {
    //                    return this.activeElement.getElement(activeLeafIndex--);
    //                } else {
    //                    this.activeElement = root.getElement(--activeParaIndex);
    //                    activeParaElementsCount = this.activeElement.getElementCount();
    //                    activeLeafIndex = activeParaElementsCount - 2;
    //                    return this.activeElement.getElement(activeParaElementsCount - 1);
    //                }
    //            } else
    //                return (activeLeafIndex >= endLeafIndex && activeParaIndex == endParaIndex) ? this.activeElement.getElement(activeLeafIndex--) : null;
    //        }
    // --Commented out by Inspection STOP (4.2.05 16:15)

    // --Commented out by Inspection START (4.2.05 16:15):
    //        public final Element nextElement() {
    //            if (activeParaIndex < endParaIndex) {
    //                if (isFirst) {
    //                    isFirst = false;
    //                    return this.activeElement;
    //                }
    //                if (activeLeafIndex < activeParaElementsCount) {
    //                    return this.activeElement.getElement(activeLeafIndex++);
    //                } else {
    //                    this.activeElement = root.getElement(++activeParaIndex);
    //                    activeParaElementsCount = this.activeElement.getElementCount();
    //                    activeLeafIndex = 0;
    //                    return this.activeElement;
    //                }
    //            } else
    //                return (activeLeafIndex <= endLeafIndex && activeParaIndex == endParaIndex) ? this.activeElement.getElement(activeLeafIndex++) : null;
    //        }
    // --Commented out by Inspection STOP (4.2.05 16:15)


}
