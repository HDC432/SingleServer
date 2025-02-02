package udp;

import common.Request;
import common.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UDPClient {
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        if (args.length != 2) {
            log("Usage: java UDPClient <server_ip> <port>");
            return;
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            InetAddress serverAddress = InetAddress.getByName(serverIp);

            while (true) {
                System.out.print("Enter command (PUT/GET/DELETE) or 'exit' to quit: ");
                String command = scanner.nextLine().trim().toUpperCase();

                if (command.equals("EXIT")) {
                    System.out.println("Disconnecting from server...");
                    break;
                }

                if (!(command.equals("PUT") || command.equals("GET") || command.equals("DELETE"))) {
                    System.out.println("Invalid command. Please enter PUT, GET, DELETE, or 'exit'.");
                    continue;
                }

                System.out.print("Enter key: ");
                String key = scanner.nextLine().trim();

                String value = null;
                if (command.equals("PUT")) {
                    System.out.print("Enter value: ");
                    value = scanner.nextLine().trim();
                }

                Request.Type type = Request.Type.valueOf(command);
                Request request = new Request(type, key, value);

                sendRequest(socket, serverAddress, port, request);
            }
        } catch (IOException | ClassNotFoundException e) {
            logError("Client error: " + e.getMessage());
        }
    }

    private static void sendRequest(DatagramSocket socket, InetAddress serverAddress, int port, Request request)
            throws IOException, ClassNotFoundException {

        byte[] requestData = serializeRequest(request);
        DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, port);
        socket.send(requestPacket);
        log("Sent " + request.getType() + " request for key: " + request.getKey());

        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(responsePacket);

        try (ByteArrayInputStream bais = new ByteArrayInputStream(responsePacket.getData(), 0, responsePacket.getLength());
             ObjectInputStream in = new ObjectInputStream(bais)) {

            Response response = (Response) in.readObject();
            log("Received response: " + response.getMessage());
        }
    }

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
