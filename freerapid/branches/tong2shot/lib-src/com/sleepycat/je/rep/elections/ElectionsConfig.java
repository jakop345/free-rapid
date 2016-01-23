/*-
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2002, 2014 Oracle and/or its affiliates.  All rights reserved.
 *
 */

package com.sleepycat.je.rep.elections;

import com.sleepycat.je.rep.impl.RepImpl;
import com.sleepycat.je.rep.impl.node.NameIdPair;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;

public interface ElectionsConfig {

    /**
     * Gets the replication group name.
     * @return group name
     */
    public String getGroupName();

    /**
     * Gets the nodes NameIdPair.
     * @return NameIdPair
     */
    public NameIdPair getNameIdPair();

    /**
     * Gets the ServiceDispatcher.
     * @return ServiceDispatcher
     */
    public ServiceDispatcher getServiceDispatcher();

    /**
     * Gets the election priority.
     * @return election priority
     */
    public int getElectionPriority();

    /**
     * Gets the JE log version.
     * @return log version
     */
    public int getLogVersion();

    /**
     * Gets the RepImpl.
     * @return RepImpl
     */
    public RepImpl getRepImpl();

    /**
     * Get the RepNode. May be null if the Elections
     * object is not used for the initiation of
     * an election.
     * @return RepNode
     */
    public RepNode getRepNode();
}
