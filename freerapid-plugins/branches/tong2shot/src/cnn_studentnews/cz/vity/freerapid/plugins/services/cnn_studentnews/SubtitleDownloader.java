package cz.vity.freerapid.plugins.services.cnn_studentnews;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.services.cnn_studentnews.subtitle.FormatSCC;
import cz.vity.freerapid.plugins.services.cnn_studentnews.subtitle.TimedTextFileFormat;
import cz.vity.freerapid.plugins.services.cnn_studentnews.subtitle.TimedTextObject;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpMethod;

import java.io.*;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
class SubtitleDownloader {
    private final static Logger logger = Logger.getLogger(SubtitleDownloader.class.getName());

    public void downloadSubtitle(HttpDownloadClient client, HttpFile httpFile, String subtitleUrl, String lang) throws Exception {
        if ((subtitleUrl == null) || subtitleUrl.isEmpty()) {
            return;
        }
        logger.info("Downloading subtitle");
        HttpMethod method = client.getGetMethod(subtitleUrl);
        if (200 != client.makeRequest(method, true)) {
            throw new PluginImplementationException("Failed to request subtitle");
        }
        String subtitle = client.getContentAsString();
        InputStream is = new ByteArrayInputStream(subtitle.getBytes());

        String fnameNoExt = HttpUtils.replaceInvalidCharsForFileSystem(httpFile.getFileName().replaceFirst("\\.[^\\.]{3,4}$", ""), "_");
        String fnameOutput = fnameNoExt + "." + lang + ".srt";
        File outputFile = new File(httpFile.getSaveToDirectory(), fnameOutput);
        int outputFileCounter = 2;
        while (outputFile.exists()) {
            fnameOutput = fnameNoExt + "-" + outputFileCounter++ + "." + lang + ".srt";
            outputFile = new File(httpFile.getSaveToDirectory(), fnameOutput);
        }
        TimedTextFileFormat ttff = new FormatSCC();
        TimedTextObject tto = ttff.parseFile(fnameOutput, is);
        writeFileTxt(outputFile, tto.toSRT());
    }

    private void writeFileTxt(File file, String[] totalFile) {
        FileWriter fw = null;
        PrintWriter pw;
        try {
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);
            for (String aTotalFile : totalFile) {
                pw.println(aTotalFile);
            }
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        } finally {
            try {
                // Execute the "finally" to make sure the file is closed
                if (null != fw)
                    fw.close();
            } catch (Exception e2) {
                LogUtils.processException(logger, e2);
            }
        }
    }

}
