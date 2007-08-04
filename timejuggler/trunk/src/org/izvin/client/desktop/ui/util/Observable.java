/*
 * Filename.......: Observable.java
 * Project........: org.izvin.client.desktop.ui.util
 * Last modified..: $Date: 2006/09/27 13:47:42 $
 * CVS revision...: $Revision: 1.1 $
 * Build name.....: $Name$
 * Author.........: Xavier Hanin
 * Created date...: 27. září 2006, 0:48, GMT +1
 */

package org.izvin.client.desktop.ui.util;

import java.beans.PropertyChangeListener;

/**
 * @author Xavier Hanin
 */
public interface Observable {

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    public PropertyChangeListener[] getPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName);

    public boolean hasListeners(String propertyName);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}