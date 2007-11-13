package net.wordrider.gui;

/**
 * @author Vity
 */
public final class LaF {
    private final String className;
    private final String name;
    private final String themeClass;
    private boolean toolbarOpaque = true;

    public LaF(final String className, final String name, final String themeClass, final boolean toolbarOpaque) {
        this.className = className;
        this.name = name;
        this.themeClass = themeClass;
        this.toolbarOpaque = toolbarOpaque;
    }

    public final String getClassName() {
        return className;
    }

    public final String getName() {
        return name;
    }

    public final String getThemeClass() {
        return themeClass;
    }

    public final boolean isToolbarOpaque() {
        return toolbarOpaque;
    }

    public final boolean equals(final Object obj) {
        return obj instanceof LaF && equals((LaF) obj);
    }

    public final boolean equals(final LaF obj) {
        if (className.equals(obj.getClassName()))
            if (themeClass != null)
                return themeClass.equals(obj.getThemeClass());
            else
                return (obj.getThemeClass() == null);
        return false;
    }

    public final String toString() {
        return this.getName();
    }
}
