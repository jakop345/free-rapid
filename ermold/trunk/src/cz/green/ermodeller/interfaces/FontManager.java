package cz.green.ermodeller.interfaces;

/**
 * This interface enebles the font managment. Implements the
 * function theat gives the <code>FonTMetrics</code> for the font to represent
 * strings at 100% scale. Is used to count the size elements.
 */
public interface FontManager {
    /**
     * Returns the <code>FontMetrics</code> for the current font choosed to represent the
     * string at 100% scale. The real value knows only one object and others routes the
     * answers to him.
     *
     * @return The choosed font's metrics.
     */
    java.awt.FontMetrics getReferentFontMetrics();
}
