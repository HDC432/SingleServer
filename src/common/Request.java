package common;

import java.io.Serializable;

public class Request implements Serializable {
    public enum Type { PUT, GET, DELETE }

    private Type type;
    private String key;
    private String value;

    public Request(Type type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public Type getType() { return type; }
    public String getKey() { return key; }
    public String getValue() { return value; }
}
