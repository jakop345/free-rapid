package cz.felk.cvut.erm.datatype;

import cz.felk.cvut.erm.datatype.editor.DataTypePanel;
import cz.felk.cvut.erm.typeseditor.UserTypeStorage;
import cz.felk.cvut.erm.typeseditor.UserTypeStorageVector;

import java.util.Enumeration;
import java.util.Vector;

public class ObjectDataType extends DataType {

    /**
     * items in this object
     */
    protected UserTypeStorageVector itemVector = null;
    /**
     * all user defined types
     */
    protected UserTypeStorageVector userDefinedTypesVector = null;

    /**
     * tells whether this object is being tested for containing nested table
     * or varray data type
     */
    private boolean checked = false;
    /**
     * tels whether this object is in process of returning nested table names
     */
    private boolean namesChecked = false;

    /**
     * Constructor
     */

    public ObjectDataType() {
        super();
        itemVector = new UserTypeStorageVector();
    }

    public ObjectDataType(UserTypeStorageVector v) {
        super();
        itemVector = new UserTypeStorageVector();
        userDefinedTypesVector = v;
    }

    public UserTypeStorage getItemAt(int index) {
        if (index >= itemVector.getSize())
            return null;
        else
            return itemVector.getTypeAt(index);
    }

    public void addItem(UserTypeStorage u) {
        itemVector.addType(u);
    }

    public void removeItemAt(int index) {
        itemVector.removeTypeAt(index);
    }

    public UserTypeStorageVector getItemVector() {
        return itemVector;
    }

    public void setUserDefinedTypesVector(UserTypeStorageVector v) {
        userDefinedTypesVector = v;
    }

    public DataTypePanel getPanel() {
        return null;
    }

    public boolean hasItems() {
        return itemVector.getSize() > 0;
    }

    private DataType getDataType(String name) {
        UserTypeStorage s;
        DataType dt = null;

        //System.out.println("name je "+name);
        for (Enumeration e = userDefinedTypesVector.elements(); e.hasMoreElements();) {
            s = (UserTypeStorage) e.nextElement();
            //System.out.println("type name je "+s.getTypeName());
            if (s.getTypeName().equals(name))
                dt = s.getDataType();
        }
        //System.out.println("data type je " +dt);
        return dt;
    }

    public boolean containsVarray() {
        String name;
        boolean result = false;

        if (!checked) {
            checked = true;
            for (Enumeration e = itemVector.elements(); e.hasMoreElements();) {
                name = ((UserTypeStorage) e.nextElement()).getDataType().toString();
                if (getDataType(name) instanceof VarrayDataType) {
                    checked = false;
                    //System.out.println("object obsahuje varray true");
                    return true; //we can stop searching now
                } else if (getDataType(name) instanceof ObjectDataType)
                    result = result || ((ObjectDataType) getDataType(name)).containsVarray();
            }
        }
        checked = false;
        //System.out.println("object obsahuje varray? "+result);
        return result;
    }

    public boolean containsNested() {
        String name;
        boolean result = false;

        if (!checked) {
            checked = true;
            for (Enumeration e = itemVector.elements(); e.hasMoreElements();) {
                name = ((UserTypeStorage) e.nextElement()).getDataType().toString();
                if (getDataType(name) instanceof NestedTableDataType) {
                    checked = false;
                    //System.out.println("object obsahuje nested true");
                    return true; //we can stop searching now
                } else if (getDataType(name) instanceof ObjectDataType)
                    result = result || ((ObjectDataType) getDataType(name)).containsNested();
            }
        }
        checked = false;
        //System.out.println("object obsahuje nested? "+result);
        return result;
    }

    /**
     * returns names of all neted table data type items in this object
     */
    public Vector<String> getNestedNames() {
        Vector<String> result = new Vector<String>(0);
        String dataType;
        if (!namesChecked) {
            namesChecked = true;
            for (Enumeration e = itemVector.elements(); e.hasMoreElements();) {
                UserTypeStorage u = (UserTypeStorage) e.nextElement();
                dataType = u.getDataType().toString();
                if (getDataType(dataType) instanceof NestedTableDataType)
                    result.addElement(u.getTypeName());
                if (getDataType(dataType) instanceof ObjectDataType) {
                    Vector<String> temp = ((ObjectDataType) getDataType(dataType)).getNestedNames();
                    for (Enumeration<String> enu = temp.elements(); enu.hasMoreElements();)
                        result.addElement(enu.nextElement());
                }
            }
        }
        namesChecked = false;
        return result;
    }

    /**
     * String representation of the datatype in property list.
     *
     * @return java.lang.String
     */
    public String toDescriptionString() {
        return "Object";
    }

    /**
     * String representation of the datatype.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Object";
        //return "Object (" +"\n"+ itemVector.getTypes() +"\n)";
    }

}