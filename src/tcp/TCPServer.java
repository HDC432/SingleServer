package tcp;

import common.KeyValueStore;
import common.Request;
import common.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TCPServer {
    private static final KeyValueStore store = new KeyValueStore();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println(timestamp() + " Server started on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                     ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                    while (true) {
                        try {
                            Request request = (Request) in.readObject();
                            System.out.println(timestamp() + " Received request: " + request.getType() + " - Key: " + request.getKey());
        
                            Response response = handleRequest(request);
                            out.writeObject(response);
                            out.flush();
                            System.out.println(timestamp() + " Sent response: " + response.getMessage());
                        } catch (EOFException e) {
                            break;
                        }
                    }

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println(timestamp() + " Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(timestamp() + " Server error: " + e.getMessage());
        }
    }


    private static Response handleRequest(Request request) {
        switch (request.getType()) {
            case PUT:
                store.put(request.getKey(), request.getValue());
                return new Response(true, "PUT successful for key: " + request.getKey());
            case GET:
                String value = store.get(request.getKey());
                if (value != null) {
                    return new Response(true, "Value: " + value);
                } else {
                    return new Response(false, "Key not found: " + request.getKey());
                }
            case DELETE:
                String deletedValue = store.delete(request.getKey());
                return deletedValue != null
                    ? new Response(true, "DELETE successful for key: " + request.getKey())
                    : new Response(false, "Key not found: " + request.getKey());
            default:
                return new Response(false, "Invalid request type");
        }
    }
    

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}


