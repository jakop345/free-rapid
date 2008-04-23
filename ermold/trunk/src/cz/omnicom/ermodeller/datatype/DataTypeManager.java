package cz.omnicom.ermodeller.datatype;

import java.util.Vector;

/**
 * @author Ladislav Vitasek
 */
public class DataTypeManager {
    /**
     * list of available user datatypes
     */
    protected final Vector<String> typeNames = new Vector<String>(0);
    protected boolean typeNamesChanged = false;
    /**
     * list of nested tables already created
     */
    protected final Vector<String> nestedNames = new Vector<String>(0);
    /**
     * list of varrays already created
     */
    protected final Vector<String> varrayNames = new Vector<String>(0);
    /**
     * list of objects already created
     */
    protected final Vector<String> objectNames = new Vector<String>(0);

    private static DataTypeManager instance = null;

    public DataTypeManager() {
        instance = this;
    }

    public static DataTypeManager getInstance() {
        return instance;
    }

    public void addToVarrayNames(String name) {
        varrayNames.addElement(name);
    }

    public void addToNestedNames(String name) {
        nestedNames.addElement(name);
    }

    public void addToObjectNames(String name) {
        objectNames.addElement(name);
    }

    public void removeAllFromNestedNames() {
        nestedNames.removeAllElements();
    }

    public void removeAllFromVarrayNames() {
        varrayNames.removeAllElements();
    }

    public void removeAllFromObjectNames() {
        objectNames.removeAllElements();
    }

    public void removeFromVarrayNames(String name) {
        varrayNames.remove(name);
    }

    public void removeFromNestedNames(String name) {
        nestedNames.remove(name);
    }

    public void removeFromObjectNames(String name) {
        objectNames.remove(name);
    }

    public boolean isInNestedNames(String name) {
        //System.out.println("is in nested names...");
        return nestedNames.contains(name);
    }

    public boolean isInVarrayNames(String name) {
        //System.out.println("is in varray names...");
        return varrayNames.contains(name);
    }

    public boolean isInObjectNames(String name) {
        //System.out.println("is in object names...");
        return objectNames.contains(name);
    }

    public Vector<String> getTypeNames() {
        return typeNames;
    }

    public void addToTypeNames(String name) {
        typeNames.addElement(name);
        typeNamesChanged = true;
    }

    public void addToTypeNamesAt(String name, int index) {
        typeNames.add(index, name);
        typeNamesChanged = true;
    }

    public void removeFromTypeNames(int index) {
        typeNames.removeElementAt(index);
        typeNamesChanged = true;
    }

    public void setTypeNamesChanged(boolean value) {
        typeNamesChanged = value;
    }

    public boolean getTypeNamesChanged() {
        return typeNamesChanged;
    }
}
