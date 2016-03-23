package cz.vity.freerapid.plugins.services.dlfreefr;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author ntoskrnl,tong2shot
 */
class DlFreeFrFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(DlFreeFrFileRunner.class.getName());

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        checkURL();
        logger.info("runCheck " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            checkProblems();
            checkNameAndSize();
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    @Override
    public void run() throws Exception {
        super.run();
        checkURL();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentType = method.getResponseHeader("Content-Type").getValue().toLowerCase(Locale.ENGLISH);
            if (!contentType.contains("html")) {
                //try downloading directly
                if (tryDownloadAndSaveFile(method)) return;
            }

            checkProblems();
            checkNameAndSize();

            final String file = PlugUtils.getStringBetween(getContentAsString(), "name=\"file\" value=\"", "\"");
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL)
                    .setActionFromFormWhereActionContains("getfile", true)
                    .setParameter("file", file)
                    .toPostMethod();

            if (!tryDownloadAndSaveFile(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException("Error starting download");
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize() throws ErrorDuringDownloadingException {
        final Matcher name = getMatcherAgainstContent("Fichier:?</[^<>]+>([^<>]+?)<");
        if (!name.find()) throw new PluginImplementationException("File name not found");
        httpFile.setFileName(name.group(1).trim());

        //they use 'o' instead of 'b' in French
        final Matcher size = getMatcherAgainstContent("Taille:?</[^<>]+>(.+?)o");
        if (!size.find()) throw new PluginImplementationException("File size not found");
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(size.group(1).trim() + "b"));

        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String content = getContentAsString();
        if (content.contains("Fichier inexistant") || content.contains("Erreur 404") || content.contains("Appel incorrect")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
        if (content.contains("\"disabled\":true")) {
            throw new PluginImplementationException("Server rejected your request");
        }
    }

    private void checkURL() {
        if (!fileURL.contains("getfile.pl")) {
            fileURL = fileURL.replaceAll("dl\\.free\\.fr/.", "dl.free.fr/getfile.pl?file=/"); //yes you read it right, delete 1 char after /
        }
    }

}