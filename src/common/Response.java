package common;

import java.io.Serializable;


/**
 * This class is serializable, allowing it to be sent over a network or saved to a file.
 */
public class Response implements Serializable {
    private boolean success;
    private String message;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
