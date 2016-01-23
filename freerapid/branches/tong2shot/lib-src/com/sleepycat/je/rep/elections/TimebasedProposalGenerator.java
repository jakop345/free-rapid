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

package com.sleepycat.je.rep.elections;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.rep.elections.Proposer.Proposal;
import com.sleepycat.je.rep.elections.Proposer.ProposalParser;

/**
 * Generates a unique sequence of ascending proposal numbers that is unique
 * across all machines.
 *
 * Each proposal number is built as the catenation of the following components:
 *
 * ms time (8 bytes) | IP address  (16 bytes) | locally unique Id (4 bytes)
 *
 * The ms time supplies the increasing number and the IP address is a number
 * unique across machines. If the node that hosts the replication node is
 * disconnected, an IP address is unavailable. In that case, we will substitute
 * a constant value of 00...0001. The node will be disconnected if:
 *   - this node is isolated from the network and other nodes from the group
 *     are on other machines, in which case it doesn't matter
 *     if this node generates a proper proposal, because no one will ever see it
 *   - or this is a test that is being run on a single, disconnected machine
 *   - or this is a demo that is being run on a single, disconnected machine
 * In all these cases, a disconnected host need not generate a unique proposal
 * value.
 *
 * Mostly to support testing, the IP address is xor'ed with a random number so
 * that generators created in separate JVMs on the same host will still most
 * likely get different values.
 */
public class TimebasedProposalGenerator {

    /*
     * A number that is unique for all instances of the TimeBasedGenerator on
     * this machine.
     */
    private final int locallyUniqueId;
    private static final AtomicInteger uniqueIdGenerator =
        new AtomicInteger(1);
    static final private String DEFAULT_ID = "000000000000000000000000000001";

    /*
     * Tracks the time (in ms) used to generate the previous proposal
     * preventing the creation of duplicate proposals.  Synchronize on this
     * instance when accessing this field.
     */
    private long prevProposalTime = System.currentTimeMillis();

    /*
     * A unique ID for this JVM, using a hex representation of the IP address
     * XOR'ed with a random value.
     */
    private static final String machineId;

    /* Width of each field in the Proposal number in hex characters. */
    final static int TIME_WIDTH = 16;

    /* Allow for 16 byte ipv6 addresses. */
    final static int ADDRESS_WIDTH =32;
    final static int UID_WIDTH = 8;

    /*
     * Initialize machineId, do it just once to minimize latencies in the face
     * of misbehaving networks that slow down calls to getLocalHost()
     */
    static  {
        try {
            final InetAddress localHost = java.net.InetAddress.getLocalHost();
            byte[] localAddress = localHost.getAddress();

            if (localHost.isLoopbackAddress()) {
                /* Linux platforms return a loopback address, examine the
                 * interfaces individually for a suitable address.
                 */
                localAddress = null;
                try {
                    for (Enumeration<NetworkInterface> interfaces =
                        NetworkInterface.getNetworkInterfaces();
                        interfaces.hasMoreElements();) {
                        for (Enumeration<InetAddress> addresses =
                            interfaces.nextElement().getInetAddresses();
                            addresses.hasMoreElements();) {
                            InetAddress ia = addresses.nextElement();
                            if (! (ia.isLoopbackAddress() ||
                                   ia.isAnyLocalAddress() ||
                                   ia.isMulticastAddress())) {
                                /* Found one, any one of these will do. */
                                localAddress = ia.getAddress();
                                break;
                            }
                        }
                    }
                } catch (SocketException e) {
                    // Could not get the network interfaces, give up
                }
            }
            if (localAddress == null) {
                /*
                 * This host is disconnected, no need to provide a unique
                 * machine id. Unfortunately, no way to log this fact,
                 * since logging is not available for a static block.
                 */
                machineId = DEFAULT_ID;

            } else {

                /*
                 * Convert the address to a positive integer, XOR it with a
                 * random value of the right size, and format in hex
                 */
                final BigInteger addrVal = new BigInteger(1, localAddress);
                final BigInteger randVal =
                    new BigInteger(ADDRESS_WIDTH * 4, new SecureRandom());
                machineId = String.format("%0" + ADDRESS_WIDTH + "x",
                                          addrVal.xor(randVal));
            }
        } catch (UnknownHostException e) {
            throw EnvironmentFailureException.unexpectedException(e);
        }
    }

    /**
     * Creates an instance with an application-specified locally (machine wide)
     * unique id, e.g. a port number, or a combination of a pid and some other
     * number.
     *
     * @param locallyUniqueId the machine wide unique id
     */
    TimebasedProposalGenerator(int locallyUniqueId) {
        this.locallyUniqueId = locallyUniqueId;
    }

    /**
     * Constructor defaulting the unique id so it's merely unique within the
     * process.
     */
    public TimebasedProposalGenerator() {
        this(uniqueIdGenerator.getAndIncrement());
    }

    /**
     * Returns the next Proposal greater than all previous proposals returned
     * on this machine.
     *
     * @return the next unique proposal
     */
    public Proposal nextProposal() {
        long proposalTime = System.currentTimeMillis();
        synchronized (this) {
            if (proposalTime <= prevProposalTime) {
                /* Proposals are moving faster than the clock. */
                proposalTime = ++prevProposalTime;
            }
            prevProposalTime = proposalTime;
        }
        return new StringProposal(String.format("%016x%s%08x", proposalTime,
                                                machineId, locallyUniqueId));
    }

    /**
     * Returns the parser used to convert wire representations into Proposal
     * instances.
     *
     * @return a ProposalParser
     */
    public static ProposalParser getParser() {
        return StringProposal.getParser();
    }

    /**
     * Implements the Proposal interface for a string based proposal. The
     * string is a hex representation of the Proposal.
     */
    private static class StringProposal implements Proposal {
        private final String proposal;

        /* The canonical proposal parser. */
        private static ProposalParser theParser = new ProposalParser() {
                @Override
                public Proposal parse(String wireFormat) {
                    return ((wireFormat == null) || ("".equals(wireFormat))) ?
                        null :
                        new StringProposal(wireFormat);
                }
            };

        StringProposal(String proposal) {
            assert (proposal != null);
            this.proposal = proposal;
        }

        @Override
        public String wireFormat() {
            return proposal;
        }

        @Override
        public int compareTo(Proposal otherProposal) {
            return this.proposal.compareTo
                (((StringProposal) otherProposal).proposal);
        }

        @Override
        public String toString() {
            return "Proposal(" +
                proposal.substring(0, TIME_WIDTH) +
                ":" +
                proposal.substring(TIME_WIDTH, TIME_WIDTH + ADDRESS_WIDTH) +
                ":" + proposal.substring(TIME_WIDTH + ADDRESS_WIDTH) +
                ")";
        }

        private static ProposalParser getParser() {
            return theParser;
        }

        @Override
        public int hashCode() {
            return ((proposal == null) ? 0 : proposal.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof StringProposal)) {
                return false;
            }
            final StringProposal other = (StringProposal) obj;
            if (proposal == null) {
                if (other.proposal != null) {
                    return false;
                }
            } else if (!proposal.equals(other.proposal)) {
                return false;
            }
            return true;
        }
    }
}
