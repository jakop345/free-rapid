package cz.vity.freerapid.plugins.services.chayfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingRunner;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandler;
import cz.vity.freerapid.plugins.services.xfilesharing.nameandsize.FileSizeHandlerNoSize;
import org.apache.commons.httpclient.HttpMethod;

import java.util.List;

/**
 * Class which contains main code
 *
 * @author birchie
 */
class ChayFileFileRunner extends XFileSharingRunner {

    @Override
    protected List<FileSizeHandler> getFileSizeHandlers() {
        final List<FileSizeHandler> fileSizeHandlers = super.getFileSizeHandlers();
        fileSizeHandlers.add(new FileSizeHandlerNoSize());
        return fileSizeHandlers;
    }

    @Override
    protected void setLanguageCookie() throws Exception {
        setTextContentTypes("application/cgi");
        super.setLanguageCookie();
    }

    @Override
    protected boolean handleDirectDownload(final HttpMethod method) throws Exception {
        fileURL = getMethodBuilder().setReferer(method.getURI().getURI()).setAction(method.getResponseHeader("Location").getValue()).getEscapedURI();
        return false;
    }
}