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

package com.sleepycat.je.rep.impl;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.rep.MemberNotFoundException;
import com.sleepycat.je.rep.impl.RepGroupDB.NodeBinding;
import com.sleepycat.je.utilint.VLSN;

/**
 * Represents a snapshot of the Replication Group as a whole. Note that
 * membership associated with a group is dynamic and its constituents can
 * change at any time. It's useful to keep in mind that due to the distributed
 * nature of the Replication Group all the nodes in a replication group may not
 * have the same consistent picture of the replication group at a single point
 * in time, but will converge to become consistent eventually.
 */
public class RepGroupImpl {

    /** The latest supported format version. */
    public static final int MAX_FORMAT_VERSION = 3;

    /**
     * Format version introduced in JE 6.0.1 that records a node's most recent
     * JE version, and the minimum JE version required to join the group.
     */
    public static final int FORMAT_VERSION_3 = 3;

    /**
     * The latest format version that is compatible with JE 6.0.0 and earlier
     * versions.
     */
    public static final int FORMAT_VERSION_2 = 2;

    /** The initial format version for newly created RepGroupImpl instances. */
    public static final int INITIAL_FORMAT_VERSION = 3;

    /** The oldest supported format version. */
    static final int MIN_FORMAT_VERSION = 2;

    /** The first JE version that supports FORMAT_VERSION_3. */
    public static final JEVersion FORMAT_VERSION_3_JE_VERSION =
        new JEVersion("6.0.1");

    /**
     * The first JE version that supports the oldest supported format version.
     */
    public static final JEVersion MIN_FORMAT_VERSION_JE_VERSION =
        new JEVersion("5.0.0");

    /** The initial change version. */
    private final static int CHANGE_VERSION_START = 0;

    /*
     * The special UUID associated with a group, when the group UUID is unknown
     * because a node is still in the process of joining the group. This value
     * cannot be created by UUID.randomUUID
     */
    private final static UUID UNKNOWN_UUID = new UUID(0, 0);

    /**
     * The maximum number of secondary nodes that can join the group at the
     * same time time.  This number of secondary node IDs will be reserved at
     * the top of the node ID range.
     */
    public static final int MAX_SECONDARY_NODES = 1024;

    /** The first node ID for persistent nodes. */
    private static final int NODE_SEQUENCE_START = 0;

    /** The maximum node ID for persistent nodes. */
    private static final int NODE_SEQUENCE_MAX =
        Integer.MAX_VALUE - MAX_SECONDARY_NODES;

    /** Returns true if the node is electable. */
    private static final Predicate ELECTABLE_PREDICATE = new Predicate() {
        @Override
        boolean include(final RepNodeImpl n) {
            return n.getType().isElectable();
        }
    };

    /** Returns true if the node is a monitor. */
    private static final Predicate MONITOR_PREDICATE = new Predicate() {
        @Override
        boolean include(final RepNodeImpl n) {
            return n.getType().isMonitor();
        }
    };

    /** Returns true if the node is secondary. */
    private static final Predicate SECONDARY_PREDICATE = new Predicate() {
        @Override
        boolean include(final RepNodeImpl n) {
            return n.getType().isSecondary();
        }
    };

    /** Returns true if the node can return acks but is not an Arbiter. */
    private static final Predicate ACK_PREDICATE = new Predicate() {
        @Override
        boolean include(final RepNodeImpl n) {
            return n.getType().isElectable() && !n.getType().isArbiter();
        }
    };

    /** Returns true if the node is an arbiter. */
    private static final Predicate ARBITER_PREDICATE = new Predicate() {
        @Override
        boolean include(final RepNodeImpl n) {
            return n.getType().isArbiter();
        }
    };

    /* The name of the Replication Group. */
    private final String groupName;

    /*
     * The universally unique UUID associated with the replicated environment.
     */
    private UUID uuid;

    /*
     * The version number associated with this group's format in the database.
     */
    private volatile int formatVersion;

    /*
     * Tracks the change version level. It's updated with every change to the
     * member set in the membership database.
     */
    private int changeVersion = 0;

    /*
     * The most recently assigned node ID for persistent nodes.  Node IDs for
     * persistent nodes are never reused.
     */
    private int nodeIdSequence;

    /*
     * The following maps represent the set of nodes in the group indexed in
     * two different ways: by user-defined node name and by internal id. Note
     * that both maps contain nodes that are no longer members of the group.
     *
     * All access to nodesById and nodesByName should be synchronized on
     * nodesById, to avoid ConcurrentModificationException and to provide
     * consistent results for both maps.
     */

    /* All the nodes that form the replication group, indexed by Id. */
    private final Map<Integer, RepNodeImpl> nodesById =
        new HashMap<Integer, RepNodeImpl>();

    /*
     * All the nodes that form the replication group, indexed by node name.
     * This map is used exclusively for efficient lookups by name. The map
     * nodesById does all the heavy lifting.
     */
    private final Map<String, RepNodeImpl> nodesByName =
        new HashMap<String, RepNodeImpl>();

    /** The minimum JE version required for nodes to join the group. */
    private volatile JEVersion minJEVersion = MIN_FORMAT_VERSION_JE_VERSION;

    /**
     * Constructor to create a new empty repGroup, typically as part of
     * environment initialization.
     *
     * @param groupName the group name
     * @param currentJEVersion if non-null, override the current JE version,
     * for testing
     */
    public RepGroupImpl(String groupName, JEVersion currentJEVersion) {
        this(groupName, false, currentJEVersion);
    }

    /**
     * Constructor to create a group and specify if the group's UUID should be
     * unknown or generated randomly.
     */
    RepGroupImpl(String groupName,
                 boolean unknownUUID,
                 JEVersion currentJEVersion) {
        this(groupName,
             unknownUUID ? UNKNOWN_UUID : UUID.randomUUID(),
             getCurrentFormatVersion(currentJEVersion));
    }

    /** Get the current format version, supporting a test override. */
    private static int getCurrentFormatVersion(
        final JEVersion currentJEVersion) {

        return (currentJEVersion == null) ?
            MAX_FORMAT_VERSION :
            getMaxFormatVersion(currentJEVersion);
    }

    /**
     * Constructor to create a group and specify the group's UUID and format
     * version.
     */
    public RepGroupImpl(String groupName, UUID uuid, int formatVersion) {
        this(groupName,
             uuid,
             formatVersion,
             CHANGE_VERSION_START,
             NODE_SEQUENCE_START,
             ((formatVersion < FORMAT_VERSION_3) ?
              MIN_FORMAT_VERSION_JE_VERSION :
              FORMAT_VERSION_3_JE_VERSION));
    }

    /**
     * Constructor used to recreate an existing RepGroup, typically as part of
     * a deserialization operation.
     *
     * @param groupName
     * @param uuid
     * @param formatVersion
     * @param changeVersion
     * @param minJEVersion
     */
    public RepGroupImpl(String groupName,
                        UUID uuid,
                        int formatVersion,
                        int changeVersion,
                        int nodeIdSequence,
                        JEVersion minJEVersion) {
        this.groupName = groupName;
        this.uuid = uuid;
        this.formatVersion = formatVersion;
        this.changeVersion = changeVersion;
        setNodeIdSequence(nodeIdSequence);
        this.minJEVersion = minJEVersion;

        if (formatVersion < MIN_FORMAT_VERSION ||
            formatVersion > MAX_FORMAT_VERSION) {
            throw new IllegalStateException(
                "Expected membership database format version between: " +
                MIN_FORMAT_VERSION + " and " + MAX_FORMAT_VERSION +
                ", encountered unsupported version: " + formatVersion);
        }
        if (minJEVersion == null) {
            throw new IllegalArgumentException(
                "The minJEVersion must not be null");
        }
    }

    /*
     * Returns true if the UUID has not as yet been established at this node.
     * This is the case when a knew node first joins a group, and it has not
     * as yet replicated the group database via the replication stream.
     */
    public boolean hasUnknownUUID() {
        return UNKNOWN_UUID.equals(uuid);
    }

    /**
     * Predicate to help determine whether the UUID is the canonical unknown
     * UUID.
     */
    public static boolean isUnknownUUID(UUID uuid) {
        return UNKNOWN_UUID.equals(uuid);
    }

    /**
     * Sets the UUID. The UUID can only be set if it's currently unknown.
     */
    public void setUUID(UUID uuid) {
        if (!hasUnknownUUID()) {
            throw EnvironmentFailureException.unexpectedState
                ("Expected placeholder UUID, not " + uuid);
        }
        this.uuid = uuid;
    }

    /**
     * Removes a member transiently from the rep group by marking it as removed
     * and optionally deleting it from the by-name and by-ID maps. This action
     * is usually a precursor to making the change persistent on disk.
     *
     * @param nodeName identifies the node being removed
     *
     * @param delete whether to delete the node from the maps
     *
     * @return the node that was removed
     *
     * @throws EnvironmentFailureException if the node is not part of the group
     * or is a secondary node
     */
    public RepNodeImpl removeMember(final String nodeName,
                                    final boolean delete) {
        final RepNodeImpl node = getMember(nodeName);
        if (node == null) {
            throw EnvironmentFailureException.unexpectedState
                ("Node:" + nodeName + " is not a member of the group.");
        }
        if (node.getType().isSecondary()) {
            throw EnvironmentFailureException.unexpectedState(
                "Cannot remove secondary node: " + nodeName);
        }
        if (delete) {
            synchronized (nodesById) {
                nodesById.remove(node.getNodeId());
                nodesByName.remove(nodeName);
            }
        }
        node.setRemoved(true);
        return node;
    }

    /**
     * Checks for whether a new or changed node definition is in conflict with
     * other members of the group.  In particular, checks that the specified
     * node does not use the same socket address as another member.
     * <p>
     * This check must be done when adding a new member to the group, or
     * changing the network address of an existing member, and must be done
     * with the rep group entry in the database locked for write to prevent
     * race conditions.
     *
     * @param node the new node that is being checked for conflicts
     * @throws NodeConflictException if there is a conflict
     */
    public void checkForConflicts(RepNodeImpl node)
        throws DatabaseException, NodeConflictException {

        for (RepNodeImpl n : getAllMembers(null)) {
            if (n.getNameIdPair().equals(node.getNameIdPair())) {
                continue;
            }
            if (n.getSocketAddress().equals(node.getSocketAddress())) {
                throw new NodeConflictException
                    ("New or moved node:" + node.getName() +
                     ", is configured with the socket address: " +
                     node.getSocketAddress() +
                     ".  It conflicts with the socket already " +
                     "used by the member: " + n.getName());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + changeVersion;
        result = prime * result
                + ((groupName == null) ? 0 : groupName.hashCode());
        synchronized (nodesById) {
            result = prime * result + nodesById.hashCode();
        }
        /* Don't bother with nodesByName */
        result = prime * result
                + ((uuid == null) ? 0 : uuid.hashCode());
        result = prime * result + formatVersion;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RepGroupImpl)) {
            return false;
        }
        RepGroupImpl other = (RepGroupImpl) obj;
        if (changeVersion != other.changeVersion) {
            return false;
        }
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }
        /* Don't bother with nodesByName, since nodesById equality covers it */
        if (uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!uuid.equals(other.uuid)) {
            return false;
        }
        if (formatVersion != other.formatVersion) {
            return false;
        }
        if (!minJEVersion.equals(other.minJEVersion)) {
            return false;
        }

        /*
         * Do this last, since it is expensive because of its need to avoid
         * concurrency conflicts.
         */
        final Map<Integer, RepNodeImpl> otherNodesById;
        synchronized (other.nodesById) {
            otherNodesById =
                new HashMap<Integer, RepNodeImpl>(other.nodesById);
        }
        synchronized (nodesById) {
            if (!nodesById.equals(otherNodesById)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sets the nodes associated with the Rep group. Note that both nodesById
     * and nodesByIndex are initialized.
     */
    public void setNodes(final Map<Integer, RepNodeImpl> nodes) {

        synchronized (nodesById) {

            /* Remove non-secondary nodes */
            for (final Iterator<RepNodeImpl> iter =
                     nodesById.values().iterator();
                 iter.hasNext(); ) {
                final RepNodeImpl node = iter.next();
                if (!node.getType().isSecondary()) {
                    iter.remove();
                    nodesByName.remove(node.getName());
                }
            }

            /* Add specified nodes */
            if (nodes != null) {
                for (final RepNodeImpl node : nodes.values()) {
                    final RepNodeImpl prevById =
                        nodesById.put(node.getNodeId(), node);
                    final RepNodeImpl prevByName =
                        nodesByName.put(node.getName(), node);

                    /*
                     * Also remove entries for any previous nodes if the
                     * mapping between names and IDs was changed.
                     */
                    if ((prevById != null) &&
                        !node.getName().equals(prevById.getName())) {
                        nodesByName.remove(prevById.getName());
                    }
                    if ((prevByName != null) &&
                        node.getNodeId() != prevByName.getNodeId()) {
                        nodesById.remove(prevByName.getNodeId());
                    }
                }
            }

            assert new HashSet<RepNodeImpl>(nodesById.values()).equals(
                new HashSet<RepNodeImpl>(nodesByName.values()))
                : "Node maps indexed by ID and name differ: " +
                "IDs: " + nodesById + ", Names: " + nodesByName;
        }
    }

    /**
     * Add a secondary node.  The caller should have already have an assigned
     * the node an ID and checked that the replication group supports secondary
     * nodes.
     *
     * @param node the secondary node
     * @throws IllegalStateException if the store does not currently support
     *         secondary nodes
     * @throws NodeConflictException if the node conflicts with an existing
     *         persistent node
     */
    public void addSecondaryNode(final RepNodeImpl node) {
        if (!node.getType().isSecondary()) {
            throw new IllegalArgumentException(
                "Attempt to call addSecondaryNode on a non-SECONDARY node: " +
                node);
        }
        if (node.getNameIdPair().hasNullId()) {
            throw new IllegalArgumentException(
                "Attempt to call addSecondaryNode on node without ID: " +
                node);
        }

        synchronized (nodesById) {
            final RepNodeImpl prevById = nodesById.get(node.getNodeId());
            assert (prevById == null) || prevById.getType().isSecondary()
                : "Same node ID for secondary and non-secondary nodes: " +
                node + ", " + prevById;
            final RepNodeImpl prevByName = nodesByName.get(node.getName());
            if ((prevByName != null) && !prevByName.getType().isSecondary()) {
                throw new NodeConflictException(
                    "New secondary node " + node.getName() +
                    " conflicts with an existing non-secondary node with the" +
                    " same name: " + prevByName);
            }
            final RepNodeImpl prevById2 =
                nodesById.put(node.getNodeId(), node);
            assert prevById == prevById2;
            final RepNodeImpl prevByName2 =
                nodesByName.put(node.getName(), node);
            assert prevByName == prevByName2;
            if ((prevById != null) &&
                !node.getName().equals(prevById.getName())) {
                nodesByName.remove(prevById.getName());
            }
            if ((prevByName != null) &&
                (node.getNodeId() != prevByName.getNodeId())) {
                nodesById.remove(prevByName.getNodeId());
            }

            assert new HashSet<RepNodeImpl>(nodesById.values()).equals(
                new HashSet<RepNodeImpl>(nodesByName.values()))
                : "Node maps indexed by ID and name differ: " +
                "IDs: " + nodesById + ", Names: " + nodesByName;
        }
    }

    /**
     * Remove a secondary node, which should have an assigned ID
     *
     * @param node the secondary node
     */
    public void removeSecondaryNode(final RepNodeImpl node) {
        if (!node.getType().isSecondary()) {
            throw new IllegalArgumentException(
                "Attempt to call removeSecondaryNode on a" +
                " non-SECONDARY node: " + node);
        }
        if (node.getNameIdPair().hasNullId()) {
            throw new IllegalArgumentException(
                "Attempt to call removeSecondaryNode on a node with no ID: " +
                node);
        }
        synchronized (nodesById) {
            nodesById.remove(node.getNodeId());
            nodesByName.remove(node.getName());
        }
    }

    /**
     * returns the unique UUID associated with the replicated environment.
     *
     * @return the UUID
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns the version of the format (the schema) in use by this group
     * instance in the database.
     *
     * @return the format version
     */
    public int getFormatVersion() {
        return formatVersion;
    }

    /**
     * Returns the highest format version supported by the specified JE
     * version.
     *
     * @param jeVersion the JE version
     * @return the highest format version supported by that JE version
     */
    public static int getMaxFormatVersion(final JEVersion jeVersion) {
        if (jeVersion.compareTo(FORMAT_VERSION_3_JE_VERSION) < 0) {
            return FORMAT_VERSION_2;
        }
        return FORMAT_VERSION_3;
    }

    /**
     * Returns the version of the instance as represented by changes to the
     * members constituting the group.
     *
     * @return the object change version
     */
    public int getChangeVersion() {
        return changeVersion;
    }

    /**
     * Increments the object change version. It must be called with the group
     * entry locked in the group database.
     *
     * @return the incremented change version
     */
    public int incrementChangeVersion() {
        return ++changeVersion;
    }

    /**
     * Returns the current highest node ID currently in use by the group.
     *
     * @return the highest node ID in use
     */
    public int getNodeIdSequence() {
        return nodeIdSequence;
    }

    /**
     * Set the node id sequence. This is only done in unusual circumstances,
     * e.g. when a replication group is being reset in an existing replicated
     * environment and we want to ensure that the internal node ids are not
     * reused in the logs.
     */
    public void setNodeIdSequence(int nodeIdSequence) {
        if (nodeIdSequence < 0 || nodeIdSequence > NODE_SEQUENCE_MAX) {
            throw new IllegalArgumentException(
                "Bad nodeIdSequence: " + nodeIdSequence);
        }
        this.nodeIdSequence = nodeIdSequence;
    }

    /**
     * Increments the node ID sequence and returns it.
     *
     * @return the next node ID for use in a new node
     */
    public int getNextNodeId() {
        if (nodeIdSequence >= NODE_SEQUENCE_MAX) {
            throw new IllegalStateException("Reached maximum node ID");
        }
        return ++nodeIdSequence;
    }

    /**
     * Returns the node ID that is associated with the very first node in the
     * replication group.
     */
    public static int getFirstNodeId() {
        return NODE_SEQUENCE_START + 1;
    }

    /**
     * Returns the minimum JE version that a node must be running in order to
     * join the group.
     */
    public JEVersion getMinJEVersion() {
        return minJEVersion;
    }

    /**
     * Sets the minimum JE version that a node must be running in order to join
     * the replication group.  The group object should have had its nodes
     * fetched using the {@link RepGroupDB#fetchGroup} method and should be
     * stored to the group database after making this change.  Throws a {@link
     * MinJEVersionUnsupportedException} if the requested version is not
     * supported.  Updates the group format version as needed to match the JE
     * version.  Has no effect if the current minimum value is already as high
     * or higher than the requested one.
     *
     * @param newMinJEVersion the new minimum JE version
     * @throws MinJEVersionUnsupportedException if the requested version is not
     * supported by the group's electable nodes
     */
    public void setMinJEVersion(final JEVersion newMinJEVersion)
        throws MinJEVersionUnsupportedException {

        if (newMinJEVersion == null) {
            throw new IllegalArgumentException(
                "The newMinJEVersion argument must not be null");
        }
        if (newMinJEVersion.compareTo(minJEVersion) <= 0) {
            return;
        }
        final int newFormatVersion = getMaxFormatVersion(newMinJEVersion);

        /* Minimum JE version is not stored before format version 3 */
        if (newFormatVersion < FORMAT_VERSION_3) {
            return;
        }

        for (final RepNodeImpl node : getElectableMembers()) {
            final JEVersion nodeJEVersion = node.getJEVersion();
            if ((nodeJEVersion != null) &&
                nodeJEVersion.compareTo(newMinJEVersion) < 0) {
                throw new MinJEVersionUnsupportedException(
                    newMinJEVersion, node.getName(), nodeJEVersion);
            }
        }
        minJEVersion = newMinJEVersion;
        formatVersion = newFormatVersion;
    }

    /**
     * Used to ensure that the ReplicationGroup value is consistent after it
     * has been fetched via a readUncommitted access to the rep group database.
     * It does so by ensuring that the summarized values match the nodes that
     * were actually read.
     */
    public void makeConsistent() {
        synchronized (nodesById) {
            if (nodesById.isEmpty()) {
                return;
            }
            int computedNodeId = NODE_SEQUENCE_START-1;
            int computedChangeVersion = -1;
            for (RepNodeImpl mi : nodesById.values()) {
                /* Get the highest node ID */
                if (computedNodeId < mi.getNodeId()) {
                    computedNodeId = mi.getNodeId();
                }
                /* Get the highest change version. */
                if (computedChangeVersion < mi.getChangeVersion()) {
                    computedChangeVersion = mi.getChangeVersion();
                }
            }
            setNodeIdSequence(computedNodeId);
            changeVersion = computedChangeVersion;
        }
    }

    /*
     * Serialization
     */

    /**
     * Serializes an object by converting its TupleBinding byte based
     * representation into the hex characters denoting the bytes.
     *
     * @param <T> the type of the object being serialized
     * @param binding the tuble binding used to convert it into its byte form
     * @param object the object being serialized
     * @return the hex string containing the serialized hex form of the object
     */
    static <T> String objectToHex(TupleBinding<T> binding, T object) {
        StringBuilder buffer = new StringBuilder();
        TupleOutput tuple = new TupleOutput(new byte[100]);
        binding.objectToEntry(object, tuple);
        byte[] bytes = tuple.getBufferBytes();
        int size = tuple.getBufferLength();

        for (int i = 0; i < size; i++) {
            int lowNibble = (bytes[i] & 0xf);
            int highNibble = ((bytes[i]>>4) & 0xf);
            buffer.append(Character.forDigit(lowNibble, 16));
            buffer.append(Character.forDigit(highNibble, 16));
        }
        return buffer.toString();
    }

    /**
     * Returns a serialized character based form of the group suitable for use
     * in subclasses of SimpleProtocol. The serialized form is a multi-token
     * string. The first token represents the RepGroup object itself with each
     * subsequent node representing a node in the group. Tokens are separated
     * by '|', the protocol separator character. The number of tokens is thus
     * equal to the number of nodes in the group + 1. Each token is itself a
     * hex character based representation of the binding used to serialize a
     * RepGroup and store it into the database.
     *
     * @param groupFormatVersion the group format version
     * @return the string encoded as above
     */
    public String serializeHex(final int groupFormatVersion) {
        final RepGroupDB.GroupBinding groupBinding =
            new RepGroupDB.GroupBinding(groupFormatVersion);
        StringBuilder buffer = new StringBuilder();
        buffer.append(objectToHex(groupBinding, this));
        synchronized (nodesById) {
            for (RepNodeImpl mi : nodesById.values()) {

                /*
                 * Only include nodes that can be serialized with the specified
                 * format version
                 */
                if (NodeBinding.supportsObjectToEntry(
                        mi, groupFormatVersion)) {
                    buffer.append(TextProtocol.SEPARATOR);
                    buffer.append(serializeHex(mi, groupFormatVersion));
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Returns the serialized form of the node as a sequence of hex characters
     * suitable for use by the text based protocols.
     *
     * @param node the node to be serialized.
     * @param formatVersion the group format version
     * @return the string containing the serialized form of the node
     */
    public static String serializeHex(final RepNodeImpl node,
                                      final int formatVersion) {
        final NodeBinding nodeBinding = new NodeBinding(formatVersion);
        return objectToHex(nodeBinding, node);
    }

    /**
     * Serialize the node into its byte representation.
     *
     * @param node the node to be serialized
     * @param formatVersion the group format version
     * @return the serialized byte array
     */
    public static byte[] serializeBytes(final RepNodeImpl node,
                                        final int formatVersion) {

        final NodeBinding binding = new NodeBinding(formatVersion);
        final TupleOutput tuple =
            new TupleOutput(new byte[NodeBinding.APPROX_MAX_SIZE]);
        binding.objectToEntry(node, tuple);
        return tuple.getBufferBytes();
    }

    /**
     * Deserializes the object serialized by {@link #serializeHex}
     *
     * @param hex the string containing the serialized form of the node
     * @param formatVersion the group format version
     *
     * @return the de-serialized object
     */
    public static RepNodeImpl hexDeserializeNode(final String hex,
                                                 final int formatVersion) {
        final NodeBinding nodeBinding = new NodeBinding(formatVersion);
        return hexToObject(nodeBinding, hex);
    }

    /**
     * Deserialize the mode from its byte representation.
     *
     * @param bytes the byte representation of the node.
     * @param formatVersion the group format version
     *
     * @return the deserialized object
     */
    public static RepNodeImpl deserializeNode(final byte[] bytes,
                                              final int formatVersion) {
        final NodeBinding binding = new NodeBinding(formatVersion);
        TupleInput tuple = new TupleInput(bytes);
        return binding.entryToObject(tuple);
    }

    /**
     * Carries out the two step de-serialization from hex string into a byte
     * buffer and subsequently into its object representation.
     *
     * @return the object representation
     */
    private static <T> T hexToObject(TupleBinding<T> binding, String hex) {
        byte buffer[] = new byte[(hex.length() / 2)];
        for (int i = 0; i < hex.length(); i += 2) {
            int value = Character.digit(hex.charAt(i), 16);
            value |= Character.digit(hex.charAt(i + 1), 16) << 4;
            buffer[i >> 1] = (byte)value;
        }
        TupleInput tuple = new TupleInput(buffer);
        return binding.entryToObject(tuple);
    }

    /**
     * De-serializes an array of tokens into a Rep group object and its nodes.
     * the token at <code>start</code>represents the group object and each
     * subsequent token represents a node in the group.
     *
     * @param tokens the array representing the group and its nodes
     * @param start the position in the array at which to start the
     * de-serialization.
     *
     * @return the de-serialized RepGroup
     */
    static public RepGroupImpl deserializeHex(String[] tokens, int start) {
        final RepGroupDB.GroupBinding groupBinding =
            new RepGroupDB.GroupBinding();
        RepGroupImpl group = hexToObject(groupBinding, tokens[start++]);
        Map<Integer, RepNodeImpl> nodeMap =
            new HashMap<Integer, RepNodeImpl>();
        while (start < tokens.length) {
            RepNodeImpl n =
                hexDeserializeNode(tokens[start++], group.getFormatVersion());
            RepNodeImpl old = nodeMap.put(n.getNameIdPair().getId(), n);
            assert(old == null);
        }
        group.setNodes(nodeMap);
        return group;
    }

    /*
     * Accessing nodes and groups of nodes
     */

    /**
     * Returns the node IDs for all nodes that are currently members of the
     * group and that act as proposers, acceptors, or distinguished learners.
     * Returns IDs for all ELECTABLE and MONITOR nodes that are not removed,
     * even if they are not acknowledged, but not for SECONDARY nodes.
     */
    public Set<Integer> getAllElectionMemberIds() {
        Set<Integer> ret = new HashSet<Integer>();
        synchronized (nodesById) {
            for (RepNodeImpl mi : nodesById.values()) {
                if (!mi.isRemoved() && !mi.getType().isSecondary()) {
                    ret.add(mi.getNodeId());
                }
            }
        }
        return ret;
    }

    /**
     * Returns all nodes that are currently members of the group.  Returns all
     * ELECTABLE and MONITOR nodes that are not removed, even if they are not
     * acknowledged, and SECONDARY nodes.  If the predicate is not null, only
     * includes members that satisfy the predicate.
     */
    public Set<RepNodeImpl> getAllMembers(final Predicate p) {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeAllMembers(p, result);
        return result;
    }

    /**
     * Adds all nodes that are currently members of the group to the specified
     * set.  Adds all ELECTABLE and MONITOR nodes that are not removed, even if
     * they are not acknowledged, and SECONDARY nodes.  If the predicate is not
     * null, only adds members that satisfy the predicate.
     */
    public void includeAllMembers(final Predicate p,
                                  final Set<? super RepNodeImpl> set) {
        synchronized (nodesById) {
            for (RepNodeImpl mi : nodesById.values()) {
                if (!mi.isRemoved() && ((p == null) || p.include(mi))) {
                    set.add(mi);
                }
            }
        }
    }

    /**
     * Counts the number of nodes that are currently members of the group.
     * Counts all ELECTABLE and MONITOR nodes that are not removed, even if
     * they are not acknowledged, and SECONDARY nodes.  If the predicate is not
     * null, only counts members that satisfy the predicate.
     */
    public int countAllMembers(final Predicate p) {
        int count = 0;
        synchronized (nodesById) {
            for (final RepNodeImpl mi : nodesById.values()) {
                if (!mi.isRemoved() && ((p == null) || p.include(mi))) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Adds nodes that are currently members of the group to the specified set.
     * Adds ELECTABLE and MONITOR node that are not removed and are
     * acknowledged, and SECONDARY nodes.  If the predicate is not null, only
     * adds members that satisfy the predicate.
     */
    public void includeMembers(final Predicate p,
                               final Set<? super RepNodeImpl> set) {
        synchronized (nodesById) {
            for (RepNodeImpl n : nodesById.values()) {
                if (!n.isRemoved() &&
                    n.isQuorumAck() &&
                    ((p == null) || p.include(n))) {
                    set.add(n);
                }
            }
        }
    }

    /**
     * Gets the node that is currently a member of the group that has the given
     * socket address.  Returns ELECTABLE and MONITOR nodes that are not
     * removed, even if it is not acknowledged, and SECONDARY nodes.
     *
     * @return the desired node, or null if there is no such node, including
     *         if it was removed
     */
    public RepNodeImpl getMember(InetSocketAddress socket) {
        synchronized (nodesById) {
            for (RepNodeImpl n : nodesById.values()) {
                if (socket.equals(n.getSocketAddress()) && !n.isRemoved()) {
                    return n;
                }
            }
        }
        return null;
    }

    /**
     * Returns nodes that are removed from the group.  Returns ELECTABLE and
     * MONITOR nodes that are removed and are acknowledged, but not SECONDARY
     * nodes, which are not remembered when they are removed.
     */
    public Set<RepNodeImpl> getRemovedNodes() {
        Set<RepNodeImpl> ret = new HashSet<RepNodeImpl>();
        synchronized (nodesById) {
            for (RepNodeImpl mi : nodesById.values()) {
                if (mi.isRemoved() && mi.isQuorumAck()) {
                    ret.add(mi);
                }
            }
        }
        return ret;
    }

    /** A predicate for specifying which replication nodes to include. */
    abstract static class Predicate {
        abstract boolean include(RepNodeImpl n);
    }

    /**
     * Returns all electable nodes that are currently members of the group.
     * Returns all ELECTABLE nodes that are not removed, even if they are not
     * acknowledged, but not MONITOR or SECONDARY nodes.
     */
    public Set<RepNodeImpl> getAllElectableMembers() {
        return getAllMembers(ELECTABLE_PREDICATE);
    }

    /**
     * Returns electable nodes that are currently members of the group.
     * Returns ELECTABLE nodes that are not removed and are acknowledged, but
     * not MONITOR or SECONDARY nodes.
     */
    public Set<RepNodeImpl> getElectableMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeElectableMembers(result);
        return result;
    }

    /**
     * Adds the electable nodes that are currently members of the group to the
     * specified set.  Adds ELECTABLE nodes that are not removed and are
     * acknowledged, but not MONITOR or SECONDARY nodes.
     */
    public void includeElectableMembers(final Set<? super RepNodeImpl> set) {
        includeAllMembers(
            new Predicate() {
                @Override
                boolean include(RepNodeImpl n) {
                    return n.getType().isElectable() && n.isQuorumAck();
                }
            },
            set);
    }

    /**
     * Returns the nodes that are currently members of the group that store
     * replication data.  Returns ELECTABLE nodes that are not removed and are
     * acknowledged, and SECONDARY nodes, but not MONITOR nodes.
     */
    public Set<RepNodeImpl> getDataMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeDataMembers(result);
        return result;
    }

    /**
     * Adds the nodes that are currently members of the group that store
     * replication data to the specified set.  Adds ELECTABLE nodes that are
     * not removed and are acknowledged, and SECONDARY nodes, but not MONITOR
     * nodes.
     */
    public void includeDataMembers(final Set<? super RepNodeImpl> set) {
        includeAllMembers(
            new Predicate() {
                @Override
                boolean include(final RepNodeImpl n) {
                    return n.getType().isDataNode() && n.isQuorumAck();
                }
            },
            set);
    }

    /**
     * Returns the monitor nodes that are currently members of the group.
     * Returns MONITOR nodes that are not removed and are acknowledged, but not
     * ELECTABLE or SECONDARY nodes.
     *
     * @return the set of monitor nodes
     */
    public Set<RepNodeImpl> getMonitorMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeMonitorMembers(result);
        return result;
    }

    /**
     * Adds the monitor nodes that are currently members of the group to the
     * specified set.  Adds MONITOR nodes that are not removed and are
     * acknowledged, but not ELECTABLE or SECONDARY nodes.
     */
    public void includeMonitorMembers(final Set<? super RepNodeImpl> set) {
        includeMembers(MONITOR_PREDICATE, set);
    }

    /**
     * Returns all the nodes that are currently members of the group that act
     * as distinguished learners to receive election results.  Returns all
     * ELECTABLE and MONITOR that are not removed, even if they are not
     * acknowledged, but not SECONDARY nodes.
     */
    public Set<RepNodeImpl> getAllLearnerMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeAllMembers(
            new Predicate() {
                @Override
                boolean include(final RepNodeImpl n) {
                    return (n.getType().isElectable() ||
                            n.getType().isMonitor());
                }
            },
            result);
        return result;
    }

    /**
     * Returns the secondary nodes that are currently members of the group.
     * Returns SECONDARY nodes, but not ELECTABLE or MONITOR nodes.
     */
    public Set<RepNodeImpl> getSecondaryMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeSecondaryMembers(result);
        return result;
    }

    /**
     * Adds the secondary nodes that are currently members of the group to the
     * specified set.  Adds SECONDARY nodes, but not ELECTABLE or MONITOR
     * nodes.
     */
    public void includeSecondaryMembers(final Set<? super RepNodeImpl> set) {
        includeAllMembers(SECONDARY_PREDICATE, set);
    }

    /**
     * Returns the arbiter nodes that are currently members of the group.
     * Returns ARBITER nodes.
     */
    public Set<RepNodeImpl> getArbiterMembers() {
        final Set<RepNodeImpl> result = new HashSet<RepNodeImpl>();
        includeArbiterMembers(result);
        return result;
    }

    /**
     * Adds the arbiter nodes that are currently members of the group to the
     * specified set.  Adds ARBITER nodes.
     */
    public void includeArbiterMembers(final Set<? super RepNodeImpl> set) {
        includeAllMembers(ARBITER_PREDICATE, set);
    }

    /**
     * Returns the socket addresses for all nodes that are currently members of
     * the group.  Returns addresses for all ELECTABLE and MONITOR nodes that
     * are not removed, even if they are not acknowledged, and for all
     * SECONDARY nodes.  If the predicate is not null, only returns addresses
     * for members that satisfy the predicate. ARBITER nodes are also ELECTABLE
     * and will be part of the returned set.
     */
    private Set<InetSocketAddress> getAllMemberSockets(Predicate p) {
        Set<InetSocketAddress> sockets = new HashSet<InetSocketAddress>();
        synchronized (nodesById) {
            for (final RepNodeImpl mi : nodesById.values()) {
                if ((((mi.getType().isElectable() ||
                       mi.getType().isMonitor()) &&
                      !mi.isRemoved()) ||
                     mi.getType().isSecondary()) &&
                    ((p == null) || p.include(mi))) {
                    sockets.add(mi.getSocketAddress());
                }
            }
        }
        return sockets;
    }

    /**
     * Return the socket addresses for all nodes that are currently members of
     * the group and act as distinguished learners to receive election results.
     * Returns addresses for all ELECTABLE and MONITOR nodes that are not
     * removed, even if they are not acknowledged, but not for SECONDARY nodes.
     *
     * @return set of learner socket addresses
     */
    public Set<InetSocketAddress> getAllLearnerSockets() {

        /*
         * TODO: Consider including SECONDARY nodes in this list.  That change
         * would increase the chance that SECONDARY nodes have up-to-date
         * information about the master, but would need to be paired with a
         * change to only wait for delivery of notifications to ELECTABLE
         * nodes, to avoid adding sensitivity to potentially longer network
         * delays in communicating with secondary nodes.
         */
        return getAllMemberSockets(new Predicate() {
            @Override
            boolean include(RepNodeImpl n) {
                return !n.getType().isSecondary();
            }
        });
    }

    /**
     * Return the socket addresses for all nodes that are currently members of
     * the group and act as helpers to supply election results.  Returns
     * addresses for all ELECTABLE and MONITOR nodes that are not removed, even
     * if they are not acknowledged, and SECONDARY nodes.
     *
     * @return set of helper socket addresses
     */
    public Set<InetSocketAddress> getAllHelperSockets() {
        return getAllMemberSockets(null);
    }

    /**
     * Returns the socket addresses for all monitor nodes that are currently
     * members of the group.  Returns addresses for all MONITOR nodes that are
     * not removed, even if they are not acknowledged, but not for ELECTABLE or
     * SECONDARY nodes.
     *
     * @return the set of Monitor socket addresses
     */
    public Set<InetSocketAddress> getAllMonitorSockets() {
        return getAllMemberSockets(MONITOR_PREDICATE);
    }

    /**
     * Returns the socket addresses for all nodes that are currently members of
     * the group and act as acceptors for elections.  Returns addresses for all
     * ELECTABLE nodes that are not removed, even if they are not acknowledged,
     * but not for MONITOR or SECONDARY nodes, which do not act as acceptors.
     *
     * @return the set of acceptor socket addresses
     */
    public Set<InetSocketAddress> getAllAcceptorSockets() {
        return getAllMemberSockets(ELECTABLE_PREDICATE);
    }

    /**
     * Returns the node with the specified ID that is currently a member of the
     * group, throwing an exception if the node is found but is no longer a
     * member.  Returns ELECTABLE and MONITOR nodes that are not removed, even
     * if they are not acknowledged, and SECONDARY nodes.
     *
     * @param nodeId the node ID
     * @return the member or null
     * @throws EnvironmentFailureException if the node is no longer a member
     */
    public RepNodeImpl getMember(int nodeId) {
        RepNodeImpl node = getNode(nodeId);
        if (node == null) {
            return null;
        }
        if (node.isRemoved()) {
            throw EnvironmentFailureException.unexpectedState
                ("No longer a member:" + nodeId);
        }
        return node;
    }

    /**
     * Returns the node with the specified name that is currently a member of
     * the group, throwing an exception if the node is found but is no longer a
     * member.  Returns ELECTABLE and MONITOR nodes that are not removed, even
     * if they are not acknowledged, and SECONDARY nodes.
     *
     * @param name the node name
     * @return the member or null
     * @throws MemberNotFoundException if the node is no longer a member
     */
    public RepNodeImpl getMember(String name)
        throws MemberNotFoundException {

        RepNodeImpl node = getNode(name);
        if (node == null) {
            return null;
        }
        if (node.isRemoved()) {
            throw new MemberNotFoundException
                ("Node no longer a member:" + name);
        }
        return node;
    }

    /**
     * Returns the node with the specified ID, regardless of its membership
     * state.  Returns all ELECTABLE and MONITOR nodes, even if they are
     * removed or are not acknowledged, and SECONDARY nodes.
     *
     *  @return the node or null
     */
    public RepNodeImpl getNode(int nodeId) {
        synchronized (nodesById) {
            return nodesById.get(nodeId);
        }
    }

    /**
     * Returns the node with the specified name, regardless of its membership
     * state.  Returns all ELECTABLE and MONITOR nodes, even if they are
     * removed or are not acknowledged, and SECONDARY nodes.
     *
     *  @return the node or null
     */
    public RepNodeImpl getNode(String name) {
        synchronized (nodesById) {
            return nodesByName.get(name);
        }
    }

    /**
     * Returns the number of all electable nodes that are currently members of
     * the group.  Includes all ELECTABLE nodes that are not removed, even if
     * they are not acknowledged, but not MONITOR or SECONDARY nodes.  Note
     * that even unACKed nodes are considered part of the group for group
     * size/durability considerations.
     *
     * @return the size of the group for durability considerations
     */
    public int getElectableGroupSize() {
        return countAllMembers(ELECTABLE_PREDICATE);
    }

    /**
     * Returns the number of all electable nodes that are currently members of
     * the group.  Includes all ELECTABLE nodes that are not removed, even if
     * they are not acknowledged, but not MONITOR, ARBITER or SECONDARY nodes.
     * Note that even unACKed nodes are considered part of the group for group
     * size/durability considerations.
     *
     * @return the size of the group for durability considerations
     */
    public int getAckGroupSize() {
        return countAllMembers(ACK_PREDICATE);
    }

    /* Miscellaneous */

    /**
     * Returns the name of the group.
     *
     * @return the name of the group.
     */
    public String getName() {
        return groupName;
    }

    /**
     * Encapsulates the last known syncup state associated with a node.
     */
    public static class BarrierState implements Serializable {

        private static final long serialVersionUID = 1L;

        /*
         * The latest sync position of this node in the replication stream.
         * This position is approximate and is updated on some regular basis.
         * It is conservative in that the node is likely to have a newer sync
         * point. So it represents a lower bound for its sync point.
         */
        private final VLSN lastLocalCBVLSN;

        /*
         * The time that the sync point was last recorded. Note that clocks
         * should be reasonably synchronized.
         */
        private final long barrierTime;

        public BarrierState(VLSN lastLocalCBVLSN, long barrierTime) {
            super();
            this.lastLocalCBVLSN = lastLocalCBVLSN;
            this.barrierTime = barrierTime;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result +
                    (lastLocalCBVLSN == null ? 0 : lastLocalCBVLSN.hashCode());
            result = prime * result +
                     (int) (barrierTime ^ (barrierTime >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            BarrierState other = (BarrierState) obj;
            if (lastLocalCBVLSN == null) {
                if (other.lastLocalCBVLSN != null) {
                    return false;
                }
            } else if (!lastLocalCBVLSN.equals(other.lastLocalCBVLSN)) {
                return false;
            }
            if (barrierTime != other.barrierTime) {
                return false;
            }
            return true;
        }

        public VLSN getLastCBVLSN() {
            return lastLocalCBVLSN;
        }

        public long getBarrierTime() {
            return barrierTime;
        }

        @Override
        public String toString() {
            return String.format("LocalCBVLSN:%,d at:%tc",
                                 lastLocalCBVLSN.getSequence(), barrierTime);
        }
    }

    /*
     * An internal exception indicating that two nodes have conflicting
     * configurations. For example, they both use the same hostname and port.
     */
    @SuppressWarnings("serial")
    public static class NodeConflictException extends DatabaseException {
        public NodeConflictException(String message) {
            super(message);
        }
    }

    /**
     * Return information to the user, format nicely for ease of reading.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group info [").append(groupName).append("] ");
        sb.append(getUUID()).
            append("\n Format version: ").append(getFormatVersion()).
            append("\n Change version: ").append(getChangeVersion()).
            append("\n Max persist rep node ID: ").append(getNodeIdSequence()).
            append("\n Min JE version: ").append(minJEVersion).
            append("\n");

        synchronized (nodesById) {
            for (final RepNodeImpl node : nodesById.values()) {
                sb.append(" ").append(node);
            }
        }
        return sb.toString();
    }
}
