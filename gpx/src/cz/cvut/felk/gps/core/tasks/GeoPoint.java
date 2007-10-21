package cz.cvut.felk.gps.core.tasks;

import java.awt.geom.Point2D;

/**
 * @author Vity
 */
public class GeoPoint extends Point2D.Double {
    public GeoPoint(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public GeoPoint(String longitude, String latitude) {
        this(java.lang.Double.parseDouble(normalize(longitude)), java.lang.Double.parseDouble(normalize(latitude)));
    }

    private static String normalize(final String tude) {
        if (tude.endsWith("E") || tude.endsWith("N"))
            return tude.substring(0, tude.length() - 1);
        else return tude;
    }
}
