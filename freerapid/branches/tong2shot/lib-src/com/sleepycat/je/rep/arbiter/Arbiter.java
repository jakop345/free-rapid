/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */
package com.sleepycat.je.rep.arbiter;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.je.EnvironmentMutableConfig;
import com.sleepycat.je.EnvironmentNotFoundException;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.config.EnvironmentParams;
import com.sleepycat.je.rep.RepInternal;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;
import com.sleepycat.je.rep.ReplicationMutableConfig;
import com.sleepycat.je.rep.arbiter.impl.ArbiterImpl;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.utilint.DatabaseUtil;

/**
 * Provides a mechanism to allow write availability for the Replication
 * group even when the number of replication nodes is less than majority.
 * The main use of an Arbiter is when the replication group consists of
 * two nodes. The addition of an Arbiter to the replication group
 * allows for one node to fail and provide write availability with ACK
 * durability of SIMPLE_MAJORITY. The Arbiter acknowledges the transaction,
 * but does not retain a copy of the data. The Arbiter persists a
 * small amount of state to insure that only the Replication nodes that
 * contain the Arbiter acknowledged transactions may become a Master.
 * <p>
 * The Arbiter node participates in elections and may acknowledge transaction
 * commits.
 * <p>
 */
public class Arbiter {

    private ArbiterImpl ai;
    private final ReplicatedEnvironment repEnv;
    private final ArbiterConfig ac;

    private final String ARB_CONFIG = "ArbiterConfig";
    private final String ARB_HOME = "ArbiterHome";

    /**
     * @hidden
     * An Arbiter used in elections and transaction acknowledgments.
     * This method returns when a connection to the current master
     * replication node is made. The Arbiter.shutdown() method is
     * used to shutdown the threads that run as part of the Arbiter.
     *
     * @param arbiterConfig Configuration parameters for the Arbiter.
     *
     * @throws EnvironmentNotFoundException if the environment does not exist
     *
     * @throws EnvironmentLockedException when an environment cannot be opened
     * because another Arbiter has the environment open.
     *
     * @throws DatabaseException problem establishing connection to the master.
     *
     * @throws IllegalArgumentException if an invalid parameter is specified,
     * for example, an invalid {@code ArbiterConfig} parameter.
     */
    public Arbiter(ArbiterConfig arbiterConfig)
        throws EnvironmentNotFoundException,
               EnvironmentLockedException,
               DatabaseException,
               IllegalArgumentException {

        ac = arbiterConfig.clone();
        verifyParameters(ac);
        File envHome = new File(ac.getArbiterHome());
        if (!envHome.exists()) {
            throw new IllegalArgumentException(
                "The specified environment directory " +
                envHome.getAbsolutePath() +
                " does not exist.");
        }

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setReadOnly(true);
        envConfig.setTransactional(true);
        envConfig.setConfigParam(
            EnvironmentParams.ENV_RECOVERY.getName(), "false");
        envConfig.setConfigParam(
            EnvironmentParams.ENV_SETUP_LOGGER.getName(), "true");

        /*
         * Only set if user has set it so as to not
         * override the je.properties settings.
         */
        if (ac.isConfigParamSet(
            EnvironmentParams.JE_FILE_LEVEL.getName())) {
            envConfig.setConfigParam(
                EnvironmentParams.JE_FILE_LEVEL.getName(),
                ac.getFileLoggingLevel());
        }
        if (ac.isConfigParamSet(
            EnvironmentParams.JE_CONSOLE_LEVEL.getName())) {
            envConfig.setConfigParam(
                EnvironmentParams.JE_CONSOLE_LEVEL.getName(),
                ac.getConsoleLoggingLevel());
        }
        ReplicationConfig repConfig = new ReplicationConfig();
        repConfig.setGroupName(ac.getGroupName());
        repConfig.setNodeName(ac.getNodeName());
        repConfig.setNodeHostPort(ac.getNodeHostPort());
        repConfig.setHelperHosts(ac.getHelperHosts());
        repConfig.setConfigParam(RepParams.ARBITER_USE.getName(), "true");
        repConfig.setConfigParam(
            RepParams.ENV_UNKNOWN_STATE_TIMEOUT.getName(),
            ac.getUnknownStateTimeout(TimeUnit.SECONDS) + " s");
        repConfig.setRepNetConfig(ac.getRepNetConfig());
        repConfig.setConfigParam(
            RepParams.REPLICA_MESSAGE_QUEUE_SIZE.getName(),
            Integer.toString(ac.getMessageQueueSize()));
        repConfig.setConfigParam(
            RepParams.REPLICA_TIMEOUT.getName(),
            ac.getChannelTimeout(TimeUnit.SECONDS) + " s");
        repConfig.setConfigParam(
            RepParams.PRE_HEARTBEAT_TIMEOUT.getName(),
            ac.getPreHeartbeatTimeout(TimeUnit.SECONDS) + " s");
        repConfig.setConfigParam(
            RepParams.REPSTREAM_OPEN_TIMEOUT.getName(),
            ac.getStreamOpenTimeout(TimeUnit.SECONDS) + " s");
        repConfig.setConfigParam(
            RepParams.HEARTBEAT_INTERVAL.getName(),
            Integer.toString(ac.getHeartbeatInterval()));
        repConfig.setConfigParam(
            RepParams.REPLICA_RECEIVE_BUFFER_SIZE.getName(),
            Integer.toString(ac.getReceiveBufferSize()));
        repConfig.setConfigParam(
            RepParams.ENABLE_GROUP_ACKS.getName(),
            Boolean.toString(ac.getEnableGroupAcks()));

        repEnv = RepInternal.createInternalEnvHandle(envHome,
                                                 repConfig,
                                                 envConfig);
        try {
            ai = new ArbiterImpl(envHome, RepInternal.getRepImpl(repEnv));
            ai.runArbiter();
        } catch (Throwable t) {
            shutdown();
            throw t;
        }
    }

    /**
     * @hidden
     * Returns the Arbiter mutable attributes.
     *
     * @return Arbiter attributes.
     */
    public ArbiterMutableConfig getArbiterMutableConfig() {
        return ac.getArbiterMutableConfig();
    }

    /**
     * @hidden
     * Sets the Arbiter mutable attributes.
     *
     * @param config Arbiter attributes.
     * @throws DatabaseException
     */
    public void setArbiterMutableConfig(ArbiterMutableConfig config)
        throws DatabaseException {
        ReplicationMutableConfig rmc = repEnv.getRepMutableConfig();
        rmc.setHelperHosts(config.getHelperHosts());
        repEnv.setRepMutableConfig(rmc);
        ai.refreshHelperHosts();
        EnvironmentMutableConfig emc = repEnv.getMutableConfig();
        if (config.isConfigParamSet(
            EnvironmentParams.JE_FILE_LEVEL.getName())) {
            emc.setConfigParam(
                EnvironmentConfig.FILE_LOGGING_LEVEL,
                config.getFileLoggingLevel());
        }
        if (config.isConfigParamSet(
            EnvironmentParams.JE_CONSOLE_LEVEL.getName())) {
            emc.setConfigParam(
                EnvironmentConfig.CONSOLE_LOGGING_LEVEL,
                config.getConsoleLoggingLevel());
        }
        repEnv.setMutableConfig(emc);
    }

    /**
     * @hidden
     * Gets the Arbiter statistics.
     *
     * @param config The general statistics attributes.  If null, default
     * attributes are used.
     *
     * @return Arbiter statistics.
     * @throws DatabaseException
     */
    public ArbiterStats getStats(StatsConfig config)
        throws DatabaseException {
        if (ai == null) {
            return null;
        }

        StatsConfig useConfig =
                (config == null) ? StatsConfig.DEFAULT : config;

        return new ArbiterStats(ai.loadStats(useConfig));
    }

    /**
     * @hidden
     * Shutdown the Arbiter.
     * Threads are stopped and resources are released.
     * @throws DatabaseException
     */
    public void shutdown()
        throws DatabaseException {
        if (ai != null) {
            ai.shutdown();
            try {
                ai.join();
            } catch (InterruptedException ignore) {

            }
        }
        if (repEnv != null) {
            repEnv.close();
        }
    }

    private void verifyParameters(ArbiterConfig ac)
        throws IllegalArgumentException {
        DatabaseUtil.checkForNullParam(ac, ARB_CONFIG);
        DatabaseUtil.checkForNullParam(ac.getArbiterHome(), ARB_HOME);
        DatabaseUtil.checkForNullParam(ac.getGroupName(), ReplicationConfig.GROUP_NAME);
        DatabaseUtil.checkForNullParam(ac.getNodeHostPort(), ReplicationConfig.NODE_HOST_PORT);
        DatabaseUtil.checkForNullParam(ac.getHelperHosts(), ReplicationMutableConfig.HELPER_HOSTS);
    }
}
