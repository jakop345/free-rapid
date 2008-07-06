package cz.felk.cvut.erm.conc2obj;

import cz.felk.cvut.erm.conc2obj.interfaces.SubObjProducer;
import cz.felk.cvut.erm.conc2obj.interfaces.SubTreeProducerObj;
import cz.felk.cvut.erm.conc2rela.SchemaC2R;
import cz.felk.cvut.erm.icontree.IconNode;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * SQL schema with SQL commands.
 */
public class SchemaObj implements SubObjProducer, SubTreeProducerObj {
    /**
     * Corresponding relational schema.
     *
     * @see cz.felk.cvut.erm.conc2rela.SchemaC2R
     */
    private SchemaC2R schemaC2R = null;
    /**
     * Commands for creating tables.
     */
    private Vector createCommands = new Vector();
    /**
     * Commands for altering tables.
     */
    private Vector alterAddCommands = new Vector();
    /**
     * Commands for dropping tables.
     */
    private Vector dropCommands = new Vector();
    /**
     * Commands for creating types
     */
    private Vector createTypes = new Vector();
    /**
     * Commands for creating incomplete types
     */
    private Vector createIncompleteTypes = new Vector();
    /**
     * Commands for dropping types
     */
    private Vector dropTypes = new Vector();
    /**
     * Commands for object types
     */
    private Vector createCommandTypesObj = new Vector();

    private Vector alterReferenceTypes = new Vector();

    private Intro intro = null;

    /**
     * Constructor.
     */
    public SchemaObj() {
    }

    /**
     * Constructor.
     *
     * @param aSchemaC2R Corresponding relational schema to SQL schema
     */
    public SchemaObj(SchemaC2R aSchemaC2R) {
        schemaC2R = aSchemaC2R;
    }

    /**
     * Adds alter add command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addAlterAddCommandObj(AlterAddCommandObj aAlterAddCommand) {
        if (getAlterAddCommands().contains(aAlterAddCommand)) {
//		throw AlreadyContainsExceptionSQL();
        }
        //if (!aAlterAddCommand.isEmpty())
        getAlterAddCommands().addElement(aAlterAddCommand);
    }

    /**
     * Adds create table command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addCreateCommandObj(CreateCommandObj aCreateCommand) {
        if (getCreateCommands().contains(aCreateCommand)) {
//		throw AlreadyContainsExceptionSQL();
        }
        //if (!aCreateCommand.isEmpty()) opraveno 1.7.
        getCreateCommands().addElement(aCreateCommand);
    }

    /**
     * Adds drop table command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addDropCommandObj(DropCommandObj aDropCommand) {
        if (getDropCommands().contains(aDropCommand)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getDropCommands().addElement(aDropCommand);
    }

    /**
     * Adds create type command.
     *
     * @param aCreateType cz.omnicom.ermodeller.sql.CreateTypeSQL
     */
    public void addCreateTypeObj(CreateTypeObj aCreateType) {
        if (getCreateTypes().contains(aCreateType)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getCreateTypes().addElement(aCreateType);
    }

    public void setIntro(Intro i) {
        intro = i;
    }

    /**
     * Adds create incomplete type command.
     *
     * @param aCreateType cz.omnicom.ermodeller.sql.CreateTypeSQL
     */
    public void addCreateIncompleteTypeObj(CreateIncompleteTypeObj aCreateType) {
        if (getCreateIncompleteTypes().contains(aCreateType)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getCreateIncompleteTypes().addElement(aCreateType);
    }

    /**
     * Adds drop type command.
     *
     * @param aDropType cz.omnicom.ermodeller.sql.DropTypeSQL
     */
    public void addDropTypeObj(DropTypeObj aDropType) {
        if (getDropTypes().contains(aDropType)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getDropTypes().addElement(aDropType);
    }

    /**
     * Adds type command.
     *
     * @param aCreateCommandTypeObj cz.omnicom.ermodeller.sql.CreateCommandTypeObj
     */
    public void addCommandTypeObj(CreateCommandTypeObj aCreateCommandTypeObj) {
        if (getCommandTypesObj().contains(aCreateCommandTypeObj)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getCommandTypesObj().addElement(aCreateCommandTypeObj);
    }

    /**
     * Adds reference command.
     *
     * @param aColumnReferenceObj cz.omnicom.ermodeller.sql.AlterReferenceType
     */
    public void addReferenceType(AlterReferenceType aColumnReferenceObj) {
        if (getReferenceTypesObj().contains(aColumnReferenceObj)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getReferenceTypesObj().addElement(aColumnReferenceObj);
    }

    /**
     * Creates string representation of the SQL schema.
     *
     * @return java.lang.String
     * @see #createSubSQL
     */
    public String createSQL() {
        return createSubSQL(0);
    }

    /**
     * Creates string representation of the SQL schema left inteded <code>countTabs</code> from the left.
     * It is group of commands.
     *
     * @param countTabs number of intendations from left
     * @return java.lang.String
     */
    public String createSubSQL(int countTabs) {
        String result = "";

        result += intro.toString() + "\n";
        for (Enumeration elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandObj commandSQL = (DropCommandObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeObj commandSQL = (DropTypeObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeObj commandSQL = (CreateIncompleteTypeObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeObj commandSQL = (CreateTypeObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getCommandTypesObj().elements(); elements.hasMoreElements();) {
            CreateCommandTypeObj commandSQL = (CreateCommandTypeObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getReferenceTypesObj().elements(); elements.hasMoreElements();) {
            AlterReferenceType commandSQL = (AlterReferenceType) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandObj commandSQL = (CreateCommandObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandObj commandSQL = (AlterAddCommandObj) elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n";
        }
        return result;
    }

    /**
     * Creates subtree representing the SQL schema.
     *
     * @return cz.omnicom.ermodeller.errorlog.icontree.IconNode
     */
    public IconNode createSubTree() {
        IconNode top = new IconNode(this, true, getIcon());
        intro = new Intro();
        top.add(intro.createSubTree());
        for (Enumeration elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandObj commandObj = (DropCommandObj) elements.nextElement();
            top.add(commandObj.createSubTree());
        }
        for (Enumeration elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeObj commandObj = (DropTypeObj) elements.nextElement();
            top.add(commandObj.createSubTree());
        }
        for (Enumeration elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeObj commandSQL = (CreateIncompleteTypeObj) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeObj commandSQL = (CreateTypeObj) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getCommandTypesObj().elements(); elements.hasMoreElements();) {
            CreateCommandTypeObj commandSQL = (CreateCommandTypeObj) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getReferenceTypesObj().elements(); elements.hasMoreElements();) {
            AlterReferenceType commandSQL = (AlterReferenceType) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandObj commandSQL = (CreateCommandObj) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandObj commandSQL = (AlterAddCommandObj) elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        return top;
    }

    /**
     * Creates tree representing the whole SQL schema.
     *
     * @return DefaultMutableTreeNode
     * @see #createSubTree
     */
    public IconNode createTree() {
        return createSubTree();
    }

    /**
     * Returns alter table commands.
     *
     * @return java.util.Vector
     */
    protected Vector getAlterAddCommands() {
        if (alterAddCommands == null)
            alterAddCommands = new Vector();
        return alterAddCommands;
    }

    /**
     * Return create table commands.
     *
     * @return java.util.Vector
     */
    protected Vector getCreateCommands() {
        if (createCommands == null)
            createCommands = new Vector();
        return createCommands;
    }

    /**
     * Returns drop commands.
     *
     * @return java.util.Vector
     */
    protected Vector getDropCommands() {
        if (dropCommands == null)
            dropCommands = new Vector();
        return dropCommands;
    }

    /**
     * Returns create type commands.
     *
     * @return java.util.Vector
     */
    protected Vector getCreateTypes() {
        if (createTypes == null)
            createTypes = new Vector();
        return createTypes;
    }

    /**
     * Returns create incomplete type commands.
     *
     * @return java.util.Vector
     */
    protected Vector getCreateIncompleteTypes() {
        if (createIncompleteTypes == null)
            createIncompleteTypes = new Vector();
        return createIncompleteTypes;
    }

    /**
     * Returns drop type commands.
     *
     * @return java.util.Vector
     */
    protected Vector getDropTypes() {
        if (dropTypes == null)
            dropTypes = new Vector();
        return dropTypes;
    }

    /**
     * Returns type commands.
     *
     * @return java.util.Vector
     */
    protected Vector getCommandTypesObj() {
        if (createCommandTypesObj == null)
            createCommandTypesObj = new Vector();
        return createCommandTypesObj;
    }

    /**
     * Returns reference commands.
     *
     * @return java.util.Vector
     */
    protected Vector getReferenceTypesObj() {
        if (alterReferenceTypes == null)
            alterReferenceTypes = new Vector();
        return alterReferenceTypes;
    }

    protected Intro getIntro() {
        if (intro == null) {
            intro = new Intro();
        }
        return intro;
    }

    /**
     * Returns icon for representing the schema in the SQL tree.
     *
     * @return javax.swing.Icon
     */
    public Icon getIcon() {
        return new ImageIcon(ClassLoader.getSystemResource("img/sqlschema.gif"));
    }

    /**
     * Returns name of the schema or null if created from default constructor.
     *
     * @return java.lang.String
     */
    public String getName() {
        return (schemaC2R == null) ? "Schema" : schemaC2R.getNameC2R().toString();
    }

    /**
     * Returns title of the schema.
     *
     * @return java.lang.String
     */
    public String toString() {
        return "Object relation commands in the schema";//schemaC2R.getNameC2R();
    }
}