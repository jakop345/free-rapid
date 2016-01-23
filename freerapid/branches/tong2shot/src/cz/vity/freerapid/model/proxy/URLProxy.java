package cz.vity.freerapid.model.proxy;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author tong2shot
 */

@Persistent(proxyFor = URL.class)
public class URLProxy implements PersistentProxy<URL> {

    String urlStr;

    protected URLProxy() {
    }

    @Override
    public void initializeProxy(URL object) {
        urlStr = object.toExternalForm();
    }

    @Override
    public URL convertProxy() {
        try {
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
