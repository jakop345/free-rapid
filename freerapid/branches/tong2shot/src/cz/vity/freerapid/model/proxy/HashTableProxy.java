package cz.vity.freerapid.model.proxy;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author tong2shot
 */

@Persistent(proxyFor = Hashtable.class)
public class HashTableProxy<K, V> implements PersistentProxy<Hashtable<K, V>> {

    private K[] keys;
    private V[] values;

    protected HashTableProxy() {
    }

    @Override
    public void initializeProxy(Hashtable<K, V> object) {
        int size = object.size();
        keys = (K[]) new Object[size];
        values = (V[]) new Object[size];
        int i = 0;
        for (Map.Entry<K, V> entry : object.entrySet()) {
            keys[i] = entry.getKey();
            values[i] = entry.getValue();
            i += 1;
        }
    }

    @Override
    public Hashtable<K, V> convertProxy() {
        int size = values.length;
        Hashtable<K, V> object = new Hashtable<K, V>(size);
        for (int i = 0; i < size; i += 1) {
            object.put(keys[i], values[i]);
        }
        return object;
    }
}
