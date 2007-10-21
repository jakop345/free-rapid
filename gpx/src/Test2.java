import cz.cvut.felk.gps.core.tasks.Polygon2D;

import java.awt.geom.Point2D;

/**
 * @author Vity
 */
public class Test2 {
    public static void main(String[] args) {
        final Point2D p1 = new Point2D.Double(50.04022177886585F, 14.40529892552092F);
        final Point2D p2 = new Point2D.Double(50.03681090322574F, 14.40773890254857F);
        final Point2D p3 = new Point2D.Double(50.03800752754654F, 14.40909007385753F);
        final Point2D p4 = new Point2D.Double(50.04092710302574F, 14.40683074896286F);
        final Point2D p5 = new Point2D.Double(50.04022177886585F, 14.40529892552092F);
        final Point2D cont = new Point2D.Double(50.03866541392449F, 14.40557436706965F);
        final Polygon2D polygon = new Polygon2D();
        polygon.addPoint(50.04022177886585F, 14.40529892552092F);
        polygon.addPoint(50.03681090322574F, 14.40773890254857F);
        polygon.addPoint(50.03800752754654F, 14.40909007385753F);
        polygon.addPoint(50.04092710302574F, 14.40683074896286F);
        polygon.addPoint(50.04022177886585F, 14.40529892552092F);
        //final boolean le(50.03866541392449F, 14.40557436706965F);contain = polygon.contains(50.04009722222223F, 14.40635910168633F);
        boolean contain = polygon.contains(50.0395454189122F, 14.40428964989346F);//mimo
        System.out.println("contain out = " + contain);
        contain = polygon.contains(50.03904550960794F, 14.4068250838694F);//in
        System.out.println("contain in = " + contain);
        contain = polygon.contains(50.03995954576556F, 14.40597321019401F);//in
        System.out.println("contain in = " + contain);
        contain = polygon.contains(50.03674081558079F, 14.40834789134931F);//mimo
        System.out.println("contain mimo = " + contain);
        contain = polygon.contains(50.04039089068473F, 14.40628422092948F);//in
        System.out.println("contain in = " + contain);
        contain = polygon.contains(50.04115186934687F, 14.40608159501353F);//mimo
        System.out.println("contain mimo = " + contain);
        contain = polygon.contains(50.04065104881526F, 14.40602633447299F);//mimo
        System.out.println("contain mimo = " + contain);


    }
}