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
 * This is an implementation of SSLAuthenticator that authenticates based on
 * the certificate of the client matching the certificate that we would use when
 * operating as a client.
 */

public class SSLMirrorAuthenticator
    extends SSLMirrorMatcher
    implements SSLAuthenticator {

    /**
     * Construct an SSLMirrorAuthenticator
     *
     * @param params the instantiation parameters.
     * @throws IllegalArgumentException if the instance cannot be created due
     * to a problem related to the input parameters
     */
    public SSLMirrorAuthenticator(InstanceParams params)
        throws IllegalArgumentException {

        super(params, false);
    }

    /*
     * Checks whether the peer should be trusted based on the information in
     * the SSLSession object.  This should be called only after the SSL
     * handshake has completed.
     */
    @Override
    public boolean isTrusted(SSLSession sslSession) {
        return peerMatches(sslSession);
    }
}
