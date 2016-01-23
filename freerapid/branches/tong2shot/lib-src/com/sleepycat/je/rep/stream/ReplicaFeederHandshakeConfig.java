/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.stream;

import com.sleepycat.je.rep.NodeType;
import com.sleepycat.je.rep.impl.RepGroupImpl;
import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.utilint.NamedChannel;
import com.sleepycat.je.rep.utilint.RepUtils.Clock;

public interface ReplicaFeederHandshakeConfig {

    /**
     * Gets the RepImpl.
     * @return RepImpl
     */
    public RepImpl getRepImpl();

    /**
     * Gets the nodes NameIdPair.
     * @return NameIdPair
     */
    public NameIdPair getNameIdPair();

    /**
     * Gets the clock.
     * @return Clock
     */
    public Clock getClock();

    /**
     * Gets the NodeType.
     * @return NodeType
     */
    public NodeType getNodeType();

    /**
     * Gets the RepGroupImpl.
     * @return RepGroupImpl
     */
    public RepGroupImpl getGroup();

    /**
     * Gets the NamedChannel.
     * @return NamedChannel
     */
    public NamedChannel getNamedChannel();
}
