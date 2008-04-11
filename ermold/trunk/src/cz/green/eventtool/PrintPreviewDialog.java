package cz.green.eventtool;

import cz.green.ermodeller.AppPrefs;
import cz.green.ermodeller.Consts;
import cz.green.event.interfaces.ContainerDesktop;
import cz.green.swing.*;
import cz.green.util.ActionAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Dialog, where is component PrintPreview situated, print controls and which controls printing, when is button print
 * pressed.
 */
public class PrintPreviewDialog extends JDialog implements ItemListener, KeyListener {
    /**
     * The button to run the printing
     */
    private JButton printButton = null;
    /**
     * The button to set the image resolution
     */
    private JButton resolutionButton = null;
    /**
     * Checkbox for enabling setting the custom scale
     */
    private JRadioButton customZoomButton = null;
    /**
     * Checkbox for specifying the zoom to fit the schema to one prointer page
     */
    private JRadioButton fitPageButton = null;
    /**
     * The group for all two checkboxes
     */
    private ButtonGroup zoomSelection = null;
    /**
     * Label for custom scale input box
     */
    private JLabel zoom = null;
    /**
     * The input text field for custom scale
     */
    private JTextField zoomTextField = null;
    /**
     * The component where the print preview is shown
     */
    private PrintPreview printPreview = null;
    /**
     * The PrintJob class specified by user, which selectes printer and set it up.
     */
    protected java.awt.PrintJob printJob = null;
    /**
     * The desktop of printed schema
     */
    protected ContainerDesktop desktop = null;
    /**
     * The name of the current printing job
     */
    protected String jobName = "";
    /**
     * Font for printing desktop. This font should be used for printing. The size of the font can be change according to
     * the scale of the printing
     */
    protected java.awt.Font printFont = null;
    /**
     * Determines printing (<code>true</code>) or saving as image (<code>false</code>).
     */
    protected boolean print = true;
    protected Frame parent = null;
    private JPanel ivjJDialogContentPane = null;
    //	private java.lang.String defDir;
    private java.lang.String fileName;

    /**
     * Constructor
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    public PrintPreviewDialog() {
        super();
        initialize();
    }

    /**
     * Constructor that creates the dialog. The dialog is in specified frame and is created for print job with name
     * <code>aJobName</code>.
     *
     * @param parent   The frame where dialog situated is.
     * @param aJobName The name of the current previewing print job.
     */
    public PrintPreviewDialog(java.awt.Frame parent, String aJobName) {
        super(parent, "Print preview: " + aJobName, true);
        this.parent = parent;
        jobName = aJobName;
        setName("PrintPrieviewDialog");
        getContentPane().setLayout(new CountLayout(500, 302));
        //add components
        getContentPane().add(getPrintButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-155, 1.0, 150, 0.0), new ConstantConstraint(5, 25)));
        getContentPane().add(getResolutionButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-155, 1.0, 150, 0.0), new ConstantConstraint(35, 25)));
        getContentPane().add(getFitPageButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-145, 1.0, 140, 0.0), new ConstantConstraint(70, 25)));
        getContentPane().add(getCustomButton(), new SimpleBoundsConstraint(
                new LinearConstraint(-145, 1.0, 140, 0.0), new ConstantConstraint(100, 25)));
        getContentPane().add(getZoom(), new SimpleBoundsConstraint(
                new LinearConstraint(-145, 1.0, 60, 0.0), new ConstantConstraint(130, 25)));
        getContentPane().add(getZoomTextField(), new SimpleBoundsConstraint(
                new LinearConstraint(-80, 1.0, 75, 0.0), new ConstantConstraint(130, 25)));
        getContentPane().add(getPrintPreview(), new SimpleBoundsConstraint(
                new LinearConstraint(5, 0.0, -165, 1.0), new LinearConstraint(5, 0.0, -10, 1.0)));
        zoomSelectionGroup();
        //set action listeners
        try {
            getPrintButton().addActionListener(new ActionAdapter(this, "printButtonAction"));
            getResolutionButton().addActionListener(new ActionAdapter(this, "resolutionButtonAction"));
        } catch (NoSuchMethodException x) {
        }
        getFitPageButton().addItemListener(this);
        getCustomButton().addItemListener(this);
        getZoomTextField().addKeyListener(this);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Enables (disables) the custom zoom selection. When is selected fit to one page these controls are disables and
     * when is selected custom zoom thay are enabled.
     *
     * @param b Determine whether enable or disable.
     */
    public void customZoomEnabler(boolean b) {
        getZoomTextField().setEnabled(b);
        getZoom().setEnabled(b);
    }

    /**
     * Return the checkbox for selecting customer set scale.
     */
    private JRadioButton getCustomButton() {
        if (customZoomButton == null) {
            customZoomButton = new JRadioButton("Custom zoom");
            customZoomButton.setSelected(true);
        }
        return customZoomButton;
    }


    /**
     * Insert the method's description here. Creation date: (30.5.2001 11:49:06)
     *
     * @return java.lang.String
     */
    java.lang.String getFileName() {
        return fileName;
    }

    /**
     * Return the checkbox for calculating the scale to fit to one print page.
     */
    private JRadioButton getFitPageButton() {
        if (fitPageButton == null) {
            fitPageButton = new JRadioButton("FitPage");
        }
        return fitPageButton;
    }

    /**
     * Return the JDialogContentPane property value.
     *
     * @return javax.swing.JPanel
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJDialogContentPane() {
        if (ivjJDialogContentPane == null) {
            try {
                ivjJDialogContentPane = new javax.swing.JPanel();
                ivjJDialogContentPane.setName("JDialogContentPane");
                ivjJDialogContentPane.setLayout(null);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJDialogContentPane;
    }

    /**
     * Return the button, which starts the printing with the choosen scale.
     */
    private JButton getPrintButton() {
        if (printButton == null) {
            printButton = new JButton("Close (print)");
        }
        return printButton;
    }

    /**
     * Return the component <code>PrintPreview</code>, which shows the print preview.
     */
    private PrintPreview getPrintPreview() {
        if (printPreview == null) {
            printPreview = new PrintPreview();
        }
        return printPreview;
    }

    /**
     * Return the button, which starts the setting the resolution.
     */
    private JButton getResolutionButton() {
        if (resolutionButton == null) {
            resolutionButton = new JButton("Image resolution");
        }
        return resolutionButton;
    }

    /**
     * Return the label to text field for inserting zoom from user.
     */
    private JLabel getZoom() {
        if (zoom == null) {
            zoom = new JLabel("Zoom [%]:");
        }
        return zoom;
    }

    /**
     * Checkbox group for two checkboxes: for selecting fit page scale and user defined scale.
     *
     * @see getCustomZoomCheckbox()
     * @see getFitPageCheckBox()
     */
    private ButtonGroup getZoomSelection() {
        if (zoomSelection == null) {
            zoomSelection = new ButtonGroup();
            zoomSelection.add(getFitPageButton());
            zoomSelection.add(getCustomButton());
        }
        return zoomSelection;
    }

    /**
     * Return the text field to insert scale by user.
     */
    private JTextField getZoomTextField() {
        if (zoomTextField == null) {
            zoomTextField = new JTextField("ZoomTextField");
            zoomTextField.setText("100");
        }
        return zoomTextField;
    }

    /**
     * Called whenever the part throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initialize the class.
     */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("PrintPreviewDialog");
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setSize(426, 240);
            setContentPane(getJDialogContentPane());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        // user code end
    }

    /**
     * Invoked when is selected fit page checkbox or custom scale check box. Only invokes
     * <code>zoomItemStateChanged()</code>
     *
     * @param e The item event.
     * @see java.awt.event.ItemListener
     * @see zoomItemStateChanged()
     */
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        zoomItemStateChanged();
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(java.awt.event.KeyEvent e) {
    }

    /**
     * In this methods we are notified about pressing the Enter key. We do the same action as when the repaint button is
     * pressed. This method is the reason to implement interface KeyListener.
     *
     * @see java.awt.event.KeyListener
     * @see repaintButtonAction()
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
            getPrintPreview().setPrintScale(Integer.parseInt(getZoomTextField().getText()));
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            PrintPreviewDialog aPrintPreviewDialog;
            aPrintPreviewDialog = new PrintPreviewDialog(new java.awt.Frame(), "");
            aPrintPreviewDialog.setModal(true);
            aPrintPreviewDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }

            });
            aPrintPreviewDialog.setVisible(true);
            java.awt.Insets insets = aPrintPreviewDialog.getInsets();
            aPrintPreviewDialog.setSize(aPrintPreviewDialog.getWidth() + insets.left + insets.right, aPrintPreviewDialog.getHeight() + insets.top + insets.bottom);
            aPrintPreviewDialog.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of javax.swing.JDialog");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Prints the schema in the selected printer.
     */
    protected void print() {
        if (desktop != null) {
            float scale = 1;
            try {
                scale = desktop.getScale();
                boolean[][] printPage = getPrintPreview().getPrintPages();
                java.awt.Graphics g;
                java.awt.Dimension dim = printJob.getPageDimension();
                float printScale = getPrintPreview().getPrintScale();
                desktop.setScale(printScale);
                java.awt.Font font = new java.awt.Font(printFont.getName(), printFont.getStyle(), (int) (printFont.getSize() / printScale));
                for (int y = 0; y < printPage[0].length; y++) {
                    for (int x = 0; x < printPage.length; x++) {
                        if (printPage[x][y]) {
                            g = printJob.getGraphics();
                            g.setFont(font);
                            g.translate(-x * dim.width, -y * dim.height);
                            ((Printable) desktop).print(g);
                            g.dispose();
                        }
                    }
                }
                printJob.end();
            } finally {
                desktop.setScale(scale);
            }
        }
        dispose();
    }

    /**
     * Invoked when print button is pressed. This method invokes printing or saving into image file.
     *
     * @see #print()
     * @see #saveAsImage()
     */
    public void printButtonAction() {
        if (print)
            print();
        else
            saveAsImage();
    }

    /**
     * This is the method invoked after pressing one of two buttons. Determine which button was pressed and then invokes
     * either <code>printButtonAction</code> or <code>setButtonAction</code>.
     *
     * @param e The action event.
     * @see java.awt.event.ActionListener
     * @see printButtonAction()
     * @see repaintButtonAction()
     */
    public void resolutionButtonAction() {
        ResolutionDialog rd = new ResolutionDialog(parent);
        java.awt.Dimension dim = getPrintPreview().getPageSize();
        rd.setResolution(new java.awt.Dimension(dim.width, dim.height));
        rd.setVisible(true);
        getPrintPreview().setPageSize(rd.getResolution());
    }

    /**
     * This method was created by Jiri Mares
     */
    protected void saveAsImage() {
        //com.ibm.imageconversion.BMPEncoder bmp;
        if (desktop != null) {
            float scale = 1;
            try {

                String dir, name;
                scale = desktop.getScale();
                boolean[][] printPage = getPrintPreview().getPrintPages();
                java.awt.Graphics g;
                //select the file name
                JFileChooser chooser = new JFileChooser();

                if (fileName != null) {
                    java.io.File f = new java.io.File(fileName);
                    int i = fileName.lastIndexOf(f.getName());
                    if (i > -1) {
                        dir = fileName.substring(0, i);
                        chooser.setCurrentDirectory(new java.io.File(dir));
                        chooser.setSelectedFile(f);
                    }
                } else {
                    chooser.setCurrentDirectory(new File(AppPrefs.getProperty(AppPrefs.LOAD_STORE_DIR, Consts.DEF_LOAD_STORE_DIR)));
                }
                chooser.setDialogTitle("Save schema as image");
                ExtensionFileFilter fileFilterBMP = new ExtensionFileFilter("bmp", "Windows Bitmap images (*.bmp)");
                ExtensionFileFilter fileFilterPNG = new ExtensionFileFilter("png", "PNG images (*.png)");
                chooser.addChoosableFileFilter(fileFilterBMP);
                chooser.addChoosableFileFilter(fileFilterPNG);
                chooser.setFileFilter(fileFilterPNG);
                if ((chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile() != null)) {
                    java.io.File f = chooser.getSelectedFile();
                    String filePath = f.getPath();
                    dir = filePath.substring(0, filePath.lastIndexOf(f.getName()));
                    filePath = f.getName();
                    int pos = filePath.lastIndexOf('.');
                    if (pos != -1) {
                        name = filePath.substring(0, pos);
                    } else
                        name = filePath;
                } else {
                    dispose();
                    return;
                }
                if ((name == null) || (dir == null)) {
                    dispose();
                    return;
                }
                Dimension dim = getPrintPreview().getPageSize();
                float printScale = getPrintPreview().getPrintScale();
                desktop.setScale(printScale);
                java.awt.Font font = new java.awt.Font(printFont.getName(), printFont.getStyle(), (int) (printFont.getSize() / printScale));
                java.awt.Image im;
                FileFilter selFileFilter = chooser.getFileFilter();
                String ext;
                if (selFileFilter instanceof ExtensionFileFilter) {
                    ext = ((ExtensionFileFilter) selFileFilter).getExtension();
                } else {
                    String path = chooser.getSelectedFile().getPath().toLowerCase();
                    if (path.endsWith(".bmp"))
                        ext = fileFilterBMP.getExtension();
                    else
                        ext = fileFilterPNG.getExtension();
                }

                //bmp = new com.ibm.imageconversion.BMPEncoder();
                for (int y = 0, z = 1; y < printPage[0].length; y++) {
                    for (int x = 0; x < printPage.length; x++) {
                        if (printPage[x][y]) {
                            g = (im = createImage(dim.width, dim.height)).getGraphics();
                            g.setColor(java.awt.Color.white);
                            g.fillRect(0, 0, dim.width, dim.height);
                            g.setColor(java.awt.Color.black);
                            g.setFont(font);
                            g.translate(-x * dim.width, -y * dim.height);
                            ((Printable) desktop).print(g);
                            g.dispose();
//						bmp.setInputFilename(dir + name + (z++) + ext);
//						bmp.setInputImage(im);
//						bmp.triggerAction();

                            ImageIO.write(convert(im), ext, new File(dir + name + (z++) + "." + ext));
                        }
                    }
                }
                final File createdFile = new File(dir + name + "1." + ext);
                if (createdFile.exists())
                    AppPrefs.storeProperty(AppPrefs.LOAD_STORE_DIR, createdFile.getAbsolutePath(), true);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                desktop.setScale(scale);
            }
        }
        dispose();
    }

    private BufferedImage convert(Image im) {
        BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }


    /**
     * First asks user to print or save schema as image, then if printing has been selected shows dialog for selecting
     * printer and its setting. This setting stores for following printing. It's suggested to call this method after
     * <code>setDesktop</code>.
     *
     * @return <code>True</code> (<code>false</code>) if all finished ok (bad).
     * @see #setDesktop(cz.green.event.interfaces.ContainerDesktop ,java.awt.Font)
     */
    public boolean selectPrintJob() {
        String[] options = {"Image", "Print"};
        int option;
        option = JOptionPane.showOptionDialog(parent, "Print schema or save it as image?", "How to print", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        getResolutionButton().setEnabled(!(print = (option == 1)));
        switch (option) {
            case 0:
                getPrintPreview().setPageSize(new java.awt.Dimension(640, 480));
                return true;
            case 1:
                java.awt.PrintJob job = java.awt.Toolkit.getDefaultToolkit().getPrintJob(parent, jobName, new java.util.Properties());
                if (job != null) {
                    printJob = job;
                    getPrintPreview().setPageSize(printJob.getPageDimension());
                    return true;
                }
        }
        return false;
    }

    /**
     * Sets the atribut <code>desktop</code> and set this atribut also in <code>PrintPreview</code> component, which is
     * placed into the dialog.
     *
     * @param desktop The desktop to print and view its print preview.
     */
    public void setDesktop(ContainerDesktop desktop, java.awt.Font font) {
        getPrintPreview().setDesktop(this.desktop = desktop, this.printFont = font);
    }

    public void setFileName(java.lang.String newValue) {
        int index = newValue.length();
        if (newValue != null) {
            index = newValue.lastIndexOf(".cts");
            if (index < 0)
                index = newValue.lastIndexOf(".xml");
        }
        this.fileName = newValue.substring(0, index);
    }

    /**
     * Set the correct work regime for in-placed <code>PrintPreview</code> component. User want to do fit page or select
     * its own scale.
     */
    public void zoomItemStateChanged() {
        boolean customZoom = getCustomButton().isSelected();
        customZoomEnabler(customZoom);
        if (customZoom) {
            getPrintPreview().setPrintScale(Integer.parseInt(getZoomTextField().getText()));
        } else {
            getPrintPreview().setFitPage();
        }
    }

    /**
     * Checkbox group for two checkboxes: for selecting fit page scale and user defined scale.
     *
     * @see getCustomZoomCheckbox()
     * @see getFitPageCheckBox()
     */
    private void zoomSelectionGroup() {
        if (zoomSelection == null) {
            zoomSelection = new ButtonGroup();
            zoomSelection.add(getFitPageButton());
            zoomSelection.add(getCustomButton());
        }
    }
}
