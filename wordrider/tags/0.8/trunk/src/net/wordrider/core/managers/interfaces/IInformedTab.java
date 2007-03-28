package net.wordrider.core.managers.interfaces;

import javax.swing.*;

/**
 * @author Vity
 */
public interface IInformedTab {
    public JComponent getComponent();

    public Icon getIcon();

    public String getTip();

    public String getName();

    public String getTabName();

    public void activate();

    public void deactivate();

    public boolean closeSoft() throws Throwable;

    public void closeHard() throws Throwable;
}