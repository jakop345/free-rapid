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
    private JGraph graphComponent;

    public ContentArea() {
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        graphComponent = new JGraph();
        this.add(new JScrollPane(graphComponent), BorderLayout.CENTER);
    }


    public JGraph getGraphComponent() {
        return graphComponent;
    }

    public void freeUpResources() {

    }

    public boolean isModified() {
        //  return modified;
        return true;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
