package cz.cvut.felk.timejuggler.core.data;

import com.jgoodies.binding.value.ValueHolder;

import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * @author Vity
 */
public class CurrentDateHolder {
    private ValueHolder holder;

    public CurrentDateHolder() {
        holder = new ValueHolder(new Date());
    }

    public void setValue(Object newValue) {
        holder.setValue(newValue);
    }

    public void addValueChangeListener(PropertyChangeListener l) {
        holder.addValueChangeListener(l);
    }

    public void removeValueChangeListener(PropertyChangeListener l) {
        holder.removeValueChangeListener(l);
    }
}
