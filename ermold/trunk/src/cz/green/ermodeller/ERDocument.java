package cz.green.ermodeller;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for working with ER schemas in XML files
 */

public class ERDocument {
    private org.w3c.dom.Document doc;
    //private NodeList nl;
    private NodeList[] nodeList;
    private String name;
    //private int pos = 0;
    private int[] position = {0, 0, 0};
    private int pos1 = 0;

    public ERDocument(Document document) {
        super();
        doc = document;
        nodeList = new NodeList[3];
    }

    public String getNextValue() {
        Node node;
        int i = 0;

        if (position[0] < nodeList[0].getLength()) {
            node = nodeList[0].item(position[0]);
            node = node.getFirstChild();
            while (node != null) {
                if ((name.equals(node.getNodeName()))) {
                    i++;
                    if (i > pos1) {
                        pos1 = i;
                        if (node.hasChildNodes())
                            return node.getFirstChild().getNodeValue();
                        else
                            return node.getNodeValue();
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }

    public String getNextValue(int level) {
        Node node;
        int i = 0;

        if (position[level] < nodeList[level].getLength()) {
            node = nodeList[level].item(position[level]);
            node = node.getFirstChild();
            while (node != null) {
                if ((name.equals(node.getNodeName()))) {
                    i++;
                    if (i > pos1) {
                        pos1 = i;
                        if (node.hasChildNodes())
                            return node.getFirstChild().getNodeValue();
                        else
                            return node.getNodeValue();
                    }
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }


    public String getValue(String name) {
        Node node;

//	for (int i=pos; i < nl.getLength(); i++) {
        if (position[0] < nodeList[0].getLength()) {
            node = nodeList[0].item(position[0]);
            node = node.getFirstChild();
            while (node != null) {
                if ((name.equals(node.getNodeName()))) {
                    if (node.hasChildNodes())
                        return node.getFirstChild().getNodeValue();
                    else
                        return node.getNodeValue();
                }
                node = node.getNextSibling();
            }
        }
        return null;
    }

    public String getValue(String name, int level) {
        Node node;

//	for (int i=pos; i < nl.getLength(); i++) {
        if (nodeList[level] != null) {
            node = nodeList[level].item(position[level]);
            node = node.getFirstChild();
            //System.out.println("hledam na urovni "+level);
            while (node != null) {
                //System.out.println("node name: "+node.getNodeName());
                if ((name.equals(node.getNodeName()))) {
                    if (node.hasChildNodes())
                        return node.getFirstChild().getNodeValue();
                    else
                        return node.getNodeValue();
                }
                node = node.getNextSibling();
            }
            //System.out.println("nenasel :-(");
        }
        return null;
    }

    public boolean next() {
        position[0]++;
        return nodeList[0].item(position[0]) != null;
    }

    public boolean next(int level) {
        boolean someElementNode;
        if (nodeList[level] == null)
            return false;
        someElementNode = false;
        for (int i = position[level] + 1; i < nodeList[level].getLength(); i++)
            if ((!someElementNode) && (nodeList[level].item(i).getNodeType() == Node.ELEMENT_NODE)) {
                someElementNode = true;
                position[level] = i;
            }
        if (!someElementNode) {
            nodeList[level] = null;
            return false;
        }
        for (; (nodeList[level] != null) && (level < 2); level++) {
            nodeList[level + 1] = nodeList[level].item(position[level]).getChildNodes();
            someElementNode = false;
            for (int i = 0; i < nodeList[level + 1].getLength(); i++)
                if ((!someElementNode) && (nodeList[level + 1].item(i).getNodeType() == Node.ELEMENT_NODE)) {
                    someElementNode = true;
                    position[level + 1] = i;
                }
            if ((nodeList[level + 1].getLength() == 0) || (!someElementNode))
                nodeList[level + 1] = null;
        }
        for (; level < 2; level++) {
            nodeList[level] = null;
        }
        return true;
    }

    public boolean setElements(String name) {
        boolean someElementNode;

        nodeList[0] = doc.getElementsByTagName(name);
        position[0] = 0;
        if (nodeList[0] != null && nodeList[0].getLength() > 0) {
            nodeList[1] = nodeList[0].item(position[0]).getChildNodes();
            someElementNode = false;
            for (int i = 0; i < nodeList[1].getLength(); i++)
                if ((!someElementNode) && (nodeList[1].item(i).getNodeType() == Node.ELEMENT_NODE)) {
                    someElementNode = true;
                    position[1] = i;
                }
            if ((nodeList[1].getLength() == 0) || (!someElementNode))
                nodeList[1] = null;
        } else
            nodeList[1] = null;
        if (nodeList[1] != null) {
            nodeList[2] = nodeList[1].item(position[1]).getChildNodes();
            someElementNode = false;
            for (int i = 0; i < nodeList[2].getLength(); i++)
                if ((!someElementNode) && (nodeList[2].item(i).getNodeType() == Node.ELEMENT_NODE)) {
                    someElementNode = true;
                    position[2] = i;
                }
            if ((nodeList[2].getLength() == 0) || (!someElementNode))
                nodeList[2] = null;
        } else
            nodeList[2] = null;

        return (nodeList[0].getLength() != 0);
    }

    public void setNode(String aname) {
        pos1 = 0;
        name = aname;
    }
}