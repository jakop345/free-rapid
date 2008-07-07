package cz.felk.cvut.erm.conceptual.beans;

import cz.felk.cvut.erm.errorlog.*;
import cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException;
import cz.felk.cvut.erm.errorlog.interfaces.ShowErrorListener;
import cz.felk.cvut.erm.errorlog.interfaces.Validatable;

import javax.swing.*;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A general abstract superclass of all classes in conceptual schema.
 */
public abstract class ConceptualObject implements Serializable, ShowErrorListener, Validatable {
    /**
     * Name of the object.
     */
    private String fieldName = "";
    /**
     * ID of the object.
     */
    protected int fieldID = 0;
    /**
     * Listeners of the PropertyChange event.
     *
     * @see java.beans.PropertyChangeSupport
     */
    protected transient PropertyChangeSupport propertyChange = null;
    /**
     * Schema owner of the object.
     *
     * @see Schema
     */
    private Schema fieldSchema = null;
    /**
     * List of actual found errors.
     *
     * @see cz.felk.cvut.erm.errorlog.ErrorLogList
     */
    private transient ErrorLogList errorLogList = new ErrorLogList();
    /**
     * If the object was already checked or not
     */
    private boolean fieldValidated = false;
    /**
     * Listners of ShowError events
     *
     * @see cz.felk.cvut.erm.errorlog.ShowErrorSupport
     */
    private transient ShowErrorSupport showError = null;
    /**
     * Comment on the object
     */
    private String fieldComment = "";

    public static final String SCHEMA_PROPERTY_CHANGE = "schema";
    public static final String NAME_PROPERTY_CHANGE = "name";
    public static final String COMMENT_PROPERTY_CHANGE = "comment";

    /**
     * <code>ConceptualObjectNameController</code> class is used when checking unicity
     * of names of objects. The vector of objects which has to be controlled must be
     * vector of <code>ConceptualObjectNameController</code> instances.
     *
     * @see #checkVectorForNameDuplicity
     */
    protected class ConceptualObjectNameController {
        /**
         * Is the enclosed object already in some error.
         */
        private boolean wrong = false;
        /**
         * Conceptual object hold.
         *
         * @see ConceptualObject
         */
        private final ConceptualObject conceptualObject;

        /**
         * Constructor.
         *
         * @see ConceptualObject
         */
        public ConceptualObjectNameController(ConceptualObject anObject) {
            conceptualObject = anObject;
        }

        /**
         * Returns if the object enclosed is already in some error.
         *
         * @return wrong property
         * @see #setWrong
         */
        public boolean isAlreadyWrong() {
            return wrong;
        }

        /**
         * Sets the <code>wrong</code> property.
         *
         * @see #isAlreadyWrong
         */
        public void setWrong(boolean newWrong) {
            wrong = newWrong;
        }

        /**
         * @return cz.omnicom.ermodeller.conceptual.ConceptualObject
         */
        public ConceptualObject getConceptualObject() {
            return conceptualObject;
        }
    }

    /**
     * Adds an Error to the list of errors of the object.
     * This method is called by <code>connectErrorToObject()</code> methos of the
     * <code>ValidationError</code>
     *
     * @param anError cz.omnicom.ermodeller.errorlog.ValidationError
     * @see cz.felk.cvut.erm.errorlog.ValidationError#connectErrorToObject
     */
    public void addError(ValidationError anError) {
        this.errorLogList.addElement(anError);
    }

    /**
     * The addPropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().addPropertyChangeListener(listener);
    }

    /**
     * Adds ShowError event listener.
     *
     * @param aShowErrorListener cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public synchronized void addShowErrorListener(ShowErrorListener aShowErrorListener) {
        getShowError().addShowErrorListener(aShowErrorListener);
    }

    /**
     * Empties the object. Every derived class must call its superclass's method first:
     * <blockquote>
     * <pre>
     * super.empty();
     * // Then goes derived classes code.
     * </pre>
     * </blockquote>
     */
    protected synchronized void empty() {
        setErrorLogList(null);
    }

    /**
     * Empties the specified <code>vector</code>. On each element
     * the method <code>empty()</code> is called.
     *
     * @param vector java.util.Vector
     * @see #empty
     */
    protected static final void emptyConceptualVector(Vector vector) {
        synchronized (vector) {
            for (Enumeration elements = vector.elements(); elements.hasMoreElements();) {
                // Each object must be emptied.
                ((ConceptualObject) elements.nextElement()).empty();
            }
            vector.removeAllElements();
            vector.trimToSize();
        }
    }

    /**
     * The firePropertyChange method was generated to support the propertyChange field.
     */
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);

        // invalidation of all errors connected to this object
        setErrorLogList(null);
    }

    /**
     * Firing ShowError event.
     */
    public void fireShowError(ShowErrorEvent evt) {
        getShowError().fireShowError(evt);
    }

    /**
     * Gets the <code>comment</code> property (java.lang.String) value.
     *
     * @return The comment property value.
     * @see #setComment
     */
    public String getComment() {
        return fieldComment;
    }

    /**
     * Gets actual errorLogList.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @see #errorLogList
     */
    protected ErrorLogList getErrorLogList() {
        if (errorLogList == null)
            errorLogList = new ErrorLogList();
        return errorLogList;
    }

    /**
     * Gets the ID property value.
     *
     * @return The ID property value.
     * @see #setID
     */
    public int getID() {
        return fieldID;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for invalid state.
     *
     * @return Icon represented invalid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public abstract Icon getInvalidIcon();

    /**
     * Gets the name property (java.lang.String) value.
     *
     * @return The name property value.
     * @see #setName
     */
    public String getName() {
        return fieldName;
    }

    /**
     * Accessor for the propertyChange field.
     */
    protected PropertyChangeSupport getPropertyChange() {
        if (propertyChange == null)
            propertyChange = new PropertyChangeSupport(this);
        return propertyChange;
    }

    /**
     * Accessor for the showError field.
     */
    protected ShowErrorSupport getShowError() {
        if (showError == null)
            showError = new ShowErrorSupport(this);
        return showError;
    }

    /**
     * Gets the schema property (cz.omnicom.ermodeller.conceptual.Schema) value.
     *
     * @return The schema property value.
     * @see #setSchema
     */
    public Schema getSchema() {
        return fieldSchema;
    }

    /**
     * Gets the validated property (boolean) value.
     *
     * @return The validated property value.
     * @see #setValidated
     */
    private boolean getValidated() {
        return fieldValidated;
    }

    /**
     * Gets the icon for representation in list of errors in <code>ErrorLogDialog</code>.
     * This returns icon for valid state.
     *
     * @return Icon represented valid state of the atribute.
     * @see cz.felk.cvut.erm.dialogs.ErrorLogDialog
     */
    public abstract Icon getValidIcon();

    /**
     * Checks the vector of <code>ConceptualObjectNameController</code>s for the duplicity.
     *
     * @param aValidationErrorClass Class of the error which should be created when duplicity is found.
     *                              The instantiated error should be subclass of <code>ConceptualObjectVectorValidationError</code>.
     * @return list of errors
     * @see cz.felk.cvut.erm.errorlog.ErrorLogList
     */
    protected static final ErrorLogList checkVectorForNameDuplicity(Vector<ConceptualObjectNameController> vectorToCheck, Class aValidationErrorClass) throws InstantiationException, IllegalAccessException {
        ErrorLogList errorLogList = new ErrorLogList();
        synchronized (vectorToCheck) {
            for (int i = 0; i < vectorToCheck.size(); i++) {
                ConceptualObjectNameController firstController = vectorToCheck.elementAt(i);
                if (!firstController.isAlreadyWrong()) {
                    ConceptualObject firstObject = firstController.getConceptualObject();
                    String firstName = firstObject.getName();
                    // create new Error
                    ConceptualObjectVectorValidationError error = (ConceptualObjectVectorValidationError) aValidationErrorClass.newInstance();
                    boolean errorAppeared = false;
                    for (int j = i + 1; j < vectorToCheck.size(); j++) {
                        ConceptualObjectNameController secondController = vectorToCheck.elementAt(j);
                        if (!secondController.isAlreadyWrong()) {
                            ConceptualObject secondObject = secondController.getConceptualObject();
                            String secondName = secondObject.getName();
                            if (firstName.equalsIgnoreCase(secondName)) {
                                errorAppeared = true;
                                // add to new Error
                                error.connectErrorToObject(secondObject);
                                firstController.setWrong(true);
                                secondController.setWrong(true);
                            }
                        }
                    }
                    if (errorAppeared) {
                        // if error appeared, then add it to list of errors
                        error.connectErrorToObject(firstObject);
                        errorLogList.addElement(error);
                    }
                }
            }
        }
        return errorLogList;
    }

    /**
     * Returns true if the schema is changed
     */
    public boolean isChanged() {
        return getSchema().isChanged();
    }

    /**
     * The removePropertyChangeListener method was generated to support the propertyChange field.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        getPropertyChange().removePropertyChangeListener(listener);
    }

    /**
     * Removes <code>aShowErrorListener</code> from ShowError event listeners.
     *
     * @param aShowErrorListener cz.omnicom.ermodeller.errorlog.ShowErrorListener
     */
    public synchronized void removeShowErrorListener(ShowErrorListener aShowErrorListener) {
        getShowError().removeShowErrorListener(aShowErrorListener);
    }

    /**
     * Sets the object's validated property to <code>false</code>.
     * Empties list of errors.
     *
     * @see #setValidated
     * @see #setErrorLogList
     */
    protected void setAllUnvalidated() {
        setValidated(false);
        setErrorLogList(new ErrorLogList());
    }

    /**
     * Sets the comment property (java.lang.String) value.
     *
     * @param comment The new value for the property.
     * @see #getComment
     */
    public void setComment(String comment) {
        String oldValue = fieldComment;
        fieldComment = comment;
        firePropertyChange(COMMENT_PROPERTY_CHANGE, oldValue, comment);
    }

    /**
     * Resets the list of errors.
     * The <code>ConceptualObject</code> could be listener of the errors in the previous list,
     * for every error the <code>conceptualObject</code> is removed from the list of ShowError listeners.
     *
     * @param newValue cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @see cz.felk.cvut.erm.errorlog.ValidationError#removeShowErrorListener
     */
    protected void setErrorLogList(ErrorLogList newValue) {
        // pro kazdou chybu se musi odregistrovat this objekt z listeners chyby
        for (Enumeration errors = getErrorLogList().elements(); errors.hasMoreElements();) {
            ((ValidationError) errors.nextElement()).removeShowErrorListener(this);
        }
        // u novych chyb je registrovan pri jejich vytvareni
        this.errorLogList = newValue;
    }

    /**
     * Sets the schema is changed
     */
    public void setChanged(boolean newChanged) {
        getSchema().setChanged(newChanged);
    }

    /**
     * Sets the ID property value.
     *
     * @see #getID
     */
    public void setID(int id) {
        fieldID = id;
        //System.out.println(id+"\t"+getName()+"\t"+getClass());
    }

    /**
     * Sets the name property (java.lang.String) value.
     *
     * @param name The new value for the property.
     * @see #getName
     */
    public void setName(String name) {
        String oldValue = fieldName;
        fieldName = name;
        firePropertyChange(NAME_PROPERTY_CHANGE, oldValue, name);
    }

    /**
     * Sets the schema property (cz.omnicom.ermodeller.conceptual.Schema) value.
     *
     * @param schema The new value for the property.
     * @see #getSchema
     */
    public void setSchema(Schema schema) {
        Schema oldValue = fieldSchema;
        fieldSchema = schema;
        firePropertyChange(SCHEMA_PROPERTY_CHANGE, oldValue, schema);
        setID(schema.createID());
        //System.out.println("setSchema \t"+getClass());
    }

    /**
     * Sets the validated property (boolean) value.
     *
     * @param validated The new value for the property.
     * @see #getValidated
     */
    private void setValidated(boolean validated) {
        fieldValidated = validated;
    }

    /**
     * When an ShowError event is passed to the <code>ConceptualObject</code>,
     * it fires ShowError event.
     *
     * @param anEvent cz.omnicom.ermodeller.errorlog.ShowErrorEvent
     * @see #fireShowError
     */
    public void showError(ShowErrorEvent anEvent) {
        fireShowError(anEvent);
    }

    public String toString() {
        //                + "                        ";
//        if (getID() >= getSchema().getComposeID())
//            s = '*' + s;
        return "ConceptualObject " + getName();
    }

    /**
     * Checks the object and returns list of errors.
     *
     * @return cz.omnicom.ermodeller.errorlog.ErrorLogList
     * @throws cz.felk.cvut.erm.errorlog.exception.CheckNameDuplicityValidationException
     *
     * @see ConceptualObject#validate
     */
    protected synchronized ErrorLogList valid() throws CheckNameDuplicityValidationException {
        ErrorLogList errorLogList = new ErrorLogList();
        String name = getName();
        if (name == null || name.length() < 1) {
            if (!(this instanceof UniqueKey)) {
                ValidationError error = new DoesntHaveNameValidationError(this);
                error.connectErrorToObject(this);
                errorLogList.addElement(error);
            }
        } else {
            // checks the name for thefirst char
            char[] allowed = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', ' ', '\t'};
            boolean found = false;
            for (int i = 1; i < 53 & !found; i++) {
                found = (name.charAt(0) == allowed[i]);
            }
            if (!found) {
                ValidationError error = new BadFirstCharacterInNameValidationError(this, name.charAt(0));
                error.connectErrorToObject(this);
                errorLogList.addElement(error);
            }
            // checks the name for allowed characters
            char[] nameArr = name.toCharArray();
            Vector<Character> notAllowed = new Vector<Character>();
            for (char aNameArr : nameArr) {
                found = false;
                for (int j = 0; j < allowed.length && !found; j++) {
                    found = (allowed[j] == aNameArr);
                }
                if (!found)
                    notAllowed.addElement(aNameArr);
            }
            if (!notAllowed.isEmpty()) {
                ValidationError error = new BadCharacterInNameValidationError(this, notAllowed);
                error.connectErrorToObject(this);
                errorLogList.addElement(error);
            }
        }
        return errorLogList;
    }

    /**
     * Checks the object. It calls <code>valid()</code> method.
     *
     * @return List of errors
     * @see #valid
     */
    public synchronized final ErrorLogList validate() throws CheckNameDuplicityValidationException {
        ErrorLogList eLL;
        if (getValidated())
            eLL = new ErrorLogList();
        else
            eLL = this.valid();
        setValidated(true);
        return eLL;
    }

    /**
     * Writes data for conceptual object model into XML file
     *
     * @param pw java.io.PrintWriter
     */
    public void write(java.io.PrintWriter pw) {
        pw.println("\t\t<id>" + getID() + "</id>");
        pw.println("\t\t<name>" + getName() + "</name>");
        pw.println("\t\t<comment>" + getComment() + "</comment>");
        //System.out.println(getID()+"\t"+getName()+"\t"+getClass());
    }
}