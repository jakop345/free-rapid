package cz.vity.freerapid.plugins.services.onefichier;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class OneFichierFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(OneFichierFileRunner.class.getName());
    private final static String loginUrl = "https://1fichier.com/en/login.pl";

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        setEnglishURL();
        final GetMethod getMethod = getGetMethod(fileURL);//make first request
        final int status = client.makeRequest(getMethod, false);
        if (status / 100 == 3) {
            getAltTempFileName();
        } else if (status == 200) {
            checkProblems();
            try {
                checkNameAndSize(getContentAsString());
            } catch (Exception e) {
                getAltTempFileName();
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void getAltTempFileName() throws Exception {
        final Matcher match = PlugUtils.matcher("https?://(\\w+)\\.(1fichier|desfichiers)\\.com/en/?(.*)", fileURL);
        if (match.find()) {
            String name = match.group(1);
            if (URLDecoder.decode(match.group(3), "UTF-8").replace("\"", "").trim().length() > 0)
                name = URLDecoder.decode(match.group(3), "UTF-8").replace("\"", "").trim();
            httpFile.setFileName(name);
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void setEnglishURL() {
        fileURL = fileURL.replaceFirst("https?://", "https://");
        if (!fileURL.contains("/en")) {
            String[] temp = fileURL.split(".com");
            fileURL = temp[0] + ".com/en";
            if (temp.length > 1) fileURL += temp[1];
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        if (fileURL.contains("/dir/")) {
            PlugUtils.checkName(httpFile, content, "box_header\">", "<");
        } else {
            final Matcher match = PlugUtils.matcher("ame\\s*?:\\s*?</t.>\\s*?<t.*?>(.+?)<", getContentAsString());
            if (!match.find()) throw new PluginImplementationException("File name not found");
            httpFile.setFileName(match.group(1).trim());
            final Matcher matchS = PlugUtils.matcher("Size\\s*?:\\s*?</t.>\\s*?<t.*?>(.+?)<", getContentAsString());
            if (!matchS.find()) throw new PluginImplementationException("File size not found");
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(matchS.group(1).trim()));
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        setEnglishURL();
        login();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        final int status1 = client.makeRequest(method, false);
        if (status1 / 100 == 3) {
            final String dlLink = method.getResponseHeader("Location").getValue();
            httpFile.setFileName(URLDecoder.decode(dlLink.substring(1 + dlLink.lastIndexOf("/")), "UTF-8"));
            if (!tryDownloadAndSaveFile(method)) {
                checkDownloadProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else if (status1 == 200) {
            if (fileURL.contains("/dir/")) {
                List<URI> list = new LinkedList<URI>();
                final Matcher match = PlugUtils.matcher("<a href=\"(https?://(1fichier|desfichiers)\\.com/\\?.+?)\"", getContentAsString());
                while (match.find()) {
                    list.add(new URI(match.group(1).trim()));
                }
                if (list.isEmpty()) throw new PluginImplementationException("No links found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName("Link(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
            } else {
                checkDownloadProblems();//check problems
                checkNameAndSize(getContentAsString());//extract file name and size from the page

                HttpMethod hMethod;
                int loopCount = 0;
                while (!getContentAsString().contains("Click here to download")) {
                    if (loopCount++ > 10)
                        throw new ServiceConnectionProblemException("Error accessing download link");
                    hMethod = getMethodBuilder().setReferer(fileURL).setActionFromFormWhereTagContains("ownload", true).toPostMethod();
                    if (!makeRedirectedRequest(hMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                }
                hMethod = getMethodBuilder().setActionFromAHrefWhereATagContains("Click here to download").toGetMethod();
                if (!tryDownloadAndSaveFile(hMethod)) {
                    checkDownloadProblems();//if downloading failed
                    throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
                }
            }
        } else {
            checkDownloadProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("file could not be found") ||
                contentAsString.contains("The requested file has been deleted")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (contentAsString.contains("you can download only one file at a timeg")) {
            int delay = 15;
            try {
                delay = PlugUtils.getNumberBetween(contentAsString, "wait up to", "minute");
            } catch (Exception e) {/**/}
            throw new YouHaveToWaitException("You can download only one file at a time and you must wait up to " + delay + " minutes between each downloads", delay * 60);
        }
    }

    private void checkDownloadProblems() throws ErrorDuringDownloadingException {
        checkProblems();
        if (getContentAsString().contains("you must wait between downloads")) {
            final Matcher waitMatch  = PlugUtils.matcher("You must wait (\\d+) (.+?)[<\\.]", getContentAsString());
            if (waitMatch.find()) {
                int time = Integer.parseInt(waitMatch.group(1));
                if (waitMatch.group(2).contains("minute"))
                    time = time * 60;
                throw new YouHaveToWaitException("You must wait between downloads (" + waitMatch.group(1) + " " + waitMatch.group(2) + ")", time);
        }   }

    }

    private void login() throws Exception {
        synchronized (OneFichierFileRunner.class) {
            OneFichierServiceImpl service = (OneFichierServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (pa.isSet()) {
                final HttpMethod method = getMethodBuilder()
                        .setAction(loginUrl).setReferer(loginUrl)
                        .setParameter("mail", pa.getUsername())
                        .setParameter("pass", pa.getPassword())
                        .setParameter("valider", "Send")
                        .toPostMethod();
                if (!makeRedirectedRequest(method)) {
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                if (getContentAsString().contains("Invalid username or password") ||
                        getContentAsString().contains("Invalid email address") ||
                        getContentAsString().contains("Invalid password")) {
                    throw new BadLoginException("Invalid 1Fichier account login information!");
                }
                else logger.info("Logged in.");
            }
            else logger.info("No account details");
        }
    }

}