//Steve
package cz.felk.cvut.erm.datatype;

import cz.felk.cvut.erm.datatype.editor.DataTypePanel;
import cz.felk.cvut.erm.datatype.editor.UserDefinedDataTypePanel;

/**
 * User defined datatype.
 */
public class UserDefinedDataType extends DataType {

    /**
     * name of the type
     */
    private String typeName = null;

    /**
     * name of the type used for creating the type.
     * Only for setting correct item name in combo box in panel
     */
    private String tempName = null;

    /**
     * panel for customizing
     */
    private UserDefinedDataTypePanel panel = null;

    /**
     * Constructor
     */
    public UserDefinedDataType() {
        super();
//        if (DataTypeManager.getTypeNames().size() > 0)
//            typeName = DataTypeManager.getTypeNames().elementAt(0);
//        else
        typeName = "";
        //System.out.println("user data type constructor");
    }

    /**
     * Constructor - sets the typeName to name
     */
    public UserDefinedDataType(String name) {
        super();
        tempName = name;
        typeName = name;
    }

    /**
     * sets typeName to name
     */
    public void setName(String name) {
        typeName = name;
    }

    /**
     * returns panel for customizing the type
     */
    public DataTypePanel getPanel() {
        if (panel == null) {
            panel = new UserDefinedDataTypePanel();
            panel.setUserDefinedDataType(this);
            panel.setEnabled(true);
            if (tempName != null)
                panel.getJComboBox().setSelectedItem(tempName);
        }
        /*
      if(DataType.getTypeNamesChanged())
        panel.getRefreshButton().doClick();
      */
        //System.out.println("konec getPanel()");
        return panel;
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return typeName;
    }

    /**
     * Returns string representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return typeName;
    }
}