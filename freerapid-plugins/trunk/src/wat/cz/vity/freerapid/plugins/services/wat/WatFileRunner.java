package cz.vity.freerapid.plugins.services.wat;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.util.logging.Logger;

/**
 * Class which contains main code
 *
 * @author ntoskrnl
 */
class WatFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(WatFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems(method);
            checkNameAndSize();
        } else {
            checkProblems(method);
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        final String name = PlugUtils.getStringBetween(getContentAsString(), "<strong class=\"c2\" itemprop=\"title\">", "</strong>");
        httpFile.setFileName(name + ".mp4");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        HttpMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems(method);
            checkNameAndSize();
            final String id = PlugUtils.getStringBetween(getContentAsString(), "/swf2/188507nIc0K11", "\"");
            final String url = "/webhd/" + id;
            method = getMethodBuilder()
                    .setAction("/get" + url)
                    .setParameter("token", getToken(url))
                    .setParameter("domain", "www.wat.tv")
                    .setParameter("domain2", "null")
                    .setParameter("revision", "4.1.028")
                    .setParameter("synd", "0")
                    .setParameter("helios", "1")
                    .setParameter("context", "swf2")
                    .setParameter("pub", "1")
                    .setParameter("country", "FR")
                    .setParameter("sitepage", "")
                    .setParameter("lieu", "wat")
                    .setParameter("playerContext", "CONTEXT_WAT")
                    .setParameter("getURL", "1")
                    .setParameter("version", "WIN%202011,1,102,55")
                    .toGetMethod();
            if (makeRedirectedRequest(method)) {
                method = getGetMethod(getContentAsString().trim());
                if (!tryDownloadAndSaveFile(method)) {
                    checkProblems(method);
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            } else {
                checkProblems(method);
                throw new ServiceConnectionProblemException();
            }
        } else {
            checkProblems(method);
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems(final HttpMethod method) throws Exception {
        if ("http://www.wat.tv/".equals(method.getURI().toString())) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String getToken(final String url) throws Exception {
        final String salt = "9b673b13fa4682ed14c3cfa5af5310274b514c4133e9b3a81e6e3aba00912564";
        final String time = String.format("%08x", System.currentTimeMillis() / 1000);
        return DigestUtils.md5Hex(salt + url + time) + "/" + time;
    }

}