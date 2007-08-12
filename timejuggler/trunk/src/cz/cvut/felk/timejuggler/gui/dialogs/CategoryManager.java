package cz.cvut.felk.timejuggler.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.entity.Category;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;

import javax.swing.*;
import java.util.List;

/**
 * @author Vity
 */
public class CategoryManager {
    /**
     * Holds the List of Categories. Categories are added and removed from this List. The ObservableList implements
     * ListModel, and so, we can directly use this List for the UI and can observe changes.<p>
     * <p/>
     * In a real world application this List may be kept in synch with a database.
     */
    private final ArrayListModel<CategoryEntity> managedCategories;

    // Instance Creation ******************************************************

    /**
     * Constructs a CategoryManager for the given list of Categories.
     */
    public CategoryManager(List<CategoryEntity> categories, final boolean cloneObjects) {
        if (cloneObjects) {
            for (CategoryEntity categoryEntity : categories) {

            }
        }
        this.managedCategories = new ArrayListModel<CategoryEntity>(categories);
    }

    // Exposing the ListModel of Categories ****************************************

    public ListModel getManagedCategories() {
        return managedCategories;
    }

    // Managing Categories *********************************************************

    /**
     * Creates and return a new Category.
     * @return the new Category
     */
    public CategoryEntity createItem() {
        return new Category();
    }


    /**
     * Adds the given Category to the List of managed Categories and notifies observers of the managed Categories
     * ListModel about the change.
     * @param CategoryToAdd the Category to add
     */
    public void addItem(Category categoryToAdd) {
        managedCategories.add(categoryToAdd);
    }


    /**
     * Removes the given Category from the List of managed Categories and notifies observers of the managed
     * Categories ListModel about the change.
     * @param CategoryToRemove the Category to remove
     */
    public void removeItem(CategoryEntity categoryToRemoveEntity) {
        managedCategories.remove(categoryToRemoveEntity);
    }

}
