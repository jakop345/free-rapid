package cz.cvut.felk.erm.gui.managers;

import org.jgraph.JGraph;

import javax.swing.*;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public class ContentArea extends JPanel {
    public static final String MODIFIED_PROPERTY = "modifiedProperty";
    private boolean modified = false;

    public ContentArea() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(new JGraph()), BorderLayout.CENTER);
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
