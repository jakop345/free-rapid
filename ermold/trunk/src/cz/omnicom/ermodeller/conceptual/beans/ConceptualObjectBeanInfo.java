package cz.omnicom.ermodeller.conceptual.beans;

import java.lang.reflect.Method;

/**
 * The bean information class for cz.omnicom.ermodeller.conceptual.ConceptualObject.
 */
public class ConceptualObjectBeanInfo extends java.beans.SimpleBeanInfo {
    /**
     * Gets the comment property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor commentPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the comment property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getComment", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getComment", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            java.lang.String.class
                    };
                    aSetMethod = getBeanClass().getMethod("setComment", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setComment", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("9comment"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("9comment"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            aDescriptor.setDisplayName("Comment");
            aDescriptor.setShortDescription("Object's comment.");
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
            for (Method method : methods) {
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
        return ConceptualObject.class;
    }

    /**
     * Gets the bean class name.
     *
     * @return java.lang.String
     */
    public static java.lang.String getBeanClassName() {
        return ConceptualObject.class.getName();
    }

    public java.beans.BeanDescriptor getBeanDescriptor() {
        java.beans.BeanDescriptor aDescriptor = null;
        try {
            /* Create and return the ConceptualObjectBeanInfo bean descriptor. */
            aDescriptor = new java.beans.BeanDescriptor(ConceptualObject.class);
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
                    propertyChangeEventSetDescriptor()
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
                    commentPropertyDescriptor()
                    , namePropertyDescriptor()
                    , schemaPropertyDescriptor()
                    , idPropertyDescriptor()
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
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Gets the name property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor idPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the name property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getID", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getID", 0);
                }
                java.lang.reflect.Method aSetMethod = null;
                aDescriptor = new java.beans.PropertyDescriptor("id"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("id"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            aDescriptor.setDisplayName("ID");
            aDescriptor.setShortDescription("Object's id.");
            /*aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
            /* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }

    /**
     * Gets the name property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor namePropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the name property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getName", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getName", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            java.lang.String.class
                    };
                    aSetMethod = getBeanClass().getMethod("setName", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setName", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("1name"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("1name"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            aDescriptor.setDisplayName("Name");
            aDescriptor.setShortDescription("Object's name.");
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
     * Gets the propertyChange event set descriptor.
     *
     * @return java.beans.EventSetDescriptor
     */
    public java.beans.EventSetDescriptor propertyChangeEventSetDescriptor() {
        java.beans.EventSetDescriptor aDescriptor = null;
        try {
            try {
                /* Try using method descriptors to create the propertyChange event set descriptor. */
                java.beans.MethodDescriptor eventMethodDescriptors[] = {
                        propertyChangepropertyChange_javabeansPropertyChangeEventMethodEventDescriptor()};
                java.lang.reflect.Method anAddMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class anAddMethodParameterTypes[] = {
                            java.beans.PropertyChangeListener.class
                    };
                    anAddMethod = getBeanClass().getMethod("addPropertyChangeListener", anAddMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    anAddMethod = findMethod(getBeanClass(), "addPropertyChangeListener", 1);
                }
                java.lang.reflect.Method aRemoveMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aRemoveMethodParameterTypes[] = {
                            java.beans.PropertyChangeListener.class
                    };
                    aRemoveMethod = getBeanClass().getMethod("removePropertyChangeListener", aRemoveMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aRemoveMethod = findMethod(getBeanClass(), "removePropertyChangeListener", 1);
                }
                aDescriptor = new java.beans.EventSetDescriptor(
                        "propertyChange",
                        java.beans.PropertyChangeListener.class,
                        eventMethodDescriptors, anAddMethod, aRemoveMethod);
            } catch (Throwable exception) {
                /* Using method descriptors failed, try using the methods names. */
                handleException(exception);
                java.lang.String eventMethodNames[] = {
                        "propertyChange"};
                aDescriptor = new java.beans.EventSetDescriptor(getBeanClass(),
                        "propertyChange",
                        java.beans.PropertyChangeListener.class,
                        eventMethodNames,
                        "addPropertyChangeListener",
                        "removePropertyChangeListener");
            }
            /* aDescriptor.setUnicast(false); */
            /* aDescriptor.setDisplayName("propertyChange"); */
            /* aDescriptor.setShortDescription("propertyChange"); */
            /* aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }

    /**
     * Gets the propertyChange.propertyChange(java.beans.PropertyChangeEvent) method descriptor.
     *
     * @return java.beans.MethodDescriptor
     */
    public java.beans.MethodDescriptor propertyChangepropertyChange_javabeansPropertyChangeEventMethodEventDescriptor() {
        java.beans.MethodDescriptor aDescriptor = null;
        try {
            /* Create and return the propertyChange.propertyChange(java.beans.PropertyChangeEvent) method descriptor. */
            java.lang.reflect.Method aMethod;
            try {
                /* Attempt to find the method using getMethod with parameter types. */
                java.lang.Class aParameterTypes[] = {
                        java.beans.PropertyChangeEvent.class
                };
                aMethod = (java.beans.PropertyChangeListener.class).getMethod("propertyChange", aParameterTypes);
            } catch (Throwable exception) {
                /* Since getMethod failed, call findMethod. */
                handleException(exception);
                aMethod = findMethod((java.beans.PropertyChangeListener.class), "propertyChange", 1);
            }
            try {
                /* Try creating the method descriptor with parameter descriptors. */
                java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
                aParameterDescriptor1.setName("arg1");
                aParameterDescriptor1.setDisplayName("evt");
                java.beans.ParameterDescriptor aParameterDescriptors[] = {
                        aParameterDescriptor1
                };
                aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
            } catch (Throwable exception) {
                /* Try creating the method descriptor without parameter descriptors. */
                handleException(exception);
                aDescriptor = new java.beans.MethodDescriptor(aMethod);
            }
            aDescriptor.setDisplayName("propertyChange.propertyChange(java.beans.PropertyChangeEvent)");
            /* aDescriptor.setShortDescription("propertyChange.propertyChange(java.beans.PropertyChangeEvent)"); */
            /* aDescriptor.setExpert(false); */
            /* aDescriptor.setHidden(false); */
            /* aDescriptor.setValue("preferred", new Boolean(false)); */
        } catch (Throwable exception) {
            handleException(exception);
        }
        return aDescriptor;
    }

    /**
     * Gets the schema property descriptor.
     *
     * @return java.beans.PropertyDescriptor
     */
    public java.beans.PropertyDescriptor schemaPropertyDescriptor() {
        java.beans.PropertyDescriptor aDescriptor = null;
        try {
            try {
                /* Using methods via getMethod is the faster way to create the schema property descriptor. */
                java.lang.reflect.Method aGetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aGetMethodParameterTypes[] = {};
                    aGetMethod = getBeanClass().getMethod("getSchema", aGetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aGetMethod = findMethod(getBeanClass(), "getSchema", 0);
                }
                java.lang.reflect.Method aSetMethod;
                try {
                    /* Attempt to find the method using getMethod with parameter types. */
                    java.lang.Class aSetMethodParameterTypes[] = {
                            Schema.class
                    };
                    aSetMethod = getBeanClass().getMethod("setSchema", aSetMethodParameterTypes);
                } catch (Throwable exception) {
                    /* Since getMethod failed, call findMethod. */
                    handleException(exception);
                    aSetMethod = findMethod(getBeanClass(), "setSchema", 1);
                }
                aDescriptor = new java.beans.PropertyDescriptor("schema"
                        , aGetMethod, aSetMethod);
            } catch (Throwable exception) {
                /* Since we failed using methods, try creating a default property descriptor. */
                handleException(exception);
                aDescriptor = new java.beans.PropertyDescriptor("schema"
                        , getBeanClass());
            }
            aDescriptor.setBound(true);
            /* aDescriptor.setConstrained(false); */
            /* aDescriptor.setDisplayName("schema"); */
            /* aDescriptor.setShortDescription("schema"); */
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
