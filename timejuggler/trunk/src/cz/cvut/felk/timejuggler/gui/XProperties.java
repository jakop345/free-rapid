package cz.cvut.felk.timejuggler.gui;

import application.SessionStorage;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;

import java.awt.*;
import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLEncoder;

/**
 * @author Vity
 */
public class XProperties {
    public XProperties() {
    }

    public void registerPersistenceDelegates() {
        XMLEncoder encoder = new XMLEncoder(System.out);
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
            splitPane.getMultiSplitLayout().setModel(((XMultiSplitPaneState) state).getModel());
            splitPane.validate();
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

}