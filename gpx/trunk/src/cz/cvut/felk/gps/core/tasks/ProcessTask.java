package cz.cvut.felk.gps.core.tasks;

import com.sun.org.apache.xpath.internal.XPathAPI;
import cz.cvut.felk.gps.swing.Swinger;
import cz.cvut.felk.gps.utilities.LogUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class ProcessTask extends CoreTask<Void, Void> {
    private final File gpxFolder;
    private final File kmlFile;
    //private final static String KML_XPATH = "/kml/Document/Placemark/Polygon/outerBoundaryIs/LinearRing/coordinates";
    private final static String KML_XPATH = "//Polygon[1]/*/*/coordinates[1]";

    private final static String GPX_XPATH = "//trkpt";
    private final static Logger logger = Logger.getLogger(ProcessTask.class.getName());
    private File outDir;
    private Polygon2D polygon2D;
    private DocumentBuilderFactory dfactory;

    public ProcessTask(File gpxFolder, File kmlFile) {
        super();
        this.gpxFolder = gpxFolder;
        this.kmlFile = kmlFile;
        this.outDir = new File(gpxFolder, "out");
    }

    protected Void doInBackground() throws Exception {
        setMessage("Reading polygon");
        polygon2D = readPolygon();
        if (outDir.exists()) {
            final File[] files = outDir.listFiles();
            for (File file : files) {
                file.delete();
            }
        }        
        outDir.mkdirs();
        processGpxFolder();
        return null;
    }

    private void processGpxFolder() throws Exception {

        final File[] files = gpxFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".gpx");
            }
        });
        final int max = files.length + 2;
        this.setProgress(0, 0, max);
        dfactory = DocumentBuilderFactory.newInstance();
        final Transformer serializer = TransformerFactory.newInstance().newTransformer();
        for (int i = 0; i < files.length; i++) {
            if (isCancelled())
                return;
            final File file = files[i];
            setMessage("Processing " + file.getName());
            setProgress(i + 1);
            try {
                processGpxFile(serializer, file);
            } catch (Exception e) {
                logger.severe("Problem in the file " + file.getName());
                LogUtils.processException(logger, e);
                throw e;
            }
        }
        setProgress(max);
        dfactory = null;
    }

    private void processGpxFile(Transformer serializer, File gpxFile) throws Exception {
        InputSource in = new InputSource(new FileInputStream(gpxFile));
        //dfactory.setNamespaceAware(true);
        final Document doc = dfactory.newDocumentBuilder().parse(in);
        logger.info("Querying DOM using " + GPX_XPATH);
        final NodeList list = doc.getElementsByTagName("trkpt");

        Node n;
        List<Node> nodesToRemove = new ArrayList<Node>();
//        while ((n = nl.nextNode()) != null) {
        final int length = list.getLength();
        boolean found = false;
        for (int i = 0; i < length; ++i) {
            //  logger.info("Checking trackpoint");
            n = list.item(i);
            final NamedNodeMap attrs = n.getAttributes();
            final String latitude = attrs.getNamedItem("lat").getTextContent();
            final String longitude = attrs.getNamedItem("lon").getTextContent();
            final GeoPoint testPoint = new GeoPoint(longitude, latitude);
            if (polygon2D.contains(testPoint)) {
                logger.fine("found point in polygon");
//                final Attr attribute = doc.createAttribute("inPolygon");
//                attribute.setTextContent("yes");
//                ((Element) n).setAttributeNode(attribute);
                found = true;
            } else nodesToRemove.add(n);
        }
        for (Node node : nodesToRemove) {
            node.getParentNode().removeChild(node);
        }
        nodesToRemove.clear();
        if (!found)
            logger.warning("File " + gpxFile.getName() + " does not intersects with selected polygon");
        else {            
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(new File(outDir, gpxFile.getName()));
                serializer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(stream)));
            } finally {
                if (stream != null)
                    stream.close();
            }
        }
        in.getByteStream().close();
    }

    private Polygon2D readPolygon() throws ParserConfigurationException, TransformerException, IOException, SAXException {
        InputSource in = new InputSource(new FileInputStream(kmlFile));
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        Document doc = dfactory.newDocumentBuilder().parse(in);

        // Set up an identity transformer to use as serializer.
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // Use the simple XPath API to select a nodeIterator.
        setMessage("Querying DOM using " + KML_XPATH);
        NodeIterator nl = XPathAPI.selectNodeIterator(doc, KML_XPATH);

        // Serialize the found nodes to System.out.
        Node n = nl.nextNode();
        if (n != null) {
            final String nodeValue = n.getTextContent();
            logger.info("coordinates nodeValue = " + nodeValue);
            final Polygon2D polygon2D = parsePolygonCoordinates(nodeValue);
            logger.info("polygon2D = " + polygon2D);
            return polygon2D;
        } else throw new IllegalStateException("Polygon coordinates not found");
    }

    private Polygon2D parsePolygonCoordinates(String value) {
        List<GeoPoint> pointList = new ArrayList<GeoPoint>();
        final String[] points = value.trim().split(",0");
        for (String point : points) {
            final String[] longLat = point.trim().split(",");
            if (longLat.length != 2)
                throw new IllegalStateException("Invalid coordinates format");
            final GeoPoint newPoint = new GeoPoint(longLat[0], longLat[1]);
            logger.info("parsing polygon newPoint = " + newPoint);
            pointList.add(newPoint);
        }
        final GeoPoint[] pointArray = new GeoPoint[pointList.size()];
        if (pointArray.length < 3)
            throw new IllegalStateException("Not enough coordinates to create polygon");
        return new Polygon2D(pointList.toArray(pointArray));
    }

    /**
     * Decide if the node is text, and so must be handled specially
     */
    static boolean isTextNode(Node n) {
        if (n == null)
            return false;
        short nodeType = n.getNodeType();
        return nodeType == Node.CDATA_SECTION_NODE || nodeType == Node.TEXT_NODE;
    }


    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
        Swinger.showErrorDialog("errorProcessing", cause, false);
        super.failed(cause);
    }

    @Override
    protected void succeeded(Void result) {
        super.succeeded(result);
        Swinger.showInformationDialog("Succesfully proceeded.");
    }
}
