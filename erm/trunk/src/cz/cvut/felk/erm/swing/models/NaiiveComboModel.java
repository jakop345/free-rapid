package cz.cvut.felk.erm.swing.models;

import javax.swing.*;

/**
 * Model podporujici '-' jako separator mezi polozkami Item '-' nelze vybrat.
 * @author Ladislav Vitasek
 */
public class NaiiveComboModel extends DefaultComboBoxModel {
    public NaiiveComboModel() {
        super();
    }

    public NaiiveComboModel(Object items[]) {
        super(items);
    }

    public void setSelectedItem(Object o) {
        //Object currentItem = getSelectedItem();
        if (!"-".equals(o)) {
            super.setSelectedItem(o);
        }
    }
}
