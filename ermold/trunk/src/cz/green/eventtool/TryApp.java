package cz.green.eventtool;

import cz.green.swing.ConstantConstraint;
import cz.green.swing.CountLayout;
import cz.green.swing.LinearConstraint;
import cz.green.swing.SimpleBoundsConstraint;
import cz.green.util.ActionAdapter;
import cz.green.util.WindowCloser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The test application for the second stage.
 */
public class TryApp extends JApplet implements KeyListener {
    private JButton activeButton = null;
    /**
     * Button for adding the group
     */
    private JButton addGroupButton = null;
    /**
     * Button for adding the window
     */
    private JButton addWindowButton = null;
    /**
     * Button for deleting the item
     */
    private JButton deleteButton = null;
    /**
     * Button for loading the schema from a file
     */
    private JButton loadButton = null;
    /**
     * Button for printing the schema to a printer
     */
    private JButton printButton = null;
    /**
     * Button for saving the schema to a file
     */
    private JButton saveButton = null;
    /**
     * Input field for the user scale
     */
    private JTextField scale = null;
    /**
     * Scroll pane for the schema
     */
    private JScrollPane scrollPane = null;
    /**
     * Button for setting the scale fot the schema
     */
    private JButton setScaleButton = null;
    /**
     * Button for starting working
     */
    private JButton workButton = null;
    /**
     * The container where the desktop paints
     *
     * @see Container
     * @see Desktop
     */
    private cz.green.eventtool.Container place = null;
    /**
     * The original label of the active button
     */
    private String activeLabel = null;

    public void addGroupButtonAction() {
        if (getPlace().addingGroup())
            setActive(getAddGroupButton());
    }

    public void addWindowButtonAction() {
        if (getPlace().addingWindow())
            setActive(getAddWindowButton());
    }

    public void deleteButtonAction() {
        if (getPlace().deleting())
            setActive(getDeleteButton());
    }

    /**
     * Return the AddGroupButton.
     */
    private JButton getAddGroupButton() {
        if (addGroupButton == null) {
            addGroupButton = new JButton("Add group");
            addGroupButton.setBounds(211, 365, 89, 23);
        }
        ;
        return addGroupButton;
    }

    /**
     * Return the AddWindowGroup.
     */
    private JButton getAddWindowButton() {
        if (addWindowButton == null) {
            addWindowButton = new JButton("Add window");
            addWindowButton.setBounds(311, 365, 89, 23);
        }
        ;
        return addWindowButton;
    }

    /**
     * Gets the applet information.
     */
    public String getAppletInfo() {
        return "cz.green.eventtool.TryApp created using VisualAge for Java.";
    }

    /**
     * Return the DeleteButton.
     */
    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new JButton("Delete");
            deleteButton.setBounds(511, 365, 89, 23);
        }
        ;
        return deleteButton;
    }

    /**
     * Return the LoadButton.
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton("Load");
            loadButton.setBounds(312, 396, 89, 23);
        }
        ;
        return loadButton;
    }

    /**
     * Returns the value of property place. If the value is <code>null</code>,
     * then creates the cz.green.eventtol.Container.
     *
     * @return The place container.
     * @see Container
     */
    private cz.green.eventtool.Container getPlace() {
        if (place == null) {
            place = new cz.green.eventtool.Container(1000, 1000);
            place.setBackground(java.awt.Color.black);
        }
        ;
        return place;
    }

    /**
     * Return the PrintButton.
     */
    private JButton getPrintButton() {
        if (printButton == null) {
            printButton = new JButton("Print");
            printButton.setBounds(411, 396, 89, 23);
        }
        ;
        return printButton;
    }

    /**
     * Return the SaveButton.
     */
    private JButton getSaveButton() {
        if (saveButton == null) {
            saveButton = new JButton("Save");
            saveButton.setBounds(211, 396, 89, 23);
        }
        ;
        return saveButton;
    }

    /**
     * Return the Scale.
     */
    private JTextField getScale() {
        if (scale == null) {
            scale = new JTextField("1");
            scale.setBounds(11, 365, 88, 23);
        }
        ;
        return scale;
    }

    /**
     * Return the ScrollPane.
     */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setBackground(java.awt.Color.lightGray);
            scrollPane.getViewport().add(getPlace(), null);
        }
        ;
        return scrollPane;
    }

    /**
     * Return the SetScaleButton.
     */
    private JButton getSetScaleButton() {
        if (setScaleButton == null) {
            setScaleButton = new JButton("Set scale");
            setScaleButton.setBounds(111, 365, 89, 23);
        }
        ;
        return setScaleButton;
    }

    /**
     * Return the WorkButton.
     */
    private JButton getWorkButton() {
        if (workButton == null) {
            workButton = new JButton("Work");
            workButton.setBounds(411, 365, 89, 23);
        }
        ;
        return workButton;
    }

    /**
     * Initializes the applet.
     */
    public void init() {
        super.init();
        //	setSize(615, 433);
        getContentPane().setLayout(new CountLayout(505, 440));
        //first button line
        getContentPane().add(getAddGroupButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(5, 120), new LinearConstraint(-60, 1.0, 25, 0.0)));
        getContentPane().add(getAddWindowButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(130, 120), new LinearConstraint(-60, 1.0, 25, 0.0)));
        getContentPane().add(getWorkButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(255, 120), new LinearConstraint(-60, 1.0, 25, 0.0)));
        getContentPane().add(getDeleteButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(380, 120), new LinearConstraint(-60, 1.0, 25, 0.0)));
        //second button line
        getContentPane().add(getScale(), new SimpleBoundsConstraint(
                new ConstantConstraint(5, 120), new LinearConstraint(-30, 1.0, 25, 0.0)));
        getContentPane().add(getPrintButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(130, 120), new LinearConstraint(-30, 1.0, 25, 0.0)));
        getContentPane().add(getSaveButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(255, 120), new LinearConstraint(-30, 1.0, 25, 0.0)));
        getContentPane().add(getLoadButton(), new SimpleBoundsConstraint(
                new ConstantConstraint(380, 120), new LinearConstraint(-30, 1.0, 25, 0.0)));
        //put paint place
        getContentPane().add(getScrollPane(), new SimpleBoundsConstraint(
                new LinearConstraint(5, 0.0, -10, 1.0), new LinearConstraint(5, 0.0, -70, 1.0)));
        //set listeners
        getScale().addKeyListener(this);
        try {
            getSetScaleButton().addActionListener(new ActionAdapter(this, "setScaleButtonAction"));
            getAddGroupButton().addActionListener(new ActionAdapter(this, "addGroupButtonAction"));
            getAddWindowButton().addActionListener(new ActionAdapter(this, "addWindowButtonAction"));
            getWorkButton().addActionListener(new ActionAdapter(this, "workButtonAction"));
            getDeleteButton().addActionListener(new ActionAdapter(this, "deleteButtonAction"));
            getSaveButton().addActionListener(new ActionAdapter(this, "saveButtonAction"));
            getLoadButton().addActionListener(new ActionAdapter(this, "loadButtonAction"));
            getPrintButton().addActionListener(new ActionAdapter(this, "printButtonAction"));
        } catch (NoSuchMethodException x) {
        }
    }

    public void keyPressed(KeyEvent e) {
        try {
            if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER)
                getPlace().setScale(new Float(getScale().getText()).floatValue());
            getScrollPane().doLayout();
            getPlace().repaint();
        } catch (IllegalArgumentException ex) {
            // Quietly ignore.
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    /**
     * Loads the schema from a file.
     */
    public void loadButtonAction() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load scheme");
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                getPlace().loadFromFile(f.getPath());
                getScale().setText(new Float(getPlace().getDesktop().getScale()).toString());
                setScaleButtonAction();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Starts the TryApp as application.
     */
    public static void main(java.lang.String[] args) {
        try {
            String clsName = "cz.green.eventtool.TryApp";
            ClassLoader clsLoader = Class.forName(clsName).getClassLoader();
            TryApp app = (cz.green.eventtool.TryApp) java.beans.Beans.instantiate(clsLoader, clsName);
            JDialog dialog = new JDialog((Frame) null, "Try application - stage 2", true);
            dialog.addWindowListener(new WindowCloser(dialog, true));
            dialog.getContentPane().add("Center", app);
            dialog.setSize(513, 350);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);;
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of java.applet.Applet");
            exception.printStackTrace(System.out);
        }
    }

    public void printButtonAction() {
        PrintPreviewDialog d = new PrintPreviewDialog(null, "ER schema");
        d.setDesktop(getPlace().getDesktop(), getPlace().getFont());
        if (d.selectPrintJob())
            d.setVisible(true);;
    }

    /**
     * Saves the schema to a file.
     */
    public void saveButtonAction() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save scheme");
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                java.io.File f = chooser.getSelectedFile();
                getPlace().saveToFile(f.getPath());
            }
        } catch (java.io.IOException e) {
            System.out.println(e.toString());
        }
    }

    /**
     * Sets the new label for the active button.
     */
    public void setActive(JButton button) {
        if (activeButton != null)
            activeButton.setText(activeLabel);
        activeLabel = (activeButton = button).getText();
        if (activeLabel.equals("Add group")) {
            activeButton.setText("Adding group");
            return;
        }
        if (activeLabel.equals("Add window")) {
            activeButton.setText("Adding window");
            return;
        }
        if (activeLabel.equals("Work")) {
            activeButton.setText("Working");
            return;
        }
        if (activeLabel.equals("Delete")) {
            activeButton.setText("Deleting");
            return;
        }
    }

    public void setScaleButtonAction() {
        getPlace().setScale(new Float(getScale().getText()).floatValue());
        getScrollPane().doLayout();
        getPlace().repaint();
    }

    public void workButtonAction() {
		if (getPlace().working())
			setActive(getWorkButton());
  }  
}
