package org.izvin.client.desktop.ui.util;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

@Deprecated
public class UIBeanEnhancer {
    //  private final static Logger logger = Logger.getLogger(UIBeanEnhancer.class.getName());
    /**
     * Enhance the given object to transform its properties in bound properties, i.e. properties firing
     * PropertyChangeEvent when they change (through a call to any setter).
     * <p/>
     * Creates a proxy in front of the given object, which is an object of a subclass of the class of the given object,
     * implementing Observable.
     * <p/>
     * This proxy fires property change events each time a setter is called.
     * <p/>
     * All calls except Observable ones are propagated to the given object.
     * <p/>
     * This is very useful with Bean Bindings framework, for instance jgoodies binding.
     * <p/>
     * You can then use a simple javabean in which there is no property change support, like this: new
     * PresentationModel(UIBeanEnhancer.enhance(mybean));
     * @param bean the object to enhance
     * @return the enhanced object
     */
    public static <C> C enhance(C bean) {
        if (bean instanceof Observable) {
            //  logger.log(Level.INFO, "Bean " + bean + " was already enhanced to Observable");
            return bean;
        }
        UIBeanInterceptor interceptor = new UIBeanInterceptor(bean);
        Object p = Enhancer.create(bean.getClass(), new Class[]{Observable.class}, interceptor);
        interceptor.setProxy(p);
        return (C) p;
    }


    private static class UIBeanInterceptor implements MethodInterceptor {
        private static Collection<String> OBSERVABLE_METHODS = new ArrayList<String>();

        static {
            Method[] m = Observable.class.getMethods();

            for (Method aM : m) {
                OBSERVABLE_METHODS.add(getSignatureAsString(aM));
            }
        }

        private PropertyChangeSupport support;
        private Object target;
        private Object proxy;

        private UIBeanInterceptor(Object target) {
            this.target = target;
        }

        private static String getSignatureAsString(Method method) {
            StringBuffer sb = new StringBuffer(method.getName());
            sb.append("(");
            Class[] params = method.getParameterTypes();
            for (Class p : params) {
                sb.append(p.getName()).append(",");
            }
            sb.append(")");
            return sb.toString();
        }

        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            Object retValFromSuper = null;
            String name = method.getName();
            boolean isSetter = name.startsWith("set") &&
                    args.length == 1 &&
                    method.getReturnType() == Void.TYPE;
            Object old = null;
            String propName = null;
            if (isSetter) {
                String propStr = name.substring("set".length());
                String getterName = "get" + propStr;

                Method getter = getGetter(getterName);
                if (getter != null) {
                    old = getter.invoke(target, new Object[0]);
                }

                char prop[] = propStr.toCharArray();
                prop[0] = Character.toLowerCase(prop[0]);
                propName = new String(prop);
            }
            try {
                if (!Modifier.isAbstract(method.getModifiers())) {
                    retValFromSuper = method.invoke(target, args);
                }
            } finally {
                if (Modifier.isAbstract(method.getModifiers())
                        && OBSERVABLE_METHODS.contains(getSignatureAsString(method))) {
                    Method m = UIBeanInterceptor.class.getDeclaredMethod(name, method.getParameterTypes());
                    return m.invoke(this, args);
                } else if (isSetter) {
                    firePropertyChange(new String(propName), old, args[0]);
                }
            }
            return retValFromSuper;
        }

        private Method getGetter(String getterName) throws NoSuchMethodException {
            Class cl = target.getClass();
            Method g = null;
            while (g == null && cl != null) {
                try {
                    g = cl.getDeclaredMethod(getterName, new Class[0]);
                } catch (Exception ex) {
                } finally {
                    cl = cl.getSuperclass();
                }
            }
            return g;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            support.addPropertyChangeListener(propertyName, listener);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
            support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
            support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
            support.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public void firePropertyChange(PropertyChangeEvent evt) {
            support.firePropertyChange(evt);
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
            support.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(String propertyName, int oldValue, int newValue) {
            support.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            support.firePropertyChange(propertyName, oldValue, newValue);
        }

        public PropertyChangeListener[] getPropertyChangeListeners() {
            return support.getPropertyChangeListeners();
        }

        public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
            return support.getPropertyChangeListeners(propertyName);
        }

        public boolean hasListeners(String propertyName) {
            return support.hasListeners(propertyName);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            support.removePropertyChangeListener(propertyName, listener);
        }

        public Object getProxy() {
            return proxy;
        }

        public void setProxy(Object proxy) {
            this.proxy = proxy;
            support = new PropertyChangeSupport(proxy);
        }
    }

    public static void main(String[] args) {
        final TestInterface testClass = new TestClass();
        UIBeanEnhancer.enhance(testClass);
    }

    private interface TestInterface {
        void setmethod(int xxxx);

        int getmethod();
    }


    private static class TestClass implements TestInterface {
        public TestClass() {
        }

        public void setmethod(int xxxx) {

        }

        public int getmethod() {
            return 0;
        }
    }
}