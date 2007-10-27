package cz.cvut.felk.gpx.core.tasks;

/**
 * @author Vity
 */
public class PolygonProvider {
    private PolygonProvider() {
    }

    public enum PolygonType {
        JAVA_GRAPHIC, GIS_LITE
    }

    public static SimpleGeographicPolygon getPolygonInstance(PolygonType type, GeoPoint[] geoPoints) {
        if (type == PolygonType.JAVA_GRAPHIC) {
            return new Polygon2D(geoPoints);
        } else {
            final GeoPolygon geoPolygon = new GeoPolygon();
            for (GeoPoint point : geoPoints) {
                geoPolygon.addGeoPoint(point);
            }
            return geoPolygon;
        }
    }

}
