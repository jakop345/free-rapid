package cz.vity.freerapid.plugins.services.muchshare;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author tong2shot
 */
class MuchShareFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(MuchShareFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        if (makeRedirectedRequest(getMethod)) {
            checkFileProblems();
            checkNameAndSize(getContentAsString());//ok let's extract file name and size from the page
        } else {
            checkFileProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "Filename:</b></td><td nowrap>", "</td></tr>");
        PlugUtils.checkFileSize(httpFile, content, "Size:</b></td><td>", "<small>");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private boolean login() throws Exception {
        synchronized (MuchShareFileRunner.class) {
            MuchShareServiceImpl service = (MuchShareServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();

            //for testing purpose
            //pa.setPassword("freerapid");
            //pa.setUsername("freerapid");

            if (pa == null || !pa.isSet()) {
                logger.info("No account data set, skipping login");
                return false;
            }

            final HttpMethod httpMethod = getMethodBuilder()
                    .setAction("http://muchshare.net/login.html")
                    .setParameter("op", "login")
                    .setParameter("redirect", "")
                    .setParameter("login", pa.getUsername())
                    .setParameter("password", pa.getPassword())
                    .setParameter("submit", "")
                    .toPostMethod();
            addCookie(new Cookie(".muchshare.net", "login", pa.getUsername(), "/", null, false));
            addCookie(new Cookie(".muchshare.net", "xfss", "", "/", null, false));
            if (!makeRedirectedRequest(httpMethod))
                throw new ServiceConnectionProblemException("Error posting login info");
            if (getContentAsString().contains("Incorrect Login or Password"))
                throw new NotRecoverableDownloadException("Invalid MuchShare registered account login information!");

            return true;
        }
    }

    private boolean isPassworded() {
        boolean passworded = getContentAsString().contains("<input type=\"password\" name=\"password\" class=\"myForm\">");
        return passworded;
    }

    @Override
    public void run() throws Exception {
        super.run();
        login();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkFileProblems();//check problems
            checkDownloadProblems();
            checkNameAndSize(contentAsString);//extract file name and size from the page

            //process wait time
            String waitTimeRule = "id=\"countdown_str\".*?<span id=\".*?\">.*?(\\d+).*?</span";
            Matcher waitTimematcher = PlugUtils.matcher(waitTimeRule, getContentAsString());
            if (waitTimematcher.find()) {
                downloadTask.sleep(Integer.parseInt(waitTimematcher.group(1)));
            }

            final MethodBuilder methodBuilder = getMethodBuilder()
                    .setActionFromFormByName("F1", true)
                    .setAction(fileURL)
                    .removeParameter("method_premium");

            if (isPassworded()) {
                final String password = getDialogSupport().askForPassword("MuchShare");
                if (password == null) {
                    throw new NotRecoverableDownloadException("This file is secured with a password");
                }
                methodBuilder.setParameter("password", password);
            }

            final HttpMethod httpMethod = methodBuilder.toPostMethod();

            //here is the download link extraction
            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkDownloadProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkFileProblems();
            checkDownloadProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkFileProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

    private void checkDownloadProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("This file reached max downloads limit")) {
            throw new ServiceConnectionProblemException("This file reached max downloads limit");
        }

        if (contentAsString.contains("You can download files up to")) {
            throw new NotRecoverableDownloadException("Need premium account for files bigger than 500 Mb"); //let to know user in FRD
        }
        if (contentAsString.contains("Undefined subroutine")) {
            throw new PluginImplementationException("Server problem");
        }
        if (contentAsString.contains("You have to wait")) {
            int xMinutes = 0;
            int xSeconds = 0;
            int waittime;
            if (contentAsString.contains("minute")) {
                logger.info("Minutes WAIT!!!");

                Matcher matcher = PlugUtils.matcher("([0-9]+) minute(?:s)?, ([0-9]+) seconds", contentAsString);
                if (matcher.find()) {
                    xMinutes = new Integer(matcher.group(1));
                    xSeconds = new Integer(matcher.group(2));
                }
            } else {
                Matcher matcher = PlugUtils.matcher("([0-9]+) seconds", contentAsString);
                if (matcher.find()) xSeconds = new Integer(matcher.group(1));
            }
            waittime = xMinutes * 60 + xSeconds;
            throw new YouHaveToWaitException("You have to wait " + waittime + " seconds", waittime);

        }
    }

}