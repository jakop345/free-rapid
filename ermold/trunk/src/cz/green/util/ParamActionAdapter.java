package cz.green.util;

import java.awt.event.ActionEvent;

/**
 * This type was created by Jiri Mares.
 */
public class ParamActionAdapter extends ActionAdapter {
    private Object param = null;
    private Class paramType = null;

    /**
     * ParamActionAdopter constructor comment.
     *
     * @param destination java.lang.Object
     * @param handlerName java.lang.String
     * @throws java.lang.NoSuchMethodException
     *          The exception description.
     */
    public ParamActionAdapter(Object destination, String handlerName, Object param, Class paramType) throws NoSuchMethodException {
        super(destination);
        this.param = param;
        Class cls = destination.getClass();
        Class[] paramTypes = {this.paramType = paramType};
        if (paramType == null) {
            paramTypes = null;
            param = null;
        }
        while (true) {
            try {
                //try to find handler
                this.handler = cls.getMethod(handlerName, paramTypes);
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
        Object[] params = {param};
        try {
            if (paramType == null)
                params = null;
            handler.invoke(destination, params);
        } catch (Throwable ex) {
            System.out.println(ex);
        }
    }
}
