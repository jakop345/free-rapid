package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.gui.actions.FileTransferHandlerImpl;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.swing.CustomLayoutConstraints;
import cz.cvut.felk.erm.swing.LookAndFeels;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.swing.components.GraphicMenuItem;
import info.clearthought.layout.TableLayout;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public final class BackgroundManager implements IFileChangeListener, PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(BackgroundManager.class.getName());
    private boolean isGraphicMenu = false;
    private Component graphicMenu = null;
    private JLabel labelWelcome = null;
    private LayoutManager layout;
    private final ManagerDirector director;


    public BackgroundManager(ManagerDirector director) {
        this.director = director;
    }


    public void fileWasOpened(final FileChangeEvent event) {
        if (isGraphicMenu) {
            final Container container = this.getContainerUI();
            container.setLayout(layout);
            container.remove(graphicMenu);
            container.repaint();
            isGraphicMenu = false;
        }
    }

    public void fileWasClosed(final FileChangeEvent event) {
        if (!((AreaManager) event.getSource()).hasOpenedInstance())
            setGraphicMenu();
    }

    public void setGraphicMenu() {
        if (!isGraphicMenu) {
            final Container container = this.getContainerUI();
            layout = container.getLayout();
            container.setLayout(new BorderLayout());
            container.add(getGraphicMenu(), BorderLayout.CENTER);
            isGraphicMenu = true;
            director.getPluginToolsManager().setPluginToolsVisible(false);
            container.doLayout();
            logger.fine("Setting graphic menu");
        }
    }

    private Container getContainerUI() {
        return director.getDockingManager().getContentPane();
    }

    private Component getGraphicMenu() {
        if (graphicMenu != null)
            return graphicMenu;
        UIManager.addPropertyChangeListener(this);
        final double f = TableLayout.FILL;
        final double p = TableLayout.PREFERRED;
        final TableLayout mgr = new TableLayout(new double[]{p, p, f}, new double[]{p, p, p});
        mgr.setHGap(10);
        mgr.setVGap(5);
        final JPanel bgPanel = new BackGroundPanel(mgr);
        bgPanel.setTransferHandler(new FileTransferHandlerImpl());
        bgPanel.setPreferredSize(new Dimension(650, 500));
        bgPanel.setName("BackGroundPanel");
        final TableLayout mgr2 = new TableLayout(new double[]{p}, new double[]{p, p, p, p, p, p});
        mgr.setHGap(10);
        mgr.setVGap(5);
        final JPanel leftPanel = new JPanel(mgr2);
        leftPanel.setOpaque(false);
        leftPanel.setBackground(null);
        final TableLayout mgr3 = new TableLayout(new double[]{p}, new double[]{p, p, p, p, p, p});
        mgr.setHGap(10);
        mgr.setVGap(5);
        final JPanel rightPanel = new JPanel(mgr3);
        rightPanel.setOpaque(false);
        rightPanel.setBackground(null);
        //bgPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelWelcome = new JLabel();
        labelWelcome.setName("gmenu.labelWelcome");
        labelWelcome.setFont(labelWelcome.getFont().deriveFont(Font.BOLD, 28));
        final Border emptyBorder = BorderFactory.createEmptyBorder(4, 10, 4, 4);

        labelWelcome.setBorder(emptyBorder);
        labelWelcome.setForeground(Color.WHITE);
        labelWelcome.setOpaque(true);
        labelWelcome.setHorizontalAlignment(JLabel.LEFT);
        updateLabelWelcomeColor();

        final ResourceMap map = Swinger.getResourceMap(BackgroundManager.class);

        final Component item1 = getGraphicItem(map, "gmenu.itemNew", Swinger.getAction("newScheme"));
        final Component item2 = getGraphicItem(map, "gmenu.itemOpen", Swinger.getAction("openScheme"));
        final Component item3 = getGraphicItem(map, "gmenu.itemRecent", Swinger.getAction("openScheme"));
        final Component item4 = getGraphicItem(map, "gmenu.itemDemo", Swinger.getAction("openScheme"));
        final Component item5 = getGraphicItem(map, "gmenu.itemSample", Swinger.getAction("openScheme"));
        //final Component item6 = getGraphicItem(map, "gmenu.itemKeymap", Swinger.getAction("openScheme"));
        final Component item7 = getGraphicItem(map, "gmenu.itemWeb", Swinger.getAction("visitHomepage"));

        bgPanel.add(labelWelcome, new CustomLayoutConstraints(0, 0, 3, 1));
        bgPanel.add(leftPanel, new CustomLayoutConstraints(0, 1));
        bgPanel.add(rightPanel, new CustomLayoutConstraints(1, 1));

        leftPanel.add(Swinger.getTitleComponent2("gmenu.sectionNew"), new CustomLayoutConstraints(0, 0));
        leftPanel.add(item1, new CustomLayoutConstraints(0, 1));
        leftPanel.add(Swinger.getTitleComponent2("gmenu.sectionOpen"), new CustomLayoutConstraints(0, 2));
        leftPanel.add(item2, new CustomLayoutConstraints(0, 3));
        leftPanel.add(item3, new CustomLayoutConstraints(0, 4));
        rightPanel.add(Swinger.getTitleComponent2("gmenu.sectionQuickStart"), new CustomLayoutConstraints(0, 0));
        rightPanel.add(item4, new CustomLayoutConstraints(0, 1));
        rightPanel.add(item5, new CustomLayoutConstraints(0, 2));
        rightPanel.add(Swinger.getTitleComponent2("gmenu.sectionQuickLinks"), new CustomLayoutConstraints(0, 3));
        //rightPanel.add(item6, new CustomLayoutConstraints(0, 4));
        rightPanel.add(item7, new CustomLayoutConstraints(0, 5));
        graphicMenu = bgPanel;
        map.injectComponents(bgPanel);
        return graphicMenu;
    }

    private void updateLabelWelcomeColor() {
        if (labelWelcome != null) {
            labelWelcome.setBackground(UIManager.getDefaults().getColor("Menu.selectionBackground"));
            if (labelWelcome.isVisible()) {
                labelWelcome.validate();
                labelWelcome.repaint();
            }
        }
    }


    public void propertyChange(PropertyChangeEvent evt) {
        if (graphicMenu == null)
            return;
        if (evt.getPropertyName().equals("lookAndFeel")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    LookAndFeels.updateComponentTreeUI(graphicMenu);
                    updateLabelWelcomeColor();
                }
            });
        }
    }

    private static class BackGroundPanel extends JPanel {
        private ImageIcon icon;
        //   private BufferedImage bimage;


        public BackGroundPanel(LayoutManager layout) {
            super(layout);
            this.icon = Swinger.getIconImage(Swinger.getResourceMap(BackgroundManager.class), "gmenu.bgicon");
//            bimage = new BufferedImage(icon.getWidth(null), icon.getHeight(null), BufferedImage.TRANSLUCENT);
//            final Graphics2D g2d = bimage.createGraphics();
//            g2d.drawImage(icon, 0, 0, null);
//            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;
            final Composite composite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            final int width = icon.getImage().getWidth(null);
            final int height = icon.getImage().getHeight(null);
            g2.drawImage(icon.getImage(), this.getWidth() - width, this.getHeight() - height, null);
            g2.setComposite(composite);
        }

    }

    private JComponent getGraphicItem(ResourceMap map, String title, Action action) {
        //  map.injectComponent(menuItem);
        return new GraphicMenuItem(title, map.getString(title + ".header"), map.getString(title + ".comment"), action);
    }

}
