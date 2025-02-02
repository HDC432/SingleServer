package udp;

import common.KeyValueStore;
import common.Request;
import common.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A UDP server that handles client requests to interact with a key-value store.
 */
public class UDPServer {
    private static final KeyValueStore store = new KeyValueStore();
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length != 1) {
            log("Usage: java UDPServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (DatagramSocket socket = new DatagramSocket(port)) {
            log("UDP Server started on port " + port);

            byte[] buffer = new byte[BUFFER_SIZE];

            // Loop to handle incoming requests
            while (true) {
                // Receive a request packet from a client
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                InetAddress clientAddress = requestPacket.getAddress();
                int clientPort = requestPacket.getPort();

                try (ByteArrayInputStream bais = new ByteArrayInputStream(requestPacket.getData(), 0, requestPacket.getLength());
                     ObjectInputStream in = new ObjectInputStream(bais)) {

                    Request request = (Request) in.readObject();
                    log("Received request from " + clientAddress + ":" + clientPort + " - Type: " + request.getType() + " - Key: " + request.getKey());

                    // Process the request and generate a response
                    Response response = handleRequest(request);
                    byte[] responseData = serializeResponse(response);

                    // Send the response back to the client
                    DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                    log("Sent response to " + clientAddress + ":" + clientPort + " - " + response.getMessage());

                } catch (IOException | ClassNotFoundException e) {
                    logError("Received malformed request from " + clientAddress + ":" + clientPort);
                }
            }
        } catch (IOException e) {
            logError("Server error: " + e.getMessage());
        }
    }


    /**
     * Processes a client request and generates a response.
     */
    private static Response handleRequest(Request request) {
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
     * Serializes a response object into a byte array.
     */
    private static byte[] serializeResponse(Response response) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(response);
            return baos.toByteArray();
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
