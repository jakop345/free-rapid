package cz.cvut.felk.erm.gui.managers;

import javax.swing.*;

/**
 * @author Ladislav Vitasek
 */
public class RiderArea extends JPanel {
    public static final String MODIFIED_PROPERTY = "modifiedProperty";
    private boolean modified = false;

    public RiderArea() {
        initComponents();
    }

    private void initComponents() {
        this.add(new JLabel("Tady se bude editovat"));            
    }

    public void freeUpResources() {

    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
