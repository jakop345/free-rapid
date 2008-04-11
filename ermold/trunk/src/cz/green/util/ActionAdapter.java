package cz.green.util;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

/**
 * This type was created by Jiri Mares.
 */
public class ActionAdapter implements java.awt.event.ActionListener {
    Object destination = null;
    Method handler = null;

    /**
     * MenuActionAdaptor constructor comment.
     */
    ActionAdapter(Object destination) {
        this.destination = destination;
    }

    /**
     * MenuActionAdaptor constructor comment.
     */
    public ActionAdapter(Object destination, String handlerName) throws NoSuchMethodException {
        this.destination = destination;
        Class cls = destination.getClass();
        while (true) {
            try {
                //try to find handler
                this.handler = cls.getMethod(handlerName, null);
                return;
            } catch (NoSuchMethodException e) {
                //handler doesn't exist, try to look in super class
                if ((cls = cls.getSuperclass()) == null)
                    throw e;
            }
        }
    }

    /**
     * actionPerformed method comment.
     */
    public void actionPerformed(ActionEvent e) {
        try {
            handler.invoke(destination, null);
        } catch (Throwable ex) {
        }
    }

    /**
     * Returns a String that represents the value of this object.
     *
     * @return a string representation of the receiver
     */
    public String toString() {
        StringBuffer message = new StringBuffer(getClass().getName());
        message.append("[").append(destination.getClass().getName()).append("->");
        message.append(handler.getName());
        message.append("]@").append(Integer.toHexString(hashCode()));
        return new String(message);
    }
}
