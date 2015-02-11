package cz.vity.freerapid.plugins.services.solidfiles;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class SolidFilesFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(SolidFilesFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "<h2 title=\"", "\"");
        final Matcher match = PlugUtils.matcher("File size</dt>\\s*?<dd>(.+?)</dd>", content);
        if (!match.find()) throw new PluginImplementationException("File size not found");
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1)));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        doLogin();
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);
            String name = httpFile.getFileName();
            if (name.contains(" "))
                name = name.substring(name.lastIndexOf(" ") +1);
            final Matcher match = PlugUtils.matcher("<a[^<>]+?href=\"(http.+?" + Pattern.quote(name) + ")\"", contentAsString);
            if (!match.find()) {
                throw new PluginImplementationException("Download link not found");
            }
            final HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(match.group(1)).toGetMethod();
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("We couldn't find the file you requested")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private void doLogin() throws Exception {
        synchronized (SolidFilesFileRunner.class) {
            SolidFilesServiceImpl service = (SolidFilesServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (pa.isSet()) {
                if (!makeRedirectedRequest(getGetMethod("https://www.solidfiles.com/login/"))) {
                    throw new ServiceConnectionProblemException("Login page unavailable");
                }
                final HttpMethod method = getMethodBuilder()
                        .setActionFromFormWhereActionContains("login", true)
                        .setParameter("username", pa.getUsername())
                        .setParameter("password", pa.getPassword())
                        .toPostMethod();
                if (!makeRedirectedRequest(method)) {
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                if (getContentAsString().contains("enter a correct username and password")) {
                    throw new BadLoginException("Invalid SolidFiles account login information!");
                }
                logger.info("Logged in.");
            }
        }
    }

}