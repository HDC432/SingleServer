package udp;

import common.Request;
import common.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;


/**
 * A UDP client that interacts with a key-value store server.
 */
public class UDPClient {
    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT = 5000;

    public static void main(String[] args) {
        if (args.length != 2) {
            log("Usage: java UDPClient <server_ip> <port>");
            return;
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        // Create a UDP socket
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(serverIp);
            socket.setSoTimeout(TIMEOUT);

            // Loop to interact with the user
            while (true) {
                System.out.print("Enter command (PUT/GET/DELETE) or 'exit' to quit: ");
                String command = scanner.nextLine().trim().toUpperCase();
                // Exit the client if the user types 'exit'
                if (command.equals("EXIT")) {
                    System.out.println("Disconnecting from server...");
                    break;
                }
                // Validate the command
                if (!(command.equals("PUT") || command.equals("GET") || command.equals("DELETE"))) {
                    System.out.println("Invalid command. Please enter PUT, GET, DELETE, or 'exit'.");
                    continue;
                }

                // Get the key from the user
                System.out.print("Enter key: ");
                String key = scanner.nextLine().trim();

                // Get the value from the user
                String value = null;
                if (command.equals("PUT")) {
                    System.out.print("Enter value: ");
                    value = scanner.nextLine().trim();
                }

                // Create a request object
                Request.Type type = Request.Type.valueOf(command);
                Request request = new Request(type, key, value);

                // Send the request to the server and receive the response
                sendRequest(socket, serverAddress, port, request);
            }
        } catch (IOException | ClassNotFoundException e) {
            logError("Client error: " + e.getMessage());
        }
    }


    /**
     * Sends a request to the server and waits for a response.
     */
    private static void sendRequest(DatagramSocket socket, InetAddress serverAddress, int port, Request request)
            throws IOException, ClassNotFoundException {

        // Serialize the request into a byte array
        byte[] requestData = serializeRequest(request);
        DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, port);
        socket.send(requestPacket);
        log("Sent " + request.getType() + " request for key: " + request.getKey());

        // Receive the response from the server
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);

        try {
            socket.receive(responsePacket);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(responsePacket.getData(), 0, responsePacket.getLength());
                ObjectInputStream in = new ObjectInputStream(bais)) {
                Response response = (Response) in.readObject();
                log("Received response: " + response.getMessage());
            }
        }catch (SocketTimeoutException e) {
            logError("Timeout waiting for response to " + request.getType() + " request for key: " + request.getKey());
        }
    }


    /**
     * Serializes a request object into a byte array.
     */
    private static byte[] serializeRequest(Request request) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(request);
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
