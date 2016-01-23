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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.sleepycat.je.rep.net.InstanceParams;

/**
 * This is an implementation of HostnameVerifier, which is intended to verify
 * that the host to which we are connected is valid.  This implementation
 * authenticates based on the Distinguished Name (DN) in the certificate of
 * the server matching the DN in the certificate that we would use when
 * operating as a server.  This is useful if deploying with a common SSL key
 * for all hosts.
 */

public class SSLMirrorHostVerifier
    extends SSLMirrorMatcher
    implements HostnameVerifier {

    /**
     * Construct an SSLMirrorHostVerifier
     *
     * @param params the instantiation parameters.
     * @throws IllegalArgumentException if the instance cannot be created due
     * to a problem related to the input parameters
     */
    public SSLMirrorHostVerifier(InstanceParams params)
        throws IllegalArgumentException {

        super(params, true);
    }

    /**
     * Checks whether an SSL connection has been made to the intended target.
     * This should be called only after the SSL handshake has completed.
     *
     * @param targetHost the host to which a connection is being established.
     *   This parameter is not used by this implementation.
     * @param sslSession the established SSL session
     * @return true if the sslSession is set up with the correct host
     */
    @Override
    public boolean verify(String targetHost, SSLSession sslSession) {
        return peerMatches(sslSession);
    }
}
