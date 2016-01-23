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

package com.sleepycat.je.rep.impl.node;

import static com.sleepycat.je.rep.ReplicatedEnvironment.State.DETACHED;
import static com.sleepycat.je.rep.ReplicatedEnvironment.State.MASTER;
import static com.sleepycat.je.rep.ReplicatedEnvironment.State.REPLICA;
import static com.sleepycat.je.rep.ReplicatedEnvironment.State.UNKNOWN;
import static com.sleepycat.je.rep.impl.RepParams.DBTREE_CACHE_CLEAR_COUNT;
import static com.sleepycat.je.rep.impl.RepParams.ENV_CONSISTENCY_TIMEOUT;
import static com.sleepycat.je.rep.impl.RepParams.GROUP_NAME;
import static com.sleepycat.je.rep.impl.RepParams.HEARTBEAT_INTERVAL;
import static com.sleepycat.je.rep.impl.RepParams.IGNORE_SECONDARY_NODE_ID;
import static com.sleepycat.je.rep.impl.RepParams.LOG_FLUSH_TASK_INTERVAL;
import static com.sleepycat.je.rep.impl.RepParams.NODE_TYPE;
import static com.sleepycat.je.rep.impl.RepParams.REPLAY_COST_PERCENT;
import static com.sleepycat.je.rep.impl.RepParams.REPLAY_FREE_DISK_PERCENT;
import static com.sleepycat.je.rep.impl.RepParams.RESET_REP_GROUP_RETAIN_UUID;
import static com.sleepycat.je.rep.impl.RepParams.RUN_LOG_FLUSH_TASK;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sleepycat.je.CheckpointConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.RecoveryProgress;
import com.sleepycat.je.ReplicaConsistencyPolicy;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.cleaner.Cleaner;
import com.sleepycat.je.cleaner.FileSelector;
import com.sleepycat.je.cleaner.FileSummary;
import com.sleepycat.je.dbi.DbConfigManager;
import com.sleepycat.je.dbi.StartupTracker.Phase;
import com.sleepycat.je.log.FileManager;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.log.LogManager;
import com.sleepycat.je.rep.AppStateMonitor;
import com.sleepycat.je.rep.GroupShutdownException;
import com.sleepycat.je.rep.InsufficientLogException;
import com.sleepycat.je.rep.MasterStateException;
import com.sleepycat.je.rep.MasterTransferFailureException;
import com.sleepycat.je.rep.MemberActiveException;
import com.sleepycat.je.rep.MemberNotFoundException;
import com.sleepycat.je.rep.NodeType;
import com.sleepycat.je.rep.QuorumPolicy;
import com.sleepycat.je.rep.RepInternal;
import com.sleepycat.je.rep.ReplicaConsistencyException;
import com.sleepycat.je.rep.ReplicaStateException;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicatedEnvironmentStats;
import com.sleepycat.je.rep.ReplicationNode;
import com.sleepycat.je.rep.RestartRequiredException;
import com.sleepycat.je.rep.UnknownMasterException;
import com.sleepycat.je.rep.arbitration.Arbiter;
import com.sleepycat.je.rep.elections.Elections;
import com.sleepycat.je.rep.elections.ElectionsConfig;
import com.sleepycat.je.rep.elections.Proposer.Proposal;
import com.sleepycat.je.rep.elections.TimebasedProposalGenerator;
import com.sleepycat.je.rep.impl.BinaryNodeStateProtocol;
import com.sleepycat.je.rep.impl.BinaryNodeStateProtocol.BinaryNodeStateResponse;
import com.sleepycat.je.rep.impl.BinaryNodeStateService;
import com.sleepycat.je.rep.impl.GroupService;
import com.sleepycat.je.rep.impl.MinJEVersionUnsupportedException;
import com.sleepycat.je.rep.impl.NodeStateService;
import com.sleepycat.je.rep.impl.PointConsistencyPolicy;
import com.sleepycat.je.rep.impl.RepGroupDB;
import com.sleepycat.je.rep.impl.RepGroupImpl;
import com.sleepycat.je.rep.impl.RepGroupImpl.NodeConflictException;
import com.sleepycat.je.rep.impl.RepGroupProtocol;
import com.sleepycat.je.rep.impl.RepGroupProtocol.GroupResponse;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.RepNodeImpl;
import com.sleepycat.je.rep.impl.RepParams;
import com.sleepycat.je.rep.impl.TextProtocol.MessageExchange;
import com.sleepycat.je.rep.impl.TextProtocol.ResponseMessage;
import com.sleepycat.je.rep.monitor.LeaveGroupEvent.LeaveReason;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.net.DataChannelFactory.ConnectOptions;
import com.sleepycat.je.rep.stream.FeederTxns;
import com.sleepycat.je.rep.stream.MasterChangeListener;
import com.sleepycat.je.rep.stream.MasterStatus;
import com.sleepycat.je.rep.stream.MasterSuggestionGenerator;
import com.sleepycat.je.rep.util.ldiff.LDiffService;
import com.sleepycat.je.rep.utilint.RepUtils;
import com.sleepycat.je.rep.utilint.RepUtils.Clock;
import com.sleepycat.je.rep.utilint.RepUtils.ExceptionAwareCountDownLatch;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.rep.vlsn.VLSNIndex;
import com.sleepycat.je.utilint.FileStoreInfo;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.FormatUtil;
import com.sleepycat.je.utilint.StoppableThread;
import com.sleepycat.je.utilint.TestHook;
import com.sleepycat.je.utilint.TestHookExecute;
import com.sleepycat.je.utilint.VLSN;

/**
 * Represents a replication node. This class is the locus of operations that
 * manage the state of the node, master, replica, etc. Once the state of a node
 * has been established the thread of control passes over to the Replica or
 * FeederManager instances.
 *
 * Note that both Feeders and the Replica instance may be active in future when
 * we support r2r replication, in addition to m2r replication. For now however,
 * either the FeederManager is active, or the Replica is and the same common
 * thread control can be shared between the two.
 */
public class RepNode extends StoppableThread {

    /*
     * The unique node name and internal id that identifies the node within
     * the rep group. There is a canonical instance of this that's updated
     * when the node joins the group.
     */
    private final NameIdPair nameIdPair;

    /*
     * The socket address on which Replicas connect to me, were this node
     * to become the master.
     */
    private final InetSocketAddress mySocket;

    /* The service dispatcher used by this replication node. */
    private final ServiceDispatcher serviceDispatcher;

    /* The election instance for this node */
    private Elections elections;

    /* The locus of operations when the node is a replica. */
    private final Replica replica;

    /* Used when the node is a feeder. */
    private FeederManager feederManager;

    /*
     * The status of the Master. Note that this is the leading state as
     * communicated to this node via the Listener. The node itself may not as
     * yet have responded to this state change announced by the Listener. That
     * is, nodeState, may reflect a different state until the transition to
     * this state has been completed.
     */
    private final MasterStatus masterStatus;
    private final MasterChangeListener changeListener;
    private final MasterSuggestionGenerator suggestionGenerator;

    /*
     * Represents the application visible state of this node. It may lag the
     * state as described by masterStatus.
     */
    private final NodeState nodeState;

    private final RepImpl repImpl;

    /* The encapsulated internal replication group database. */
    final RepGroupDB repGroupDB;

    /*
     * The latch used to indicate that the node has a well defined state as a
     * Master or Replica and has finished the node-specific initialization that
     * will permit it to function immediately in that capacity.
     *
     * For a Master it means that it's ready to start accepting connections
     * from Replicas.
     *
     * For a Replica, it means that it has established a connection with a
     * Feeder, completed the handshake process that validates it as being a
     * legitimate member of the group, established a sync point, and is ready
     * to start replaying the replication stream.
     */
    private volatile ExceptionAwareCountDownLatch readyLatch = null;

    /*
     * Latch used to freeze txn commit VLSN advancement during an election.
     */
    private final CommitFreezeLatch vlsnFreezeLatch = new CommitFreezeLatch();

    /*
     * Describes the nodes that form the group. This information is dynamic
     * it's initialized at startup and subsequently as a result of changes
     * made either directly to it, when the node is a master, or via the
     * replication stream, when it is a Replica.
     */
    volatile private RepGroupImpl group;

    /*
     * Determines the election policy to use when the node holds its very first
     * elections
     */
    private QuorumPolicy electionQuorumPolicy = QuorumPolicy.SIMPLE_MAJORITY;

    /*
     * Amount of times to sleep between retries when a new node tries to locate
     * a master.
     */
    private static final int MASTER_QUERY_INTERVAL = 10000;

    /* Number of times to retry joining on a retryable exception. */
    private static final int JOIN_RETRIES = 10;

    /*
     * Encapsulates access to current time, to arrange for testing of clock
     * skews.
     */
    private final Clock clock;

    private com.sleepycat.je.rep.impl.networkRestore.FeederManager
        logFeederManager;
    private LDiffService ldiff;
    private NodeStateService nodeStateService;
    private BinaryNodeStateService binaryNodeStateService;
    private GroupService groupService;

    /* tracks the local CBVLSN for this node. */
    final LocalCBVLSNTracker cbvlsnTracker;

    /* The currently in-progress Master Transfer operation, if any. */
    private MasterTransfer xfrInProgress;

    /* calculates and manages the global, cached CBVLSN */
    final GlobalCBVLSN globalCBVLSN;

    /* Determines how long to wait for a replica to catch up on a close. */
    private long replicaCloseCatchupMs = -1;

    /* Manage and notify MonitorChangeEvents fired by this RepNode. */
    private MonitorEventManager monitorEventManager;

    /* The user defined AppStateMonitor which gets the application state. */
    private AppStateMonitor appStateMonitor;

    /* A timer used to track inactive socket channels used by the RepNode. */
    private final Timer timer;
    private final ChannelTimeoutTask channelTimeoutTask;
    private LogFlusher logFlusher;

    final Logger logger;

    /* Locus of election and durability quorum decisions */
    private final ElectionQuorum electionQuorum;
    private final DurabilityQuorum durabilityQuorum;

    private final Arbiter arbiter;
    private final NodeType nodeType;

    /** Manages the allocation of node IDs for secondary nodes. */
    private final SecondaryNodeIds secondaryNodeIds =
        new SecondaryNodeIds(RepGroupImpl.MAX_SECONDARY_NODES);

    /**
     * Synchronize on this object when setting the minimum JE version or adding
     * a secondary node, which could change the JE versions of the nodes to
     * check when setting a new minimum.
     *
     * @see #setMinJEVersion
     * @see #addSecondaryNode
     */
    private final Object minJEVersionLock = new Object();

    /**
     * The relative cost of replay as compared with network restore, as
     * specified by
     * {@link com.sleepycat.je.rep.ReplicationConfig#REPLAY_COST_PERCENT}.
     */
    private final int replayCostPercent;

    /**
     * The percentage of free disk space to maintain when choosing files to
     * retain based on replay cost, as specified by {@link
     * com.sleepycat.je.rep.ReplicationConfig#REPLAY_FREE_DISK_PERCENT}.
     */
    private final int replayFreeDiskPercent;

    /**
     * The minimum VLSN that should be retained as requested by
     * replayCostPercent and replayFreeDiskPercent, or NULL_VLSN if disabled.
     */
    private volatile VLSN replayCostMinVLSN = VLSN.NULL_VLSN;

    /* Used by tests only. */
    private int logVersion = LogEntryType.LOG_VERSION;

    /* For unit testing */
    private Set<TestHook<Integer>> convertHooks;

    public RepNode(RepImpl repImpl,
                   Replay replay,
                   NodeState nodeState)
        throws IOException, DatabaseException {

        super(repImpl, "RepNode " + repImpl.getNameIdPair());

        this.repImpl = repImpl;
        readyLatch = new ExceptionAwareCountDownLatch(repImpl, 1);
        nameIdPair = repImpl.getNameIdPair();
        logger = LoggerUtils.getLogger(getClass());

        this.mySocket = repImpl.getSocket();
        this.serviceDispatcher =
            new ServiceDispatcher(mySocket, repImpl,
                                  repImpl.getChannelFactory());
        serviceDispatcher.start();
        clock = new Clock(RepImpl.getClockSkewMs());
        this.repGroupDB = new RepGroupDB(repImpl);

        masterStatus = new MasterStatus(nameIdPair);
        replica = ReplicaFactory.create(this, replay);

        feederManager = new FeederManager(this);
        changeListener = new MasterChangeListener(this);
        suggestionGenerator = new MasterSuggestionGenerator(this);

        this.nodeState = nodeState;

        electionQuorum = new ElectionQuorum(repImpl);
        durabilityQuorum = new DurabilityQuorum(repImpl);

        utilityServicesStart();
        this.cbvlsnTracker = new LocalCBVLSNTracker(this);
        this.globalCBVLSN = new GlobalCBVLSN(this);
        this.monitorEventManager = new MonitorEventManager(this);
        timer = new Timer(true);
        channelTimeoutTask = new ChannelTimeoutTask(timer);
        configLogFlusher(getConfigManager());

        arbiter = new Arbiter(repImpl);
        nodeType = NodeType.valueOf(getConfigManager().get(NODE_TYPE));
        replayCostPercent = getConfigManager().getInt(REPLAY_COST_PERCENT);
        replayFreeDiskPercent = getReplayFreeDiskPercentParameter();
    }

    private void utilityServicesStart() {
        ldiff = new LDiffService(serviceDispatcher, repImpl);
        logFeederManager =
            new com.sleepycat.je.rep.impl.networkRestore.FeederManager
            (serviceDispatcher, repImpl, nameIdPair);

        /* Register the node state querying service. */
        nodeStateService = new NodeStateService(serviceDispatcher, this);
        serviceDispatcher.register(nodeStateService);

        binaryNodeStateService =
            new BinaryNodeStateService(serviceDispatcher, this);
        groupService = new GroupService(serviceDispatcher, this);
        serviceDispatcher.register(groupService);
    }

    private int getReplayFreeDiskPercentParameter() {
        final int value = getConfigManager().getInt(REPLAY_FREE_DISK_PERCENT);
        if (value == 0) {
            return 0;
        }
        try {
            FileStoreInfo.checkSupported();
            return value;
        } catch (UnsupportedOperationException e) {
            LoggerUtils.warning(
                logger, repImpl,
                "The " + REPLAY_FREE_DISK_PERCENT.getName() +
                " parameter was specified, but is not supported: " +
                e.getMessage());
            return 0;
        }
    }

    /* Create a placeholder node, for test purposes only. */
    public RepNode(NameIdPair nameIdPair) {
        this(nameIdPair, null);
    }

    public RepNode() {
        this(NameIdPair.NULL);
    }

    public RepNode(NameIdPair nameIdPair,
                   ServiceDispatcher serviceDispatcher) {
        super("RepNode " + nameIdPair);
        repImpl = null;
        clock = new Clock(0);

        this.nameIdPair = nameIdPair;
        mySocket = null;
        this.serviceDispatcher = serviceDispatcher;

        this.repGroupDB = null;

        masterStatus = new MasterStatus(NameIdPair.NULL);
        replica = null;
        feederManager = null;
        changeListener = null;
        suggestionGenerator = null;
        nodeState = null;
        cbvlsnTracker = null;
        globalCBVLSN = null;
        logger = null;
        timer = null;
        channelTimeoutTask = null;
        electionQuorum = null;
        durabilityQuorum = null;
        arbiter = null;
        nodeType = NodeType.ELECTABLE;
        replayCostPercent = 0;
        replayFreeDiskPercent = 0;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the node type of this node.
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Returns the timer associated with this RepNode
     */
    public Timer getTimer() {
        return timer;
    }

    public ServiceDispatcher getServiceDispatcher() {
        return serviceDispatcher;
    }

    /**
     * Returns the accumulated statistics for this node. The method
     * encapsulates the statistics associated with its two principal components
     * the FeederManager and the Replica.
     */
    public ReplicatedEnvironmentStats getStats(StatsConfig config) {
        return RepInternal.makeReplicatedEnvironmentStats(repImpl, config);
    }

    public void resetStats() {
        feederManager.resetStats();
        replica.resetStats();
    }

    public ExceptionAwareCountDownLatch getReadyLatch() {
        return readyLatch;
    }

    public CommitFreezeLatch getVLSNFreezeLatch() {
        return vlsnFreezeLatch;
    }

    public void resetReadyLatch(Exception exception) {
        ExceptionAwareCountDownLatch old = readyLatch;
        readyLatch = new ExceptionAwareCountDownLatch(repImpl, 1);
        if (old.getCount() != 0) {
            /* releasing latch in some error situation. */
            old.releaseAwait(exception);
        }
    }

    /* The methods below return the components of the rep node. */
    public FeederManager feederManager() {
        return feederManager;
    }

    public Replica replica() {
        return replica;
    }

    public Clock getClock() {
        return clock;
    }

    public Replica getReplica() {
        return replica;
    }

    public RepGroupDB getRepGroupDB() {
        return repGroupDB;
    }

    /**
     * Retrieves the node's current snapshot image of the group definition.
     * <p>
     * There is a very brief period of time, during node start-up, where this
     * can be <code>null</code>.  But after that it should always return a
     * valid object.
     */
    public RepGroupImpl getGroup() {
        return group;
    }

    /**
     * Returns the UUID associated with the replicated environment.
     */
    public UUID getUUID() {
        if (group == null) {
            throw EnvironmentFailureException.unexpectedState
                ("Group info is not available");
        }
        return group.getUUID();
    }

    /**
     * Returns the nodeName associated with this replication node.
     *
     * @return the nodeName
     */
    public String getNodeName() {
        return nameIdPair.getName();
    }

    /**
     * Returns the nodeId associated with this replication node.
     *
     * @return the nodeId
     */
    public int getNodeId() {
        return nameIdPair.getId();
    }

    public NameIdPair getNameIdPair() {
        return nameIdPair;
    }

    public InetSocketAddress getSocket() {
        return mySocket;
    }

    public MasterStatus getMasterStatus() {
        return masterStatus;
    }

    /**
     * Returns a definitive answer to whether this node is currently the master
     * by checking both its status as a master and whether the group agrees
     * that it is the master.
     *
     * Such an authoritative answer is needed in a network partition situation
     * to detect a master that may be isolated on the minority side of a
     * network partition.
     *
     * @return true if the node is definitely the master. False if it's not or
     * we cannot be sure.
     */
    public boolean isAuthoritativeMaster() {
        return (electionQuorum.isAuthoritativeMaster(getMasterStatus(),
                                                     feederManager));
    }

    public int getHeartbeatInterval() {
        return getConfigManager().getInt(HEARTBEAT_INTERVAL);
    }

    /* For unit testing only. */
    public void setVersion(int version) {
        logVersion = version;
    }

    public int getLogVersion() {
        return logVersion;
    }

    public int getElectionPriority() {
        final int priority =
            getConfigManager().getInt(RepParams.NODE_PRIORITY);
        final int defaultPriority =
            Integer.parseInt(RepParams.NODE_PRIORITY.getDefault());
        return (getConfigManager().getBoolean(RepParams.DESIGNATED_PRIMARY) &&
                (priority == defaultPriority)) ?
            defaultPriority + 1 : /* Raise its priority. */
            priority; /* Explicit priority, leave it intact. */
    }

    /*
     * Amount of time to wait for a thread to finish on a shutdown. It's
     * a multiple of a heartbeat, since a thread typically polls for a
     * shutdown once per heartbeat.
     */
    public int getThreadWaitInterval() {
        return getHeartbeatInterval() * 4;
    }

    int getDbTreeCacheClearingOpCount() {
        return getConfigManager().getInt(DBTREE_CACHE_CLEAR_COUNT);
    }

    public RepImpl getRepImpl() {
        return repImpl;
    }

    public LogManager getLogManager() {
        return repImpl.getLogManager();
    }

    DbConfigManager getConfigManager() {
        return repImpl.getConfigManager();
    }

    public VLSNIndex getVLSNIndex() {
        return repImpl.getVLSNIndex();
    }

    public FeederTxns getFeederTxns() {
        return repImpl.getFeederTxns();
    }

    public Elections getElections() {
        return elections;
    }

    public MasterSuggestionGenerator getSuggestionGenerator() {
        return suggestionGenerator;
    }

    /* Used by unit tests only. */
    public QuorumPolicy getElectionPolicy() {
        return electionQuorumPolicy;
    }

    /**
     * Returns an array of nodes suitable for feeding log files for a network
     * restore.
     *
     * @return an array of feeder nodes
     */
    public RepNodeImpl[] getLogProviders() {
        final Set<RepNodeImpl> nodes = getGroup().getDataMembers();
        return nodes.toArray(new RepNodeImpl[nodes.size()]);
    }

    /* Used by unit tests only. */
    public LogFlusher getLogFlusher() {
        return logFlusher;
    }

    /* Configure the log flusher according to the configuration changes. */
    public void configLogFlusher(DbConfigManager configMgr) {
        boolean enableTask = configMgr.getBoolean(RUN_LOG_FLUSH_TASK);
        int flushInterval = configMgr.getDuration(LOG_FLUSH_TASK_INTERVAL);

        /* Cancel the log flushing the task if we want to. */
        if (!enableTask) {
            if (logFlusher != null) {
                logFlusher.cancelTask();
            }

            return;
        }

        /* Create LogFlusher if it's null and we do want to start the task. */
        if (logFlusher == null) {
            logFlusher = new LogFlusher(this, timer);
        }

        /* Configure the flushing task. */
        logFlusher.configFlushTask(flushInterval);
    }

    public ChannelTimeoutTask getChannelTimeoutTask() {
        return channelTimeoutTask;
    }

    public boolean isMaster() {
        return masterStatus.isNodeMaster();
    }

    public MonitorEventManager getMonitorEventManager() {
        return monitorEventManager;
    }

    /**
     * Register an AppStateMonitor with this RepNode.
     */
    public void registerAppStateMonitor(AppStateMonitor stateMonitor) {
        this.appStateMonitor = stateMonitor;
    }

    /**
     * Return the application state that defined in user specified
     * AppStateMonitor.
     */
    public byte[] getAppState() {

        /*
         * If the AppStateMonitor is not defined, or there is currently no
         * returned application state, return null.
         */
        if (appStateMonitor == null || appStateMonitor.getAppState() == null) {
            return null;
        }

        /* Application state shouldn't be a zero length byte array. */
        if (appStateMonitor.getAppState().length == 0) {
            throw new IllegalStateException
                ("Application state should be a byte array larger than 0.");
        }

        return appStateMonitor.getAppState();
    }

    /* Get the current master name if it exists. */
    public String getMasterName() {
        if (masterStatus.getGroupMasterNameId().getId() ==
            NameIdPair.NULL_NODE_ID) {
            return null;
        }

        return masterStatus.getGroupMasterNameId().getName();
    }

    /**
     * Returns the latest VLSN associated with a replicated commit. Note that
     * since the lastTxnEndVLSN is computed outside the write log latch, via
     * EnvironmentImpl.registerVLSN(LogItem) it's possible for it to be behind
     * on an instantaneous basis, but it will eventually catch up when the
     * updates quiesce.
     */
    public VLSN getCurrentTxnEndVLSN() {
        return repImpl.getLastTxnEnd();
    }

    /*
     * Testing API used to force this node as a master. The mastership is
     * communicated upon election completion via the Listener. It's the
     * responsibility of the caller to ensure that only one node is forced
     * at a time via this API.
     *
     * @param force true to force this node as the master, false reverts back
     *              to use of normal (non-preemptive) elections.
     */
    public void forceMaster(boolean force)
        throws InterruptedException, DatabaseException {

        suggestionGenerator.forceMaster(force);
        /* Initiate elections to make the changed proposal heard. */
        refreshCachedGroup();
        elections.initiateElection(group, electionQuorumPolicy);
    }

    /**
     * Starts up the thread in which the node does its processing as a master
     * or replica. It then waits for the newly started thread to transition it
     * out of the DETACHED state, and returns upon completion of this
     * transition.
     *
     * @throws DatabaseException
     */
    private void startup(QuorumPolicy initialElectionPolicy)
        throws DatabaseException {

        if (isAlive()) {
            return;
        }

        if (nodeState.getRepEnvState().isDetached()) {
            nodeState.changeAndNotify(UNKNOWN, NameIdPair.NULL);
        }
        elections = new Elections(new RepElectionsConfig(this),
                                  changeListener,
                                  suggestionGenerator);

        repImpl.getStartupTracker().start(Phase.FIND_MASTER);
        try {

            if (repImpl.getConfigManager().
                getBoolean(RepParams.RESET_REP_GROUP)) {
                /* Invoked by DbResetRepGroup utility */
                reinitSelfElect();
            } else {
                findMaster();
            }
            this.electionQuorumPolicy = initialElectionPolicy;

            /* Electable members should participate in elections */
            if (electionQuorum.nodeTypeParticipates(nodeType)) {
                elections.participate();
            }
        } finally {
            repImpl.getStartupTracker().stop(Phase.FIND_MASTER);
        }

        start();
    }

    /**
     * This method must be invoked when a RepNode is first initialized and
     * subsequently every time there is a change to the replication group.
     * <p>
     * The Master should invoke this method each time a member is added or
     * removed, and a replica should invoke it each time it detects the commit
     * of a transaction that modifies the membership database.
     * <p>
     * In addition, it must be invoked after a syncup operation, since it may
     * revert changes made to the membership table.
     *
     * @throws DatabaseException
     */
    public RepGroupImpl refreshCachedGroup()
        throws DatabaseException {

        group = repGroupDB.getGroup();
        elections.updateRepGroup(group);
        if (nameIdPair.hasNullId()) {
            RepNodeImpl n = group.getMember(nameIdPair.getName());
            if (n != null) {

                /*
                 * Don't update the node ID for a secondary node if
                 * IGNORE_SECONDARY_NODE_ID is true.  In that case, we are
                 * trying to convert a previously electable node to a secondary
                 * node, so the information about the electable node ID in the
                 * local copy of the rep group DB should be ignored.
                 */
                if (!nodeType.isSecondary() ||
                    !getConfigManager().getBoolean(IGNORE_SECONDARY_NODE_ID)) {
                    /* May not be sufficiently current in the rep stream. */
                    nameIdPair.update(n.getNameIdPair());
                }
            }
        }
        return group;
    }

    /**
     * Removes a node so that it's no longer a member of the group.
     *
     * Note that names referring to removed nodes cannot be reused.
     *
     * @param nodeName identifies the node to be removed
     *
     * @throws MemberNotFoundException if the node denoted by
     * <code>memberName</code> is not a member of the replication group.
     *
     * @throws MasterStateException if the member being removed is currently
     * the Master
     *
     * @see <a href="https://sleepycat.oracle.com/trac/wiki/DynamicGroupMembership#DeletingMembers">Member Deletion</a>
     */
    public void removeMember(String nodeName) {
        removeMember(nodeName, false);
    }

    /**
     * Remove or delete a node from the group.  If deleting a node, the node
     * must not be active.
     *
     * <p>Note that names referring to removed nodes cannot be reused, but
     * names for deleted nodes can be.
     *
     * @param nodeName identifies the node to be removed or deleted
     *
     * @param delete whether to delete the node rather than just remove it
     *
     * @throws MemberActiveException if {@code delete} is {@code true} and
     * the node is currently active
     *
     * @throws MemberNotFoundException if the node denoted by
     * <code>memberName</code> is not a member of the replication group.
     *
     * @throws MasterStateException if the member being removed or deleted is
     * currently the Master
     */
    public void removeMember(String nodeName, boolean delete) {
        checkValidity(
            nodeName, delete ? "Deleting member" : "Removing member");

        if (delete && feederManager.activeReplicas().contains(nodeName)) {
            throw new MemberActiveException(
                "Attempt to delete an active node: " + nodeName);
        }

        /*
         * First remove it from the cached group, effectively setting new
         * durability requirements, for the ensuing group db updates.
         */
        RepNodeImpl node = group.removeMember(nodeName, delete);

        /*
         * Shutdown any feeder that may be active with the replica. Unless
         * deleting, any subsequent attempts by the replica to rejoin the group
         * will result in a failure.
         */
        feederManager.shutdownFeeder(node);
        repGroupDB.removeMember(node, delete);
    }

    /**
     * Update the network address of a node.
     *
     * Note that an alive node's address can't be updated, we'll throw an
     * ReplicaStateException for this case.
     *
     * @param nodeName identifies the node to be updated
     * @param newHostName the new host name of this node
     * @param newPort the new port of this node
     */
    public void updateAddress(String nodeName,
                              String newHostName,
                              int newPort) {
        final RepNodeImpl node =
            checkValidity(nodeName, "Updating node's address");

        /* Check whether the node is still alive. */
        if (feederManager.getFeeder(nodeName) != null) {
            throw new ReplicaStateException
                ("Can't update the network address for a live node.");
        }

        /* Update the node information in the group database. */
        node.setHostName(newHostName);
        node.setPort(newPort);
        node.setQuorumAck(false);
        repGroupDB.updateMember(node, true);
    }

    /**
     * Transfer the master role to one of the specified replicas.
     * <p>
     * We delegate most of the real work to an instance of the {@link
     * MasterTransfer} class.  Here, after some simple initial validity
     * checking, we're concerned with coordinating the potential for multiple
     * overlapping Master Transfer operation attempts.  The possible outcomes
     * are:
     * <ol>
     * <li>complete success ({@code done == true})
     * <ul>
     * <li>
     * don't unblock txns here; that'll happen automatically as part of the
     * usual handling when the environment transitions from master->replica
     * state.
     * <li>
     * don't clear xfrInProgress, because we don't want to allow another
     * attempt to supersede
     * </ul>
     * <li>timeout before establishing a winner (no superseder)
     * <ul>
     * <li>unblock txns
     * <li>clear xfrInProgress
     * </ul>
     * <li>superseded (see {@link #setUpTransfer})
     * <ul>
     * <li>abort existing op (if permitted), unblock txns before unleashing the
     * new one
     * <li>replace xfrInProgress
     * </ul>
     * <li>env is closed (or invalidated because of an error) during the
     * operation
     * <ul>
     * <li>release the block
     * <li>leave xfrInProgress as is.
     * </ul>
     * </ol>
     *
     * @param replicas candidate targets for new master role
     * @param timeout time limit, in msec
     * @param force whether to replace any existing, in-progress
     * transfer operation
     */
    public String transferMaster(Set<String> replicas,
                                 long timeout,
                                 boolean force) {
        if (replicas == null || replicas.isEmpty()) {
            throw new IllegalArgumentException
                ("Parameter 'replicas' cannot be null or empty");
        }
        if (!nodeState.getRepEnvState().isMaster()) {
            throw new IllegalStateException("Not currently master");
        }
        if (replicas.contains(getNodeName())) {

            /*
             * The local node is on the list of candidate new masters, and
             * we're already master: the operation is trivially satisfied.
             */
            return getNodeName();
        }
        for (String rep : replicas) {
            RepNodeImpl node = group.getNode(rep);
            if (node == null || node.isRemoved()) {
                throw new IllegalArgumentException
                    ("Node '" + rep +
                     "' is not currently an active member of the group");
            } else if (!node.getType().isElectable()) {
                throw new IllegalArgumentException
                    ("Node '" + rep +
                     "' must have node type ELECTABLE, but had type " +
                     node.getType());
            }
        }

        MasterTransfer xfr = setUpTransfer(replicas, timeout, force);
        boolean done = false;
        try {
            String winner = xfr.transfer();
            done = true;
            return winner;
        } finally {
            synchronized (this) {
                if (xfrInProgress == xfr && !done) {
                    xfrInProgress = null;
                }
            }
        }
    }

    /**
     * Sets up a Master Transfer operation, ensuring that only one operation
     * can be in progress at a time.
     */
    synchronized private MasterTransfer setUpTransfer(Set<String> replicas,
                                                      long timeout,
                                                      boolean force) {
        boolean reject = false; // initial guess, refine below if nec.
        if (xfrInProgress != null) {
            reject = true;      // next best guess, refine below again if nec.

            /*
             * If the new operation is "forcing", see if we can abort the
             * existing one.
             */
            if (force &&
                xfrInProgress.abort
                (new MasterTransferFailureException("superseded"))) {
                reject = false;

                repImpl.unblockTxnCompletion();
            }
        }
        if (reject) {
            throw new MasterTransferFailureException
                ("another Master Transfer (started at " +
                 new Date(xfrInProgress.getStartTime()) +
                 ") is already in progress");
        }
        xfrInProgress = new MasterTransfer(replicas, timeout, this);
        return xfrInProgress;
    }

    public MasterTransfer getActiveTransfer() {
        return xfrInProgress;
    }

    /**
     * Called by the RepNode when a transition to replica status has completely
     * finished.
     */
    public synchronized void clearActiveTransfer() {
        xfrInProgress = null;
    }

    /**
     * Performs some basic validity checking, common code for some
     * Group Membership operations.
     *
     * @param nodeName name of a replica node on which an operation is
     * to be performed
     * @param actionName textual description of the operation (for
     * exception message)
     * @return the named node
     */
    private RepNodeImpl checkValidity(String nodeName, String actionName)
        throws MemberNotFoundException {

        if (!nodeState.getRepEnvState().isMaster()) {
            throw EnvironmentFailureException.unexpectedState
                ("Not currently a master. " + actionName + " must be " +
                 "invoked on the node that's currently the master.");
        }

        final RepNodeImpl node = group.getNode(nodeName);
        if (node == null) {
            throw new MemberNotFoundException("Node:" + nodeName +
                                              "is not a member of the group:" +
                                              group.getName());
        }

        if (node.isRemoved() && node.isQuorumAck()) {
            throw new MemberNotFoundException("Node:" + nodeName +
                                              "is not currently a member of " +
                                              "the group:" + group.getName() +
                                              " It had been removed.");
        }

        /* Check if the node is the master itself. */
        if (nodeName.equals(getNodeName())) {
            throw new MasterStateException(getRepImpl().
                                           getStateChangeEvent());
        }

        return node;
    }

    /**
     * Updates the cached group info for the node, avoiding a database read.
     *
     * @param updateNameIdPair the node whose localCBVLSN must be updated.
     * @param barrierState the new node syncup state
     */
    public void updateGroupInfo(NameIdPair updateNameIdPair,
                                RepGroupImpl.BarrierState barrierState) {

        RepNodeImpl node = group.getMember(updateNameIdPair.getName());
        if (node == null) {
            /*  A subsequent refresh will get it, along with the new node. */
            return;
        }

        LoggerUtils.fine(logger, repImpl,
                         "LocalCBVLSN for " + updateNameIdPair +
                         " updated to " + barrierState +
                         " from " + node.getBarrierState().getLastCBVLSN());
        node.setBarrierState(barrierState);
        globalCBVLSN.recalculate(group);
    }

    /**
     * Recalculate the Global CBVLSN, provoked by Replay, to ensure that the
     * replica's global CBVLSN is up to date.
     */
    public void recalculateGlobalCBVLSN() {
        globalCBVLSN.recalculate(group);
    }

    LocalCBVLSNTracker getCBVLSNTracker() {
        return cbvlsnTracker;
    }

    public void freezeLocalCBVLSN() {
        cbvlsnTracker.incrementFreezeCounter();
    }

    public void unfreezeLocalCBVLSN() {
        cbvlsnTracker.decrementFreezeCounter();
    }

    /**
     * Finds a master node.
     *
     * @throws DatabaseException
     */
    private void findMaster()
        throws DatabaseException {

        refreshCachedGroup();
        elections.startLearner();
        LoggerUtils.info(logger, repImpl, "Current group size: " +
                         group.getElectableGroupSize());
        final RepNodeImpl thisNode = group.getNode(nameIdPair.getName());
        if ((thisNode == null) &&

            /*
             * Secondary nodes are not stored in the group DB, so they will not
             * be found even though they are not new.  Use group UUID to
             * distinguish -- it is only unknown if the node is new.
             */
            (nodeType.isElectable() || group.hasUnknownUUID())) {

            /* A new node */
            LoggerUtils.info(logger, repImpl, "New node " + nameIdPair +
                             " unknown to rep group");
            Set<InetSocketAddress> helperSockets = repImpl.getHelperSockets();

            /*
             * Not present in the replication group. Use the helper, to get
             * to a master and enter the group.
             */
            if ((group.getElectableGroupSize() == 0) &&
                (helperSockets.size() == 1) &&
                nodeType.isElectable() &&
                serviceDispatcher.getSocketAddress().
                    equals(helperSockets.iterator().next())) {
                /* A startup situation, should this node become master. */
                selfElect();
                elections.updateRepGroup(group);
                return;
            }
            try {
                queryGroupForMembership();
            } catch (InterruptedException e) {
                throw EnvironmentFailureException.unexpectedException(e);
            }
        } else if ((thisNode != null) && thisNode.isRemoved()) {
            throw EnvironmentFailureException.unexpectedState
                ("Node: " + nameIdPair.getName() +
                 " was previously deleted.");
        } else {

            /* An existing node */
            LoggerUtils.info(logger, repImpl,
                             "Existing node " + nameIdPair.getName() +
                             " querying for a current master.");

            /*
             * The group has other members, see if they know of a master,
             * along with any helpers that were also supplied.
             */
            Set<InetSocketAddress> helperSockets = repImpl.getHelperSockets();
            helperSockets.addAll(group.getAllHelperSockets());
            elections.getLearner().queryForMaster(helperSockets);
        }
    }

    /**
     * This method enforces the requirement that all addresses within a
     * replication group, must be loopback addresses or they must all be
     * non-local ip addresses. Mixing them means that the node with a loopback
     * address cannot be contacted by a different node.  Addresses specified by
     * hostnames that currently have no DNS entries are assumed to not be
     * loopback addresses.
     *
     * @param helperSockets the helper nodes used by this node when contacting
     * the master.
     */
    private void checkLoopbackAddresses(Set<InetSocketAddress> helperSockets) {

        final InetAddress myAddress = mySocket.getAddress();
        final boolean isLoopback = myAddress.isLoopbackAddress();

        for (InetSocketAddress socketAddress : helperSockets) {
            final InetAddress nodeAddress = socketAddress.getAddress();

            /*
             * If the node address was specified with a hostname that does not,
             * at least currently, have a DNS entry, then the address will be
             * null.  We can safely assume this will not happen for loopback
             * addresses, whose host names and addresses are both fixed.
             */
            final boolean nodeAddressIsLoopback =
                (nodeAddress != null) && nodeAddress.isLoopbackAddress();

            if (nodeAddressIsLoopback == isLoopback) {
                continue;
            }
            String message = mySocket +
                " the address associated with this node, " +
                (isLoopback? "is " : "is not ") +  "a loopback address." +
                " It conflicts with an existing use, by a different node " +
                " of the address:" +
                socketAddress +
                (!isLoopback ? " which is a loopback address." :
                 " which is not a loopback address.") +
                " Such mixing of addresses within a group is not allowed, " +
                "since the nodes will not be able to communicate with " +
                "each other.";
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Communicates with existing nodes in the group in order to figure out how
     * to start up, in the case where the local node does not appear to be in
     * the (local copy of the) GroupDB, typically because the node is starting
     * up with an empty env directory.  It could be that this is a new node
     * (never before been part of the group).  Or it could be a pre-existing
     * group member that has lost its env dir contents and wants to be restored
     * via a Network Restore operation.
     * <p>
     * We first try to find a currently running master node.  (An authoritative
     * master can easily handle either of the above-mentioned situations.)  If
     * we can't find a master, we look for other running nodes that may know of
     * us (by asking them for their Group information).
     * <p>
     * We query the designated helpers and all known learners.  The helpers are
     * the ones that were identified via the node's configuration, while the
     * learners are the ones currently in the member database.  We use both in
     * order to cast the widest possible net.
     * <p>
     * Returns normally when the master is found.
     *
     * @throws InterruptedException if the current thread is interrupted,
     *         typically due to a shutdown
     * @throws InsufficientLogException if the environment requires a network
     *         restore
     * @see #findRestoreSuppliers
     */
    private void queryGroupForMembership()
        throws InterruptedException {

        Set<InetSocketAddress> helperSockets = repImpl.getHelperSockets();

        checkLoopbackAddresses(helperSockets);

        /*
         * Not in the rep group. Use the designated helpers and other members
         * of the group to help us figure out how to get started.
         */
        final Set<InetSocketAddress> helpers =
            new HashSet<InetSocketAddress>(helperSockets);
        helpers.addAll(group.getAllHelperSockets());
        if (helpers.isEmpty()) {
            throw EnvironmentFailureException.unexpectedState
                ("Need a helper to add a new node into the group");
        }

        NameIdPair groupMasterNameId;
        while (true) {
            elections.getLearner().queryForMaster(helpers);
            groupMasterNameId = masterStatus.getGroupMasterNameId();
            if (!groupMasterNameId.hasNullId()) {
                /* A new, or pre-query, group master. */
                if (nameIdPair.hasNullId() &&
                    groupMasterNameId.getName().equals(nameIdPair.getName())) {

                    /*
                     * Residual obsolete information in replicas, ignore it.
                     * Can't be master if we don't know our own id, but some
                     * other node does! This state means that the node was a
                     * master in the recent past, but has had its environment
                     * deleted since that time.
                     */
                    try {
                        Thread.sleep(MASTER_QUERY_INTERVAL);
                    } catch (InterruptedException e) {
                        throw EnvironmentFailureException.unexpectedException(
                            e);
                    }
                    continue;
                }

                if (checkGroupMasterIsAlive(groupMasterNameId)) {
                    /* Use the current group master if it's alive. */
                    break;
                }
            }

            /*
             * If there's no master, or the last known master cannot be
             * reached, see if anyone thinks we're actually already in the
             * group, and could supply us with a Network Restore. (Remember,
             * we're here only if we didn't find ourselves in the local
             * GroupDB. So we could be in a group restore from backup
             * situation.)
             */
            findRestoreSuppliers(helpers);

            Thread.sleep(MASTER_QUERY_INTERVAL);
        }
        LoggerUtils.info(logger, repImpl, "New node " + nameIdPair.getName() +
                         " located master: " + groupMasterNameId);
    }

    /**
     * Check that the master found by querying other group nodes is indeed
     * alive and that we are not dealing with obsolete cached information.
     *
     * @return true if the master node could be contacted and was truly alive
     *
     * TODO: handle protocol version mismatch here and in DbPing, also
     * consolidate code so that a single copy is shared.
     */
    private boolean checkGroupMasterIsAlive(NameIdPair groupMasterNameId) {

        DataChannel channel = null;

        try {
            final InetSocketAddress masterSocket =
                masterStatus.getGroupMaster();

            final BinaryNodeStateProtocol protocol =
                new BinaryNodeStateProtocol(NameIdPair.NOCHECK, null);

            /* Build the connection. Set the parameter connectTimeout.*/
            channel = repImpl.getChannelFactory().
                connect(masterSocket,
                        new ConnectOptions().
                        setTcpNoDelay(true).
                        setOpenTimeout(5000).
                        setReadTimeout(5000));
            ServiceDispatcher.doServiceHandshake
                (channel, BinaryNodeStateService.SERVICE_NAME);

            /* Send a NodeState request to the node. */
            protocol.write
                (protocol.new
                    BinaryNodeStateRequest(groupMasterNameId.getName(),
                                           group.getName()),
                 channel);

            /* Get the response and return the NodeState. */
            BinaryNodeStateResponse response =
                protocol.read(channel, BinaryNodeStateResponse.class);

            ReplicatedEnvironment.State state = response.getNodeState();
           return (state != null) && state.isMaster();
        } catch (Exception e) {
            LoggerUtils.info(logger, repImpl,
                             "Queried master:" + groupMasterNameId +
                             " unavailable. Reason:" + e);
            return false;
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ioe) {
                    /* Ignore it */
                }
            }
        }
    }

    /**
     * Sets up a Network Restore, as part of the process of restoring an entire
     * group from backup, by producing an appropriate {@code
     * InsufficientLogException} if possible.
     * <p>
     * Queries each of the supplied helper hosts for their notion of the group
     * make-up.  If any of them consider us to be already in the group, then
     * instead of joining the group as a new node we ought to try a Network
     * Restore; and the node(s) that do already know of us are the suitable
     * suppliers for it.
     *
     * @throws InsufficientLogException in the successful case, if one or more
     * suitable suppliers for a Network Restore can be found; otherwise just
     * returns.
     */
    public void findRestoreSuppliers(Set<InetSocketAddress> helpers) {
        final Set<ReplicationNode> suppliers = new HashSet<ReplicationNode>();
        RepGroupProtocol protocol =
            new RepGroupProtocol(group.getName(), nameIdPair, repImpl,
                                 repImpl.getChannelFactory());

        for (InetSocketAddress helper : helpers) {
            MessageExchange msg =
                protocol.new MessageExchange(helper,
                                             GroupService.SERVICE_NAME,
                                             protocol.new GroupRequest());

            /*
             * Just as we did in the queryForMaster() case, quietly ignore any
             * unsurprising response error or socket exceptions; we'll retry
             * later if we end up not finding any Network Restore suppliers.
             */
            msg.run();
            ResponseMessage response = msg.getResponseMessage();
            if (response == null ||
                protocol.RGFAIL_RESP.equals(response.getOp())) {
                continue;
            } else if (!protocol.GROUP_RESP.equals(response.getOp())) {
                LoggerUtils.warning(logger, repImpl,
                                    "Expected GROUP_RESP, got " +
                                    response.getOp() + ": " + response);
                continue;
            }
            GroupResponse groupResp = (GroupResponse) response;

            /*
             * If the response from the remote node shows that I am already a
             * member of the group, add the node to the list of nodes that will
             * serve the Network Restore.
             */
            RepGroupImpl groupInfo = groupResp.getGroup();
            RepNodeImpl me = groupInfo.getNode(nameIdPair.getName());
            if (me == null || me.isRemoved() || !me.isQuorumAck()) {
                continue;
            }

            ReplicationNode supplier = groupInfo.getMember(helper);
            if (supplier != null) {
                suppliers.add(supplier);
            }
        }

        if (suppliers.isEmpty()) {
            return;
        }

        throw new InsufficientLogException(this, VLSN.NULL_VLSN, suppliers);
    }

    /**
     * Elects this node as the master. The operation is only valid when the
     * group consists of just this node, and when this is an ELECTABLE node.
     *
     * @throws DatabaseException
     * @throws IllegalStateException if the node type is not ELECTABLE
     */
    private void selfElect()
        throws DatabaseException {

        if (!nodeType.isElectable()) {
            throw new IllegalStateException(
                "Cannot elect node " + nameIdPair.getName() +
                " as master because its node type, " + nodeType +
                ", is not ELECTABLE");
        }
        nameIdPair.setId(RepGroupImpl.getFirstNodeId());

        /* Master by default of a nascent group. */
        Proposal proposal = new TimebasedProposalGenerator().nextProposal();
        elections.getLearner().processResult(proposal,
                                             suggestionGenerator.get(proposal));
        LoggerUtils.info(logger, repImpl, "Nascent group. " +
                         nameIdPair.getName() +
                         " is master by virtue of being the first node.");
        masterStatus.sync();
        nodeState.changeAndNotify(MASTER, masterStatus.getNodeMasterNameId());
        repImpl.getVLSNIndex().initAsMaster();
        repGroupDB.addFirstNode();
        refreshCachedGroup();
        /* Unsync so that the run loop does not call for an election. */
        masterStatus.unSync();
    }

    /**
     * Establishes this node as the master, after re-initializing the group
     * with this as the sole node in the group. This method is used solely
     * as part of the DbResetRepGroup utility.
     *
     * @throws IllegalStateException if the node type is not ELECTABLE
     */
    private void reinitSelfElect() {
        if (nodeType.isSecondary()) {
            throw new IllegalStateException(
                "Cannot elect node " + nameIdPair.getName() +
                " as master because its node type, " + nodeType +
                ", is not ELECTABLE");
        }

        /* Establish an empty group so transaction commits can proceed. */
        group = repGroupDB.emptyGroup;
        LoggerUtils.info(logger, repImpl, "Reinitializing group to node " +
                         nameIdPair);

        /*
         * Unilaterally transition the nodeState to Master, so that write
         * transactions needed to reset the group and establish this node can
         * be issued against the environment.
         */
        nodeState.changeAndNotify(MASTER, masterStatus.getNodeMasterNameId());
        repImpl.getVLSNIndex().initAsMaster();

        /*
         * Start using new log files. The file ensures that we can safely
         * truncate the past VLSNs.
         */
        repImpl.forceLogFileFlip();

        CheckpointConfig ckptConfig = new CheckpointConfig();
        ckptConfig.setForce(true);

        /*
         * The checkpoint ensures that we do not have to replay VLSNs from the
         * prior group and that we have a complete VLSN index on disk.
         */
        repImpl.getCheckpointer().doCheckpoint(ckptConfig,
                                               "Reinit of RepGroup");
        VLSN lastOldVLSN = repImpl.getVLSNIndex().getRange().getLast();

        /* Now create the new rep group on disk. */
        repGroupDB.reinitFirstNode(lastOldVLSN);
        refreshCachedGroup();

        long lastOldFile =
            repImpl.getVLSNIndex().getLTEFileNumber(lastOldVLSN);

        /*
         * Discard the VLSN index covering the pre group reset VLSNS, to ensure
         * that the pre reset part of the log is never replayed. We don't want
         * to replay this part of the log, since it contains references to
         * repnodes via node ids that are no longer part of the reset rep
         * group. Note that we do not reuse rep node ids, that is, rep node id
         * sequence continues across the reset operation and is not itself
         * reset. Nodes joining the new group will need to do a network restore
         * when they join the group.
         *
         * Don't perform the truncation if RESET_REP_GROUP_RETAIN_UUID is true.
         * In that case, we are only removing the rep group members, but
         * retaining the remaining information, because we will be restarting
         * the rep group in place with an old secondary acting as an electable
         * node.
         */
        final boolean retainUUID =
            getConfigManager().getBoolean(RESET_REP_GROUP_RETAIN_UUID);
        if (!retainUUID) {
            repImpl.getVLSNIndex().truncateFromHead(lastOldVLSN, lastOldFile);
        }

        elections.startLearner();
        /* Unsync so that the run loop does not call for an election. */
        masterStatus.unSync();
    }

    /**
     * The top level Master/Feeder or Replica loop in support of replication.
     * It's responsible for driving the node level state changes resulting
     * from elections initiated either by this node, or by other members of the
     * group.
     * <p>
     * The thread is terminated via an orderly shutdown initiated as a result
     * of an interrupt issued by the shutdown() method. Any exception that is
     * not handled by the run method itself is caught by the thread's uncaught
     * exception handler, and results in the RepImpl being made invalid.  In
     * that case, the application is responsible for closing the Replicated
     * Environment, which will provoke the shutdown.
     * <p>
     * Note: This method currently runs either the feeder loop or the replica
     * loop. With R to R support, it would be possible for a Replica to run
     * both. This will be a future feature.
     */
    @Override
    public void run() {
        /* Set to indicate an error-initiated shutdown. */
        Error repNodeError = null;
        try {
            LoggerUtils.info(logger, repImpl,
                             "Node " + nameIdPair.getName() + " started" +
                             (!nodeType.isElectable() ?
                              " as " + nodeType :
                              ""));
            while (!isShutdownOrInvalid()) {
                if (nodeState.getRepEnvState() != UNKNOWN) {
                    /* Avoid unnecessary state changes. */
                    nodeState.changeAndNotify(UNKNOWN, NameIdPair.NULL);
                }

                /*
                 * Initiate elections if we don't have a group master, or there
                 * is a master, but we were unable to use it.
                 */
                if (masterStatus.getGroupMasterNameId().hasNullId() ||
                    masterStatus.inSync()) {

                    /*
                     * But we can't if we don't have our own node ID yet or if
                     * we are not ELECTABLE.
                     */
                    if (nameIdPair.hasNullId() || !nodeType.isElectable()) {
                        queryGroupForMembership();
                    } else {
                        elections.initiateElection(group, electionQuorumPolicy);

                        /*
                         * Subsequent elections must always use a simple
                         * majority.
                         */
                        electionQuorumPolicy = QuorumPolicy.SIMPLE_MAJORITY;
                    }
                    /* In case elections were shut down. */
                    if (isShutdownOrInvalid()) {
                        return;
                    }
                }

                /* Start syncing this node to the new group master */
                masterStatus.sync();

                if (masterStatus.isNodeMaster()) {
                    repImpl.getVLSNIndex().initAsMaster();
                    replica.masterTransitionCleanup();

                    /* Master is ready for business. */
                    nodeState.changeAndNotify
                            (MASTER, masterStatus.getNodeMasterNameId());

                    /*
                     * Update the JE version information stored for the master
                     * in the RepGroupDB, if needed.
                     */
                    maybeUpdateMasterJEVersion();

                    feederManager.runFeeders();

                    /*
                     * At this point, the feeder manager has been shutdown.
                     * Re-initialize the VLSNIndex put latch mechanism, which
                     * is present on masters to maintain a tip cache of the
                     * last record on the replication stream, and by all
                     * nodes when doing checkpoint vlsn consistency waiting.
                     * Create a new feeder manager, should this node become a
                     * master later on.
                     * Set the node to UNKNOWN state right away, because the
                     * MasterTxn will use node state to prevent the advent of
                     * any replicated writes.  Once the VLSNIndex is
                     * initialized for replica state, the node will NPE if it
                     * attempts execute replicated writes.
                     */
                    nodeState.changeAndNotify(UNKNOWN, NameIdPair.NULL);
                    repImpl.getVLSNIndex().initAsReplica();
                    assert runConvertHooks();
                    feederManager = new FeederManager(this);
                } else {

                    /*
                     * Replica will notify us when connection is successfully
                     * made, and Feeder handshake done, at which point we'll
                     * update nodeState.
                     */
                    replica.replicaTransitionCleanup();
                    replica.runReplicaLoop();
                }
            }
        } catch (InterruptedException e) {
            LoggerUtils.fine(logger, repImpl,
                             "RepNode main thread interrupted - " +
                             " forced shutdown.");
        } catch (GroupShutdownException e) {
            saveShutdownException(e);
            LoggerUtils.fine(logger, repImpl,
                             "RepNode main thread sees group shutdown - " + e);
        } catch (InsufficientLogException e) {
            saveShutdownException(e);
        } catch (RuntimeException e) {
            LoggerUtils.fine(logger, repImpl,
                             "RepNode main thread sees runtime ex - " + e);
            saveShutdownException(e);
            throw e;
        } catch (Error e) {
            LoggerUtils.fine(logger, repImpl, e +
                             " incurred during repnode loop");
            repNodeError = e;
            repImpl.invalidate(e);
        } finally {
            try {
                LoggerUtils.info(logger, repImpl,
                                 "RepNode main thread shutting down.");

                if (repNodeError != null) {
                    LoggerUtils.info(logger, repImpl,
                                     "Node state at shutdown:\n"+
                                     repImpl.dumpState());
                    throw repNodeError;
                }
                Throwable exception = getSavedShutdownException();

                if (exception == null) {
                    LoggerUtils.fine(logger, repImpl,
                                     "Node state at shutdown:\n"+
                                     repImpl.dumpState());
                } else {
                    LoggerUtils.info(logger, repImpl,
                                     "RepNode shutdown exception:\n" +
                                     exception.getMessage() +
                                     repImpl.dumpState());
                }

                try {
                    shutdown();
                } catch (DatabaseException e) {
                    RepUtils.chainExceptionCause(e, exception);
                    LoggerUtils.severe(logger, repImpl,
                                       "Unexpected exception during shutdown" +
                                       e);
                    throw e;
                }
            } catch (InterruptedException e1) {
                // Ignore exceptions on exit
            }
            nodeState.changeAndNotify(DETACHED, NameIdPair.NULL);
            cleanup();
        }
    }

    /**
     * Update the information stored for the master in the RepGroupDB if
     * storing it is supported and the current version is different from the
     * recorded version.
     */
    private void maybeUpdateMasterJEVersion() {

        /* Check if storing JE version information is supported */
        if (group.getFormatVersion() < RepGroupImpl.FORMAT_VERSION_3) {
            return;
        }

        final JEVersion currentJEVersion = repImpl.getCurrentJEVersion();
        final RepNodeImpl node = group.getMember(nameIdPair.getName());

        if (currentJEVersion.equals(node.getJEVersion())) {
            return;
        }
        node.updateJEVersion(currentJEVersion);
        repGroupDB.updateMember(node, false);
    }

    void notifyReplicaConnected() {
        nodeState.changeAndNotify(REPLICA, masterStatus.getNodeMasterNameId());
    }

    /**
     * Returns true if the node has been shutdown or if the underlying
     * environment has been invalidated. It's used as the basis for exiting
     * the FeederManager or the Replica.
     */
    boolean isShutdownOrInvalid() {
        if (isShutdown()) {
            return true;
        }
        if (getRepImpl().wasInvalidated()) {
            saveShutdownException(getRepImpl().getInvalidatingException());
            return true;
        }
        return false;
    }

    /**
     * Used to shutdown all activity associated with this replication stream.
     * If method is invoked from different thread of control, it will wait
     * until the rep node thread exits. If it's from the same thread, it's the
     * caller's responsibility to exit the thread upon return from this method.
     *
     * @throws InterruptedException
     * @throws DatabaseException
     */
    public void shutdown()
        throws InterruptedException, DatabaseException {

        if (shutdownDone()) {
            return;
        }

        LoggerUtils.info(logger, repImpl, "Shutting down node " + nameIdPair);

        /* Fire a LeaveGroup if this RepNode is valid. */
        if (repImpl.isValid()) {
            monitorEventManager.notifyLeaveGroup(getLeaveReason());
        }

        /* Stop accepting any new network requests. */
        serviceDispatcher.preShutdown();

        if (elections != null) {
            elections.shutdown();
        }

        /* Initiate the FeederManger soft shutdown if it's active. */
        feederManager.shutdownQueue();

        if ((getReplicaCloseCatchupMs() >= 0) &&
            (nodeState.getRepEnvState().isMaster())) {

            /*
             * A group shutdown. Shutting down the queue will cause the
             * FeederManager to shutdown its feeders and exit.
             */
            this.join();
        }

        /* Shutdown the replica, if it's active. */
        replica.shutdown();

        shutdownThread(logger);

        LoggerUtils.info(logger, repImpl,
                         "RepNode main thread: " + this.getName() + " exited.");
        /* Shut down all other services. */
        utilityServicesShutdown();

        /* Shutdown all the services before shutting down the dispatcher. */
        MasterTransfer mt = getActiveTransfer();
        if (mt != null) {
            Exception ex = getSavedShutdownException();
            if (ex == null) {
                ex = new MasterTransferFailureException("shutting down");
            }
            mt.abort(ex);
        }
        serviceDispatcher.shutdown();
        LoggerUtils.info(logger, repImpl,
                         nameIdPair + " shutdown completed.");
        masterStatus.setGroupMaster(null, NameIdPair.NULL);
        readyLatch.releaseAwait(getSavedShutdownException());

        /* Cancel the TimerTasks. */
        channelTimeoutTask.cancel();
        if (logFlusher != null) {
            logFlusher.cancelTask();
        }
        timer.cancel();
    }

    /**
     * Soft shutdown for the RepNode thread. Note that since the thread is
     * shared by the FeederManager and the Replica, the FeederManager or
     * Replica specific soft shutdown actions should already have been done
     * earlier.
     */
    @Override
    protected int initiateSoftShutdown() {
        return getThreadWaitInterval();
    }

    /* Get the shut down reason for this node. */
    private LeaveReason getLeaveReason() {
        LeaveReason reason = null;

        Exception exception = getSavedShutdownException();
        if (exception == null) {
            reason = LeaveReason.NORMAL_SHUTDOWN;
        } else if (exception instanceof GroupShutdownException) {
            reason = LeaveReason.MASTER_SHUTDOWN_GROUP;
        } else {
            reason = LeaveReason.ABNORMAL_TERMINATION;
        }

        return reason;
    }

    private void utilityServicesShutdown() {
        if (ldiff != null) {
            ldiff.shutdown();
        }

        if (logFeederManager != null) {
            logFeederManager.shutdown();
        }

        if (binaryNodeStateService != null) {
            binaryNodeStateService.shutdown();
        }

        if (nodeStateService != null) {
            serviceDispatcher.cancel(NodeStateService.SERVICE_NAME);
        }

        if (groupService != null) {
            serviceDispatcher.cancel(GroupService.SERVICE_NAME);
        }
    }

    /**
     * Must be invoked on the Master via the last open handle.
     *
     * Note that the method itself does not shutdown the group. It merely
     * sets replicaCloseCatchupMs, indicating that the ensuing handle close
     * should shutdown the Replicas. The actual coordination with the closing
     * of the handle is implemented by ReplicatedEnvironment.shutdownGroup().
     *
     * @see ReplicatedEnvironment#shutdownGroup(long, TimeUnit)
     */
    public void shutdownGroupOnClose(long timeoutMs)
        throws IllegalStateException {

        if (!nodeState.getRepEnvState().isMaster()) {
            throw new IllegalStateException
                ("Node state must be " + MASTER +
                 ", not " + nodeState.getRepEnvState());
        }
        replicaCloseCatchupMs = (timeoutMs < 0) ? 0 : timeoutMs;
    }

    /**
     * JoinGroup ensures that a RepNode is actively participating in a
     * replication group. It's invoked each time a replicated environment
     * handle is created.
     *
     * If the node is already participating in a replication group, because
     * it's not the first handle to the environment, it will return without
     * having to wait. Otherwise it will wait until a master is elected and
     * this node is active, either as a Master, or as a Replica.
     *
     * If the node joins as a replica, it will wait further until it has become
     * sufficiently consistent as defined by its consistency argument. By
     * default it uses PointConsistencyPolicy to ensure that it is at least as
     * consistent as the master as of the time the handle was opened.
     *
     * A node can also join in the Unknown state if it has been configured to
     * do so via ENV_UNKNOWN_STATE_TIMEOUT.
     *
     * @throws UnknownMasterException If a master cannot be established within
     * ENV_SETUP_TIMEOUT, unless ENV_UNKNOWN_STATE_TIMEOUT has
     * been set to allow the creation of a handle while in the UNKNOWN state.
     *
     * @return MASTER, REPLICA, or UNKNOWN (if ENV_UNKNOWN_STATE_TIMEOUT
     * is set)
     */
    public ReplicatedEnvironment.State
        joinGroup(ReplicaConsistencyPolicy consistency,
                  QuorumPolicy initialElectionPolicy)
        throws ReplicaConsistencyException, DatabaseException {

        final JoinGroupTimeouts timeouts =
                new JoinGroupTimeouts(getConfigManager());

        startup(initialElectionPolicy);
        LoggerUtils.finest(logger, repImpl, "joinGroup " +
                           nodeState.getRepEnvState());

        DatabaseException exitException = null;
        int retries = 0;
        repImpl.getStartupTracker().start(Phase.BECOME_CONSISTENT);
        repImpl.getStartupTracker().setProgress
           (RecoveryProgress.BECOME_CONSISTENT);
        try {
            for (retries = 0; retries < JOIN_RETRIES; retries++ ) {
                try {
                    /* Wait for Feeder/Replica to be fully initialized. */
                    boolean done = getReadyLatch().awaitOrException
                        (timeouts.getTimeout(), TimeUnit.MILLISECONDS);

                    /*
                     * Save the state, and use it from this point forward,
                     * since the node's state may change again.
                     */
                    final ReplicatedEnvironment.State finalState =
                        nodeState.getRepEnvState();
                    if (!done) {

                        /* An election or setup, timeout. */
                        if (finalState.isReplica()) {
                            if (timeouts.timeoutIsForUnknownState()) {

                                /*
                                 * Replica syncing up; move onwards to the
                                 * setup timeout and continue with the syncup.
                                 */
                                timeouts.setSetupTimeout();
                                continue;
                            }
                            throw new ReplicaConsistencyException
                                (String.format("Setup time exceeded %,d ms",
                                               timeouts.getSetupTimeout()),
                                               null);
                        }

                        if (finalState.isUnknown() &&
                            timeouts.timeoutIsForUnknownState()) {
                            return UNKNOWN;
                        }
                        break;
                    }

                    switch (finalState) {
                        case UNKNOWN:

                            /*
                             * State flipped between release of ready latch and
                             * nodeState.getRepEnvState() above; retry for a
                             * Master/Replica state.
                             */
                            continue;

                        case REPLICA:
                            joinAsReplica(consistency);
                            break;

                        case MASTER:
                            LoggerUtils.info(logger, repImpl,
                                             "Joining group as master");
                            break;

                        case DETACHED:
                            throw EnvironmentFailureException.
                                unexpectedState("Node in DETACHED state " +
                                                "while joining group.");
                    }

                    return finalState;
                } catch (InterruptedException e) {
                    throw EnvironmentFailureException.unexpectedException(e);
                } catch (MasterStateException e) {
                    /* Transition to master while establishing consistency. */
                    LoggerUtils.warning(logger, repImpl,
                                        "Join retry due to master transition: "
                                        + e.getMessage());
                    continue;
                } catch (RestartRequiredException e) {
                    LoggerUtils.warning(logger, repImpl,
                                        "Environment needs to be restarted: " +
                                        e.getMessage());
                    throw e;
                } catch (DatabaseException e) {
                    Throwable cause = e.getCause();
                    if ((cause != null) &&
                        (cause.getClass() ==
                         Replica.ConnectRetryException.class)) {

                        /*
                         * The master may have changed. Retry if there is time
                         * left to do so. It may result in a new master.
                         */
                        exitException = e;
                        if (timeouts.getTimeout() > 0) {
                            LoggerUtils.warning(logger, repImpl,
                                                "Join retry due to exception: "
                                                + cause.getMessage());
                            continue;
                        }
                    }
                    throw e;
                }
            }
        } finally {
            repImpl.getStartupTracker().stop(Phase.BECOME_CONSISTENT);
        }

        /* Timed out or exceeded retries. */
        if (exitException != null) {
            LoggerUtils.warning(logger, repImpl, "Exiting joinGroup after " +
                                retries + " retries." + exitException);
            throw exitException;
        }
        throw new UnknownMasterException(null, repImpl.getStateChangeEvent());
    }

    /**
     * Join the group as a Replica ensuring that the node is sufficiently
     * consistent as defined by its consistency policy.
     *
     * @param consistency the consistency policy to use when joining initially
     */
    private void joinAsReplica(ReplicaConsistencyPolicy consistency)
        throws InterruptedException {

        if (consistency == null) {
            final int consistencyTimeout =
                getConfigManager().getDuration(ENV_CONSISTENCY_TIMEOUT);
            consistency = new PointConsistencyPolicy
                (new VLSN(replica.getMasterTxnEndVLSN()),
                 consistencyTimeout, TimeUnit.MILLISECONDS);
        }

        /*
         * Wait for the replica to become sufficiently consistent.
         */
        consistency.ensureConsistency(repImpl);

        /*
         * Flush changes to the file system. The flush ensures in particular
         * that any member database updates defining this node itself are not
         * lost in case of a process crash. See SR 20607.
         */
        repImpl.getLogManager().flushNoSync();

        LoggerUtils.info(logger, repImpl, "Joined group as a replica. " +
                         " join consistencyPolicy=" + consistency +
                         " " + repImpl.getVLSNIndex().getRange());
    }

    /**
     * Should be called whenever a new VLSN is associated with a log entry
     * suitable for Replica/Feeder syncup.
     */
    public void trackSyncableVLSN(VLSN syncableVLSN, long lsn) {
        cbvlsnTracker.track(syncableVLSN, lsn);
    }

    /**
     * Returns the group wide CBVLSN. The group CBVLSN is computed as the
     * minimum of CBVLSNs after discarding CBVLSNs that are obsolete. A CBVLSN
     * is considered obsolete, if it has not been updated within a configurable
     * time interval relative to the time that the most recent CBVLSN was
     * updated.  Also considers VLSNs protected by REPLAY_COST_PERCENT.
     *
     * <p>May return NULL_VLSN.
     */
    public VLSN getGroupCBVLSN() {
        return VLSN.min(globalCBVLSN.getCBVLSN(), replayCostMinVLSN);
    }

    /**
     * For testing purposes only
     */
    public VLSN getGlobalCBVLSN() {
        return globalCBVLSN.getCBVLSN();
    }

    /**
     * Marks the start of the search for a matchpoint that happens during a
     * syncup.  The globalCBVLSN can't be changed when a syncup is in
     * progress. A feeder may have multiple syncups in action. The caller
     * should call {@link #syncupEnded} when the syncup is done.
     */
    public void syncupStarted() {
        globalCBVLSN.syncupStarted();
    }

    public void syncupEnded() {
        globalCBVLSN.syncupEnded();
    }

    /**
     * Returns the file number that forms a barrier for the cleaner's file
     * deletion activities. Files with numbers >= this file number cannot be
     * removed by the cleaner without disrupting the replication stream.
     *
     * @return the file number that's the barrier for cleaner file deletion
     *
     * @throws DatabaseException
     */
    public long getCleanerBarrierFile()
        throws DatabaseException {

        /*
         * It turns out that this method is only called in contexts that
         * specify an active syncup is underway.  Neither the global CBVLSN or
         * the VLSNIndex can change during an active syncup, so being called
         * during an active syncup means we can depend on the global CBVLSN
         * having an entry in the VLSNIndex.  The check below that there are
         * active syncups underway doesn't guarantee that the syncup will
         * remain in effect during the whole call, but is a good indication
         * that we can depend on the global CBVLSN having an entry in the
         * VLSNIndex, as needed later in this method.
         */
        if (globalCBVLSN.getActiveSyncups() <= 0) {
            throw new IllegalStateException(
            "getCleanerBarrierFile should only be called during" +
            " an active syncup");
        }

        /*
         * Take the minimum of GlobalCBVLSN, replayCostMinVLSN, and
         * SyncCleanerBarrier
         */
        final VLSN globalCBVLSNValue = globalCBVLSN.getCBVLSN();
        final VLSN cbvlsn = VLSN.min(globalCBVLSNValue, replayCostMinVLSN);

        if (logger.isLoggable(Level.FINE)) {
            LoggerUtils.fine(
                logger, repImpl,
                "Computing getCleanerBarrierFile:" +
                " GlobalCBVLSN=" + globalCBVLSNValue +
                " replayCostMinVLSN=" + replayCostMinVLSN +
                " CBVLSN=" + cbvlsn);
        }

        /* The GlobalCBVLSN can be null, putting it outside the VLSN index */
        if (cbvlsn.isNull()) {
            return 0;
        }
        return repImpl.getVLSNIndex().getLTEFileNumber(cbvlsn);
    }

    /**
     * Protects additional files from deletion by returning a subset of the
     * specified unprotected files.
     *
     * <p>The unprotectedFiles parameter provides a collection of files that
     * the caller believes are safe to delete.  These files have had their live
     * data migrated by the cleaner, and they are not in use by DbBackup or
     * DataSync.  This method decides which of the unprotected files should be
     * removed from the return value to protect them from deletion so that they
     * can be used to stream replication data to replay at replicas.
     *
     * <p>The diagram below represents log files organized by the time they
     * were created, with the oldest file at the left and the newest at the
     * right.
     *
     * <pre>
     *
     * <-- Older                                             Newer -->
     *
     * All files:
     *
     *   Region 1  |        Region 2         |  Region 3  |  Region 4
     * ------------A------------X------------B------------C------------
     *             |            |            |            |
     *             Start of     CBVLSN       Global       Last
     *             VLSN Index   |            CBVLSN       VLSN
     *                          |                         |
     * From unprotected files:  |                         |
     *                          |                         |
     *     Truncated files      |     Retained files      |
     *
     * </pre>
     *
     * <p>Note that the unprotected files represent a subset of the files in
     * each region of the diagram.
     *
     * <p>Files in region 1 are files that are not represented in the VLSN
     * Index.  The mappings between LSNs and VLSNs maintained by the VLSN Index
     * are needed to support replays, so these files cannot be used for
     * replication.
     *
     * <p>Region 1 files consist primarily of live files containing data needed
     * for recovery, typically with a high enough percentage of live data that
     * the cleaner does not consider them worth cleaning.  There are usually
     * gaps in the sequence of files in this region because some of these older
     * files have been removed after being cleaned.
     *
     * <p>Note that entries are removed from the VLSN Index before the
     * associated log file files are deleted.  If log files are found to be in
     * use by DbBackup, or the system crashes before the files can be deleted,
     * it is possible for cleaned log files to appear in region 1 that do not
     * have associated mappings in the VLSN index.  That is the reason why it
     * is possible for this region to contain some unprotected files.
     *
     * <p>Files in region 2 are files that are represented in the VLSN index
     * and so can be used for replication.  Because these files only contain
     * VLSNs before the Global CBVLSN, they are not known to be needed to
     * support replication for electable replicas that have been in contact
     * with the master within the time period represented by the
     * REP_STREAM_TIMEOUT parameter, or by any replica currently performing
     * replication, but they might still be useful to secondary nodes that are
     * out of contact or electable nodes that have been out of contact for
     * longer than REP_STREAM_TIMEOUT.
     *
     * <p>The system uses the values of the REPLAY_COST_PERCENT and
     * REPLAY_FREE_DISK_PERCENT parameters to determine if it is worthwhile to
     * retain cleaned files in region 2, while also satisfying the requested
     * free disk space target.  The system needs to retain a contiguous subset
     * of the files at the upper end of this range in order for them to be
     * useful for replication, since replication requires an uninterrupted
     * sequence of VLSNs.  The system will select a VLSN between points A and B
     * as point X, which represents the lowest VLSN for files that should be
     * protected.
     *
     * <p>Files in region 3 are files that are known to be needed for
     * replication by electable nodes, so otherwise unprotected files in this
     * range are retained for that reason.
     *
     * <p>Files in region 4 are files that do not contain any VLSNs.  These
     * files include log entries to represent changes to the structure of the
     * Btree, but that are not needed for replication.  Cleaned files in this
     * region are candidates for deletion because they are not needed for
     * either replication or network restore.
     *
     * <p>The unprotected files passed to the getUnprotectedFileSet consist of
     * cleaned files that appear in the four regions described above.
     *
     * <p>The unprotected files in region 1 will always be removed, and are
     * called "truncated files".
     *
     * <p>Unprotected files in region 2 below point X, the final CBVLSN value,
     * will be removed, and are also called "truncated files".
     *
     * <p>Unprotected files in region 3 will be retained because they are known
     * to be needed for replication.
     *
     * <p>Unprotected files in region 4 will always be removed because they are
     * not needed for replication or replay.
     *
     * Unprotected Barren files (files that do not contain any VLSNs and have
     * been cleaned) are always removed regardless of the region they occupy.
     *
     * <p>
     * Note that the Global CBVLSN increases over time, which moves files from
     * region 3 into region 2, where they can be evaluated for retention based
     * on replay cost. The minimum VLSN of the files protected by replay cost
     * also increases over time, allowing the low end of the VLSN Index to be
     * truncated.
     *
     * @param unprotectedFiles set of file numbers of the files that are
     * potential candidates for deletion, before taking into consideration the
     * sync-up needs of other nodes in the replication group
     *
     * @param cleaner the cleaner, to provide the implementation needs
     * additional information about log files
     *
     * @return a subset of the original set, where files that we want to
     * protect have been removed.  We never destructively modify the input set.
     * Instead we return either a restricted view of the original set or a new
     * set with copies of some of the file numbers from the original set.
     */
    public NavigableSet<Long> getUnprotectedFileSet(
        final NavigableSet<Long> unprotectedFiles,
        final Cleaner cleaner) {

        final GlobalCBVLSN.CBVLSNInfo globalCBVLSNInfo =
            globalCBVLSN.getCBVLSNInfo();
        final VLSN globalCBVLSNValue = globalCBVLSNInfo.cbvlsn;
        final long globalCBVLSNFile = globalCBVLSNValue.isNull() ?
            0 :
            getVLSNIndex().getLTEFileNumber(globalCBVLSNValue);
        final VLSN lastVLSN = getVLSNIndex().getRange().getLast();
        final long lastVLSNFile = getVLSNIndex().getGTEFileNumber(lastVLSN);

        LoggerUtils.info(logger, envImpl,"Candidates for deletion:" +
            FormatUtil.asString(unprotectedFiles));

        /*
         * Compute the minimum VLSN associated with replay cost based on
         * REPLAY_COST_PERCENT and REPLAY_FREE_DISK_PERCENT
         */
        replayCostMinVLSN = computeReplayCostMinVLSN(
            unprotectedFiles, cleaner, globalCBVLSNFile, lastVLSNFile);

        /*
         * Compute the new CBVLSN as the minimum of globalCBVLSN and
         * replayCostMinVLSN
         */
        VLSN cbvlsn;
        long cbvlsnFile;
        String cbvlsnMessage;
        if (!replayCostMinVLSN.isNull() &&
            (replayCostMinVLSN.compareTo(globalCBVLSNValue) < 0)) {
            cbvlsn = replayCostMinVLSN;
            cbvlsnFile = getVLSNIndex().getLTEFileNumber(replayCostMinVLSN);
            cbvlsnMessage = "to support replication using free space" +
                " available beyond the " + replayFreeDiskPercent + "%" +
                " free space requirement specified by the" +
                " ReplicationConfig.REPLAY_FREE_DISK_PERCENT parameters";

        } else {
            cbvlsn = globalCBVLSNValue;
            cbvlsnFile = globalCBVLSNFile;
            cbvlsnMessage = globalCBVLSNInfo.message;
        }

        final FeederManager.MinFeederVLSNInfo minFeederVLSNInfo =
            feederManager.getMinFeederVLSN();
        final VLSN minFeederVLSN = minFeederVLSNInfo.vlsn;
        if (!minFeederVLSN.isNull() && (minFeederVLSN.compareTo(cbvlsn) < 0)) {

            /*
             * We are currently feeding some replica with a VLSN < GCBVLSN
             * Protect the files temporarily. Since getMinFeederVLSN does not
             * guarantee a monotonically decreasing value, the protection is not
             * foolproof.
             */
            cbvlsn = minFeederVLSN;
            cbvlsnFile = getVLSNIndex().getLTEFileNumber(minFeederVLSN);
            cbvlsnMessage = "to support active replication by node " +
                minFeederVLSNInfo.nodeName;
        }

        /*
         * The files we want to preserve range from the one containing the
         * CBVLSN, to the one containing the last VLSN.  So there could be up
         * to three disjoint subsets of the unprotected files: (1) those before
         * the CBVLSN (the "truncated files"), (2) those we want to preserve
         * (the "retained files"), and (3) those after the last VLSN.  Since we
         * need the retained files in (2), the
         * unprotected files we return from this method consist of the files in
         * (1) and (3).
         */
        final NavigableSet<Long> truncatedFiles =
            unprotectedFiles.headSet(cbvlsnFile, false);

        /*
         * enumerateFiles() can be expensive, so only generate this String
         * if logging is set to FINER
         */
        if (logger.isLoggable(Level.FINER)) {
            if (!truncatedFiles.isEmpty()) {
                logger.finer("Known unused files before CBVLSN start: " +
                              FormatUtil.asString(truncatedFiles));
            }
        }

        NavigableSet<Long> result = truncatedFiles;
        /* Now determine if there are barren files to be added to this set. */
        final FileSelector fileSelector = cleaner.getFileSelector();
        for (final long file : unprotectedFiles) {
            final VLSN fileFirstVLSN = fileSelector.getFirstVLSN(file);

            if ((fileFirstVLSN == null) || !fileFirstVLSN.isNull()) {
                /* No information or file has VLSNs */
                continue;
            }
            /* No VLSNs in this file since firstVLSN is null */
            if (result == truncatedFiles) {
                /* Create a copy if we are about to modify it. */
                result = new TreeSet<Long>(truncatedFiles);
            }
            /* Expand the result set with all barren files. */
            result.add(file);
            LoggerUtils.info(logger, envImpl, "File " + file  +
                             " has no VLSNs and can be deleted");
        }

        if (unprotectedFiles.size() > result.size()) {
            LoggerUtils.traceAndLog(
                logger, repImpl, Level.INFO,
                "Replication prevents deletion of " +
                (unprotectedFiles.size() - result.size()) +
                " files by the Cleaner " + cbvlsnMessage + ". " +
                "Start file=0x" + Long.toHexString(cbvlsnFile) +
                " holds CBVLSN " + cbvlsn +
                ", end file=0x" + Long.toHexString(lastVLSNFile) +
                " holds last VLSN " + lastVLSN);
        }

        LoggerUtils.info(logger, envImpl, "Files chosen for deletion by HA:" +
                         FormatUtil.asString(result));
        return result;
    }

    /**
     * Returns the minimum VLSN of files that should be retained to support
     * replay, or NULL_VLSN if disabled or no files are found to protect.
     */
    private VLSN computeReplayCostMinVLSN(
        final NavigableSet<Long> unprotectedFiles,
        final Cleaner cleaner,
        final long globalCBVLSNFile,
        final long lastVLSNFile) {

        /* Check if disabled */
        if (replayCostPercent == 0) {
            if (logger.isLoggable(Level.FINE)) {
                LoggerUtils.fine(logger, repImpl,
                                 "replayCostPercent is disabled");
            }
            return VLSN.NULL_VLSN;
        }

        /*
         * Use FileSummary information to determine log file sizes.  Specify
         * false for the includeTrackedFiles parameter to say that no
         * information is needed about changes made since the last checkpoint
         * because this method is called immediately after a checkpoint.
         */
        final Map<Long, FileSummary> fileSummaryMap =
            cleaner.getUtilizationProfile().getFileSummaryMap(false);

        /* Prepare to check free disk space, if enabled */
        SpaceInfo spaceInfo = (replayFreeDiskPercent != 0) ?
            new SpaceInfo(fileSummaryMap, unprotectedFiles, globalCBVLSNFile,
                          lastVLSNFile) :
            null;

        /*
         * Compute the number of bytes needed for a network restore, including
         * files that are already protected, plus any files containing VLSNs
         * between the GlobalCBVLSN and the last VLSN, because they are slated
         * to be protected.
         */
        long networkRestoreBytes = 0;
        for (final Entry<Long, FileSummary> ent : fileSummaryMap.entrySet()) {
            final long file = ent.getKey();
            if (!unprotectedFiles.contains(file) ||
                ((file >= globalCBVLSNFile) && (file <= lastVLSNFile))) {
                final FileSummary fileSummary = ent.getValue();
                networkRestoreBytes += fileSummary.totalSize;
            }
        }

        /*
         * Compute the maximum number of replay bytes as a percentage of the
         * network restore bytes
         */
        final long maxReplayBytes =
            (long) (networkRestoreBytes / (replayCostPercent / 100.0));

        /*
         * Iterate backwards over the files, from newest to oldest, counting
         * the size in bytes until it reaches the maximum number of replay
         * bytes worth retaining, and so long as we have enough free space.
         * Don't count barren files, since they are not used for replay and
         * will be deleted, or files below the VLSNIndex, since those can't be
         * used for replay.
         */
        final VLSN firstVLSN = getVLSNIndex().getRange().getFirst();
        final long firstVLSNFile = getVLSNIndex().getLTEFileNumber(firstVLSN);
        final FileSelector fileSelector = cleaner.getFileSelector();
        long replayBytes = 0;
        VLSN newReplayCostMinVLSN = VLSN.NULL_VLSN;
        for (final long file : unprotectedFiles.descendingSet()) {

            /* Ignore trailing files */
            if (file > lastVLSNFile) {
                continue;
            }

            /* Done if we pass the start of the VLSN Index */
            if (file < firstVLSNFile) {
                break;
            }

            /* Ignore deleted and barren files */
            final VLSN fileFirstVLSN = fileSelector.getFirstVLSN(file);
            if ((fileFirstVLSN == null) || fileFirstVLSN.isNull()) {
                continue;
            }

            /* Check free disk space */
            final long fileSize = fileSummaryMap.get(file).totalSize;
            if (spaceInfo != null) {
                final FileStoreSpaceInfo fileInfo =
                    spaceInfo.getFileInfo(file);
                if (fileInfo != null) {
                    if (fileSize > fileInfo.replaySpace) {
                        if (logger.isLoggable(Level.INFO)) {
                            LoggerUtils.info(
                                logger, envImpl,
                                String.format(
                                    "Limited free disk space prevented" +
                                    " retaining some log files." +
                                    " Retained %,d bytes, but wanted to" +
                                    " retain %,d bytes based on replay cost." +
                                    " Associated file store: %s",
                                    replayBytes, maxReplayBytes, fileInfo));
                        }
                        break;
                    }
                    fileInfo.replaySpace -= fileSize;
                }
            }
            newReplayCostMinVLSN = fileFirstVLSN;
            replayBytes += fileSize;

            /* Check if we've reached the maximum of useful replay bytes */
            if (replayBytes >= maxReplayBytes) {
                break;
            }
        }

        if (logger.isLoggable(Level.FINE)) {
            LoggerUtils.fine(
                logger, repImpl,
                String.format("Computing replayCostMinVLSN:" +
                              " networkRestoreBytes=%,d" +
                              " maxReplayBytes=%,d" +
                              " replayBytes=%,d" +
                              " firstVLSN=%s" +
                              " replayCostMinVLSN=%s",
                              networkRestoreBytes, maxReplayBytes, replayBytes,
                              firstVLSN, newReplayCostMinVLSN));
        }
        return newReplayCostMinVLSN;
    }

    /** Holds space information for a file store. */
    private class FileStoreSpaceInfo {
        private final FileStoreInfo fileStoreInfo;
        final long totalSpace;
        final long freeSpace;
        long replaySpace;

        FileStoreSpaceInfo(final FileStoreInfo fileStoreInfo)
            throws IOException {

            this.fileStoreInfo = fileStoreInfo;
            totalSpace = fileStoreInfo.getTotalSpace();
            freeSpace = fileStoreInfo.getUsableSpace();
            replaySpace = freeSpace - getTargetFreeSpace();
        }

        /**
         * Returns the target free disk space for the associate file store.
         */
        private long getTargetFreeSpace() {
            return (long) (totalSpace * (replayFreeDiskPercent / 100.0));
        }

        @Override
        public String toString() {
            return String.format("%s: totalSpace=%,d freeSpace=%,d" +
                                 " targetFreeSpace=%,d",
                                 fileStoreInfo, totalSpace, freeSpace,
                                 getTargetFreeSpace());
        }
    }

    /**
     * Maintain information about disk space available for log files used for
     * replay, so we can decide whether we have enough free space to retain
     * them.  Information is looked up by file, but the return value expresses
     * the free space available on the file store (meaning volume or disk)
     * associated with the file.
     */
    private class SpaceInfo {

        /**
         * Maps a file store to an object storing space information for log
         * files in that file store.
         */
        private final Map<FileStoreInfo, FileStoreSpaceInfo> fileStoreInfoMap =
            new HashMap<FileStoreInfo, FileStoreSpaceInfo>();

        /**
         * Maps a file number to the object that stores space information for
         * all files in the same file store.  The value is shared for all
         * entries in this map associated with the same file store, and is
         * shared with the associated value in fileStoreInfoMap.
         */
        private final Map<Long, FileStoreSpaceInfo> fileMap =
            new HashMap<Long, FileStoreSpaceInfo>();

        /** The number of IOExceptions logged. */
        private int loggedIOExceptions;

        SpaceInfo(final Map<Long, FileSummary> fileSummaryMap,
                  final NavigableSet<Long> unprotectedFiles,
                  final long globalCBVLSNFile,
                  final long lastVLSNFile) {

            /*
             * Tally information for all currently unprotected files, including
             * barren files, but not files containing VLSNs between the global
             * CBVLSN and the last VLSN, because they will be protected
             */
            for (final long file : unprotectedFiles.descendingSet()) {
                if ((file > globalCBVLSNFile) && (file <= lastVLSNFile)) {
                    continue;
                }
                final FileSummary fileSummary = fileSummaryMap.get(file);
                if (fileSummary != null) {
                    tallyFile(file, fileSummary);
                }
            }
        }

        /**
         * Returns the disk space information for the file store associated
         * with the specified file.
         */
        FileStoreSpaceInfo getFileInfo(final long file) {
            return fileMap.get(file);
        }

        /**
         * Create an entry for the file store associated with the file, if not
         * already present, and tally the space used by the individual file.
         */
        private void tallyFile(final long file,
                               final FileSummary fileSummary) {

            final String fileName = repImpl.getFileManager().getFullFileName(
                file, FileManager.JE_SUFFIX);
            try {
                final FileStoreInfo fileStoreInfo =
                    FileStoreInfo.getInfo(fileName);
                FileStoreSpaceInfo fileInfo =
                    fileStoreInfoMap.get(fileStoreInfo);
                if (fileInfo == null) {

                    /*
                     * Set the initial value to the free space available beyond
                     * the target amount.  We will then add to this value the
                     * amount of space used by replay log files located in this
                     * file store.  If the initial value is negative, then the
                     * result will be the amount of space we have available to
                     * store log files and still maintain the requested free
                     * space.  The result may be negative, meaning we can't
                     * meet the requirement even if all these replay files are
                     * deleted.
                     */
                    fileInfo = new FileStoreSpaceInfo(fileStoreInfo);
                    fileStoreInfoMap.put(fileStoreInfo, fileInfo);
                    if (logger.isLoggable(Level.FINE)) {
                        LoggerUtils.fine(
                            logger, repImpl,
                            "Space information for file store " + fileInfo);
                    }
                }
                fileMap.put(file, fileInfo);
                fileInfo.replaySpace += fileSummary.totalSize;
            } catch (IOException e) {

                /*
                 * Problem getting file store information.  Leave this file out
                 * of the summary, which means that it will be retained because
                 * we can't figure out which file store it is part of.
                 *
                 * Only log the first exception at INFO, to reduce clutter.
                 */
                final Level level =
                    (loggedIOExceptions == 0) ? Level.INFO : Level.FINE;
                if (logger.isLoggable(level)) {
                    loggedIOExceptions++;
                    LoggerUtils.logMsg(
                        logger, repImpl, level,
                        "Problem accessing file store info for file " +
                        fileName + ": " + e);
                }
            }
        }
    }

    long getReplicaCloseCatchupMs() {
        return replicaCloseCatchupMs;
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    /**
     * Shuts down the Network backup service *before* a rollback is initiated
     * as part of syncup, thus ensuring that NetworkRestore does not see an
     * inconsistent set of log files. Any network backup operations that are in
     * progress at this node are aborted. The client of the service will
     * experience network connection failures and will retry with this node
     * (when the service is re-established at this node), or with some other
     * node.
     * <p>
     * restarNetworkBackup() is then used to restart the service after it was
     * shut down.
     */
    final public void shutdownNetworkBackup() {
        logFeederManager.shutdown();
        logFeederManager = null;
    }

    /**
     * Restarts the network backup service *after* a rollback has been
     * completed and the log files are once again in a consistent state.
     */
    final public void restartNetworkBackup() {
        if (logFeederManager != null) {
            throw EnvironmentFailureException.unexpectedState(repImpl);
        }
        logFeederManager =
            new com.sleepycat.je.rep.impl.networkRestore.FeederManager
            (serviceDispatcher, repImpl, nameIdPair);
    }

    /**
     * Dumps the states associated with any active Feeders as well as
     * information pertaining to the group CBVLSN and the composition of the
     * group itself.
     */
    public String dumpState() {
        return  "\n" + feederManager.dumpState(false /* acksOnly */) +
            "\nGlobalCBVLSN=" + getGroupCBVLSN() +
            "\n" + getGroup();
    }

    /**
     * Dumps the state associated with all active Feeders that supply
     * acknowledgments.
     */
    public String dumpAckFeederState() {
        return  "\n" + feederManager.dumpState(true /* acksOnly */) + "\n";
    }

    public ElectionQuorum getElectionQuorum() {
        return electionQuorum;
    }

    public DurabilityQuorum getDurabilityQuorum() {
        return durabilityQuorum;
    }

    public void setConvertHook(TestHook<Integer> hook) {
        if (convertHooks == null) {
            convertHooks = new HashSet<TestHook<Integer>>();
        }
        convertHooks.add(hook);
    }

    private boolean runConvertHooks () {
        if (convertHooks == null) {
            return true;
        }

        for (TestHook<Integer> h : convertHooks) {
            assert TestHookExecute.doHookIfSet(h, 0);
        }
        return true;
    }

    /**
     * Get the group minimum JE version.
     *
     * <p>Returns the minimum JE version that is required for all nodes that
     * join this node's replication group.  The version returned is supported
     * by all current and future group members.  The minimum JE version is
     * guaranteed to only increase over time, so long as the data for the
     * environment is not rolled back or lost.
     *
     * @return the group minimum JE version
     */
    public JEVersion getMinJEVersion() {
        synchronized (minJEVersionLock) {
            return group.getMinJEVersion();
        }
    }

    /**
     * Checks if all data nodes in the replication group support the specified
     * JE version.  Updates the group minimum JE version, and the group format
     * version, as needed to require all nodes joining the group to be running
     * at least the specified JE version.
     *
     * <p>This method should only be called on the master, because attempts to
     * update the rep group DB on an replica will fail.
     *
     * @param newMinJEVersion the new minimum JE version
     * @throws DatabaseException if an error occurs when accessing the
     * replication group database
     * @throws MinJEVersionUnsupportedException if the version is not supported
     * by one or more current group members
     */
    public void setMinJEVersion(final JEVersion newMinJEVersion)
        throws MinJEVersionUnsupportedException {

        /*
         * Synchronize here on minJEVersionLock to prevent new secondary nodes
         * from being added while updating the minimum JE version.  Electable
         * nodes are stored in the RepGroupDB, so the check performed on that
         * class's setMinJEVersion within a transaction insures that all
         * current nodes have been checked before the minimum JE version is
         * increased.  But secondary nodes are not stored persistently, so
         * other synchronization is needed for them.
         */
        synchronized (minJEVersionLock) {

            /* Check if at least this version is already required */
            final JEVersion groupMinJEVersion = group.getMinJEVersion();
            if (groupMinJEVersion.compareTo(newMinJEVersion) >= 0) {
                return;
            }

            for (final RepNodeImpl node : group.getDataMembers()) {
                JEVersion nodeJEVersion = node.getJEVersion();
                if (getNodeName().equals(node.getName())) {

                    /* Use the current software version for the local node */
                    nodeJEVersion = repImpl.getCurrentJEVersion();
                } else {

                    /* Use the version recorded by the feeder for replicas */
                    final Feeder feeder =
                        feederManager.getFeeder(node.getName());
                    if (feeder != null) {
                        final JEVersion currentReplicaJEVersion =
                            feeder.getReplicaJEVersion();
                        if (currentReplicaJEVersion != null) {
                            nodeJEVersion = currentReplicaJEVersion;
                        }
                    }
                }
                if ((nodeJEVersion == null) ||
                    (newMinJEVersion.compareTo(nodeJEVersion) > 0)) {
                    throw new MinJEVersionUnsupportedException(
                        newMinJEVersion, node.getName(), nodeJEVersion);
                }
            }
            repGroupDB.setMinJEVersion(newMinJEVersion);
        }
    }

    /**
     * Adds a secondary node to the group.  Assign a node ID and add the node
     * to the RepGroupImpl.  Don't notify the monitor: secondary nodes do not
     * generate GroupChangeEvents.
     *
     * @param node the node
     * @throws IllegalStateException if the store does not currently support
     *         secondary nodes or the node doesn't meet the current minimum JE
     *         version
     * @throws NodeConflictException if the node conflicts with an existing
     *         persistent node
     */
    public void addSecondaryNode(final RepNodeImpl node) {
        if (!node.getType().isSecondary()) {
            throw new IllegalArgumentException(
                "Attempt to call addSecondaryNode with a" +
                " non-SECONDARY node: " + node);
        }
        final JEVersion requiredJEVersion =
            RepGroupImpl.FORMAT_VERSION_3_JE_VERSION;
        try {
            setMinJEVersion(requiredJEVersion);
        } catch (MinJEVersionUnsupportedException e) {
            if (e.nodeVersion == null) {
                throw new IllegalStateException(
                    "Secondary nodes are not currently supported." +
                    " The version running on node " + e.nodeName +
                    " could not be determined," +
                    " but this feature requires version " +
                    requiredJEVersion.getNumericVersionString() +
                    " or later.");
            }
            throw new IllegalStateException(
                "Secondary nodes are not currently supported." +
                " Node " + e.nodeName + " is running version " +
                e.nodeVersion.getNumericVersionString() +
                ", but this feature requires version " +
                requiredJEVersion.getNumericVersionString() +
                " or later.");
        }

        /*
         * Synchronize on minJEVersionLock to coordinate with setMinJEVersion
         */
        synchronized (minJEVersionLock) {
            final JEVersion minJEVersion = group.getMinJEVersion();
            if (node.getJEVersion().compareTo(minJEVersion) < 0) {
                throw new IllegalStateException(
                    "The node does not meet the minimum required version" +
                    " for the group." +
                    " Node " + node.getNameIdPair().getName() +
                    " is running version " + node.getJEVersion() +
                    ", but the minimum required version is " +
                    minJEVersion);
            }
            if (!node.getNameIdPair().hasNullId()) {
                throw new IllegalStateException(
                    "New secondary node " + node.getNameIdPair().getName() +
                    " already has an ID: " + node.getNameIdPair().getId());
            }
            node.getNameIdPair().setId(secondaryNodeIds.allocateId());
            group.addSecondaryNode(node);
        }
    }

    /**
     * Removes a secondary node from the group.  Remove the node from the
     * RepGroupImpl and deallocate the node ID.
     *
     * @param node the node
     */
    public void removeSecondaryNode(final RepNodeImpl node) {
        if (!node.getType().isSecondary()) {
            throw new IllegalArgumentException(
                "Attempt to call removeSecondaryNode with a" +
                " non-SECONDARY node: " + node);
        }
        group.removeSecondaryNode(node);
        secondaryNodeIds.deallocateId(node.getNodeId());
    }

    private class RepElectionsConfig implements ElectionsConfig {
        private final RepNode repNode;
        RepElectionsConfig(RepNode repNode) {
            this.repNode = repNode;
        }

        public String getGroupName() {
            return repNode.getRepImpl().getConfigManager().get(GROUP_NAME);
        }

        public NameIdPair getNameIdPair() {
            return repNode.getNameIdPair();
        }

        public ServiceDispatcher getServiceDispatcher() {
            return repNode.getServiceDispatcher();
        }

        public int getElectionPriority() {
            return repNode.getElectionPriority();
        }

        public int getLogVersion() {
            return repNode.getLogVersion();
        }

        public RepImpl getRepImpl() {
            return repNode.getRepImpl();
        }

        public RepNode getRepNode() {
            return repNode;
        }
    }

    /**
     * Track node IDs for secondary nodes.  IDs are allocated from the specified
     * number of values at the high end of the range of integers.
     */
    static class SecondaryNodeIds {
        private final int size;
        private final BitSet bits;

        /** Creates an instance that allocates the specified number of IDs. */
        SecondaryNodeIds(final int size) {
            this.size = size;
            assert size > 0;
            bits = new BitSet(size);
        }

        /**
         * Allocates a free ID, throwing IllegalStateException if none are
         * available.
         */
        synchronized int allocateId() {

            /*
             * Note that scanning for the next clear bit is somewhat
             * inefficient, but this inefficiency shouldn't matter given the
             * small number of secondary nodes expected.  If needed, the next
             * improvement would probably be to remember the last allocated ID,
             * to avoid repeated scans of an initial range of already allocated
             * bits.
             */
            final int pos = bits.nextClearBit(0);
            if (pos >= size) {
                throw new IllegalStateException("No more secondary node IDs");
            }
            bits.set(pos);
            return Integer.MAX_VALUE - pos;
        }

        /**
         * Deallocates a previously allocated ID, throwing
         * IllegalArgumentException if the argument was not allocated by
         * allocateId or if the ID is not currently allocated.
         */
        synchronized void deallocateId(final int id) {
            if (id < Integer.MAX_VALUE - size) {
                throw new IllegalArgumentException(
                    "Illegal secondary node ID: " + id);
            }
            final int pos = Integer.MAX_VALUE - id;
            if (!bits.get(pos)) {
                throw new IllegalArgumentException(
                    "Secondary node ID is not currently allocated: " + id);
            }
            bits.clear(pos);
        }
    }
}
