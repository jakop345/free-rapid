package cz.omnicom.ermodeller.conceptual;

/**
 * The bean information class for cz.omnicom.ermodeller.conceptual.Cardinality.
 */
public class CardinalityBeanInfo extends java.beans.SimpleBeanInfo {
    /**
     * Gets the arbitrary property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor arbitraryPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the arbitrary property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getArbitrary", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getArbitrary", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            boolean.class
                    };
                    aSetMethod = getBeanClass().getMethod("setArbitrary", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setArbitrary", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("2arbitrary"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("2arbitrary"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            aDescriptor.setPropertyEditorClass(cz.omnicom.ermodeller.conceptual.BooleanEditor.class);
            aDescriptor.setDisplayName("Mandatory");
            aDescriptor.setShortDescription("Connected entity should be always in relationship (parciality of connection).");
            /* aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
            /* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
        } catch (Throwable exception) {
            handleException(exception);
        }

        return aDescriptor;
    }

    /**
     * Gets the entity property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor entityPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the entity property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getEntity", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getEntity", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            cz.omnicom.ermodeller.conceptual.Entity.class
                    };
                    aSetMethod = getBeanClass().getMethod("setEntity", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setEntity", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("entity"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("entity"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            /* aDescriptor.setDisplayName("entity"); */
            /* aDescriptor.setShortDescription("entity"); */
            /* aDescriptor.setExpert(false); */
            aDescriptor.setHidden(true);
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
            aDescriptor.setValue("ivjDesignTimeProperty", Boolean.FALSE);
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }

    /**
     * Find the method by comparing (name & parameter size) against the methods in the class.
     *
     * @param aClass         java.lang.Class
     * @param methodName     java.lang.String
     * @param parameterCount int
     * @return java.lang.reflect.Method
     */
    public static java.lang.reflect.Method findMethod(java.lang.Class aClass, java.lang.String methodName, int parameterCount) {
        try {
            /* Since this method attempts to find a method by getting all methods from the class,
       this method should only be called if getMethod cannot find the method. */
            java.lang.reflect.Method methods[] = aClass.getMethods();
            for (int index = 0; index < methods.length; index++) {
                java.lang.reflect.Method method = methods[index];
                if ((method.getParameterTypes().length == parameterCount) && (method.getName().equals(methodName))) {
                    return method;
                }
            }
        } catch (java.lang.Throwable exception) {
            return null;
        }
        return null;
    }

    /**
     * Returns the BeanInfo of the superclass of this bean to inherit its features.
     *
     * @return java.beans.BeanInfo[]
     */
    public java.beans.BeanInfo[] getAdditionalBeanInfo() {
        java.lang.Class superClass;
        java.beans.BeanInfo superBeanInfo = null;

        try {
            superClass = getBeanDescriptor().getBeanClass().getSuperclass();
        } catch (java.lang.Throwable exception) {
            return null;
        }

        try {
            superBeanInfo = java.beans.Introspector.getBeanInfo(superClass);
        } catch (java.beans.IntrospectionException ie) {
        }

        if (superBeanInfo != null) {
            java.beans.BeanInfo[] ret = new java.beans.BeanInfo[1];
            ret[0] = superBeanInfo;
            return ret;
        }
        return null;
    }

    /**
     * Gets the bean class.
     *
     * @return java.lang.Class
     */
    public static java.lang.Class getBeanClass() {
        return cz.omnicom.ermodeller.conceptual.Cardinality.class;
    }

    /**
     * Gets the bean class name.
     *
     * @return java.lang.String
     */
    public static java.lang.String getBeanClassName() {
        return cz.omnicom.ermodeller.conceptual.Cardinality.class.getName();
    }

    public java.beans.BeanDescriptor getBeanDescriptor() {
        java.beans.BeanDescriptor aDescriptor = null;
        try {
            /* Create and return the CardinalityBeanInfo bean descriptor. */
            aDescriptor = new java.beans.BeanDescriptor(cz.omnicom.ermodeller.conceptual.Cardinality.class, cz.omnicom.ermodeller.conceptual.CardinalityCustomizer.class);
            /* aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
        } catch (Throwable exception) {
        }
        return aDescriptor;
    }

    /**
     * Return the event set descriptors for this bean.
     *
     * @return java.beans.EventSetDescriptor[]
     */
    public java.beans.EventSetDescriptor[] getEventSetDescriptors() {
        try {
            java.beans.EventSetDescriptor aDescriptorList[] = {
            };
            return aDescriptorList;
        } catch (Throwable exception) {
            handleException(exception);
        }
        return null;
    }

    /**
     * Return the method descriptors for this bean.
     *
     * @return java.beans.MethodDescriptor[]
     */
    public java.beans.MethodDescriptor[] getMethodDescriptors() {
        try {
            java.beans.MethodDescriptor aDescriptorList[] = {
            };
            return aDescriptorList;
        } catch (Throwable exception) {
            handleException(exception);
        }
        return null;
    }

    /**
     * Return the property descriptors for this bean.
     *
     * @return java.beans.PropertyDescriptor[]
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
        try {
            java.beans.PropertyDescriptor aDescriptorList[] = {
                    arbitraryPropertyDescriptor()
                    , entityPropertyDescriptor()
                    , multiCardinalityPropertyDescriptor()
                    , relationPropertyDescriptor()
            };
            return aDescriptorList;
        } catch (Throwable exception) {
            handleException(exception);
        }
        return null;
    }

    /**
     * Called whenever the bean information class throws an exception.
     *
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Gets the multiCardinality property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor multiCardinalityPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the multiCardinality property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getMultiCardinality", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getMultiCardinality", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            boolean.class
                    };
                    aSetMethod = getBeanClass().getMethod("setMultiCardinality", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setMultiCardinality", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("3multiCardinality"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("3multiCardinality"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            aDescriptor.setPropertyEditorClass(cz.omnicom.ermodeller.conceptual.MultiCardinalityEditor.class);
            aDescriptor.setDisplayName("Arity");
            aDescriptor.setShortDescription("Cardinality of connection entity to relationship.");
            /* aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
            /* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }

    /**
     * Gets the relation property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor relationPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the relation property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getRelation", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getRelation", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            RelationBean.class
                    };
                    aSetMethod = getBeanClass().getMethod("setRelation", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setRelation", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("relation"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("relation"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            /* aDescriptor.setDisplayName("relation"); */
            /* aDescriptor.setShortDescription("relation"); */
            /* aDescriptor.setExpert(false); */
            aDescriptor.setHidden(true);
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
            aDescriptor.setValue("ivjDesignTimeProperty", Boolean.FALSE);
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }
}
