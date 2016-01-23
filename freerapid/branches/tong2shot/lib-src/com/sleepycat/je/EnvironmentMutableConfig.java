/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.je;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import com.sleepycat.je.config.ConfigParam;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.evictor.OffHeapCache;

/**
 * Specifies the environment attributes that may be changed after the
 * environment has been opened.  EnvironmentMutableConfig is a parameter to
 * {@link Environment#setMutableConfig} and is returned by {@link
 * Environment#getMutableConfig}.
 *
 * <p>There are two types of mutable environment properties: per-environment
 * handle properties, and environment wide properties.</p>
 *
 * <h4>Per-Environment Handle Properties</h4>
 *
 * <p>Per-environment handle properties apply only to a single Environment
 * instance.  For example, to change the default transaction commit behavior
 * for a single environment handle, do this:</p>
 *
 * <blockquote><pre>
 *     // Specify no-sync behavior for a given handle.
 *     EnvironmentMutableConfig mutableConfig = env.getMutableConfig();
 *     mutableConfig.setTxnNoSync(true);
 *     env.setMutableConfig(mutableConfig);
 * </pre></blockquote>
 *
 * <p>The per-environment handle properties are listed below.  These properties
 * are accessed using the setter and getter methods listed, as shown in the
 * example above.</p>
 *
 * <ul>
 * <li>{@link #setDurability}, {@link #getDurability}</li>
 * <li>{@link #setTxnNoSync}, {@link #getTxnNoSync} <em>deprecated</em></li>
 * <li>{@link #setTxnWriteNoSync}, {@link #getTxnWriteNoSync} <em>deprecated</em></li>
 * </ul>
 *
 * <h4>Environment-Wide Mutable Properties</h4>
 *
 * <p>Environment-wide mutable properties are those that can be changed for an
 * environment as a whole, irrespective of which environment instance (for the
 * same physical environment) is used.  For example, to stop the cleaner daemon
 * thread, do this:</p>
 *
 * <blockquote><pre>
 *     // Stop the cleaner daemon threads for the environment.
 *     EnvironmentMutableConfig mutableConfig = env.getMutableConfig();
 *     mutableConfig.setConfigParam(EnvironmentConfig.ENV_RUN_CLEANER, "false");
 *     env.setMutableConfig(mutableConfig);
 * </pre></blockquote>
 *
 * <p>The environment-wide mutable properties are listed below.  These
 * properties are accessed using the {@link #setConfigParam} and {@link
 * #getConfigParam} methods, as shown in the example above, using the property
 * names listed below.  In some cases setter and getter methods are also
 * available.</p>
 *
 * <ul>
 * <li>je.maxMemory ({@link #setCacheSize}, {@link #getCacheSize})</li>
 * <li>je.maxMemoryPercent ({@link #setCachePercent},
 * {@link #getCachePercent})</li>
 * <li>je.env.runINCompressor</li>
 * <li>je.env.runEvictor</li>
 * <li>je.env.runCheckpointer</li>
 * <li>je.env.runCleaner</li>
 * </ul>
 *
 * <h4>Getting the Current Environment Properties</h4>
 *
 * To get the current "live" properties of an environment after constructing it
 * or changing its properties, you must call {@link Environment#getConfig} or
 * {@link Environment#getMutableConfig}.  The original EnvironmentConfig or
 * EnvironmentMutableConfig object used to set the properties is not kept up to
 * date as properties are changed, and does not reflect property validation or
 * properties that are computed. @see EnvironmentConfig
 */
public class EnvironmentMutableConfig implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * Change copyHandlePropsTo and Environment.copyToHandleConfig when adding
     * fields here.
     */
    private boolean txnNoSync = false;
    private boolean txnWriteNoSync = false;

    /**
     * Cache size is a category of property that is calculated within the
     * environment.  It is only supplied when returning the cache size to the
     * application and never used internally; internal code directly checks
     * with the MemoryBudget class;
     */
    private long cacheSize;

    private long offHeapCacheSize;

    /**
     * Note that in the implementation we choose not to extend Properties in
     * order to keep the configuration type safe.
     */
    Properties props;

    /**
     * For unit testing, to prevent loading of je.properties.
     */
    private transient boolean loadPropertyFile = true;

    /**
     * Internal boolean that says whether or not to validate params.  Setting
     * it to false means that parameter value validatation won't be performed
     * during setVal() calls.  Only should be set to false by unit tests using
     * DbInternal.
     */
    transient boolean validateParams = true;

    private transient ExceptionListener exceptionListener = null;
    private CacheMode cacheMode;

    /**
     * An instance created using the default constructor is initialized with
     * the system's default settings.
     */
    public EnvironmentMutableConfig() {
        props = new Properties();
    }

    /**
     * Used by EnvironmentConfig to construct from properties.
     */
    EnvironmentMutableConfig(Properties properties)
        throws IllegalArgumentException {

        DbConfigManager.validateProperties(properties,
                                           false,  // isRepConfigInstance
                                           getClass().getName());
        /* For safety, copy the passed in properties. */
        props = new Properties();
        props.putAll(properties);
    }

    /**
     * Configures the database environment for asynchronous transactions.
     *
     * @param noSync If true, do not write or synchronously flush the log on
     * transaction commit. This means that transactions exhibit the ACI
     * (Atomicity, Consistency, and Isolation) properties, but not D
     * (Durability); that is, database integrity is maintained, but if the JVM
     * or operating system fails, it is possible some number of the most
     * recently committed transactions may be undone during recovery. The
     * number of transactions at risk is governed by how many updates fit into
     * a log buffer, how often the operating system flushes dirty buffers to
     * disk, and how often the database environment is checkpointed.
     *
     * <p>This attribute is false by default for this class and for the
     * database environment.</p>
     *
     * @deprecated replaced by {@link #setDurability}
     */
    public EnvironmentMutableConfig setTxnNoSync(boolean noSync) {
        setTxnNoSyncVoid(noSync);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setTxnNoSyncVoid(boolean noSync) {
        TransactionConfig.checkMixedMode
            (false, noSync, txnWriteNoSync, getDurability());
        txnNoSync = noSync;
    }

    /**
     * Returns true if the database environment is configured for asynchronous
     * transactions.
     *
     * @return true if the database environment is configured for asynchronous
     * transactions.
     *
     * @deprecated replaced by {@link #getDurability}
     */
    public boolean getTxnNoSync() {
        return txnNoSync;
    }

    /**
     * Configures the database environment for transactions which write but do
     * not flush the log.
     *
     * @param writeNoSync If true, write but do not synchronously flush the log
     * on transaction commit. This means that transactions exhibit the ACI
     * (Atomicity, Consistency, and Isolation) properties, but not D
     * (Durability); that is, database integrity is maintained, but if the
     * operating system fails, it is possible some number of the most recently
     * committed transactions may be undone during recovery. The number of
     * transactions at risk is governed by how often the operating system
     * flushes dirty buffers to disk, and how often the database environment is
     * checkpointed.
     *
     * <p>The motivation for this attribute is to provide a transaction that
     * has more durability than asynchronous (nosync) transactions, but has
     * higher performance than synchronous transactions.</p>
     *
     * <p>This attribute is false by default for this class and for the
     * database environment.</p>
     *
     * @deprecated replaced by {@link #setDurability}
     */
    public EnvironmentMutableConfig setTxnWriteNoSync(boolean writeNoSync) {
        setTxnWriteNoSyncVoid(writeNoSync);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setTxnWriteNoSyncVoid(boolean writeNoSync) {
        TransactionConfig.checkMixedMode
            (false, txnNoSync, writeNoSync, getDurability());
        txnWriteNoSync = writeNoSync;
    }

    /**
     * Returns true if the database environment is configured for transactions
     * which write but do not flush the log.
     *
     * @return true if the database environment is configured for transactions
     * which write but do not flush the log.
     *
     * @deprecated replaced by {@link #getDurability}
     */
    public boolean getTxnWriteNoSync() {
        return txnWriteNoSync;
    }

    /**
     * Configures the durability associated with transactions.
     * <p>
     * Equivalent to setting the je.txn.durability property in the
     * je.properties file.
     * </p>
     * @see Durability
     *
     * @param durability the new durability definition
     *
     * @return this
     */
    public EnvironmentMutableConfig setDurability(Durability durability) {
        setDurabilityVoid(durability);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setDurabilityVoid(Durability durability) {
        TransactionConfig.checkMixedMode
            (false, txnNoSync, txnWriteNoSync, durability);

        if (durability == null) {
            props.remove(EnvironmentParams.JE_DURABILITY);
        } else {
            DbConfigManager.setVal(props, EnvironmentParams.JE_DURABILITY,
                                   durability.toString(),
                                   validateParams);
        }
    }

    /**
     * Returns the durability associated with the configuration.
     *
     * @return the durability setting currently associated with this config.
     */
    public Durability getDurability() {
        String value = DbConfigManager.getVal(props,
                                              EnvironmentParams.JE_DURABILITY);
        return Durability.parse(value);
    }

    /**
     * Configures the memory available to the database system, in bytes.
     *
     * <p>Equivalent to setting the je.maxMemory property in the je.properties
     * file. The system will evict database objects when it comes within a
     * prescribed margin of the limit.</p>
     *
     * <p>By default, JE sets the cache size to:</p>
     *
     * <pre><blockquote>
     *         (je.maxMemoryPercent *  JVM maximum memory) / 100
     * </pre></blockquote>
     *
     * <p>where JVM maximum memory is specified by the JVM -Xmx flag. However,
     * calling setCacheSize() with a non-zero value overrides the percentage
     * based calculation and sets the cache size explicitly. Calling
     * setCacheSize() is equivalent to setting {@link
     * EnvironmentConfig#MAX_MEMORY}.</p>
     *
     * <p>The following details apply to setting the cache size to a byte size
     * (this method) as well as to a percentage of the JVM heap size (see
     * {@link #setCachePercent}</p>
     *
     * <p>Note that the log buffer cache may be cleared if the cache size is
     * changed after the environment has been opened.</p>
     *
     * <p>If setSharedCache(true) is called, setCacheSize and setCachePercent
     * specify the total size of the shared cache, and changing these
     * parameters will change the size of the shared cache.</p>
     *
     * <p>
     * When using the shared cache feature, new environments that join the
     * cache may alter the cache percent setting if their configuration is set
     * to a different value.</p>
     *
     * <p>To take full advantage of JE cache memory, it is strongly recommended
     * that
     * <a href="http://download.oracle.com/javase/7/docs/technotes/guides/vm/performance-enhancements-7.html#compressedOop">compressed oops</a>
     * (<code>-XX:+UseCompressedOops</code>) is specified when a 64-bit JVM is
     * used and the maximum heap size is less than 32 GB.  As described in the
     * referenced documentation, compressed oops is sometimes the default JVM
     * mode even when it is not explicitly specified in the Java command.
     * However, if compressed oops is desired then it <em>must</em> be
     * explicitly specified in the Java command when running DbCacheSize or a
     * JE application.  If it is not explicitly specified then JE will not
     * aware of it, even if it is the JVM default setting, and will not take it
     * into account when calculating cache memory sizes.</p>
     *
     * @param totalBytes The memory available to the database system, in bytes.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     *
     * @return this
     */
    public EnvironmentMutableConfig setCacheSize(long totalBytes)
        throws IllegalArgumentException {

        setCacheSizeVoid(totalBytes);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setCacheSizeVoid(long totalBytes)
        throws IllegalArgumentException {

        DbConfigManager.setVal(props, EnvironmentParams.MAX_MEMORY,
            Long.toString(totalBytes), validateParams);
    }

    /**
     * Returns the memory available to the database system, in bytes. A valid
     * value is only available if this EnvironmentConfig object has been
     * returned from Environment.getConfig();
     *
     * @return The memory available to the database system, in bytes.
     */
    public long getCacheSize() {

        /*
         * CacheSize is filled in from the EnvironmentImpl by way of
         * fillInEnvironmentGeneratedProps.
         */
        return cacheSize;
    }

    /**
     * Configures the memory available to the database system, as a percentage
     * of the JVM maximum memory.
     *
     * <p>By default, JE sets its cache size proportionally to the JVM
     * memory. This formula is used:</p>
     *
     * <blockquote><pre>
     *         je.maxMemoryPercent *  JVM maximum memory
     * </pre></blockquote>
     *
     * <p>where JVM maximum memory is specified by the JVM -Xmx flag.
     * setCachePercent() specifies the percentage used and is equivalent to
     * setting {@link EnvironmentConfig#MAX_MEMORY_PERCENT}.</p>
     *
     * <p>Calling setCacheSize() with a non-zero value overrides the percentage
     * based calculation and sets the cache size explicitly.</p>
     *
     * <p>See {@link #setCacheSize} for additional details.</p>
     *
     * @param percent The percent of JVM memory to allocate to the JE cache.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified.
     *
     * @return this
     */
    public EnvironmentMutableConfig setCachePercent(int percent)
        throws IllegalArgumentException {

        setCachePercentVoid(percent);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setCachePercentVoid(int percent)
        throws IllegalArgumentException {

        DbConfigManager.setIntVal(props, EnvironmentParams.MAX_MEMORY_PERCENT,
            percent, validateParams);
    }

    /**
     * Returns the percentage value used in the JE cache size calculation.
     *
     * @return the percentage value used in the JE cache size calculation.
     */
    public int getCachePercent() {

        return DbConfigManager.getIntVal(props,
                                         EnvironmentParams.MAX_MEMORY_PERCENT);
    }

    /**
     * Configures the number of bytes to be used as a secondary, off-heap cache.
     *
     * The off-heap cache is used to hold record data and Btree nodes when
     * these are evicted from the "main cache" because it overflows. Eviction
     * occurs according to an LRU algorithm and takes into account the user-
     * specified {@link CacheMode}. When the off-heap cache overflows, eviction
     * occurs there also according to the same algorithm.
     * <p>
     * The main cache is in the Java heap and consists primarily of the Java
     * objects making up the in-memory Btree data structure. Btree objects are
     * not serialized the main cache, so no object materialization is needed to
     * access the Btree there. Access to records in the main cache is therefore
     * very fast, but the main cache has drawbacks as well: 1) The larger the
     * main cache, the more likely it is to have Java GC performance problems.
     * 2) When the Java heap exceeds 32GB, the "compressed OOPs" setting no
     * longer applies and less data will fit in the same amount of memory. For
     * these reasons, JE applications often configure a heap of 32GB or less,
     * and a main cache that is significantly less than 32GB, leaving any
     * additional machine memory for use by the file system cache.
     * <p>
     * The use of the file system cache has performance benefits, but
     * also has its own drawbacks: 1) There is a significant redundancy
     * between the main cache and the file system cache because all data and
     * Btree information that is logged (written) by JE appears in the file
     * system and may also appear in the main cache. 2) It is not possible
     * for <em>dirty</em> Btree information to be placed in the file system
     * cache without logging it, this logging may be otherwise unnecessary, and
     * the logging creates additional work for the JE cleaner; in other words,
     * the size of the main cache alone determines the maximum size of the
     * in-memory "dirty set".
     * <p>
     * The off-heap cache is stored outside the Java heap using a native
     * platform memory allocator. The current implementation relies on
     * internals that are specific to the Oracle and IBM JDKs; however, a
     * memory allocator interface that can be implemented for other situations
     * is being considered for a future release. Records and Btree objects are
     * serialized when they are placed in the off-heap cache, and they must be
     * materialized when they are moved back to the main cache in order to
     * access them. This serialization and materialization adds some CPU
     * overhead and thread contention, as compared to accessing data directly
     * in the main cache. The off-heap cache can contain dirty Btree
     * information, so it can be used to increase the maximum size of the
     * in-memory "dirty set".
     * <p>
     * NOTE: If an off-heap cache is configured but cannot be used because
     * that native allocator is not available in the JDK that is used, an
     * {@code IllegalStateException} will be thrown by the {@link Environment}
     * or {@link com.sleepycat.je.rep.ReplicatedEnvironment} constructor. In
     * the current release, this means that the {@code sun.misc.Unsafe} class
     * must contain the {@code allocateMemory} method and related methods, as
     * defined in the Oracle JDK.
     * <p>
     * When configuring an off-heap cache you can think of the performance
     * trade-offs in two ways. First, if the off-heap cache is considered to be
     * a replacement for the file system cache, the serialization and
     * materialization overhead is not increased. In this case, the use of
     * the off-heap cache is clearly beneficial, and using the off-heap cache
     * "instead of" the file system cache is normally recommended. Second, the
     * off-heap cache can be used along with a main cache that is reduced in
     * size in order to compensate for Java GC problems. In this case, the
     * trade-off is between the additional serialization, materialization and
     * contention overheads of the off-heap cache, as compared to the Java GC
     * overhead.
     * <p>
     * When dividing up available memory for the JVM heap, the off-heap cache,
     * and for other uses, please be aware that the file system cache and the
     * off-heap cache are different in one important respect. The file system
     * cache automatically shrinks when memory is needed by the OS or other
     * processes, while the off-heap cache does not. Therefore, it is best to
     * be conservative about leaving memory free for other uses, and it is not
     * a good idea to size the off-heap cache such that all machine memory will
     * be allocated. If off-heap allocations or other allocations fail because
     * there is no available memory, the process is likely to die without any
     * exception being thrown. In one test on Linux, for example, the process
     * was killed abruptly by the OS and the only indication of the problem was
     * the following shown by {@code dmesg}.
     * <pre>
     * Out of memory: Kill process 28768 (java) score 974 or sacrifice child
     * Killed process 28768 (java)
     *    total-vm:278255336kB, anon-rss:257274420kB, file-rss:0kB
     * </pre>
     * <p>
     * WARNING: Although this configuration property is mutable, it cannot be
     * changed from zero to non-zero, or non-zero to zero. In other words, the
     * size of the off-heap cache can be changed after initially configuring a
     * non-zero size, but the off-heap cache cannot be turned on and off
     * dynamically. An attempt to do so will cause an {@code
     * IllegalArgumentException} to be thrown by the {@link Environment} or
     * {@link com.sleepycat.je.rep.ReplicatedEnvironment} constructor.
     */
    public EnvironmentMutableConfig setOffHeapCacheSize(long totalBytes)
        throws IllegalArgumentException {

        setOffHeapCacheSizeVoid(totalBytes);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setOffHeapCacheSizeVoid(long totalBytes)
        throws IllegalArgumentException {

        DbConfigManager.setVal(props, EnvironmentParams.MAX_OFF_HEAP_MEMORY,
            Long.toString(totalBytes), validateParams);
    }

    /**
     * Returns the number of bytes to be used as a secondary, off-heap cache.
     */
    public long getOffHeapCacheSize() {

        /*
         * CacheSize is filled in from the EnvironmentImpl by way of
         * fillInEnvironmentGeneratedProps.
         */
        return offHeapCacheSize;
    }

    /**
     * Sets the exception listener for an Environment.  The listener is called
     * when a daemon thread throws an exception, in order to provide a
     * notification mechanism for these otherwise asynchronous exceptions.
     * Daemon thread exceptions are also printed through stderr.
     * <p>
     * Not all daemon exceptions are fatal, and the application bears
     * responsibility for choosing how to respond to the notification. Since
     * exceptions may repeat, the application should also choose how to handle
     * a spate of exceptions. For example, the application may choose to act
     * upon each notification, or it may choose to batch up its responses
     * by implementing the listener so it stores exceptions, and only acts
     * when a certain number have been received.
     * @param exceptionListener the callback to be executed when an exception
     * occurs.
     *
     * @return this
     */
    public EnvironmentMutableConfig
        setExceptionListener(ExceptionListener exceptionListener) {

        setExceptionListenerVoid(exceptionListener);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setExceptionListenerVoid(ExceptionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    /**
     * Returns the exception listener, if set.
     */
    public ExceptionListener getExceptionListener() {
        return exceptionListener;
    }

    /**
     * Sets the default {@code CacheMode} used for operations performed in this
     * environment.  The default cache mode may be overridden on a per-database
     * basis using {@link DatabaseConfig#setCacheMode}, and on a per-record or
     * per-operation basis using {@link Cursor#setCacheMode}.
     *
     * @param cacheMode is the default {@code CacheMode} used for operations
     * performed in this environment.  If {@code null} is specified, {@link
     * CacheMode#DEFAULT} will be used.
     *
     * @see CacheMode for further details.
     *
     * @since 4.0.97
     */
    public EnvironmentMutableConfig setCacheMode(final CacheMode cacheMode) {
        setCacheModeVoid(cacheMode);
        return this;
    }
    
    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setCacheModeVoid(final CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    /**
     * Returns the default {@code CacheMode} used for operations performed in
     * this environment, or null if {@link CacheMode#DEFAULT} is used.
     *
     * @return the default {@code CacheMode} used for operations performed on
     * this database, or null if {@link CacheMode#DEFAULT} is used.
     *
     * @see #setCacheMode
     *
     * @since 4.0.97
     */
    public CacheMode getCacheMode() {
        return cacheMode;
    }

    /**
     * Set this configuration parameter. First validate the value specified for
     * the configuration parameter; if it is valid, the value is set in the
     * configuration.
     *
     * @param paramName the configuration parameter name, one of the String
     * constants in this class
     *
     * @param value The configuration value
     *
     * @return this
     *
     * @throws IllegalArgumentException if the paramName or value is invalid.
     */
    public EnvironmentMutableConfig setConfigParam(String paramName,
                                                   String value)
        throws IllegalArgumentException {

        DbConfigManager.setConfigParam(props,
                                       paramName,
                                       value,
                                       true, /* require mutability. */
                                       validateParams,
                                       false /* forReplication */,
                                       true  /* verifyForReplication */);
        return this;
    }

    /**
     * Returns the value for this configuration parameter.
     *
     * @param paramName a valid configuration parameter, one of the String
     * constants in this class.
     * @return the configuration value.
     * @throws IllegalArgumentException if the paramName is invalid.
     */
    public String getConfigParam(String paramName)
        throws IllegalArgumentException {

       return DbConfigManager.getConfigParam(props, paramName);
    }

    /**
     * @hidden
     * For internal use only.
     */
    public boolean isConfigParamSet(String paramName) {
        return props.containsKey(paramName);
    }

    /*
     * Helpers
     */
    void setValidateParams(boolean validateParams) {
        this.validateParams = validateParams;
    }

    /**
     * @hidden
     * Used by unit tests.
     */
    boolean getValidateParams() {
        return validateParams;
    }

    /**
     * Checks that the immutable values in the environment config used to open
     * an environment match those in the config object saved by the underlying
     * shared EnvironmentImpl.
     * @param handleConfigProps are the config property values that were
     * specified by configuration object from the Environment.
     */
    void checkImmutablePropsForEquality(Properties handleConfigProps)
        throws IllegalArgumentException {

        Iterator<String> iter =
            EnvironmentParams.SUPPORTED_PARAMS.keySet().iterator();
        while (iter.hasNext()) {
            String paramName = iter.next();
            ConfigParam param =
                EnvironmentParams.SUPPORTED_PARAMS.get(paramName);
            assert param != null;
            if (!param.isMutable() && !param.isForReplication()) {
                String paramVal = props.getProperty(paramName);
                String useParamVal = handleConfigProps.getProperty(paramName);
                if ((paramVal != null) ?
                    (!paramVal.equals(useParamVal)) :
                    (useParamVal != null)) {
                    throw new IllegalArgumentException
                        (paramName + " is set to " +
                         useParamVal +
                         " in the config parameter" +
                         " which is incompatible" +
                         " with the value of " +
                         paramVal + " in the" +
                         " underlying environment");
                }
            }
        }
    }

    /**
     * @hidden
     * For internal use only.
     * Overrides Object.clone() to clone all properties, used by this class and
     * EnvironmentConfig.
     */
    @Override
    protected EnvironmentMutableConfig clone() {

        try {
            EnvironmentMutableConfig copy =
                (EnvironmentMutableConfig) super.clone();
            copy.props = (Properties) props.clone();
            return copy;
        } catch (CloneNotSupportedException willNeverOccur) {
            return null;
        }
    }

    /**
     * Used by Environment to create a copy of the application supplied
     * configuration. Done this way to provide non-public cloning.
     */
    EnvironmentMutableConfig cloneMutableConfig() {
        EnvironmentMutableConfig copy = (EnvironmentMutableConfig) clone();
        /* Remove all immutable properties. */
        copy.clearImmutableProps();
        return copy;
    }

    /**
     * Copies the per-handle properties of this object to the given config
     * object.
     */
    void copyHandlePropsTo(EnvironmentMutableConfig other) {
        other.txnNoSync = txnNoSync;
        other.txnWriteNoSync = txnWriteNoSync;
        other.setDurability(getDurability());
    }

    /**
     * Copies all mutable props to the given config object.
     * Unchecked suppress here because Properties don't play well with
     * generics in Java 1.5
     */
    @SuppressWarnings("unchecked")
    void copyMutablePropsTo(EnvironmentMutableConfig toConfig) {

        Properties toProps = toConfig.props;
        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String paramName = (String) propNames.nextElement();
            ConfigParam param =
                EnvironmentParams.SUPPORTED_PARAMS.get(paramName);
            assert param != null;
            if (param.isMutable()) {
                String newVal = props.getProperty(paramName);
                toProps.setProperty(paramName, newVal);
            }
        }
        toConfig.exceptionListener = this.exceptionListener;
        toConfig.cacheMode = this.cacheMode;
    }

    /**
     * Fills in the properties calculated by the environment to the given
     * config object.
     */
    void fillInEnvironmentGeneratedProps(EnvironmentImpl envImpl) {

        cacheSize = envImpl.getMemoryBudget().getMaxMemory();

        final OffHeapCache offHeapCache = envImpl.getOffHeapCache();

        offHeapCacheSize =
            (offHeapCache != null) ? offHeapCache.getMaxMemory() : 0;
    }

   /**
    * Removes all immutable props.
    * Unchecked suppress here because Properties don't play well with
    * generics in Java 1.5
    */
    @SuppressWarnings("unchecked")
    private void clearImmutableProps() {
        Enumeration propNames = props.propertyNames();
        while (propNames.hasMoreElements()) {
            String paramName = (String) propNames.nextElement();
            ConfigParam param =
                EnvironmentParams.SUPPORTED_PARAMS.get(paramName);
            assert param != null;
            if (!param.isMutable()) {
                props.remove(paramName);
            }
        }
    }

    Properties getProps() {
        return props;
    }

    /**
     * For unit testing, to prevent loading of je.properties.
     */
    void setLoadPropertyFile(boolean loadPropertyFile) {
        this.loadPropertyFile = loadPropertyFile;
    }

    /**
     * For unit testing, to prevent loading of je.properties.
     */
    boolean getLoadPropertyFile() {
        return loadPropertyFile;
    }

    /**
     * Testing support
     * @hidden
     */
    public int getNumExplicitlySetParams() {
        return props.size();
    }

    /**
     * Display configuration values.
     */
    @Override
    public String toString() {
        return ("cacheSize=" + cacheSize + "\n" +
                "txnNoSync=" + txnNoSync + "\n" +
                "txnWriteNoSync=" + txnWriteNoSync + "\n" +
                props.toString() + "\n");
    }
}
