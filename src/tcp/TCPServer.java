package tcp;

import common.KeyValueStore;
import common.Request;
import common.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A TCP server that handles client requests to interact with a key-value store.
 */
public class TCPServer {
    private static final KeyValueStore store = new KeyValueStore();

    public static void main(String[] args) {
        // Check if port number is provided
        if (args.length != 1) {
            log("Usage: java TCPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        // Create server socket and make it run forever
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server started on port " + port);
            while (true) { 
                Socket clientSocket = serverSocket.accept();
                log("New client connected from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                // Handle each client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            // Log server errors
            logError("Server error: " + e.getMessage());
        }
    }


    /**
     * Handles communication with a connected client.
     */
    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // Keep reading requests from the client
            while (true) { 
                try {
                    Request request = (Request) in.readObject();
                    log("Received request from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() +
                            " - Type: " + request.getType() + " - Key: " + request.getKey());

                    // Process request and send it to the server
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


    /**
     * Processes a client request and generates a response.
     */
    private static Response handleRequest(Request request) {
        // Check request type
        switch (request.getType()) {
            case PUT:
                // Store key-value pair
                store.put(request.getKey(), request.getValue());
                return new Response(true, "PUT successful for key: " + request.getKey());
            case GET:
                // Get value for the key
                String value = store.get(request.getKey());
                return value != null ? new Response(true, "Value: " + value)
                        : new Response(false, "Key not found: " + request.getKey());
            case DELETE:
                // Delete key-value pair
                String deletedValue = store.delete(request.getKey());
                return deletedValue != null ? new Response(true, "DELETE successful for key: " + request.getKey())
                        : new Response(false, "Key not found: " + request.getKey());
            default:
                return new Response(false, "Invalid request type");
        }
    }

    /**
     * Print log with timestamp.
     */
    private static void log(String message) {
        System.out.println(timestamp() + " " + message);
    }

    /**
     * Print error log with timestamp.
     */
    private static void logError(String message) {
        System.err.println(timestamp() + " [ERROR] " + message);
    }

    /**
     * Get current timestamp.
     */
    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
}
