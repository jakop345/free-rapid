package net.wordrider.dialogs.settings;

import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.util.*;

/**
 * @author Vity
 */
final class OptionsGroupManager implements IOptionsManager {
    private final Set<IOptionable> changedComponents = new LinkedHashSet<IOptionable>(2);
    //    private final List registeredComponents = new ArrayList(5);
    private final List<IOptionable> applyedComponents = new ArrayList<IOptionable>(2);
    private final SettingsDialog dialog;

    public OptionsGroupManager(final SettingsDialog dialog) {
        super();    //call to super
        this.dialog = dialog;
    }

    //  public final void registerOptionable(final Optionable optionable) {
    //      registeredComponents.add(optionable);
//    }

    public final void makeChange(final IOptionable optionable) {
        if (optionable.wasChanged()) {
            changedComponents.add(optionable);
            setEnableApplyButton(true);

        } else {
            changedComponents.remove(optionable);
            if (!wasChanged())
                setEnableApplyButton(false);
        }
    }

    public final void applyChanges() {
        if (wasChanged()) {
            final Set<IOptionGroup> groups = new LinkedHashSet<IOptionGroup>(2);
            Set<IOptionable> changed = new HashSet<IOptionable>();
            changed.addAll(changedComponents);
            for (IOptionable changedComponent : changed) {
                groups.add(changedComponent.getOptionsGroup());
                changedComponent.applyChange();
            }
            for (IOptionGroup group : groups) {
                (group).doGroupChange();
            }
            applyedComponents.addAll(changed);
            changedComponents.clear();
            setEnableApplyButton(false);
            Swinger.inputFocus(dialog.getButtonPanel().getOkButton());
        }
    }

    public final void restoreChanged() {
        IOptionable optionable;
        Set<IOptionable> changed = new HashSet<IOptionable>();
        changed.addAll(changedComponents);
        for (IOptionable changedComponent : changed) {
            optionable = changedComponent;
            optionable.restorePrevious();
        }
        final Set<IOptionGroup> groups = new LinkedHashSet<IOptionGroup>(2);
        for (IOptionable applyedComponent : applyedComponents) {
            optionable = applyedComponent;
            optionable.restorePrevious();
            groups.add(optionable.getOptionsGroup());
            optionable.applyChange();
        }
        for (IOptionGroup group : groups) {
            (group).doGroupChange();
        }
        applyedComponents.clear();
    }

    public final void resetChanges() {
        applyedComponents.clear();
        changedComponents.clear();
        setEnableApplyButton(false);
    }

    public final boolean wasChanged() {
        return !changedComponents.isEmpty();
    }

    private void setEnableButton(final AbstractButton button, final boolean enable) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                button.setEnabled(enable);
            }
        });
    }

    private void setEnableApplyButton(final boolean enable) {
        setEnableButton(dialog.getButtonPanel().getApplyButton(), enable);
    }
}
