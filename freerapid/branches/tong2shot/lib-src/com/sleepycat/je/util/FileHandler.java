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

package com.sleepycat.je.util;

import java.io.IOException;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;

import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * JE instances of java.util.logging.Logger are configured to use this
 * implementation of java.util.logging.FileHandler. By default, the handler's
 * level is {@link Level#INFO} To enable the console output, use the standard
 * java.util.logging.LogManager configuration to set the desired level:
 * <pre>
 * com.sleepycat.je.util.FileHandler.level=INFO
 * </pre>
 * <p>
 * The default destination for this output is a circular set of files named
 * &lt;environmentHome&gt;/je.info.# The logging file size can be configured
 * with standard java.util.logging.FileHandler configuration.
 * <p>
 * JE augments the java.util.logging API with a JE environment parameter for
 * setting handler levels. This is described in greater detail in
 * {@link <a href="{@docRoot}/../GettingStartedGuide/managelogging.html">
 * Chapter 12.Administering Berkeley DB Java Edition Applications</a>}
 *
 * @see <a href="{@docRoot}/../GettingStartedGuide/managelogging.html">
 * Chapter 12. Logging</a>
 * @see <a href="{@docRoot}/../traceLogging.html">Using JE Trace Logging</a>
 */
public class FileHandler extends java.util.logging.FileHandler {

    /*
     * The default ErrorManager will blindly write to stderr when it sees an
     * exception.  For instance, when we send an interrupt() to the Rep Node
     * we can see an InterruptedIOException written to stderr, but it never
     * gets passed to the caller.  For several tests, this causes irrelevant
     * stack traces to spew out even though no execption is ever thrown at us.
     * e.g.
     *
     * ------------- Standard Error -----------------
     * java.util.logging.ErrorManager: 2
     * java.io.InterruptedIOException
     *     at java.io.FileOutputStream.writeBytes(Native Method)
     *     at java.io.FileOutputStream.write(FileOutputStream.java:260)
     *     at java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:65)
     *     at java.io.BufferedOutputStream.flush(BufferedOutputStream.java:123)
     *     at java.util.logging.FileHandler$MeteredStream.flush(FileHandler.java:143)
     *     at sun.nio.cs.StreamEncoder.implFlush(StreamEncoder.java:278)
     *     at sun.nio.cs.StreamEncoder.flush(StreamEncoder.java:122)
     *     at java.io.OutputStreamWriter.flush(OutputStreamWriter.java:212)        at java.util.logging.StreamHandler.flush(StreamHandler.java:225)        at java.util.logging.FileHandler.publish(FileHandler.java:556)
     *     at com.sleepycat.je.utilint.FileRedirectHandler.publish(FileRedirectHandler.java:54)
     *     at java.util.logging.Logger.log(Logger.java:458)
     *     at java.util.logging.Logger.doLog(Logger.java:480)
     *     at java.util.logging.Logger.log(Logger.java:503)
     *     at com.sleepycat.je.utilint.LoggerUtils.logMsg(LoggerUtils.java:343)
     *     at com.sleepycat.je.utilint.LoggerUtils.info(LoggerUtils.java:395)
     *     at com.sleepycat.je.rep.impl.node.FeederManager.runFeeders(FeederManager.java:449)
     *     at com.sleepycat.je.rep.impl.node.RepNode.run(RepNode.java:1198)
     */
    public static boolean STIFLE_DEFAULT_ERROR_MANAGER = false;

    /*
     * Using a JE specific handler lets us enable and disable output for the
     * entire library, and specify an environment specific format and level
     * default.
     */
    public FileHandler(String pattern,
                       int limit,
                       int count,
                       Formatter formatter,
                       EnvironmentImpl envImpl)
        throws SecurityException, IOException {

        super(pattern, limit, count, true /* append */);

            ErrorManager em = new ErrorManager() {
                    public void error(String msg, Exception e, int code) {
                        if (STIFLE_DEFAULT_ERROR_MANAGER) {
                            System.out.println
                                ("FileHandler stifled exception: " + e);
                        } else {
                            super.error(msg, e, code);
                        }
                    }
                };
            setErrorManager(em);

        /* Messages may be formatted with an environment specific tag. */
        setFormatter(formatter);

        Level level = LoggerUtils.getHandlerLevel
            (envImpl.getConfigManager(), EnvironmentParams.JE_FILE_LEVEL,
             getClass().getName() + ".level");

        setLevel(level);
    }
}
