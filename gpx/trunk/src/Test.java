import ie.tcd.cs.dsg.hermes.gislite.geometry.Point;
import ie.tcd.cs.dsg.hermes.gislite.geometry.Polygon;

/**
 * @author Vity
 */
public class Test {
    public static void main(String[] args) {
        final Point p1 = new Point(50.04022177886585F, 14.40529892552092F);
        final Point p2 = new Point(50.03681090322574F, 14.40773890254857F);
        final Point p3 = new Point(50.03800752754654F, 14.40909007385753F);
        final Point p4 = new Point(50.04092710302574F, 14.40683074896286F);
        final Point p5 = new Point(50.04022177886585F, 14.40529892552092F);
        final Point cont = new Point(50.03866541392449F, 14.40557436706965F);
        final Polygon polygon = new Polygon();
        polygon.addVertex(p1);
        polygon.addVertex(p2);
        polygon.addVertex(p3);
        polygon.addVertex(p4);
        polygon.addVertex(p5);
        //final boolean contain = polygon.contains(50.04009722222223F, 14.40635910168633F);
        boolean contain = polygon.getBounds().contains(50.0395454189122F, 14.40428964989346F);//mimo
        System.out.println("contain = " + contain);
        contain = polygon.getBounds().contains(50.03904550960794F, 14.4068250838694F);//in
        System.out.println("contain = " + contain);
        contain = polygon.getBounds().contains(50.03995954576556F, 14.40597321019401F);//in
        System.out.println("contain = " + contain);
        contain = polygon.getBounds().contains(50.03674081558079F, 14.40834789134931F);//mimo
        System.out.println("contain = " + contain);
        contain = polygon.getBounds().contains(50.04039089068473F, 14.40628422092948F);//in
        System.out.println("contain = " + contain);
        contain = polygon.getBounds().contains(50.04115186934687F, 14.40608159501353F);//mimo
        System.out.println("contain = " + contain);
        contain = polygon.contains(50.04065104881526F, 14.40602633447299F);//mimo
        System.out.println("contain mimo = " + contain);
        contain = polygon.contains(50.04060986831761F, 14.40606038184733F);//mimo
        System.out.println("contain mimo = " + contain);
    }
}
