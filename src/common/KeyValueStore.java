package common;

import java.util.HashMap;
import java.util.Map;

public class KeyValueStore {
    private final Map<String, String> store;

    public KeyValueStore() {
        this.store = new HashMap<>();
    }

    public synchronized String put(String key, String value) {
        return store.put(key, value);
    }

    public synchronized String get(String key) {
        return store.get(key); 
    }

    public synchronized String delete(String key) {
        return store.remove(key); 
    }
}
