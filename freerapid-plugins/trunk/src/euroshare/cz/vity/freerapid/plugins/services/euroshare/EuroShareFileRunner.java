package cz.vity.freerapid.plugins.services.euroshare;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class EuroShareFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(EuroShareFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        setCookie();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void setCookie() {
        addCookie(new Cookie("euroshare.eu", "lang", "sk", "/", 86400, false));
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        Matcher match = PlugUtils.matcher("fileName\\s*:\\s*'(.+?)'", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim());
        if (fileURL.contains("/folder/")) {
            httpFile.setFileName("Folder > " + httpFile.getFileName());
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        setCookie();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            if (fileURL.contains("/folder/")) {
                final List<URI> list = new LinkedList<URI>();
                final Matcher matcher = getMatcherAgainstContent("href=\"([^\"]+?)\" class=\"blank\" target=\"_blank\"");
                 while (matcher.find()) {
                    final String link = getMethodBuilder().setReferer(fileURL).setAction(matcher.group(1).trim()).getEscapedURI();
                    list.add(new URI(link));
                }
                // add urls to queue
                if (list.isEmpty()) throw new PluginImplementationException("No links found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName("Link(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
            }
            else {
                HttpMethod getMethod = getMethodBuilder().setReferer(fileURL)
                        .setAction(fileURL + "?download=true").toGetMethod();
                if (!makeRedirectedRequest(getMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();
                HttpMethod httpMethod;
                try {
                    httpMethod = getMethodBuilder().setReferer(fileURL)
                            .setActionFromAHrefWhereATagContains("DOWNLOAD WITHOUT REGISTRATION").toHttpMethod();
                } catch (Exception x) {
                    httpMethod = getMethodBuilder().setReferer(fileURL)
                            .setActionFromAHrefWhereATagContains("STIAHNUŤ BEZ REGISTRACIE").toHttpMethod();
                }
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    checkProblems();//if downloading failed
                    throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Požadovaný súbor sa na serveri nenachádza alebo bol odstránený") ||
                contentAsString.contains(";File has been removed") ||
                contentAsString.contains("súbor bol odstránený") ||
                contentAsString.contains("Súbor neexistuje")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (contentAsString.contains("Server overloaded. Use PREMIUM downloading")) {
            throw new YouHaveToWaitException("Server overloaded. Use PREMIUM downloading", 300); //let to know user in FRD
        }
        if (contentAsString.contains("Z Vasej IP uz prebieha stahovanie") ||
                contentAsString.contains("Ako free uzivatel mozete stahovat iba jeden subor")) {
            throw new ServiceConnectionProblemException("Free users can only download one file at a time"); //let to know user in FRD
        }
    }

}