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
            log("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server started on port " + port);

            while (true) { 
                Socket clientSocket = serverSocket.accept();
                log("New client connected from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            logError("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) { 
                try {
                    Request request = (Request) in.readObject();
                    log("Received request from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +
                            " - Type: " + request.getType() + " - Key: " + request.getKey());

                    Response response = handleRequest(request);
                    out.writeObject(response);
                    out.flush();
                    log("Sent response to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +
                            " - " + response.getMessage());

                } catch (EOFException e) {
                    log("Client disconnected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    logError("Error while handling client: " + e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            logError("Error with client connection: " + e.getMessage());
        }
    }

    private static Response handleRequest(Request request) {
        switch (request.getType()) {
            case PUT:
                store.put(request.getKey(), request.getValue());
                return new Response(true, "PUT successful for key: " + request.getKey());
            case GET:
                String value = store.get(request.getKey());
                return value != null ? new Response(true, "Value: " + value)
                        : new Response(false, "Key not found: " + request.getKey());
            case DELETE:
                String deletedValue = store.delete(request.getKey());
                return deletedValue != null ? new Response(true, "DELETE successful for key: " + request.getKey())
                        : new Response(false, "Key not found: " + request.getKey());
            default:
                return new Response(false, "Invalid request type");
        }
    }

    private static void log(String message) {
        System.out.println(timestamp() + " " + message);
    }

    private static void logError(String message) {
        System.err.println(timestamp() + " [ERROR] " + message);
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
