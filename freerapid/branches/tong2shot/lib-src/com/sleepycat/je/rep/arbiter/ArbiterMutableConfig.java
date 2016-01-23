/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.arbiter;

import java.util.Properties;
import java.util.logging.Level;

import com.sleepycat.je.config.ConfigParam;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.rep.impl.RepParams;

public class ArbiterMutableConfig implements Cloneable {

    protected Properties props;

    protected boolean validateParams = true;

    protected ArbiterMutableConfig() {
       props = new Properties();
    }

    protected ArbiterMutableConfig(Properties properties) {
        props = (Properties)properties.clone();
     }

    /**
     * @hidden
     * Identify one or more helpers nodes by their host and port pairs in this
     * format:
     * <pre>
     * hostname[:port][,hostname[:port]]*
     * </pre>
     *
     * @param helperHosts the string representing the host and port pairs.
     */
    public ArbiterMutableConfig setHelperHosts(String helperHosts) {
        DbConfigManager.setVal(
            props, RepParams.HELPER_HOSTS, helperHosts, validateParams);
        return this;
    }

    /**
     * @hidden
     * Returns the string identifying one or more helper host and port pairs in
     * this format:
     * <pre>
     * hostname[:port][,hostname[:port]]*
     * </pre>
     *
     * @return the string representing the host port pairs.
     */
    public String getHelperHosts() {
        return DbConfigManager.getVal(props, RepParams.HELPER_HOSTS);
    }

    /**
     * @hidden
     * Trace messages equal and above this level will be logged to the je.info
     * file, which is in the Arbiter home directory.  Value should
     * be one of the predefined java.util.logging.Level values.
     * <p>
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
     *
     * @param val value of the logging level.
     * @return ArbiterConfig.
     */
    public ArbiterMutableConfig setFileLoggingLevel(String val) {
        Level.parse(val);
        DbConfigManager.setVal(
            props, EnvironmentParams.JE_FILE_LEVEL, val, false);
        return this;
    }

    /**
     * @hidden
     * Gets the file logging level.
     * @return logging level
     */
    public String getFileLoggingLevel() {
        return DbConfigManager.getVal(props, EnvironmentParams.JE_FILE_LEVEL);
    }

    /**
     * @hidden
     * Trace messages equal and above this level will be logged to the
     * console. Value should be one of the predefined
     * java.util.logging.Level values.
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
     *
     * @param val Logging level.
     * @return this.
     */
    public ArbiterMutableConfig setConsoleLoggingLevel(String val) {
        Level.parse(val);
        DbConfigManager.setVal(
            props, EnvironmentParams.JE_CONSOLE_LEVEL, val, false);
        return this;
    }

    /**
     * @hidden
     * Gets the console logging level.
     * @return logging level
     */
    public String getConsoleLoggingLevel() {
        return DbConfigManager.getVal(props, EnvironmentParams.JE_CONSOLE_LEVEL);
    }

    /**
     * @hidden
     * Set this configuration parameter. First validate the value specified for
     * the configuration parameter; if it is valid, the value is set in the
     * configuration. Hidden could be used to set parameters internally.
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
    public ArbiterMutableConfig setConfigParam(String paramName,
                                                   String value)
        throws IllegalArgumentException {

        boolean forReplication = false;
        ConfigParam param =
            EnvironmentParams.SUPPORTED_PARAMS.get(paramName);
        if (param != null) {
            forReplication = param.isForReplication();
        }

        DbConfigManager.setConfigParam(props,
                                       paramName,
                                       value,
                                       true, /* require mutability. */
                                       true,
                                       forReplication, /* forReplication */
                                       true  /* verifyForReplication */);
        return this;
    }

    /**
     * @hidden
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

    protected ArbiterMutableConfig copy() {
        return new ArbiterMutableConfig(props);
    }

    /**
     * @hidden
     * For internal use only.
     */
    public boolean isConfigParamSet(String paramName) {
        return props.containsKey(paramName);
    }

    /**
     * @hidden
     */
    public ArbiterMutableConfig clone() {
        try {
            ArbiterMutableConfig copy =
                (ArbiterMutableConfig) super.clone();
            copy.props = (Properties) props.clone();
            return copy;
        } catch (CloneNotSupportedException willNeverOccur) {
            return null;
        }
    }

}
