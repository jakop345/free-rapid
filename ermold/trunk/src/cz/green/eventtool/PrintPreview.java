package cz.green.eventtool;

import cz.green.event.interfaces.ContainerDesktop;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * This component shows all schema and his layout on the print pages. Also enable to determine
 * which page will be printed or not.
 */
class PrintPreview extends JComponent implements MouseListener, MouseMotionListener {
    /**
     * Desktop of the printed schema
     */
    private ContainerDesktop desktop = null;
    /**
     * Specify the size of the printer page
     */
    private java.awt.Dimension pageSize = null;
    /**
     * Helpful for internal computing. Holds the number of pages in each dimension to fit
     * the schema at the reserved zoom
     */
    private java.awt.Dimension nPages = null;
    /**
     * Specify the size of the printer size at this component representation
     */
    private java.awt.Dimension previewPageSize = null;
    /**
     * Should this class to count the scale to fit whole schema to one printer page
     */
    private boolean fitPage = false;
    /**
     * The scale in which will be the schema printed
     */
    private float printScale = 1;
    /**
     * Two dimensional array of booleans, which says whether the page should be printed
     * (<code>true</code>) or not (<code>false</code>). The size in first dimension is the same as
     * <code>nPages.width</code> and in the second as <code>nPages.height</code>.
     */
    private boolean[][] printPages = null;
    /**
     * Font for printing desktop. This font should be used for printing. The size of the font
     * can be change according to the scale of the printing
     */
    private java.awt.Font printFont = null;
    /**
     * Holds the last enabled or disabled page
     */
    private java.awt.Point lastPage = null;

    /**
     * This constructor calls the deived default constructor. Then initialize some atributes
     * and sets this class as listener the mouse events.
     *
     * @see java.awt.event.MouseListener
     */
    public PrintPreview() {
        super();
        nPages = new java.awt.Dimension();
        pageSize = new java.awt.Dimension();
        previewPageSize = new java.awt.Dimension();
        addMouseMotionListener(this);
        addMouseListener(this);
    }

    /**
     * Returns the page size;
     *
     * @return The page size.
     */
    public java.awt.Dimension getPageSize() {
        return pageSize;
    }

    /**
     * Returns the prefferd size. This preffer size is equal to the component size.
     */
    public java.awt.Dimension getPreferredSize() {
        return getSize();
    }

    /**
     * Counts the scale, previews printer page and so one. This values are counted as the preview were as
     * large as possible. The caunted atributes are: number of pages to fit schema, print scale,
     * preview page size. Then creates two dimensional array of booleans to determine which pages will be printed.
     *
     * @return The scale for preview painting.
     * @see #nPages
     * @see #printScale
     * @see #previewPageSize
     * @see #printPage
     */
    float getPreviewScale() {
        if ((pageSize == null) || (desktop == null))
            return -1;
        java.awt.Dimension dim = getSize();
        float x, y;
        if (fitPage) {
            desktop.setScale(1);
            //how many page to cover whole desktop
            nPages.width = 1;
            nPages.height = 1;
            //count preview scale for desktop drawing
            java.awt.Dimension dDim = ((Printable) desktop).getPrintBounds().getSize();
            x = ((float) dDim.width) / ((float) pageSize.width);
            y = ((float) dDim.height) / ((float) pageSize.height);
            printScale = (x > y) ? x : y;
        } else {
            desktop.setScale(printScale);
            //how many page to cover whole desktop
            java.awt.Dimension dDim = ((Printable) desktop).getPrintBounds().getSize();
            x = ((float) dDim.width) / ((float) pageSize.width);
            nPages.width = (((float) ((int) x)) == x) ? (int) x : (int) (x + 1);
            y = ((float) dDim.height) / ((float) pageSize.height);
            nPages.height = (((float) ((int) y)) == y) ? (int) y : (int) (y + 1);
            //count preview scale to x
            dim.width = dim.width / nPages.width;
            dim.height = dim.height / nPages.height;
        }
        //create printPage
        if (printPages == null) {
            printPages = new boolean[nPages.width][nPages.height];
            for (int i = nPages.width - 1; i >= 0; i--)
                for (int j = nPages.height - 1; j >= 0; j--)
                    printPages[i][j] = true;
        }
        x = ((float) pageSize.width) / ((float) dim.width);
        y = ((float) pageSize.height) / ((float) dim.height);
        x = (x > y) ? x : y;
        //count preview page size
        previewPageSize.width = (int) (pageSize.width / x);
        previewPageSize.height = (int) (pageSize.height / x);
        return printScale * x;
    }

    /**
     * Returns the value of the atribute <code>printPage</code>.
     *
     * @return The atribute <code>printPage</code>.
     * @see #printPage
     */
    public boolean[][] getPrintPages() {
        return printPages;
    }

    /**
     * Returns the print scale. The scale in which have to be schema printed.
     *
     * @param Value of the attribute <code>printScale</code>
     * @see #printScale
     */
    public float getPrintScale() {
        return printScale;
    }

    /**
     * In this method is code to set the page either printable or not.
     * This method is the reason to implement the interface MouseListener.
     *
     * @see java.awt.event.MouseListener
     */
    public void mouseClicked(MouseEvent evt) {
    }

    /**
     * In this method is code to set the page either printable or not.
     * This method is the reason to implement the interface MouseMotionListener.
     *
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseDragged(MouseEvent evt) {
        if ((previewPageSize != null) && (printPages != null)) {
            int x = evt.getX();
            int y = evt.getY();
            x = x / previewPageSize.width;
            y = y / previewPageSize.height;
            if (x >= printPages.length || y >= printPages[0].length)
                return;
            if (lastPage == null)
                lastPage = new java.awt.Point(-1, -1);
            if (lastPage.x != x || lastPage.y != y) {
                lastPage.x = x;
                lastPage.y = y;
                printPages[x][y] = (!printPages[x][y]);
                repaint(x * previewPageSize.width, y * previewPageSize.height, previewPageSize.width - 1, previewPageSize.height - 1);
            }
        }
    }

    /**
     * Exists for implementing the interface MouseListener
     *
     * @see java.awt.event.MouseListener
     */
    public void mouseEntered(MouseEvent evt) {
    }

    /**
     * Exists for implementing the interface MouseListener
     *
     * @see java.awt.event.MouseListener
     */
    public void mouseExited(MouseEvent evt) {
    }

    /**
     * Exists for implementing the interface MouseMotionListener
     *
     * @see java.awt.event.MouseMotionListener
     */
    public void mouseMoved(MouseEvent evt) {
    }

    /**
     * In this method is code to set the page either printable or not.
     * This method is the reason to implement the interface MouseListener.
     *
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent evt) {
        mouseDragged(evt);
    }

    /**
     * Exists for implementing the interface MouseListener
     *
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent evt) {
        lastPage.x = -1;
        lastPage.y = -1;
    }

    /**
     * Paint whole print preview.
     *
     * @param g Where to paint.
     */
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        if (desktop != null) {
            float scale = desktop.getScale();
            try {
                float previewScale = getPreviewScale();
                g.setFont(new java.awt.Font(printFont.getName(), printFont.getStyle(), (int) (printFont.getSize() / previewScale)));
                g.setColor(java.awt.Color.white);
                g.fillRect(0, 0, previewPageSize.width * nPages.width, previewPageSize.height * nPages.height);
                g.setColor(java.awt.Color.black);
                java.awt.Dimension d = getSize();
                desktop.setScale(previewScale);
                ((Printable) desktop).print(g);
                g.setColor(java.awt.Color.blue);
                java.awt.Rectangle r = new java.awt.Rectangle();
                for (int x = 1; x <= nPages.width; x++) {
                    for (int y = 1; y <= nPages.height; y++) {
                        r.x = (x - 1) * previewPageSize.width + 2;
                        r.y = (y - 1) * previewPageSize.height + 2;
                        r.width = previewPageSize.width - 5;
                        r.height = previewPageSize.height - 5;
                        g.drawRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4);

                        if (printPages[x - 1][y - 1]) {
                            int dt = (r.width < r.height) ? (r.width / 6) : (r.height / 6);
                            g.drawLine(r.x + dt, r.y, r.x + r.width, r.y);
                            g.drawLine(r.x + r.width, r.y, r.x + r.width, r.y + r.height);
                            g.drawLine(r.x + r.width, r.y + r.height, r.x, r.y + r.height);
                            g.drawLine(r.x, r.y + r.height, r.x, r.y + dt);
                            g.drawLine(r.x, r.y + dt, r.x + dt, r.y);
                            g.drawLine(r.x + dt, r.y, r.x + dt, r.y + dt);
                            g.drawLine(r.x + dt, r.y + dt, r.x, r.y + dt);
                        }
                    }
                }
            } finally {
                desktop.setScale(scale);
            }
        }
    }

    /**
     * Set the desktop of the printed schema.
     *
     * @param desktop The desktop.
     */
    public void setDesktop(ContainerDesktop desktop, java.awt.Font font) {
        this.desktop = desktop;
        this.printFont = font;
    }

    /**
     * User want to count scale to fit whole schema to one printer page.
     * This methods caused repainting.
     */
    public void setFitPage() {
        this.printScale = 1;
        this.fitPage = true;
        this.printPages = null;
        repaint();
    }

    /**
     * The size of the printer page. Is set during seting printer properties.
     *
     * @param newValue The size of the printer page.
     */
    public void setPageSize(java.awt.Dimension newValue) {
        pageSize.width = newValue.width;
        pageSize.height = newValue.height;
        this.printPages = null;
        repaint();
    }

    /**
     * User want to print the schema in the specified scale. Caused the repainting.
     *
     * @param newValue The user selected scale.
     */
    public void setPrintScale(int newValue) {
        this.printScale = ((float) 100.0) / ((float) newValue);
        this.fitPage = false;
        this.printPages = null;
        repaint();
    }
}
