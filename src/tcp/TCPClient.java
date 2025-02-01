package tcp;

import common.Request;
import common.Response;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <server_ip> <port>");
            return;
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(serverIp, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Request putRequest = new Request(Request.Type.PUT, "key1", "value1");
            log("Sending PUT request for key: " + putRequest.getKey());
            out.writeObject(putRequest);
            out.flush();

            Response putResponse = (Response) in.readObject();
            log("Received response: " + putResponse.getMessage());

            Request getRequest = new Request(Request.Type.GET, "key1", null);
            log("Sending GET request for key: " + getRequest.getKey());
            out.writeObject(getRequest);
            out.flush();

            Object responseObj = in.readObject();
            if (responseObj instanceof Response) {
                Response getResponse = (Response) responseObj;
                log("Received response: " + getResponse.getMessage());
            } else {
                log("Unexpected response type: " + responseObj.getClass().getName());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            log("Client error: " + e.getMessage());
        }
    }

    private static void log(String message) {
        System.out.println(timestamp() + " " + message);
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
