package cz.cvut.felk.timejuggler.core.tasks;

import cz.cvut.felk.timejuggler.core.Consts;
import cz.cvut.felk.timejuggler.core.application.ProxyHelper;
import cz.cvut.felk.timejuggler.core.application.SubmitErrorInfo;
import cz.cvut.felk.timejuggler.utilities.LogUtils;
import org.jdesktop.application.Application;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SubmitErrorInfoTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(SubmitErrorInfoTask.class.getName());
    private final SubmitErrorInfo errorInfo;

    public SubmitErrorInfoTask(SubmitErrorInfo errorInfo) {
        super(Application.getInstance(), SubmitErrorInfoTask.class);
        this.errorInfo = errorInfo;
        this.setUserCanCancel(false);
        ProxyHelper.initProxy();//init proxy settings
    }


    @Override
    protected Void doInBackground() throws Exception {
        HttpURLConnection urlConn = null;
        message("message.status.connecting");
        try {
            urlConn = (HttpURLConnection) new URL(Consts.WEBURL_SUBMIT_ERROR).openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            final DataOutputStream bufferOut = new DataOutputStream(urlConn.getOutputStream());
            logger.info("Connected to WordRider.net, Writing params");
            final String postingData = errorInfo.toURLPostData();
            logger.info("Posting data:" + postingData);
            message("message.status.sending");
            bufferOut.write(postingData.getBytes());
            bufferOut.close();
            //logger.info("reading Response");
            urlConn.getInputStream().close();
            logger.info("disconnecting");
            message("message.status.disconnect");
            urlConn.disconnect();
        } catch (UnknownHostException e) { //Inet not available
            if (urlConn != null) urlConn.disconnect();
            throw e;
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            if (urlConn != null) urlConn.disconnect();
            throw e;
        }
        return null;
    }
}
