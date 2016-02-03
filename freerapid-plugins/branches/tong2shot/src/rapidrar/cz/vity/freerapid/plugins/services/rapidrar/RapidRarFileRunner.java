package cz.vity.freerapid.plugins.services.rapidrar;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerRunner;
import cz.vity.freerapid.plugins.webclient.MethodBuilder;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.codec.binary.Base64;

import java.util.regex.Matcher;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class RapidRarFileRunner extends XFilePlayerRunner {

    @Override
    protected MethodBuilder getXFSMethodBuilder() throws Exception {
        try {
            return super.getXFSMethodBuilder();
        } catch (Exception x) {
            return getFirstPageTrap();
        }
    }

    private MethodBuilder getFirstPageTrap() throws Exception {
        String content = getContentAsString();
        Matcher match = PlugUtils.matcher("src=\"([^<>]+?/js/rapidrar\\d+\\.js.*?)\"", content);
        if (!match.find())
            throw new PluginImplementationException("error finding next page detail 1");
        if (!makeRedirectedRequest(getGetMethod(match.group(1).trim())))
            throw new ServiceConnectionProblemException();
        match = PlugUtils.matcher("^\\s+\\$.+?\\('span#(.+?)'\\)", getContentAsString());
        if (!match.find())
            throw new PluginImplementationException("error finding next page detail 2");
        String token1 = match.group(1).trim();
        match = PlugUtils.matcher("(?s)id=\"" +token1+ "\"[^<>]*?>(.+?)<", content);
        if (!match.find())
            throw new PluginImplementationException("error finding next page detail 3");
        String token2 = match.group(1).trim();
        String decodedToken = new String(Base64.decodeBase64(token2));
        MethodBuilder builder = getMethodBuilder(content)
                .setActionFromFormWhereTagContains(decodedToken, true)
                .setAction(fileURL).setReferer(fileURL);

        match = PlugUtils.matcher("name=\"(.+?)\" value=\"Premium", content);
        if (!match.find())
            throw new PluginImplementationException("error finding next page detail 4");
        String token3 = match.group(1).trim();    //premium tag name
        builder.removeParameter(token3);

        return builder;
    }
}