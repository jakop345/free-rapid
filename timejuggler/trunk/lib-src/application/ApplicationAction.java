
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package application;

import application.Action.Block;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;



/**
 * The {@link javax.swing.Action} class used to implement the
 * <tt>&#064;Action</tt> annotation.  This class is typically not
 * instantiated directly, it's created as a side effect of constructing
 * an <tt>ApplicationActionMap</tt>:
 * <pre>
 * public class MyActions {
 *     &#064;Action public void anAction() { }  // an &#064;Action named "anAction"
 * }
 * ApplicationContext ac = ApplicationContext.getInstance();
 * ActionMap actionMap = ac.getActionMap(new MyActions());
 * myButton.setAction(actionMap.get("anAction"));
 * </pre>
 * 
 * <p>
 * When an ApplicationAction is constructed, it initializes all of its
 * properties from the specified <tt>ResourceMap</tt>.  Resource names
 * must match the {@code @Action's} name, which is the name of the
 * corresponding method, or the value of the optional {@code @Action} name
 * parameter.  To initialize the text and shortDescription properties
 * of the action named <tt>"anAction"</tt> in the previous example, one
 * would define two resources:
 * <pre>
 * anAction.Action.text = Button/Menu/etc label text for anAction
 * anAction.Action.shortDescription = Tooltip text for anAction
 * </pre>
 * 
 * <p>
 * A complete description of the mapping between resources and Action
 * properties can be found in the ApplicationAction {@link
 * #ApplicationAction constructor} documentation.
 * 
 * <p>
 * An ApplicationAction's <tt>enabled</tt> and <tt>selected</tt> 
 * properties can be delegated to boolean properties of the 
 * Actions class, by specifying the corresponding property names.
 * This can be done with the {@code @Action} annotation, e.g.:
 * <pre>
 * public class MyActions {
 *     &#064;Action(enabledProperty = "anActionEnabled")
 *     public void anAction() { } 
 *     public boolean isAnActionEnabled() {
 *         // will fire PropertyChange when anActionEnabled changes 
 *         return anActionEnabled;
 *     }
 * }
 * </pre>
 * If the MyActions class supports PropertyChange events, then then
 * ApplicationAction will track the state of the specified property
 * ("anActionEnabled" in this case) with a PropertyChangeListener.
 * 
 * <p>
 * ApplicationActions can automatically <tt>block</tt> the GUI while the 
 * <tt>actionPerformed</tt> method is running, depending on the value of
 * block property.  For example, if the value of block is 
 * <tt>Block.ACTION</tt>, then the action will be disabled while
 * the actionPerformed method runs.
 * 
 * <p> 
 * An ApplicationAction can have a <tt>proxy</tt> Action, i.e.
 * another Action that provides the <tt>actionPerformed</tt> method,
 * the enabled/selected properties, and values for the Action's long
 * and short descriptions.  If the proxy property is set, this
 * ApplicationAction tracks all of the aforementioned properties, and
 * the <tt>actionPerformed</tt> method just calls the proxy's
 * <tt>actionPerformed</tt> method.  If a <tt>proxySource</tt> is
 * specified, then it becomes the source of the ActionEvent that's
 * passed to the proxy <tt>actionPerformed</tt> method.  Proxy action 
 * dispatching is as simple as this:
 * <pre>
 * public void actionPerformed(ActionEvent actionEvent) {
 *     javax.swing.Action proxy = getProxy();
 *     if (proxy != null) {
 *         actionEvent.setSource(getProxySource());
 *         proxy.actionPerformed(actionEvent);
 *     }
 *     // ....
 * }
 * </pre>
 * 
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ApplicationContext#getActionMap(Object)
 * @see ResourceMap
 */
public class ApplicationAction extends AbstractAction {
    private final ApplicationActionMap appAM;
    private final ResourceMap resourceMap;
    private final String actionName;       // see getName()
    private final Method actionMethod;     // The @Action method
    private final String enabledProperty;  // names an appAM.getActionsClass() property
    private final Method isEnabledMethod;  // Method object for is/getEnabledProperty
    private final Method setEnabledMethod; // Method object for setEnabledProperty
    private final String selectedProperty; // TBD...
    private final Action.Block block;
    private boolean blocking = false;
    private javax.swing.Action proxy = null;
    private Object proxySource = null;
    private PropertyChangeListener proxyPCL = null;

    /**
     * Construct an <tt>ApplicationAction</tt> that implements an <tt>&#064;Action</tt>.
     * 
     * <p>
     * If a {@code ResourceMap} is provided, then all of the 
     * {@link javax.swing.Action Action} properties are initialized
     * with the values of resources whose key begins with {@code baseName}.
     * ResourceMap keys are created by appending an &#064;Action resource
     * name, like "Action.shortDescription" to the &#064;Action's baseName 
     * For example, Given an &#064;Action defined like this:
     * <pre>
     * &#064;Action void actionBaseName() { } 
     * </pre>
     * <p>
     * Then the shortDescription resource key would be 
     * <code>actionBaseName.Action.shortDescription</code>, as in:
     * <pre>
     * actionBaseName.Action.shortDescription = Do perform some action
     * </pre>
     * 
     * <p>
     * The complete set of &#064;Action resources is:
     * <pre>
     * Action.icon
     * Action.text
     * Action.shortDescription
     * Action.longDescription
     * Action.smallIcon
     * Action.largeIcon
     * Action.command
     * Action.accelerator
     * Action.mnemonic
     * Action.displayedMnemonicIndex
     * </pre>
     * 
     * <p>
     * A few the resources are handled specially:
     * <ul>
     * <li><tt>Action.text</tt><br>
     * Used to initialize the Action properties with keys
     * <tt>Action.NAME</tt>, <tt>Action.MNEMONIC_KEY</tt> and
     * <tt>Action.DISPLAYED_MNEMONIC_INDEX</tt>.
     * If the resources's value contains an "&" or an "_" it's 
     * assumed to mark the following character as the mnemonic.
     * If Action.mnemonic/Action.displayedMnemonic resources are
     * also defined (an odd case), they'll override the mnemonic 
     * specfied with the Action.text marker character.
     * 
     * <li><tt>Action.icon</tt><br>
     * Used to initialize both ACTION.SMALL_ICON,LARGE_ICON.  If 
     * Action.smallIcon or Action.largeIcon resources are also defined
     * they'll override the value defined for Action.icon.
     * 
     * <li><tt>Action.displayedMnemonicIndexKey</tt><br>
     * The corresponding javax.swing.Action constant is only defined in Java SE 6.
     * We'll set the Action property in Java SE 5 too.
     * </ul>
     * 
     * @param appAM the ApplicationActionMap this action is being constructed for.
     * @param resourceMap initial Action properties are loaded from this ResourceMap.
     * @param baseName the name of the &#064;Action
     * @param actionMethod unless a proxy is specified, actionPerformed calls this method.
     * @param enabledProperty name of the enabled property.
     * @param selectedProperty name of the selected property.
     * @param block how much of the GUI to block while this action executes.
     * 
     * @see #getName
     * @see ApplicationActionMap#getActionsClass
     * @see ApplicationActionMap#getActionsObject
     */
    public ApplicationAction(ApplicationActionMap appAM,
			     ResourceMap resourceMap,
			     String baseName,
			     Method actionMethod, 
			     String enabledProperty, 
			     String selectedProperty,
			     Action.Block block) {
	if (appAM == null) {
	    throw new IllegalArgumentException("null appAM");
	}
	if (baseName == null) {
	    throw new IllegalArgumentException("null baseName");
	}

	this.appAM = appAM;
	this.resourceMap = resourceMap;
	this.actionName = baseName;
	this.actionMethod = actionMethod;
	this.enabledProperty = enabledProperty;
	this.selectedProperty = selectedProperty;
	this.block = block;

	/* If enabledProperty is specified, lookup up the is/set methods and
	 * verify that the former exists.
	 */
	if (this.enabledProperty != null) {
	    setEnabledMethod = propertySetMethod(enabledProperty, boolean.class);
	    isEnabledMethod = propertyGetMethod(enabledProperty);
	    if (isEnabledMethod == null) {
		String acn = appAM.getActionsClass().getName();
		String eps = "\"is/get" + enabledProperty + "\"";
		throw new IllegalArgumentException("no property named " + eps + " in " + acn);
	    }
	}
	else {
	    this.isEnabledMethod = null;
	    this.setEnabledMethod = null;
	}

	// TBD selectedProperty

	if (resourceMap != null) {
	    initActionProperties(resourceMap, baseName);
	    if (block != Action.Block.NONE) {
		String title = resourceMap.getString(baseName + ".Action.BlockingDialog.title");
		if (title != null) {
		    putValue("BlockingDialog.title", title);
		}
		String message = resourceMap.getString(baseName + ".Action.BlockingDialog.message");
		if (message != null) {
		    putValue("BlockingDialog.message", message);
		}
		Icon icon = resourceMap.getIcon(baseName + ".Action.BlockingDialog.icon");
		if (icon != null) {
		    putValue("BlockingDialog.icon", icon);
		}
	    }
	}
    }

    /* Shorter convenience constructor used to create ProxyActions, 
     * see ApplicationActionMap.addProxyAction().
     */
    ApplicationAction(ApplicationActionMap appAM, ResourceMap resourceMap, String actionName) {
	this(appAM, resourceMap, actionName, null, null, null, Action.Block.NONE);
    }


    /**
     * The name of the enabledProperty whose value is returned
     * by {@link #isEnabled isEnabled}, or null.
     * 
     * @return the name of the enabledProperty or null.
     * @see #isEnabled
     */
    String getEnabledProperty() { 
	return enabledProperty; 
    }
    
    /**
     * TBD
     */
    String getSelectedProperty() { return selectedProperty; }


    /**
     * Return the proxy for this action or null.
     *
     * @return the value of the proxy property.
     * @see #setProxy
     * @see #setProxySource
     * @see #actionPerformed
     */
    public javax.swing.Action getProxy() { 
	return proxy;
    }

    /**
     * Set the proxy for this action.  If the proxy is non-null then 
     * we delegate/track the following:
     * <ul>
     * <li><tt>actionPerformed</tt><br>
     * Our <tt>actionPerformed</tt> method calls the delegate's after 
     * the ActionEvent source to be the value of <tt>getProxySource</tt>
     * 
     * <li><tt>shortDescription</tt><br>
     * If the proxy's shortDescription, i.e. the value for key
     * {@link javax.swing.Action#SHORT_DESCRIPTION SHORT_DESCRIPTION} is not null,
     * then set this action's shortDescription.  Most Swing components use
     * the shortDescription to initialize their tooltip.
     * 
     * <li><tt>longDescription</tt><br>
     * If the proxy's longDescription, i.e. the value for key
     * {@link javax.swing.Action#LONG_DESCRIPTION LONG_DESCRIPTION} is not null,
     * then set this action's longDescription.  
     * </ul>
     * 
     * @see #setProxy
     * @see #setProxySource
     * @see #actionPerformed
     */
    public void setProxy(javax.swing.Action proxy) { 
	javax.swing.Action oldProxy = this.proxy;
	this.proxy = proxy;
	if (oldProxy != null) {
	    oldProxy.removePropertyChangeListener(proxyPCL);
	    proxyPCL = null;
	}
	if (this.proxy != null) {
	    updateProxyProperties();
	    proxyPCL = new ProxyPCL();
	    proxy.addPropertyChangeListener(proxyPCL);
	}
	else if (oldProxy != null) {
	    setEnabled(false);
	    // TBD SELECTED
	}
	firePropertyChange("proxy", oldProxy, this.proxy);
    }

    /**
     * Return the value that becomes the <tt>ActionEvent</tt> source  before
     * the ActionEvent is passed along to the proxy Action.
     * 
     * @return the value of the proxySource property.
     * @see #getProxy
     * @see #setProxySource
     * @see ActionEvent#getSource
     */
    public Object getProxySource() { 
	return proxySource; 
    }

    /**
     * Set the value that becomes the <tt>ActionEvent</tt> source before
     * the ActionEvent is passed along to the proxy Action.  
     * 
     * @param source the <tt>ActionEvent</tt> source/
     * @see #getProxy
     * @see #getProxySource
     * @see ActionEvent#setSource
     */
    public void setProxySource(Object source) {
	Object oldValue = this.proxySource;
	this.proxySource = source;
	firePropertyChange("proxySource", oldValue, this.proxySource);
    }

    private void  maybePutDescriptionValue(String key, javax.swing.Action proxy) {
	Object s = proxy.getValue(key);
	if (s instanceof String) {
	    putValue(key, (String)s);
	}
    }

    private void updateProxyProperties() {
	javax.swing.Action proxy = getProxy();
	if (proxy != null) {
	    setEnabled(proxy.isEnabled());
	    // TBD SELECTED
	    maybePutDescriptionValue(javax.swing.Action.SHORT_DESCRIPTION, proxy);
	    maybePutDescriptionValue(javax.swing.Action.LONG_DESCRIPTION, proxy);
	}
    }

    /* This PCL is added to the proxy action, i.e. getProxy().  We
     * track the following properties of the proxy action we're bound to:
     * enabled, selected, longDescription, shortDescription.  We only
     * mirror the description properties if they're non-null.
     */
    private class ProxyPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ((propertyName == null) || "enabled".equals(propertyName) ||
		// TBD SELECTED
		javax.swing.Action.SHORT_DESCRIPTION.equals(propertyName) ||
		javax.swing.Action.LONG_DESCRIPTION.equals(propertyName)) {
		updateProxyProperties();
	    }
	}
    }    

    /* The corresponding javax.swing.Action constants are only 
     * defined in Mustang (1.6), see 
     * http://download.java.net/jdk6/docs/api/javax/swing/Action.html
     */
    private static final String SELECTED_KEY = "SwingSelectedKey";
    private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
    private static final String LARGE_ICON_KEY = "SwingLargeIconKey";

    /* Init all of the javax.swing.Action properties for the @Action
     * named actionName.  
     */
    private void initActionProperties(ResourceMap resourceMap, String baseName) {
	boolean iconOrNameSpecified = false;  // true if Action's icon/name properties set
	String typedName = null;

	// Action.text => Action.NAME,MNEMONIC_KEY,DISPLAYED_MNEMONIC_INDEX_KEY
	String text = resourceMap.getString(baseName + ".Action.text");
	if (text != null) {
	    int mnemonicIndex = text.indexOf("&");
	    if (mnemonicIndex == -1) {
		mnemonicIndex = text.indexOf("_");
	    }
	    if ((mnemonicIndex != -1) && ((mnemonicIndex + 1) < text.length())) {
		text = text.substring(0, mnemonicIndex) + text.substring(mnemonicIndex + 1);
		putValue(javax.swing.Action.NAME, text);
		putValue(javax.swing.Action.MNEMONIC_KEY, new Integer(text.charAt(mnemonicIndex)));
		putValue(DISPLAYED_MNEMONIC_INDEX_KEY, mnemonicIndex);
	    }
	    else {
		putValue(javax.swing.Action.NAME, text);
	    }
	    iconOrNameSpecified = true;
	}
	// Action.mnemonic => Action.MNEMONIC_KEY
	Integer mnemonic = resourceMap.getKeyCode(baseName + ".Action.mnemonic");
	if (mnemonic != null) {
	    putValue(javax.swing.Action.MNEMONIC_KEY, mnemonic);
	}
	// Action.mnemonic => Action.DISPLAYED_MNEMONIC_INDEX_KEY
	Integer index = resourceMap.getKeyCode(baseName + ".Action.displayedMnemonicIndex");
	if (index != null) {
	    putValue(DISPLAYED_MNEMONIC_INDEX_KEY, index);
	}
	// Action.accelerator => Action.ACCELERATOR_KEY
	KeyStroke key = resourceMap.getKeyStroke(baseName + ".Action.accelerator");
	if (key != null) {
	    putValue(javax.swing.Action.ACCELERATOR_KEY, key);
	}
	// Action.icon => Action.SMALL_ICON,LARGE_ICON_KEY
	Icon icon = resourceMap.getIcon(baseName + ".Action.icon");
	if (icon != null) {
	    putValue(javax.swing.Action.SMALL_ICON, icon);
	    putValue(LARGE_ICON_KEY, icon);
	    iconOrNameSpecified = true;
	}
	// Action.smallIcon => Action.SMALL_ICON
	Icon smallIcon = resourceMap.getIcon(baseName + ".Action.smallIcon");
	if (smallIcon != null) {
	    putValue(javax.swing.Action.SMALL_ICON, smallIcon);
	    iconOrNameSpecified = true;
	}
	// Action.largeIcon => Action.LARGE_ICON
	Icon largeIcon = resourceMap.getIcon(baseName + ".Action.largeIcon");
	if (largeIcon != null) {
	    putValue(LARGE_ICON_KEY, largeIcon);
	    iconOrNameSpecified = true;
	}
	// Action.shortDescription => Action.SHORT_DESCRIPTION
	putValue(javax.swing.Action.SHORT_DESCRIPTION, 
		 resourceMap.getString(baseName + ".Action.shortDescription"));
	// Action.longDescription => Action.LONG_DESCRIPTION
	putValue(javax.swing.Action.LONG_DESCRIPTION, 
		 resourceMap.getString(baseName + ".Action.longDescription"));
	// Action.command => Action.ACTION_COMMAND_KEY
	putValue(javax.swing.Action.ACTION_COMMAND_KEY,
		 resourceMap.getString(baseName + ".Action.command"));
	// If no visual was defined for this Action, i.e. no text
	// and no icon, then we default Action.NAME
	if (!iconOrNameSpecified) {
	    putValue(javax.swing.Action.NAME, actionName);
	}
    }

    private String propertyMethodName(String prefix, String propertyName) {
	return prefix + propertyName.substring(0,1).toUpperCase() + propertyName.substring(1);
    }

    private Method propertyGetMethod(String propertyName) {
	String[] getMethodNames = {
	    propertyMethodName("is", propertyName),
	    propertyMethodName("get", propertyName)
	};
	Class actionsClass = appAM.getActionsClass();
	for (String name : getMethodNames) {
	    try {
		return actionsClass.getMethod(name);
	    }
	    catch(NoSuchMethodException ignore) { }
	}
	return null;
    }

    private Method propertySetMethod(String propertyName, Class type) {
	Class actionsClass = appAM.getActionsClass();
	try {
	    return actionsClass.getMethod(propertyMethodName("set", propertyName), type);
	}
	catch(NoSuchMethodException ignore) { 
	    return null;
	}
    }

    /**
     * 
     * The name of this Action.  This string begins with the name
     * the corresponding &#064;Action method (unless the <tt>name</tt>
     * &#064;Action parameter was specified).
     * 
     * <p>
     * This name is used as a prefix to look up action resources,
     * and the ApplicationContext Framework uses it as the key for this
     * Action in ApplicationActionMaps.  
     * 
     * <p> 
     * Note: this property should not confused with the {@link
     * javax.swing.Action#NAME Action.NAME} key.  That key is actually
     * used to initialize the <tt>text</tt> properties of Swing
     * components, which is why we call the corresponding
     * ApplicationAction resource "Action.text", as in:
     * <pre> 
     * myCloseButton.Action.text = Close 
     * </pre>
     * 
     * 
     * @return the read-only name of this ApplicationAction
     */
    public String getName() {
	return actionName;
    }

    /**
     * 
     * Provides parameter values to &#064;Action methods.  By default, parameter
     * values are selected based exclusively on their type:
     * <table border=1>
     *   <tr> 
     *     <th>Parameter Type</th> 
     *     <th>Parameter Value</th> 
     *   </tr>
     *   <tr> 
     *     <td><tt>ActionEvent</tt></td> 
     *     <td><tt>actionEvent</tt></td> 
     *   </tr>
     *   <tr> 
     *     <td><tt>javax.swing.Action</tt></td> 
     *     <td>this <tt>ApplicationAction</tt> object</td> 
     *   </tr>
     *   <tr> 
     *     <td><tt>ActionMap</tt></td> 
     *     <td>the <tt>ActionMap</tt> that contains this <tt>Action</tt></td> 
     *   </tr>
     *   <tr> 
     *     <td><tt>ResourceMap</tt></td> 
     *     <td>the <tt>ResourceMap</tt> of the the <tt>ActionMap</tt> that contains this <tt>Action</tt></td> 
     *   </tr>
     *   <tr> 
     *     <td><tt>ApplicationContext</tt></td> 
     *     <td>the value of <tt>ApplicationContext.getInstance()</tt></td> 
     *   </tr>
     * </table>
     * 
     * <p> 
     * ApplicationAction subclasses may also select values based on
     * the value of the <tt>Action.Parameter</tt> annotation, which is
     * passed along as the <tt>pKey</tt> argument to this method:
     * <pre>
     * &#064;Action public void doAction(&#064;Action.Parameter("myKey") String myParameter) {
     *    // The value of myParameter is computed by:
     *    // getActionArgument(String.class, "myKey", actionEvent)
     * }
     * </pre>
     * 
     * <p>
     * If <tt>pType</tt> and <tt>pKey</tt> aren't recognized, this method 
     * calls {@link #actionFailed} with an IllegalArgumentException.
     * 
     * 
     * @param pType parameter type
     * @param pKey the value of the &#064;Action.Parameter annotation
     * @param actionEvent the ActionEvent that trigged this Action
     */
    protected Object getActionArgument(Class pType, String pKey, ActionEvent actionEvent) {
	Object argument = null;
	if (pType == ActionEvent.class) {
	    argument = actionEvent;
	}
	else if (pType == javax.swing.Action.class) {
	    argument =  this;
	}
	else if (pType == ActionMap.class) {
	    argument = appAM;
	}
	else if (pType == ResourceMap.class) {
	    argument = resourceMap;
	}
	else if (pType == ApplicationContext.class) {
	    argument = ApplicationContext.getInstance();
	}
	else {
	    Exception e = new IllegalArgumentException("unrecognized @Action method parameter");
	    actionFailed(actionEvent, e);
	}
	return argument;
    }

    private JDialog getBlockingDialog(final Task task, ActionEvent event) {
	String dialogTitle = (String)(getValue("BlockingDialog.title"));
	String message = (String)(getValue("BlockingDialog.message"));
	Icon icon = (Icon)(getValue("BlockingDialog.icon"));
	String cancelButtonText = UIManager.getString("OptionPane.cancelButtonText");
	JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
	if (task.getUserCanCancel()) {
	    JButton cancelButton = new JButton(cancelButtonText);
	    ActionListener doCancelTask = new ActionListener() {
		    public void actionPerformed(ActionEvent ignore) {
			task.cancel(true);
		    }
		};
	    cancelButton.addActionListener(doCancelTask);
	    optionPane.setOptions(new Object[]{cancelButton});
	}
	if (icon != null) {
	    optionPane.setIcon(icon);
	}
	// TBD get window
	Component dialogOwner = (Component)(event.getSource());
	if (dialogOwner != null) {
	    Window w = SwingUtilities.getWindowAncestor(dialogOwner);
	    if (w != null) {
		dialogOwner = w;
	    }
	}
	JDialog dialog = optionPane.createDialog(dialogOwner, dialogTitle);
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	return dialog;
    }
    

    private class BlockPCL implements PropertyChangeListener {
	private final Task task;
	private final ActionEvent event;
	private Component component = null;
	private JDialog dialog = null;

	BlockPCL(Task task, ActionEvent event) {
	    this.task = task;
	    this.event = event;
	}

	private void showDialog() {
	    dialog = getBlockingDialog(task, event);
	    dialog.setModal(true);
	    // dialog.setModalityType(ModalityType.APPLICATION_MODAL);
	    dialog.pack();
	    /* The Task may have already fired some
	     * PropertyChange events, give them a chance
	     * to be handled first.
	     */
	    Runnable doShowDialog = new Runnable() {
		public void run() {
		    dialog.setVisible(true);
		}
	    };
	    EventQueue.invokeLater(doShowDialog);
	}

	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ("started".equals(propertyName)) {
		// TBD dialog cancel button (if any) should be enabled here
		if (block != Block.NONE) {
		    blocking = true;
		}
		switch(block) {
		case ACTION:
		    ApplicationAction.this.setEnabled(false);
		    break;
		case COMPONENT: 
		    component = (Component)(event.getSource());
		    if (component != null) {
			component.setEnabled(false);
		    }
		    break;
		case WINDOW:
		case APPLICATION: 
		    showDialog();
		    break;
		}
	    }
	    else if ("done".equals(propertyName)) {
		blocking = false;
		switch(block) {
		case ACTION: 
		    ApplicationAction.this.setEnabled(true);
		    break;
		case COMPONENT:
		    if (component != null) {
			component.setEnabled(true);
		    }
		    break;
		case WINDOW:
		case APPLICATION:
		    if (dialog != null) {
			dialog.setVisible(false);
		    }
		    break;
		}
	    }
	}
    }

    private void noProxyActionPerformed(ActionEvent actionEvent) {
	Object taskObject = null;

	/* Before the background task starts and its PCL (see BlockPCL)
	 * triggers the blocking action (like popping up a modal dialog)
	 * there's chance to handle another input event and get another 
	 * actionPerformed started.  We prevent that here.
	 */
	if (blocking) {
	    return;
	}

	/* Create the arguments array for actionMethod by 
	 * calling getActionArgument() for each parameter.
	 */
	Annotation[][] allPAnnotations = actionMethod.getParameterAnnotations();
	Class<?>[] pTypes = actionMethod.getParameterTypes();
	Object[] arguments = new Object[pTypes.length];
	for(int i = 0; i < pTypes.length; i++) {
	    String pKey = null;
	    for(Annotation pAnnotation : allPAnnotations[i]) {
		if (pAnnotation instanceof Action.Parameter) {
		    pKey = ((Action.Parameter)pAnnotation).value();
		    break;
		}
	    }
	    arguments[i] = getActionArgument(pTypes[i], pKey, actionEvent);
	}

	/* Call target.actionMethod(arguments).  If the return value
	 * is a Task, then execute it.
	 */
	try {
	    Object target = appAM.getActionsObject();
	    taskObject = actionMethod.invoke(target, arguments);
	}
	catch (Exception e) {
	    actionFailed(actionEvent, e);
	}

	if (taskObject instanceof Task) {
	    Task task = (Task)taskObject;
	    task.addPropertyChangeListener(new BlockPCL(task, actionEvent));
	    ApplicationContext appContext = ApplicationContext.getInstance();
	    appContext.getTaskService().execute(task);
	}
    }

    /**
     * This method implements this <tt>Action's</tt> behavior.  
     * 
     * <p>
     * If there's a proxy Action then call its actionPerformed
     * method.  Otherwise, call the &#064;Action method with parameter
     * values provided by {@code getActionArgument()}.  If anything goes wrong
     * call {@code actionFailed()}.  
     * 
     * <p>
     * [TBD explain the &#064;Action block=XXX parameter in more detail,
     * likewise for exception handling.
     * 
     * 
     * @param actionEvent @{inheritDoc}
     * @see #setProxy
     * @see #getActionArgument
     * @see Task
     */
    public void actionPerformed(ActionEvent actionEvent) {
	javax.swing.Action proxy = getProxy();
	if (proxy != null) {
	    actionEvent.setSource(getProxySource());
	    proxy.actionPerformed(actionEvent);
	}
	else if (actionMethod != null) {
	    noProxyActionPerformed(actionEvent);    
	}
    }

    /**
     * If the proxy action is null and <tt>enabledProperty</tt> was
     * specified, then return the value of the enabled property.
     * Otherwise return the value of this Action's enabled property by
     * invoking the corresponding <tt>is/get</tt> method on
     * <tt>appAM.getActionsObject()</tt>.
     * <p>
     * TBD: explain exception handling.
     * 
     * @return {@inheritDoc}
     * @see #setProxy
     * @see #setEnabled
     * @see ApplicationActionMap#getActionsObject
     */
    @Override
    public boolean isEnabled() {
	if ((getProxy() != null) || (isEnabledMethod == null)) {
	    return super.isEnabled();
	}
	else {
	    try {
		Object b = isEnabledMethod.invoke(appAM.getActionsObject());
		return (Boolean)b;
	    }
	    catch (Exception e) {
		e.printStackTrace(); // TBD NO NO NO
		return super.isEnabled();
	    }
	}
    }

    /**
     * If the proxy action is null and <tt>enabledProperty</tt> was
     * specified, then set the value of the enabled property by
     * invoking the corresponding <tt>set</tt> method on
     * <tt>appAM.getActionsObject()</tt>.  Otherwise return the value
     * of this Action's enabled property.  
     * <p>
     * TBD: explain exception handling
     * 
     * @param enabled {@inheritDoc}
     * @see #setProxy
     * @see #isEnabled
     * @see ApplicationActionMap#getActionsObject
     */
    @Override
    public void setEnabled(boolean enabled) {
	if ((getProxy() != null) || (setEnabledMethod == null)) {
	    super.setEnabled(enabled);
	}
	else {
	    try {
		setEnabledMethod.invoke(appAM.getActionsObject(), enabled);
	    }
	    catch (Exception e) {
		e.printStackTrace();  // NO NO NO
		super.setEnabled(enabled);
	    }
	}
    }

    /* Forward the controller's PropertyChangeEvent e to this
     * Action's PropertyChangeListeners using actionPropertyName instead
     * the original controller property name.  This method is used
     * by ControllerPCL to forward controller @Action enabledProperty and 
     * selectedProperty changes.
     */
    void forwardPropertyChangeEvent(PropertyChangeEvent e, String actionPropertyName) {
	firePropertyChange(actionPropertyName, e.getOldValue(), e.getNewValue());
    }

    /* Log enough output for a developer to figure out 
     * what went wrong.
     */
    private void actionFailed(ActionEvent actionEvent, Exception e) {
	// TBD Log an error
	// e.printStackTrace();
	throw new Error(e);
    }

    /**
     * Returns a string representation of this
     * <tt>ApplicationAction</tt> that should be useful for debugging.
     * If the action is enabled it's name is enclosed by parentheses;
     * if it's selected then a "+" appears after the name.  If the
     * action will appear with a text label, then that's included too.
     * If the action has a proxy, then we append the string for
     * the proxy action.
     *
     * @return A string representation of this ApplicationAction
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
	sb.append(getClass().getName());
	sb.append(" ");
	boolean enabled = isEnabled();
	if (!enabled) { sb.append("(");	}
	sb.append(getName());
	Object selectedValue = getValue(SELECTED_KEY);
	if (selectedValue instanceof Boolean) {
	    if (((Boolean)selectedValue).booleanValue()) {
		sb.append("+");
	    }
	}
	if (!enabled) { sb.append(")");	}
	Object nameValue = getValue(javax.swing.Action.NAME); // [getName()].Action.text
	if (nameValue instanceof String) {
	    sb.append("\"");
	    sb.append((String)nameValue);
	    sb.append("\"");
	}
	proxy = getProxy();
	if (proxy != null) {
	    sb.append(" Proxy for: ");
	    sb.append(proxy.toString());
	}
	return sb.toString();
    }
}

