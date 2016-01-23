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

import java.security.PrivateKey;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.net.Socket;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;

/**
 * An implementation of X509ExtendedKeyManager which delegates most operations
 * to an underlying implementation, but which supports explicit selection of
 * alias.
 */
public class AliasKeyManager extends X509ExtendedKeyManager {

    private final X509ExtendedKeyManager delegateKeyManager;
    private final String serverAlias;
    private final String clientAlias;

    /**
     * Constructor.
     * @param delegateKeyManager the underlying key manager to fulfill key
     * retrieval requests
     * @param serverAlias the alias to return for server context requests
     * @param clientAlias the alias to return for client context requests
     */
    public AliasKeyManager(X509ExtendedKeyManager delegateKeyManager,
                           String serverAlias,
                           String clientAlias) {
        this.delegateKeyManager = delegateKeyManager;
        this.serverAlias = serverAlias;
        this.clientAlias = clientAlias;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
    	return delegateKeyManager.getClientAliases(keyType, issuers);
    }

    @Override
    public String chooseClientAlias(
        String[] keyType, Principal[] issuers, Socket socket) {
        if (clientAlias != null) {
            return clientAlias;
        }

        return delegateKeyManager.chooseClientAlias(keyType, issuers, socket);
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return delegateKeyManager.getServerAliases(keyType, issuers);
    }

    @Override
    public String chooseServerAlias(
        String keyType, Principal[] issuers, Socket socket) {

        if (serverAlias != null) {
            return serverAlias;
        }

        return delegateKeyManager.chooseServerAlias(keyType, issuers, socket);
    }

    @Override
    public X509Certificate[] getCertificateChain(String alias) {
        return delegateKeyManager.getCertificateChain(alias);
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return delegateKeyManager.getPrivateKey(alias);
    }

    @Override
    public String chooseEngineClientAlias(String[] keyType,
                                          Principal[] issuers,
                                          SSLEngine engine) {
        if (clientAlias != null) {
            return clientAlias;
        }
        return delegateKeyManager.
            chooseEngineClientAlias(keyType, issuers, engine);
    }

    @Override
    public String chooseEngineServerAlias(String keyType,
                                          Principal[] issuers,
                                          SSLEngine engine) {
        if (serverAlias != null) {
            return serverAlias;
        }
        return delegateKeyManager.
            chooseEngineServerAlias(keyType, issuers, engine);
    }
}
