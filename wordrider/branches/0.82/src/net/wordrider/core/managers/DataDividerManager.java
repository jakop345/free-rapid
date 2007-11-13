package net.wordrider.core.managers;

import info.clearthought.layout.TableLayout;
import net.wordrider.core.actions.*;
import net.wordrider.core.managers.interfaces.IFileChangeListener;
import net.wordrider.core.managers.interfaces.IRiderManager;
import net.wordrider.core.swing.CustomLayoutConstraints;
import net.wordrider.gui.LookAndFeels;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class DataDividerManager implements IRiderManager, IFileChangeListener, PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(DataDividerManager.class.getName());
    private final JSplitPane splitPane;
    private boolean isGraphicMenu = false;
    private final Component rightComponent;
    private Component graphicMenu = null;
    private JLabel labelWelcome = null;


    public DataDividerManager(final JPanel parentPane, final Component leftComponent, final Component rightComponent) {
        this.rightComponent = rightComponent;
        parentPane.add(splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComponent, rightComponent), BorderLayout.CENTER);
        //        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        // splitPane.setLastDividerLocation(300);
        //splitPane.setLayout(new BorderLayout());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(8);
        //splitPane.setBackground(Color.GREEN);
        //     splitPane.setDividerLocation(50);
    }

    // --Commented out by Inspection START (4.2.05 16:18):
    //    public final void setLeftComponent(final Component component) {
    //        splitPane.setLeftComponent(component);
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:18)

    public final Component getManagerComponent() {
        return splitPane;
    }

    // --Commented out by Inspection START (4.2.05 16:18):
    //    public final void setRightComponent(final Component component) {
    //        splitPane.setRightComponent(component);
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:18)


    public void fileWasOpened(final FileChangeEvent event) {
        if (isGraphicMenu) {
            splitPane.setRightComponent(rightComponent);
            isGraphicMenu = false;
        }
    }

    public void fileWasClosed(final FileChangeEvent event) {
        if (!((AreaManager) event.getSource()).hasOpenedInstance())
            setGraphicMenu();
    }

    public void setGraphicMenu() {
        if (!isGraphicMenu) {
            splitPane.setRightComponent(getGraphicMenu());
            isGraphicMenu = true;
            logger.fine("Setting graphic menu");
        }
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
        final JPanel jPanel = new BackGroundPanel(mgr);
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
        //jPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        labelWelcome = Swinger.getLabel("gmenu.labelWelcome");
        labelWelcome.setFont(labelWelcome.getFont().deriveFont(Font.BOLD, 28));
        final Border emptyBorder = BorderFactory.createEmptyBorder(4, 10, 4, 4);

        labelWelcome.setBorder(emptyBorder);
        labelWelcome.setForeground(Color.WHITE);
        labelWelcome.setOpaque(true);
        labelWelcome.setHorizontalAlignment(JLabel.LEFT);
        updateLabelWelcomeColor();
        

        final Component item1 = Swinger.getGraphicItem("gmenu.itemNew", "editor.gif", CreateNewFileAction.getInstance());
        final Component item2 = Swinger.getGraphicItem("gmenu.itemOpen", "open_big.gif", OpenFileAction.getInstance());
        final Component item3 = Swinger.getGraphicItem("gmenu.itemRecent", "open_big.gif", new OpenRecentsAction());
        final Component item4 = Swinger.getGraphicItem("gmenu.itemDemo", "flash.gif", new WebAction(Consts.ONLINE_TUTORIAL));
        final Component item5 = Swinger.getGraphicItem("gmenu.itemSample", "editor.gif", new OpenSampleFileAction());
        final Component item6 = Swinger.getGraphicItem("gmenu.itemKeymap", "adobe.gif", OpenKeymapAction.getInstance());
        final Component item7 = Swinger.getGraphicItem("gmenu.itemWeb", "011.png", VisitHomepageAction.getInstance());

        jPanel.add(labelWelcome, new CustomLayoutConstraints(0, 0, 3, 1));
        jPanel.add(leftPanel, new CustomLayoutConstraints(0, 1));
        jPanel.add(rightPanel, new CustomLayoutConstraints(1, 1));

        leftPanel.add(Swinger.getTitleComponent2("gmenu.sectionNew"), new CustomLayoutConstraints(0, 0));
        leftPanel.add(item1, new CustomLayoutConstraints(0, 1));
        leftPanel.add(Swinger.getTitleComponent2("gmenu.sectionOpen"), new CustomLayoutConstraints(0, 2));
        leftPanel.add(item2, new CustomLayoutConstraints(0, 3));
        leftPanel.add(item3, new CustomLayoutConstraints(0, 4));
        rightPanel.add(Swinger.getTitleComponent2("gmenu.sectionQuickStart"), new CustomLayoutConstraints(0, 0));
        rightPanel.add(item4, new CustomLayoutConstraints(0, 1));
        rightPanel.add(item5, new CustomLayoutConstraints(0, 2));
        rightPanel.add(Swinger.getTitleComponent2("gmenu.sectionQuickLinks"), new CustomLayoutConstraints(0, 3));
        rightPanel.add(item6, new CustomLayoutConstraints(0, 4));
        rightPanel.add(item7, new CustomLayoutConstraints(0, 5));
        graphicMenu = jPanel;
        return jPanel;
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
        private Image image;
        //   private BufferedImage bimage;


        public BackGroundPanel(LayoutManager layout) {
            super(layout);
            this.image = Swinger.getIconImage("calcbg.png");
//            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TRANSLUCENT);
//            final Graphics2D g2d = bimage.createGraphics();
//            g2d.drawImage(image, 0, 0, null);
//            g2d.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            final Graphics2D g2 = (Graphics2D) g;
            final Composite composite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            final int width = image.getWidth(null);
            final int height = image.getHeight(null);
            g2.drawImage(image, this.getWidth() - width, this.getHeight() - height, null);
            g2.setComposite(composite);
        }

    }

}
