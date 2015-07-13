package cz.vity.freerapid.plugins.services.minhateca;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
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
class MinhatecaFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(MinhatecaFileRunner.class.getName());

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
        final Matcher match = PlugUtils.matcher("downloadFileFid\">\\s*?.+?>(.+?)</", content);
        if (!match.find()) throw new PluginImplementationException("File name not found");
        httpFile.setFileName(match.group(1).trim());
        PlugUtils.checkFileSize(httpFile, content, "fileSize\">", "</");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            doLogin();
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            final Matcher match = PlugUtils.matcher("name=\"FileId\"[^<>]*?value=\"(.+?)\"", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("'FileId' not found");
            final String fileID = match.group(1).trim();
            HttpMethod httpMethod = getMethodBuilder(contentAsString)
                    .setAction("http://minhateca.com.br/action/License/Download")
                    .setReferer(fileURL).setAjax()
                    .setParameter("fileId", fileID)
                    .setParameter("__RequestVerificationToken", getReqVerToken(contentAsString))
                    .toPostMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            if (getContentAsString().contains("title=\\\"Cadastrar\\\"")) {
                final Matcher errMatch = PlugUtils.matcher("(?s)title=\"Cadastrar\">(.+?)</strong>", PlugUtils.unescapeUnicode(getContentAsString()));
                String errMsg = "";
                if (errMatch.find())
                    errMsg = errMatch.group(1).replaceAll("<.+?>", "").replaceAll("\\s+", " ");
                throw new ErrorDuringDownloadingException("Registration required. " + errMsg);
            }
            final HttpMethod downloadMethod = getMethodBuilder()
                    .setReferer(fileURL)
                    .setAction(PlugUtils.unescapeUnicode(PlugUtils.getStringBetween(getContentAsString(), "Url\":\"", "\"")))
                    .toGetMethod();
            setFileStreamContentTypes("text/multipart");
            setClientParameter(DownloadClientConsts.NO_CONTENT_LENGTH_AVAILABLE, true);  //not always available
            if (!tryDownloadAndSaveFile(downloadMethod)) {
                checkProblems();//if downloading failed
                throw new ServiceConnectionProblemException("Error starting download");//some unknown problem
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private String getReqVerToken(final String content) throws PluginImplementationException{
        final Matcher match = PlugUtils.matcher("name=\"__RequestVerificationToken\"[^<>]*?value=\"(.+?)\"", content);
        if (!match.find())
            throw new PluginImplementationException("'RequestVerificationToken' not found");
        return match.group(1).trim();
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("File Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
        if (contentAsString.contains("Arquivamento indisponível")) {
            throw new ServiceConnectionProblemException("Download is currently unavailable ");
        }

    }

    private boolean doLogin() throws Exception {
        synchronized (MinhatecaFileRunner.class) {
            MinhatecaServiceImpl service = (MinhatecaServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (pa.isSet()) {
                final HttpMethod method = getMethodBuilder().setAjax()
                        .setAction("http://minhateca.com.br/action/login/login")
                        .setParameter("FileId", "0")
                        .setParameter("Login", pa.getUsername())
                        .setParameter("Password", pa.getPassword())
                        .setParameter("__RequestVerificationToken", getReqVerToken(getContentAsString()))
                        .toPostMethod();
                if (!makeRedirectedRequest(method)) {
                    throw new ServiceConnectionProblemException("Error posting login info");
                }
                String content = PlugUtils.unescapeUnicode(getContentAsString());
                if (content.contains("Connta com este nome n&#227;o existe") ||
                        content.contains("Certifique se que indicou o nome correto") ||
                        content.contains("A senha indicada n&#227;o &#233; a senha correcta")) {
                    throw new BadLoginException("Invalid Minhateca account login information!");
                }
                logger.info("Logged in.!");
                return true;
            }
            else logger.info("No account details.");
        }
        return false;
    }

}