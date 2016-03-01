package cz.vity.freerapid.plugins.services.nhk_vod;

import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.services.adobehds.HdsDownloader;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.InflaterInputStream;

/**
 * Class which contains main code
 *
 * @author tong2shot
 * @since 0.9u4
 */
class NHK_vodFileRunner extends AbstractRunner {
    private final static Logger logger = Logger.getLogger(NHK_vodFileRunner.class.getName());

    private final static String OOYALA_SECRET_KEY = "4b3d32bed59fb8c54ab8a190d5d147f0e4f0cbe6804c8e0721175ab68b40cb01";
    private final static String OOYALA_SECRET_IV = "00020406080a0c0ea0a2a4a6a8aaacae";

    @Override
    public void runCheck() throws Exception {
        super.runCheck();
        final GetMethod getMethod = getGetMethod(fileURL);
        if (makeRedirectedRequest(getMethod)) {
            checkProblems();
            checkNameAndSize(getContentAsString());
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkNameAndSize(String content) throws ErrorDuringDownloadingException {
        PlugUtils.checkName(httpFile, content, "<h3>", "</");
        httpFile.setFileName(httpFile.getFileName() + ".flv");
        httpFile.setFileState(FileState.CHECKED_AND_EXISTING);
    }

    @Override
    public void run() throws Exception {
        super.run();
        logger.info("Starting download in TASK " + fileURL);
        final GetMethod method = getGetMethod(fileURL);
        if (makeRedirectedRequest(method)) {
            final String contentAsString = getContentAsString();
            checkProblems();
            checkNameAndSize(contentAsString);

            String movieId;
            try {
                movieId = PlugUtils.getStringBetween(contentAsString, "'movie-area', '", "'");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("Movie ID not found");
            }
            String moviePlayerUrl = "http://player.ooyala.com/nuplayer?embedCode=" + movieId +
                    "&hide=channels%2Cembed%2Cendscreen%2Cinfo%2Csharing&playerBrandingId=43e68966aa77408bb5cfeb054861e73a";
            HttpMethod httpMethod = getMethodBuilder().setReferer(fileURL).setAction(moviePlayerUrl).toHttpMethod();
            if (!makeRedirectedRequest(httpMethod)) {
                checkProblems();
                throw new ServiceConnectionProblemException();
            }
            checkProblems();

            String mediaSelectorEncrypted = getContentAsString();
            String mediaSelectorDecrypted = ooyalaDecrypt(mediaSelectorEncrypted);
            String hdsManifestUrl;
            try {
                hdsManifestUrl = PlugUtils.getStringBetween(mediaSelectorDecrypted, "<httpDynamicStreamUrl>", "</");
            } catch (PluginImplementationException e) {
                throw new PluginImplementationException("HDS manifest URL not found");
            }
            HdsDownloader downloader = new HdsDownloader(client, httpFile, downloadTask);
            downloader.tryDownloadAndSaveFile(hdsManifestUrl);
        } else {
            checkProblems();
            throw new ServiceConnectionProblemException();
        }
    }

    private void checkProblems() throws ErrorDuringDownloadingException {
        final String contentAsString = getContentAsString();
        if (contentAsString.contains("Error: 404 Not Found")) {
            throw new URLNotAvailableAnymoreException("File not found");
        }
    }

    private String ooyalaDecrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(Hex.decodeHex(OOYALA_SECRET_KEY.toCharArray()), "AES"),
                new IvParameterSpec(Hex.decodeHex(OOYALA_SECRET_IV.toCharArray())));
        byte[] deciphered = cipher.doFinal(Base64.decodeBase64(cipherText));

        ByteArrayInputStream bais = new ByteArrayInputStream(deciphered);
        byte[] buffer = new byte[1024];
        readBytes(bais, buffer, 4);
        InflaterInputStream inflater = new InflaterInputStream(bais);
        StringBuilder sb = new StringBuilder(8192);
        int len;
        while ((len = inflater.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, "UTF-8"));
        }
        return sb.toString();
    }

    private int readBytes(InputStream is, byte[] buffer, int count) throws IOException {
        int read = 0, i;
        while (count > 0 && (i = is.read(buffer, 0, count)) != -1) {
            count -= i;
            read += i;
        }
        return read;
    }

}
