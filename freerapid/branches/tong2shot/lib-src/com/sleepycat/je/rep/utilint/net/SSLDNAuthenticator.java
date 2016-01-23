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

package com.sleepycat.je.rep.utilint.net;

import javax.net.ssl.SSLSession;

import com.sleepycat.je.rep.net.SSLAuthenticator;
import com.sleepycat.je.rep.net.InstanceParams;

/**
 * This is an implementation of SSLAuthenticator which authenticates based
 * on the Distinguished Name (DN) in the SSL peer's certificate.  Matching
 * is done using Java regular expressions against the RFC1779 normalized
 * DN.  This may be used to match against the complete DN or just a portion,
 * such as the CN portion.
 */

public class SSLDNAuthenticator
    extends SSLDNMatcher
    implements SSLAuthenticator {

    /**
     * Construct an SSLDNAuthenticator
     *
     * @param params The parameter for authentication creation.  This class
     *        requires a Java regular expression to be applied to the subject
     *        common name.
     */
    public SSLDNAuthenticator(InstanceParams params) {
        super(params);
    }

    /*
     * Based on the information in the SSLSession object, should the peer
     * be trusted as an internal entity?  This should be called only after
     * The SSL handshake has completed.
     */
    @Override
    public boolean isTrusted(SSLSession sslSession) {
        return peerMatches(sslSession);
    }

    /**
     * Verify that the string is a valid pattern.
     * @throws IllegalArgumentException if not a valid pattern.
     */
    public static void validate(String regex)
        throws IllegalArgumentException {

        validateRegex(regex);
    }
}
