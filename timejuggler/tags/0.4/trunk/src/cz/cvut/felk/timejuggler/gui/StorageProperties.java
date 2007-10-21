package cz.cvut.felk.timejuggler.gui;

import cz.cvut.felk.timejuggler.core.MainApp;
import org.jdesktop.application.SessionStorage;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.MultiSplitLayout;

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
                new DefaultPersistenceDelegate(new String[]{"visible"}));
        encoder.setPersistenceDelegate(XMultiSplitPaneState.class,
                new DefaultPersistenceDelegate(new String[]{"model"}));
    }

    public static class XMultipleSplitPaneProperty implements SessionStorage.Property {
        public Object getSessionState(Component c) {
            checkComponent(c);
            final JXMultiSplitPane splitPane = (JXMultiSplitPane) c;
            final MultiSplitLayout.Node model = splitPane.getMultiSplitLayout().getModel();
            return new XMultiSplitPaneState(model);
        }

        public void setSessionState(Component c, Object state) {
            checkComponent(c);
            final JXMultiSplitPane splitPane = (JXMultiSplitPane) c;
            splitPane.getMultiSplitLayout().setFloatingDividers(false);
            splitPane.getMultiSplitLayout().setModel(((XMultiSplitPaneState) state).getModel());
            splitPane.revalidate();
            splitPane.repaint();
        }

        private void checkComponent(Component component) {
            if (component == null) {
                throw new IllegalArgumentException("null component");
            }
            if (!(component instanceof JXMultiSplitPane)) {
                throw new IllegalArgumentException("invalid component - expected JXMultiSplitPane");
            }
        }
    }

    public static class XMultiSplitPaneState {
        MultiSplitLayout.Node model;

        public XMultiSplitPaneState(MultiSplitLayout.Node model) {
            this.model = model;
        }

        public MultiSplitLayout.Node getModel() {
            return model;
        }

        public void setModel(MultiSplitLayout.Node model) {
            this.model = model;
        }
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
