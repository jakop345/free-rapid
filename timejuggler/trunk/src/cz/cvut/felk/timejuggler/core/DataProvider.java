/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core;

import cz.cvut.felk.timejuggler.db.entity.Category;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


/**
 * Hlavni trida poskytujici Stara se o pristup o ziskavani dat z DB a cachovani
 */
public class DataProvider {

    private static DataProvider ourInstance = new DataProvider();
    List<Category> categories;


    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
        categories = Arrays.asList(new Category("Birthday", Color.YELLOW), new Category("Annivesary", Color.BLUE), new Category("Annivesary", Color.BLUE));
    }

    public List<Category> getCategories() {
        return categories;
    }
}
