package cz.cvut.felk.timejuggler.db.entity;

import cz.cvut.felk.timejuggler.db.*;
import cz.cvut.felk.timejuggler.db.TimeJugglerJDBCTemplate;

/**
 * @author Jan Struz
 * @version 0.1
 * @created 14-IV-2007 15:35:01
 */
public abstract class DbElement {

    private int id = -1;

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

    public abstract void store();
    
    public abstract void saveOrUpdate(TimeJugglerJDBCTemplate template);

}