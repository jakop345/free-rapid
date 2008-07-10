package cz.felk.cvut.erm.typeseditor;

import cz.felk.cvut.erm.datatype.*;
import cz.felk.cvut.erm.sql.TabCreator;

import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class UserTypeStorageVector {

    public static final String ADD_PROPERTYCHANGE = "TYPE_ADD";
    public static final String REMOVE_PROPERTYCHANGE = "TYPE_REMOVE";
    public static final String ADD_TO_PROPERTYCHANGE = "TYPE_ADD_TO";
    public static final int DIRECT = 1;
    public static final int INDIRECT = 0;

    protected Vector<UserTypeStorage> userTypeStorageVector = null;
    protected transient PropertyChangeSupport propertyChange = null;

    public UserTypeStorageVector() {
        userTypeStorageVector = new Vector<UserTypeStorage>(0);
    }

    public PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        //System.out.println("addPropertyChangeListener()");
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * adds new UserTypeStorage object at the end of userTypeStorageVector and
     * automatically increases its size
     */
    public void addType(UserTypeStorage s) {
        userTypeStorageVector.addElement(s);
        //System.out.println("Type added to "+userTypeStorageVector.size());
        getPropertyChange().firePropertyChange(ADD_PROPERTYCHANGE, null, s);
    }

    /**
     * deletes UserTypeStorage object from userTypeStorageVector from specified position
     * and automatically decreases its size
     */
    public void removeTypeAt(int index) {
        UserTypeStorage u = getTypeAt(index);
        userTypeStorageVector.removeElementAt(index);
        getPropertyChange().firePropertyChange(REMOVE_PROPERTYCHANGE, u, null);
    }

    /**
     * removes all UserTypeStorage objects from this vector
     */
    public void removeAllTypes() {
        userTypeStorageVector.removeAllElements();
    }

    /**
     * returns UserTypeStorage object from the specified position
     */
    public UserTypeStorage getTypeAt(int index) {
        //System.out.println("Size: "+userTypeStorageVector.size());
        //System.out.println("Index requested: "+index);
        return (userTypeStorageVector.get(index));
    }

//    /**
//     * adds UserTypeStorage object to userTypeStorageVector at the specified position
//     */
//    public void addTypeAt(UserTypeStorage s, int index) {
//        userTypeStorageVector.add(index, s);
//        getPropertyChange().firePropertyChange(ADD_TO_PROPERTYCHANGE, null, s);
//    }

    public Vector<UserTypeStorage> getUserTypeStorageVector() {
        return (Vector<UserTypeStorage>) Collections.unmodifiableCollection(userTypeStorageVector);
    }

    public Enumeration<UserTypeStorage> elements() {
        return userTypeStorageVector.elements();
    }

    /**
     * reorders elements in userTypeStorageVector in order to be ready for
     * generating SQL commands
     * It divides elements into two groups - first group is for direct creating
     * and the second is for creating using incomplete type declaration
     */
    public Enumeration<UserTypeStorage> elementsForCreating(int which) {
        Vector<UserTypeStorage> vector = new Vector<UserTypeStorage>(0);
        Vector<UserTypeStorage> vectorIncomplete = new Vector<UserTypeStorage>(0);
        Vector<String> names = new Vector<String>(0);
        boolean changed = true;

        //System.out.println("entering elementsForDirectCreating");
        for (Enumeration<UserTypeStorage> e = elements(); e.hasMoreElements();) {
            UserTypeStorage uts = e.nextElement();
            DataType dt = uts.getDataType();
            //System.out.println("getting next element");
            if (!((dt instanceof VarrayDataType) || (dt instanceof NestedTableDataType) || (dt instanceof ObjectDataType))) {
                vector.addElement(uts);
                names.addElement(uts.getTypeName());
            } else if (dt instanceof NestedTableDataType) {
                if (!(((NestedTableDataType) dt).getType() instanceof UserDefinedDataType)) {
                    vector.addElement(uts);
                    names.addElement(uts.getTypeName());
                }
            } else if (dt instanceof VarrayDataType) {
                if (!(((VarrayDataType) dt).getType() instanceof UserDefinedDataType)) {
                    vector.addElement(uts);
                    names.addElement(uts.getTypeName());
                }
            }
            if (dt instanceof ObjectDataType) {
                int i = 0;
                boolean dirty = false;
                UserTypeStorage item;
                while (((item = ((ObjectDataType) dt).getItemAt(i++)) != null) && (!dirty)) {
                    dirty = (item.getDataType() instanceof UserDefinedDataType);
                    //System.out.println("cyklujeme");
                }
                if (!dirty) {
                    vector.addElement(uts);
                    names.addElement(uts.getTypeName());
                }
            }
        }
        while (changed) {
            changed = false;
            for (Enumeration<UserTypeStorage> e = elements(); e.hasMoreElements();) {
                UserTypeStorage uts;
                do {
                    uts = e.nextElement();
                } while (e.hasMoreElements() && names.contains(uts.getTypeName()));
                if (!names.contains(uts.getTypeName())) {
                    DataType dt = uts.getDataType();
                    if (dt instanceof NestedTableDataType) {
                        if (names.contains(((NestedTableDataType) dt).getType().toString())) {
                            vector.addElement(uts);
                            names.addElement(uts.getTypeName());
                            changed = true;
                        }
                    } else if (dt instanceof VarrayDataType) {
                        if (names.contains(((VarrayDataType) dt).getType().toString())) {
                            vector.addElement(uts);
                            names.addElement(uts.getTypeName());
                            changed = true;
                        }
                    } else if (dt instanceof ObjectDataType) {
                        int i = 0;
                        boolean dirty = false;
                        UserTypeStorage item;
                        while (((item = ((ObjectDataType) dt).getItemAt(i++)) != null) && (!dirty)) {
                            dirty = (!names.contains(item.getDataType().toString()));
                            //System.out.println("cyklujeme");
                        }
                        if (!dirty) {
                            vector.addElement(uts);
                            names.addElement(uts.getTypeName());
                            changed = true;
                        }
                    }
                }
            }
        }
        for (Enumeration<UserTypeStorage> e = elements(); e.hasMoreElements();) {
            UserTypeStorage uts = e.nextElement();
            if (!vector.contains(uts))
                vectorIncomplete.addElement(uts);
        }
        if (which == DIRECT)
            return vector.elements();
        else
            return vectorIncomplete.elements();
    }

    public int getSize() {
        return userTypeStorageVector.size();
    }

    public boolean nameAlreadyExists(String name) {
        for (int i = 0; i < getSize(); i++)
            if ((userTypeStorageVector.get(i)).getTypeName().equals(name))
                return true;
        return false;
    }

    /**
     * returns string representation of data types stored in userTypeStorageVector
     * It will be used for generating SQL script
     */
    public String getTypes() {
        int i;
        String list = "";

        for (i = 0; i < getSize(); i++) {
            if (i == 0) {
                list = TabCreator.getTabs(1) + getTypeAt(i).getTypeName();
                list += " " + getTypeAt(i).getDataType().toString();
            } else {
                list = list + ",\n" + TabCreator.getTabs(1) + getTypeAt(i).getTypeName();
                list = list + " " + getTypeAt(i).getDataType().toString();
            }
        }
        return list;
    }

}