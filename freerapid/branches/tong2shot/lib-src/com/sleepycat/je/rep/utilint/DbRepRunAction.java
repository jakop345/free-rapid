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

package com.sleepycat.je.rep.utilint;

import java.io.File;

import com.sleepycat.je.CheckpointConfig;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.rep.RepInternal;
import com.sleepycat.je.rep.ReplicatedEnvironment;
import com.sleepycat.je.rep.ReplicationConfig;
import com.sleepycat.je.utilint.CmdUtil;

/**
 * @hidden
 * RepRunAction is a debugging aid that invokes a ReplicatedEnvironment recovery
 * from the command line.
 */
public class DbRepRunAction {
    private static final String USAGE =

        "usage: " + CmdUtil.getJavaCommand(DbRepRunAction.class) + "\n" +
        "       -h <dir> # environment home directory\n" +
        "       -group <name> # groupName\n" +
        "       -name <name> # nodeName\n" +
        "       -host <host> # nodeHost\n" +
        "       -showVLSN (dump vlsn index )\n" +
        "       -checkpoint (forced )\n";

    private File envHome;
    private String nodeName;
    private String nodeHost;
    private String groupName;
    private boolean showVLSN;
    private boolean doCheckpoint;

    public static void main(String[] argv) {

        DbRepRunAction runAction = new DbRepRunAction();
        runAction.parseArgs(argv);

        try {
            runAction.run();
            System.exit(0);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void parseArgs(String argv[]) {

        int argc = 0;
        int nArgs = argv.length;

        if (nArgs < 4) {
            printUsage(null);
            System.exit(0);
        }

        while (argc < nArgs) {
            String thisArg = argv[argc++];
            if (thisArg.equals("-h")) {
                if (argc < nArgs) {
                    envHome = new File(argv[argc++]);
                } else {
                    printUsage("-h requires an argument");
                }
            } else if (thisArg.equals("-name")) {
                if (argc < nArgs) {
                    nodeName = argv[argc++];
                } else {
                    printUsage("-name requires an argument");
                }
            } else if (thisArg.equals("-host")) {
                if (argc < nArgs) {
                    nodeHost = argv[argc++];
                } else {
                    printUsage("-host requires an argument");
                }
            } else if (thisArg.equals("-group")) {
                if (argc < nArgs) {
                    groupName = argv[argc++];
                } else {
                    printUsage("-group requires an argument");
                }
            } else if (thisArg.equals("-showVLSN")) {
                showVLSN = true;
            } else if (thisArg.equals("-checkpoint")) {
                doCheckpoint = true;
            } else {
                printUsage(thisArg + " is not a valid argument");
            }
        }
    }

    private void run() {
        ReplicatedEnvironment repEnv = recover();
        if (showVLSN) {
            RepInternal.getRepImpl(repEnv).getVLSNIndex().dumpDb(true);
        }
        if (doCheckpoint) {
            repEnv.checkpoint(new CheckpointConfig().setForce(true));
        }
        repEnv.close();
    }

    private ReplicatedEnvironment recover() {
        ReplicationConfig repConfig = new ReplicationConfig();
        repConfig.setNodeName(nodeName);
        repConfig.setGroupName(groupName);
        repConfig.setNodeHostPort(nodeHost);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);

        return RepInternal.createDetachedEnv(envHome,
                                             repConfig,
                                             envConfig);

    }

    private void printUsage(String msg) {
        if (msg != null) {
            System.out.println(msg);
        }
        System.out.println(USAGE);
        System.exit(-1);
    }
}
