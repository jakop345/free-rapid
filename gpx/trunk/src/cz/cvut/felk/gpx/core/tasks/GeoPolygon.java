package cz.cvut.felk.gpx.core.tasks;

import ie.tcd.cs.dsg.hermes.gislite.geometry.Point;
import ie.tcd.cs.dsg.hermes.gislite.geometry.Polygon;

/**
 * @author Vity
 */
public class GeoPolygon implements SimpleGeographicPolygon{
    private Polygon polygon;
    public GeoPolygon() {
        this.polygon = new Polygon();
    }

    public void addGeoPoint(GeoPoint point) {
        this.polygon.addVertex(new Point((float)point.getLatitude(), (float)point.getLongitude()));
    }

    public boolean contains(GeoPoint point) {
        return this.polygon.getBounds().contains((float)point.getLatitude(), (float)point.getLongitude());
    }
}
