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
public class SchemaObjSQL implements SubObjProducer, SubTreeProducerObj {
    /**
     * Corresponding relational schema.
     *
     * @see cz.felk.cvut.erm.conc2rela.SchemaC2R
     */
    private SchemaC2R schemaC2R = null;
    /**
     * Commands for creating tables.
     */
    private Vector<CreateCommandObj> createCommands = new Vector<CreateCommandObj>();
    /**
     * Commands for altering tables.
     */
    private Vector<AlterAddCommandObj> alterAddCommands = new Vector<AlterAddCommandObj>();
    /**
     * Commands for dropping tables.
     */
    private Vector<DropCommandObj> dropCommands = new Vector<DropCommandObj>();
    /**
     * Commands for creating types
     */
    private Vector<CreateTypeObj> createTypes = new Vector<CreateTypeObj>();
    /**
     * Commands for creating incomplete types
     */
    private Vector<CreateIncompleteTypeObj> createIncompleteTypes = new Vector<CreateIncompleteTypeObj>();
    /**
     * Commands for dropping types
     */
    private Vector<DropTypeObj> dropTypes = new Vector<DropTypeObj>();
    /**
     * Commands for object types
     */
    private Vector<CreateCommandTypeObj> createCommandTypesObj = new Vector<CreateCommandTypeObj>();

    private Vector<AlterReferenceType> alterReferenceTypes = new Vector<AlterReferenceType>();

    private Intro intro = null;

    /**
     * Constructor.
     */
    public SchemaObjSQL() {
    }

    /**
     * Constructor.
     *
     * @param aSchemaC2R Corresponding relational schema to SQL schema
     */
    public SchemaObjSQL(SchemaC2R aSchemaC2R) {
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
        for (Enumeration<DropCommandObj> elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<DropTypeObj> elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateIncompleteTypeObj> elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateTypeObj> elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateCommandTypeObj> elements = getCommandTypesObj().elements(); elements.hasMoreElements();) {
            CreateCommandTypeObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<AlterReferenceType> elements = getReferenceTypesObj().elements(); elements.hasMoreElements();) {
            AlterReferenceType commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateCommandObj> elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandObj commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<AlterAddCommandObj> elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandObj commandSQL = elements.nextElement();
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
        for (Enumeration<DropCommandObj> elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandObj commandObj = elements.nextElement();
            top.add(commandObj.createSubTree());
        }
        for (Enumeration<DropTypeObj> elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeObj commandObj = elements.nextElement();
            top.add(commandObj.createSubTree());
        }
        for (Enumeration<CreateIncompleteTypeObj> elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeObj commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateTypeObj> elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeObj commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateCommandTypeObj> elements = getCommandTypesObj().elements(); elements.hasMoreElements();) {
            CreateCommandTypeObj commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<AlterReferenceType> elements = getReferenceTypesObj().elements(); elements.hasMoreElements();) {
            AlterReferenceType commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateCommandObj> elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandObj commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<AlterAddCommandObj> elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandObj commandSQL = elements.nextElement();
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
    protected Vector<AlterAddCommandObj> getAlterAddCommands() {
        if (alterAddCommands == null)
            alterAddCommands = new Vector<AlterAddCommandObj>();
        return alterAddCommands;
    }

    /**
     * Return create table commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateCommandObj> getCreateCommands() {
        if (createCommands == null)
            createCommands = new Vector<CreateCommandObj>();
        return createCommands;
    }

    /**
     * Returns drop commands.
     *
     * @return java.util.Vector
     */
    protected Vector<DropCommandObj> getDropCommands() {
        if (dropCommands == null)
            dropCommands = new Vector<DropCommandObj>();
        return dropCommands;
    }

    /**
     * Returns create type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateTypeObj> getCreateTypes() {
        if (createTypes == null)
            createTypes = new Vector<CreateTypeObj>();
        return createTypes;
    }

    /**
     * Returns create incomplete type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateIncompleteTypeObj> getCreateIncompleteTypes() {
        if (createIncompleteTypes == null)
            createIncompleteTypes = new Vector<CreateIncompleteTypeObj>();
        return createIncompleteTypes;
    }

    /**
     * Returns drop type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<DropTypeObj> getDropTypes() {
        if (dropTypes == null)
            dropTypes = new Vector<DropTypeObj>();
        return dropTypes;
    }

    /**
     * Returns type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateCommandTypeObj> getCommandTypesObj() {
        if (createCommandTypesObj == null)
            createCommandTypesObj = new Vector<CreateCommandTypeObj>();
        return createCommandTypesObj;
    }

    /**
     * Returns reference commands.
     *
     * @return java.util.Vector
     */
    protected Vector<AlterReferenceType> getReferenceTypesObj() {
        if (alterReferenceTypes == null)
            alterReferenceTypes = new Vector<AlterReferenceType>();
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