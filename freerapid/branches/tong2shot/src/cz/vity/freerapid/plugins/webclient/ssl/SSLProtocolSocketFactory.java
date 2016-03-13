package cz.vity.freerapid.plugins.webclient.ssl;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.jdesktop.application.ApplicationContext;

import javax.net.SocketFactory;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SSLProtocolSocketFactory implements SecureProtocolSocketFactory {

    /**
     * Log object for this class.
     */
    private static final Logger logger = Logger.getLogger(SSLProtocolSocketFactory.class.getName());

    private static Pattern pemBlock = Pattern.compile(
            "-----BEGIN CERTIFICATE-----\\s*(.*?)\\s*-----END CERTIFICATE-----", Pattern.DOTALL);
    private static final Pattern charsetPattern = Pattern.compile(
            "charset\\s*=\\s*([^;]+)", Pattern.CASE_INSENSITIVE);

    private final ApplicationContext applicationContext;
    private final boolean verifyHostname;
    private SSLContext sslcontext = null;

    /**
     * Constructor for SSLProtocolSocketFactory.
     */
    public SSLProtocolSocketFactory(final ApplicationContext applicationContext, final boolean verifyHostname) {
        super();
        this.applicationContext = applicationContext;
        this.verifyHostname = verifyHostname;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private SSLContext createSSLContext() {
        try {
            //Mozilla CA certs
            //https://curl.haxx.se/docs/caextract.html
            String location = AppPrefs.getProperty(UserProp.CA_CERT_URL, "https://curl.haxx.se/ca/cacert.pem"); //https is a must
            File cachedPemFile = new File(applicationContext.getLocalStorage().getDirectory(), "pemfile_cached");
            boolean remote = true;
            if (cachedPemFile.exists()
                    && (System.currentTimeMillis() - cachedPemFile.lastModified() < 7 * 24 * 60 * 60 * 1000)) {
                location = cachedPemFile.getAbsolutePath();
                remote = false;
            }
            String pemBlocks;
            if (remote) {
                SSLContext contextInit;
                HttpsURLConnection remotePemFile = (HttpsURLConnection) (new URL(location)).openConnection();
                remotePemFile.setRequestMethod("GET");
                try {
                    remotePemFile.connect();
                    contextInit = SSLContext.getDefault();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                    try {
                        File caCertInit = new File(Utils.getAppPath(), "cacert.pem");
                        contextInit = (caCertInit.exists()
                                ? getSSLContextFromString(Utils.loadFile((cachedPemFile.exists() && (cachedPemFile.lastModified() > caCertInit.lastModified())
                                ? new File(location) : caCertInit), "utf-8"))
                                : new EasySSLProtocolSocketFactory().createEasySSLContext());
                        if (contextInit == null) {
                            logger.warning("Failed to create SSL context: CA certs init content is null.");
                            logger.warning("Trying to create fallback SSL context..");
                            contextInit = new EasySSLProtocolSocketFactory().createEasySSLContext();
                        }
                        remotePemFile.setSSLSocketFactory(contextInit.getSocketFactory());
                        remotePemFile.connect();
                    } catch (Exception e1) {
                        LogUtils.processException(logger, e1);
                        contextInit = new EasySSLProtocolSocketFactory().createEasySSLContext();
                        remotePemFile.setSSLSocketFactory(contextInit.getSocketFactory());
                        remotePemFile.connect();
                    }
                }
                pemBlocks = inputStreamToString(remotePemFile.getInputStream(), getCharset(remotePemFile));
                if (pemBlocks == null) {
                    logger.warning("Failed to create SSL context: CA certs content is null");
                    logger.warning("Trying to create fallback SSL context..");
                    return (contextInit != null ? contextInit : new EasySSLProtocolSocketFactory().createEasySSLContext());
                }
                cachedPemFile.delete();
                Files.write(Paths.get(cachedPemFile.getAbsolutePath()), pemBlocks.getBytes("utf-8"));
            } else {
                pemBlocks = Utils.loadFile(new File(location), "utf-8");
            }
            SSLContext context = getSSLContextFromString(pemBlocks);
            if (context != null) return context;
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            return new EasySSLProtocolSocketFactory().createEasySSLContext();
        }
        return new EasySSLProtocolSocketFactory().createEasySSLContext();
    }

    private SSLContext getSSLContextFromString(String pemBlocks) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Matcher matcher = pemBlock.matcher(pemBlocks);
        boolean found = false;
        while (matcher.find()) {
            String pemBlock = matcher.group(1).replaceAll("[\\n\\r]+", "");
            ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.decodeBase64(pemBlock));
            java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) cf.generateCertificate(byteStream);
            String alias = cert.getSubjectX500Principal().getName("RFC2253");
            if (alias != null && !keyStore.containsAlias(alias)) {
                found = true;
                keyStore.setCertificateEntry(alias, cert);
            }
        }
        if (found) {
            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManager.init(keyStore, null);
            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManager.init(keyStore);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), null);
            return context;
        }
        return null;
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }


    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String, int, java.net.InetAddress, int)
     */
    public Socket createSocket(
            String host,
            int port,
            InetAddress clientHost,
            int clientPort)
            throws IOException {

        SSLSocket sslSocket = (SSLSocket) getSSLContext().getSocketFactory().createSocket(
                host,
                port,
                clientHost,
                clientPort
        );
        verifyHostname(sslSocket);
        return sslSocket;
    }

    /**
     * Attempts to get a new socket connection to the given host within the given time limit.
     * <p>
     * To circumvent the limitations of older JREs that do not support connect timeout a
     * controller thread is executed. The controller thread attempts to create a new socket
     * within the given limit of time. If socket constructor does not return until the
     * timeout expires, the controller terminates and throws an {@link ConnectTimeoutException}
     * </p>
     *
     * @param host         the host name/IP
     * @param port         the port on the host
     * @param localAddress the local host name/IP to bind the socket to
     * @param localPort    the port on the local machine
     * @param params       {@link HttpConnectionParams Http connection parameters}
     * @return Socket a new socket
     * @throws IOException          if an I/O error occurs while creating the socket
     * @throws UnknownHostException if the IP address of the host cannot be
     *                              determined
     */
    public Socket createSocket(
            final String host,
            final int port,
            final InetAddress localAddress,
            final int localPort,
            final HttpConnectionParams params
    ) throws IOException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = getSSLContext().getSocketFactory();
        if (timeout == 0) {
            SSLSocket sslSocket = (SSLSocket) socketfactory.createSocket(host, port, localAddress, localPort);
            verifyHostname(sslSocket);
            return sslSocket;
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            SSLSocket sslSocket = (SSLSocket) socket;
            verifyHostname(sslSocket);
            return sslSocket;
        }
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.lang.String, int)
     */
    public Socket createSocket(String host, int port)
            throws IOException {
        SSLSocket sslSocket = (SSLSocket) getSSLContext().getSocketFactory().createSocket(
                host,
                port
        );
        verifyHostname(sslSocket);
        return sslSocket;
    }

    /**
     * @see SecureProtocolSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
     */
    public Socket createSocket(
            Socket socket,
            String host,
            int port,
            boolean autoClose)
            throws IOException {
        SSLSocket sslSocket = (SSLSocket) getSSLContext().getSocketFactory().createSocket(
                socket,
                host,
                port,
                autoClose
        );
        verifyHostname(sslSocket);
        return sslSocket;
    }

    /**
     * Describe <code>verifyHostname</code> method here.
     *
     * @param socket a <code>SSLSocket</code> value
     * @throws SSLPeerUnverifiedException If there are problems obtaining
     *                                    the server certificates from the SSL session, or the server host name
     *                                    does not match with the "Common Name" in the server certificates
     *                                    SubjectDN.
     * @throws UnknownHostException       If we are not able to resolve
     *                                    the SSL sessions returned server host name.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void verifyHostname(SSLSocket socket)
            throws SSLPeerUnverifiedException, UnknownHostException {
        if (!verifyHostname)
            return;

        SSLSession session = socket.getSession();
        String hostname = session.getPeerHost();
        try {
            InetAddress.getByName(hostname);
        } catch (UnknownHostException uhe) {
            throw new UnknownHostException("Could not resolve SSL sessions "
                    + "server hostname: " + hostname);
        }

        X509Certificate[] certs = session.getPeerCertificateChain();
        if (certs == null || certs.length == 0)
            throw new SSLPeerUnverifiedException("No server certificates found!");

        //get the servers DN in its string representation
        String dn = certs[0].getSubjectDN().getName();

        //might be useful to print out all certificates we receive from the
        //server, in case one has to debug a problem with the installed certs.
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Server certificate chain:");
            for (int i = 0; i < certs.length; i++) {
                logger.fine("X509Certificate[" + i + "]=" + certs[i]);
            }
        }
        //get the common name from the first cert
        String cn = getCN(dn);
        if (hostname.equalsIgnoreCase(cn)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Target hostname valid: " + cn);
            }
        } else {
            throw new SSLPeerUnverifiedException(
                    "HTTPS hostname invalid: expected '" + hostname + "', received '" + cn + "'");
        }
    }


    /**
     * Parses a X.500 distinguished name for the value of the
     * "Common Name" field.
     * This is done a bit sloppy right now and should probably be done a bit
     * more according to <code>RFC 2253</code>.
     *
     * @param dn a X.500 distinguished name.
     * @return the value of the "Common Name" field.
     */
    private String getCN(String dn) {
        int i;
        i = dn.indexOf("CN=");
        if (i == -1) {
            return null;
        }
        //get the remaining DN without CN=
        dn = dn.substring(i + 3);
        // System.out.println("dn=" + dn);
        char[] dncs = dn.toCharArray();
        for (i = 0; i < dncs.length; i++) {
            if (dncs[i] == ',' && i > 0 && dncs[i - 1] != '\\') {
                break;
            }
        }
        return dn.substring(0, i);
    }

    public boolean equals(Object obj) {
        return ((obj != null) && obj.getClass().equals(SSLProtocolSocketFactory.class));
    }

    public int hashCode() {
        return SSLProtocolSocketFactory.class.hashCode();
    }

    static String inputStreamToString(InputStream inputStream, String charset) {
        try {
            final char[] chars = new char[8192];
            StringBuilder builder = new StringBuilder(chars.length);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset), chars.length);
            try {
                for (int len; -1 != (len = reader.read(chars, 0, chars.length)); builder.append(chars, 0, len)) ;
            } catch (EOFException | SSLProtocolException | SocketException e) {
            }
            return builder.toString();
        } catch (Throwable t) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                //
            }
        }
    }

    static String getCharset(URLConnection conn) {
        String charset = conn.getContentType();
        if (charset != null) {
            Matcher matcher = charsetPattern.matcher(charset);
            if (matcher.find()) {
                charset = matcher.group(1);
                if (Charset.isSupported(charset)) {
                    return charset;
                }
            }
        }
        return "utf-8";
    }
}
