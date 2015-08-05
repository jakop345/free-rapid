package cz.vity.freerapid.plugins.services.majorgeeks;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class MajorGeeksFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(MajorGeeksFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getParentContent(getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        Matcher matcher = PlugUtils.matcher("<h1>\\s*(?:<[^<>]+?>)?(.+\\s*.*)\\s*</h1>\\s*<hr", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("File name not found");
        }
        String filename = matcher.group(1).replaceAll("<[^<>]*>", "").replaceAll("\\s+", " ").trim();
        matcher = PlugUtils.matcher("fileSize\" content=\"(.+?)\"", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("File size not found");
        }
        long filesize = PlugUtils.getFileSizeFromString(matcher.group(1));
        httpFile.setFileName(filename);
        httpFile.setFileSize(filesize);
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentAsString = getContentAsString();
            checkProblems();
            checkNameAndSize(getParentContent(contentAsString));
            final HttpMethod httpMethod = getMethodBuilder(contentAsString).setReferer(fileURL).setActionFromAHrefWhereATagContains("Click here if it").toHttpMethod();

            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("file does not exist")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String getParentContent(String content) throws Exception {
        Matcher matcher = PlugUtils.matcher("<a [^<>]*?href=[\"']([^\"']*?files/details/[^\"']+?)[\"']", content);
        if (!matcher.find()) {
            throw new PluginImplementationException("Parent URL not found");
        }
        String parentUrlPath = matcher.group(1);
        String baseUrl = getBaseURL() + (getBaseURL().endsWith("/") ? "" : "/");
        String parentUriString = new URI(baseUrl).resolve(parentUrlPath).toString();
        if (!makeRedirectedRequest(getGetMethod(parentUriString))) {
            checkProblems();
            throw new ServiceConnectionProblemException("Error requesting parent page");
        }
        checkProblems();
        return getContentAsString();
    }

    @Override
    protected boolean tryDownloadAndSaveFile(HttpMethod method) throws Exception {
        Header locationHeader;
        String action = method.getURI().toString();
        do {
            final HttpMethod method2 = getMethodBuilder().setReferer(fileURL).setAction(action).toGetMethod();
            processHttpMethod(method2);
            if (method2.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new URLNotAvailableAnymoreException("File not found");
            }
            locationHeader = method2.getResponseHeader("Location");
            if (locationHeader != null) {
                action = locationHeader.getValue();
            }
            fileURL = method2.getURI().toString();
            method2.abort();
            method2.releaseConnection();
        } while (locationHeader != null);
        method = getMethodBuilder().setReferer(fileURL).setAction(action).toGetMethod();
        httpFile.setFileName(PlugUtils.suggestFilename(method.getURI().toString()));
        return super.tryDownloadAndSaveFile(method);
    }

    private void processHttpMethod(HttpMethod method) throws IOException {
        if (client.getHTTPClient().getHostConfiguration().getProtocol() != null) {
            client.getHTTPClient().getHostConfiguration().setHost(method.getURI().getHost(), 80, client.getHTTPClient().getHostConfiguration().getProtocol());
        }
        client.getHTTPClient().executeMethod(method);
    }

}
