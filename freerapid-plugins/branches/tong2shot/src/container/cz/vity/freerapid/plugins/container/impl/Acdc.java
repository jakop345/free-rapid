package cz.vity.freerapid.plugins.container.impl;

import cz.vity.freerapid.plugins.container.ContainerFormat;
import cz.vity.freerapid.plugins.container.ContainerPlugin;
import cz.vity.freerapid.plugins.container.FileInfo;
import cz.vity.freerapid.plugins.container.impl.proxy.FileInfoProxy;
import cz.vity.freerapid.plugins.container.impl.proxy.FileInfoProxyListWrapper;
import cz.vity.freerapid.utilities.LogUtils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class Acdc implements ContainerFormat {
    private final static Logger logger = Logger.getLogger(Acdc.class.getName());

    public static String[] getSupportedFiles() {
        return new String[]{"acdc"};
    }

    public Acdc(final ContainerPlugin plugin) {
    }

    @Override
    public List<FileInfo> read(InputStream is) throws Exception {
        final Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        final char[] buffer = new char[1024];
        final StringBuilder stringBuilder = new StringBuilder();

        while (reader.read(buffer) != -1) {
            stringBuilder.append(buffer);
        }
        String content = stringBuilder.toString();
        logger.info(content);

        XMLDecoder xmlDecoder = null;
        try {
            xmlDecoder = new XMLDecoder(new ByteArrayInputStream(content.getBytes()), null, null, FileInfoProxyListWrapper.class.getClassLoader());
            final Object obj = xmlDecoder.readObject();
            if (obj instanceof FileInfoProxyListWrapper) {
                final FileInfoProxyListWrapper list = (FileInfoProxyListWrapper) obj;
                List<FileInfoProxy> fileInfoProxyList = list.getList();
                List<FileInfo> result = new ArrayList<FileInfo>(fileInfoProxyList.size());
                for (FileInfoProxy proxy : fileInfoProxyList) {
                    URL url;
                    try {
                        url = new URL(proxy.getFileUrl());
                    } catch (MalformedURLException e) {
                        url = null;
                    }
                    FileInfo fileInfo = new FileInfo(url);
                    fileInfo.setFileName(proxy.getFileName());
                    fileInfo.setFileSize(proxy.getFileSize());
                    fileInfo.setDescription(proxy.getDescription());
                    fileInfo.setSaveToDirectory(proxy.getSaveToDirectory());
                    result.add(fileInfo);
                }
                return result;
            } else {
                throw new IOException("Invalid 'acdc' format");
            }
        } catch (RuntimeException e) {
            LogUtils.processException(logger, e);
            throw new Exception(e);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw e;
        } finally {
            if (xmlDecoder != null) {
                try {
                    xmlDecoder.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }

    @Override
    public void write(List<FileInfo> files, OutputStream os) throws Exception {
        XMLEncoder xmlEncoder = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInfoProxyListWrapper proxyList = new FileInfoProxyListWrapper();
        List<FileInfoProxy> fileInfoProxyList = new ArrayList<FileInfoProxy>(files.size());
        for (FileInfo file : files) {
            FileInfoProxy proxy = new FileInfoProxy();
            proxy.setFileUrl(file.getFileUrl().toExternalForm());
            proxy.setFileName(file.getFileName());
            proxy.setFileSize(file.getFileSize());
            proxy.setDescription(file.getDescription());
            proxy.setSaveToDirectory(file.getSaveToDirectory());
            fileInfoProxyList.add(proxy);
        }
        proxyList.setList(fileInfoProxyList);
        try {
            xmlEncoder = new XMLEncoder(baos);
            xmlEncoder.writeObject(proxyList);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw e;
        } finally {
            if (xmlEncoder != null) {
                try {
                    xmlEncoder.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }

        String result = new String(baos.toByteArray());
        try {
            baos.close();
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
        final Writer w = new OutputStreamWriter(os, "UTF-8");
        w.write(result);
        w.flush();
    }

}
