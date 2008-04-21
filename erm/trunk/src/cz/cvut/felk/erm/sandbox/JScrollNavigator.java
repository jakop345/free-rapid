package cz.cvut.felk.erm.sandbox;

/**
 * @author Ladislav Vitasek
 */

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class JScrollNavigator extends JDialog implements ComponentListener,
        AdjustmentListener {

    private JScrollPane jScrollPane;

    private NavBox overBox = new NavBox();

    boolean isAdjusting = false;

    public JScrollNavigator() {

        this.setTitle("Navigation");
        this.setSize(new Dimension(80, 100));

        this.getContentPane().setBackground(Color.BLACK);

        this.getContentPane().setLayout(null);
        this.getContentPane().add(overBox);

        this.getContentPane().addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                updateOverBox();
            }

        });

        overBox.addComponentListener(this);
    }

    public void setJScrollPane(JScrollPane jScrollPane) {
        this.jScrollPane = jScrollPane;

        Component view = jScrollPane.getViewport().getView();

        if (view != null) {
            view.addComponentListener(this);
            jScrollPane.getViewport().addComponentListener(this);
            jScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
            jScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
            updateOverBox();
        }

    }

    private void updateOverBox() {
        isAdjusting = true;

        JViewport viewport = this.jScrollPane.getViewport();

        Dimension d = viewport.getViewSize();
        Rectangle vRect = viewport.getViewRect();

        int vWidth = d.width;
        int vHeight = d.height;

        int w = this.getContentPane().getWidth();
        int h = this.getContentPane().getHeight();

        float xMult = (float) w / vWidth;
        float yMult = (float) h / vHeight;

        int newX = (int) (vRect.x * xMult);
        int newY = (int) (vRect.y * yMult);
        int newW = (int) (vRect.width * xMult);
        int newH = (int) (vRect.height * yMult);

        overBox.setLocation(newX, newY);
        overBox.setSize(newW, newH);

        isAdjusting = false;

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        JScrollPane jsp = new JScrollPane();
        JTextArea textArea = new JTextArea();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            buffer.append("test" + i + "\n");
        }
        textArea.setText(buffer.toString());

        jsp.setViewportView(textArea);

        JScrollNavigator nav = new JScrollNavigator();
        nav.setJScrollPane(jsp);

        JFrame mFrame = new JFrame();
        mFrame.setTitle("JScrollNavigator Test");

        mFrame.setSize(800, 600);
        mFrame.getContentPane().add(jsp);
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        mFrame.setLocation((screenDim.width - mFrame.getSize().width) / 2,
                (screenDim.height - mFrame.getSize().height) / 2);

        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mFrame.show();

        nav.setModal(false);
        nav.setVisible(true);
    }

    public void componentResized(ComponentEvent e) {
        if (e.getSource() == jScrollPane.getViewport()
                || e.getSource() == jScrollPane.getViewport().getView()) {
            updateOverBox();
        }
    }

    public void componentMoved(ComponentEvent e) {
        if (e.getSource() == overBox) {
            isAdjusting = true;

            Rectangle r = overBox.getBounds();

            JViewport viewport = this.jScrollPane.getViewport();
            Dimension d = viewport.getViewSize();

            int vWidth = d.width;
            int vHeight = d.height;

            int w = this.getContentPane().getWidth();
            int h = this.getContentPane().getHeight();

            float xMult = (float) vWidth / w;
            float yMult = (float) vHeight / h;

            int newX = (int) (r.x * xMult);
            int newY = (int) (r.y * yMult);
            int newW = (int) (r.width * xMult);
            int newH = (int) (r.height * yMult);

            Rectangle newRect = new Rectangle(newX, newY, newW, newH);

            ((JComponent) viewport.getView()).scrollRectToVisible(newRect);

            isAdjusting = false;
        }
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (!isAdjusting) {
            updateOverBox();
        }
    }

    class NavBox extends JPanel {
        boolean dragging = false;

        Point origin = null;

        int originX = -1;

        int originY = -1;

        public NavBox() {
            this.setBorder(new LineBorder(Color.GREEN, 1));

            this.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    origin = SwingUtilities.convertPoint(NavBox.this, e.getPoint(), NavBox.this.getParent());
                    originX = NavBox.this.getX();
                    originY = NavBox.this.getY();
                }

                public void mouseReleased(MouseEvent e) {
                    origin = null;
                    originX = -1;
                    originY = -1;
                }

            });

            this.addMouseMotionListener(new MouseMotionAdapter() {

                public void mouseDragged(MouseEvent e) {
                    NavBox box = NavBox.this;
                    Container c = box.getParent();

                    int leftBound = -originX;
                    int rightBound = c.getWidth() - box.getWidth() - originX;
                    int topBound = -originY;
                    int bottomBound = c.getHeight() - box.getHeight() - originY;

                    Point p = SwingUtilities.convertPoint(box, e.getPoint(), c);

                    int xDiff = p.x - origin.x;
                    int yDiff = p.y - origin.y;

                    if (xDiff < leftBound) {
                        xDiff = leftBound;
                    }

                    if (xDiff > rightBound) {
                        xDiff = rightBound;
                    }

                    if (yDiff < topBound) {
                        yDiff = topBound;
                    }

                    if (yDiff > bottomBound) {
                        yDiff = bottomBound;
                    }

                    box.setLocation(originX + xDiff, originY + yDiff);
                }

            });
        }
    }

}