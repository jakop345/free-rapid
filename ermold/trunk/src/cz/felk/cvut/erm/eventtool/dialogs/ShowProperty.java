package cz.felk.cvut.erm.eventtool.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

/**
 * Can displey and sometimes also edit the properties values. The property values are presented in text field,
 * choice or by self painting by PropertyEditor. Editting ensure the text field, choice or custom editor of the
 * PropertyEditor.
 */
public class ShowProperty extends JPanel implements PropertyChangeListener, ActionListener, MouseListener, ItemListener, FocusListener, KeyListener {
    /**
     * The propertyEditor for the edited property
     */
    protected PropertyEditor editor = null;
    /**
     * The name of the edited property
     */
    protected String name = null;
    /**
     * The text field or the choice used to edit the value of the property
     */
    private Component customizer = null;
    protected Frame parent = null;

    /**
     * Determine which type of the PropertyEditor is <code>editor</code> and
     * according to the result places the right component and set this class
     * as listener to the right component and event.
     *
     * @param editor The PropertyEditor for editing the property.
     * @param name   The name of the edited property useful for showing CustomPropertyEditor.
     * @see CustomPropertyEditor
     */
    public ShowProperty(Frame parent, PropertyEditor editor, String name) {
        super();
        this.parent = parent;
        this.editor = editor;
        this.name = name;
        setLayout(new java.awt.BorderLayout());
        String text = editor.getAsText();
        String[] choices;
        Dimension d = getSize();
        if (!editor.isPaintable()) {
            if ((editor.supportsCustomEditor()) && (text != null)) {
                customizer = new JButton(text);
                ((JButton) customizer).setHorizontalAlignment(JButton.LEFT);
                //customizer.addMouseListener(this);
                ((JButton) customizer).addActionListener(this);
                ((JButton) customizer).setActionCommand("custom");
                customizer.setForeground(Color.black);
            } else if ((choices = editor.getTags()) != null) {
                JComboBox c = new JComboBox(choices);
                c.setSelectedIndex(0);
                c.setSelectedItem(text);
                c.addItemListener(this);
                customizer = c;
            } else if (text != null) {
                customizer = new JTextField(text);
                customizer.addKeyListener(this);
                customizer.addFocusListener(this);
            }
        }
        if ((customizer == null) && (editor.supportsCustomEditor())) {
            addMouseListener(this);
        }
        if (customizer != null) {
            customizer.setBounds(0, 0, d.width, d.height);
            add(customizer, "Center");
        }
        this.editor.addPropertyChangeListener(this);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (editor.supportsCustomEditor() && e.getActionCommand().equals("custom")) {
            JDialog dialog = new JDialog(parent, "Edit \"" + name + "\"", true);
            dialog.getContentPane().add("Center", editor.getCustomEditor());
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            //dialog.setVisible(true);;
            dialog.setVisible(true);
        }
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(java.awt.event.FocusEvent e) {
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(java.awt.event.FocusEvent e) {
        if (customizer instanceof JTextField)
            try {
                editor.setAsText(((JTextField) customizer).getText());
            } catch (IllegalArgumentException ex) {
                // Quietly ignore.
            }
    }

    /**
     * Invoked when the choice is used to edit property and user selectes other value of the choice.
     */
    public void itemStateChanged(ItemEvent evt) {
        editor.setAsText(((JComboBox) customizer).getSelectedItem().toString());
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * In this methods we are notified about pressing the Enter key. We try to
     * set the new value to the property editor.
     *
     * @see java.awt.event.KeyListener
     */
    public void keyReleased(KeyEvent e) {
        try {
            if (e.getKeyCode() == KeyEvent.VK_ENTER)
                editor.setAsText(((JTextField) customizer).getText());
        } catch (IllegalArgumentException ex) {
            // Quietly ignore.
        }
    }

    /**
     * Exists for implementing the interface KeyListener
     *
     * @see java.awt.event.KeyListener
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Invoked when user clickes at this component. Used when the PropertyEditor
     * support custom editor to diplay it.
     *
     * @see java.awt.event.MouseListener
     * @see CustomPropertyEditor
     */
    public void mouseClicked(MouseEvent evt) {
        if (editor.supportsCustomEditor()) {
            JDialog dialog = new JDialog(parent, "Edit \"" + name + "\"", true);
            dialog.getContentPane().add("Center", editor.getCustomEditor());
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            //dialog.setVisible(true);;
            dialog.setVisible(true);
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
     * Exists for implementing the interface MouseListener
     *
     * @see java.awt.event.MouseListener
     */
    public void mousePressed(MouseEvent evt) {
    }

    /**
     * Exists for implementing the interface MouseListener
     *
     * @see java.awt.event.MouseListener
     */
    public void mouseReleased(MouseEvent evt) {
    }

    /**
     * If the editor support the self painting this methods invokes the
     * PropertyEditor <code>paintValue</code> method.
     *
     * @see java.beans.PropertyEditor#paintValue(java.awt.Graphics,java.awt.Rectangle)
     */
    public void paint(Graphics g) {
        super.paint(g);
        if (editor.isPaintable()) {
            Dimension d = getSize();
            Rectangle box = new Rectangle(0, 0, d.width, d.height);
            editor.paintValue(g, box);
        }
    }

    /**
     * Sets the new value of the changed property. This method is invoked when the value of a
     * property changes in its editor. Change the value of the property and rereads the values
     * of all properties to find out which changed to repaint it.
     *
     * @param e The event with the source of the event.
     */
    public void propertyChange() {
        if (editor.isPaintable()) {
            repaint();
            return;
        }
        if (customizer instanceof JTextField) {
            ((JTextField) customizer).setText(editor.getAsText());
            return;
        }
        if (customizer instanceof JLabel) {
            ((JLabel) customizer).setText(editor.getAsText());
            return;
        }
        if (customizer instanceof JButton) {
            ((JButton) customizer).setText(editor.getAsText());
            return;
        }
        if (customizer instanceof JComboBox) {
            ((JComboBox) customizer).setSelectedItem(editor.getAsText());
            customizer.repaint();
        }
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        propertyChange();
    }
}
