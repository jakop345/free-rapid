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

package com.sleepycat.je.rep.elections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.logging.Level;

import com.sleepycat.je.EnvironmentFailureException;
import com.sleepycat.je.JEVersion;
import com.sleepycat.je.rep.elections.Proposer.Proposal;
import com.sleepycat.je.rep.elections.Protocol.Accept;
import com.sleepycat.je.rep.elections.Protocol.Propose;
import com.sleepycat.je.rep.elections.Protocol.Value;
import com.sleepycat.je.rep.impl.TextProtocol.InvalidMessageException;
import com.sleepycat.je.rep.impl.TextProtocol.RequestMessage;
import com.sleepycat.je.rep.impl.TextProtocol.ResponseMessage;
import com.sleepycat.je.rep.impl.node.RepNode;
import com.sleepycat.je.rep.net.DataChannel;
import com.sleepycat.je.rep.utilint.ServiceDispatcher;
import com.sleepycat.je.utilint.LoggerUtils;

/**
 * Plays the role of Acceptor in the consensus algorithm. It runs in its
 * own thread listening for and responding to messages sent by Proposers.
 */
public class Acceptor extends ElectionAgentThread {

    /*
     * The currently promised proposal. Proposals below this one are rejected.
     */
    private Proposal promisedProposal = null;

    private Value acceptedValue = null;

    /* Used to return suggestions in response to Propose requests. */
    private final SuggestionGenerator suggestionGenerator;

    /* Identifies the Acceptor Service. */
    public static final String SERVICE_NAME = "Acceptor";

    private final ElectionsConfig config;

    /**
     * Creates an Acceptor
     */
    public Acceptor(Protocol protocol,
                    ElectionsConfig config,
                    SuggestionGenerator suggestionGenerator) {

        super(config.getRepImpl(), protocol,
              "Acceptor Thread " + config.getNameIdPair().getName());
        this.config = config;

        this.suggestionGenerator = suggestionGenerator;
    }

    /**
     * The Acceptor thread body.
     */
    @Override
    public void run() {
        final ServiceDispatcher serviceDispatcher =
            config.getServiceDispatcher();
        serviceDispatcher.register(SERVICE_NAME, channelQueue);
        LoggerUtils.logMsg
            (logger, envImpl, formatter, Level.FINE, "Acceptor started");
        DataChannel channel = null;
        try {
            while (true) {
                channel = serviceDispatcher.takeChannel
                    (SERVICE_NAME, true /* block */,
                     protocol.getReadTimeout());

                if (channel == null) {
                    /* A soft shutdown. */
                    return;
                }

                BufferedReader in = null;
                PrintWriter out = null;
                try {
                    in = new BufferedReader(
                        new InputStreamReader(
                            Channels.newInputStream(channel)));
                    out = new PrintWriter(
                        Channels.newOutputStream(channel), true);
                    String requestLine = in.readLine();
                    if (requestLine == null) {
                        LoggerUtils.logMsg(logger, envImpl,
                                           formatter, Level.FINE,
                                           "Acceptor: EOF on request");
                        continue;
                    }
                    RequestMessage requestMessage = null;
                    try {
                        requestMessage = protocol.parseRequest(requestLine);
                    } catch (InvalidMessageException e) {
                        LoggerUtils.logMsg(logger, envImpl,
                                           formatter, Level.WARNING,
                                           "Message format error: " +
                                           e.getMessage());
                        out.println
                            (protocol.new ProtocolError(e).wireFormat());
                        continue;
                    }
                    ResponseMessage responseMessage = null;
                    if (requestMessage.getOp() == protocol.PROPOSE) {
                        responseMessage = process((Propose) requestMessage);
                    } else if (requestMessage.getOp() == protocol.ACCEPT) {
                        responseMessage = process((Accept) requestMessage);
                    } else if (requestMessage.getOp() == protocol.SHUTDOWN) {
                        break;
                    } else {
                        LoggerUtils.logMsg(logger, envImpl,
                                           formatter, Level.SEVERE,
                                           "Unrecognized request: " +
                                           requestLine);
                        continue;
                    }

                    /*
                     * The request message may be of an earlier version. If so,
                     * this node transparently read the older version. JE only
                     * throws out InvalidMesageException when the version of
                     * the request message is newer than the current protocol.
                     * To avoid sending a repsonse that the requester cannot
                     * understand, we send a response in the same version as
                     * that of the original request message.
                     */
                    responseMessage.setSendVersion
                        (requestMessage.getSendVersion());
                    out.println(responseMessage.wireFormat());
                } catch (IOException e) {
                    LoggerUtils.logMsg
                        (logger, envImpl, formatter, Level.WARNING,
                         "IO error on socket: " + e.getMessage());
                    continue;
                } finally {
                    Utils.cleanup(logger, envImpl, formatter, channel, in, out);
                    cleanup();
                }
            }
        } catch (InterruptedException e) {
            if (isShutdown()) {
                /* Treat it like a shutdown, exit the thread. */
                return;
            }
            LoggerUtils.logMsg(logger, envImpl, formatter, Level.WARNING,
                               "Acceptor unexpected interrupted");
            throw EnvironmentFailureException.unexpectedException(e);
        } finally {
            serviceDispatcher.cancel(SERVICE_NAME);
            cleanup();
        }
    }

    /**
     * Responds to a Propose request.
     *
     * @param propose the request proposal
     *
     * @return the response: a Promise if the request was accepted, a Reject
     *         otherwise.
     */
    ResponseMessage process(Propose propose) {

        if ((promisedProposal != null) &&
            (promisedProposal.compareTo(propose.getProposal()) > 0)) {
            LoggerUtils.logMsg(logger, envImpl, formatter, Level.FINE,
                               "Reject Propose: " + propose.getProposal() +
                               " Promised proposal: " + promisedProposal);
            return protocol.new Reject(promisedProposal);
        }

        promisedProposal = propose.getProposal();
        final Value suggestedValue = suggestionGenerator.get(promisedProposal);
        final long suggestionRanking =
            suggestionGenerator.getRanking(promisedProposal);
        LoggerUtils.logMsg(logger, envImpl, formatter, Level.FINE,
                           "Promised: " + promisedProposal +
                           " Suggested Value: " + suggestedValue +
                           " Suggestion Ranking: " + suggestionRanking);
        return protocol.new Promise
                (promisedProposal,
                 acceptedValue,
                 suggestedValue,
                 suggestionRanking,
                 config.getElectionPriority(),
                 config.getLogVersion(),
                 JEVersion.CURRENT_VERSION);
    }

    /**
     * Responds to Accept request
     *
     * @param accept the request
     * @return an Accepted or Reject response as appropriate.
     */
    ResponseMessage process(Accept accept) {
        if ((promisedProposal != null) &&
            (promisedProposal.compareTo(accept.getProposal()) != 0)) {
            LoggerUtils.logMsg(logger, envImpl, formatter, Level.FINE,
                               "Reject Accept: " + accept.getProposal() +
                               " Promised proposal: " + promisedProposal);
            return protocol.new Reject(promisedProposal);
        }
        acceptedValue = accept.getValue();
        LoggerUtils.logMsg(logger, envImpl, formatter, Level.FINE,
                           "Promised: " + promisedProposal + " Accepted: " +
                           accept.getProposal() + " Value: " + acceptedValue);
        return protocol.new Accepted(accept.getProposal(), acceptedValue);
    }

    public interface SuggestionGenerator {

        /**
         * Used to generate a suggested value for use by a Proposer. It's a
         * hint.  The proposal argument may be used to freeze values like the
         * VLSN number from advancing (if they were used in the ranking) until
         * an election has completed.
         *
         * @param proposal the Proposal for which the value is being suggested.
         *
         * @return the suggested value.
         */
        abstract Value get(Proposal proposal);

        /**
         * The importance associated with the above suggestion. Acceptors have
         * to agree on a common system for ranking importance so that the
         * relative importance of different suggestions can be meaningfully
         * compared.
         *
         * @param the proposal associated with the ranking
         *
         * @return the importance of the suggestion as a number
         */
        abstract long getRanking(Proposal proposal);
    }
}
