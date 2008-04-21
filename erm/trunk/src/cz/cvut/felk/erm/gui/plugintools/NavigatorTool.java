package cz.cvut.felk.erm.gui.plugintools;

import cz.cvut.felk.erm.gui.managers.AreaManager;
import cz.cvut.felk.erm.gui.managers.FileInstance;
import cz.cvut.felk.erm.gui.managers.PluginTool;
import org.jgraph.event.GraphLayoutCacheEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class NavigatorTool extends PluginTool implements PropertyChangeListener, AdjustmentListener {
    private final static Logger logger = Logger.getLogger(NavigatorTool.class.getName());


    /**
     * Shared cursor objects to avoid expensive constructor calls.
     */
    protected static final Cursor CURSOR_DEFAULT = new Cursor(
            Cursor.DEFAULT_CURSOR),
            CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);

    /**
     * Shared cursor objects to avoid expensive constructor calls.
     */
    public static Color DEFAULT_BACKGROUND = Color.lightGray;

    /**
     * Component listener to udpate the scale.
     */
    protected ComponentListener componentListener = new ComponentAdapter() {

        /*
           * (non-Javadoc)
           */
        public void componentResized(ComponentEvent e) {
            updateScale();
        }

    };

    /**
     * Holds the backing graph and references the displayed (current) graph.
     */
    protected JComponent backingGraph;

    /**
     * Weak reference to the current graph.
     */
    protected WeakReference currentGraph;

    /**
     * Holds the navigator pane the displays the backing graph.
     */
    protected NavigatorPane navigatorPane;

    /**
     * Specifies the maximum scale for the navigator view. Default is 0.5
     */
    protected double maximumScale = 0.5;

    /**
     * Specifies whether the background image of the enclosing diagram pane
     * should be visible. Default is true.
     */
    protected boolean isBackgroundImageVisible = true;


    public NavigatorTool() {
        super();
        final Container contentPane = getContentPane();
        contentPane.add(new JScrollPane(new JList()), BorderLayout.CENTER);

        // Add a label above List to describe what is being shown

        getComponent().setPreferredSize(new Dimension(250, 300));
    }

    public final String getName() {
        return "PluginTool";
    }

    public String getTabName() {
        return getName();
    }

    public final Icon getIcon() {
        return null;
    }

    public final String getTip() {
        return "Navigator";
    }

    public void activate() {
        setFileInstance(AreaManager.getInstance().getActiveInstance());
        logger.info("PluginTool1 activated");
    }

    public void deactivate() {
        setFileInstance(null);
        logger.info("PluginTool1 deactivated");
    }

    public void updateData() {
//        if (editor != null && listModel != null)
//            listModel.updateDataList();
    }

    public void setFileInstance(final FileInstance instance) {

        if (instance != null)
            setCurrentGraph(instance.getContentArea());
        else
            setCurrentGraph(null);
        getContentPane().setEnabled(currentGraph != null);
    }


    public boolean closeSoft() {
        return true;  //implement - call to super class
    }

    public void closeHard() {
        //implement - call to super class
    }


    /**
     * Returns the navigator pane that contains the backing graph.
     *
     * @return Returns the navigator pane.
     */
    public NavigatorPane getScrollPane() {
        return navigatorPane;
    }

    /**
     * Returns the maximum scale to be used for the backing graph.
     *
     * @return Returns the maximumScale.
     */
    public double getMaximumScale() {
        return maximumScale;
    }


    /**
     * Sets the maximum scale to be used for the backing graph.
     *
     * @param maximumScale The maximumScale to set.
     */
    public void setMaximumScale(double maximumScale) {
        this.maximumScale = maximumScale;
    }

    /**
     * Returns the backing graph that is used to display {@link #currentGraph}.
     *
     * @return Returns the backing graph.
     */
    public JComponent getBackingGraph() {
        return backingGraph;
    }

    /**
     * Sets the backing graph that is used to display {@link #currentGraph}.
     *
     * @param backingGraph The backing graph to set.
     */
    public void setBackingGraph(JComponent backingGraph) {
        this.backingGraph = backingGraph;
    }

    /**
     * Returns the graph that is currently displayed.
     *
     * @return Returns the backing graph.
     */
    public JComponent getCurrentGraph() {
        return (JComponent) ((currentGraph != null) ? currentGraph.get() : null);
    }

    /**
     * @return Returns the isBackgroundImageVisible.
     */
    public boolean isBackgroundImageVisible() {
        return isBackgroundImageVisible;
    }

    /**
     * @param isBackgroundImageVisible The isBackgroundImageVisible to set.
     */
    public void setBackgroundImageVisible(boolean isBackgroundImageVisible) {
        this.isBackgroundImageVisible = isBackgroundImageVisible;
    }

    /**
     * Sets the graph that is currently displayed.
     *
     * @param sourceGraph The current graph to set.
     */
    public void setCurrentGraph(JComponent sourceGraph) {
        if (sourceGraph == null || getParentGraph(sourceGraph) == null) {
            if (sourceGraph != null) {
                JComponent oldValue = getCurrentGraph();

                // Removes listeners from the previous graph
                if (oldValue != null && sourceGraph != oldValue) {
//                    oldValue.getModel().removeGraphModelListener(this);
//                    oldValue.getGraphLayoutCache()
//                            .removeGraphLayoutCacheListener(this);
                    oldValue.removePropertyChangeListener(this);
                    JScrollPane scrollPane = getParentScrollPane(oldValue);
                    if (scrollPane != null) {
                        scrollPane.removeComponentListener(componentListener);
                        scrollPane.getVerticalScrollBar()
                                .removeAdjustmentListener(this);
                        scrollPane.getHorizontalScrollBar()
                                .removeAdjustmentListener(this);
                        scrollPane.removePropertyChangeListener(this);
                    }

                    // Restores the layout cache of the backing graph
                    //backingGraph.setGraphLayoutCache(initialLayoutCache);
                }
                this.currentGraph = new WeakReference(sourceGraph);

                // Installs change listeners to update the size
                if (sourceGraph != null) {
                    sourceGraph.addPropertyChangeListener(this);
                    JScrollPane currentScrollPane = getParentScrollPane(sourceGraph);
                    if (currentScrollPane != null) {
                        currentScrollPane
                                .addComponentListener(componentListener);
                        currentScrollPane.getVerticalScrollBar()
                                .addAdjustmentListener(this);
                        currentScrollPane.getHorizontalScrollBar()
                                .addAdjustmentListener(this);
                        currentScrollPane.addPropertyChangeListener(this);
                    }
                }
                updateScale();
            }
        }
    }

    /**
     * Updates the scale of the backing graph.
     */
    protected void updateScale() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JComponent graph = getCurrentGraph();
                if (graph != null) {
                    Dimension d = graph.getPreferredSize();
                    Dimension b = graph.getBounds().getSize();
                    d.width = Math.max(d.width, b.width);
                    b.height = Math.max(d.height, b.height);
                    // double scale = graph.getScale();
                    double scale = 1.0;
                    d.setSize(d.width * 1 / scale, d.height * 1 / scale);
                    Dimension s = getScrollPane().getViewport().getSize();
                    double sx = s.getWidth() / d.getWidth();
                    double sy = s.getHeight() / d.getHeight();
                    scale = Math.min(Math.min(sx, sy), getMaximumScale());
                    //getBackingGraph().setScale(scale);
                    //getBackingGraph().setScale(scale);
                    getContentPane().repaint();
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     */
    public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
        updateScale();
    }

    /*
     * (non-Javadoc)
     */
    public void propertyChange(PropertyChangeEvent event) {
        updateScale();
    }

    /*
     * (non-Javadoc)
     */
    public void adjustmentValueChanged(AdjustmentEvent e) {
        navigatorPane.repaint();
    }

    /**
     * Helper method that returns the parent scrollpane for the specified
     * component in the component hierarchy. If the component is itself a
     * scrollpane then it is returned.
     *
     * @return Returns the parent scrollpane or component.
     */
    public static JScrollPane getParentScrollPane(Component component) {
        while (component != null) {
            if (component instanceof JScrollPane)
                return (JScrollPane) component;
            component = component.getParent();
        }
        return null;
    }

    /**
     * Helper method that returns the parent JComponent for the specified component
     * in the component hierarchy. The component itself is never returned.
     *
     * @return Returns the parent scrollpane or component.
     */
    public static JComponent getParentGraph(Component component) {
        do {
            component = component.getParent();
            if (component instanceof JComponent)
                return (JComponent) component;
        } while (component != null);
        return null;
    }

    /**
     * Scrollpane that implements special painting used for the navigator
     * preview.
     */
    public class NavigatorPane extends JScrollPane implements MouseListener,
            MouseMotionListener {

        /**
         * Holds the bounds of the finder (red box).
         */
        protected Rectangle currentViewport = new Rectangle();

        /**
         * Holds the location of the last mouse event.
         */
        protected Point lastPoint = null;

        /**
         * Constructs a new navigator pane using the specified backing graph to
         * display the preview.
         *
         * @param backingGraph The backing graph to use for rendering.
         */
        public NavigatorPane(JComponent backingGraph) {
            super(backingGraph);
            setOpaque(false);
            getViewport().setOpaque(false);
        }

        /**
         * Paints the navigator pane on the specified graphics.
         *
         * @param g The graphics to paint the navigator to.
         */
        public void paint(Graphics g) {
            JComponent graph = getCurrentGraph();
            JScrollPane scrollPane = getParentScrollPane(graph);
            g.setColor(DEFAULT_BACKGROUND);
            g.fillRect(0, 0, getWidth(), getHeight());
            if (scrollPane != null && graph != null) {
                JViewport viewport = scrollPane.getViewport();
                Rectangle rect = viewport.getViewRect();
                //double scale = backingGraph.getScale() / graph.getScale();
                double scale = 1.0;
                Dimension pSize = graph.getPreferredSize();
                g.setColor(getBackground());
                g.fillRect(0, 0, (int) (pSize.width * scale),
                        (int) (pSize.height * scale));
                g.setColor(Color.WHITE);
                currentViewport.setFrame((int) (rect.getX() * scale),
                        (int) (rect.getY() * scale),
                        (int) (rect.getWidth() * scale), (int) (rect
                        .getHeight() * scale));
                g.fillRect(currentViewport.x, currentViewport.y,
                        currentViewport.width, currentViewport.height);

//                // Draws the background image that the editor diagram pane uses
//                if (isBackgroundImageVisible()
//                        && scrollPane instanceof JComponentEditorDiagramPane) {
//                    JComponentEditorDiagramPane editorPane = (JComponentEditorDiagramPane) scrollPane;
//
//                    ImageIcon icon = editorPane.getBackgroundImage();
//                    if (icon != null) {
//                        g.drawImage(icon.getImage(), 0, 0, (int) (icon
//                                .getIconWidth() * scale), (int) (icon
//                                .getIconHeight() * scale), this);
//                    }
//                }
//
//                super.paint(g);
//                g.setColor(Color.RED);
//                g.drawRect(currentViewport.x, currentViewport.y,
//                        currentViewport.width, currentViewport.height);
            }
        }

        /*
         * (non-Javadoc)
         */
        public void mouseClicked(MouseEvent e) {
            // empty
        }

        /*
         * (non-Javadoc)
         */
        public void mousePressed(MouseEvent e) {
            if (currentViewport.contains(e.getX(), e.getY()))
                lastPoint = e.getPoint();
        }

        /*
         * (non-Javadoc)
         */
        public void mouseReleased(MouseEvent e) {
            lastPoint = null;
        }

        /*
         * (non-Javadoc)
         */
        public void mouseEntered(MouseEvent e) {
            // empty

        }

        /*
         * (non-Javadoc)
         */
        public void mouseExited(MouseEvent e) {
            // empty

        }

        /*
         * (non-Javadoc)
         */
        public void mouseDragged(MouseEvent e) {
            if (lastPoint != null) {
                JComponent graph = getCurrentGraph();
                JScrollPane scrollPane = getParentScrollPane(graph);
                if (scrollPane != null && currentGraph != null) {
                    JViewport viewport = scrollPane.getViewport();
                    Rectangle rect = viewport.getViewRect();
                    //double scale = backingGraph.getScale() / graph.getScale();
                    double scale = 1.0;

                    double x = (e.getX() - lastPoint.getX()) / scale;
                    double y = (e.getY() - lastPoint.getY()) / scale;
                    lastPoint = e.getPoint();
                    x = rect.getX() + ((x > 0) ? rect.getWidth() : 0) + x;
                    y = rect.getY() + ((y > 0) ? rect.getHeight() : 0) + y;
                    //Point2D pt = new Point2D.Double(x, y);
                    viewport.scrollRectToVisible(new Rectangle((int) x, (int) y, 100, 100));
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
            if (currentViewport.contains(e.getPoint()))
                setCursor(CURSOR_HAND);
            else
                setCursor(CURSOR_DEFAULT);
        }

    }


}
