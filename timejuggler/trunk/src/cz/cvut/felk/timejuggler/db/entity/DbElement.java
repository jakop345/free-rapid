package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;
import cz.cvut.felk.timejuggler.db.entity.interfaces.EntityElement;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 14-IV-2007 15:35:01
 */
public abstract class DbElement implements EntityElement {

    private int id = -1;
    private boolean changed = false;


    public DbElement() {

    }

    public DbElement(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int newVal) {
        id = newVal;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbElement dbElement = (DbElement) o;

        return id == dbElement.id;

    }

    /**
     * Metoda testuje zda je element obsazen v databazi
     * @return vraci true pokud jiz v databazi je
     */
    public boolean isAssigned() {
        return getId() > 0;
    }


    public int hashCode() {
        return id;
    }

    public abstract void store();

    public void saveOrUpdate(TimeJugglerJDBCTemplate template) {
        setChanged(false);
    }

}