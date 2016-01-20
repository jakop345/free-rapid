package cz.vity.freerapid.plugins.services.yandexdisk;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import jlibs.core.net.URLUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.net.URLDecoder;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author tong2shot
 */
class YandexDiskFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(YandexDiskFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            fileURL = getMethod.getURI().toString();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "\"og:title\" content=\"", "\"");
        httpFile.setFileName(PlugUtils.unescapeHtml(httpFile.getFileName()));
        String filesize;
        try {
            filesize = PlugUtils.getStringBetween(content, "Размер:</span>", "<").replace("М", "M").replace("Б", "B");
        } catch (PluginImplementationException e) {
            throw new PluginImplementationException("File size not found");
        }
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(filesize));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            fileURL = method.getURI().toString();
            String mainPageContent = getContentAsString();
            checkNameAndSize(mainPageContent);

            String fileId = PlugUtils.getStringBetween(getContentAsString(), "\"id\":\"", "\"");
            String sk = PlugUtils.getStringBetween(getContentAsString(), "\"sk\":\"", "\"");
            String clientId = generateClientId();
            HttpMethod httpMethod = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction("https://yadi.sk/models/?_m=do-get-resource-url") //https is mandatory
                    .setParameter("idClient", clientId)
                    .setParameter("version", "3.1.1")
                    .setParameter("sk", sk)
                    .setParameter("_model.0", "do-get-resource-url")
                    .setParameter("id.0", fileId)
                    .setAjax()
                    .toPostMethod();
            httpMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }

            final String downloadURL;
            String contentType = null;
            try {
                downloadURL = PlugUtils.replaceEntities(PlugUtils.getStringBetween(getContentAsString(), "\"file\":\"", "\""));
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Error getting download URL");
            }
            logger.info("Download URL: " + downloadURL);
            try {
                contentType = URLDecoder.decode(URLUtil.getQueryParams(downloadURL, "UTF-8").get("content_type"), "UTF-8");
            } catch (Exception e) {
                //
            }
            if (contentType != null) {
                logger.info("Content type: " + contentType);
                setFileStreamContentTypes(contentType);
            }

            httpMethod = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(downloadURL)
                    .toGetMethod();
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
        if (contentAsString.contains("was removed or not found")
                || contentAsString.contains("could not be found")
                || contentAsString.contains("bad formed path")
                || contentAsString.contains("resource not found")
                || contentAsString.contains("may have deleted file")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String generateClientId() {
        byte[] bytes = new byte[16];
        new Random().nextBytes(bytes);
        return new String(Hex.encodeHex(bytes));
    }
}