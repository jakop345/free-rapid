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

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;

import com.sleepycat.je.rep.net.InstanceParams;

/**
 * This is an implementation of SSLAuthenticator which authenticates based
 * on the Distinguished Name (DN) in the SSL peer's certificate.  Matching
 * is done using Java regular expressions against the RFC1779-formatted DN.
 * This is typically used to match against the CN portion of the name.
 */

class SSLDNMatcher {

    private final Pattern pattern;

    /**
     * Construct an SSLDNMatcher
     *
     * @param params The instantiation params.  The classParams must be
     * a pattern to be matched to a Distinguished Name in an SSL certificate.
     * The match pattern must be a valid Java regular expression.
     * @throws IllegalArgumentException if the pattern is not a valid
     * regular expression
     */
    SSLDNMatcher(InstanceParams params)
        throws IllegalArgumentException {

        this.pattern = compileRegex(params.getClassParams());
    }

    /*
     * Check whether the peer certificate matches the configured expression.
     */
    public boolean peerMatches(SSLSession sslSession) {
        Principal principal = null;
        try {
            principal = sslSession.getPeerPrincipal();
        } catch (SSLPeerUnverifiedException pue) {
            return false;
        }

        if (principal != null) {
            if (principal instanceof X500Principal) {
                final X500Principal x500Principal = (X500Principal) principal;
                final String name =
                    x500Principal.getName(X500Principal.RFC1779);
                final Matcher m = pattern.matcher(name);
                if (m.matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Pattern compileRegex(String regex)
        throws IllegalArgumentException {
        try {
            return Pattern.compile(regex);
        } catch(PatternSyntaxException pse) {
            throw new IllegalArgumentException(
                "pattern is invalid", pse);
        }
    }

    static void validateRegex(String regex)
        throws IllegalArgumentException {

        /* ignore the result */
        compileRegex(regex);
    }
}


