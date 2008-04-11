package cz.green.eventtool;

import cz.green.event.interfaces.ContainerDesktop;
import cz.green.event.exceptions.ImpossibleNegativeValueException;


/**
 * This class has the same functionality as its predecessor. Adds three new functions. Two are
 * needful for saving and loading schema to (from) a stream and the third is useful for printing
 * schema.
 */
public class Container extends cz.green.event.Container {
    /**
     * Same constructor as deriver one.
     *
     * @see cz.green.event.Container#Container(int, int, int, int)
     */
    public Container(int width, int height) {
        super(width, height);
    }

    /**
     * This method creates desktop. Is need for creating desktop instances. When we need other instance than
     * <code>cz.green.event.Desktop</code> can simply override this method to return other.
     *
     * @return cz.green.event.ContainerDesktop
     */
    public ContainerDesktop getDesktop() {
        if (desktop == null) {
            try {
                java.awt.Rectangle r = getBounds();
                desktop = new Desktop(this, r.x, r.y, r.width, r.height);
            } catch (ImpossibleNegativeValueException e) {
                return null;
            }
        }
        return desktop;
    }

    /**
     * Simply loads schema from a file. For loading uses Java Serialization. After loading initialize
     * loaded desktop by method <code>init</code>.
     *
     * @param fileName File from which to load schema.
     * @throws java.io.IOException If file doesn't exist or occur other file error.
     * @throws java.lang.ClassNotFoundException
     *                             If file contains unknow class. For example doesn't contains schema, but other classes.
     * @see cz.green.event.Desktop#init(cz.green.event.Container)
     */
    public void loadFromFile(String fileName) throws java.io.IOException, ClassNotFoundException {
/*	Node node;
	NodeList nl;
	DOMParser parser = new DOMParser();

	try {
		parser.parse(fileName);
	} catch (SAXException se) {
		se.printStackTrace();
	} catch (IOException ioe) {
		ioe.printStackTrace();
	}
	Document doc = ((DOMParser) parser).getDocument();
	if (doc != null) try {
		ERDocument erdoc = new ERDocument(doc);
		nl=doc.getElementsByTagName("schema");
		erdoc.setNL(nl);
		int l=(new Integer(erdoc.getValue("left"))).intValue();
		int t=(new Integer(erdoc.getValue("top"))).intValue();
		int w=(new Integer(erdoc.getValue("width"))).intValue();
		int h=(new Integer(erdoc.getValue("height"))).intValue();
		cz.green.event.Desktop d = new cz.green.event.Desktop(this,l,t,w,h);
		desktop = d;
		d.setScale((new Float(erdoc.getValue("scale"))).floatValue());
		setScale(d.getScale());
		repaint();
	} catch (Exception e) {
	}
/*		nl=doc.getElementsByTagName("schema");
		for (int i=0;i<nl.getLength();i++) {
			node=nl.item(i);
			System.out.println(node);
			node=node.getFirstChild();
			while (node!=null) {
				if (node.getNodeType()==1) {
					if (node.hasChildNodes())
						System.out.println("    "+node.getNodeName()+" "+node.getFirstChild().getNodeValue());
				}
					node=node.getNextSibling();
			} 
		}
	}

	if (true) return;
*/
        java.io.FileInputStream f = null;
        try {
            f = new java.io.FileInputStream(fileName);
            java.io.ObjectInputStream s = new java.io.ObjectInputStream(f);
            cz.green.event.Desktop d = (cz.green.event.Desktop) s.readObject();
            d.init(this);
            desktop = d;
            setScale(d.getScale());
            repaint();
        } finally {
            if (f != null)
                f.close();
        }
    }

    /**
     * Same as derived method, but instead of creting windows (groups) as instances
     * <code>cz.green.event.Window</code> (<code>cz.green.event.Group</code>) creates it
     * as instances <code>cz.green.eventtool.Window</code> (<code>cz.green.eventtool.Group</code>).
     * It is necessary for new features as serializing and printing.
     *
     * @param e The mouse event.
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(java.awt.event.MouseEvent e) {
        int x, y, pom;
        float scale = desktop.getScale();
        x = (int) (e.getX() * scale);
        y = (int) (e.getY() * scale);
        switch (workMode) {
            case ADDING_WINDOW:
            case ADDING_GROUP:
                if (cooX > x) {
                    pom = x;
                    x = cooX;
                    cooX = pom;
                }
                if (cooY > y) {
                    pom = y;
                    y = cooY;
                    cooY = pom;
                }
                try {
                    if (workMode == ADDING_GROUP)
                        desktop.fallAndHandleEvent(cooX, cooY, new cz.green.event.AddItemEvent(cooX, cooY, new Group(desktop, cooX, cooY, x - cooX, y - cooY), this));
                    else
                        desktop.fallAndHandleEvent(cooX, cooY, new cz.green.event.AddItemEvent(cooX, cooY, new Window(desktop, cooX, cooY, x - cooX, y - cooY), this));
                } catch (Exception ex) {
                    return;
                }
                break;
            default:
                super.mouseReleased(e);
        }
    }

    /**
     * Print the whole schemo on the specified graphic context (<code>g</code>).
     *
     * @param g Where to print.
     */
    public void print(java.awt.Graphics g) {
        ((Printable) desktop).print(g);
    }

    /**
     * Simply stores schema to a file. For storing uses Java Serialization.
     *
     * @param fileName File to which to store schema.
     * @throws java.io.IOException If occur some file error.
     */
    public void saveToFile(String fileName) throws java.io.IOException {
        java.io.FileOutputStream f = null;
        try {
            f = new java.io.FileOutputStream(fileName);
            java.io.ObjectOutputStream s = new java.io.ObjectOutputStream(f);
            s.writeObject(desktop);
            s.flush();
        } finally {
            if (f != null)
                f.close();
        }
    }
}
