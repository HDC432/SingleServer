package common;

import java.io.Serializable;


/**
 * Represents a request to perform an operation on the key-value store.
 */
public class Request implements Serializable {
    public enum Type { PUT, GET, DELETE }

    private Type type;
    private String key;
    private String value;


    /**
     * Constructs a new Request with the specified type, key, and value.
     */
    public Request(Type type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    
    public Type getType() { return type; }
    public String getKey() { return key; }
    public String getValue() { return value; }
}
