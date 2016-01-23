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
package com.sleepycat.je.rep.stream;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;
import java.util.logging.Logger;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.LockTimeoutException;
import com.sleepycat.je.log.LogEntryType;
import com.sleepycat.je.rep.InsufficientAcksException;
import com.sleepycat.je.rep.InsufficientReplicasException;
import com.sleepycat.je.rep.NodeType;
import com.sleepycat.je.rep.impl.RepGroupImpl;
import com.sleepycat.je.rep.impl.RepGroupImpl.NodeConflictException;
import com.sleepycat.je.rep.impl.RepNodeImpl;
import com.sleepycat.je.rep.impl.node.Feeder;
import com.sleepycat.je.rep.impl.node.Feeder.ExitException;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.stream.Protocol.JEVersions;
import com.sleepycat.je.rep.stream.Protocol.JEVersionsReject;
import com.sleepycat.je.rep.stream.Protocol.NodeGroupInfo;
import com.sleepycat.je.rep.stream.Protocol.ReplicaJEVersions;
import com.sleepycat.je.rep.stream.Protocol.ReplicaProtocolVersion;
import com.sleepycat.je.rep.stream.Protocol.SNTPRequest;
import com.sleepycat.je.rep.utilint.BinaryProtocol.Message;
import com.sleepycat.je.rep.utilint.BinaryProtocol.ProtocolException;
import com.sleepycat.je.rep.utilint.NamedChannel;
import com.sleepycat.je.utilint.LoggerUtils;
import com.sleepycat.je.utilint.TestHook;
import com.sleepycat.je.utilint.TestHookExecute;

/**
 * Implements the Feeder side of the handshake between the Feeder and the
 * Replica. The ReplicaFeederHandshake class takes care of the other side.
 *
 * @see <a href="https://sleepycat.oracle.com/pmwiki/pmwiki.php?n=JEHomePage.ReplicaFeederHandshake">FeederReplicaHandshake</a>
 */
public class FeederReplicaHandshake {
    /* The rep node (server or replica) */
    private final RepNode repNode;
    private final NamedChannel namedChannel;

    private final NameIdPair feederNameIdPair;

    /* Established during the first message. */
    private NameIdPair replicaNameIdPair = null;

    private volatile ReplicaJEVersions replicaJEVersions;

    /** The node associated with the replica, or null if not known. */
    private volatile RepNodeImpl replicaNode;

    private final Logger logger;

    /**
     * A test hook that is called before a message is written.  Note that the
     * hook is inherited by the ReplicaFeederHandshake, and will be kept in
     * place for the entire handshake.
     */
    private final TestHook<Message> writeMessageHook;

    /*
     * Used during testing: A non-zero value overrides the actual log
     * version.
     */
    private static int testCurrentLogVersion = 0;

    /**
     * An instance of this class is created with each new handshake preceding
     * the setting up of a connection.
     *
     * @param repNode the replication node
     * @param feeder the feeder instance
     * @param namedChannel the channel to be used for the handshake
     */
    public FeederReplicaHandshake(RepNode repNode,
                                  Feeder feeder,
                                  NamedChannel namedChannel) {
        this.repNode = repNode;
        this.namedChannel = namedChannel;
        feederNameIdPair = repNode.getNameIdPair();
        logger = LoggerUtils.getLogger(getClass());
        writeMessageHook = feeder.getWriteMessageHook();
    }

    /**
     * Returns the replica node ID. The returned value is only valid after
     * the handshake has been executed.
     *
     * @return the replica node name id pair
     */
    public NameIdPair getReplicaNameIdPair() {
        return replicaNameIdPair;
    }

    /**
     * Returns the current log version for the feeder, which is the highest log
     * version of any replicable log entry supplied by this feeder.  Uses
     * LogEntryType.LOG_VERSION_HIGHEST_REPLICABLE, not LOG_VERSION, since some
     * log versions may have only applied to non-replicable log entries, as was
     * the case for log version 10.
     */
    private int getCurrentLogVersion() {
        return (testCurrentLogVersion != 0) ?
            testCurrentLogVersion :
            LogEntryType.LOG_VERSION_HIGHEST_REPLICABLE;
    }

    static public void setTestLogVersion(int testLogVersion) {
        testCurrentLogVersion = testLogVersion;
    }

    /** Get the current JE version, supporting a test override. */
    private JEVersion getCurrentJEVersion() {
        return repNode.getRepImpl().getCurrentJEVersion();
    }

    /** Get the current protocol version, supporting a test override. */
    private int getCurrentProtocolVersion() {
        return Protocol.getJEVersionProtocolVersion(getCurrentJEVersion());
    }

    /**
     * Determines log compatibility. Returns null if they are compatible or the
     * server would like to defer the rejection to the replica. Return a
     * JEVersionsReject message if the server does not wish to communicate with
     * the replica.
     *
     * This check requires the log version of the replicas to be greater than,
     * equal to, or, if the current log version is {@value
     * LogEntryType#LOG_VERSION_REPLICATE_PREVIOUS} or greater, one less than
     * the log version on the feeder.  Allowing the replica to be one version
     * behind the feeder supports replication during upgrades where the feeder
     * is upgraded before the replica.  [#22336]
     *
     * This check also requires that the JE version of the replica is at least
     * the minJEVersion specified by the RepGroupImpl, if any.  This check
     * makes sure that nodes running an older software version cannot join the
     * group after a new and incompatible feature has been used.
     */
    private JEVersionsReject checkJECompatibility(final Protocol protocol,
                                                  final JEVersions jeVersions) {

        final int replicaLogVersion = jeVersions.getLogVersion();
        final int feederLogVersion = getCurrentLogVersion();
        final int minReplicaLogVersion;

        /*
         * TODO: Modify to check for a specific minimum version rather than one
         * less than the current version when format changes are next
         * introduced to replicable log entries.
         */
        if (feederLogVersion >= LogEntryType.LOG_VERSION_REPLICATE_PREVIOUS) {
            minReplicaLogVersion = feederLogVersion - 1;
        } else {
            minReplicaLogVersion = feederLogVersion;
        }
        if (replicaLogVersion < minReplicaLogVersion) {
            return protocol.new JEVersionsReject(
                "Incompatible log versions. " +
                "Feeder log version: " + feederLogVersion +
                ", Feeder JE version: " + getCurrentJEVersion() +
                ", Replica log version: " + replicaLogVersion +
                ", Replica JE version: " + jeVersions.getVersion());
        }

        final JEVersion minJEVersion = repNode.getGroup().getMinJEVersion();
        if (minJEVersion.compareTo(jeVersions.getVersion()) > 0) {
            return protocol.new JEVersionsReject(
                "Unsupported JE version. " +
                "Feeder JE version: " + getCurrentJEVersion() +
                ", Feeder min JE version: " + minJEVersion +
                ", Replica JE version: " + jeVersions.getVersion());
        }

        return null;
    }

    /**
     * Returns the log version supported by the replica, or {@code 0} if the
     * value is not yet known.  This method should only be called after the
     * {@link #execute} method has returned successfully.
     *
     * @return the replica's log version or {@code 0}
     */
    public int getReplicaLogVersion() {
        return (replicaJEVersions != null) ?
            replicaJEVersions.getLogVersion() :
            0;
    }

    /**
     * Returns the JE version supported by the replica, or {@code null} if the
     * value is not yet known.  This method should only be called after the
     * {@link #execute} method has returned successfully.
     *
     * @return the replica's JE version or {@code null}
     */
    public JEVersion getReplicaJEVersion() {
        return (replicaJEVersions != null) ?
            replicaJEVersions.getVersion() :
            null;
    }

    /**
     * Returns a RepNodeImpl that represents the replica for a successful
     * handshake.  This method should only be called after the {@link #execute}
     * method has returned successfully, and will throw IllegalStateException
     * otherwise.
     *
     * @return the replica node
     * @throws IllegalStateException if the handshake did not complete
     */
    public RepNodeImpl getReplicaNode() {
        if (replicaNode == null) {
            throw new IllegalStateException("Handshake did not complete");
        }
        return replicaNode;
    }

    /**
     * Executes the feeder side of the handshake.
     * @throws ProtocolException
     * @throws ExitException
     */
    public Protocol execute()
        throws DatabaseException,
               IOException,
               ProtocolException,
               ExitException {

        LoggerUtils.info(logger, repNode.getRepImpl(),
                         "Feeder-replica handshake start");

        /* First negotiate a compatible protocol */
        Protocol protocol = negotiateProtocol();

        /* Now exchange JE version information using the negotiated protocol */
        replicaJEVersions = (ReplicaJEVersions) protocol.read(namedChannel);
        LoggerUtils.fine(logger, repNode.getRepImpl(),
                         " Replica " + replicaNameIdPair.getName() +
                         " JE version: " +
                         replicaJEVersions.getVersion().getVersionString() +
                         " Log version: " +
                         replicaJEVersions.getLogVersion() +
                         " Protocol version: " + protocol.getVersion());
        JEVersionsReject reject =
            checkJECompatibility(protocol, replicaJEVersions);

        if (reject != null) {
            final String msg = "Version incompatibility: " +
                reject.getErrorMessage() +
                " with replica " + replicaNameIdPair.getName();
            LoggerUtils.warning(logger, repNode.getRepImpl(), msg);
            writeMessage(protocol, reject);
            throw new ExitException(msg);
        }

        /*
         * If the replica is compatible, then use its log version if it is
         * older than the feeder's.
         */
        final int feederLogVersion =
            Math.min(getCurrentLogVersion(), replicaJEVersions.getLogVersion());

        writeMessage(protocol,
                     protocol.new FeederJEVersions(
                         getCurrentJEVersion(), feederLogVersion));
        /* Verify replica membership info */
        verifyMembershipInfo(protocol);

        checkClockSkew(protocol);
        LoggerUtils.info
            (logger, repNode.getRepImpl(),
             "Feeder-replica " + replicaNameIdPair.getName() +
             " handshake completed.");

        return protocol;
    }

    /** Write a protocol message to the channel. */
    private void writeMessage(final Protocol protocol,
                              final Message message)
        throws IOException {

        assert TestHookExecute.doHookIfSet(writeMessageHook, message);
        protocol.write(message, namedChannel);
    }

    /**
     * Responds to message exchanges used to establish clock skew.
     * @throws ProtocolException
     */
    private void checkClockSkew(Protocol protocol)
        throws IOException,
               ProtocolException {
        SNTPRequest request;
        do {
            request = protocol.read(namedChannel.getChannel(),
                                    SNTPRequest.class);
            writeMessage(protocol, protocol.new SNTPResponse(request));
        } while (!request.isLast());
    }

    /**
     * Verifies that the group as configured here at the Feeder matches the
     * configuration of the replica.
     *
     * @param protocol the protocol to use for this verification
     *
     * @throws IOException
     * @throws DatabaseException
     */
    private void verifyMembershipInfo(Protocol protocol)
        throws IOException,
               DatabaseException,
               ExitException {

        NodeGroupInfo nodeGroup =
            (Protocol.NodeGroupInfo)(protocol.read(namedChannel));
        final RepGroupImpl group = repNode.getGroup();
        RepNodeImpl node = group.getNode(nodeGroup.getNodeName());

        try {

            if (nodeGroup.getNodeId() != replicaNameIdPair.getId()) {
                throw new ExitException
                    ("The replica node ID sent during protocol negotiation: " +
                     replicaNameIdPair +
                     " differs from the one sent in the MembershipInfo " +
                     "request: " + nodeGroup.getNodeId());
            }

            if (nodeGroup.getNodeType().isSecondary()) {

                /*
                 * Note the secondary node if this is a new node.  Otherwise,
                 * fall through, and the subsequent code will notice the
                 * incompatible node type.
                 */
                if (node == null) {

                    /* A new secondary node */
                    node = new RepNodeImpl(nodeGroup);
                    try {
                        repNode.addSecondaryNode(node);
                    } catch (IllegalStateException | NodeConflictException e) {
                        throw new ExitException(e, true);
                    }
                }
            } else if (node == null || !node.isQuorumAck()) {
                /* Not currently a confirmed member. */
                if (nodeGroup.getNodeType().isArbiter()) {
                    Set<RepNodeImpl> arbMembers = group.getArbiterMembers();
                    if (arbMembers.size() > 0) {
                        throw new ExitException(
                            "An Arbiter node already exists in the "+
                            "replication group.");
                    }
                }
                try {
                    repNode.getRepGroupDB().ensureMember(nodeGroup);
                    node =
                        repNode.getGroup().getMember(nodeGroup.getNodeName());
                    if (node == null) {
                        throw EnvironmentFailureException.unexpectedState
                            ("Node: " + nodeGroup.getNameIdPair() +
                             " not found");
                    }
                } catch (InsufficientReplicasException |
                         InsufficientAcksException |
                         LockTimeoutException e) {
                    throw new ExitException(e, false);
                } catch (NodeConflictException e) {
                    throw new ExitException(e, true);
                }
            } else if (node.isRemoved()) {
                throw new ExitException
                    ("Node: " + nodeGroup.getNameIdPair() +
                     " is no longer a member of the group." +
                     " It was explicitly removed.");
            }

            doGroupChecks(nodeGroup, group);
            doNodeChecks(nodeGroup, node);
            maybeUpdateJEVersion(nodeGroup, group, node);
        } catch (ExitException exception) {
            LoggerUtils.info
                (logger, repNode.getRepImpl(), exception.getMessage());
            if (exception.failReplica()) {
                /*
                 * Explicit message to force replica to invalidate the
                 * environment.
                 */
                writeMessage(protocol,
                             protocol.new NodeGroupInfoReject(
                             exception.getMessage()));
            }
            throw exception;
        }

        /* Id is now established for sure, update the pair. */
        replicaNameIdPair.update(node.getNameIdPair());
        namedChannel.setNameIdPair(replicaNameIdPair);
        LoggerUtils.fine(logger, repNode.getRepImpl(),
                         "Channel Mapping: " + replicaNameIdPair + " is at " +
                         namedChannel.getChannel());
        writeMessage(protocol,
                     protocol.new NodeGroupInfoOK(
                         group.getUUID(), replicaNameIdPair));
    }

    /**
     * Verifies that the group related information is consistent.
     *
     * @throws ExitException if the configuration in the group db differs
     * from the supplied config
     */
    private void doGroupChecks(NodeGroupInfo nodeGroup,
                               final RepGroupImpl group)
        throws ExitException {

        if (nodeGroup.isDesignatedPrimary() &&
                repNode.getRepImpl().isDesignatedPrimary()) {
            throw new ExitException
            ("Conflicting Primary designations. Feeder node: " +
             repNode.getNodeName() +
             " and replica node: " + nodeGroup.getNodeName() +
            " cannot simultaneously be designated primaries");
        }

        if (!nodeGroup.getGroupName().equals(group.getName())) {
            throw new ExitException
                ("The feeder belongs to the group: " +
                 group.getName() + " but replica id" + replicaNameIdPair +
                 " belongs to the group: " + nodeGroup.getGroupName());
        }

        if (!RepGroupImpl.isUnknownUUID(nodeGroup.getUUID()) &&
            !nodeGroup.getUUID().equals(group.getUUID())) {
            throw new ExitException
                ("The environments have the same name: " +
                 group.getName() +
                 " but represent different environment instances." +
                 " The environment at the master has UUID " +
                 group.getUUID() +
                 ", while the replica " + replicaNameIdPair.getName() +
                 " has UUID: " + nodeGroup.getUUID());
        }
    }

    /**
     * Verifies that the old and new node configurations are the same.
     *
     * @throws ExitException if the configuration in the group db differs
     * from the supplied config
     */
    private void doNodeChecks(NodeGroupInfo nodeGroup,
                              RepNodeImpl node)
        throws ExitException {

        if (!nodeGroup.getHostName().equals(node.getHostName())) {
            throw new ExitException
                ("Conflicting hostnames for replica id: " +
                 replicaNameIdPair +
                 " Feeder thinks it is: " + node.getHostName() +
                 " Replica is configured to use: " +
                 nodeGroup.getHostName());
        }

        if (nodeGroup.port() != node.getPort()) {
            throw new ExitException
                ("Conflicting ports for replica id: " + replicaNameIdPair +
                 " Feeder thinks it uses: " + node.getPort() +
                 " Replica is configured to use: " + nodeGroup.port());
        }

        if (!((NodeType.ELECTABLE == node.getType()) ||
              (NodeType.SECONDARY == node.getType()) ||
              (NodeType.ARBITER == node.getType()) ||
              (NodeType.MONITOR == node.getType()))) {
            throw new ExitException
                ("The replica node: " + replicaNameIdPair +
                 " is of type: " + node.getType());
        }

        if (!nodeGroup.getNodeType().equals(node.getType())) {
            throw new ExitException
                ("Conflicting node types for: " + replicaNameIdPair +
                 " Feeder thinks it uses: " + node.getType() +
                 " Replica is configured as type: " + nodeGroup.getNodeType());
        }
        replicaNode = node;
    }

    /**
     * Update the node's JE version from the provided group info if storing the
     * JE version is supported and the current version differs from the stored
     * one.  It's OK if the attempt does not fully succeed due to a lack of
     * replicas or acknowledgments: we can try again the next time.
     *
     * @throws ExitException if the attempt fails because the updated node's
     * socket address conflicts with another node
     */
    private void maybeUpdateJEVersion(final NodeGroupInfo nodeGroup,
                                      final RepGroupImpl group,
                                      final RepNodeImpl node)
        throws ExitException {

        if ((group.getFormatVersion() >= RepGroupImpl.FORMAT_VERSION_3) &&
            (nodeGroup.getJEVersion() != null) &&
            !nodeGroup.getJEVersion().equals(node.getJEVersion())) {

            /*
             * Try updating the JE version information, given that the group
             * format supports this.  Don't require a quorum of acknowledgments
             * since the fact that the handshake for this replica is underway
             * may mean that a quorum is not available.  Saving the JE version
             * is only an optimization, so it is OK if this attempt fails to be
             * persistent.
             */
            try {
                repNode.getRepGroupDB().updateMember(
                    new RepNodeImpl(nodeGroup), false);
            } catch (InsufficientReplicasException |
                     InsufficientAcksException |
                     LockTimeoutException e) {
                /* Ignored */
            } catch (NodeConflictException e) {
                throw new ExitException(e, true);
            }
        }
    }

    /**
     * Negotiates and returns the protocol that will be used in all subsequent
     * interactions with the Replica, if the replica accepts to it.
     *
     * @return the protocol instance to be used for subsequent interactions
     *
     * @throws IOException
     * @throws ExitException
     */
    private Protocol negotiateProtocol()
        throws IOException, ExitException {

        Protocol defaultProtocol =
            Protocol.getProtocol(repNode, getCurrentProtocolVersion());

        /*
         * Wait to receive the replica's version, decide which protocol version
         * to use ourselves, and then reply with our version.
         */
        ReplicaProtocolVersion message =
            (ReplicaProtocolVersion) defaultProtocol.read(namedChannel);

        replicaNameIdPair = message.getNameIdPair();

        Feeder dup =
            repNode.feederManager().getFeeder(replicaNameIdPair.getName());
        if ((dup != null) ||
            (message.getNameIdPair().getName().
                    equals(feederNameIdPair.getName()))) {
            /* Reject the connection. */
            writeMessage(defaultProtocol,
                         defaultProtocol.new DuplicateNodeReject(
                             "This node: " + replicaNameIdPair +
                             " is already in active use at the feeder "));
            SocketAddress dupAddress =
                namedChannel.getChannel().getSocketChannel().socket().
                getRemoteSocketAddress();

            throw new ExitException
                ("A replica with the id: " + replicaNameIdPair +
                 " is already active with this feeder. " +
                 " The duplicate replica resides at: " +
                 dupAddress);
        }

        /*
         * If the Replica's version is acceptable, use it, otherwise return the
         * default protocol at this node, in case the Replica can support it.
         */
        final int replicaVersion = message.getVersion();
        Protocol protocol =
            Protocol.get(repNode, replicaVersion, getCurrentProtocolVersion());
        if (protocol == null) {
            protocol = defaultProtocol;
        }
        defaultProtocol.write
             (defaultProtocol.new FeederProtocolVersion(protocol.getVersion()),
              namedChannel);
        return protocol;
    }
}
