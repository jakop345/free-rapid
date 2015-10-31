package cz.vity.freerapid.plugins.services.sharerapid;

import cz.vity.freerapid.plugins.exceptions.NotRecoverableDownloadException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Jan Smejkal (edit from CZshare profi to RapidShare)
 * @ edit František Musil (lister@gamesplit.cz, repair multidownload)
 */
class ShareRapidRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(ShareRapidRunner.class.getName());
    //time to next check (seconds)
    private final static Integer timeToCheck = 120;
    private final static Integer maxReconnect = 30;

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new PluginImplementationException();
        }
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);

        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkNameAndSize(getContentAsString());

            client.setReferer(fileURL);
            String serverURL = "http://" + getMethod.getURI().getHost();

            Login(serverURL);

            Matcher matcher = PlugUtils.matcher("(?:<h1>|<div class=\"download_button_group\">|<div style=\"margin: 30px 0 10px 30px;\">|<span style=\"padding: 12px 0px 0px 10px; display: block\">)\\s*?<a href=\"([^\"]+)\" title=\"[^\"]+\">.+?</a>", getContentAsString());
            Matcher matcher2 = PlugUtils.matcher("<a href=\"(.+?)\" class=\".*?download-button.*?\">.*?Stáhnout soubor.*?</a>", getContentAsString());
            boolean match2 = matcher2.find();
            if (matcher.find() || match2) {
                String downURL;
                if (match2)
                    downURL = matcher2.group(1);
                else
                    downURL = matcher.group(1);
                if (!downURL.contains("http://"))
                    downURL = serverURL + downURL;
                for (int i = 0; i <= maxReconnect; i++) {
                    final GetMethod method = getGetMethod(downURL);

                    httpFile.setState(DownloadState.GETTING);
                    if (tryDownloadAndSaveFile(method))
                        return;
                    if (!getContentAsString().equals(""))
                        checkProblems();
logger.info("###############"+getContentAsString()+"################");
                    downloadTask.sleep(timeToCheck);

                    /*
                    if (!tryDownloadAndSaveFile(method)) {
                        if(getContentAsString().equals(""))
                            throw new NotRecoverableDownloadException("No credit for download this file!");
                        checkProblems();
                        logger.info(getContentAsString());
                        throw new PluginImplementationException();
                    }
                    */
                }
                if (getContentAsString().equals(""))
                    throw new NotRecoverableDownloadException("No credit for download this file or too many downloads!");
                checkProblems();
                throw new PluginImplementationException();
            } else {
                checkProblems();
                throw new PluginImplementationException("No download link found");
            }
        } else
            throw new ServiceConnectionProblemException();

    }

    private void checkNameAndSize(String content) throws Exception {
        Matcher matcher = PlugUtils.matcher("<span style=\"padding: 12px 0px 0px 10px; display: block\">(.+?)<", content);
        if (matcher.find())
            httpFile.setFileName(matcher.group(1).trim());
        else
        {   matcher = PlugUtils.matcher("<title>Soubor(.+?)\\(", content);
            if (matcher.find())
                httpFile.setFileName(matcher.group(1).trim());
            else
                PlugUtils.checkName(httpFile, content, "<h1>", "</h1>");
        }
        matcher = PlugUtils.matcher("Velikost:.+?([0-9].+?B)<", content);
        if (matcher.find()) {
            httpFile.setFileSize(PlugUtils.getFileSizeFromString(matcher.group(1).trim().replace("iB", "B")));
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    private void Login(String serverURL) throws Exception {
        synchronized (ShareRapidRunner.class) {
            ShareRapidServiceImpl service = (ShareRapidServiceImpl) getPluginService();
            PremiumAccount pa = service.getConfig();
            if (pa.isSet()) { /* || badConfig) {
                pa = service.showConfigDialog();
                if (pa == null || !pa.isSet()) {
                    throw new NotRecoverableDownloadException("No ShareRapid account login information!, Stahování je přístupné pouze přihlášeným uživatelům");
                }
                badConfig = false;
            }                   */
                if (serverURL.contains("file-share.top")) {
                    String postURL = serverURL + "//login";
                    GetMethod getmethod = getGetMethod(postURL);
                    if (!makeRequest(getmethod))
                        throw new PluginImplementationException();
                    HttpMethod httpMethod = getMethodBuilder()
                            .setActionFromFormWhereTagContains("password", true)
                            .setAction(postURL).setReferer(postURL)
                            .setParameter("email", pa.getUsername())
                            .setParameter("password", pa.getPassword())
                            .toPostMethod();

                    if (makeRedirectedRequest(httpMethod)) {
                        if (!getContentAsString().contains("logout")) {
                            throw new NotRecoverableDownloadException("Bad ShareRapid account login information!");
                        }
                    }
                } else {
                String postURL = serverURL + "/prihlaseni/";

                GetMethod getmethod = getGetMethod(postURL);
                if (!makeRequest(getmethod))
                    throw new PluginImplementationException();

                PostMethod postmethod = getPostMethod(postURL + "?");

                PlugUtils.addParameters(postmethod, getContentAsString(), new String[]{"hash", "sbmt"});
                postmethod.addParameter("login", pa.getUsername());
                postmethod.addParameter("pass1", pa.getPassword());

                if (makeRedirectedRequest(postmethod)) {
                    if (!getContentAsString().contains("class=\"logged_in_nickname")) {
                        throw new NotRecoverableDownloadException("Bad ShareRapid account login information!");
                    }
                }
                }
                GetMethod getMethod = getGetMethod(fileURL);
                if (!makeRedirectedRequest(getMethod)) {
                    throw new PluginImplementationException();
                }
            }
        }
    }

    private void checkProblems() throws Exception {
        final String content = getContentAsString();
        if (content.contains("Soubor byl smazán"))
            throw new URLNotAvailableAnymoreException("Soubor byl smazán");
        //if (content.contains("Stahování je přístupné pouze přihlášeným uživatelům"))
        //    throw new ErrorDuringDownloadingException("Stahování je přístupné pouze přihlášeným uživatelům");
        if (content.contains("Stahování zdarma je možné jen přes náš"))
            throw new NotRecoverableDownloadException("Stahování zdarma je možné jen přes náš download manager");
        if (content.contains("Chcete-li stahovat, musíte se přihlásit"))
            throw new NotRecoverableDownloadException("Chcete-li stahovat, musíte se přihlásit (To download, must be logged in)");
        if (content.contains("Soubor nelze stáhnout, aktuálně nemáte aktivní žádné předplacené služby."))
            throw new NotRecoverableDownloadException("Soubor nelze stáhnout, aktuálně nemáte aktivní žádné předplacené služby.");

        Matcher matcher;
        matcher = getMatcherAgainstContent("<h1>Po.adovan. str.nka nebyla nalezena</h1>");
        if (matcher.find()) {
            throw new URLNotAvailableAnymoreException("<b>Soubor nenalezen</b><br>");
        }
        matcher = getMatcherAgainstContent("<strong>Ji. V.m do.el kredit a vy.erpal jste free limit</strong>");
        if (matcher.find()) {
            throw new NotRecoverableDownloadException("No credit for download!");
        }
    }

}