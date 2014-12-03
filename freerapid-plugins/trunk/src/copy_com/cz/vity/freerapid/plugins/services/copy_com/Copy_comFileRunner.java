package cz.vity.freerapid.plugins.services.copy_com;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.JsonMapper;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.URIUtil;
import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 * @author tong2shot
 */
class Copy_comFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(Copy_comFileRunner.class.getName());
    private SettingsConfig config;

    private void setConfig() throws Exception {
        Copy_comServiceImpl service = (Copy_comServiceImpl) getPluginService();
        config = service.getConfig();
    }

    @Override
    public void runCheck() throws Exception {
        checkUrl();
        super.runCheck();
        final PostMethod postMethod = getGetLinkMethod(fileURL, 2);
        if (makeRedirectedRequest(postMethod)) {
            setConfig();
            checkProblems();
            checkNameAndSize(getRootNode(getContentAsString()));
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkUrl() {
        if (!fileURL.contains("copy.com/s/"))
            fileURL = fileURL.replaceFirst("copy\\.com/", "copy.com/s/");
    }

    private void checkNameAndSize(JsonNode rootNode) throws ErrorDuringDownloadingException {
        JsonNode nameNode = (config.isAppendPathToFilename() ? rootNode.findPath("parent").findPath("path") : rootNode.findPath("parent").findPath("name"));
        String name = (!nameNode.isMissingNode() ? nameNode.getTextValue() : rootNode.findPath("link_name").getTextValue());
        if (name == null) {
            throw new PluginImplementationException("File name not found");
        }
        httpFile.setFileName(name.replaceFirst("^/", "").replaceAll("/", " - "));
        JsonNode typeNode = rootNode.findPath("parent").findPath("type");
        if (!typeNode.isMissingNode() && typeNode.getTextValue().equals("file")) {
            long size = rootNode.findPath("parent").findPath("size").getValueAsLong();
            httpFile.setFileSize(size);
        }
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        checkUrl();
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final PostMethod method = getGetLinkMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            setConfig();
            JsonNode rootNode = getRootNode(getContentAsString());
            checkProblems();
            checkNameAndSize(rootNode);
            if (isRoot(rootNode) || isFolder(rootNode)) {
                List<URI> list = new LinkedList<URI>();
                JsonNode objectsNodes = rootNode.findPath("objects");
                for (JsonNode objectsNode : objectsNodes) {
                    try {
                        String url = objectsNode.get("url").getTextValue();
                        //they encode '/' as '%2F', so we have to decode it first, then encode the path & query components
                        list.add(new URI(URIUtil.encodePathQuery(URLDecoder.decode(url, "UTF-8"))));
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                    }
                }
                if (list.isEmpty()) throw new PluginImplementationException("No links found");
                getPluginService().getPluginContext().getQueueSupport().addLinksToQueue(httpFile, list);
                httpFile.setFileName(list.size() + " Link(s) Extracted !");
                httpFile.setState(DownloadState.COMPLETED);
                httpFile.getProperties().put("removeCompleted", true);
            } else {
                final String dlUrl = rootNode.findPath("parent").findPath("download_url").getTextValue();
                if (dlUrl == null) {
                    throw new PluginImplementationException("Error getting download URL");
                }
                setClientParameter(DownloadClientConsts.DONT_USE_HEADER_FILENAME, true);
                final HttpMethod httpMethod = getGetMethod(URLDecoder.decode(dlUrl, "UTF-8"));
                if (!tryDownloadAndSaveFile(httpMethod)) {
                    checkProblems();
                    throw new ServiceConnectionProblemException("Error starting download");
                }
            }
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("message\":\"Cannot find")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private PostMethod getGetLinkMethod(String fileUrl, int limit) throws Exception {
        PostMethod postMethod = (PostMethod) getMethodBuilder()
                .setReferer(fileUrl)
                .setAction("https://apiweb.copy.com/jsonrpc")
                .setHeader("X-Api-Version", "1.0")
                .setHeader("X-Client-Type", "API")
                .setHeader("X-Client-Version", "1.0.00")
                .toPostMethod();
        Matcher matcher = PlugUtils.matcher("/s/([^/]+)(/.+)?", URLDecoder.decode(fileUrl, "UTF-8"));
        if (!matcher.find()) {
            throw new PluginImplementationException("Unknown URL pattern");
        }
        String linkToken = matcher.group(1);
        String group2 = matcher.group(2);
        String path = (group2 == null ? "" : String.format("\"path\":\"%s\",", group2)); //path should be in decoded form
        String requestContent = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"get_link\",\"params\":{\"link_token\":\"%s\",%s\"limit\":%d,\"list_watermark\":0,\"max_items\":%d,\"offset\":0,\"include_total_items\":true},\"id\":1}", linkToken, path, limit, limit);
        postMethod.setRequestEntity(new StringRequestEntity(requestContent, "application/x-www-form-urlencoded", "UTF-8"));
        return postMethod;
    }

    private PostMethod getGetLinkMethod(String fileUrl) throws Exception {
        return getGetLinkMethod(fileUrl, 10000);
    }

    private JsonNode getRootNode(String content) throws PluginImplementationException {
        JsonNode rootNode;
        try {
            rootNode = new JsonMapper().getObjectMapper().readTree(content);
        } catch (Exception e) {
            throw new PluginImplementationException("Error getting root node");
        }
        return rootNode;
    }

    private boolean isFolder(JsonNode rootNode) {
        JsonNode typeNode = rootNode.findPath("parent").findPath("type");
        return !typeNode.isMissingNode() && typeNode.getTextValue().equals("dir");
    }

    private boolean isRoot(JsonNode rootNode) {
        return rootNode.findPath("parent").isMissingNode();
    }

}