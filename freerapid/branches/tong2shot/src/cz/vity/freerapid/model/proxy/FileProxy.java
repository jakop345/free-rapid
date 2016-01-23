package cz.vity.freerapid.model.proxy;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

import java.io.File;

/**
 * @author tong2shot
 */

@Persistent(proxyFor = File.class)
public class FileProxy implements PersistentProxy<File> {

    String fileStr;

    protected FileProxy() {
    }

    @Override
    public void initializeProxy(File object) {
        fileStr = object.toString();
    }

    @Override
    public File convertProxy() {
        return new File(fileStr);
    }
}
