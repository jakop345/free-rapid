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

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DbInternal;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.cleaner.FileSummary;
import com.sleepycat.je.cleaner.UtilizationCalculator;
import com.sleepycat.je.dbi.EnvironmentImpl;
import com.sleepycat.je.log.UtilizationFileReader;
import com.sleepycat.je.utilint.CmdUtil;
import com.sleepycat.je.utilint.DbLsn;

/**
 * DbSpace displays the disk space utilization for an environment.
 * <pre>
 * usage: java { com.sleepycat.je.util.DbSpace |
 *               -jar je-&lt;version&gt;.jar DbSpace }
 *          -h &lt;dir&gt;# environment home directory
 *         [-q]     # quiet, print grand totals only
 *         [-u]     # sort by utilization
 *         [-d]     # dump file summary details
 *         [-r]     # recalculate utilization (expensive)
 *         [-s]     # start file number or LSN, in hex
 *         [-e]     # end file number or LSN, in hex
 *         [-V]     # print JE version number
 * </pre>
 */
public class DbSpace {

    private static final String USAGE =
        "usage: " + CmdUtil.getJavaCommand(DbSpace.class) + "\n" +
        "       -h <dir> # environment home directory\n" +
        "       [-q]     # quiet, print grand totals only\n" +
        "       [-u]     # sort by utilization\n" +
        "       [-d]     # dump file summary details\n" +
        "       [-r]     # recalculate utilization (expensive)\n" +
        "       [-s]     # start file number or LSN, in hex\n" +
        "       [-e]     # end file number or LSN, in hex\n" +
        "       [-V]     # print JE version number";

    public static void main(String argv[])
        throws Exception {

        DbSpace space = new DbSpace();
        space.parseArgs(argv);

        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setReadOnly(true);
        Environment env = new Environment(space.envHome, envConfig);
        space.initEnv(DbInternal.getEnvironmentImpl(env));

        try {
            space.print(System.out);
            System.exit(0);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        } finally {
            try {
                env.close();
            } catch (Throwable e) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }

    private File envHome = null;
    private EnvironmentImpl envImpl;
    private boolean quiet = false;
    private boolean sorted = false;
    private boolean details = false;
    private boolean recalc = false;
    private long startLsn = DbLsn.NULL_LSN;
    private long finishLsn = DbLsn.NULL_LSN;

    private DbSpace() {
    }

    /**
     * Creates a DbSpace object for calculating utilization using an open
     * Environment.
     */
    public DbSpace(Environment env,
                   boolean quiet,
                   boolean details,
                   boolean sorted) {
        this(DbInternal.getEnvironmentImpl(env), quiet, details, sorted);
    }

    /**
     * For internal use only.
     * @hidden
     */
    public DbSpace(EnvironmentImpl envImpl,
                   boolean quiet,
                   boolean details,
                   boolean sorted) {
        initEnv(envImpl);
        this.quiet = quiet;
        this.details = details;
        this.sorted = sorted;
    }

    private void initEnv(EnvironmentImpl envImpl) {
        this.envImpl = envImpl;
    }

    private void printUsage(String msg) {
        if (msg != null) {
            System.err.println(msg);
        }
        System.err.println(USAGE);
        System.exit(-1);
    }

    private void parseArgs(String argv[]) {

        int argc = 0;
        int nArgs = argv.length;

        if (nArgs == 0) {
            printUsage(null);
            System.exit(0);
        }

        while (argc < nArgs) {
            String thisArg = argv[argc++];
            if (thisArg.equals("-q")) {
                quiet = true;
            } else if (thisArg.equals("-u")) {
                sorted = true;
            } else if (thisArg.equals("-d")) {
                details = true;
            } else if (thisArg.equals("-r")) {
                recalc = true;
            } else if (thisArg.equals("-V")) {
                System.out.println(JEVersion.CURRENT_VERSION);
                System.exit(0);
            } else if (thisArg.equals("-h")) {
                if (argc < nArgs) {
                    envHome = new File(argv[argc++]);
                } else {
                    printUsage("-h requires an argument");
                }
            } else if (thisArg.equals("-s")) {
                if (argc < nArgs) {
                    startLsn = CmdUtil.readLsn(argv[argc++]);
                } else {
                    printUsage("-s requires an argument");
                }
            } else if (thisArg.equals("-e")) {
                if (argc < nArgs) {
                    finishLsn = CmdUtil.readLsn(argv[argc++]);
                } else {
                    printUsage("-e requires an argument");
                }
            }
        }

        if (envHome == null) {
            printUsage("-h is a required argument");
        }
    }

    /**
     * Sets the recalculation property, which if true causes a more expensive
     * recalculation of utilization to be performed for debugging purposes.
     * This property is false by default.
     */
    public void setRecalculate(boolean recalc) {
        this.recalc = recalc;
    }

    /**
     * Sets the start file number, which is a lower bound on the range of
     * files for which utilization is reported and (optionally) recalculated.
     * By default there is no lower bound.
     */
    public void setStartFile(long startFile) {
        this.startLsn = startFile;
    }

    /**
     * Sets the ending file number, which is an upper bound on the range of
     * files for which utilization is reported and (optionally) recalculated.
     * By default there is no upper bound.
     */
    public void setEndFile(long endFile) {
        this.finishLsn = endFile;
    }

    /**
     * Calculates utilization and prints a report to the given output stream.
     */
    public void print(PrintStream out)
        throws DatabaseException {

        long startFile = (startLsn != DbLsn.NULL_LSN) ?
            DbLsn.getFileNumber(startLsn) :
            0;
        long finishFile = (finishLsn != DbLsn.NULL_LSN) ?
            DbLsn.getFileNumber(finishLsn) :
            Long.MAX_VALUE;
        SortedMap<Long,FileSummary> map =
            envImpl.getUtilizationProfile().getFileSummaryMap(true).
            subMap(startFile, finishFile);
        Map<Long, FileSummary> recalcMap =
            recalc ?
            UtilizationFileReader.calcFileSummaryMap(envImpl, startLsn,
                                                     finishLsn) :
            null;
        int fileIndex = 0;

        Summary totals = new Summary();
        Summary[] summaries = null;
        if (!quiet) {
            summaries = new Summary[map.size()];
        }

        Iterator<Map.Entry<Long,FileSummary>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Long,FileSummary> entry = iter.next();
            Long fileNum = entry.getKey();
            FileSummary fs = entry.getValue();
            FileSummary recalcFs = null;
            if (recalcMap != null) {
                 recalcFs = recalcMap.get(fileNum);
            }
            Summary summary = new Summary(fileNum, fs, recalcFs);
            if (summaries != null) {
                summaries[fileIndex] = summary;
            }
            if (details) {
                out.println
                    ("File 0x" + Long.toHexString(fileNum.longValue()) +
                     ": " + fs);
                if (recalcMap != null) {
                    out.println
                        ("Recalculated File 0x" +
                         Long.toHexString(fileNum.longValue()) +
                         ": " + recalcFs);
                }
            }
            totals.add(summary);
            fileIndex += 1;
        }

        if (details) {
            out.println();
        }
        out.println(recalc ? Summary.RECALC_HEADER : Summary.HEADER);

        if (summaries != null) {
            if (sorted) {
                Arrays.sort(summaries, new Comparator<Summary>() {
                    public int compare(Summary s1, Summary s2) {
                        return s1.utilization() - s2.utilization();
                    }
                });
            }
            for (int i = 0; i < summaries.length; i += 1) {
                summaries[i].print(out, recalc);
            }
        }

        totals.print(out, recalc);
    }

    private class Summary {

        static final String HEADER = "  File    Size (KB)  % Used\n" +
                                     "--------  ---------  ------";
                                   // 12345678  123456789     123
                                   //         12         12345
                                   // TOTALS:

        static final String RECALC_HEADER =
                   "  File    Size (KB)  % Used  % Used (recalculated)\n" +
                   "--------  ---------  ------  ------";
                 // 12345678  123456789     123     123
                 //         12         12345   12345
                 // TOTALS:

        Long fileNum;
        long totalSize;
        long obsoleteSize;
        long recalcObsoleteSize;

        Summary() {}

        Summary(Long fileNum, FileSummary summary, FileSummary recalcSummary) {
            this.fileNum = fileNum;
            totalSize = summary.totalSize;
            obsoleteSize = summary.getObsoleteSize();
            if (recalcSummary != null) {
                recalcObsoleteSize = recalcSummary.getObsoleteSize();
            }
        }

        void add(Summary o) {
            totalSize += o.totalSize;
            obsoleteSize += o.obsoleteSize;
            recalcObsoleteSize += o.recalcObsoleteSize;
        }

        void print(PrintStream out, boolean recalc) {
            if (fileNum != null) {
                pad(out, Long.toHexString(fileNum.longValue()), 8, '0');
            } else {
                out.print(" TOTALS ");
            }
            int kb = (int) (totalSize / 1024);
            out.print("  ");
            pad(out, Integer.toString(kb), 9, ' ');
            out.print("     ");
            pad(out, Integer.toString(utilization()), 3, ' ');
            if (recalc) {
                out.print("     ");
                pad(out, Integer.toString(recalcUtilization()), 3, ' ');
            }
            out.println();
        }

        int utilization() {
            return FileSummary.utilization(obsoleteSize, totalSize);
        }

        int recalcUtilization() {
            return FileSummary.utilization(recalcObsoleteSize, totalSize);
        }

        private void pad(PrintStream out, String val, int digits,
                           char padChar) {
            int padSize = digits - val.length();
            for (int i = 0; i < padSize; i += 1) {
                out.print(padChar);
            }
            out.print(val);
        }
    }
}
