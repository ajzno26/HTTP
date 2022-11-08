package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

// HTTPConnection represents a single client connection to this server.
public class HTTPConnection implements Runnable {
    // Already connected socket for one client.
    private Socket client;

    // Input channel 
    private Scanner socketInput;

    // Output channel.
    private DataOutputStream socketOutput;

    // Current state of this connection.
    private boolean connected;

    // Constructor
    HTTPConnection(Socket client) {
        this.client = client;
    }

    // Handle a single request message.
    private void processRequest() {
        try {
            var request = new HTTPRequest(socketInput);
            var response = new HTTPResponse(request, socketOutput);
            
            if (request.isValid()) {
                String url = request.getHostHeader() + request.getPath();
                System.out.println(client + ": Received valid request for " + url);
                System.out.println(client + ": Status Code: " + response.getStatusCode());
                response.sendResponseMessage();
                if (response.getStatusCode() == 404) {
                    connected = false;
                }
            }
            else {
                System.out.println(client + ": Received invalid request");
                System.out.println(client + ": Status Code: " + response.getStatusCode());
                response.sendResponseMessage();
                connected = false;
            }
        } catch (NoSuchElementException e) {
            connected = false;
        }
    }

    // Handle a new connection.
    @Override
    public void run() {
        System.out.println(client + ": Connected");
        connected = true;

        try {
            // Setup input and output streams for the client
            socketInput = new Scanner(client.getInputStream());
            socketOutput = new DataOutputStream(client.getOutputStream());

            // Keep proccessing requests from the client until it is disconnecte4d
            while(connected) {
                processRequest();
            }
        } catch(Exception e) {
            System.out.println(client + ": Socket error: " + e);
        } finally {
            // Close the client connection
            try {
                client.close();
            } catch (IOException e) {}
            System.out.println(client + ": Closed");
        }
    }
}
