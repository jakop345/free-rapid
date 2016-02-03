package cz.vity.freerapid.plugins.services.sendfile;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
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
 * @author birchie
 */
class SendFileFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(SendFileFileRunner.class.getName());

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
        Matcher match = PlugUtils.matcher("Название</td>\\s*<td>(.+?)</td>", content);
        if (!match.find()) throw new PluginImplementationException("file name not found");
        httpFile.setFileName(match.group(1).trim());
        match = PlugUtils.matcher("\\((\\d+?)B\\)", content);
        if (!match.find()) throw new PluginImplementationException("file size not found");
        httpFile.setFileSize(PlugUtils.getFileSizeFromString(match.group(1).trim()));
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL); //create GET request
        if (makeRedirectedRequest(method)) { //we make the main request
            final String contentAsString = getContentAsString();//check for response
            checkProblems();//check problems
            checkNameAndSize(contentAsString);//extract file name and size from the page
            Matcher match = PlugUtils.matcher("server_id\\s*=\\s*(.+?)[;\"]", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("server id not found");
            final String server_id = match.group(1).trim();
            match = PlugUtils.matcher("file_id\\s*=\\s*(.+?)[;\"]", contentAsString);
            if (!match.find())
                throw new PluginImplementationException("file id not found");
            final String file_id = match.group(1).trim();

            HttpMethod aMethod = getMethodBuilder().setReferer(fileURL)
                    .setAction("get_download_link.php")
                    .setParameter("file_id", file_id)
                    .setAjax().toPostMethod();
            if (!makeRedirectedRequest(aMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();
            final String download_link = "http://s" + server_id + ".sendfile.su/download/" + file_id + "/" + getContentAsString().trim();
            final HttpMethod httpMethod = getGetMethod(download_link);
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
        if (contentAsString.contains("File Not Found") || contentAsString.contains("Файл не найден")) {
            throw new URLNotAvailableAnymoreException("File not found"); //let to know user in FRD
        }
    }

}