package cz.felk.cvut.erm.sql;

import cz.felk.cvut.erm.conc2rela.SchemaC2R;
import cz.felk.cvut.erm.icontree.IconNode;
import cz.felk.cvut.erm.sql.interfaces.SubSQLProducer;
import cz.felk.cvut.erm.sql.interfaces.SubTreeProducer;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * SQL schema with SQL commands.
 */
public class SchemaSQL implements SubSQLProducer, SubTreeProducer {
    /**
     * Corresponding relational schema.
     *
     * @see cz.felk.cvut.erm.conc2rela.SchemaC2R
     */
    private SchemaC2R schemaC2R = null;
    /**
     * Commands for creating tables.
     */
    private Vector<CreateCommandSQL> createCommands = new Vector<CreateCommandSQL>();
    /**
     * Commands for altering tables.
     */
    private Vector<AlterAddCommandSQL> alterAddCommands = new Vector<AlterAddCommandSQL>();
    /**
     * Commands for dropping tables.
     */
    private Vector<DropCommandSQL> dropCommands = new Vector<DropCommandSQL>();
    /**
     * Commands for creating types
     */
    private Vector<CreateTypeSQL> createTypes = new Vector<CreateTypeSQL>();
    /**
     * Commands for creating incomplete types
     */
    private Vector<CreateIncompleteTypeSQL> createIncompleteTypes = new Vector<CreateIncompleteTypeSQL>();
    /**
     * Commands for dropping types
     */
    private Vector<DropTypeSQL> dropTypes = new Vector<DropTypeSQL>();

    private Intro intro = null;

    /**
     * Constructor.
     */
    public SchemaSQL() {
    }

    /**
     * Constructor.
     *
     * @param aSchemaC2R Corresponding relational schema to SQL schema
     */
    public SchemaSQL(SchemaC2R aSchemaC2R) {
        schemaC2R = aSchemaC2R;
    }

    /**
     * Adds alter add command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addAlterAddCommand(AlterAddCommandSQL aAlterAddCommand) {
        if (getAlterAddCommands().contains(aAlterAddCommand)) {
//		throw AlreadyContainsExceptionSQL();
        }
        if (!aAlterAddCommand.isEmpty())
            getAlterAddCommands().addElement(aAlterAddCommand);
    }

    /**
     * Adds create table command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addCreateCommand(CreateCommandSQL aCreateCommand) {
        if (getCreateCommands().contains(aCreateCommand)) {
//		throw AlreadyContainsExceptionSQL();
        }
        if (!aCreateCommand.isEmpty())
            getCreateCommands().addElement(aCreateCommand);
    }

    /**
     * Adds drop table command.
     *
     * @param aAlterAddCommand cz.omnicom.ermodeller.sql.AlterAddCommandSQL
     */
    public void addDropCommand(DropCommandSQL aDropCommand) {
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
    public void addCreateType(CreateTypeSQL aCreateType) {
        if (getCreateTypes().contains(aCreateType)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getCreateTypes().addElement(aCreateType);
    }

    /**
     * Adds create incomplete type command.
     *
     * @param aCreateType cz.omnicom.ermodeller.sql.CreateTypeSQL
     */
    public void addCreateIncompleteType(CreateIncompleteTypeSQL aCreateType) {
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
    public void addDropType(DropTypeSQL aDropType) {
        if (getDropTypes().contains(aDropType)) {
//		throw AlreadyContainsExceptionSQL();
        }
        getDropTypes().addElement(aDropType);
    }

    public void setIntro(Intro i) {
        intro = i;
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
        for (Enumeration<DropCommandSQL> elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<DropTypeSQL> elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateIncompleteTypeSQL> elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateTypeSQL> elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n/\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<CreateCommandSQL> elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandSQL commandSQL = elements.nextElement();
            result += commandSQL.createSubSQL(countTabs) + ";\n" + ((elements.hasMoreElements()) ? "" : "\n");
        }
        for (Enumeration<AlterAddCommandSQL> elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandSQL commandSQL = elements.nextElement();
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
        for (Enumeration<DropCommandSQL> elements = getDropCommands().elements(); elements.hasMoreElements();) {
            DropCommandSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<DropTypeSQL> elements = getDropTypes().elements(); elements.hasMoreElements();) {
            DropTypeSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateIncompleteTypeSQL> elements = getCreateIncompleteTypes().elements(); elements.hasMoreElements();) {
            CreateIncompleteTypeSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateTypeSQL> elements = getCreateTypes().elements(); elements.hasMoreElements();) {
            CreateTypeSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<CreateCommandSQL> elements = getCreateCommands().elements(); elements.hasMoreElements();) {
            CreateCommandSQL commandSQL = elements.nextElement();
            top.add(commandSQL.createSubTree());
        }
        for (Enumeration<AlterAddCommandSQL> elements = getAlterAddCommands().elements(); elements.hasMoreElements();) {
            AlterAddCommandSQL commandSQL = elements.nextElement();
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
    protected Vector<AlterAddCommandSQL> getAlterAddCommands() {
        if (alterAddCommands == null)
            alterAddCommands = new Vector<AlterAddCommandSQL>();
        return alterAddCommands;
    }

    /**
     * Return create table commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateCommandSQL> getCreateCommands() {
        if (createCommands == null)
            createCommands = new Vector<CreateCommandSQL>();
        return createCommands;
    }

    /**
     * Returns drop commands.
     *
     * @return java.util.Vector
     */
    protected Vector<DropCommandSQL> getDropCommands() {
        if (dropCommands == null)
            dropCommands = new Vector<DropCommandSQL>();
        return dropCommands;
    }

    /**
     * Returns create type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateTypeSQL> getCreateTypes() {
        if (createTypes == null)
            createTypes = new Vector<CreateTypeSQL>();
        return createTypes;
    }

    /**
     * Returns create incomplete type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<CreateIncompleteTypeSQL> getCreateIncompleteTypes() {
        if (createIncompleteTypes == null)
            createIncompleteTypes = new Vector<CreateIncompleteTypeSQL>();
        return createIncompleteTypes;
    }

    /**
     * Returns drop type commands.
     *
     * @return java.util.Vector
     */
    protected Vector<DropTypeSQL> getDropTypes() {
        if (dropTypes == null)
            dropTypes = new Vector<DropTypeSQL>();
        return dropTypes;
    }

    protected Intro getIntro() {
        if (intro == null) intro = new Intro();
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
        return "SQL commands in the schema";//schemaC2R.getNameC2R();
    }
}