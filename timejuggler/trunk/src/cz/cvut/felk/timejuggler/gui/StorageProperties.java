package cz.cvut.felk.timejuggler.gui;

import application.SessionStorage;
import cz.cvut.felk.timejuggler.core.MainApp;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.awt.*;
import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLEncoder;

/**
 * @author Vity
 */
public class StorageProperties {
    public StorageProperties() {
    }

    public void registerPersistenceDelegates() {
        XMLEncoder encoder = new XMLEncoder(System.out);
        encoder.setPersistenceDelegate(XVisibleState.class,
                new DefaultPersistenceDelegate(new String[] { "visible"}));
    }

    public static class XStatusBarProperty implements SessionStorage.Property {
        public Object getSessionState(Component c) {
            checkComponent(c);
            return new XVisibleState(c.isVisible());
        }

        public void setSessionState(Component c, Object state) {
            checkComponent(c);
            XVisibleState s = ((XVisibleState) state);
            final Action action = MainApp.getAContext().getActionMap().get("showStatusBar");
            action.putValue(Action.SELECTED_KEY, s.getVisible());
           // c.setVisible(s.getVisible());
        }

        private void checkComponent(Component component) {
            if (component == null) {
                throw new IllegalArgumentException("null component");
            }
            if (!(component instanceof JXStatusBar)) {
                throw new IllegalArgumentException("invalid component - expected JXStatusBar");
            }
        }
    }

    public static class XVisibleState {
        private boolean visible;

        public XVisibleState(boolean visible) {
            this.visible = visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean getVisible() {
            return visible;
        }
    }

    public static class JToolbarProperty implements SessionStorage.Property {
        public Object getSessionState(Component c) {
            checkComponent(c);
            return new XVisibleState(c.isVisible());
        }

        public void setSessionState(Component c, Object state) {
            checkComponent(c);
            XVisibleState s = (XVisibleState) state;
            final Action action = MainApp.getAContext().getActionMap().get("showToolbar");
            action.putValue(Action.SELECTED_KEY, s.getVisible());
           // c.setVisible(s.getVisible());
        }

        private void checkComponent(Component component) {
            if (component == null) {
                throw new IllegalArgumentException("null component");
            }
            if (!(component instanceof JToolBar)) {
                throw new IllegalArgumentException("invalid component - expected JToolBar");
            }
        }
    }
}
