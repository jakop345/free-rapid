package net.wordrider.dialogs.pictures;

import net.wordrider.core.Lng;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Vity
 */
final class InputPicturePanel extends JComponent implements MouseMotionListener, MouseListener, ActionListener, KeyListener {
    private Image img = null;
    private final static int BORDER_X = 4;
    private final static int BORDER_Y = 4;
    private int dx = BORDER_X;
    private int dy = BORDER_Y;
    private int paintImageWidth = 0,
            paintImageHeight = 0;
    private static final float[] dash = new float[]{4.0f};
    private final static Stroke[] strokes = {
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 7.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 6.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 5.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 4.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 3.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 2.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 1.0f),
            new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, dash, 0.0f)};
    private final static int STROKES_COUNT = strokes.length;
    private int selectionX = -1,
            selectionY = -1;
    private int selectionEndX = -1;
    private int selectionEndY = -1;

    private int drawRectX, drawRectY, drawRectEndX, drawRectEndY;
    private int mousePosX = 0,
            mousePosY = 0;

    private Rectangle rectangle = new Rectangle();
    private final String formatString1 = Lng.getLabel("dialog.images.cropinfo");
    private final String formatString2 = Lng.getLabel("dialog.images.cropposition");
    private final FilterDialog filterDialog;

    private final Timer selectionTimer;
    private final Timer movingTimer = new Timer(100, this);

    private float zoomFactor = 0.5f;
    private int origImageWidth;
    private int origImageHeight;
    private int activeStroke = 0;
    private JViewport viewport = null;
    private Point movePoint;
    private final static int SENSITIVE_BORDER = 10;
    private boolean isMoving = false;
    private Rectangle outputRectangle = new Rectangle();
    private static final String RANGE_OUT = " - ";

    private final static float ZOOM_TABLE[] = {1, 2, 5, 6.25f, 8f + 1 / 3, 12.5f, 16f + 2 / 3, 25, 33f + 1 / 3, 50, 66f + 2 / 3, 75, 100, 200, 300, 400, 500, 600, 700, 800, 1200, 1600};

    public InputPicturePanel(final FilterDialog filterDialog) {
        super();
        this.filterDialog = filterDialog;
        this.setBorder(null);
        this.selectionTimer = new Timer(150, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                activeStroke = (activeStroke + 1) % STROKES_COUNT;
                updateContent();
            }
        });
        this.setFocusable(true);
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
    }


    void init() {
        getViewport().addComponentListener(new ComponentListener() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                InputPicturePanel.this.componentResized();
            }

            public void componentShown(ComponentEvent e) {
            }
        });

    }

    private void recalculateDxy(final int newWidth, final int newHeight) {
        final JViewport viewport = getViewport();
        final int viewportWidth = viewport.getWidth();
        final int viewportHeight = viewport.getHeight();
        dx = (newWidth < viewportWidth) ? (viewportWidth - paintImageWidth) / 2 : (newWidth - paintImageWidth) / 2;
        dy = (newHeight < viewportHeight) ? (viewportHeight - paintImageHeight) / 2 : (newHeight - paintImageHeight) / 2;
    }

    private void componentResized() {
        recalculateDxy(getWidth(), getHeight());
        //getViewport().revalidate();
        revalidate();
        updateContent();
    }

    private void updateSelectionInfo() {
        final boolean plusX = selectionEndX < selectionX;
        final boolean plusY = selectionEndY < selectionY;

        final int cropPointX = getOrigin(selectionX, plusX);
        final int cropPointY = getOrigin(selectionY, plusY);
        int endPointX = getOrigin(selectionEndX, !plusX);
        int endPointY = getOrigin(selectionEndY, !plusY);

        rectangle.x = Math.min(cropPointX, endPointX);
        rectangle.y = Math.min(cropPointY, endPointY);
//            rectangle.width = Math.max(1, getOrigin(Math.abs(selectionEndX - selectionX), !plusX));
//            rectangle.height = Math.max(1,getOrigin(Math.abs(selectionEndY - selectionY), !plusY));


        if (!isMoving) {
            drawRectX = getDrawRectX(Math.max(rectangle.x, 0));
            drawRectY = getDrawRectX(Math.max(rectangle.y, 0));
            outputRectangle.width = rectangle.width = Math.max(1, Math.abs(endPointX - cropPointX));
            outputRectangle.height = rectangle.height = Math.max(1, Math.abs(endPointY - cropPointY));
        } else {
            drawRectX = getDrawRectX(rectangle.x);
            drawRectY = getDrawRectX(rectangle.y);
        }
        outputRectangle.width = rectangle.width;
        outputRectangle.height = rectangle.height;
        drawRectEndX = getDrawRectX(rectangle.x + rectangle.width);
        drawRectEndY = getDrawRectX(rectangle.y + rectangle.height);
//        System.out.println("paintimagewidth" + paintImageWidth);
//        System.out.println("paintimageheight" + paintImageHeight);
        if (drawRectEndY == paintImageHeight)
            --drawRectEndY;
        if (drawRectEndX == paintImageWidth)
            --drawRectEndX;
        updateSelectionRectangle();
    }


    private void updateInfoPosition(final int x, final int y) {
        final boolean inBounds = x >= 0 && x < origImageWidth && y >= 0 && y < origImageHeight;
        if (isSelection()) {
            final boolean inBoundsWidthHeight = (outputRectangle.width > 0 && outputRectangle.height > 0);
            updateInfoPosition(Lng.formatLabel(formatString1, new Object[]{inBounds ? String.valueOf(x) : RANGE_OUT, inBounds ? String.valueOf(y) : RANGE_OUT, inBoundsWidthHeight ? String.valueOf(outputRectangle.width) : RANGE_OUT, inBoundsWidthHeight ? String.valueOf(outputRectangle.height) : RANGE_OUT}));
        } else {
            updateInfoPosition(Lng.formatLabel(formatString2, new Object[]{inBounds ? String.valueOf(x) : RANGE_OUT, inBounds ? String.valueOf(y) : RANGE_OUT}));
        }

    }

    private void updateDrawRect() {
        if (isSelection()) {
            selectionX = drawRectX = getDrawRectX(rectangle.x);
            selectionY = drawRectY = getDrawRectX(rectangle.y);
            selectionEndX = drawRectEndX = getDrawRectX(rectangle.x + rectangle.width);
            selectionEndY = drawRectEndY = getDrawRectX(rectangle.y + rectangle.height);
            if (drawRectEndY == paintImageHeight)
                --drawRectEndY;
            if (drawRectEndX == paintImageWidth)
                --drawRectEndX;
        }
    }

    private void updateInfoPosition(final String text) {
        filterDialog.updateLabelInfoPosition(text);
    }

    public final void setImg(final Image img) {
        this.img = img;
        if (img != null) {
            origImageWidth = img.getWidth(null);
            origImageHeight = img.getHeight(null);
            mouseExited(null);
            updatePaintImage();
        }
    }

    private boolean isIn(final MouseEvent e) {
        return (isIn(e.getX(), e.getY()));
    }

    private boolean isIn(final int x, final int y) {
        return (x >= 0 && x < paintImageWidth && y >= 0 && y < paintImageHeight);
    }

    public void actionPerformed(ActionEvent e) {
        if (!isSelection())
            return;

        final Point p = getViewport().getViewPosition();
        if (viewport == null)
            return;
        Point previous = (Point) p.clone();

        final int viewportWidth = viewport.getWidth();
        final int viewportHeight = viewport.getHeight();
        if (!(movePoint.x < SENSITIVE_BORDER || movePoint.x > viewportWidth - SENSITIVE_BORDER || movePoint.y < SENSITIVE_BORDER || movePoint.y > viewportHeight - SENSITIVE_BORDER))
            return;

        final int deltax = (movePoint.x - viewportWidth / 2) / 4;
        final int deltay = (movePoint.y - viewportHeight / 2) / 4;

        p.translate(deltax, deltay);

        if (p.x < 0)
            p.x = 0;
        else if (p.x >= viewport.getView().getWidth() - viewportWidth)
            p.x = viewport.getView().getWidth() - viewportWidth;

        if (p.y < 0)
            p.y = 0;
        else if (p.y >= viewport.getView().getHeight() - viewportHeight)
            p.y = viewport.getView().getHeight() - viewportHeight;

        viewport.setViewPosition(p);
        //SwingUtilities.convert
        final Point currentPosition = SwingUtilities.convertPoint(viewport, movePoint.x + (p.x - previous.x), movePoint.y + (p.y - previous.y), this);
        this.dispatchEvent(new MouseEvent(this, MouseEvent.MOUSE_DRAGGED, 0, 0, currentPosition.x, currentPosition.y, 0, false));

    }

    /**
     * Finds the JViewport enclosing this component.
     */
    private JViewport getViewport() {
        if (this.viewport != null)
            return viewport;
        final Container p = getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                final JScrollPane scroller = (JScrollPane) gp;
                viewport = scroller.getViewport();
                if (viewport == null || viewport.getView() != this) {
                    return viewport = null;
                } else {
                    return viewport;
                }
            }
        }
        return null;
    }


    private void updatePaintImage() {

        paintImageWidth = Math.round(origImageWidth * zoomFactor);
        paintImageHeight = Math.round(origImageHeight * zoomFactor);
        final Dimension dimension = new Dimension(paintImageWidth + BORDER_X * 2, paintImageHeight + 2 * BORDER_Y);

        recalculateDxy(dimension.width, dimension.height);
        setMinimumSize(dimension);
        setPreferredSize(dimension);
        getViewport().revalidate();
    }

    private void updateContent() {
        repaint();
    }

    private void updateCropLines(final Graphics2D g2) {
//        System.out.println("dx - " + drawRectX);
//        System.out.println("dy - " + drawRectY);
//        System.out.println("endx - " + drawRectEndX);
//        System.out.println("endy - " + drawRectEndY);
        g2.setStroke(strokes[activeStroke]);
        g2.setColor(Color.WHITE);
        g2.setXORMode(Color.BLACK);
        g2.drawPolygon(new int[]{drawRectX, drawRectEndX, drawRectEndX}, new int[]{drawRectY
                , drawRectY, drawRectEndY}, 3);
        g2.drawPolygon(new int[]{drawRectX, drawRectX, drawRectEndX}, new int[]{drawRectY
                , drawRectEndY, drawRectEndY}, 3);

    }

    public final void paintComponent(final Graphics g) {
        if (!isShowing())
            return;

        final Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.YELLOW);
        g.drawRect(dx - 1, dy - 1, paintImageWidth + 1, paintImageHeight + 1);
        g2.translate(dx, dy);

        g2.scale(zoomFactor, zoomFactor);

        g2.drawImage(img, 0, 0, null);
        g2.scale(1 / zoomFactor, 1 / zoomFactor);

        if (selectionX != -1) {
            Rectangle bounds = g2.getClipBounds();

            final int x, y, width, height;
            if (bounds.x + bounds.width >= paintImageWidth) {
                x = 0;
                width = paintImageWidth;
            } else {
                x = Math.max(0, bounds.x);
                width = Math.min(paintImageWidth - bounds.x - 1, bounds.width);
            }
            if (bounds.y + bounds.height >= paintImageHeight) {
                y = 0;
                height = paintImageHeight;
            } else {
                y = Math.max(0, bounds.y);
                height = Math.min(paintImageHeight - bounds.y - 1, bounds.height);
            }
            g2.setClip(x, y, width, height);
            updateCropLines(g2);
//            System.out.println("==================================================");
//            System.out.println("paintimagewidth: " + paintImageWidth);
//            System.out.println("paintimageheight: " + paintImageHeight);
//            System.out.println("bounds: " + bounds);
//            System.out.println("==================================================");
        }
    }

    public final boolean isSelection() {
        return (selectionX != -1);
    }

    public final Rectangle getSelectionRectangle() {
        return this.outputRectangle;
    }

    private void updateSelectionRectangle() {

        if (!isSelection()) {
            outputRectangle.setBounds(0, 0, origImageWidth, origImageHeight);
        } else {
            outputRectangle.x = rectangle.x;
            outputRectangle.y = rectangle.y;
            if (outputRectangle.x >= origImageWidth || outputRectangle.y >= origImageHeight) {
                outputRectangle.width = 0;
                outputRectangle.height = 0;
            } else {
                if (outputRectangle.x < 0) {
                    outputRectangle.x = 0;
                    outputRectangle.width = Math.min(Math.max(rectangle.width + rectangle.x, 0), outputRectangle.width);
                } else outputRectangle.width = Math.min(Math.abs(origImageWidth - rectangle.x), outputRectangle.width);
                if (outputRectangle.y < 0) {
                    outputRectangle.y = 0;
                    outputRectangle.height = Math.min(Math.max(rectangle.height + rectangle.y, 0), outputRectangle.height);
                } else
                    outputRectangle.height = Math.min(Math.abs(origImageHeight - rectangle.y), outputRectangle.height);
            }
        }
    }

    public final void mouseMoved(final MouseEvent e) {
        if (img == null)
            return;
        e.translatePoint(-dx, -dy);
        final boolean isIn = isIn(e);
        if (isSelection() && isIn && isInDrawSelection(e.getX(), e.getY())) {
            if (this.getCursor().getType() != Cursor.MOVE_CURSOR) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        } else if (this.getCursor().getType() != Cursor.CROSSHAIR_CURSOR) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
        updateInfoPosition(e);
    }

    public final void mouseDragged(final MouseEvent e) {
        if (img == null)
            return;
        movePoint = SwingUtilities.convertPoint(this, e.getPoint(), viewport);
        e.translatePoint(-dx, -dy);
        if (isMoving) {
            final int deltaX = e.getX() - mousePosX;
            final int deltaY = e.getY() - mousePosY;
            selectionX += deltaX;
            selectionY += deltaY;
            selectionEndX += deltaX;
            selectionEndY += deltaY;
            mousePosX = e.getX();
            mousePosY = e.getY();
        } else {
            if (isIn(e)) {
                selectionEndX = e.getX();
                selectionEndY = e.getY();
                if (selectionX == -1) {
                    selectionX = Math.min(paintImageWidth - 1, Math.max(mousePosX, 0));
                    selectionY = Math.min(paintImageHeight - 1, Math.max(mousePosY, 0));
                }
            } else {
                selectionEndX = Math.min(paintImageWidth - 1, Math.max(e.getX(), 0));
                selectionEndY = Math.min(paintImageHeight - 1, Math.max(e.getY(), 0));
            }
            mousePosX = e.getX();
            mousePosY = e.getY();
        }
        updateSelectionInfo();
        updateInfoPosition(e);
        updateContent();
    }

    private void updateInfoPosition(MouseEvent e) {
        final int endPointX = getOrigin(e.getX(), false);
        final int endPointY = getOrigin(e.getY(), false);
        updateInfoPosition(endPointX, endPointY);
    }

    private boolean isInDrawSelection(int x, int y) {
        return (x >= drawRectX && y >= drawRectY && x <= drawRectEndX && y <= drawRectEndY);
    }

    private int getDrawRectX(final int x) {
        return (int) (x * zoomFactor);
    }

    public final void mousePressed(final MouseEvent e) {
        this.requestFocus();
        if (img == null)
            return;
        e.translatePoint(-dx, -dy);

        this.isMoving = isIn(e) && isSelection() && isInDrawSelection(e.getX(), e.getY());
        this.mousePosX = Math.min(paintImageWidth, Math.max(e.getX(), 0));
        this.mousePosY = Math.min(paintImageHeight, Math.max(e.getY(), 0));

        if (!isMoving) {
            this.selectionX = selectionEndX = -1;
            this.selectionY = selectionEndY = -1;
            updateContent();
            selectionTimer.start();
        } else {
            outputRectangle.width = rectangle.width;
            outputRectangle.height = rectangle.height;
            selectionTimer.stop();
        }
        movingTimer.start();
    }

    public final void mouseReleased(final MouseEvent e) {
        if (img == null)
            return;
        movingTimer.stop();
        selectionTimer.stop();

        isMoving = false;
        updateSelectionRectangle();
        if (isSelection()) {
            if (!(outputRectangle.getWidth() > 0 && outputRectangle.getHeight() > 0)) { //cancel selection
                selectionTimer.stop();
                selectionX = selectionEndX = selectionY = selectionEndY = -1;
                updateInfoPosition(e);
                updateContent();
            } else selectionTimer.start();
        }
        if (filterDialog.isCropEnabled())
            filterDialog.updateFilter(FilterDialog.FILTER_SELECTION, FilterDialog.SRC_SELECTIONCHANGED);
    }

    public final void mouseClicked(final MouseEvent e) {
    }

    public final void mouseEntered(final MouseEvent e) {
    }

    public final void mouseExited(final MouseEvent e) {
        updateInfoPosition(-1, -1);
    }

    private int getOrigin(int x, final boolean plusX) {
        x /= zoomFactor;
        if (plusX)
            x++;
        return x;
    }

    private void updateZoom(final float newZoom) {
        final Point p = new Point();
        final JViewport viewport = getViewport();
        Point oldViewPoint = viewport.getViewPosition();
        final int viewportWidth = viewport.getWidth();
        final int viewportHeight = viewport.getHeight();

//        p.x = (int) (oldViewPoint.x + (getWidth() - (getWidth()* (newZoom / zoomFactor))) / 2);
//        p.y = (int) (oldViewPoint.y + (getHeight() - (getHeight()* (newZoom / zoomFactor))) / 2);
        p.x = (int) (oldViewPoint.x + ((newZoom / zoomFactor) * getWidth() - getWidth()) / 2);
        p.y = (int) (oldViewPoint.y + ((newZoom / zoomFactor) * getHeight() - getHeight()) / 2);
        if (p.x < 0)
            p.x = 0;
        else if (p.x >= viewport.getView().getWidth() - viewportWidth)
            p.x = viewport.getView().getWidth() - viewportWidth;

        if (p.y < 0)
            p.y = 0;
        else if (p.y >= viewport.getView().getHeight() - viewportHeight)
            p.y = viewport.getView().getHeight() - viewportHeight;

        zoomFactor = newZoom;
        updatePaintImage();
        updateDrawRect();
        filterDialog.getScrollPaneInput().validate();
        viewport.setViewPosition(p);

        updateContent();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        //System.out.println("key typed");
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '+') {
            updateZoom(getNextZoom(zoomFactor, true));
        } else if (e.getKeyChar() == '-') {
            updateZoom(getNextZoom(zoomFactor, false));
        }
    }

    public void freeResources() {
        selectionTimer.stop();
        selectionTimer.removeActionListener(this);
        movingTimer.stop();
    }

    private float getNextZoom(float currentZoom, final boolean plus) {
        currentZoom *= 100f;
        final int tableLength = ZOOM_TABLE.length;
        int i;
        if (plus) {
            for (i = 0; i < tableLength && Float.compare(currentZoom, ZOOM_TABLE[i]) >= 0; ++i) {
            }
            if (i == tableLength)
                i = tableLength - 1;
        } else {
            for (i = tableLength - 1; i >= 0 && Float.compare(currentZoom, ZOOM_TABLE[i]) <= 0; --i) {
            }
            if (i < 0)
                i = 0;
        }
        return ZOOM_TABLE[i] / 100;
    }

}
