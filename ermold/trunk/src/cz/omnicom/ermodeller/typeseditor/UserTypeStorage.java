package cz.omnicom.ermodeller.typeseditor;

import cz.omnicom.ermodeller.datatype.DataType;
import cz.omnicom.ermodeller.datatype.ObjectDataType;

import java.util.Enumeration;

public class UserTypeStorage {

    protected String typeName = null;
    protected DataType dataType = null;
    protected UserTypesEditorPanel panel = null;

    public UserTypeStorage(String name, DataType type, UserTypesEditorPanel aPanel) {
        typeName = name;
        dataType = type;
        panel = aPanel;
    }

    public String getTypeName() {
        return typeName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public UserTypesEditorPanel getPanel() {
        return panel;
    }

    public void setTypeName(String name) {
        typeName = name;
    }

    public void setDataType(DataType type) {
        dataType = type;
    }

    public void setPanel(UserTypesEditorPanel aPanel) {
        panel = aPanel;
    }

    public void write(java.io.PrintWriter pw) {
        pw.println("\t<usertype>");
        pw.print("\t\t<typename>");
        pw.print(getTypeName());
        pw.println("</typename>");
        pw.println("\t\t<datatypedef>");
        pw.print("\t\t\t<datatype>");
        pw.print(getDataType().toString());
        pw.println("</datatype>");
        if (getDataType() instanceof ObjectDataType) {
            UserTypeStorageVector itemVector = ((ObjectDataType) getDataType()).getItemVector();
            for (Enumeration elements = itemVector.elements(); elements.hasMoreElements();) {
                UserTypeStorage u = (UserTypeStorage) elements.nextElement();
                pw.println("\t\t\t<item>");
                pw.print("\t\t\t\t<itemname>");
                pw.print(u.getTypeName());
                pw.println("</itemname>");
                pw.print("\t\t\t\t<datatype>");
                pw.print(u.getDataType());
                pw.println("</datatype>");
                pw.println("\t\t\t</item>");
            }
        }
        pw.println("\t\t</datatypedef>");
        pw.println("\t</usertype>");
    }
}