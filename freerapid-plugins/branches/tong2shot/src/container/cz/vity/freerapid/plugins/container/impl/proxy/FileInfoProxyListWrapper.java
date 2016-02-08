package cz.vity.freerapid.plugins.container.impl.proxy;

import java.util.List;

/**
 * @author tong2shot
 */
public class FileInfoProxyListWrapper {
    private List<FileInfoProxy> list;

    public FileInfoProxyListWrapper() {
    }

    public List<FileInfoProxy> getList() {
        return this.list;
    }

    public void setList(List<FileInfoProxy> proxyList) {
        this.list = proxyList;
    }

}
