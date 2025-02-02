package tcp;

import common.Request;
import common.Response;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/**
 * A TCP client that interacts with a key-value store server.
 */
public class TCPClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <server_ip> <port>");
            return;
        }

        String serverIp = args[0];
        int port = Integer.parseInt(args[1]);

        // Connect to the server, send requests and receive responses
        try (Socket socket = new Socket(serverIp, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Loop to interact with the user
            while (true) {
                System.out.print("Enter command (PUT/GET/DELETE) or 'exit' to quit: ");
                String command = scanner.nextLine().trim().toUpperCase();

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

                // Send the request to the server
                out.writeObject(request);
                out.flush();

                // Receive and display the response from the server
                Response response = (Response) in.readObject();
                System.out.println("Received response: " + response.getMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
