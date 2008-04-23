package cz.green.ermodeller;

import cz.green.ermodeller.dialogs.AboutDialog;
import cz.green.ermodeller.dialogs.ChangeNotationDialog;
import cz.green.ermodeller.dialogs.OptionsDialog;
import cz.green.ermodeller.interfaces.FontManager;
import cz.green.event.*;
import cz.green.event.exceptions.ItemNotInsideManagerException;
import cz.green.eventtool.dialogs.PrintPreviewDialog;
import cz.green.eventtool.dialogs.PropertyListDialog;
import cz.green.eventtool.interfaces.Connection;
import cz.green.swing.ExtensionFileFilter;
import cz.green.swing.ShowException;
import cz.green.util.ActionAdapter;
import cz.green.util.ParamActionAdapter;
import cz.omnicom.ermodeller.conc2obj.ObjDialog;
import cz.omnicom.ermodeller.conceptual.NotationType;
import cz.omnicom.ermodeller.conceptual.beans.*;
import cz.omnicom.ermodeller.datatype.*;
import cz.omnicom.ermodeller.errorlog.ConceptualObjectVectorValidationError;
import cz.omnicom.ermodeller.errorlog.ErrorLogList;
import cz.omnicom.ermodeller.errorlog.ValidationError;
import cz.omnicom.ermodeller.errorlog.dialogs.ConflictsDialog;
import cz.omnicom.ermodeller.errorlog.dialogs.ErrorLogDialog;
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
import java.util.Enumeration;
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
    private Container place = null;

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
        final int activeNotation = AppPrefs.getProperty(AppPrefs.GENERAL_DEFNOTATION, Consts.DEF_GENERAL_DEFNOTATION);
        if (NotationType.CHEN.ordinal() == activeNotation) {
            setChen();
        } else if (NotationType.CHEN.ordinal() == activeNotation) {
            setBinary();
        } else if (NotationType.UML.ordinal() == activeNotation) {
            setUML();
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

        Schema.SHOW_SHORTEN_CARD_IN_UML = AppPrefs.getProperty(AppPrefs.GENERAL_SHORTEN_CARDS_UML, Consts.DEF_GENERAL_SHORTEN_CARDS_UML);

        WindowItem.OBJECT_FOREGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_OBJECT_FG, Consts.DEF_COLORS_OBJECT_FG);
        WindowItem.OBJECT_BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_OBJECT_BG, Consts.DEF_COLORS_OBJECT_BG);
        WindowItem.SELECTED_OBJECT_BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_SELOBJECT_BG, Consts.DEF_COLORS_SELOBJECT_BG);
        WindowItem.BACKGROUND_COLOR = AppPrefs.getProperty(AppPrefs.COLORS_BG, Consts.DEF_COLORS_BG);
    }

    /**
     * Saves configuration to file
     *
     * @param driver JDBC SQL driver
     * @param url    connection DB url
     * @param user   user name
     */
    public void writeUserConfigFile(String driver, String url, String user) {
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_DRIVER, driver);
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_URL, url);
        AppPrefs.storeProperty(AppPrefs.DBCONNECT_USER, user);

        AppPrefs.storeProperty(AppPrefs.GENERAL_SHORTEN_CARDS_UML, Schema.SHOW_SHORTEN_CARD_IN_UML);
        AppPrefs.storeProperty(AppPrefs.COLORS_OBJECT_FG, WindowItem.OBJECT_FOREGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_OBJECT_BG, WindowItem.OBJECT_BACKGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_SELOBJECT_BG, WindowItem.SELECTED_OBJECT_BACKGROUND_COLOR);
        AppPrefs.storeProperty(AppPrefs.COLORS_BG, WindowItem.BACKGROUND_COLOR);
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
        Desktop d = getDesktop();
        Schema schema = getSchema();
        if (schema.getComposeID() > schema.getID()) {//?????????
            conflictsDialog.setID(0);
            conflictsDialog.setDesktop(d);
            conflictsDialog.setPrefix("");
        }
        conflictsDialog.refreshButton_ActionEvents();
        conflictsDialog.setVisible(true);
    }

    private Schema getSchema() {
        Desktop d = getDesktop();
        return (Schema) d.getModel();
    }

    private Desktop getDesktop() {
        return (Desktop) getPlace().getDesktop();
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
        final int activeNotation = AppPrefs.getProperty(AppPrefs.GENERAL_DEFNOTATION, Consts.DEF_GENERAL_DEFNOTATION);
        if (NotationType.CHEN.ordinal() == activeNotation) {
            setChen();
        } else if (NotationType.BINARY.ordinal() == activeNotation) {
            setBinary();
        } else if (NotationType.UML.ordinal() == activeNotation) {
            setUML();
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
                Schema model = getSchema();
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
                        if (getNotationType() != ConceptualConstructItem.CHEN) {
                            if (getNotationType() == ConceptualConstructItem.BINARY)
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
                        if (getNotationType() != ConceptualConstructItem.CHEN) {
                            if (getNotationType() == ConceptualConstructItem.BINARY)
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
            new ShowException(null, "Error", x, true);
        }
    }

    /**
     * Generation object-relation script
     */
    public void generateObj() {
        try {

            if (getPlace().getDesktop() instanceof Desktop) {
                cz.omnicom.ermodeller.conc2rela.SchemaC2R schemaC2R;
                Schema model = getSchema();
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
                        final NotationType type = getNotationType();
                        if (type != ConceptualConstructItem.CHEN) {
                            if (type == ConceptualConstructItem.BINARY)
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
                        if (type != ConceptualConstructItem.CHEN) {
                            if (type == ConceptualConstructItem.BINARY)
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
            new ShowException(null, "Error", x, true);
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
    public Container getPlace() {
        if (place == null) {
            place = new Container(2500, 2500);
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
                Schema model = getSchema();
                errDialog.setErrorLogList(model.checkConsistency());
            }
        } catch (Throwable x) {
            new ShowException(null, "Error", x, true);
        }
        errDialog.setDesktop((Desktop) getPlace().getDesktop());
        errDialog.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to CHEN
     */
    public void checkChen() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstructItem.CHEN, getNotationType());
        ccnDial.setLocationRelativeTo(this);
        ccnDial.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to UML
     */
    public void checkUML() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstructItem.UML, getNotationType());
        ccnDial.setLocationRelativeTo(this);
        ccnDial.setVisible(true);
    }

    /**
     * Show check and change notation dialog for changing notation to BINARY
     */
    public void checkBinary() {
        ChangeNotationDialog ccnDial = new ChangeNotationDialog(this, this, ConceptualConstructItem.BINARY, getNotationType());
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
        return ((Schema) d.getModel())
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
            new ShowException(null, "Error", x, true);
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
        RelationConstruct rel;
        AttributeConstruct atr;
        CardinalityConstruct car;
        UniqueKeyConstruct uni;
        ConceptualObject coM;
        Atribute atrM;
        ConceptualConstruct ccM;
        Entity entM;
        Schema schemaM;
        Cardinality carM;
        UniqueKey uniM;
        cz.omnicom.ermodeller.datatype.DataType dt;
        ConceptualConstructItem cc;
        int schemaID;
        Vector<Atribute> attrs = new Vector<Atribute>();
        Vector<Integer> attrsPos = new Vector<Integer>();

        try {
            ERDocument erdoc = new ERDocument(doc);
            if (!erdoc.setElements("schema"))
                return "";
            schemaM = (Schema) d.getModel();
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
            schemaID = id + new Integer(erdoc.getValue("id"));
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
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    w = new Integer(erdoc.getValue("width"));
                    h = new Integer(erdoc.getValue("height"));
                    ent = d.createEntity(l, t, w, h, null);
                    ent.setID(id
                            + new Integer(erdoc.getValue("id")));
                    entM = ent
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
                            + new Integer(erdoc.getValue("id")));
                    coM = (Relation) rel
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
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    s = erdoc.getValue("ent");
                    if (s == null) {
                        s = erdoc.getValue("rel");
                        cc = d.getRelation(id + new Integer(s));
                    } else
                        cc = d.getEntity(id + new Integer(s));
                    atr = cc.createAtribute(l, t);
                    atr.setID(id
                            + new Integer(erdoc.getValue("id")));
                    atrM = atr
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
                            Boolean.valueOf(erdoc.getValue("arbitrary")));
                    atrM.setPrimary(
                            Boolean.valueOf(erdoc.getValue("primary")));
                    atrM.setUnique(
                            Boolean.valueOf(erdoc.getValue("uniq")));
                    attrsPos.addElement(new Integer(erdoc.getValue("position")));
                } while (erdoc.next());

            if (erdoc.setElements("cardinality"))
                do {
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    ent = d.getEntity(id
                            + new Integer(erdoc.getValue("ent")));
                    rel = d.getRelation(id
                            + new Integer(erdoc.getValue("rel")));
                    car = rel.createCardinality(ent, d, l, t);
                    car.setID(id
                            + new Integer(erdoc.getValue("id")));
                    carM = (Cardinality) car
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
                    carM.setArbitrary(Boolean.valueOf(s));
                    s = erdoc.getValue("multi");
                    carM.setMultiCardinality(Boolean.valueOf(s));
                    s = erdoc.getValue("glue");
                    carM.setGlue(Boolean.valueOf(s));
                } while (erdoc.next());

            if (erdoc.setElements("unique"))
                do {
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    ent = d.getEntity(id
                            + new Integer(erdoc.getValue("ent")));
                    ccM = ent.getModel();
                    uni = ent.createUniqueKey(l, t);
                    uni.setID(id
                            + new Integer(erdoc.getValue("id")));
                    uniM = (UniqueKey) uni
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
                        atr = d.getAtribute(id + new Integer(s));
                        uni.addAtribute(atr);
                    }
                    if (Boolean.valueOf(erdoc.getValue("primary")))
                        uni.setPrimary();
                } while (erdoc.next());

            if (erdoc.setElements("strong"))
                do {
                    t = tt + new Integer(erdoc.getValue("top"));
                    l = ll + new Integer(erdoc.getValue("left"));
                    i = id + new Integer(erdoc.getValue("ent"));
                    int j = id
                            + new Integer(erdoc.getValue("child"));
                    ent = d.getEntity(i);
                    child = (EntityConstruct) d.getConceptualObject(j);
                    StrongAddiction.createStrongAddiction(ent, child, child.getManager(), l, t);
                } while (erdoc.next());
            schemaM.setID(schemaID);
            // System.out.println(schemaM.getID());
            for (int j = 0; j < attrs.size(); j++)
                (attrs.get(j)).setPosition(attrsPos.get(j));
            Vector v = d.getAllEntities();
            for (Object aV : v) ((EntityConstruct) aV).recalculatePositionsOfAtributes();

        } catch (Exception e) {
            ShowException se = new ShowException(null, "Error", e, true);
            return "";
        }
        return prefix;
    }

    /**
     * Loads desktop from document model
     */
    public NotationType loadNotation(Document doc) {
        NotationType notation = NotationType.CHEN;
        try {
            ERDocument erdoc = new ERDocument(doc);
            if (!erdoc.setElements("schema"))
                return NotationType.CHEN;
            if (erdoc.getValue("notation") != null) {
                notation = NotationType.values()[Integer.parseInt(erdoc.getValue("notation"))];
            }

        } catch (Exception e) {
            ShowException se = new ShowException(null, "Error", e, true);
            return NotationType.CHEN;
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
        RelationConstruct rel;
        AttributeConstruct atr;
        CardinalityConstruct car;
        UniqueKeyConstruct uni;
        ConceptualObject coM;
        Atribute atrM;
        ConceptualConstruct ccM;
        Entity entM;
        Schema schemaM;
        Cardinality carM;
        UniqueKey uniM;
        cz.omnicom.ermodeller.datatype.DataType dt;
        ConceptualConstructItem cc;
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
                        id = ((Schema) ((Desktop) place
                                .getDesktop()).getModel()).createID();
                        id++;
                        if (what == WITH_XML) {
                            NotationType withNotation = loadNotation(doc);
                            //		System.out.println("act notation " + ConceptualConstruct.ACTUAL_NOTATION + ", with not " + withNotation);
                            if (getNotationType() != withNotation) {
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
                            schemaM = (Schema) d
                                    .getModel();
                            place.addDesktop(d);
                            getPlace().getDesktop();
                            repaint();
                            break;
                        }
                        schemaM = (Schema) d
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
                        schemaM = (Schema) d
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
            new ShowException(null, "General Error", x, true);
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
        Desktop d = getDesktop();
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
            RelationConstruct rel = (RelationConstruct) rel1;
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
                    Integer.toString((int) (100 / (Float) evt.getNewValue())));
            setScaleAction();
        }
        if (evt.getPropertyName().equals("workMode")) {
            String mode = "img/working.gif";
            switch ((Integer) evt.getNewValue()) {
                case Container.WORKING:
                    mode = "img/working.gif";
                    break;
                case Container.DELETING:
                    mode = "img/deleting.gif";
                    break;
                case Container.MOVING:
                    setChanged(true);
                    mode = "img/moving.gif";
                    break;
                case Container.RESIZING:
                    setChanged(true);
                    mode = "img/resizing.gif";
                    break;
                case Container.ADDING_WINDOW:
                    setChanged(true);
                    mode = "img/aWindow.gif";
                    break;
                case Container.ADDING_GROUP:
                    setChanged(true);
                    mode = "img/aGroup.gif";
                    break;
                case Container.ADDING_ENTITY:
                    setChanged(true);
                    mode = "img/aEntity.gif";
                    break;
                case Container.ADDING_RELATION:
                    setChanged(true);
                    mode = "img/aRelation.gif";
                    break;
                case Container.ADDING_RELATION_AND_CONNECTION:
                    setChanged(true);
                    mode = "img/aRelationConn.gif";
                    break;
                case Container.ADDING_CONNECTION:
                    setChanged(true);
                    mode = "img/aCardinality.gif";
                    break;
                case Container.ADDING_AS_ISA_CHILD:
                    setChanged(true);
                    mode = "img/aSetISAchild.gif";
                    break;
                case Container.ADDING_ATRIBUTE:
                    setChanged(true);
                    mode = "img/aAtribute.gif";
                    break;
                case Container.ADDING_UNIQUE_KEY:
                    setChanged(true);
                    mode = "img/aUKey.gif";
                    break;
                case Container.ADDING_CARDINALITY:
                    setChanged(true);
                    mode = "img/aCardinality.gif";
                    break;
                case Container.ADDING_STRONGADDICTION:
                    setChanged(true);
                    mode = "img/aSAddiction.gif";
                    break;
                case Container.REMOVING:
                    mode = "img/removing.gif";
                    break;
                case Container.COMPOSING_ENTITY:
                case Container.COMPOSING_RELATION:
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
    private void resizeStrongAddictions(EntityConstruct ent, NotationType nextNotation) {
        java.util.Enumeration e = ent.getConnections().elements();
        java.awt.FontMetrics fm = ((FontManager) ent.getManager()).getReferentFontMetrics();
        StrongAddiction sa = null;
        while (e.hasMoreElements()) {
            Connection c = ((Connection) e.nextElement());
            if (c.getOne() instanceof StrongAddiction) sa = ((StrongAddiction) c.getOne());
            if (c.getTwo() instanceof StrongAddiction) sa = ((StrongAddiction) c.getTwo());
            if (sa != null) {
                cz.green.event.ResizeRectangle rr = new cz.green.event.ResizeRectangle(
                        0, 0, 0, 0, ResizePoint.BOTTOM
                        | ResizePoint.RIGHT);
                java.awt.Rectangle saR = sa.getBounds();
                switch (nextNotation) {
                    case CHEN:
                        sa.handleResizeEvent(new ResizeEvent(saR.x, saR.y, -(saR.width - StrongAddiction.SIZE), -(saR.height - StrongAddiction.SIZE), rr, null));
                        break;
                    case BINARY:
                        sa.handleResizeEvent(new ResizeEvent(saR.x, saR.y, -(saR.width - (fm.getAscent() + fm.stringWidth("N:N"))), -(saR.height - (int) (2.25 * fm.getAscent())), rr, null));
                        break;
                    case UML:
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
                String name = ((Schema) (((Desktop) getPlace()
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
            new ShowException(null, "Error", x, true);
        }
        return false;
    }

    /**
     * Set display all deatails in the schema
     */
    public void setLODfull() {
        getSchema().setLevelOfDetails(Schema.LOD_FULL);
        lodStatusLabel.setText("Full details");
        minimizeAll();
        repaint();
    }

    /**
     * Set display medium details in thte schema - without atributes which are not members fo primary key
     */
    public void setLODmedium() {
        getSchema().setLevelOfDetails(Schema.LOD_MEDIUM);
        lodStatusLabel.setText("Medium details");
        if (getNotationType() == ConceptualConstructItem.UML) {
            Desktop d = getDesktop();
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
        getSchema().setLevelOfDetails(Schema.LOD_LOW);
        lodStatusLabel.setText("Low details");
        minimizeAll();
        repaint();
    }


    /**
     * Set new graphic notation (CHEN, BINARY or UML)
     *
     * @param notation - new notation
     */
    private synchronized void setNotation(NotationType type) {
        getSchema().setNotationType(type);
        //getNotationType() = notation;
    }

    /**
     * Swith notation to Chen
     */
    public void setChen() {
        Desktop desktop = getDesktop();

        if (getNotationType() != ConceptualConstructItem.CHEN) {
            desktop.decomposeTernaryRels(place);
            if (getNotationType() == ConceptualConstructItem.BINARY)
                desktop.switchAllRConnectionsCard(place);
            else desktop.switchAllRConnectionsBoth(place);
            setChanged(true);

            Vector allEntities = desktop.getAllEntities();

            setNotation(ConceptualConstructItem.CHEN);

            for (Object allEntity : allEntities) {
                EntityConstruct ent = (EntityConstruct) allEntity;
                if (ent.isStrongAddictionChild)
                    setNewStrongAddictionsManager(ent);
            }

            for (Object allEntity : allEntities) {
                EntityConstruct ent = (EntityConstruct) allEntity;
                int width = mainISAParent(ent).getBounds().width;
                ent.moveAtributesBinarytoChen(width);
                resizeStrongAddictions(ent, ConceptualConstructItem.CHEN);
            }
            Vector rels = desktop.getAllRelations();
            for (Object rel1 : rels) {
                RelationConstruct rel = (RelationConstruct) rel1;
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

        notationStatusLabel.setText("Chen Notation");
        minimizeAll();
        repaint();
    }

    private NotationType getNotationType() {
        return getSchema().getNotationType();
    }


    /**
     * Swith notation to binary
     */
    public void setBinary() {
        Desktop d = getDesktop();
        setChanged(true);

        if (getNotationType() == ConceptualConstructItem.CHEN) {
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

        setNotation(ConceptualConstructItem.BINARY);

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
                resizeStrongAddictions(ent, ConceptualConstructItem.BINARY);
        }

        Vector rels = d.getAllRelations();
        for (Object rel1 : rels) {
            RelationConstruct rel = (RelationConstruct) rel1;

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
                e.printStackTrace();
            }
            /* Move cardinalities to its Entities*/
            Enumeration e = rel.getConnections().elements();
            CardinalityConstruct car;
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof CardinalityConstruct) {
                    car = ((CardinalityConstruct) c.getOne());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
                if (c.getTwo() instanceof CardinalityConstruct) {
                    car = ((CardinalityConstruct) c.getTwo());
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
        Desktop d = getDesktop();
        setChanged(true);

        if (getNotationType() == ConceptualConstructItem.CHEN) {
            d.delRelsWithoutConnection();
            d.decomposeRelsWithAtributes(place);
            d.decomposeTernaryRels(place);
            d.switchAllRConnectionsBoth(place);
        } else {
            d.switchAllRConnectionsArb(place);
        }
        Vector allEntities = d.getAllEntities();

        setNotation(ConceptualConstructItem.UML);
        for (Object allEntity : allEntities) {
            EntityConstruct ent = (EntityConstruct) allEntity;
            ent.recalculatePositionsOfAtributes();
            resizeStrongAddictions(ent, ConceptualConstructItem.UML);
            if (ent.isStrongAddictionChild)
                setNewStrongAddictionsManager(ent);
        }

        Vector rels = d.getAllRelations();
        for (Object rel1 : rels) {
            RelationConstruct rel = (RelationConstruct) rel1;
            int height = rel.getBounds().height, width = rel.getBounds().width;
            try {
                rel.resize(7 - width, 7 - height, (ResizePoint.RIGHT | ResizePoint.BOTTOM), true);
            } catch (ItemNotInsideManagerException e) {
                e.printStackTrace();
            }
            /* Attach all cardinalities to its Entities*/
            Enumeration e = rel.getConnections().elements();
            CardinalityConstruct car;
            while (e.hasMoreElements()) {
                Connection c = ((Connection) e.nextElement());
                if (c.getOne() instanceof CardinalityConstruct) {
                    car = ((CardinalityConstruct) c.getOne());
                    car.moveCardinality(new ExMovingEvent(car.getBounds().x, car.getBounds().y, 0, 0, null, false));
                }
                if (c.getTwo() instanceof CardinalityConstruct) {
                    car = ((CardinalityConstruct) c.getTwo());
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
        ((Schema) d.getModel())
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
                    switch (getNotationType()) {
                        case CHEN:
                            sa.getManager().remove(sa);
                            ent.getManager().add(sa);
                            break;
                        case BINARY:
                            sa.getManager().remove(sa);
                            ent.getManager().add(sa);
                            break;
                        case UML:
                            sa.getManager().remove(sa);
                            sa.getParent().getManager().add(sa);
                            break;
                    }
                } catch (ItemNotInsideManagerException e1) {
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