package common;

import java.util.HashMap;
import java.util.Map;

/**
 *  This class allows storing, retrieving, and deleting key-value pairs.
 */
public class KeyValueStore {
    private final Map<String, String> store;

    /**
     * Constructs a new KeyValueStore with an empty HashMap.
     */
    public KeyValueStore() {
        this.store = new HashMap<>();
    }


    /**
     * Adds or updates a key-value pair in the store.
     */
    public synchronized String put(String key, String value) {
        return store.put(key, value);
    }


    /**
     * Retrieves the value associated with the specified key.
     */
    public synchronized String get(String key) {
        return store.get(key); 
    }


    /**
     * Delete the key-value pair associated with the specified key.
     */
    public synchronized String delete(String key) {
        return store.remove(key); 
    }
}
