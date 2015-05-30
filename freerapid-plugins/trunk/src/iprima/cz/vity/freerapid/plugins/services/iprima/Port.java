package cz.vity.freerapid.plugins.services.iprima;

/**
 * @author tong2shot
 */
public enum Port {
    _1935(1935),
    _80(80),
    _443(443);

    private int port;

    Port(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return port + (port == 1935 ? " (default)" : "");
    }


}
