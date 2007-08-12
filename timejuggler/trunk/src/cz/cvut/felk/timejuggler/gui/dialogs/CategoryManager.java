package cz.cvut.felk.timejuggler.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import cz.cvut.felk.timejuggler.db.entity.interfaces.CategoryEntity;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
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
    private boolean listChanged = false;

    // Instance Creation ******************************************************

    /**
     * Constructs a CategoryManager for the given list of Categories.
     */
    public CategoryManager(List<CategoryEntity> categories, final boolean cloneObjects) throws CloneNotSupportedException {
        if (cloneObjects) {
            this.managedCategories = new ArrayListModel<CategoryEntity>();
            for (CategoryEntity categoryEntity : categories) {
                this.managedCategories.add((CategoryEntity) categoryEntity.clone());
            }
        } else
            this.managedCategories = new ArrayListModel<CategoryEntity>(categories);
        this.managedCategories.addListDataListener(new CategoryDataListener());//ten ale nehlida obsah bean, jen seznamu
    }

    // Exposing the ListModel of Categories ****************************************

    public ListModel getManagedCategories() {
        return managedCategories;
    }

    public List<CategoryEntity> getCategoriesList() {
        return new ArrayList(managedCategories);
    }

    // Managing Categories *********************************************************

    /**
     * Adds the given CategoryEntity to the List of managed Categories and notifies observers of the managed Categories
     * ListModel about the change.
     * @param CategoryToAdd the CategoryEntityto add
     */
    public void addItem(CategoryEntity categoryToAdd) {
        managedCategories.add(categoryToAdd);
    }


    /**
     * Removes the given CategoryEntityfrom the List of managed Categories and notifies observers of the managed
     * Categories ListModel about the change.
     * @param CategoryToRemove the CategoryEntityto remove
     */
    public void removeItem(CategoryEntity categoryToRemoveEntity) {
        managedCategories.remove(categoryToRemoveEntity);
    }

    public void setListChanged(boolean listChanged) {
        this.listChanged = listChanged;
    }

    private class CategoryDataListener implements ListDataListener {
        public void intervalAdded(ListDataEvent e) {
            contentsChanged(e);
        }

        public void intervalRemoved(ListDataEvent e) {
            contentsChanged(e);
        }

        public void contentsChanged(ListDataEvent e) {
            listChanged = true;
        }
    }

    public boolean isListChanged() {
        return listChanged;
    }
}
