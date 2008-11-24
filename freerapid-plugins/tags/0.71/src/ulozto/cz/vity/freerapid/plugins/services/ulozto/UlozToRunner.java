package cz.vity.freerapid.plugins.services.ulozto;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.*;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek, Ludek Zika
 */
class UlozToRunner {
    private final static Logger logger = Logger.getLogger(cz.vity.freerapid.plugins.services.ulozto.UlozToRunner.class.getName());
    private HttpDownloadClient client;
    private HttpFileDownloader downloader;
    private String fileURL;
    private String postTargetURL;
    private HttpFile httpFile;

    public void run(HttpFileDownloader downloader) throws Exception {
        this.downloader = downloader;
        httpFile = downloader.getDownloadFile();
        client = downloader.getClient();
        fileURL = httpFile.getFileUrl().toString();
        client.getHTTPClient().getParams().setParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);
        logger.info("Starting download in TASK " + fileURL);

        final GetMethod getMethod = client.getGetMethod(fileURL);
        getMethod.setFollowRedirects(true);
        if (client.makeRequest(getMethod) == HttpStatus.SC_OK) {
            if (client.getContentAsString().contains("uloz.to")) {
                Matcher matcher = PlugUtils.matcher("\\|\\s*([^|]+) \\| </title>", client.getContentAsString());
                // odebiram jmeno
                String fn;
                if (matcher.find()) {
                    fn = matcher.group(1);
                } else fn = sicherName(fileURL);
                logger.info("File name " + fn);
                httpFile.setFileName(fn);
                // konec odebirani jmena

                while (client.getContentAsString().contains("id=\"captcha\"")) {
                    PostMethod method = stepCaptcha(client.getContentAsString());
                    //    method.setFollowRedirects(true);
                    if (trydownload(method)) break;
                    if (isRedirect(method.getStatusCode())) {
                        GetMethod getMethod2 = redirect(method);
                        getMethod2.setFollowRedirects(true);
                        if (trydownload(getMethod2)) break;
                    }
                }
            } else {
                checkProblems();
                logger.info(client.getContentAsString());
                throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
            }
        } else
            throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
    }

    private String sicherName(String s) throws UnsupportedEncodingException {
        Matcher matcher = PlugUtils.matcher("(.*/)([^/]*)$", s);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "file01";
    }

    private PostMethod stepCaptcha(String contentAsString) throws Exception {
        if (contentAsString.contains("id=\"captcha\"")) {

            Matcher matcher = PlugUtils.matcher("src=\"([^\"]*captcha[^\"]*)\"", contentAsString);
            if (matcher.find()) {
                String s = matcher.group(1);

                logger.info("Captcha URL " + s);
                String captcha = downloader.getCaptcha(s);
                if (captcha == null) {
                    throw new CaptchaEntryInputMismatchException();
                } else {

                    String captcha_nb = getParameter("captcha_nb", contentAsString);

                    matcher = PlugUtils.matcher("form name=\"dwn\" action=\"([^\"]*)\"", contentAsString);
                    if (!matcher.find()) {
                        logger.info(client.getContentAsString());
                        throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");

                    }
                    postTargetURL = matcher.group(1);
                    logger.info("Captcha target URL " + postTargetURL);
                    client.setReferer(fileURL);
                    final PostMethod postMethod = client.getPostMethod(postTargetURL);
                    postMethod.addParameter("captcha_nb", captcha_nb);
                    postMethod.addParameter("captcha_user", captcha);
                    postMethod.addParameter("download", PlugUtils.unescapeHtml("--%3E+St%C3%A1hnout+soubor+%3C--"));


                    return postMethod;

                }
            } else {
                logger.warning(contentAsString);
                throw new PluginImplementationException("Captcha picture was not found");
            }

        }
        return null;
    }


    private boolean trydownload(HttpMethodBase method) throws Exception {
        httpFile.setState(DownloadState.GETTING);
        try {
            final InputStream inputStream2 = client.makeFinalRequestForFile(method, httpFile);
            if (inputStream2 != null) {

                downloader.saveToFile(inputStream2);
                return true;
            } else {
                return false;
            }
        } finally {
            method.abort();
            method.releaseConnection();
        }
    }

    private String getParameter(String s, String contentAsString) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("name=\"" + s + "\"[^v>]*value=\"([^\"]*)\"", contentAsString);
        if (matcher.find()) {
            return matcher.group(1);
        } else
            throw new PluginImplementationException("Parameter " + s + " was not found");
    }


    private GetMethod redirect(PostMethod method) throws PluginImplementationException {
        logger.info("Mame presmerovani, tak s nim neco udelame");
        Header header = method.getResponseHeader("location");
        if (header != null) {
            String newuri = header.getValue();
            if ((newuri == null) || ("".equals(newuri))) {
                newuri = "/";
            }
            logger.info("Redirect target: " + newuri);

            GetMethod redirect = client.getGetMethod(newuri);
            return redirect;
        } else {
            logger.info("Nejsou hlavicky kam presmerovat");
            throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
        }
    }


    private boolean isRedirect(int statuscode) {
        return (statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (statuscode == HttpStatus.SC_SEE_OTHER) ||
                (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT);
    }


    private void checkProblems() throws ServiceConnectionProblemException, YouHaveToWaitException, URLNotAvailableAnymoreException {
        Matcher matcher;
        matcher = PlugUtils.matcher("soubor nebyl nalezen", client.getContentAsString());
        if (matcher.find()) {
            throw new URLNotAvailableAnymoreException(String.format("<b>Požadovaný soubor nebyl nalezen.</b><br>"));
        }
        matcher = PlugUtils.matcher("stahovat pouze jeden soubor", client.getContentAsString());
        if (matcher.find()) {
            throw new ServiceConnectionProblemException(String.format("<b>Mùžete stahovat pouze jeden soubor naráz</b><br>"));

        }


    }

}
