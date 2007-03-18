/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The application's {@code ResourceManager} provides read-only cached access to resources in {@code ResourceBundles}
 * via the {@link ResourceMap ResourceMap} class.  {@code ResourceManager} is a property of the {@code
 * ApplicationContext} and most applications look up resources relative to it, like this:
 * <pre>
 * ApplicationContext appContext = ApplicationContext.getInstance();
 * ResourceMap resourceMap = appContext.getResourceMap(MyClass.class);
 * String msg = resourceMap.getString("msg");
 * Icon icon = resourceMap.getIcon("icon");
 * Color color = resourceMap.getColor("color");
 * </pre>
 * {@link ApplicationContext#getResourceMap(Class) ApplicationContext.getResourceMap()} just delegates to its {@code
 * ResourceManager}.  The {@code ResourceMap} in this example contains resources from the ResourceBundle named {@code
 * MyClass}, its {@link ResourceMap#getParent parent} contains resources shared by {@code MyClass's} package, and the
 * rest of the chain contains resources shared by the entire application.
 * <p/>
 * Resources for a class are defined by an eponymous {@code ResourceBundle} in a {@code resources} subpackage.  The
 * subpackage may also contain a ResourceBundle with resources to be shared by the entire package, called {@code
 * PackageResources}, and the Application class itself may also provide resources. A complete description of the naming
 * conventions for ResourceBundles is provided by the {@link #getResourceMap(Class) getResourceMap()} method.
 * <p/>
 * The mapping from classes, packages, and {@code Application} to a list ResourceBundle names is handled by three
 * protected methods: {@link #getClassBundleNames(Class) getClassBundleNames}, {@link #getPackageBundleNames(Class)
 * getPackageBundleNames}, {@link #getApplicationBundleNames() getApplicationBundleNames}. Subclasses could override
 * these methods to append additional ResourceBundle names to the default lists.
 * @author Hans Muller (Hans.Muller@Sun.COM)
 * @see ApplicationContext#getResourceManager
 * @see ApplicationContext#getResourceMap
 * @see ResourceMap
 */
public class ResourceManager extends AbstractBean {
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
    private final Map<String, ResourceMap> resourceMaps;
    private List<String> applicationBundleNames = null;

    /**
     * Construct a {@code ResourceManager}.  Typically applications will not create a ResourceManager directly, they'll
     * retrieve the shared one from the {@code ApplicationContext} with:
     * <pre>
     * ApplicationContext.getInstance().getResourceManager()
     * </pre>
     * Or just look up {@code ResourceMaps} with the ApplicationContext convenience method:
     * <pre>
     * ApplicationContext.getInstance().getResourceMap(MyClass.class)
     * </pre>
     * @see ApplicationContext#getResourceManager
     * @see ApplicationContext#getResourceMap
     */
    public ResourceManager() {
        resourceMaps = new HashMap<String, ResourceMap>();
    }

    private String bundlePackageName(String bundleName) {
        int i = bundleName.lastIndexOf(".");
        return (i == -1) ? "" : bundleName.substring(0, i);
    }

    private ResourceMap createAppResourceMap(ClassLoader cl, ListIterator<String> names) {
        if (!names.hasNext()) {
            return null;
        } else {
            String bundleName0 = names.next();
            String rmBundlePackage = bundlePackageName(bundleName0);
            List<String> rmNames = new ArrayList<String>();
            rmNames.add(bundleName0);
            ResourceMap parent = createAppResourceMap(cl, names);
            return createResourceMap(cl, parent, rmNames);
        }
    }

    /* Returns the Application ResourceMap chain.  If the
     * Application hasn't been launched yet, i.e. if the 
     * ApplicationContext applicationClass property hasn't 
     * been set yet, then the ResourceMap just
     * corresponds to Application.class.
     */
    private ResourceMap getApplicationResourceMap() {
        List<String> appBundleNames = getApplicationBundleNames();
        String appResourceMapKey = appBundleNames.get(0);
        ResourceMap appResourceMap = resourceMaps.get(appResourceMapKey);
        if (appResourceMap == null) {
            Class appClass = ApplicationContext.getInstance().getApplicationClass();
            if (appClass == null) {
                logger.warning("getApplicationResourceMap(): no Application class");
                appClass = Application.class;
            }
            ClassLoader classLoader = appClass.getClassLoader();
            appResourceMap = createAppResourceMap(classLoader, appBundleNames.listIterator());
            resourceMaps.put(appResourceMapKey, appResourceMap);
        }
        return appResourceMap;
    }

    /* Returns the package ResourceMap for cls, parent is the Application
     * ResourceMap. This method is structurally identical to 
     * getClassResourceMap.
     */
    private ResourceMap getPackageResourceMap(Class cls) {
        List<String> pkgBundleNames = getPackageBundleNames(cls);
        String pkgResourceMapKey = pkgBundleNames.get(0);
        ResourceMap pkgResourceMap = resourceMaps.get(pkgResourceMapKey);
        if (pkgResourceMap == null) {
            ResourceMap appResourceMap = getApplicationResourceMap();
            pkgResourceMap = createResourceMap(cls.getClassLoader(), appResourceMap, pkgBundleNames);
            resourceMaps.put(pkgResourceMapKey, pkgResourceMap);
        }
        return pkgResourceMap;
    }

    /* Returns the cached ResourceMap for this class, or constructs one.
     */
    private ResourceMap getClassResourceMap(Class cls) {
        List<String> classBundleNames = getClassBundleNames(cls);
        String classResourceMapKey = classBundleNames.get(0);
        ResourceMap classResourceMap = resourceMaps.get(classResourceMapKey);
        if (classResourceMap == null) {
            ResourceMap packageResourceMap = getPackageResourceMap(cls);
            classResourceMap = createResourceMap(cls.getClassLoader(), packageResourceMap, classBundleNames);
            resourceMaps.put(classResourceMapKey, classResourceMap);
        }
        return classResourceMap;
    }

    /**
     * Called by {@link #getResourceMap} to lazily construct {@code ResourceMaps}. By default this method is effectively
     * just:
     * <pre>
     * return new ResourceMap(parent, classLoader, bundleNames);
     * </pre>
     * ResourceManager subclasses might override this method to construct their own ResourceMap subclasses instead.
     */
    protected ResourceMap createResourceMap(ClassLoader classLoader, ResourceMap parent, List<String> bundleNames) {
        if (classLoader == null) {
            throw new IllegalArgumentException("null ClassLoader");
        }
        if (bundleNames == null) {
            throw new IllegalArgumentException("null bundleNames List");
        }
        String[] bns = new String[bundleNames.size()];
        return new ResourceMap(parent, classLoader, bundleNames.toArray(bns));
    }

    /**
     * Returns a {@link ResourceMap#getParent chain} of four or more {@code ResourceMaps} beginning with one that
     * encapsulates the {@code ResourceBundles} for the specified class.  Its parent ResourceMap encapsulates the
     * ResourceBundles shared by the class's package, and remaining ResourceMaps in the chain encapsulate the resources
     * that are shared by the entire application.
     * <p/>
     * <p/>
     * The ResourceBundle (names) for the chain of ResourceMaps are defined by three methods: {@link
     * #getClassBundleNames}, {@link #getPackageBundleNames}, {@link #getApplicationBundleNames}.  Collectively they
     * define the standard location for {@code ResourceBundles} for a particular class as the {@code resources}
     * subpackage.  For example, the ResourceBundle for a class {@code com.myco.MyScreen}, would be named {@code
     * com.myco.resources.MyScreen}.  Typical ResourceBundles are ".properties" files, so: {@code
     * com/foo/bar/resources/MyScreen.properties}.  The following table is a comprehensive list of the ResourceMaps and
     * their constituent ResourceBundles for the same example:
     * <p/>
     * <table border="1" cellpadding="4%"> <caption><em>ResourceMap chain for class MyScreen in MyApp</em></caption>
     * <tr> <th></th> <th>ResourceMap</th> <th>ResourceBundle names</th> <th>Typical ResourceBundle files</th> </tr>
     * <tr> <td>1</td> <td>class: com.myco.MyScreen</td> <td>com.myco.resources.MyScreen</td>
     * <td>com/myco/resources/MyScreen.properties</td> </tr> <tr> <td>2</td> <td>package: com.myco</td>
     * <td>com.myco.resources.PackageResources</td> <td>com/myco/resources/PackageResources.properties</td> </tr> <tr>
     * <td>3</td> <td>application: com.myco.MyApp</td> <td>com.myco.resources.MyApp</td>
     * <td>com/myco/resources/MyApp.properties</td> </tr> <tr> <td>4</td> <td>application:
     * javax.swing.application.Application</td> <td>javax.swing.application.resources.Application</td>
     * <td>javax.swing.application.resources.Application.properties</td> </tr> </table>
     * <p/>
     * <p/>
     * None of the ResourceBundles are required to exist.  If more than one ResourceBundle contains a resource with the
     * same name then the one later in the list has precedence
     * <p/>
     * ResourceMaps are constructed lazily and cached.
     * @param cls the class that defines the location of ResourceBundles
     * @return a {@code ResourceMap} that contains resources loaded from {@code ResourceBundles}  found in the resources
     *         subpackage of the specified class's package.
     * @see #getClassBundleNames
     * @see #getPackageBundleNames
     * @see #getApplicationBundleNames
     * @see ResourceMap#getParent
     * @see ResourceMap#getBundleNames
     */
    public ResourceMap getResourceMap(Class cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null class");
        }
        return getClassResourceMap(cls);
    }

    /**
     * Returns the chain of ResourceMaps that's shared by the entire application, beginning with the resource defined
     * for the application's class, i.e. the value of the ApplicationContext {@link
     * ApplicationContext#getApplicationClass applicationClass} property. If the {@code applicationClass} property has
     * not been set, e.g. because the application has not been {@link Application#launch launched} yet, then a
     * ResourceMap for just {@code Application.class} is returned.
     * @return the Application's ResourceMap
     * @see ApplicationContext#getResourceMap()
     * @see ApplicationContext#getApplicationClass
     */
    public ResourceMap getResourceMap() {
        return getApplicationResourceMap();
    }

    private List<Class> getAppClasses(Class appClass) {
        List<Class> appClasses = new ArrayList<Class>();
        Class stopClass = Application.class.getSuperclass();
        for (Class c = appClass; !c.equals(stopClass); c = c.getSuperclass()) {
            appClasses.add(c);
        }
        return appClasses;
    }

    /**
     * The names of the ResourceBundles to be shared by the entire application.  The list is in priority order:
     * resources defined by the first ResourceBundle shadow resources with the the same name that come later.
     * <p/>
     * The default value for this property is a list of {@link #getClassBundleNames per-class} ResourceBundle names,
     * beginning with the {@code Application's} class and of each of its superclasses, up to {@code Application.class}.
     * For example, if the Application's class was {@code com.foo.bar.MyApp}, and MyApp was a subclass of {@code
     * SingleFrameApplication.class}, then the ResourceBundle names would be: <code><ol>
     * <li>com.foo.bar.resources.MyApp</li> <li>javax.swing.application.resources.SingleFrameApplication</li>
     * <li>javax.swing.application.resources.Application</li> </code></ol>
     * <p/>
     * The default value of this property is computed lazily and cached.  If it's reset, then all ResourceMaps cached by
     * {@code getResourceMap} will be updated.
     * @see #setApplicationBundleNames
     * @see #getResourceMap
     * @see #getClassBundleNames
     * @see #getPackageBundleNames
     * @see ApplicationContext#getApplication
     */
    public List<String> getApplicationBundleNames() {
        /* Lazily compute an initial value for this property, unless the
       * application's class hasn't been specified yet.  In that case
       * we just return a placeholder based on Application.class.
       */
        if (applicationBundleNames == null) {
            Class appClass = ApplicationContext.getInstance().getApplicationClass();
            if (appClass == null) {
                return getClassBundleNames(Application.class); // placeholder
            } else {
                List<Class> appClasses = getAppClasses(appClass);
                List<String> bundleNames = new ArrayList<String>(appClasses.size() * 2);
                for (Class cls : appClasses) {
                    bundleNames.addAll(getClassBundleNames(cls));
                }
                applicationBundleNames = Collections.unmodifiableList(bundleNames);
            }
        }
        return applicationBundleNames;
    }

    /**
     * Specify the names of the ResourceBundles to be shared by the entire application.  More information about the
     * property is provided by the {@link #getApplicationBundleNames} property.
     * @see #setApplicationBundleNames
     */
    public void setApplicationBundleNames(List<String> bundleNames) {
        if (bundleNames != null) {
            for (String bundleName : bundleNames) {
                if ((bundleName == null) || (bundleNames.size() == 0)) {
                    throw new IllegalArgumentException("invalid bundle name \"" + bundleName + "\"");
                }
            }
        }
        Object oldValue = applicationBundleNames;
        if (bundleNames != null) {
            applicationBundleNames = Collections.unmodifiableList(new ArrayList(bundleNames));
        } else {
            applicationBundleNames = null;
        }
        resourceMaps.clear();
        firePropertyChange("applicationBundleNames", oldValue, applicationBundleNames);
    }

    /* Convert a class name to an eponymous resource bundle in the 
     * resources subpackage.  For example, given a class named
     * com.foo.bar.MyClass, the ResourceBundle name would be
     * "com.foo.bar.resources.MyClass"  If MyClass is an inner class,
     * only its "simple name" is used.  For example, given an
     * inner class named com.foo.bar.OuterClass$InnerClass, the
     * ResourceBundle name would be "com.foo.bar.resources.InnerClass".
     * Although this could result in a collision, creating more
     * complex rules for inner classes would be a burden for
     * developers.
     */
    private String classBundleBaseName(Class cls) {
        String className = cls.getName();
        StringBuffer sb = new StringBuffer();
        int i = className.lastIndexOf('.');
        if (i > 0) {
            sb.append(className.substring(0, i));
            sb.append(".resources.");
            sb.append(cls.getSimpleName());
        } else {
            sb.append("resources.");
            sb.append(cls.getSimpleName());
        }
        return sb.toString();
    }

    /**
     * Map from a class to a list of the names of the {@code ResourceBundles} specific to the class. The list is in
     * priority order: resources defined by the first ResourceBundle shadow resources with the the same name that come
     * later.
     * <p/>
     * By default this method returns one ResourceBundle whose name is the same as the class's name, but in the {@code
     * "resources"} subpackage.
     * <p/>
     * For example, given a class named {@code com.foo.bar.MyClass}, the ResourceBundle name would be {@code
     * "com.foo.bar.resources.MyClass"}. If MyClass is an inner class, only its "simple name" is used.  For example,
     * given an inner class named {@code com.foo.bar.OuterClass$InnerClass}, the ResourceBundle name would be {@code
     * "com.foo.bar.resources.InnerClass"}.
     * <p/>
     * This method is used by {@link #getResourceMap(Class) getResourceMap()} to compute the list of ResourceBundle
     * names for a new {@code ResourceMap}.  ResourceManager subclasses can override this method to add additional
     * class-specific ResourceBundle names to the list.
     * @param cls the named ResourceBundles are specific to {@code cls}.
     * @return the names of the ResourceBundles to be loaded for {@code cls}
     * @see #getResourceMap
     * @see #getPackageBundleNames
     * @see #getApplicationBundleNames
     */
    protected List<String> getClassBundleNames(Class cls) {
        String bundleName = classBundleBaseName(cls);
        return Collections.singletonList(bundleName);
    }

    private String classPackageName(Class cls) {
        Package pkg = cls.getPackage();
        if (pkg != null) {
            return pkg.getName();
        } else {
            String className = cls.getName();
            int i = className.lastIndexOf('.');
            return (i > 0) ? className.substring(0, i) : "";
        }
    }

    private String packageBundleBaseName(Class cls) {
        return classPackageName(cls) + ".resources.PackageResources";
    }

    /**
     * Map from a class to a list of the names of the {@code ResourceBundles} that are shared by classes in the same
     * package. The list is in priority order: resources defined by the first ResourceBundle shadow resources with the
     * the same name that come later.
     * <p/>
     * By default this method returns one ResourceBundle name, {@code "resources.PackageResources"} defined relative to
     * the package of the {@code cls} argument.
     * <p/>
     * For example, given a class named {@code com.foo.bar.MyClass}, the package ResourceBundle name would be {@code
     * "com.foo.bar.resources.PackageResources"}.
     * <p/>
     * This method is used by {@link #getResourceMap(Class) getResourceMap()} to compute the list of ResourceBundle
     * names for a new {@code ResourceMap}.  ResourceManager subclasses can override this method to add additional
     * package-shared ResourceBundle names to the list.
     * @param cls the named ResourceBundles are for all classes in the same package as {@code cls}.
     * @return the names of the ResourceBundles to be loaded for classes in {@code cls.getPackage()}.
     * @see #getResourceMap
     * @see #getClassBundleNames
     * @see #getApplicationBundleNames
     */
    protected List<String> getPackageBundleNames(Class cls) {
        String bundleName = packageBundleBaseName(cls);
        return Collections.singletonList(bundleName);
    }
}
