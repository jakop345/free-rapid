package cz.vity.freerapid.gui.managers.interfaces;

/**
 * @author Vity
 */
public interface Identifiable<T, B extends ModelWrapper> {
    T getIdentificator();

    B build();
}
