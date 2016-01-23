package com.sleepycat.je.rep.arbiter;

import java.io.Serializable;

import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_MASTER;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_N_ACKS;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_N_REPLAY_QUEUE_OVERFLOW;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_STATE;
import static com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition.ARB_VLSN;

import com.sleepycat.je.rep.arbiter.impl.ArbiterStatDefinition;
import com.sleepycat.je.utilint.StatGroup;

public class ArbiterStats implements Serializable {

    private static final long serialVersionUID = 1734048134L;

    private final StatGroup arbStats;

    /**
     * @hidden
     * Internal use only.
     */
    ArbiterStats(StatGroup arbGrp) {
        if (arbGrp != null) {
            arbStats = arbGrp;
        } else {
            arbStats = new StatGroup(ArbiterStatDefinition.GROUP_NAME,
                    ArbiterStatDefinition.GROUP_DESC);
        }
    }

    /**
     * The number of attempts to queue a response when
     * the queue was full.
     */
    public long getReplayQueueOverflow() {
        return arbStats.getLong(ARB_N_REPLAY_QUEUE_OVERFLOW);
    }

    /**
     * The number of transactions that has been
     * acknowledged.
     */
    public long getAcks() {
        return arbStats.getLong(ARB_N_ACKS);
    }

    /**
     * The current master node.
     */
    public String getMaster() {
        return arbStats.getString(ARB_MASTER);
    }

    /**
     * The ReplicatedEnvironment.State of the node.
     */
    public String getState() {
        return arbStats.getString(ARB_STATE);
    }

    /**
     * The highest commit VLSN that has been
     * acknowledged.
     */
    public long getVLSN() {
        return arbStats.getLong(ARB_VLSN);
    }
}

