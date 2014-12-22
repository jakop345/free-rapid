package cz.vity.freerapid.plugins.services.data;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author Javi
 */
class DataFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(DataFileRunner.class.getName());


    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final HttpMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        final Matcher matchN = PlugUtils.matcher("download_page_filename\".*?>\\s*?.*?\\s*?.*?\\s*?<.+?>(.+?)</div>", content);
        if (!matchN.find()) throw new PluginImplementationException("File name not found");
        httpFile.setFileName(matchN.group(1).trim());
        final Matcher matchS = PlugUtils.matcher("download_page_filesize\".*?>\\s*?.*?\\s*?<.+?>(.+?)</span>", content);
        if (!matchS.find()) throw new PluginImplementationException("File size not found");
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(matchS.group(1).replaceAll("<[^>]*>", "").trim()));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentAsString = getContentAsString();
            checkProblems();
            checkNameAndSize(contentAsString);
            final Matcher matcher = getMatcherAgainstContent("<a.*?href='(.+?)'>Lassú letöltés</a");
            if (matcher.find()) {
                final String downURL = matcher.group(1);
                logger.info("downURL: " + downURL);
                final GetMethod getmethod = getGetMethod(downURL);
                if (!tryDownloadAndSaveFile(getmethod)) {
                    checkProblems();
                    logger.warning(getContentAsString());
                    throw new ServiceConnectionProblemException();
                }
            } else {
                checkProblems();
                logger.warning(getContentAsString());
                throw new PluginImplementationException("Download link not found");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("nem létezik")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (contentAsString.contains("200 MB-nál nagyobb")) {
            throw new NotRecoverableDownloadException("Premium account needed for files >200MB");
        }

    }

}