/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package application;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;


/**
 * The application's {@code ActionManager} provides read-only cached access to {@code ActionMaps} that contain one entry
 * for each method marked with the {@code @Action} annotation in a class.
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ApplicationContext#getActionMap(Object)
 * @see ApplicationActionMap
 * @see ApplicationAction
 */
public class ActionManager extends AbstractBean {
    private static final Logger logger = Logger.getLogger(ActionManager.class.getName());
    private final WeakHashMap<Object, WeakReference<ApplicationActionMap>> actionMaps;
    private ApplicationActionMap globalActionMap = null;

    public ActionManager() {
        actionMaps = new WeakHashMap<Object, WeakReference<ApplicationActionMap>>();
    }

    /* Just constructs an instance of ApplicationActionMap with the specified 
     * parent and with the ResourceMap for actionsClass.
     */
    private ApplicationActionMap createApplicationActionMap(
            Class resourcesClass, Class actionsClass, Object actionsObject, ApplicationActionMap parent) {
        if (actionsClass == null) {
            throw new IllegalArgumentException("null actionsClass");
        }
        ApplicationContext ac = ApplicationContext.getInstance();
        ResourceMap resourceMap = ac.getResourceMap(resourcesClass);
        ApplicationActionMap appAM = new ApplicationActionMap(actionsClass, actionsObject, resourceMap);
        appAM.setParent(parent);
        return appAM;
    }

    /**
     * The {@code ActionMap} chain for the entire {@code Application}.
     * <p/>
     * Returns an {@code ActionMap} with the {@code @Actions} defined in the application's {@code Application} subclass,
     * i.e. the the value of:
     * <pre>
     * ApplicationContext.getInstance().getApplicationClass()
     * </pre>
     * The remainder of the chain contains one {@code ActionMap} for each superclass, up to {@code Application.class}.
     * The {@code ActionMap.get()} method searches the entire chain, so logically, the {@code ActionMap} that this
     * method returns contains all of the application-global actions.
     * <p/>
     * The value returned by this method is cached.
     * @return the {@code ActionMap} chain for the entire {@code Application}.
     * @see #getActionMap(Class,Object)
     * @see ApplicationContext#getActionMap()
     * @see ApplicationContext#getActionMap(Class,Object)
     * @see ApplicationContext#getActionMap(Object)
     */
    public ApplicationActionMap getActionMap() {
        if (globalActionMap == null) {
            ApplicationContext ac = ApplicationContext.getInstance();
            Object appObject = ac.getApplication();
            Class appClass = ac.getApplicationClass();
            List<Class> appClasses = ac.getApplicationClasses(appClass);
            Collections.reverse(appClasses);
            ApplicationActionMap parent = null;
            for (Class cls : appClasses) {
                /* The ResourceMap used to lookup Action resources for
             * each of the ApplicationActionMaps in the chain is
             * always the same: it's the one defined for the
             * application's class (appClass).  That way, resources
             * defined for appClass always shadow the ones defined
             * for the parent application superclasses.
             */
                parent = createApplicationActionMap(appClass, cls, appObject, parent);
            }
            globalActionMap = parent;
            initProxyActionSupport();  // lazy initialization
        }
        return globalActionMap;
    }

    private void initProxyActionSupport() {
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addPropertyChangeListener(new KeyboardFocusPCL());
    }

    /**
     * Returns the {@code ApplicationActionMap} chain for the specified actions class and target object.
     * <p/>
     * The specified class can contain methods marked with the {@code @Action} annotation.  Each one will be turned into
     * an {@link ApplicationAction ApplicationAction} object and all of them will be added to a single {@link
     * ApplicationActionMap ApplicationActionMap}.  All of the {@code ApplicationActions} invoke their {@code
     * actionPerformed} method on the specified {@code actionsObject}. The parent of the returned {@code ActionMap} is
     * the global {@code ActionMap} that contains the {@code @Actions} defined in this application's {@code Application}
     * subclass.
     * <p/>
     * <p/>
     * To bind an {@code @Action} to a Swing component, one specifies the {@code @Action's} name in an expression like
     * this:
     * <pre>
     * ApplicationContext ac = ApplicationContext.getInstance();
     * MyActions myActions = new MyActions();
     * myComponent.setAction(ac.getActionMap(myActions).get("myAction"));
     * </pre>
     * <p/>
     * <p/>
     * The value returned by this method is cached.  The lifetime of the cached entry will be the same as the lifetime
     * of the {@code actionsObject} and the {@code ApplicationActionMap} and {@code ApplicationActions} that refer to
     * it.  In other words, if you drop all references to the {@code actionsObject}, including its {@code
     * ApplicationActions} and their {@code ApplicationActionMaps}, then the cached {@code ActionMap} entry will be
     * cleared.
     * @return the {@code ApplicationActionMap} for {@code actionsClass} and {@code actionsObject}
     * @see #getActionMap()
     * @see ApplicationContext#getActionMap()
     * @see ApplicationContext#getActionMap(Class,Object)
     * @see ApplicationContext#getActionMap(Object)
     */
    public ApplicationActionMap getActionMap(Class actionsClass, Object actionsObject) {
        if (actionsClass == null) {
            throw new IllegalArgumentException("null actionsClass");
        }
        synchronized (actionMaps) {
            WeakReference<ApplicationActionMap> ref = actionMaps.get(actionsObject);
            ApplicationActionMap classActionMap = (ref != null) ? ref.get() : null;
            if (classActionMap == null) {
                classActionMap = createApplicationActionMap(actionsClass, actionsClass, actionsObject, getActionMap());
                actionMaps.put(actionsObject, new WeakReference(classActionMap));
            }
            return classActionMap;
        }
    }

    private final class KeyboardFocusPCL implements PropertyChangeListener {
        private final TextActions textActions;

        KeyboardFocusPCL() {
            textActions = new TextActions();
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName() == "permanentFocusOwner") {
                ApplicationContext ac = ApplicationContext.getInstance();
                JComponent oldOwner = ac.getFocusOwner();
                Object newValue = e.getNewValue();
                JComponent newOwner = (newValue instanceof JComponent) ? (JComponent) newValue : null;
                textActions.updateFocusOwner(oldOwner, newOwner);
                ac.setFocusOwner(newOwner);
                updateAllProxyActions(oldOwner, newOwner);
            }
        }
    }

    /* For each proxyAction in each ApplicationActionMap, if
     * the newFocusOwner's ActionMap includes an Action with the same
     * name then bind the proxyAction to it, otherwise set the proxyAction's 
     * proxyBinding to null.  [TBD: synchronize access to actionMaps]
     */
    private void updateAllProxyActions(JComponent oldFocusOwner, JComponent newFocusOwner) {
        if (newFocusOwner != null) {
            ActionMap ownerActionMap = newFocusOwner.getActionMap();
            if (ownerActionMap != null) {
                updateProxyActions(getActionMap(), ownerActionMap, newFocusOwner);
                for (WeakReference<ApplicationActionMap> appAMRef : actionMaps.values()) {
                    ApplicationActionMap appAM = appAMRef.get();
                    if (appAM == null) {
                        continue;
                    }
                    updateProxyActions(appAM, ownerActionMap, newFocusOwner);
                }
            }
        }
    }

    /* For each proxyAction in appAM: if there's an action with the same
     * name in the focusOwner's ActionMap, then set the proxyAction's proxy
     * to the matching Action.  In other words: calls to the proxyAction
     * (actionPerformed) will delegate to the matching Action.
     */
    private void updateProxyActions(ApplicationActionMap appAM, ActionMap ownerActionMap, JComponent focusOwner) {
        for (ApplicationAction proxyAction : appAM.getProxyActions()) {
            String proxyActionName = proxyAction.getName();
            javax.swing.Action proxy = ownerActionMap.get(proxyActionName);
            if (proxy != null) {
                proxyAction.setProxy(proxy);
                proxyAction.setProxySource(focusOwner);
            } else {
                proxyAction.setProxy(null);
                proxyAction.setProxySource(null);
            }
        }
    }
}

