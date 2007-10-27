import ie.tcd.cs.dsg.hermes.gislite.geometry.Point;
import ie.tcd.cs.dsg.hermes.gislite.geometry.Polygon;

/**
 * @author Vity
 */
public class Test {
    public static void main(String[] args) {
        final Point p1 = new Point(50.03959526995691F, 14.40600278367175F);
        final Point p2 = new Point(50.03702356561961F, 14.40786420647061F);
        final Point p3 = new Point(50.03765270436561F, 14.40865692328808F);
        final Point p4 = new Point(50.04049303718759F, 14.40678943853316F);
        final Point p5 = new Point(50.03959526995691F, 14.40600278367175F);
        final Point cont = new Point(50.0374300F, 14.4082700F);
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
        contain = polygon.contains(50.04060986831761F, 14.4082700F);//mimo
        System.out.println("contain mimo = " + contain);
        contain = polygon.getBounds().contains(50.0374400F, 14.4082583F);//in
        System.out.println("contain in = " + contain);
    }
}
