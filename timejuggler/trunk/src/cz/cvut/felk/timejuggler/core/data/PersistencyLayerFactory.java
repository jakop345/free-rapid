/**
 * @author Vity
 */
package cz.cvut.felk.timejuggler.core.data;

class PersistencyLayerFactory {
    private static PersistencyLayerFactory ourInstance = new PersistencyLayerFactory();

    public static PersistencyLayerFactory getInstance() {
        return ourInstance;
    }

    private PersistencyLayerFactory() {
    }

    public synchronized PersitencyLayer getDefaultPersitencyLayer() {
        //return new DBPersistencyLayer();
        return new FakePersistencyLayer();
    }
}
