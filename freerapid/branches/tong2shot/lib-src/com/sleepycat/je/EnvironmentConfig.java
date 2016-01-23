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

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DbConfigManager;

/**
 * Specifies the attributes of an environment.
 *
 * <p>To change the default settings for a database environment, an application
 * creates a configuration object, customizes settings and uses it for
 * environment construction. The set methods of this class validate the
 * configuration values when the method is invoked.  An
 * IllegalArgumentException is thrown if the value is not valid for that
 * attribute.</p>
 *
 * <p>All commonly used environment attributes have convenience setter/getter
 * methods defined in this class.  For example, to change the default
 * transaction timeout setting for an environment, the application should do
 * the following:</p>
 * <pre class=code>
 *     // customize an environment configuration
 *     EnvironmentConfig envConfig = new EnvironmentConfig();
 *     // will throw if timeout value is invalid
 *     envConfig.setLockTimeout(250, TimeUnit.MILLISECONDS);
 *     // Open the environment using this configuration.
 *     Environment myEnvironment = new Environment(home, envConfig);
 * </pre>
 *
 * <p>Additional parameters are described by the parameter name String
 * constants in this class. These additional parameters will not be needed by
 * most applications. This category of properties can be specified for the
 * EnvironmentConfig object through a Properties object read by
 * EnvironmentConfig(Properties), or individually through
 * EnvironmentConfig.setConfigParam().</p>
 *
 * <p>For example, an application can change the default btree node size
 * with:</p>
 *
 * <pre>
 *     envConfig.setConfigParam(EnvironmentConfig.NODE_MAX_ENTRIES, "256");
 * </pre>
 *
 * <p>Environment configuration follows this order of precedence:</p>
 * <ol>
 * <li>Configuration parameters specified in
 * &lt;environment home&gt;/je.properties take first precedence.
 * <li>Configuration parameters set in the EnvironmentConfig object used at
 * Environment construction are next.
 * <li>Any configuration parameters not set by the application are set to
 * system defaults, described along with the parameter name String constants
 * in this class.</li>
 * </ol>
 *
 * <p>An EnvironmentConfig can be used to specify both mutable and immutable
 * environment properties.  Immutable properties may be specified when the
 * first Environment handle (instance) is opened for a given physical
 * environment.  When more handles are opened for the same environment, the
 * following rules apply:</p>
 *
 * <ol> <li>Immutable properties must equal the original values specified when
 * constructing an Environment handle for an already open environment.  When a
 * mismatch occurs, an exception is thrown.
 *
 * <li>Mutable properties are ignored when constructing an Environment handle
 * for an already open environment.  </ol>
 *
 * <p>After an Environment has been constructed, its mutable properties may be
 * changed using {@link Environment#setMutableConfig}.  See {@link
 * EnvironmentMutableConfig} for a list of mutable properties; all other
 * properties are immutable.  Whether a property is mutable or immutable is
 * also described along with the parameter name String constants in this
 * class.</p>
 *
 * <h4>Getting the Current Environment Properties</h4>
 *
 * To get the current "live" properties of an environment after constructing it
 * or changing its properties, you must call {@link Environment#getConfig} or
 * {@link Environment#getMutableConfig}.  The original EnvironmentConfig or
 * EnvironmentMutableConfig object used to set the properties is not kept up to
 * date as properties are changed, and does not reflect property validation or
 * properties that are computed.
 *
 * <h4><a name="timeDuration">Time Duration Properties</a></h4>
 *
 * <p>Several environment and transaction configuration properties are time
 * durations.  For these properties, a time unit is specified along with an
 * integer duration value.</p>
 *
 * <p>When specific setter and getter methods exist for a time duration
 * property, these methods have a {@link TimeUnit} argument.  Examples are
 * {@link #setLockTimeout(long,TimeUnit)} and {@link
 * #getLockTimeout(TimeUnit)}.  Note that the {@link TimeUnit} argument may
 * be null only when the duration value is zero; there is no default unit that
 * is used when null is specified.</p>
 *
 * <p>When a time duration is specified as a string value, the following format
 * is used.</p>
 *
 * <pre>   {@code <value> [ <whitespace> <unit> ]}</pre>
 *
 * <p>The {@code <value>} is an integer.  The {@code <unit>} name, if present,
 * must be preceded by one or more spaces or tabs.</p>
 *
 * <p>The following {@code <unit>} names are allowed.  Both {@link TimeUnit}
 * names and IEEE standard abbreviations are allowed.  Unit names are case
 * insensitive.</p>
 *
 * <table border="true">
 * <tr><th>IEEE abbreviation</th>
 *     <th>TimeUnit name</td>
 *     <th>Definition</th>
 * </tr>
 * <tr><td>{@code ns}</td>
 *     <td>{@code NANOSECONDS}</td>
 *     <td>one billionth (10<sup>-9</sup>) of a second</td>
 * </tr>
 * <tr><td>{@code us}</td>
 *     <td>{@code MICROSECONDS}</td>
 *     <td>one millionth (10<sup>-6</sup>) of a second</td>
 * </tr>
 * <tr><td>{@code ms}</td>
 *     <td>{@code MILLISECONDS}</td>
 *     <td>one thousandth (10<sup>-3</sup>) of a second</td>
 * </tr>
 * <tr><td>{@code s}</td>
 *     <td>{@code SECONDS}</td>
 *     <td>1 second</td>
 * </tr>
 * <tr><td>{@code min}</td>
 *     <td>&nbsp;</td>
 *     <td>60 seconds</td>
 * </tr>
 * <tr><td>{@code h}</td>
 *     <td>&nbsp;</td>
 *     <td>3600 seconds</td>
 * </tr>
 * </table>
 *
 * <p>Examples are:</p>
 * <pre>
 * 3 seconds
 * 3 s
 * 500 ms
 * 1000000 (microseconds is implied)
 * </pre>
 *
 * <p>The maximum duration value is currently Integer.MAX_VALUE milliseconds.
 * This translates to almost 25 days (2147483647999999 ns, 2147483647999 us,
 * 2147483647 ms, 2147483 s, 35791 min, 596 h).</p>
 *
 * <p>Note that when the {@code <unit>} is omitted, microseconds is implied.
 * This default is supported for compatibility with JE 3.3 and earlier.  In JE
 * 3.3 and earlier, explicit time units were not used and durations were always
 * implicitly specified in microseconds.  The older methods that do not have a
 * {@link TimeUnit} argument, such as {@link #setLockTimeout(long)} and {@link
 * #getLockTimeout()}, use microsecond durations and have been deprecated.</p>
 */
public class EnvironmentConfig extends EnvironmentMutableConfig {
    private static final long serialVersionUID = 1L;

    /**
     * @hidden
     * For internal use, to allow null as a valid value for the config
     * parameter.
     */
    public static final EnvironmentConfig DEFAULT = new EnvironmentConfig();

    /**
     * The {@link #setCacheSize CacheSize} property.
     *
     * When using the shared cache feature, new environments that join the
     * cache may alter the cache size setting if their configuration is set to
     * a different value.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>-none-</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     *
     * @see #setCacheSize
     */
    public static final String MAX_MEMORY = "je.maxMemory";

    /**
     * The {@link #setCachePercent CachePercent} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>60</td>
     * <td>1</td>
     * <td>90</td>
     * </tr>
     * </table></p>
     *
     * @see #setCachePercent
     */
    public static final String MAX_MEMORY_PERCENT = "je.maxMemoryPercent";

    /**
     * The {@link #setOffHeapCacheSize}  CachePercent} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     *
     * @see #setOffHeapCacheSize(long)
     */
    public static final String MAX_OFF_HEAP_MEMORY = "je.maxOffHeapMemory";

    /**
     * The {@link #setSharedCache SharedCache} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String SHARED_CACHE = "je.sharedCache";

    /**
     * If true, a checkpoint is forced following recovery, even if the
     * log ends with a checkpoint.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RECOVERY_FORCE_CHECKPOINT =
        "je.env.recoveryForceCheckpoint";

    /**
     * Used after performing a restore from backup to force creation of a new
     * log file prior to recovery.
     * <p>
     * As of JE 6.3, the use of this parameter is unnecessary except in special
     * cases. See the "Restoring from a backup" section in the DbBackup javadoc
     * for more information.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="util/DbBackup.html#restore">Restoring from a backup</a>
     */
    public static final String ENV_RECOVERY_FORCE_NEW_FILE =
        "je.env.recoveryForceNewFile";

    /**
     * By default, if a checksum exception is found at the end of the log
     * during Environment startup, JE will assume the checksum is due to
     * previously interrupted I/O and will quietly truncate the log and
     * restart. If this property is set to true, when a ChecksumException
     * occurs in the last log file during recovery, instead of truncating the
     * log file, and automatically restarting, attempt to continue reading past
     * the corrupted record with the checksum error to see if there are commit
     * records following the corruption. If there are, throw an
     * EnvironmentFailureException to indicate the presence of committed
     * transactions. The user may then need to run DbTruncateLog to truncate
     * the log for further recovery after doing manual analysis of the log.
     * Setting this property is suitable when the application wants to guard
     * against unusual cases.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String HALT_ON_COMMIT_AFTER_CHECKSUMEXCEPTION =
        "je.haltOnCommitAfterChecksumException";

    /**
     * If true, starts up the INCompressor thread.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RUN_IN_COMPRESSOR =
        "je.env.runINCompressor";

    /**
     * If true, starts up the checkpointer thread.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RUN_CHECKPOINTER = "je.env.runCheckpointer";

    /**
     * If true, starts up the cleaner thread.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RUN_CLEANER = "je.env.runCleaner";

    /**
     * If true, eviction is done by a pool of evictor threads, as well as being
     * done inline by application threads. If false, the evictor pool is not
     * used, regardless of the values of {@link #EVICTOR_CORE_THREADS} and
     * {@link #EVICTOR_MAX_THREADS}.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RUN_EVICTOR = "je.env.runEvictor";

    /**
     * If true, off-heap eviction is done by a pool of evictor threads, as well
     * as being done inline by application threads. If false, the evictor pool
     * is not used, regardless of the values of {@link #OFFHEAP_CORE_THREADS}
     * and {@link #OFFHEAP_MAX_THREADS}.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_RUN_OFFHEAP_EVICTOR =
        "je.env.runOffHeapEvictor";

    /**
     * The maximum number of read operations performed by JE background
     * activities (e.g., cleaning) before sleeping to ensure that application
     * threads can perform I/O.  If zero (the default) then no limitation on
     * I/O is enforced.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     *
     * @see #ENV_BACKGROUND_SLEEP_INTERVAL
     */
    public static final String ENV_BACKGROUND_READ_LIMIT =
        "je.env.backgroundReadLimit";

    /**
     * The maximum number of write operations performed by JE background
     * activities (e.g., checkpointing and eviction) before sleeping to ensure
     * that application threads can perform I/O.  If zero (the default) then no
     * limitation on I/O is enforced.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     *
     * @see #ENV_BACKGROUND_SLEEP_INTERVAL
     */
    public static final String ENV_BACKGROUND_WRITE_LIMIT =
        "je.env.backgroundWriteLimit";

    /**
     * The duration that JE background activities will sleep when the {@link
     * #ENV_BACKGROUND_WRITE_LIMIT} or {@link #ENV_BACKGROUND_READ_LIMIT} is
     * reached.  If {@link #ENV_BACKGROUND_WRITE_LIMIT} and {@link
     * #ENV_BACKGROUND_READ_LIMIT} are zero, this setting is not used.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>1 ms</td>
     * <td>1 ms</td>
     * <td>24 d</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String ENV_BACKGROUND_SLEEP_INTERVAL =
        "je.env.backgroundSleepInterval";

    /**
     * Debugging support: check leaked locks and txns at env close.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_CHECK_LEAKS = "je.env.checkLeaks";

    /**
     * Debugging support: call Thread.yield() at strategic points.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_FORCED_YIELD = "je.env.forcedYield";

    /**
     * If true, create an environment that is capable of performing
     * transactions.  If true is not passed, transactions may not be used.  For
     * licensing purposes, the use of this method distinguishes the use of the
     * Transactional product.  Note that if transactions are not used,
     * specifying true does not create additional overhead in the environment.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_IS_TRANSACTIONAL = "je.env.isTransactional";

    /**
     * If true, create the environment with record locking.  This property
     * should be set to false only in special circumstances when it is safe to
     * run without record locking.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_IS_LOCKING = "je.env.isLocking";

    /**
     * If true, open the environment read-only.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_READ_ONLY = "je.env.isReadOnly";

    /**
     * If true, use latches instead of synchronized blocks to implement the
     * lock table and log write mutexes. Latches require that threads queue to
     * obtain the mutex in question and therefore guarantee that there will be
     * no mutex starvation, but do incur a performance penalty. Latches should
     * not be necessary in most cases, so synchronized blocks are the default.
     * An application that puts heavy load on JE with threads with different
     * thread priorities might find it useful to use latches.  In a Java 5 JVM,
     * where java.util.concurrent.locks.ReentrantLock is used for the latch
     * implementation, this parameter will determine whether they are 'fair' or
     * not.  This parameter is 'static' across all environments.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_FAIR_LATCHES = "je.env.fairLatches";

    /**
     * The timeout for detecting internal latch timeouts, so that deadlocks can
     * be detected. Latches are held internally for very short durations. If
     * due to unforeseen problems a deadlock occurs, a timeout will occur after
     * the duration specified by this parameter. When a latch timeout occurs:
     * <ul>
     *     <li>The Environment is invalidated and must be closed.</li>
     *     <li>An {@link EnvironmentFailureException} is thrown.</li>
     *     <li>A full thread dump is logged at level SEVERE.</li>
     * </ul>
     * If this happens, thread dump in je.info file should be preserved so it
     * can be used to analyze the problem.
     * <p>
     * Most applications should not change this parameter. The default value, 5
     * minutes, should be much longer than a latch is ever held.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>5 min</td>
     * <td>1 ms</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     *
     * @since 6.2
     */
    public static final String ENV_LATCH_TIMEOUT = "je.env.latchTimeout";

    /**
     * If true, enable eviction of metadata for closed databases.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_DB_EVICTION = "je.env.dbEviction";

    /**
     * If true (the default) preload all duplicates databases at once when
     * upgrading from JE 4.1 and earlier.  If false, preload each duplicates
     * database individually instead.  Preloading all databases at once gives a
     * performance advantage if the JE cache is roughly large enough to contain
     * the internal nodes for all duplicates databases.  Preloading each
     * database individually gives a performance advantage if the JE cache is
     * roughly large enough to contain the internal nodes for a single
     * duplicates database.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String ENV_DUP_CONVERT_PRELOAD_ALL =
        "je.env.dupConvertPreloadAll";

    /**
     * By default, JE passes an entire log record to the Adler32 class for
     * checksumming.  This can cause problems with the GC in some cases if the
     * records are large and there is concurrency.  Setting this parameter will
     * cause JE to pass chunks of the log record to the checksumming class so
     * that the GC does not block.  0 means do not chunk.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>1048576 (1M)</td>
     * </tr>
     * </table></p>
     */
    public static final String ADLER32_CHUNK_SIZE = "je.adler32.chunkSize";

    /**
     * The total memory taken by log buffers, in bytes. If 0, use 7% of
     * je.maxMemory. If 0 and je.sharedCache=true, use 7% divided by N where N
     * is the number of environments sharing the global cache.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>No</td>
     * <td>0</td>
     * <td>{@value
     * com.sleepycat.je.config.EnvironmentParams#LOG_MEM_SIZE_MIN}</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_TOTAL_BUFFER_BYTES =
        "je.log.totalBufferBytes";

    /**
     * The number of JE log buffers.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>{@value
     * com.sleepycat.je.config.EnvironmentParams#NUM_LOG_BUFFERS_DEFAULT}</td>
     * <td>2</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_NUM_BUFFERS = "je.log.numBuffers";

    /**
     * The maximum starting size of a JE log buffer.  JE silently restricts
     * this value to be no more than the configured maximum log file size
     * (je.log.fileMax).
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>1048576 (1M)</td>
     * <td>1024 (1K)</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_BUFFER_SIZE = "je.log.bufferSize";

    /**
     * The buffer size for faulting in objects from disk, in bytes.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>2048 (2K)</td>
     * <td>32</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_FAULT_READ_SIZE = "je.log.faultReadSize";

    /**
     * The read buffer size for log iterators, which are used when scanning the
     * log during activities like log cleaning and environment open, in bytes.
     * This may grow as the system encounters larger log entries.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>8192 (8K)</td>
     * <td>128</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_ITERATOR_READ_SIZE =
        "je.log.iteratorReadSize";

    /**
     * The maximum read buffer size for log iterators, which are used when
     * scanning the log during activities like log cleaning and environment
     * open, in bytes.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>16777216 (16M)</td>
     * <td>128</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_ITERATOR_MAX_SIZE =
        "je.log.iteratorMaxSize";

    /**
     * The maximum size of each individual JE log file, in bytes.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>No</td>
     * <td>10000000 (10M)</td>
     * <td>1000000 (1M)</td>
     * <td>1073741824 (1G)</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_FILE_MAX = "je.log.fileMax";

    /**
     * The JE environment can be spread across multiple subdirectories.
     * Environment subdirectories may be used to spread an environment's .jdb
     * files over multiple directories, and therefore over multiple disks or
     * file systems.  Environment subdirectories reside in the environment home
     * directory and are named data001/ through dataNNN/, consecutively, where
     * NNN is the value of je.log.nDataDirectories.  A typical configuration
     * would be to have each of the dataNNN/ names be symbolic links to actual
     * directories which each reside on separate file systems or disks.
     * <p>
     * If 0, all log files (*.jdb) will reside in the environment
     * home directory passed to the Environment constructor.  A non-zero value
     * indicates the number of environment subdirectories to use for holding the
     * environment log files.
     * <p>
     * If data subdirectories are used (i.e. je.log.nDataDirectories > 0), this
     * parameter must be set when the environment is initially created.
     * Like the environment home directory, each and every one of the dataNNN/
     * subdirectories must also be present and writable.  This parameter must
     * be set to the same value for all subsequent openings of the environment
     * or an exception will be thrown.
     * <p>
     * If the set of existing dataNNN/ subdirectories is not equivalent to the
     * set { 1 ... je.log.nDataDirectories } when the environment is opened, an
     * EnvironmentFailureException will be thrown, and the Environment will
     * fail to be opened.
     * <p>
     * This parameter should be set using the je.properties file rather than
     * the EnvironmentConfig. If not, JE command line utilities that open the
     * Environment will throw an exception because they will not know of the
     * non-zero value of this parameter.
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td><td>JVM</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>0</td>
     * <td>0</td>
     * <td>256</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_N_DATA_DIRECTORIES =
        "je.log.nDataDirectories";

    /**
     * If true, perform a checksum check when reading entries from log.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_CHECKSUM_READ = "je.log.checksumRead";

    /**
     * If true, perform a checksum verification just before and after writing
     * to the log.  This is primarily used for debugging.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_VERIFY_CHECKSUMS = "je.log.verifyChecksums";

    /**
     * If true, operates in an in-memory test mode without flushing the log to
     * disk. An environment directory must be specified, but it need not exist
     * and no files are written.  The system operates until it runs out of
     * memory, at which time an OutOfMemoryError is thrown.  Because the entire
     * log is kept in memory, this mode is normally useful only for testing.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_MEM_ONLY = "je.log.memOnly";

    /**
     * The size of the file handle cache.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>100</td>
     * <td>3</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_FILE_CACHE_SIZE = "je.log.fileCacheSize";

    /**
     * The timeout limit for group file sync, in microseconds.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>500 ms</td>
     * <td>10 ms</td>
     * <td>24 d</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String LOG_FSYNC_TIMEOUT = "je.log.fsyncTimeout";

    /**
     * The time interval in nanoseconds during which transactions may be
     * grouped to amortize the cost of write and/or fsync when a transaction
     * commits with SyncPolicy#SYNC or SyncPolicy#WRITE_NO_SYNC on the local
     * machine.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>0</td>
     * <td>0</td>
     * <td>none</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     * @since 5.0.76
     * @see #LOG_GROUP_COMMIT_THRESHOLD
     */
    public static final String LOG_GROUP_COMMIT_INTERVAL =
        "je.log.groupCommitInterval";

    /**
     * The threshold value impacts the number of transactions that may be
     * grouped to amortize the cost of write and/or fsync when a
     * transaction commits with SyncPolicy#SYNC or SyncPolicy#WRITE_NO_SYNC
     * on the local machine.
     * <p>
     * Specifying larger values can result in more transactions being grouped
     * together decreasing average commit times.
     * <p>
     * <table border="1">
     * <tr>
     * <td>Name</td>
     * <td>Type</td>
     * <td>Mutable</td>
     * <td>Default</td>
     * <td>Minimum</td>
     * <td>Maximum</td>
     * </tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>0</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table>
     * </p>
     *
     * @since 5.0.76
     * @see #LOG_GROUP_COMMIT_INTERVAL
     */
    public static final String LOG_GROUP_COMMIT_THRESHOLD =
        "je.log.groupCommitThreshold";

    /**
     * If true (default is false) O_DSYNC is used to open JE log files.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_USE_ODSYNC = "je.log.useODSYNC";

    /**
     * @deprecated NIO is no longer used by JE and this parameter has no
     * effect.
     */
    public static final String LOG_USE_NIO = "je.log.useNIO";

    /**
     * If true (default is true) the Write Queue is used for file I/O
     * operations which are blocked by concurrent I/O operations.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_USE_WRITE_QUEUE = "je.log.useWriteQueue";

    /**
     * The size of the Write Queue.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>1MB</td>
     * <td>4KB</td>
     * <td>32MB-</td>
     * </tr>
     * </table></p>
     */
    public static final String LOG_WRITE_QUEUE_SIZE = "je.log.writeQueueSize";

    /**
     * @deprecated NIO is no longer used by JE and this parameter has no
     * effect.
     */
    public static final String LOG_DIRECT_NIO = "je.log.directNIO";

    /**
     * @deprecated NIO is no longer used by JE and this parameter has no
     * effect.
     */
    public static final String LOG_CHUNKED_NIO = "je.log.chunkedNIO";

    /**
     * The maximum number of entries in an internal btree node.  This can be
     * set per-database using the DatabaseConfig object.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>128</td>
     * <td>4</td>
     * <td>32767 (32K)</td>
     * </tr>
     * </table></p>
     */
    public static final String NODE_MAX_ENTRIES = "je.nodeMaxEntries";

    /**
     * @deprecated this property no longer has any effect; {@link
     * DatabaseConfig#setNodeMaxEntries} should be used instead.
     */
    public static final String NODE_DUP_TREE_MAX_ENTRIES =
        "je.nodeDupTreeMaxEntries";

    /**
     * The maximum size (in bytes) of a record's data portion that will cause
     * the record to be embedded in its parent LN.
     * 
     * Normally, records (key-value pairs) are stored on disk as individual
     * byte sequences called LNs (leaf nodes) and they are accessed via a
     * Btree. The nodes of the Btree are called INs (Internal Nodes) and the
     * INs at the bottom layer of the Btree are called BINs (Bottom Internal
     * Nodes). Conceptually, each BIN contains an array of slots. A slot
     * represents an associated data record. Among other things, it stores
     * the key of the record and the most recent disk address of that record.
     * Records and INs share the disk space (are stored in the same kind of
     * files), but LNs are stored separately from BINs, i.e., there is no
     * clustering or co-location of a BIN and its child LNs.
     * <p>
     * With embedded LNs, a whole record may be stored inside a BIN (i.e.,
     * a BIN slot may contain both the key and the data portion of a record).
     * Specifically, a record will be "embedded" if the size (in bytes) of its
     * data portion is less than or equal to the value of the
     * TREE_MAX_EMBEDDED_LN configuration parameter. The decision to embed a
     * record or not is taken on a record-by-record basis. As a result, a BIN
     * may contain both embedded and non-embedded records. The "embeddedness"
     * of a record is a dynamic property: a size-changing update may turn a
     * non-embedded record to an embedded one or vice-versa.
     * <p>
     * Notice that even though a record may be embedded, when the record is
     * inserted, updated, or deleted an LN for that record is still generated
     * and written to disk. This is because LNs also act as log records,
     * which are needed during recovery and/or transaction abort to undo/redo
     * operations that are/are-not currently reflected in the BINs. However,
     * during normal processing, these LNs will never be fetched from disk.
     * <p>
     * Obviously, embedding records has the performance advantage that no
     * extra disk read is needed to fetch the record data (i.e., the LN)
     * during read operations. This is especially true for operations like
     * cursor scans and for random searches within key ranges whose
     * containing BINs can fit in the JE cache (in other words when there
     * is locality of reference). Furthermore, embedded records do not need
     * to be migrated during cleaning; they are considered obsolete by default,
     * because they will never be needed again after their containing log file
     * is deleted. This makes cleaning faster, and more importantly, avoids
     * the dirtying of the parent BINs, which would otherwise cause even more
     * cleaning later.
     * <p>
     * On the other hand, embedded LNs make the BINs larger, which can lead to
     * more cache eviction of BINs and the associated performance problems.
     * When eviction does occur, performance can deteriorate as the size of
     * the data portion of the records grows. This is especially true for
     * insertion-only workloads. Therefore, increasing the value of 
     * TREE_MAX_EMBEDDED_LN beyond the default value of 16 bytes should be
     * done "carefully": by considering the kind of workloads that will be run
     * against BDB-JE and their relative importance and expected response
     * times, and by running performance tests with both embedded and
     * non-embedded LNs.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>16</td>
     * <td>0</td>
     * <td>Integer.MAX_VALUE</td>
     * </tr>
     * </table></p>
     */
    public static final String TREE_MAX_EMBEDDED_LN = "je.tree.maxEmbeddedLN";

    /**
     * @deprecated as of JE 6.0.  The {@link #TREE_BIN_DELTA} param alone now
     * determines whether a delta is logged.
     */
    public static final String TREE_MAX_DELTA = "je.tree.maxDelta";

    /**
     * If more than this percentage of entries are changed on a BIN, log a a
     * full version instead of a delta.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>25</td>
     * <td>0</td>
     * <td>75</td>
     * </tr>
     * </table></p>
     */
    public static final String TREE_BIN_DELTA = "je.tree.binDelta";

    /**
     * The minimum bytes allocated out of the memory cache to hold Btree data
     * including internal nodes and record keys and data.  If the specified
     * value is larger than the size initially available in the cache, it will
     * be truncated to the amount available.
     *
     * <p>{@link #TREE_MIN_MEMORY} is the minimum for a single environment.  By
     * default, 500 KB or the size initially available in the cache is used,
     * whichever is smaller.</p>
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>Yes</td>
     * <td>512000 (500K)</td>
     * <td>51200 (50K)</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String TREE_MIN_MEMORY = "je.tree.minMemory";

    /**
     * Specifies the maximum unprefixed key length for use in the compact
     * in-memory key representation.
     *
     * <p>In the Btree, the JE in-memory cache, the default representation for
     * keys uses a byte array object per key.  The per-key object overhead of
     * this approach ranges from 20 to 32 bytes, depending on the JVM
     * platform.</p>
     *
     * <p>To reduce memory overhead, a compact representation can instead be
     * used where keys will be represented inside a single byte array instead
     * of having one byte array per key. Within the single array, all keys are
     * assigned a storage size equal to that taken up by the largest key, plus
     * one byte to hold the actual key length.  The use of the fixed size array
     * reduces Java GC activity as well as memory overhead.</p>
     *
     * <p>In order for the compact representation to reduce memory usage, all
     * keys in a database, or in a Btree internal node, must be roughly the
     * same size.  The more fully populated the internal node, the more the
     * savings with this representation since the single byte array is sized to
     * hold the maximum number of keys in the internal node, regardless of the
     * actual number of keys that are present.</p>
     *
     * <p>It's worth noting that the storage savings of the compact
     * representation are realized in addition to the storage benefits of key
     * prefixing (if it is configured), since the keys stored in the key array
     * are the smaller key values after the prefix has been stripped, reducing
     * the length of the key and making it more likely that it's small enough
     * for this specialized representation.  This configuration parameter
     * ({@code TREE_COMPACT_MAX_KEY_LENGTH}) is the maximum key length, not
     * including the common prefix, for the keys in a Btree internal node
     * stored using the compact representation.  See {@link
     * DatabaseConfig#setKeyPrefixing}.</p>
     *
     * <p>The compact representation is used automatically when both of the
     * following conditions hold.</p>
     * <ul>
     * <li>All keys in a Btree internal node must have an unprefixed length
     * that is less than or equal to the length specified by this parameter
     * ({@code TREE_COMPACT_MAX_KEY_LENGTH}).</li>
     * <li>If key lengths vary by large amounts within an internal node, the
     * wasted space of the fixed length storage may negate the benefits of the
     * compact representation and cause more memory to be used than with the
     * default representation.  In that case, the default representation will
     * be used.</li>
     * </ul>
     *
     * <p>If this configuration parameter is set to zero, the compact
     * representation will not be used.</p>
     *
     * <p>The default value of this configuration parameter is 16 bytes.  The
     * potential drawbacks of specifying a larger length are:</p>
     * <ul>
     * <li>Insertion and deletion for larger keys move bytes proportional to
     * the storage length of the keys.</li>
     * <li>With the compact representation, all operations create temporary
     * byte arrays for each key involved in the operation.  Larger byte arrays
     * mean more work for the Java GC, even though these objects are short
     * lived.</li>
     * </ul>
     *
     * <p>Mutation of the key representation between the default and compact
     * approaches is automatic on a per-Btree internal node basis.  For
     * example, if a key that exceeds the configured length is added to a node
     * that uses the compact representation, the node is automatically
     * mutated to the default representation.  A best effort is made to
     * prevent frequent mutations that could increase Java GC activity.</p>
     *
     * <p>To determine how often the compact representation is used in a
     * running application, see {@link EnvironmentStats#getNINCompactKeyIN}.</p>
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>16</td>
     * <td>0</td>
     * <td>256</td>
     * </tr>
     * </table></p>
     *
     * @see DatabaseConfig#setKeyPrefixing
     * @see EnvironmentStats#getNINCompactKeyIN
     *
     * @since 5.0
     */
    public static final String TREE_COMPACT_MAX_KEY_LENGTH =
        "je.tree.compactMaxKeyLength";

    /**
     * The compressor thread wakeup interval in microseconds.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>5 s</td>
     * <td>1 s</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String COMPRESSOR_WAKEUP_INTERVAL =
        "je.compressor.wakeupInterval";

    /**
     * The number of times to retry a compression run if a deadlock occurs.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>3</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String COMPRESSOR_DEADLOCK_RETRY =
        "je.compressor.deadlockRetry";

    /**
     * The lock timeout for compressor transactions in microseconds.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>500 ms</td>
     * <td>0</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String COMPRESSOR_LOCK_TIMEOUT =
        "je.compressor.lockTimeout";

    /**
     * @deprecated as of 3.3.87.  Compression of the root node no longer has
     * any benefit and this feature has been removed.  This parameter has no
     * effect.
     */
    public static final String COMPRESSOR_PURGE_ROOT =
        "je.compressor.purgeRoot";

    /**
     * When eviction occurs, the evictor will push memory usage to this number
     * of bytes below {@link #MAX_MEMORY}.  No more than 50% of je.maxMemory
     * will be evicted per eviction cycle, regardless of this setting.
     * <p>
     * When using the shared cache feature, the value of this property is
     * applied the first time the cache is set up. New environments that
     * join the cache do not alter the cache setting.
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>No</td>
     * <td>524288 (512K)</td>
     * <td>1024 (1K)</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_EVICT_BYTES = "je.evictor.evictBytes";

    /**
     * @deprecated as of JE 6.0. This parameter is ignored by the new, more
     * efficient and more accurate evictor.
     */
    public static final String EVICTOR_NODES_PER_SCAN =
        "je.evictor.nodesPerScan";

    /**
     * At this percentage over the allotted cache, critical eviction will
     * start.  For example, if this parameter is 5, then when the cache size is
     * 5% over its maximum or 105% full, critical eviction will start.
     * <p>
     * Critical eviction is eviction performed in application threads as part
     * of normal database access operations.  Background eviction, on the other
     * hand, is performed in JE evictor threads as well as during log cleaning
     * and checkpointing.  Background eviction is unconditionally started when
     * the cache size exceeds its maximum.  When critical eviction is also
     * performed (concurrently with background eviction), it helps to ensure
     * that the cache size does not continue to grow, but can have a negative
     * impact on operation latency.
     * <p>
     * By default this parameter is zero, which means that critical eviction
     * will start as soon as the cache size exceeds its maximum.  Some
     * applications may wish to set this parameter to a non-zero value to
     * improve operation latency, when eviction is a significant performance
     * factor and latency requirements are not being satisfied.
     * <p>
     * When setting this parameter to a non-zero value, for example 5, be sure
     * to reserve enough heap memory for the cache size to be over its
     * configured maximum, for example 105% full.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>0</td>
     * <td>0</td>
     * <td>1000</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_CRITICAL_PERCENTAGE =
        "je.evictor.criticalPercentage";

    /**
     * @deprecated as of JE 4.1, since the single evictor thread has
     * been replaced be a more robust thread pool.
     */
    public static final String EVICTOR_DEADLOCK_RETRY =
        "je.evictor.deadlockRetry";

    /**
     * @deprecated  as of JE 6.0. This parameter is ignored by the new,
     * more efficient and more accurate evictor.
     */
    public static final String EVICTOR_LRU_ONLY = "je.evictor.lruOnly";

    /**
     * The number of LRU lists in the main JE cache.
     *
     * Ideally, all nodes managed by an LRU eviction policy should appear in
     * a single LRU list, ordered by the "hotness" of each node. However,
     * such a list is accessed very frequently by multiple threads, and can
     * become a synchronization bottleneck. To avoid this problem, the
     * evictor can employ multiple LRU lists. The nLRULists parameter
     * specifies the number of LRU lists to be used. Increasing the number
     * of LRU lists alleviates any potential synchronization bottleneck, but
     * it also decreases the quality of the LRU approximation. 
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>4</td>
     * <td>1</td>
     * <td>32</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_N_LRU_LISTS = "je.evictor.nLRULists";

    /**
     * Call Thread.yield() at each check for cache overflow. This improves GC
     * performance on some systems.
     * <p>
     * When using the shared cache feature, the value of this property is
     * applied the first time the cache is set up. New environments that
     * join the cache do not alter the cache setting.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_FORCED_YIELD = "je.evictor.forcedYield";

    /**
     * The minimum number of threads in the eviction thread pool. These threads
     * help keep memory usage within cache bounds, offloading work from
     * application threads.
     *
     * {@link #EVICTOR_CORE_THREADS}, {@link #EVICTOR_MAX_THREADS} and {@link
     * #EVICTOR_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>yes</td>
     * <td>1</td>
     * <td>0</td>
     * <td>Integer.MAX_VALUE</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_CORE_THREADS = "je.evictor.coreThreads";

    /**
     * The maximum number of threads in the eviction thread pool. These threads
     * help keep memory usage within cache bound, offloading work from
     * application threads. If the eviction thread pool receives more work, it
     * will allocate up to this number of threads. These threads will terminate
     * if they are idle for more than the time indicated by {@link
     * #EVICTOR_KEEP_ALIVE}.
     *
     * {@link #EVICTOR_CORE_THREADS}, {@link #EVICTOR_MAX_THREADS} and {@link
     * #EVICTOR_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>yes</td>
     * <td>10</td>
     * <td>1</td>
     * <td>Integer.MAX_VALUE</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_MAX_THREADS = "je.evictor.maxThreads";

    /**
     * The duration that excess threads in the eviction thread pool will stay
     * idle. After this period, idle threads will terminate.
     *
     * {@link #EVICTOR_CORE_THREADS}, {@link #EVICTOR_MAX_THREADS} and {@link
     * #EVICTOR_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>10 min</td>
     * <td>1 s</td>
     * <td>1 d</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String EVICTOR_KEEP_ALIVE = "je.evictor.keepAlive";

    /**
     * Allow Bottom Internal Nodes (BINs) to be written in a delta format
     * during eviction.  Using a delta format will improve write and log
     * cleaning performance, but may reduce read performance if BINs are not
     * maintained in cache memory.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String EVICTOR_ALLOW_BIN_DELTAS =
        "je.evictor.allowBinDeltas";

    /**
     * When eviction occurs, the off-heap evictor will push memory usage to
     * this number of bytes below {@link #MAX_OFF_HEAP_MEMORY}. This
     * determines the size of a single eviction batch.

     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>No</td>
     * <td>1048576 (1MB)</td>
     * <td>1024 (1K)</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String OFFHEAP_EVICT_BYTES = "je.offHeap.evictBytes";

    /**
     * The number of LRU lists in the off-heap JE cache.
     *
     * Ideally, all nodes managed by an LRU eviction policy should appear in
     * a single LRU list, ordered by the "hotness" of each node. However,
     * such a list is accessed very frequently by multiple threads, and can
     * become a synchronization bottleneck. To avoid this problem, the
     * evictor can employ multiple LRU lists. The nLRULists parameter
     * specifies the number of LRU lists to be used. Increasing the number
     * of LRU lists alleviates any potential synchronization bottleneck, but
     * it also decreases the quality of the LRU approximation.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>4</td>
     * <td>1</td>
     * <td>32</td>
     * </tr>
     * </table></p>
     */
    public static final String OFFHEAP_N_LRU_LISTS = "je.evictor.nLRULists";

    /**
     * Can be used to add a checksum to each off-heap block when the block is
     * written, and validate the checksum when the block is read, for debugging
     * purposes.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String OFFHEAP_CHECKSUM = "je.offHeap.checksum";

    /**
     * The minimum number of threads in the off-heap eviction thread pool.
     * These threads help keep memory usage within cache bounds, offloading
     * work from application threads.
     *
     * {@link #OFFHEAP_CORE_THREADS}, {@link #OFFHEAP_MAX_THREADS} and {@link
     * #OFFHEAP_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>yes</td>
     * <td>1</td>
     * <td>0</td>
     * <td>Integer.MAX_VALUE</td>
     * </tr>
     * </table></p>
     */
    public static final String OFFHEAP_CORE_THREADS = "je.offHeap.coreThreads";

    /**
     * The maximum number of threads in the off-heap eviction thread pool.
     * These threads help keep memory usage within cache bound, offloading
     * work from application threads. If the eviction thread pool receives
     * more work, it will allocate up to this number of threads. These
     * threads will terminate if they are idle for more than the time
     * indicated by {@link #OFFHEAP_KEEP_ALIVE}.
     *
     * {@link #OFFHEAP_CORE_THREADS}, {@link #OFFHEAP_MAX_THREADS} and {@link
     * #OFFHEAP_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>yes</td>
     * <td>10</td>
     * <td>1</td>
     * <td>Integer.MAX_VALUE</td>
     * </tr>
     * </table></p>
     */
    public static final String OFFHEAP_MAX_THREADS = "je.offHeap.maxThreads";

    /**
     * The duration that excess threads in the off-heap eviction thread pool
     * will stay idle. After this period, idle threads will terminate.
     *
     * {@link #OFFHEAP_CORE_THREADS}, {@link #OFFHEAP_MAX_THREADS} and {@link
     * #OFFHEAP_KEEP_ALIVE} are used to configure the core, max and keepalive
     * attributes for the {@link java.util.concurrent.ThreadPoolExecutor} which
     * implements the eviction thread pool.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>10 min</td>
     * <td>1 s</td>
     * <td>1 d</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String OFFHEAP_KEEP_ALIVE = "je.offHeap.keepAlive";

    /**
     * Ask the checkpointer to run every time we write this many bytes to the
     * log. If set, supersedes {@link #CHECKPOINTER_WAKEUP_INTERVAL}. To use
     * time based checkpointing, set this to 0.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>No</td>
     * <td>20000000 (20M)</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CHECKPOINTER_BYTES_INTERVAL =
        "je.checkpointer.bytesInterval";

    /**
     * The checkpointer wakeup interval in microseconds. By default, this
     * is inactive and we wakeup the checkpointer as a function of the
     * number of bytes written to the log ({@link
     * #CHECKPOINTER_BYTES_INTERVAL}).
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>0</td>
     * <td>1 s</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String CHECKPOINTER_WAKEUP_INTERVAL =
        "je.checkpointer.wakeupInterval";

    /**
     * The number of times to retry a checkpoint if it runs into a deadlock.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>3</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CHECKPOINTER_DEADLOCK_RETRY =
        "je.checkpointer.deadlockRetry";

    /**
     * If true, the checkpointer uses more resources in order to complete the
     * checkpoint in a shorter time interval.  Btree latches are held and other
     * threads are blocked for a longer period.  When set to true, application
     * response time may be longer during a checkpoint.
     *
     * <p><table border"1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String CHECKPOINTER_HIGH_PRIORITY =
        "je.checkpointer.highPriority";

    /**
     * The cleaner will keep the total disk space utilization percentage above
     * this value.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>50</td>
     * <td>0</td>
     * <td>90</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_MIN_UTILIZATION =
        "je.cleaner.minUtilization";

    /**
     * A log file will be cleaned if its utilization percentage is below this
     * value, irrespective of total utilization.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>5</td>
     * <td>0</td>
     * <td>50</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_MIN_FILE_UTILIZATION =
        "je.cleaner.minFileUtilization";

    /**
     * The cleaner checks disk utilization every time we write this many bytes
     * to the log.  If zero (and by default) it is set to the {@link
     * #LOG_FILE_MAX} value divided by four.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Long</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_BYTES_INTERVAL =
        "je.cleaner.bytesInterval";

    /**
     * If true, the cleaner will fetch records to determine their size and more
     * accurately calculate log utilization.  Normally when a record is updated
     * or deleted without first being read (sometimes called a blind
     * delete/update), the size of the previous version of the record is
     * unknown and therefore the cleaner's utilization calculations may be
     * incorrect.  Setting this parameter to true will cause a record to be
     * read during a blind delete/update, in order to determine its size.  This
     * will ensure that the cleaner's utilization calculations are correct, but
     * will cause more (potentially random) IO.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     *
     * @see #CLEANER_ADJUST_UTILIZATION
     */
    public static final String CLEANER_FETCH_OBSOLETE_SIZE =
        "je.cleaner.fetchObsoleteSize";

    /**
     * @deprecated in JE 6.3. Adjustments are no longer needed because LN log
     * sizes have been stored in the Btree since JE 6.0.
     */
    public static final String CLEANER_ADJUST_UTILIZATION =
        "je.cleaner.adjustUtilization";

    /**
     * The number of times to retry cleaning if a deadlock occurs.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>3</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_DEADLOCK_RETRY =
        "je.cleaner.deadlockRetry";

    /**
     * The lock timeout for cleaner transactions in microseconds.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>500 ms</td>
     * <td>0</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String CLEANER_LOCK_TIMEOUT = "je.cleaner.lockTimeout";

    /**
     * If true (the default setting), the cleaner deletes log files after
     * successful cleaning.
     *
     * This parameter may be set to false for diagnosing log cleaning problems.
     * For example, if a bug causes a LOG_FILE_NOT_FOUND exception, when
     * reproducing the problem it is often necessary to avoid deleting files so
     * they can be used for diagnosis. When this parameter is false:
     * <ul>
     *     <li>
     *     Rather than delete files that are successfully cleaned, the cleaner
     *     renames them.
     *     </li>
     *     <li>
     *     When renaming a file, its extension is changed from ".jdb" to ".del"
     *     and its last modification date is set to the current time.
     *     </li>
     *     <li>
     *     Depending on the setting of the {@link #CLEANER_USE_DELETED_DIR}
     *     parameter, the file is either renamed in its current data directory
     *     (the default), or moved into the "deleted" sub-directory.
     *     </li>
     * </ul>
     * <p>
     * When this parameter is set to false, disk usage may grow without bounds
     * and the application is responsible for removing the cleaned files. It
     * may be necessary to write a script for deleting the least recently
     * cleaned files when disk usage is low. The .del extension and the last
     * modification time can be leveraged to write such a script. The "deleted"
     * sub-directory can be used to avoid granting write or delete permissions
     * for the main data directory to the script.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>true</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_EXPUNGE = "je.cleaner.expunge";

    /**
     * When {@link #CLEANER_EXPUNGE} is false, the {@code
     * CLEANER_USE_DELETED_DIR} parameter determines whether successfully
     * cleaned files are moved to the "deleted" sub-directory.
     *
     * {@code CLEANER_USE_DELETED_DIR} applies only when {@link
     * #CLEANER_EXPUNGE} is false. When {@link #CLEANER_EXPUNGE} is true,
     * successfully cleaned files are deleted and the {@code
     * CLEANER_USE_DELETED_DIR} parameter setting is ignored.
     * <p>
     * When {@code CLEANER_USE_DELETED_DIR} is true (and {@code
     * CLEANER_EXPUNGE} is false), the cleaner will move successfully cleaned
     * data files (".jdb" files) to the "deleted" sub-directory of the
     * Environment directory, in addition to changing the file extension to
     * "*.del". In this case, the "deleted" sub-directory must have been
     * created by the application before opening the Environment. This allows
     * the application to control permissions on this sub-directory. When
     * multiple data directories are used ({@link #LOG_N_DATA_DIRECTORIES}), a
     * "deleted" sub-directory must be created under each data directory. Note
     * that {@link java.io.File#renameTo(File)} is used to move the file, and
     * this method may or may not support moving the file to a different volume
     * (when the "deleted" directory is a file system link) on a particular
     * platform.
     * <p>
     * When {@code CLEANER_USE_DELETED_DIR} is false (and {@code
     * CLEANER_EXPUNGE} is false), the cleaner will change the file extension
     * of successfully cleaned data files from ".jdb" to ".del", but will not
     * move the files to a different directory.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_USE_DELETED_DIR =
        "je.cleaner.useDeletedDir";

    /**
     * The minimum age of a file (number of files between it and the active
     * file) to qualify it for cleaning under any conditions.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>2</td>
     * <td>1</td>
     * <td>1000</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_MIN_AGE = "je.cleaner.minAge";

    /**
     * The maximum number of log files in the cleaner's backlog, or zero if
     * there is no limit.  Changing this property can impact the performance of
     * some out-of-memory applications.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>0</td>
     * <td>100000</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_MAX_BATCH_FILES =
        "je.cleaner.maxBatchFiles";

    /**
     * The read buffer size for cleaning.  If zero (the default), then {@link
     * #LOG_ITERATOR_READ_SIZE} value is used.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>0</td>
     * <td>128</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_READ_SIZE = "je.cleaner.readSize";

    /**
     * Tracking of detailed cleaning information will use no more than this
     * percentage of the cache.  The default value is 2% of {@link
     * #MAX_MEMORY}. If 0 and {@link #SHARED_CACHE} is true, use 2% divided by
     * N where N is the number of environments sharing the global cache.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>2</td>
     * <td>1</td>
     * <td>90</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_DETAIL_MAX_MEMORY_PERCENTAGE =
        "je.cleaner.detailMaxMemoryPercentage";

    /**
     * Specifies a list of files or file ranges to be cleaned at a time when no
     * other log cleaning is necessary.  This parameter is intended for use in
     * forcing the cleaning of a large number of log files.  File numbers are
     * in hex and are comma separated or hyphen separated to specify ranges,
     * e.g.: '9,a,b-d' will clean 5 files.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>String</td>
     * <td>No</td>
     * <td>""</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_FORCE_CLEAN_FILES =
        "je.cleaner.forceCleanFiles";

    /**
     * All log files having a log version prior to the specified version will
     * be cleaned at a time when no other log cleaning is necessary.  Intended
     * for use in upgrading old format log files forward to the current log
     * format version, e.g., to take advantage of format improvements; note
     * that log upgrading is optional.  The default value zero (0) specifies
     * that no upgrading will occur.  The value negative one (-1) specifies
     * upgrading to the current log version.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>0</td>
     * <td>-1</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_UPGRADE_TO_LOG_VERSION =
        "je.cleaner.upgradeToLogVersion";

    /**
     * The number of threads allocated by the cleaner for log file processing.
     * If the cleaner backlog becomes large, try increasing this value.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>1</td>
     * <td>1</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_THREADS = "je.cleaner.threads";

    /**
     * The look ahead cache size for cleaning in bytes.  Increasing this value
     * can reduce the number of Btree lookups.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>8192 (8K)</td>
     * <td>0</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String CLEANER_LOOK_AHEAD_CACHE_SIZE =
        "je.cleaner.lookAheadCacheSize";

    /**
     * @deprecated This parameter is ignored and proactive migration is no
     * longer supported due to its negative impact on eviction and Btree
     * splits. To reduce a cleaner backlog, configure more cleaner threads.
     */
    public static final String CLEANER_FOREGROUND_PROACTIVE_MIGRATION =
        "je.cleaner.foregroundProactiveMigration";

    /**
     * @deprecated This parameter is ignored and proactive migration is no
     * longer supported due to its negative impact on eviction and
     * checkpointing. To reduce a cleaner backlog, configure more cleaner
     * threads.
     */
    public static final String CLEANER_BACKGROUND_PROACTIVE_MIGRATION =
        "je.cleaner.backgroundProactiveMigration";

    /**
     * @deprecated This parameter is ignored and lazy migration is no longer
     * supported due to its negative impact on eviction and checkpointing.
     * To reduce a cleaner backlog, configure more cleaner threads.
     */
    public static final String CLEANER_LAZY_MIGRATION =
        "je.cleaner.lazyMigration";

    /**
     * The timeout for Disk Ordered Scan producer thread queue offers in
     * milliseconds.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>10 secs</td>
     * <td>0</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String DOS_PRODUCER_QUEUE_TIMEOUT =
        "je.env.diskOrderedScanLockTimeout";

    /**
     * Number of Lock Tables.  Set this to a value other than 1 when an
     * application has multiple threads performing concurrent JE operations.
     * It should be set to a prime number, and in general not higher than the
     * number of application threads performing JE operations.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>No</td>
     * <td>1</td>
     * <td>1</td>
     * <td>32767 (32K)</td>
     * </tr>
     * </table></p>
     */
    public static final String LOCK_N_LOCK_TABLES = "je.lock.nLockTables";

    /**
     * The {@link #setLockTimeout(long,TimeUnit) LockTimeout} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>500 ms</td>
     * <td>0</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see #setLockTimeout(long,TimeUnit)
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String LOCK_TIMEOUT = "je.lock.timeout";

    /**
     * Whether to throw old-style lock exceptions for compatibility with older
     * JE releases.
     *
     * <p>In JE 3.3 and earlier, {@link DeadlockException} or a subclass of it
     * was always thrown when a lock conflict occurred.  Applications typically
     * caught {@link DeadlockException} in order to detect lock conflicts and
     * determine whether to retry a transaction.  {@link DeadlockException}
     * itself was thrown when a lock or transaction timeout occurred and {@link
     * LockNotGrantedException} (a subclass of {@link DeadlockException}) was
     * thrown when a lock conflict occurred for a no-wait transaction (see
     * {@link TransactionConfig#setNoWait}).</p>
     *
     * <p>In all releases after JE 3.3, new exceptions and the new base class
     * {@link LockConflictException} are available.  Now, {@link
     * LockConflictException} should be caught to handle lock conflicts in a
     * general manner, instead of catching {@link DeadlockException}.  New
     * exceptions are now thrown as follows:
     * <ul>
     * <li>{@link LockTimeoutException} is now thrown when a lock timeout
     * occurs, rather than {@link DeadlockException}.</li>
     * <li>{@link TransactionTimeoutException} is now thrown when a transaction
     * timeout occurs, rather than {@link DeadlockException}.</li>
     * <li>{@link LockNotAvailableException} is now thrown when a lock conflict
     * occurs for a no-wait transaction, rather than {@link
     * LockNotGrantedException}.</li>
     * </ul>
     * <p>These three new exceptions are subclasses of {@link
     * LockConflictException}.  {@link DeadlockException} is also now a
     * subclass of {@link LockConflictException}, but is not currently thrown
     * by JE because true deadlock detection is not used in JE.  Currently,
     * lock timeouts are used instead.  When true deadlock detection is added
     * to JE in the future, {@link DeadlockException} will be thrown. {@link
     * LockNotGrantedException} has been deprecated and replaced by {@link
     * LockNotAvailableException}.</p>
     *
     * <li>The {@link EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} configuraton
     * parameter may be explicitly set to true to enable the old exception
     * behavior.  By default, this parameter is false.</li>
     *
     * <p>Unless {@link EnvironmentConfig#LOCK_OLD_LOCK_EXCEPTIONS} is set to
     * true, the following changes must be made to JE applications that upgrade
     * from JE 3.3 or earlier.</p>
     * <ol>
     * <li>All occurrences of {@link DeadlockException} must be replaced with
     * {@link LockConflictException} or one of its non-deprecated subclasses
     * ({@link LockTimeoutException}, {@link TransactionTimeoutException}, or
     * {@link LockNotAvailableException}).  It is strongly recommended to
     * replace it with {@link LockConflictException}, since catching this
     * exception will catch true deadlocks in the future and other types of
     * lock conflicts.  All lock conflicts all typically handled in the same
     * way, which is normally to abort and retry the transaction.</li>
     * <li>All occurances of {@link LockNotGrantedException} must be replaced
     * with {@link LockNotAvailableException}.  {@link LockNotGrantedException}
     * has been deprecated because it misleadingly extends {@link
     * DeadlockException}.</li>
     * </ol>
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String LOCK_OLD_LOCK_EXCEPTIONS =
        "je.lock.oldLockExceptions";

    /**
     * The {@link #setTxnTimeout TxnTimeout} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>0</td>
     * <td>0</td>
     * <td>75 min</td>
     * </tr>
     * </table></p>
     *
     * @see #setTxnTimeout
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String TXN_TIMEOUT = "je.txn.timeout";

    /**
     * The {@link #setTxnSerializableIsolation TxnSerializableIsolation}
     * property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>No</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     *
     * @see #setTxnSerializableIsolation
     */
    public static final String TXN_SERIALIZABLE_ISOLATION =
        "je.txn.serializableIsolation";

    /**
     * @hidden
     * The {@link #setDurability durability} property.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
      * <td>String</td>
     * <td>Yes</td>
     * <td>null</td>
     * </tr>
     * </table></p>
     *
     * The format of the durability string is described at
     * {@link Durability#parse(String)}
     *
     * @see Durability
     */
    public static final String TXN_DURABILITY = "je.txn.durability";

    /**
     * Set this parameter to true to add stacktrace information to deadlock
     * (lock timeout) exception messages.  The stack trace will show where each
     * lock was taken.  The default is false, and true should only be used
     * during debugging because of the added memory/processing cost.  This
     * parameter is 'static' across all environments.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String TXN_DEADLOCK_STACK_TRACE =
        "je.txn.deadlockStackTrace";

    /**
     * Dump the lock table when a lock timeout is encountered, for debugging
     * assistance.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>false</td>
     * </tr>
     * </table></p>
     */
    public static final String TXN_DUMP_LOCKS = "je.txn.dumpLocks";

    /**
     * @deprecated in favor of <code>FILE_LOGGING_LEVEL</code> As of JE 4.0,
     * use the standard java.util.logging configuration methodologies. To
     * enable logging output to the je.info files, set
     * com.sleepycat.je.util.FileHandler.level = {@literal <LEVEL>} through the
     * java.util.logging configuration file, or through the
     * java.util.logging.LogManager. To set the handler level programmatically,
     * set "com.sleepycat.je.util.FileHandler.level" in the EnvironmentConfig
     * object.
     */
    public static final String TRACE_FILE = "java.util.logging.FileHandler.on";

    /**
     * @deprecated in favor of <code>CONSOLE_LOGGING_LEVEL</code> As of JE
     * 4.0, use the standard java.util.logging configuration
     * methodologies. To enable console output, set
     * com.sleepycat.je.util.ConsoleHandler.level = {@literal <LEVEL>} through
     * the java.util.logging configuration file, or through the
     * java.util.logging.LogManager. To set the handler level programmatically,
     * set "com.sleepycat.je.util.ConsoleHandler.level" in the
     * EnvironmentConfig object.
     */
    public static final String TRACE_CONSOLE =
        "java.util.logging.ConsoleHandler.on";

    /**
     * @deprecated As of JE 4.0, event tracing to the .jdb files has been
     * separated from the java.util.logging mechanism. This parameter has
     * no effect.
     */
    public static final String TRACE_DB = "java.util.logging.DbLogHandler.on";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To set the FileHandler output file size,
     * set com.sleepycat.je.util.FileHandler.limit = {@literal <NUMBER>}
     * through the java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_FILE_LIMIT =
        "java.util.logging.FileHandler.limit";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To set the FileHandler output file count,
     * set com.sleepycat.je.util.FileHandler.count = {@literal <NUMBER>}
     * through the java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_FILE_COUNT =
        "java.util.logging.FileHandler.count";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. Set logging levels using class names
     * through the java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_LEVEL = "java.util.logging.level";

    /**
     * Trace messages equal and above this level will be logged to the
     * console. Value should be one of the predefined
     * java.util.logging.Level values.
     * <p>
     * Setting this parameter in the je.properties file or through {@link
     * EnvironmentConfig#setConfigParam} is analogous to setting
     * the property in the java.util.logging properties file or MBean.
     * It is preferred to use the standard java.util.logging mechanisms for
     * configuring java.util.logging.Handler, but this JE parameter is provided
     * because the java.util.logging API doesn't provide a method to set
     * handler levels programmatically.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>String</td>
     * <td>No</td>
     * <td>"OFF"</td>
     * </tr>
     * </table></p>
     * @see <a href="{@docRoot}/../GettingStartedGuide/managelogging.html"
     * target="_top">Chapter 12. Logging</a>
     */
    public static final String CONSOLE_LOGGING_LEVEL =
        "com.sleepycat.je.util.ConsoleHandler.level";

    /**
     * Trace messages equal and above this level will be logged to the je.info
     * file, which is in the Environment home directory.  Value should
     * be one of the predefined java.util.logging.Level values.
     * <p>
     * Setting this parameter in the je.properties file or through {@link
     * EnvironmentConfig#setConfigParam} is analogous to setting
     * the property in the java.util.logging properties file or MBean.
     * It is preferred to use the standard java.util.logging mechanisms for
     * configuring java.util.logging.Handler, but this JE parameter is provided
     * because the java.util.logging APIs doesn't provide a method to set
     * handler levels programmatically.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>String</td>
     * <td>No</td>
     * <td>"INFO"</td>
     * </tr>
     * </table></p>
     * @see <a href="{@docRoot}/../GettingStartedGuide/managelogging.html"
     * target="_top">Chapter 12. Logging</a>
     */
    public static final String FILE_LOGGING_LEVEL =
        "com.sleepycat.je.util.FileHandler.level";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To see locking logging, set
     * com.sleepycat.je.txn.level = {@literal <LEVEL>} through the
     * java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_LEVEL_LOCK_MANAGER =
        "java.util.logging.level.lockMgr";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To see recovery logging, set
     * com.sleepycat.je.recovery.level = {@literal <LEVEL>} through the
     * java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_LEVEL_RECOVERY =
        "java.util.logging.level.recovery";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To see evictor logging, set
     * com.sleepycat.je.evictor.level = {@literal <LEVEL>} through the
     * java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_LEVEL_EVICTOR =
        "java.util.logging.level.evictor";

    /**
     * @deprecated As of JE 4.0, use the standard java.util.logging
     * configuration methodologies. To see cleaner logging, set
     * com.sleepycat.je.cleaner.level = {@literal <LEVEL>} through the
     * java.util.logging configuration file, or through the
     * java.util.logging.LogManager.
     */
    public static final String TRACE_LEVEL_CLEANER =
        "java.util.logging.level.cleaner";

    /**
     * If environment startup exceeds this duration, startup statistics are
     * logged and can be found in the je.info file.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>No</td>
     * <td>5 min</td>
     * <td>0</td>
     * <td>none</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String STARTUP_DUMP_THRESHOLD =
        "je.env.startupThreshold";

    /**
     * If true collect and log statistics. The statistics are logged in CSV
     * format and written to the log file at a user specified interval.
     * The logging occurs per-Environment when the Environment is opened
     * in read/write mode. Statistics are written to a filed named je.stat.csv.
     * Successively older files are named by adding "0", "1", "2", etc into
     * the file name. The file name format is je.stat.[version number].csv.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Boolean</td>
     * <td>Yes</td>
     * <td>True</td>
     * <td>0</td>
     * <td>none</td>
     * </tr>
     * </table></p>
     */
    public static final String STATS_COLLECT =
        "je.stats.collect";

    /**
     * Maximum number of statistics log files to retain. The rotating set of
     * files, as each file reaches a given size limit, is closed, rotated out,
     * and a new file opened. The name of the log file is je.stat.csv.
     * Successively older files are named by adding "0", "1", "2", etc into
     * the file name. The file name format is je.stat.[version number].csv.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>10</td>
     * <td>1</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String STATS_MAX_FILES =
        "je.stats.max.files";

    /**
     * Log file maximum row count for Stat collection. When the number of
     * rows in the statistics file reaches the maximum row count, the file
     * is closed, rotated out, and a new file opened. The name of the log
     * file is je.stat.csv. Successively older files are named by adding "0",
     * "1", "2", etc into the file name. The file name format is
     * je.stat.[version number].csv.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>Integer</td>
     * <td>Yes</td>
     * <td>1440</td>
     * <td>1</td>
     * <td>-none-</td>
     * </tr>
     * </table></p>
     */
    public static final String STATS_FILE_ROW_COUNT =
        "je.stats.file.row.count";

    /**
     * The duration of the statistics capture interval. Statistics are captured
     * and written to the log file at this interval.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td>
     * <td>Default</td><td>Minimum</td><td>Maximum</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>{@link <a href="#timeDuration">Duration</a>}</td>
     * <td>Yes</td>
     * <td>1 min</td>
     * <td>1 s</td>
     * <td>24 d</td>
     * </tr>
     * </table></p>
     *
     * @see <a href="EnvironmentConfig.html#timeDuration">Time Duration
     * Properties</a>
     */
    public static final String STATS_COLLECT_INTERVAL =
        "je.stats.collect.interval";

    /**
     * The directory to save the statistics log file.
     *
     * <p><table border="1">
     * <tr><td>Name</td><td>Type</td><td>Mutable</td><td>Default</td></tr>
     * <tr>
     * <td>{@value}</td>
     * <td>String</td>
     * <td>No</td>
     * <td>"NULL-> Environment home directory"</td>
     * </tr>
     * </table></p>
     */
    public static final String STATS_FILE_DIRECTORY =
        "je.stats.file.directory";

    /**
     * For unit testing, to prevent creating the utilization profile DB.
     */
    private transient boolean createUP = true;

    /**
     * For unit testing, to prevent writing utilization data during checkpoint.
     */
    private transient boolean checkpointUP = true;

    private boolean allowCreate = false;

    /**
     * For unit testing, to set readCommitted as the default.
     */
    private transient boolean txnReadCommitted = false;

    private String nodeName = null;

    /**
     * The loggingHandler is an instance and cannot be serialized.
     */
    private transient Handler loggingHandler;

    private transient
       ProgressListener<RecoveryProgress> recoveryProgressListener;

    private transient ClassLoader classLoader;

    private transient PreloadConfig dupConvertPreloadConfig;

    private CustomStats customStats;

    /**
     * Creates an EnvironmentConfig initialized with the system default
     * settings.
     */
    public EnvironmentConfig() {
        super();
    }

    /**
     * Creates an EnvironmentConfig which includes the properties specified in
     * the properties parameter.
     *
     * @param properties Supported properties are described in this class
     *
     * @throws IllegalArgumentException If any properties read from the
     * properties param are invalid.
     */
    public EnvironmentConfig(Properties properties)
        throws IllegalArgumentException {

        super(properties);
    }

    /**
     * If true, creates the database environment if it doesn't already exist.
     *
     * @param allowCreate If true, the database environment is created if it
     * doesn't already exist.
     *
     * @return this
     */
    public EnvironmentConfig setAllowCreate(boolean allowCreate) {

        setAllowCreateVoid(allowCreate);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setAllowCreateVoid(boolean allowCreate) {
        this.allowCreate = allowCreate;
    }

    /**
     * Returns a flag that specifies if we may create this environment.
     *
     * @return true if we may create this environment.
     */
    public boolean getAllowCreate() {

        return allowCreate;
    }

    /**
     * Configures the lock timeout.
     *
     * <p>Equivalent to setting the je.lock.timeout parameter in the
     * je.properties file.</p>
     *
     * @param timeout The lock timeout for all transactional and
     * non-transactional operations.  A value of zero disables lock timeouts,
     * meaning that no lock wait time limit is enforced and a deadlocked
     * operation will block indefinitely.  We strongly recommend that a large
     * timeout value, rather than zero, is used when deadlocks are not
     * expected.  That way, a timeout exception will be thrown when an
     * unexpected deadlock occurs.
     *
     * @param unit the {@code TimeUnit} of the timeout value. May be null only
     * if timeout is zero.
     *
     * @return this
     *
     * @throws IllegalArgumentException if the value of timeout is invalid
     *
     * @see Transaction#setLockTimeout(long,TimeUnit)
     */
    public EnvironmentConfig setLockTimeout(long timeout, TimeUnit unit)
        throws IllegalArgumentException {

        setLockTimeoutVoid(timeout, unit);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setLockTimeoutVoid(long timeout, TimeUnit unit)
        throws IllegalArgumentException {

        DbConfigManager.setDurationVal(props, EnvironmentParams.LOCK_TIMEOUT,
                                       timeout, unit, validateParams);
    }

    /**
     * Configures the lock timeout, in microseconds.  This method is equivalent
     * to:
     *
     * <pre>setLockTimeout(long, TimeUnit.MICROSECONDS);</pre>
     *
     * @deprecated as of 4.0, replaced by {@link #setLockTimeout(long,
     * TimeUnit)}.
     */
    public EnvironmentConfig setLockTimeout(long timeout)
        throws IllegalArgumentException {

        setLockTimeoutVoid(timeout);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setLockTimeoutVoid(long timeout)
        throws IllegalArgumentException {

        setLockTimeout(timeout, TimeUnit.MICROSECONDS);
    }

    /**
     * Returns the lock timeout setting.
     *
     * @param unit the {@code TimeUnit} of the returned value. May not be null.
     *
     * A value of 0 means no timeout is set.
     */
    public long getLockTimeout(TimeUnit unit) {

        return DbConfigManager.getDurationVal
            (props, EnvironmentParams.LOCK_TIMEOUT, unit);
    }

    /**
     * Returns the lock timeout setting, in microseconds.  This method is
     * equivalent to:
     *
     * <pre>getLockTimeout(TimeUnit.MICROSECONDS);</pre>
     *
     * @deprecated as of 4.0, replaced by {@link #getLockTimeout(TimeUnit)}.
     */
    public long getLockTimeout() {
        return getLockTimeout(TimeUnit.MICROSECONDS);
    }

    /**
     * Configures the database environment to be read-only, and any attempt to
     * modify a database will fail.
     *
     * <p>A read-only environment has several limitations and is recommended
     * only in special circumstances.  Note that there is no performance
     * advantage to opening an environment read-only.</p>
     *
     * <p>The primary reason for opening an environment read-only is to open a
     * single environment in multiple JVM processes.  Only one JVM process at a
     * time may open the environment read-write.  See {@link
     * EnvironmentLockedException}.</p>
     *
     * <p>When the environment is open read-only, the following limitations
     * apply.</p>
     * <ul>
     * <li>In the read-only environment no writes may be performed, as
     * expected, and databases must be opened read-only using {@link
     * DatabaseConfig#setReadOnly}.</li>
     * <li>The read-only environment receives a snapshot of the data that is
     * effectively frozen at the time the environment is opened. If the
     * application has the environment open read-write in another JVM process
     * and modifies the environment's databases in any way, the read-only
     * version of the data will not be updated until the read-only JVM process
     * closes and reopens the environment (and by extension all databases in
     * that environment).</li>
     * <li>If the read-only environment is opened while the environment is in
     * use by another JVM process in read-write mode, opening the environment
     * read-only (recovery) is likely to take longer than it does after a clean
     * shutdown.  This is due to the fact that the read-write JVM process is
     * writing and checkpoints are occurring that are not coordinated with the
     * read-only JVM process.  The effect is similar to opening an environment
     * after a crash.</li>
     * <li>In a read-only environment, the JE cache will contain information
     * that cannot be evicted because it was reconstructed by recovery and
     * cannot be flushed to disk.  This means that the read-only environment
     * may not be suitable for operations that use large amounts of memory, and
     * poor performance may result if this is attempted.</li>
     * <li>In a read-write environment, the log cleaner will be prohibited from
     * deleting log files for as long as the environment is open read-only in
     * another JVM process.  This may cause disk usage to rise, and for this
     * reason it is not recommended that an environment is kept open read-only
     * in this manner for long periods.</li>
     * </ul>
     *
     * <p>For these reasons, it is recommended that a read-only environment be
     * used only for short periods and for operations that are not performance
     * critical or memory intensive.  With few exceptions, all application
     * functions that require access to a JE environment should be built into a
     * single application so that they can be performed in the JVM process
     * where the environment is open read-write.</p>
     *
     * <p>In most applications, opening an environment read-only can and should
     * be avoided.</p>
     *
     * @param readOnly If true, configure the database environment to be read
     * only, and any attempt to modify a database will fail.
     *
     * @return this
     */
    public EnvironmentConfig setReadOnly(boolean readOnly) {

        setReadOnlyVoid(readOnly);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setReadOnlyVoid(boolean readOnly) {

        DbConfigManager.setBooleanVal(props, EnvironmentParams.ENV_RDONLY,
                                      readOnly, validateParams);
    }

    /**
     * Returns true if the database environment is configured to be read only.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return true if the database environment is configured to be read only.
     */
    public boolean getReadOnly() {

        return DbConfigManager.getBooleanVal(props,
                                             EnvironmentParams.ENV_RDONLY);
    }

    /**
     * Configures the database environment for transactions.
     *
     * <p>This configuration option should be used when transactional
     * guarantees such as atomicity of multiple operations and durability are
     * important.</p>
     *
     * @param transactional If true, configure the database environment for
     * transactions.
     *
     * @return this
     */
    public EnvironmentConfig setTransactional(boolean transactional) {

        setTransactionalVoid(transactional);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setTransactionalVoid(boolean transactional) {

        DbConfigManager.setBooleanVal(props, EnvironmentParams.ENV_INIT_TXN,
                                      transactional, validateParams);
    }

    /**
     * Returns true if the database environment is configured for transactions.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return true if the database environment is configured for transactions.
     */
    public boolean getTransactional() {

        return DbConfigManager.getBooleanVal(props,
                                             EnvironmentParams.ENV_INIT_TXN);
    }

    /**
     * Configures the database environment for no locking.
     *
     * <p>This configuration option should be used when locking guarantees such
     * as consistency and isolation are not important.  If locking mode is
     * disabled (it is enabled by default), the cleaner is automatically
     * disabled.  The user is responsible for invoking the cleaner and ensuring
     * that there are no concurrent operations while the cleaner is
     * running.</p>
     *
     * @param locking If false, configure the database environment for no
     * locking.  The default is true.
     *
     * @return this
     */
    public EnvironmentConfig setLocking(boolean locking) {

        setLockingVoid(locking);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setLockingVoid(boolean locking) {

        DbConfigManager.setBooleanVal(props,
                                      EnvironmentParams.ENV_INIT_LOCKING,
                                      locking, validateParams);
    }

    /**
     * Returns true if the database environment is configured for locking.
     *
     * <p>This method may be called at any time during the life of the
     * application.</p>
     *
     * @return true if the database environment is configured for locking.
     */
    public boolean getLocking() {

        return DbConfigManager.getBooleanVal
            (props, EnvironmentParams.ENV_INIT_LOCKING);
    }

    /**
     * Configures the transaction timeout.
     *
     * <p>Equivalent to setting the je.txn.timeout parameter in the
     * je.properties file.</p>
     *
     * @param timeout The transaction timeout. A value of 0 turns off
     * transaction timeouts.
     *
     * @param unit the {@code TimeUnit} of the timeout value. May be null only
     * if timeout is zero.
     *
     * @return this
     *
     * @throws IllegalArgumentException If the value of timeout is negative
     *
     * @see Transaction#setTxnTimeout
     */
    public EnvironmentConfig setTxnTimeout(long timeout, TimeUnit unit)
        throws IllegalArgumentException {

        setTxnTimeoutVoid(timeout, unit);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setTxnTimeoutVoid(long timeout, TimeUnit unit)
        throws IllegalArgumentException {

        DbConfigManager.setDurationVal(props, EnvironmentParams.TXN_TIMEOUT,
                                       timeout, unit, validateParams);
    }

    /**
     * Configures the transaction timeout, in microseconds.  This method is
     * equivalent to:
     *
     * <pre>setTxnTimeout(long, TimeUnit.MICROSECONDS);</pre>
     *
     * @deprecated as of 4.0, replaced by {@link #setTxnTimeout(long,
     * TimeUnit)}.
     */
    public EnvironmentConfig setTxnTimeout(long timeout)
        throws IllegalArgumentException {

        setTxnTimeoutVoid(timeout);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setTxnTimeoutVoid(long timeout)
        throws IllegalArgumentException {

        setTxnTimeout(timeout, TimeUnit.MICROSECONDS);
    }

    /**
     * Returns the transaction timeout.
     *
     * <p>A value of 0 means transaction timeouts are not configured.</p>
     *
     * @param unit the {@code TimeUnit} of the returned value. May not be null.
     *
     * @return The transaction timeout.
     */
    public long getTxnTimeout(TimeUnit unit) {
        return DbConfigManager.getDurationVal
            (props, EnvironmentParams.TXN_TIMEOUT, unit);
    }

    /**
     * Returns the transaction timeout, in microseconds.  This method is
     * equivalent to:
     *
     * <pre>getTxnTimeout(TimeUnit.MICROSECONDS);</pre>
     *
     * @deprecated as of 4.0, replaced by {@link #getTxnTimeout(TimeUnit)}.
     */
    public long getTxnTimeout() {
        return getTxnTimeout(TimeUnit.MICROSECONDS);
    }

    /**
     * Configures all transactions for this environment to have Serializable
     * (Degree 3) isolation.  By setting Serializable isolation, phantoms will
     * be prevented.  By default transactions provide Repeatable Read
     * isolation.
     *
     * The default is false for the database environment.
     *
     * @see LockMode
     *
     * @return this
     */
    public EnvironmentConfig
        setTxnSerializableIsolation(boolean txnSerializableIsolation) {

        setTxnSerializableIsolationVoid(txnSerializableIsolation);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void
        setTxnSerializableIsolationVoid(boolean txnSerializableIsolation) {

        DbConfigManager.setBooleanVal
            (props, EnvironmentParams.TXN_SERIALIZABLE_ISOLATION,
             txnSerializableIsolation, validateParams);
    }

    /**
     * Returns true if all transactions for this environment has been
     * configured to have Serializable (Degree 3) isolation.
     *
     * @return true if the environment has been configured to have repeatable
     * read isolation.
     *
     * @see LockMode
     */
    public boolean getTxnSerializableIsolation() {

        return DbConfigManager.getBooleanVal
            (props, EnvironmentParams.TXN_SERIALIZABLE_ISOLATION);
    }

    /**
     * For unit testing, sets readCommitted as the default.
     */
    void setTxnReadCommitted(boolean txnReadCommitted) {

        this.txnReadCommitted = txnReadCommitted;
    }

    /**
     * For unit testing, to set readCommitted as the default.
     */
    boolean getTxnReadCommitted() {

        return txnReadCommitted;
    }

    /**
     * If true, the shared cache is used by this environment.
     *
     * <p>By default this parameter is false and this environment uses a
     * private cache.  If this parameter is set to true, this environment will
     * use a cache that is shared with all other open environments in this
     * process that also set this parameter to true.  There is a single shared
     * cache per process.</p>
     *
     * <p>By using the shared cache, multiple open environments will make
     * better use of memory because the cache LRU algorithm is applied across
     * all information in all environments sharing the cache.  For example, if
     * one environment is open but not recently used, then it will only use a
     * small portion of the cache, leaving the rest of the cache for
     * environments that have been recently used.</p>
     *
     * @param sharedCache If true, the shared cache is used by this
     * environment.
     *
     * @return this
     */
    public EnvironmentConfig setSharedCache(boolean sharedCache) {

        setSharedCacheVoid(sharedCache);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setSharedCacheVoid(boolean sharedCache) {

        DbConfigManager.setBooleanVal
            (props, EnvironmentParams.ENV_SHARED_CACHE, sharedCache,
             validateParams);
    }

    /**
     * Returns true if the shared cache is used by this environment.
     *
     * @return true if the shared cache is used by this environment. @see
     * #setSharedCache
     */
    public boolean getSharedCache() {
        return DbConfigManager.getBooleanVal
            (props, EnvironmentParams.ENV_SHARED_CACHE);
    }

    /**
     * Sets the user defined nodeName for the Environment.  If set, exception
     * messages, logging messages, and thread names will have this nodeName
     * included in them.  If a user has multiple Environments in a single JVM,
     * setting this to a string unique to each Environment may make it easier
     * to diagnose certain exception conditions as well as thread dumps.
     *
     * @return this
     */
    public EnvironmentConfig setNodeName(String nodeName) {
        setNodeNameVoid(nodeName);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setNodeNameVoid(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * Returns the user defined nodeName for the Environment.
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Sets the custom statistics object.
     *
     * @return this
     */
    public EnvironmentConfig setCustomStats(CustomStats customStats) {
        this.customStats = customStats;
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setCustomStatsVoid(CustomStats customStats) {
        this.customStats = customStats;
    }

    /**
     * Gets the custom statstics object.
     *
     * @return customStats
     */
    public CustomStats getCustomStats() {
        return customStats;
    }

    /**
     * Set a java.util.logging.Handler which will be used by all
     * java.util.logging.Loggers instantiated by this Environment. This lets
     * the application specify a handler which
     * <ul>
     * <li>requires a constructor with arguments</li>
     * <li>is specific to this environment, which is important if the
     * application is using multiple environments within the same process.
     * </ul>
     * Note that {@link Handler} is not serializable, and the logging
     * handler should be set within the same process.
     */
    public EnvironmentConfig setLoggingHandler(Handler handler) {
        setLoggingHandlerVoid(handler);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setLoggingHandlerVoid(Handler handler){
        loggingHandler = handler;
    }

    /**
     * Returns the custom java.util.logging.Handler specified by the
     * application.
     */
    public Handler getLoggingHandler() {
        return loggingHandler;
    }

    /* Documentation inherited from EnvironmentMutableConfig.setConfigParam. */
    @Override
    public EnvironmentConfig setConfigParam(String paramName, String value)
        throws IllegalArgumentException {

        DbConfigManager.setConfigParam(props,
                                       paramName,
                                       value,
                                       false, /* requireMutablity */
                                       validateParams,
                                       false  /* forReplication */,
                                       true   /* verifyForReplication */);
        return this;
    }

    /**
     * Configure the environment to make periodic calls to a ProgressListener to
     * provide feedback on environment startup (recovery). The
     * ProgressListener.progress() method is called at different stages of
     * the recovery process. See {@link RecoveryProgress} for information about
     * those stages.
     * <p>
     * When using progress listeners, review the information at {@link
     * ProgressListener#progress} to avoid any unintended disruption to
     * environment startup.
     * @param progressListener The ProgressListener to callback during
     * environment startup (recovery).
     */
    public EnvironmentConfig setRecoveryProgressListener
        (final ProgressListener<RecoveryProgress> progressListener) {
        setRecoveryProgressListenerVoid(progressListener);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setRecoveryProgressListenerVoid
        (final ProgressListener<RecoveryProgress> progressListener) {
        this.recoveryProgressListener = progressListener;
    }

    /**
     * Return the ProgressListener to be used at this environment startup.
     */
    public ProgressListener<RecoveryProgress> getRecoveryProgressListener() {
        return recoveryProgressListener;
    }

    /**
     * Configure the environment to use a specified ClassLoader for loading
     * user-supplied classes by name.
     */
    public EnvironmentConfig setClassLoader(final ClassLoader classLoader) {
        setClassLoaderVoid(classLoader);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void setClassLoaderVoid(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Returns the ClassLoader for loading user-supplied classes by name, or
     * null if no specified ClassLoader is configured.
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * @hidden
     * Configure the environment to use a specified PreloadConfig for
     * duplicates database conversion.
     */
    public EnvironmentConfig
        setDupConvertPreloadConfig(final PreloadConfig preloadConfig) {
        setDupConvertPreloadConfigVoid(preloadConfig);
        return this;
    }

    /**
     * @hidden
     * The void return setter for use by Bean editors.
     */
    public void
        setDupConvertPreloadConfigVoid(final PreloadConfig preloadConfig) {
        this.dupConvertPreloadConfig = preloadConfig;
    }

    /**
     * @hidden
     * Returns the PreloadConfig for duplicates database conversion, or
     * null if no PreloadConfig is configured.
     */
    public PreloadConfig getDupConvertPreloadConfig() {
        return dupConvertPreloadConfig;
    }

    /**
     * For unit testing, to prevent creating the utilization profile DB.
     */
    void setCreateUP(boolean createUP) {
        this.createUP = createUP;
    }

    /**
     * For unit testing, to prevent creating the utilization profile DB.
     */
    boolean getCreateUP() {
        return createUP;
    }

    /**
     * For unit testing, to prevent writing utilization data during checkpoint.
     */
    void setCheckpointUP(boolean checkpointUP) {
        this.checkpointUP = checkpointUP;
    }

    /**
     * For unit testing, to prevent writing utilization data during checkpoint.
     */
    boolean getCheckpointUP() {
        return checkpointUP;
    }

    /**
     * Returns a copy of this configuration object.
     */
    @Override
    public EnvironmentConfig clone() {
        return (EnvironmentConfig) super.clone();
    }

    /**
     * Display configuration values.
     */
    @Override
    public String toString() {
        return ("allowCreate=" + allowCreate + "\n" + super.toString());
    }
}
