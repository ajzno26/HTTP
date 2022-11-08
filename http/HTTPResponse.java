package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

// Class representing a single HTTP response message.
public class HTTPResponse {
    
    // Declare global variables
    private HTTPRequest request;
    private DataOutputStream output; 
    private String path;
    private Path filePath; 
    private int statusCode; 
    private String status; 
    private HashMap<String, String> headerCollections;

    // Constructor
    public HTTPResponse(HTTPRequest request, DataOutputStream output) {
        this.request = request; 
        this.output = output;
        handleRequest();
        storeHeaderFields();
    }

    public int getStatusCode() { return statusCode; }
    private String statusLine() { return "HTTP/1.1 " + Integer.toString(statusCode) + " " + status; }
    
    private void handleRequest() {
        path = request.getPath(); 
        String currentDir = Paths.get("").toAbsolutePath().toString(); 
       
        filePath = Paths.get(currentDir + "/content" + path);
        if (path.equals("/")) {
            filePath = Paths.get(currentDir + "/content/index.html");
        } else{
            filePath = Paths.get(currentDir + "/content" + path);
        }

        // Set status, status code, filepath according to request 
        if (!request.isValid()) {
            statusCode = 400;
            status = "Bad Request";
            filePath = Paths.get(currentDir + "/errors/400.html");
        } else if (!Files.exists(filePath)) {
            statusCode = 404;
            status = "Not Found";
            filePath = Paths.get(currentDir + "/errors/404.html");
        } else {
            statusCode = 200; 
            status = "OK";
        }
    }

    // Store 4 header fields in a hashmap 
    private void storeHeaderFields() {
        try {
            headerCollections = new HashMap<String, String>();
            long contentLength = Files.size(filePath);
            String contentType = Files.probeContentType(filePath);

            headerCollections.put("Server", "username");
            headerCollections.put("Content-Length", Long.toString(contentLength));
            headerCollections.put("Date", date());
            headerCollections.put("Content-Type", contentType);
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }

    // Return date in HTTP format
    private String date() {
        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O", Locale.US);
        String dateToday = dtFormatter.format(ZonedDateTime.now(ZoneOffset.UTC));
        return dateToday; 
    }

    // Return HTTP body message as a byte array
    private byte[] HTTPBody() {
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(filePath);
        } catch (IOException e) {
            System.out.println("Error!");
        }
        return fileContent;
    }

    public void sendResponseMessage() {
        try {
            output.writeBytes(statusLine() + "\r\n");
            for (Map.Entry<String, String> set : headerCollections.entrySet()) {
                output.writeBytes(set.getKey() + ": " + set.getValue() + "\r\n");
            }
            output.writeBytes("\r\n");
            output.write((byte[])HTTPBody());
            output.writeBytes("\r\n");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
}
