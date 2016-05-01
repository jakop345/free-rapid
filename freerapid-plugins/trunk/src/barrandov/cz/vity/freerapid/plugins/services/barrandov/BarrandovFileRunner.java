package cz.vity.freerapid.plugins.services.barrandov;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class which contains main code
 *
 * @author JPEXS
 */
class BarrandovFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(BarrandovFileRunner.class.getName());
    private BarrandovSettingsConfig config;

    @Override
    public void runCheck() throws Exception { //this method validates file
        super.runCheck();
        checkNameAndSize();
    }
    private String videoUrl;

    private void setConfig() throws Exception {
        BarrandovServiceImpl service = (BarrandovServiceImpl) getPluginService();
        config = service.getConfig();
    }

    private void checkNameAndSize() throws Exception {
        setConfig();
        Matcher m = Pattern.compile(".*barrandov\\.tv/(?:video/)?([0-9]+)\\-.*").matcher(fileURL);
        if (!m.matches()) {
            throw new PluginImplementationException("Bad link format");
        }
        if (!makeRedirectedRequest(getGetMethod(fileURL))) {
            throw new ServiceConnectionProblemException("Cannot load video data");
        }
        String quality;
        int setting = config.getQualitySetting();
        if (setting == 1) {
            logger.info("Selected HD quality");
            quality = "720p HD";
        } else {
            logger.info("Selected SD quality");
            quality = "360p SD";
        }
        final Matcher match = PlugUtils.matcher("file\\s*:\\s*\"(.+?)\",\\s*label\\s*:\\s*\"" + quality + "\"", getContentAsString());
        if (!match.find())
            throw new PluginImplementationException("Video url not found");
        videoUrl = match.group(1);
        httpFile.setFileName(videoUrl.substring(1 + videoUrl.lastIndexOf("/")));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        checkNameAndSize();
        GetMethod gm = new GetMethod(videoUrl);
        if (!tryDownloadAndSaveFile(gm)) {
            checkProblems();//if downloading failed
            logger.warning(getContentAsString());//log the info
            throw new PluginImplementationException();//some unknown problem
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
    }
}
