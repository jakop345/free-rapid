package cz.vity.freerapid.plugins.services.fileboom;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class FileBoomFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(FileBoomFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("Download file:</div>\\s*?<[^<>]*?>\\s*?<i[^<>]*?></i>\\s*?(.+?)\\s*?</div>", content);
        if (!match.find())
            throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "File size:", "<");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String content = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(content);//extract file name and size from the page
            final HttpMethod aMethod = getMethodBuilder()
                    .setReferer(fileURL).setAction(fileURL)
                    .setParameter("slow_id", PlugUtils.getStringBetween(content, "data-slow-id=\"", "\""))
                    .toPostMethod();
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            String dlLink;
            if (!getContentAsString().contains("window.location.href")) {
                final String uniqueId = PlugUtils.getStringBetween(getContentAsString(), "uniqueId\" value=\"", "\"");
                do {
                    final HttpMethod bMethod = doCaptcha(getMethodBuilder()
                            .setReferer(fileURL).setAction(fileURL)
                            .setParameter("free", "1")
                            .setParameter("freeDownloadRequest", "1")
                            .setParameter("uniqueId", uniqueId)
                    ).toPostMethod();
                    if (!makeRedirectedRequest(bMethod)) {
                        checkProblems();
                        throw new ServiceConnectionProblemException();
                    }
                    checkProblems();
                } while (getContentAsString().contains("The verification code is incorrect"));

                downloadTask.sleep(1 + PlugUtils.getNumberBetween(getContentAsString(), "tik-tak\">", "</"));
                final HttpMethod cMethod = getMethodBuilder()
                        .setReferer(fileURL).setAction(fileURL)
                        .setParameter("free", "1")
                        .setParameter("uniqueId", uniqueId)
                        .toPostMethod();
                if (!makeRedirectedRequest(cMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException();
                }
                checkProblems();

                dlLink = getMethodBuilder().setReferer(fileURL).setActionFromAHrefWhereATagContains("this").getEscapedURI();
            }
            else {
                dlLink = getMethodBuilder().setReferer(fileURL).setActionFromTextBetween("window.location.href = '", "'").getEscapedURI();
            }
            final HttpMethod httpMethod = getGetMethod(dlLink);

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
        final String content = getContentAsString();
        if (content.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (content.contains("Downloading is not possible")) {
            int time = 60 * 60; // 1 hour
            final Matcher match = PlugUtils.matcher("(\\d+):(\\d+):(\\d+)", content);
            if (match.find()) {
                int hour = new Integer(match.group(1));
                int mins = new Integer(match.group(2));
                int secs = new Integer(match.group(3));
                time = (((hour * 60) + mins) * 60) + secs;
            }
            throw new YouHaveToWaitException("You have to wait before download ", time);
        }

    }

    private MethodBuilder doCaptcha(final MethodBuilder builder) throws Exception {
        final HttpMethod newCaptcha = getMethodBuilder().setReferer(fileURL).setAction("/file/captcha.html?refresh=1").setAjax().toGetMethod();
        if (!makeRedirectedRequest(newCaptcha)) {
            throw new ServiceConnectionProblemException();
        }
        final String captchaSrc = getMethodBuilder().setReferer(fileURL).setAction(PlugUtils.getStringBetween(getContentAsString(), "url\":\"", "\"").replace("\\", "")).getEscapedURI();
        final String captchaTxt = getCaptchaSupport().getCaptcha(captchaSrc);
        if (captchaTxt == null)
            throw new CaptchaEntryInputMismatchException();
        return builder.setParameter("CaptchaForm[code]", captchaTxt);
    }
}