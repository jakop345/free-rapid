package cz.green.ermodeller;

import cz.green.ermodeller.interfaces.FontManager;
import cz.green.event.MoveEvent;
import cz.green.event.ResizeEvent;
import cz.green.event.ResizePoint;
import cz.green.event.ResizeRectangle;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.eventtool.Connection;
import cz.green.eventtool.PrintPreviewDialog;
import cz.green.eventtool.PropertyListDialog;
import cz.green.swing.ExtensionFileFilter;
import cz.green.swing.SelectUI;
import cz.green.swing.ShowException;
import cz.green.util.ActionAdapter;
import cz.green.util.ParamActionAdapter;
import cz.omnicom.ermodeller.conc2obj.ObjDialog;
import cz.omnicom.ermodeller.datatype.*;
import cz.omnicom.ermodeller.errorlog.*;
import cz.omnicom.ermodeller.sql.SQLDialog;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorage;
import cz.omnicom.ermodeller.typeseditor.UserTypeStorageVector;
import cz.omnicom.ermodeller.typeseditor.UserTypesEditor;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * The ER modeller application.
 */
public class ERModeller extends JFrame implements
        java.beans.PropertyChangeListener, WindowListener {

    /**
     * The schema file name
     */
    protected String fileName = null;

    /**
     * The container where the desktop paints
     *
     * @see Container
     * @see Desktop
     */
    private cz.green.ermodeller.Container place = null;

    /**
     * Input field for the user scale
     */
    private JComboBox scale = null;

    /**
     * Input field with the work mode - read only
     */
    private JLabel mode = null;

    /**
     * Scroll pane for the schema
     */
    private JScrollPane scrollPane = null;

    /**
     * The URL that represents the help file - will be shown in internat browser
     */
    private JToolBar toolBar = null;

    /**
     * Menu and neu Items
     */
    private JMenuBar menuBar;

    /**
     * Status panel
     */
    private Panel statusPanel;

    private JLabel notationStatusLabel;

    private JLabel lodStatusLabel;

    protected transient ErrorLogDialog errDialog = null;

    protected transient ConflictsDialog conflictsDialog = null;

    protected transient OptionsDialog optDialog = null;

    protected transient AboutDialog aboutDialog = null;

    protected transient UserTypesEditor typeEditor = null;

    protected transient SQLDialog sqlDialog = null;

    protected transient ObjDialog objDialog = null;

    /**
     * Flag if schema is changed
     */
    private boolean changed = false;

    /**
     * Realtions with some atribute
     */
    protected java.util.Vector RelsWithAtribs = null;

    /**
     * Vector with ternary Relations
     */
    protected java.util.Vector RelsTernary = null;

    /**
     * Adding file as a new cts.
     */
    public final static int NEW_CTS = 0;

    /**
     * Adding file as a new xml..
     */
    public final static int NEW_XML = 1;

    /**
     * Adding xml file into actual desktop.
     */
    public final static int WITH_XML = 2;

    /**
     * Title of the main window
     */
    private static final String TITLE = Consts.APPVERSION_FULL;


    /**
     * This method was created by Jiri Mares
     */
    public ERModeller() {
        super(TITLE + " - (new schema)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        getContentPane().setLayout(new BorderLayout(5, 5));
        setSize(950, 700);
        getContentPane().add(getScrollPane(), "Center");
        getContentPane().add(getToolBar(), "North");

        getContentPane().add(getStatusPanel(), "South");
        PropertyListDialog PLD = PropertyListDialog.createListDialog(this, false, ((Desktop) getPlace().getDesktop()).getModel(), "Properties");
        PLD.setPreferredSize(new Dimension(20, 20));
        PLD.setMaximumSize(new Dimension(20, 20));
        setChanged(false);
        getContentPane().add(PLD.getContentPane(), "East");
        //PropertyListDialog.createListDialog()
        final ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource("img/icon.gif"));
        if (icon != null)
            this.setIconImage(icon.getImage());
        createMenuBar();
        this.setJMenuBar(menuBar);

        try {
            getScale().addActionListener(
                    new ActionAdapter(this, "setScaleAction"));
        } catch (NoSuchMethodException e) {
        }
        getPlace().addPropertyChangeListener(this);
        getPlace().deleting();
        getPlace().working();
        errDialog = new ErrorLogDialog(this, new ErrorLogList());
        errDialog.setLocationRelativeTo(this);
        conflictsDialog = new ConflictsDialog(this, new ErrorLogList());
        conflictsDialog.setLocationRelativeTo(this);
        sqlDialog = new SQLDialog(this, null);
        sqlDialog.setLocationRelativeTo(this);
        objDialog = new ObjDialog(this, null);
        objDialog.setLocationRelativeTo(this);
        optDialog = new OptionsDialog(this, this);
        optDialog.setLocationRelativeTo(this);
        optDialog.pack();
        aboutDialog = new AboutDialog(this);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.pack();
        typeEditor = new UserTypesEditor(this);
        typeEditor.setLocationRelativeTo(this);

        ((Desktop) getPlace().getDesktop()).ERMFrame = this;

        loadDefaultConfiguration();
        switch (AppPrefs.getProperty(AppPrefs.GENERAL_DEFNOTATION, Consts.DEF_GENERAL_DEFNOTATION)) {
            case (ConceptualConstruct.CHEN):
                setChen();
                break;
            case (ConceptualConstruct.BINARY):
                setBinary();
                break;
            case (ConceptualConstruct.UML):
                setUML();
                break;
        }

    }

    /**
     * loads configuration
     */
    protected void loadDefaultConfiguration() {
        final String driver = AppPrefs.getProperty(AppPrefs.DBCONNECT_DRIVER, Consts.DEF_DBCONNECT_DRIVER);
        final String url = AppPrefs.getProperty(AppPrefs.DBCONNECT_URL, Consts.DEF_DBCONNECT_URL);
        final String user = AppPrefs.getProperty(AppPrefs.DBCONNECT_USER, Consts.DEF_DBCONNECT_USER);
        //optDialog.loadCfg(driver, url, user);

        ConceptualConstruct.SHOW_PK_IN_UML = AppPrefs.getProperty(AppPrefs.GENERAL_PKSHOWUML, Consts.DEF_GENERAL_PKSHOWUML);
        ConceptualConstruct.SHOW_SHORTEN_CARD_IN_UML = AppPrefs.getProperty(AppPrefs.GENERAL_SHORTEN_CARDS_UML, Consts.DEF_GENERAL_SHORTEN_CARDS_UML);

        cz.green.event.Window.OBJECT_FOREGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_OBJECT_FG, Consts.DEF_COLORS_OBJECT_FG);
        cz.green.event.Window.OBJECT_BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_OBJECT_BG, Consts.DEF_COLORS_OBJECT_BG);
        cz.green.event.Window.SELECTED_OBJECT_BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_SELOBJECT_BG, Consts.DEF_COLORS_SELOBJECT_BG);
        cz.green.event.Window.BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_BG, Consts.DEF_COLORS_BG);
    }

    /**
     * Saves configuration to file
     *
     * @param driver JDBC SQL driver
     * @param url    connection DB url
     * @param user   user name
     */
    protected void writeUserConfigFile(String driver, String url, String user) {
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_DRIVER, driver);
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_URL, url);
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_USER, user);

        AppPrefs.storeProperty(AppPrefs.GENERAL_PKSHOWUML, ConceptualConstruct.SHOW_PK_IN_UML);
        AppPrefs.storeProperty(AppPrefs.GENERAL_SHORTEN_CARDS_UML, ConceptualConstruct.SHOW_SHORTEN_CARD_IN_UML);
        AppPrefs.storeProperty(AppPrefs.COLORS_OBJECT_FG, cz.green.event.Window.OBJECT_FOREGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_OBJECT_BG, cz.green.event.Window.OBJECT_BACKGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_SELOBJECT_BG, cz.green.event.Window.SELECTED_OBJECT_BACKGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_BG, cz.green.event.Window.BACKGROUND_COLOR);
        AppPrefs.store();
    }

    /**
     * Opens the About dialog
     */
    public boolean about() {
        aboutDialog.setVisible(true);
        return true;
    }

    /**
     * Compose schema with file
     */
    public void compose() {
        String fileName;
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open schema");
            ExtensionFileFilter ff = new ExtensionFileFilter("xml",
                    "Conceptual schemas (*.xml)");
            chooser.addChoosableFileFilter(ff);
            chooser.setFileFilter(ff);
            chooser.setCurrentDirectory(new File(AppPrefs.getProperty(AppPrefs.LOAD_STORE_DIR, Consts.DEF_LOAD_STORE_DIR)));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                if (f != null) {
                    AppPrefs.storeProperty(AppPrefs.LOAD_STORE_DIR, f.getAbsolutePath(), true);
                    try {
                        fileName = ((ExtensionFileFilter) chooser
                                .getFileFilter()).getPath(f);
                    } catch (ClassCastException x) {
                        fileName = f.getPath();
                    }
                    String ext = ((ExtensionFileFilter) chooser.getFileFilter())
                            .getExtension();
                    loadFromFile(fileName, WITH_XML);
                }
            }
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Shows all conflicts in the schema
     */
    public void conflicts() {
        cz.omnicom.ermodeller.conceptual.Schema schema;
        Desktop d = (Desktop) getPlace().getDesktop();
        schema = (cz.omnicom.ermodeller.conceptual.Schema) d.getModel();
        if (schema.getComposeID() > schema.getID()) {//?????????
            conflictsDialog.setID(0);
            conflictsDialog.setDesktop(d);
            conflictsDialog.setPrefix("");
        }
        conflictsDialog.refreshButton_ActionEvents();
        conflictsDialog.setVisible(true);
    }

    /**
     * Creates new schema
     */
    public boolean create() {
        if (isChanged()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save the old schema?", "Save question",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (option) {
                case 0:
                    if (!save())
                        return false;
                    break;
                case 2:
                    return false;
            }
        }
        place.clearDesktop();
        place.getDesktop();
        place.repaint();
        setTitle(TITLE + " - (new schema)");
        fileName = null;
        typeEditor = new UserTypesEditor(this);
        typeEditor.setLocationRelativeTo(this);
        typeEditor.reset();
        switch (AppPrefs.getProperty(AppPrefs.GENERAL_DEFNOTATION, Consts.DEF_GENERAL_DEFNOTATION)) {
            case (ConceptualConstruct.CHEN):
                setChen();
                break;
            case (ConceptualConstruct.BINARY):
                setBinary();
                break;
            case (ConceptualConstruct.UML):
                setUML();
                break;
        }
        setChanged(false);
        System.gc();
        return true;
    }

    /**
     * This method was created by Jiri Mares
     */
    public void errors() {
        errDialog.setVisible(true);
    }

    /**
     * This method was created by Jiri Mares
     */
    public void generate() {
        try {
            if (getPlace().getDesktop() instanceof Desktop) {
                cz.omnicom.ermodeller.conc2rela.SchemaC2R schemaC2R;
                cz.omnicom.ermodeller.conceptual.Schema model = (cz.omnicom.ermodeller.conceptual.Schema) ((Desktop) getPlace()
                        .getDesktop()).getModel();
                ErrorLogList list = model.checkConsistency();
                conflictsDialog.setErrorLogList(list);
                conflictsDialog.setDesktop((Desktop) getPlace().getDesktop());
                if (conflictsDialog.isVisible() || !list.isEmpty()) {
                    conflictsDialog.setVisible(true);
                }
                if (list.isEmpty()) {
                    cz.omnicom.ermodeller.conc2rela.GenerateDialog genDialog = new cz.omnicom.ermodeller.conc2rela.GenerateDialog(
                            this);
                    genDialog.setLocationRelativeTo(null);
                    genDialog.setVisible(true);
                    if (genDialog.getResult()) {
//						osetreni kardinalit
                        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
                            if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY)
                                ((Desktop) getPlace().getDesktop()).switchAllRConnectionsCard(place);
                            else ((Desktop) getPlace().getDesktop()).switchAllRConnectionsBoth(place);
                        }
                        //konec osetreni kardinalit
                        schemaC2R = new cz.omnicom.ermodeller.conc2rela.SchemaC2R(
                                model, typeEditor.getTypesVector(), genDialog
                                .getGenDrop(), genDialog
                                .getShortenPrefixes(), !genDialog
                                .getDefaultGlue());
                        sqlDialog.setSchemaSQL(schemaC2R.createSchemaSQL());
//						osetreni kardinalit
                        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
                            if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY)
                                ((Desktop) getPlace().getDesktop()).switchAllRConnectionsCard(place);
                            else ((Desktop) getPlace().getDesktop()).switchAllRConnectionsBoth(place);
                        }
                        //konec osetreni kardinalit
                        sqlDialog.setSQLConnection(optDialog
                                .getSQLConnection());
                        sqlDialog.setVisible(true);
                    }
                } else {
                    sqlDialog.setSchemaSQL(null);
                    if (sqlDialog.isVisible())
                        sqlDialog.setVisible(true);
                }
            }
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Generation object-relation script
     */
    public void generateObj() {
        try {

            if (getPlace().getDesktop() instanceof Desktop) {
                cz.omnicom.ermodeller.conc2rela.SchemaC2R schemaC2R;
                cz.omnicom.ermodeller.conceptual.Schema model = (cz.omnicom.ermodeller.conceptual.Schema) ((Desktop) getPlace()
                        .getDesktop()).getModel();
                ErrorLogList list = model.checkConsistency();
                conflictsDialog.setErrorLogList(list);
                conflictsDialog.setDesktop((Desktop) getPlace().getDesktop());
                if (conflictsDialog.isVisible() || !list.isEmpty()) {
                    conflictsDialog.setVisible(true);
                }
                if (list.isEmpty()) {
                    cz.omnicom.ermodeller.conc2rela.GenerateDialog genDialog = new cz.omnicom.ermodeller.conc2rela.GenerateDialog(
                            this);
                    genDialog.setLocationRelativeTo(null);
                    genDialog.setVisible(true);
                    if (genDialog.getResult()) {
                        //osetreni kardinalit
                        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
                            if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY)
                                ((Desktop) getPlace().getDesktop()).switchAllRConnectionsCard(place);
                            else ((Desktop) getPlace().getDesktop()).switchAllRConnectionsBoth(place);
                        }
                        //konec osetreni kardinalit
                        schemaC2R = new cz.omnicom.ermodeller.conc2rela.SchemaC2R(
                                model, typeEditor.getTypesVector(), genDialog
                                .getGenDrop(), genDialog
                                .getShortenPrefixes(), !genDialog
                                .getDefaultGlue());
                        //osetreni kardinalit
                        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
                            if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY)
                                ((Desktop) getPlace().getDesktop()).switchAllRConnectionsCard(place);
                            else ((Desktop) getPlace().getDesktop()).switchAllRConnectionsBoth(place);
                        }
                        //konec osetreni kardinalit
                        objDialog.setSchemaObj(schemaC2R.createSchemaObj());
                        objDialog.setObjConnection(optDialog
                                .getSQLConnection());
                        objDialog.setVisible(true);
                    }
                } else {
                    objDialog.setSchemaObj(null);
                    if (objDialog.isVisible())
                        objDialog.setVisible(true);
                }
            }
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Return the TextField where the mode is shown.
     */
    private JLabel getMode() {
        if (mode == null) {
            mode = new JLabel();
            mode.setIcon(new ImageIcon(ClassLoader
                    .getSystemResource("img/working.gif")));
            mode.setText("");
            mode.setAlignmentY((float) 0.5);
            mode.setToolTipText("Display current working mode");
            mode.setPreferredSize(new Dimension(28, 28));
        }
        return mode;
    }

    /**
     * Returns the container where all the schema is placed.
     *
     * @see Container
     */
    cz.green.ermodeller.Container getPlace() {
        if (place == null) {
            place = new cz.green.ermodeller.Container(2500, 2500);
            place.setName("Place");
            place.setBackground(java.awt.Color.black);
            try {
                ((Desktop) place.getDesktop()).addPropertyChangeListener(this);
            } catch (ClassCastException x) {
            }
        }
        return place;
    }

    /**
     * Return the JComboBox where the scale is printed.
     */
    private JComboBox getScale() {
        if (scale == null) {
            String[] scales = {"500", "300", "150", "100", "85", "70", "50",
                    "25", "10"};
            scale = new JComboBox(scales);
            scale.setSelectedItem("100");
            scale.setEditable(true);
            scale.setToolTipText("Set the scale");
            scale.setMaximumSize(new Dimension(70, 25));
            scale.setPreferredSize(new Dimension(70, 25));
        }
        return scale;
    }

    /**
     * Return the ScrollPane for the Container.
     *
     * @see Conatiner
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getPlace());
            scrollPane.setPreferredSize(new Dimension(150, 200));
        }
        return scrollPane;
    }

    /**
     * Returns the MenuBar.
     */
    private JMenuBar createMenuBar() {
        JMenu menu, LAFmenu, LODmenu, NotationMenu;
        JMenuItem menuItem;

        // Create the menu bar.
        menuBar = new JMenuBar();

        // Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        // menu.getAccessibleContext().setAccessibleDescription(
        // "The only menu in this program that has menu items");
        menuBar.add(menu);
        menu
                .add(getMenuItem("New..", "img/new.gif", KeyEvent.VK_N, this,
                        "create"));
        menu
                .add(getMenuItem("Open..", "img/load.gif", KeyEvent.VK_O, this,
                        "load"));
        menu
                .add(getMenuItem("Save..", "img/save.gif", KeyEvent.VK_S, this,
                        "save"));
        menu.addSeparator();
        menu.add(getMenuItem("Print..", "img/print.gif", KeyEvent.VK_P, this,
                "print"));
        menu.addSeparator();
        menu.add(getMenuItem("Exit", KeyEvent.VK_X, this, "exit"));

        // Build second menu in the menu bar.
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(menu);

        NotationMenu = new JMenu("Graphic Notation");
        NotationMenu.setMnemonic(KeyEvent.VK_G);
        menu.add(NotationMenu);

        NotationMenu.add(getMenuItem("Chen Notation", KeyEvent.VK_C,
                this, "checkChen"));
        NotationMenu.add(getMenuItem("Binary Model", KeyEvent.VK_B,
                this, "checkBinary"));
        NotationMenu.add(getMenuItem("UML Notation", KeyEvent.VK_U,
                this, "checkUML"));
        // NotationMenu.add(getMenuItem("Low details", "new.gif", KeyEvent.VK_L,
        // this, "setLODlow"));

        LODmenu = new JMenu("Level of Details");
        LODmenu.setMnemonic(KeyEvent.VK_L);
        menu.add(LODmenu);

        LODmenu.add(getMenuItem("Full details", KeyEvent.VK_F, this,
                "setLODfull"));
        LODmenu.add(getMenuItem("Medium details", KeyEvent.VK_M,
                this, "setLODmedium"));
        LODmenu.add(getMenuItem("Low details", KeyEvent.VK_L, this,
                "setLODlow"));

        menu.add(getMenuItem("Refresh schema", "img/refresh.gif", KeyEvent.VK_R, getPlace(),
                "repaint"));

        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menuBar.add(menu);

        menu.add(getMenuItem("Merge schemas", "img/compose.gif", KeyEvent.VK_M, this, "compose"));
        menu.add(getMenuItem("Check consistency", "img/check.gif", KeyEvent.VK_C, this, "conflicts"));
        menu.add(getMenuItem("Generate SQL", "img/sql.gif", KeyEvent.VK_G, this, "generate"));
        menu.add(getMenuItem("Generate Object-relation script", "img/obj.gif", KeyEvent.VK_O, this, "generateObj"));
        menu.add(getMenuItem("Edit user defined data types", "img/editDataTypes.gif", KeyEvent.VK_E, this, "editTypes"));

        menu = new JMenu("Settings");
        menu.setMnemonic(KeyEvent.VK_S);
        menuBar.add(menu);

        LAFmenu = new JMenu("Look & Feel");
        LAFmenu.setMnemonic(KeyEvent.VK_L);
        menu.add(LAFmenu);

        LAFmenu.add(getMenuItem(" Metal", KeyEvent.VK_E, this, "setMetalLAF"));
        LAFmenu.add(getMenuItem(" Windows", KeyEvent.VK_W, this, "setWindowsLAF"));
        LAFmenu.add(getMenuItem(" Motif ", KeyEvent.VK_O, this, "setMotifLAF"));

//		ImageIcon PrefIcon = new ImageIcon(ClassLoader
//				.getSystemResource("img/preferences.gif"));
        menu.add(getMenuItem("Options", KeyEvent.VK_H,
                this, "options"));
//		menuItem = new JMenuItem("Options", PrefIcon);
//		menuItem.setMnemonic(KeyEvent.VK_P);
//		menu.add(menuItem);
//		properties
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        // menu.getAccessibleContext().setAccessibleDescription(
        // "This menu does nothing");
        menuBar.add(menu);
        menu.add(getMenuItem("Help Contents", "img/help.gif", KeyEvent.VK_H,
                getPlace(), "help"));
        menu.add(getMenuItem("About", KeyEvent.VK_A, this, "about"));

        return menuBar;
    }

    /**
     * Return the menu item.
     */
    private JMenuItem getMenuItem(String name, String image, int mnemonic,
                                  Object object, String method) {
        JMenuItem mItem = null;
        try {
            ImageIcon imageIcon = new ImageIcon(ClassLoader
                    .getSystemResource(image));
            mItem = new JMenuItem(name, imageIcon);
            mItem.addActionListener(new ActionAdapter(object, method));
            mItem.setMnemonic(mnemonic);
        } catch (NoSuchMethodException e) {
        }
        return mItem;
    }

    /**
     * Return the menu item.
     */
    private JMenuItem getMenuItem(String name, int mnemonic, Object object,
                                  String method) {
        JMenuItem mItem = null;
        try {
            mItem = new JMenuItem(name);
            mItem.addActionListener(new ActionAdapter(object, method));
            mItem.setMnemonic(mnemonic);
        } catch (NoSuchMethodException e) {
        }
        return mItem;
    }

    private Panel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new Panel(new GridLayout(1, 10));
            notationStatusLabel = new JLabel("Chan Notation");
            lodStatusLabel = new JLabel("Full details");
            statusPanel.add(new JLabel("Status panel:"));
            statusPanel.add(new JLabel(" "));
            statusPanel.add(notationStatusLabel);
            statusPanel.add(new JLabel("|"));
            statusPanel.add(lodStatusLabel);
        }
        return statusPanel;
    }


    /**
     * Returns the JToolBar.
     */
    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setMargin(new Insets(0, 0, 0, 0));
            getToolBar().add(getMode());
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/new.gif", "Create new conceptual schema",
                            this, "create"));
            getToolBar().add(
                    getToolBarButton("img/load.gif",
                            "Open existing conceptual schema from the file",
                            this, "load"));
            getToolBar().add(
                    getToolBarButton("img/save.gif",
                            "Save current schema to the file", this, "save"));
            getToolBar()
                    .add(
                            getToolBarButton(
                                    "img/print.gif",
                                    "Print the current schema to the printer or save as an image file",
                                    this, "print"));
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/refresh.gif",
                            "Refresh layout of the current schema", getPlace(),
                            "repaint"));
/*			getToolBar().add(
					getToolBarButton("property.gif",
							"Edit properties of the selected element",
							getPlace(), "propEditing", new Boolean(true),
							boolean.class));
			// getToolBar().add(getToolBarButton("errors.gif", "Show all
			// conceptual errors", this, "errors"));
*/
            getToolBar()
                    .add(
                            getToolBarButton(
                                    "img/compose.gif",
                                    "Merge current schema with the schema from the file",
                                    this, "compose"));
            getToolBar().add(
                    getToolBarButton("img/check.gif",
                            "Check consistency of the current schema", this,
                            "conflicts"));
            // getToolBar().add(getToolBarButton("check.gif", "Check consistency
            // of the current schema.", this, "check"));
            getToolBar().add(
                    getToolBarButton("img/sql.gif",
                            "Generate SQL script from the current schema",
                            this, "generate"));
            getToolBar().add(
                    getToolBarButton("img/obj.gif",
                            "Generate Object-relation script from the current schema",
                            this, "generateObj"));
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/work.gif", "Set 'pointing' work mode",
                            getPlace(), "working"));
            getToolBar().add(
                    getToolBarButton("img/delete.gif",
                            "Set the 'deleting' work mode", getPlace(),
                            "deleting"));
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/addEntity.gif",
                            "Add the new Entity", getPlace(),
                            "addingEntity", null, EntityConstruct.class));
/*			getToolBar().add(
					getToolBarButton("img/addRelation.gif",
							"Add the new Relation", getPlace(),
							"addingRelationToolbar"));
*/
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/readjustAll.gif", "Readjust All",
                            this, "minimizeAll"));
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/editDataTypes.gif",
                            "Edit user defined data types", this, "editTypes"));
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(getScale());
            getToolBar().addSeparator();
            getToolBar().addSeparator();
            getToolBar().add(
                    getToolBarButton("img/help.gif", "Open the help", getPlace(),
                            "help"));
        }
        return toolBar;
    }

    /**
     * Return the TextField where the mode is shown.
     */
    private JButton getToolBarButton(String image, String toolTip,
                                     Object object, String method) {
        JButton but = null;
        try {
            but = new JButton();
            // java.net.URL iconURL = ClassLoader.getSystemResource(image);
            // but.setIcon(new ImageIcon(iconURL));
            // but.setIcon(new ImageIcon(getClass().getResource("/"+image)));
            but.setIcon(new ImageIcon(ClassLoader.getSystemResource(image)));
            but.setText("");
            but.setToolTipText(toolTip);
            but.setMargin(new Insets(0, 0, 0, 0));
            but.setBorder(null);
            but.setAlignmentY((float) 0.5);
            but.addActionListener(new ActionAdapter(object, method));
            but.setFocusPainted(false);
        } catch (NoSuchMethodException e) {
        }
        return but;
    }

    /**
     * Return the TextField where the mode is shown.
     */
    private JButton getToolBarButton(String image, String toolTip,
                                     Object object, String method, Object param, Class<EntityConstruct> paramClass) {
        JButton but = null;
        try {
            but = new JButton();
            // java.net.URL iconURL = ClassLoader.getSystemResource(image);
            // but.setIcon(new ImageIcon(iconURL));
            // but.setIcon(new ImageIcon(getClass().getResource("/"+image)));
            but.setIcon(new ImageIcon(ClassLoader.getSystemResource(image)));
            but.setText("");
            but.setToolTipText(toolTip);
            but.setMargin(new Insets(0, 0, 0, 0));
            but.setBorder(null);
            but.setAlignmentY((float) 0.5);
            but.addActionListener(new ParamActionAdapter(object, method, param,
                    paramClass));
            but.setFocusPainted(false);
        } catch (NoSuchMethodException e) {
        }
        return but;
    }

    /**
     * This method was created by Jiri Mares
     */
    public void check() {
        try {
            if (getPlace().getDesktop() instanceof Desktop) {
                cz.omnicom.ermodeller.conceptual.Schema model = (cz.omnicom.ermodeller.conceptual.Schema) ((Desktop) getPlace()
                        .getDesktop()).getModel();
                errDialog.setErrorLogList(model.checkConsistency());
            }
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
        errDialog.setDesktop((Desktop) getPlace().getDesktop());
        errDialog.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to CHEN
     */
    public void checkChen() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstruct.CHEN);
        ccnDial.setLocationRelativeTo(this);
        ccnDial.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to UML
     */
    public void checkUML() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstruct.UML);
        ccnDial.setLocationRelativeTo(this);
        ccnDial.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to BINARY
     */
    public void checkBinary() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstruct.BINARY);
        ccnDial.setLocationRelativeTo(this);
        ccnDial.setVisible(true);
    }

    /**
     * Opens data type editor
     */
    public void editTypes() {
        // System.out.println("editujeme typy...");
        typeEditor.setVisible(true);
    }

    public void exit() {
        int option;
        if (isChanged()) {
            option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save the schema?", "Save question",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (option) {
                case 0:
                    if (!save())
                        return;
                    break;
                case 2:
                    return;
            }
        }
        dispose();
        System.exit(0);
    }

    /**
     * Returns <code>true</code> if schema is changed
     */
    public boolean isChanged() {
        Desktop d = (Desktop) (getPlace().getDesktop());
        return ((cz.omnicom.ermodeller.conceptual.Schema) d.getModel())
                .isChanged() || changed || typeEditor.isChanged();
    }

    /**
     * Loads the schema from a file.
     */
    public boolean load() {
        try {
            if (isChanged()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Do you want to save the old schema?", "Save question",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (option) {
                    case 0:
                        if (!save())
                            return false;
                        break;
                    case 2:
                        return false;
                }
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Open schema");
            ExtensionFileFilter ff;
            /*
                * ff = new ExtensionFileFilter("cts", "Conceptual schemas
                * (*.cts)"); chooser.addChoosableFileFilter(ff);
                * chooser.setFileFilter(ff);
                */
            ff = new ExtensionFileFilter("xml", "Conceptual schemas (*.xml)");
            chooser.addChoosableFileFilter(ff);
            chooser.setFileFilter(ff);
            if (fileName != null) {
                java.io.File f = new java.io.File(fileName);
                int i = fileName.lastIndexOf(f.getName());
                if (i > -1) {
                    String dir = fileName.substring(0, i);
                    chooser.setCurrentDirectory(new java.io.File(dir));
                }
            } else {
                chooser.setCurrentDirectory(new File(AppPrefs.getProperty(AppPrefs.LOAD_STORE_DIR, Consts.DEF_LOAD_STORE_DIR)));
            }
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                if (f != null) {
                    AppPrefs.storeProperty(AppPrefs.LOAD_STORE_DIR, f.getAbsolutePath(), true);
                    try {
                        fileName = ((ExtensionFileFilter) chooser
                                .getFileFilter()).getPath(f);
                    } catch (ClassCastException x) {
                        fileName = f.getPath();
                    }
                    sqlDialog.setFileName(fileName);
                    objDialog.setFileName(fileName);
                    String ext = ((ExtensionFileFilter) chooser.getFileFilter())
                            .getExtension();
                    if (ext.equals("xml"))// cts
                        /*
                               * loadFromFile(fileName,NEW_CTS); else
                               */
                        loadFromFile(fileName, NEW_XML);
                    try {
                        ((Desktop) getPlace().getDesktop())
                                .addPropertyChangeListener(this);
                    } catch (ClassCastException x) {
                    }
                    getScale().setSelectedItem(
                            Integer.toString((int) (100 / getPlace().getDesktop()
                                    .getScale())));
                    setScaleAction();
                    setChanged(false);
                    return true;
                }
            }
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
        return false;
    }

    /**
     * loads user data types
     */
    public void loadUserTypes(Document doc) {
        DataType dt;
        String s1;
        String typeName;
        String dataType;
        String itemName;
        String itemType;
        UserTypeStorageVector typesVector = new UserTypeStorageVector();
        ERDocument erdoc = new ERDocument(doc);
        if (erdoc.setElements("usertype")) {
            do {
                typeName = erdoc.getValue("typename", 0);
                erdoc.next(1);
                dataType = erdoc.getValue("datatype", 1);
                /*
                     * System.out.println("Nalezen uzivatelsky typ "+typeName);
                     * System.out.println("je to typ "+dataType);
                     */
                dt = extractDataType(dataType);
                if (dataType.equals("Object")) {
                    while (erdoc.next(2)) {
                        itemName = erdoc.getValue("itemname", 2);
                        itemType = erdoc.getValue("datatype", 2);
                        ((ObjectDataType) dt).addItem(new UserTypeStorage(
                                itemName, extractDataType(itemType), null));
                        // System.out.println("itemname "+ itemName + " itemtype
                        // " + itemType);
                    }
                    ((ObjectDataType) dt).setTypesVector(typesVector);
                }
                typesVector.addType(new UserTypeStorage(typeName, dt, null));
            } while (erdoc.next(0));
            typeEditor.setTypesVector(typesVector);
        }
    }

    private DataType extractDataType(String name) {
        DataType dt;
        String s1;

        if (name.startsWith("VarChar2")) {
            name = name.substring(9, name.length() - 1);
            name = name.trim();
            dt = new Varchar2DataType();
            ((LengthDataType) dt).setLength(Integer.parseInt(name));
        } else if (name.startsWith("Char")) {
            name = name.substring(5, name.length() - 1);
            name = name.trim();
            dt = new FixedCharDataType();
            ((LengthDataType) dt).setLength(Integer.parseInt(name));
        } else if (name.startsWith("Number")) {
            dt = new GeneralNumberDataType();
            s1 = name.substring(7, name.indexOf(","));
            ((GeneralNumberDataType) dt).setPrecision(Integer.parseInt(s1));
            name = name.substring(name.indexOf(",") + 1, name.length() - 1);
            name = name.trim();
            ((GeneralNumberDataType) dt).setScale(Integer.parseInt(name));
        } else if (name.startsWith("Table of")) {
            name = name.substring(9, name.length());
            name = name.trim();
            dt = new NestedTableDataType(extractDataType(name));
        } else if (name.startsWith("Varray")) {
            dt = new VarrayDataType();
            s1 = name.substring(8, name.indexOf(")"));
            ((VarrayDataType) dt).setLength(Integer.parseInt(s1));
            name = name.substring(name.indexOf(")") + 4, name.length());
            name = name.trim();
            ((VarrayDataType) dt).setType(extractDataType(name));
        } else if (name.equals("Float")) {
            dt = new FloatDataType();
        } else if (name.equals("Date")) {
            dt = new DateDataType();
        } else if (name.equals("Integer")) {
            dt = new IntegerDataType();
        } else if (name.equals("Object")) {
            dt = new ObjectDataType();
        } else
            dt = new UserDefinedDataType(name);
        return dt;
    }

    /**
     * Loads desktop from document model
     */
    public String loadDesktop(Desktop d, int id, Document doc) {
        int i, t, l, tt, ll, w, h, notation;
        String prefix;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser;
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.err.println("Fatal Error, No XML was found");
            e.printStackTrace();
            System.exit(-1);
        }

        String s;
        EntityConstruct ent;
        EntityConstruct child;
        Relation rel;
        Atribute atr;
        Cardinality car;
        UniqueKey uni;
        cz.omnicom.ermodeller.conceptual.ConceptualObject coM;
        cz.omnicom.ermodeller.conceptual.Atribute atrM;
        cz.omnicom.ermodeller.conceptual.ConceptualConstruct ccM;
        cz.omnicom.ermodeller.conceptual.Entity entM;
        cz.omnicom.ermodeller.conceptual.Schema schemaM;
        cz.omnicom.ermodeller.conceptual.Cardinality carM;
        cz.omnicom.ermodeller.conceptual.UniqueKey uniM;
        cz.omnicom.ermodeller.datatype.DataType dt;
        ConceptualConstruct cc;
        int schemaID;
        Vector attrs = new Vector();
        Vector<Integer> attrsPos = new Vector<Integer>();

        try {
            ERDocument erdoc = new ERDocument(doc);
            if (!erdoc.setElements("schema"))
                return "";
            schemaM = (cz.omnicom.ermodeller.conceptual.Schema) d.getModel();
            if (schemaM.getID() > 0) {
                int[] r = d.getHighestRect();
                ll = r[0] + 10;
                tt = r[1] + 10;
                if (ll > tt)
                    ll = 0;
                else
                    tt = 0;
            } else {
                ll = 0;
                tt = 0;
            }
            s = erdoc.getValue("name");
            if (s == null)
                s = "composed";
            prefix = s + "_";
            schemaID = id + (new Integer(erdoc.getValue("id"))).intValue();
            if (erdoc.getValue("notation") != null) {
                notation = (Integer.parseInt(erdoc.getValue("notation")));
                switch (notation) {
                    case 0:
                        setChen();
                        break;
                    case 1:
                        setBinary();
                        break;
                    case 2:
                        setUML();
                        break;
                }
            }
            if (erdoc.setElements("entity"))
                do {
                    t = tt + (new Integer(erdoc.getValue("top"))).intValue();
                    l = ll + (new Integer(erdoc.getValue("left"))).intValue();
                    w = (new Integer(erdoc.getValue("width"))).intValue();
                    h = (new Integer(erdoc.getValue("height"))).intValue();
                    ent = d.createEntity(l, t, w, h, null);
                    ent.setID(id
                            + (new Integer(erdoc.getValue("id"))).intValue());
                    entM = (cz.omnicom.ermodeller.conceptual.Entity) ent
                            .getModel();
                    s = erdoc.getValue("name");
                    if (s == null)
                        s = "";
                    entM.setName(s);
                    s = erdoc.getValue("comment");
                    if (s == null)
                        s = "";
                    entM.setComment(s);
                    s = erdoc.getValue("constraints");
                    if (s == null)
                        s = "";
                    entM.setConstraints(s);
                    if ((s = erdoc.getValue("parent")) != null) {
                        i = id + new Integer(s);
                        d.getEntity(i).addISAChild(
                                ent,
                                new cz.green.event.ResizeEvent(0, 0, 0, 0,
                                        new cz.green.event.ResizeRectangle(0,
                                                0, 0, 0, 0), this));
                    }
                } while (erdoc.next());

            if (erdoc.setElements("relation"))
                do {
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    w = new Integer(erdoc.getValue("width"));
                    h = new Integer(erdoc.getValue("height"));
                    rel = d.createRelation(l, t, w, h);
                    rel.setID(id
                            + (new Integer(erdoc.getValue("id"))).intValue());
                    coM = (cz.omnicom.ermodeller.conceptual.Relation) rel
                            .getModel();
                    s = erdoc.getValue("name");
                    if (s == null)
                        s = "";
                    coM.setName(s);
                    s = erdoc.getValue("comment");
                    if (s == null)
                        s = "";
                    coM.setComment(s);
                } while (erdoc.next());

            if (erdoc.setElements("atribute"))
                do {
                    t = tt + (new Integer(erdoc.getValue("top"))).intValue();
                    l = ll + (new Integer(erdoc.getValue("left"))).intValue();
                    s = erdoc.getValue("ent");
                    if (s == null) {
                        s = erdoc.getValue("rel");
                        cc = d.getRelation(id + (new Integer(s)).intValue());
                    } else
                        cc = d.getEntity(id + (new Integer(s)).intValue());
                    atr = cc.createAtribute(l, t);
                    atr.setID(id
                            + (new Integer(erdoc.getValue("id"))).intValue());
                    atrM = (cz.omnicom.ermodeller.conceptual.Atribute) atr
                            .getModel();
                    attrs.addElement(atr.getModel());
                    s = erdoc.getValue("name");
                    if (s == null)
                        s = "";
                    atrM.setName(s);
                    s = erdoc.getValue("comment");
                    if (s == null)
                        s = "";
                    atrM.setComment(s);
                    dt = new cz.omnicom.ermodeller.datatype.IntegerDataType();
                    s = erdoc.getValue("datatype");
                    dt = extractDataType(s);
                    atrM.setDataType(dt);
                    atrM.setArbitrary(
                            (Boolean.valueOf(erdoc.getValue("arbitrary"))).booleanValue());
                    atrM.setPrimary(
                            (Boolean.valueOf(erdoc.getValue("primary"))).booleanValue());
                    atrM.setUnique(
                            (Boolean.valueOf(erdoc.getValue("uniq"))).booleanValue());
                    attrsPos.addElement((new Integer(erdoc.getValue("position"))).intValue());
                } while (erdoc.next());

            if (erdoc.setElements("cardinality"))
                do {
                    t = tt + (new Integer(erdoc.getValue("top"))).intValue();
                    l = ll + (new Integer(erdoc.getValue("left"))).intValue();
                    ent = d.getEntity(id
                            + (new Integer(erdoc.getValue("ent"))).intValue());
                    rel = d.getRelation(id
                            + (new Integer(erdoc.getValue("rel"))).intValue());
                    car = rel.createCardinality(ent, d, l, t);
                    car.setID(id
                            + (new Integer(erdoc.getValue("id"))).intValue());
                    carM = (cz.omnicom.ermodeller.conceptual.Cardinality) car
                            .getModel();
                    s = erdoc.getValue("name");
                    if (s == null)
                        s = "";
                    carM.setName(s);
                    s = erdoc.getValue("comment");
                    if (s == null)
                        s = "";
                    carM.setComment(s);
                    s = erdoc.getValue("arbitrary");
                    carM.setArbitrary(Boolean.valueOf(s).booleanValue());
                    s = erdoc.getValue("multi");
                    carM.setMultiCardinality(Boolean.valueOf(s).booleanValue());
                    s = erdoc.getValue("glue");
                    carM.setGlue(Boolean.valueOf(s).booleanValue());
                } while (erdoc.next());

            if (erdoc.setElements("unique"))
                do {
                    t = tt + (new Integer(erdoc.getValue("top"))).intValue();
                    l = ll + (new Integer(erdoc.getValue("left"))).intValue();
                    ent = d.getEntity(id
                            + (new Integer(erdoc.getValue("ent"))).intValue());
                    ccM = (cz.omnicom.ermodeller.conceptual.Entity) ent
                            .getModel();
                    uni = ent.createUniqueKey(l, t);
                    uni.setID(id
                            + (new Integer(erdoc.getValue("id"))).intValue());
                    uniM = (cz.omnicom.ermodeller.conceptual.UniqueKey) uni
                            .getModel();
                    s = erdoc.getValue("name");
                    if (s == null)
                        s = "";
                    uniM.setName(s);
                    s = erdoc.getValue("comment");
                    if (s == null)
                        s = "";
                    uniM.setComment(s);
                    erdoc.setNode("atr");
                    while ((s = erdoc.getNextValue()) != null) {
                        atr = d.getAtribute(id + (new Integer(s)).intValue());
                        uni.addAtribute(atr);
                    }
                    if (Boolean.valueOf(erdoc.getValue("primary")).booleanValue())
                        uni.setPrimary();
                } while (erdoc.next());

            if (erdoc.setElements("strong"))
                do {
                    t = tt + (new Integer(erdoc.getValue("top"))).intValue();
                    l = ll + (new Integer(erdoc.getValue("left"))).intValue();
                    i = id + (new Integer(erdoc.getValue("ent"))).intValue();
                    int j = id
                            + (new Integer(erdoc.getValue("child"))).intValue();
                    ent = d.getEntity(i);
                    child = (EntityConstruct) d.getConceptualObject(j);
                    StrongAddiction.createStrongAddiction(ent, child, child.getManager(), l, t);
                } while (erdoc.next());
            schemaM.setID(schemaID);
            // System.out.println(schemaM.getID());
            for (int j = 0; j < attrs.size(); j++)
                ((cz.omnicom.ermodeller.conceptual.Atribute) attrs.get(j)).setPosition(attrsPos.get(j));
            Vector v = d.getAllEntities();
            for (int j = 0; j < v.size(); j++)
                ((EntityConstruct) v.get(j)).recalculatePositionsOfAtributes();

        } catch (Exception e) {
            ShowException se = new ShowException(null, "Error", e, true);
            return "";
        }
        return prefix;
    }

    /**
     * Loads desktop from document model
     */
    public int loadNotation(Desktop d, int id, Document doc) {
        int notation = 0;
        try {
            ERDocument erdoc = new ERDocument(doc);
            if (!erdoc.setElements("schema"))
                return 0;
            if (erdoc.getValue("notation") != null) {
                notation = (Integer.parseInt(erdoc.getValue("notation")));
            }

        } catch (Exception e) {
            ShowException se = new ShowException(null, "Error", e, true);
            return 0;
        }
        return notation;
    }

    /**
     * Loads schema from file
     */
    public void loadFromFile(String fileName, int what)
            throws java.io.IOException, ClassNotFoundException {
        int i, id;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = null;
        try {
            parser = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.err.println("Fatal Error, No XML was found");
            e.printStackTrace();
            System.exit(-1);
        }

        String s;
        EntityConstruct ent;
        Relation rel;
        Atribute atr;
        Cardinality car;
        UniqueKey uni;
        cz.omnicom.ermodeller.conceptual.ConceptualObject coM;
        cz.omnicom.ermodeller.conceptual.Atribute atrM;
        cz.omnicom.ermodeller.conceptual.ConceptualConstruct ccM;
        cz.omnicom.ermodeller.conceptual.Entity entM;
        cz.omnicom.ermodeller.conceptual.Schema schemaM;
        cz.omnicom.ermodeller.conceptual.Cardinality carM;
        cz.omnicom.ermodeller.conceptual.UniqueKey uniM;
        cz.omnicom.ermodeller.datatype.DataType dt;
        ConceptualConstruct cc;
        Desktop d;

        //parser.setAllowJavaEncodingName(true);

        switch (what) {
            case NEW_CTS:
                getPlace().loadFromFile(fileName);
                break;
            case NEW_XML:
                getPlace().setScale(1);
                getPlace().clearDesktop();
                if ((i = fileName.lastIndexOf("\\")) >= 0)
                    s = fileName.substring(i + 1);
                else
                    s = fileName;
                setTitle(TITLE + " - " + s);
            case WITH_XML:
                Document doc = null;
                try {
                    doc = parser.parse(new File(fileName));
                } catch (Exception e) {
                    ShowException se = new ShowException(null, "Error", e, true);
                }
                //Document doc = parser.getDocument();
                if (doc != null)
                    try {
                        loadUserTypes(doc);
                        java.awt.Rectangle rb = place.getBounds();
                        d = new Desktop(place, rb.x, rb.y, rb.width, rb.height);
                        d.addShowErrorListener(place);
                        id = ((cz.omnicom.ermodeller.conceptual.Schema) ((Desktop) place
                                .getDesktop()).getModel()).createID();
                        id++;
                        if (what == WITH_XML) {
                            int withNotation = loadNotation(d, id, doc);
                            //		System.out.println("act notation " + ConceptualConstruct.ACTUAL_NOTATION + ", with not " + withNotation);
                            if (ConceptualConstruct.ACTUAL_NOTATION != withNotation) {
                                System.out.println("different notations");
                                repaint();
                                javax.swing.JOptionPane
                                        .showMessageDialog(
                                                null,
                                                "Refused, the schema to be merged is in different graphic notation",
                                                "Compose",
                                                javax.swing.JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                        String prefix = loadDesktop(d, id, doc);
                        if (what == NEW_XML) {
                            schemaM = (cz.omnicom.ermodeller.conceptual.Schema) d
                                    .getModel();
                            place.addDesktop(d);
                            getPlace().getDesktop();
                            repaint();
                            break;
                        }
                        schemaM = (cz.omnicom.ermodeller.conceptual.Schema) d
                                .getModel();
                        ErrorLogList errList = schemaM.checkConsistency();
                        if (errList.size() > 0) {
                            repaint();
                            javax.swing.JOptionPane
                                    .showMessageDialog(
                                            null,
                                            "Refused, the schema to be merged is not consistent",
                                            "Compose",
                                            javax.swing.JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                        d = (Desktop) place.getDesktop();
                        loadDesktop(d, id, doc);
                        repaint();
                        ConceptualObjectVectorValidationError errV;
                        ValidationError err;
                        schemaM = (cz.omnicom.ermodeller.conceptual.Schema) d
                                .getModel();
                        schemaM.setComposeID(id);
                        conflictsDialog.setPrefix(prefix);
                        conflictsDialog.setID(id);
                        conflictsDialog.setDesktop(d);
                        conflictsDialog.setErrorLogList(schemaM.checkConsistency());
                        conflictsDialog.setVisible(true);
                    } catch (Exception e) {
                        ShowException se = new ShowException(null, "Error", e, true);
                    }
        }
    }

    /**
     * This method was created by Jiri Mares
     */
    public void lookFeel() {
        JDialog dialog = SelectUI.getSelectUIDialog(this);
        dialog.setVisible(true);
    }

    /**
     * Starts the ER modeller as application.
     */
    public static void main(java.lang.String[] args) {

        String helpURL = null, defDir = null;
        boolean winLF = false;

        // System.getProperties().list(System.out);
        // System.out.println(System.getProperty("java.class.path"));

        for (int i = args.length - 1; i >= 0; i--) {
            if (args[i].equals("-help"))
                helpURL = args[i + 1];
            if (args[i].equals("-directory"))
                defDir = args[i + 1];
//			System.out.println("defDir: " + defDir);
/*			if (args[i].equals("-w"))
				winLF = true;
*/
        }
        // java.util.Locale.setDefault(java.util.Locale.ENGLISH);
        if (defDir != null)
            Consts.DEF_LOAD_STORE_DIR = defDir;
        if (helpURL != null)
            Consts.DEF_HELPPATH = helpURL;
        try {
            if (winLF)
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            ERModeller app = new ERModeller();
            app.setVisible(true);
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "General Error", x, true);
            System.exit(1);
        }
    }

    /**
     * Find main ISA parent of entity ent
     *
     * @param ent
     * @return
     */
    private EntityConstruct mainISAParent(EntityConstruct ent) {
        if (ent.isSetAsISAChild()) {
            EntityConstruct parent = ent.getISAParent();
            if (parent.isSetAsISAChild()) {
                return mainISAParent(parent);
            }
            return parent;
        }
        return ent;
    }

    /**
     * Minimize all objects in the schema (count visible atributes in Entities and return minimal size)
     */
    public void minimizeAll() {
        Desktop d = (Desktop) getPlace().getDesktop();
        Vector v = d.getAllEntities();
        for (Object aV : v) {
            EntityConstruct ent = (EntityConstruct) aV;
            /*Resize entity if is too small to show inside all atributes */
            ResizeRectangle rr = new ResizeRectangle(
                    0, 0, 0, 0, ResizePoint.BOTTOM
                    | ResizePoint.RIGHT);
            ent.minimizeEntity(new ResizeEvent(ent.getBounds().x, ent.getBounds().y, 0, 0, rr, this));
        }
        Vector rels = d.getAllRelations();
        for (Object rel1 : rels) {
            Relation rel = (Relation) rel1;
            /*Minimize size of relation */
            ResizeRectangle rr = new ResizeRectangle(
                    0, 0, 0, 0, ResizePoint.BOTTOM
                    | ResizePoint.RIGHT);
            rel.minimizeRelation(new ResizeEvent(rel.getBounds().x, rel.getBounds().y, 0, 0, rr, this));
        }
    }

    /**
     * Open the options dialog
     */
    public boolean options() {
        optDialog.setActualValues();
        optDialog.setVisible(true);
        return true;
    }

    /**
     * Loads the schema from a file.
     */
    public void print() {
        PrintPreviewDialog d = new PrintPreviewDialog(this, "ER schema");
        d.setDesktop(getPlace().getDesktop(), getPlace().getReferentFont());
        if (fileName != null)
            d.setFileName(fileName);
        if (d.selectPrintJob())
            d.setVisible(true);
    }

    /**
     * This method is invoked when shema is changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("scale")) {
            setChanged(true);
            getScale().setSelectedItem(
                    new Integer((int) (100 / ((Float) evt.getNewValue())
                            .floatValue())).toString());
            setScaleAction();
        }
        if (evt.getPropertyName().equals("workMode")) {
            String mode = "img/working.gif";
            switch ((Integer) evt.getNewValue()) {
                case cz.green.event.Container.WORKING:
                    mode = "img/working.gif";
                    break;
                case cz.green.event.Container.DELETING:
                    mode = "img/deleting.gif";
                    break;
                case cz.green.event.Container.MOVING:
                    setChanged(true);
                    mode = "img/moving.gif";
                    break;
                case cz.green.event.Container.RESIZING:
                    setChanged(true);
                    mode = "img/resizing.gif";
                    break;
                case cz.green.event.Container.ADDING_WINDOW:
                    setChanged(true);
                    mode = "img/aWindow.gif";
                    break;
                case cz.green.event.Container.ADDING_GROUP:
                    setChanged(true);
                    mode = "img/aGroup.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_ENTITY:
                    setChanged(true);
                    mode = "img/aEntity.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_RELATION:
                    setChanged(true);
                    mode = "img/aRelation.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_RELATION_AND_CONNECTION:
                    setChanged(true);
                    mode = "img/aRelationConn.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_CONNECTION:
                    setChanged(true);
                    mode = "img/aCardinality.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_AS_ISA_CHILD:
                    setChanged(true);
                    mode = "img/aSetISAchild.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_ATRIBUTE:
                    setChanged(true);
                    mode = "img/aAtribute.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_UNIQUE_KEY:
                    setChanged(true);
                    mode = "img/aUKey.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_CARDINALITY:
                    setChanged(true);
                    mode = "img/aCardinality.gif";
                    break;
                case cz.green.ermodeller.Container.ADDING_STRONGADDICTION:
                    setChanged(true);
                    mode = "img/aSAddiction.gif";
                    break;
                case cz.green.ermodeller.Container.REMOVING:
                    mode = "img/removing.gif";
                    break;
                case cz.green.ermodeller.Container.COMPOSING_ENTITY:
                case cz.green.ermodeller.Container.COMPOSING_RELATION:
                    mode = "img/removing.gif";
                    break;
            }
            getMode().setIcon(
                    new ImageIcon(ClassLoader.getSystemResource(mode)));
        }
    }

    /**
     * Resize Strong addiction rectangle - same size for CHEN and UML, bigger size for BINARY
     *
     * @param ent
     * @param nextNotation
     */
    private void resizeStrongAddictions(EntityConstruct ent, int nextNotation) {
        java.util.Enumeration e = ent.getConnections().elements();
        java.awt.FontMetrics fm = ((FontManager) ent.getManager()).getReferentFontMetrics();
        StrongAddiction sa = null;
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof StrongAddiction) sa = ((StrongAddiction) c.getOne());
            if (c.getTwo() instanceof StrongAddiction) sa = ((StrongAddiction) c.getTwo());
            if (sa != null) {
                cz.green.event.ResizeRectangle rr = new cz.green.event.ResizeRectangle(
                        0, 0, 0, 0, cz.green.event.ResizePoint.BOTTOM
                        | cz.green.event.ResizePoint.RIGHT);
                java.awt.Rectangle saR = sa.getBounds();
                switch (nextNotation) {
                    case (ConceptualConstruct.CHEN):
                        sa.handleResizeEvent(new ResizeEvent(saR.x, saR.y, -(saR.width - StrongAddiction.SIZE), -(saR.height - StrongAddiction.SIZE), rr, null));
                        break;
                    case (ConceptualConstruct.BINARY):
                        sa.handleResizeEvent(new ResizeEvent(saR.x, saR.y, -(saR.width - (fm.getAscent() + fm.stringWidth("N:N"))), -(saR.height - (int) (2.25 * fm.getAscent())), rr, null));
                        break;
                    case (ConceptualConstruct.UML):
                        sa.handleResizeEvent(new ResizeEvent(saR.x, saR.y, -(saR.width - StrongAddiction.SIZE), -(saR.height - StrongAddiction.SIZE), rr, null));
                        break;
                }
                sa.moveStrongAddiction(new ExMovingEvent(saR.x, saR.y, 0, 0, null, false));
            }
        }
    }

    /**
     * Saves the schema to a file.
     */
    public boolean save() {
        int i;
        String s;

        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save schema");

            ExtensionFileFilter ff;
            /*
                * ff = new ExtensionFileFilter("cts", "Conceptual schemas
                * (*.cts)"); chooser.addChoosableFileFilter(ff);
                * chooser.setFileFilter(ff);
                */
            ff = new ExtensionFileFilter("xml", "Conceptual schemas (*.xml)");
            chooser.addChoosableFileFilter(ff);
            chooser.setFileFilter(ff);
            if (fileName != null) {
                java.io.File f = new java.io.File(fileName);
                i = fileName.lastIndexOf(f.getName());
                if (i > -1) {
                    String dir = fileName.substring(0, i);
                    chooser.setCurrentDirectory(new java.io.File(dir));
                    chooser.setSelectedFile(f);
                }
            } else {
                chooser.setCurrentDirectory(new File(AppPrefs.getProperty(AppPrefs.LOAD_STORE_DIR, Consts.DEF_LOAD_STORE_DIR)));
                String name = ((cz.omnicom.ermodeller.conceptual.Schema) (((Desktop) getPlace()
                        .getDesktop()).getModel())).getName()
                        + ".xml";
                chooser.setSelectedFile(new java.io.File(name));
            }
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                if (f != null) {
                    AppPrefs.storeProperty(AppPrefs.LOAD_STORE_DIR, f.getAbsolutePath(), true);
                    try {
                        fileName = ((ExtensionFileFilter) chooser
                                .getFileFilter()).getPath(f);
                    } catch (ClassCastException x) {
                        fileName = f.getPath();
                    }
                    String ext = ((ExtensionFileFilter) chooser.getFileFilter())
                            .getExtension();
                    if (ext.equals("xml")) {
                        /*
                               * getPlace().saveToFile(fileName); else {
                               */
                        PrintWriter pw = new PrintWriter(new BufferedWriter(
                                new FileWriter(fileName)));
                        ((Desktop) getPlace().getDesktop()).write(AppPrefs.getProperty(AppPrefs.ENCODING, Consts.DEF_ENCODING),
                                typeEditor.getTypesVector(), pw);
                        pw.flush();
                        pw.close();
                    }
                    sqlDialog.setFileName(fileName);
                    objDialog.setFileName(fileName);
                    setChanged(false);
                    typeEditor.setUnchanged();
                    if ((i = fileName.lastIndexOf("\\")) >= 0)
                        s = fileName.substring(i + 1);
                    else
                        s = fileName;
                    setTitle(TITLE + " - " + s);
                    return true;
                }
            }
        } catch (Throwable x) {
            ShowException d = new ShowException(null, "Error", x, true);
        }
        return false;
    }

    /**
     * Set display all deatails in the schema
     */
    public void setLODfull() {
        ConceptualConstruct.ACTUAL_LOD = ConceptualConstruct.LOD_FULL;
        lodStatusLabel.setText("Full details");
        minimizeAll();
        repaint();
    }

    /**
     * Set display medium details in thte schema - without atributes which are not members fo primary key
     */
    public void setLODmedium() {
        ConceptualConstruct.ACTUAL_LOD = ConceptualConstruct.LOD_MEDIUM;
        lodStatusLabel.setText("Medium details");
        if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.UML) {
            Desktop d = (Desktop) getPlace().getDesktop();
            Vector ents = d.getAllEntities();
            for (Object ent1 : ents) {
                EntityConstruct ent = (EntityConstruct) ent1;
                ent.collectPKatributes();
            }
        }
        minimizeAll();
        repaint();
    }

    /**
     * Set display low details in the schema - without all atributes
     */
    public void setLODlow() {
        ConceptualConstruct.ACTUAL_LOD = ConceptualConstruct.LOD_LOW;
        lodStatusLabel.setText("Low details");
        minimizeAll();
        repaint();
    }


    /**
     * Set new graphic notation (CHEN, BINARY or UML)
     *
     * @param notation - new notation
     */
    private synchronized void setNotation(int notation) {
        ConceptualConstruct.ACTUAL_NOTATION = notation;
    }

    /**
     * Swith notation to Chen
     */
    public void setChen() {
        Desktop desktop = (Desktop) getPlace().getDesktop();

        if (ConceptualConstruct.ACTUAL_NOTATION != ConceptualConstruct.CHEN) {
            desktop.decomposeTernaryRels(place);
            if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.BINARY)
                desktop.switchAllRConnectionsCard(place);
            else desktop.switchAllRConnectionsBoth(place);
            setChanged(true);

            Vector allEntities = desktop.getAllEntities();

            setNotation(ConceptualConstruct.CHEN);

            for (Object allEntity : allEntities) {
                EntityConstruct ent = (EntityConstruct) allEntity;
                if (ent.isStrongAddictionChild)
                    setNewStrongAddictionsManager(ent);
            }

            for (Object allEntity : allEntities) {
                EntityConstruct ent = (EntityConstruct) allEntity;
                int width = mainISAParent(ent).getBounds().width;
                ent.moveAtributesBinarytoChen(width);
                resizeStrongAddictions(ent, ConceptualConstruct.CHEN);
            }
            Vector rels = desktop.getAllRelations();
            for (Object rel1 : rels) {
                Relation rel = (Relation) rel1;
//				int height=rel.getBounds().height, width=rel.getBounds().width;
                FontMetrics fm = ((FontManager) rel.getManager()).getReferentFontMetrics();
                int width = fm.stringWidth(rel.model.getName()), height = fm.getAscent();
                try {
                    rel.resize(2 * width + height - 7, 3 * height - 7, (ResizePoint.RIGHT | ResizePoint.BOTTOM), true);
                    rel.handleMoveEvent(new MoveEvent(rel.getBounds().x, rel.getBounds().y, -rel.getBounds().width / 2 + 4, -rel.getBounds().height / 2 + 4, null));

                } catch (ItemNotInsideManagerException e) {
                    e.printStackTrace();
                }
            }
        }

        notationStatusLabel.setText("Chan Notation");
        minimizeAll();
        repaint();
    }

    /**
     * Swith notation to binary
     */
    public void setBinary() {
        Desktop d = (Desktop) getPlace().getDesktop();
        setChanged(true);

        if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.CHEN) {
            d.delRelsWithoutConnection();
            d.decomposeRelsWithAtributes(place);
            d.decomposeTernaryRels(place);
            d.switchAllRConnectionsCard(place);
        } else {
            d.switchAllRConnectionsArb(place);
        }
        Vector allEntities = d.getAllEntities();

        for (Object allEntity : allEntities) {
            EntityConstruct ent = (EntityConstruct) allEntity;
            ent.collectPKatributes();
        }

        setNotation(ConceptualConstruct.BINARY);

        for (Object allEntity : allEntities) {
            EntityConstruct ent = (EntityConstruct) allEntity;
            ent.recalculatePositionsOfAtributes();
            if (ent.isStrongAddictionChild)
                setNewStrongAddictionsManager(ent);
            /*Resize entity if is too small to show inside all atributes */
            ResizeRectangle rr = new ResizeRectangle(
                    0, 0, 0, 0, ResizePoint.BOTTOM
                    | ResizePoint.RIGHT);
            ent.resizeEntity(new ResizeEvent(ent.getBounds().x, ent.getBounds().y, 1, 1, rr, this));
            if (ent.isStrongAddictionChild)
                resizeStrongAddictions(ent, ConceptualConstruct.BINARY);
        }

        Vector rels = d.getAllRelations();
        for (int i = 0; i < rels.size(); i++) {
            Relation rel = (Relation) rels.get(i);

            /*Check for aributes*/
/*			if (!rel.getAtributes().isEmpty()) {
				System.out.println("Zadne atributy u relationspih nejsou v BIN povoleny"); 
				rel.decompose(new SelectItemEvent(rel.getBounds().x, rel.getBounds().x, false, place));
			}
*/
            /*Change size of relations to BLACK DOT*/
            int height = rel.getBounds().height, width = rel.getBounds().width;
            try {
                rel.resize(7 - width, 7 - height, (ResizePoint.RIGHT | ResizePoint.BOTTOM), true);
                rel.handleMoveEvent(new MoveEvent(rel.getBounds().x, rel.getBounds().y, width / 2 - 4, height / 2 - 4, null));
            } catch (ItemNotInsideManagerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /* Move cardinalities to its Entities*/
            java.util.Enumeration e = rel.getConnections().elements();
            Cardinality car;
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof Cardinality) {
                    car = ((Cardinality) c.getOne());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
                if (c.getTwo() instanceof Cardinality) {
                    car = ((Cardinality) c.getTwo());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
            }
        }
        notationStatusLabel.setText("Binary Notation");
        minimizeAll();
        repaint();
    }

    /**
     * Swith notation to UML
     */
    public void setUML() {
        Desktop d = (Desktop) getPlace().getDesktop();
        setChanged(true);

        if (ConceptualConstruct.ACTUAL_NOTATION == ConceptualConstruct.CHEN) {
            d.delRelsWithoutConnection();
            d.decomposeRelsWithAtributes(place);
            d.decomposeTernaryRels(place);
            d.switchAllRConnectionsBoth(place);
        } else {
            d.switchAllRConnectionsArb(place);
        }
        Vector allEntities = d.getAllEntities();

        setNotation(ConceptualConstruct.UML);
        for (int i = 0; i < allEntities.size(); i++) {
            EntityConstruct ent = (EntityConstruct) allEntities.get(i);
            ent.recalculatePositionsOfAtributes();
            resizeStrongAddictions(ent, ConceptualConstruct.UML);
            if (ent.isStrongAddictionChild)
                setNewStrongAddictionsManager(ent);
        }

        Vector rels = d.getAllRelations();
        for (int i = 0; i < rels.size(); i++) {
            Relation rel = (Relation) rels.get(i);
            int height = rel.getBounds().height, width = rel.getBounds().width;
            try {
                rel.resize(7 - width, 7 - height, (ResizePoint.RIGHT | ResizePoint.BOTTOM), true);
            } catch (ItemNotInsideManagerException e) {
                e.printStackTrace();
            }
            /* Attach all cardinalities to its Entities*/
            java.util.Enumeration e = rel.getConnections().elements();
            Cardinality car;
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof Cardinality) {
                    car = ((Cardinality) c.getOne());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
                if (c.getTwo() instanceof Cardinality) {
                    car = ((Cardinality) c.getTwo());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
            }
        }
        notationStatusLabel.setText("UML Notation");
        minimizeAll();
    }

    /**
     * Sets the schema is changed
     */
    public void setChanged(boolean newChanged) {
        Desktop d = (Desktop) (getPlace().getDesktop());
        ((cz.omnicom.ermodeller.conceptual.Schema) d.getModel())
                .setChanged(newChanged);
        changed = newChanged;
    }

    /**
     * Set new Look and Feel of application
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     *
     */
    public void setMetalLAF() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Set new Look and Feel of application
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     *
     */
    public void setWindowsLAF() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Set new Look and Feel of application
     *
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws UnsupportedLookAndFeelException
     *
     */
    public void setMotifLAF() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        SwingUtilities.updateComponentTreeUI(this);
    }

    /**
     * Set new Manager to StrongAddiction
     *
     * @param ent
     */
    private void setNewStrongAddictionsManager(EntityConstruct ent) {
        java.util.Enumeration e = ent.getConnections().elements();
        java.awt.FontMetrics fm = ((FontManager) ent.getManager()).getReferentFontMetrics();
        StrongAddiction sa = null;
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof StrongAddiction) sa = ((StrongAddiction) c.getOne());
            if (c.getTwo() instanceof StrongAddiction) sa = ((StrongAddiction) c.getTwo());
            if (sa != null) {
                try {
                    switch (ConceptualConstruct.ACTUAL_NOTATION) {
                        case (ConceptualConstruct.CHEN):
                            sa.getManager().remove(sa);
                            ent.getManager().add(sa);
                            break;
                        case (ConceptualConstruct.BINARY):
                            sa.getManager().remove(sa);
                            ent.getManager().add(sa);
                            break;
                        case (ConceptualConstruct.UML):
                            sa.getManager().remove(sa);
                            sa.getParent().getManager().add(sa);
                            break;
                    }
                } catch (ItemNotInsideManagerException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets the new scale.
     */
    public void setScaleAction() {
        getPlace().setScale(
                ((float) 100.0)
                        / ((float) Integer.parseInt(getScale().getSelectedItem()
                        .toString())));
        getScrollPane().doLayout();
        getPlace().repaint();
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        int option;
        if (isChanged()) {
            option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save the schema?", "Save question",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            switch (option) {
                case 0:
                    if (!save())
                        return;
                    break;
                case 2:
                    return;
            }
        }
        dispose();
        System.exit(0);
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }
}