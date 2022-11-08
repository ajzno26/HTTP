# HTTP

## Instructions
Clone the repository in your local terminal: 

    $ git clone git@github.com:ajzno26/HTTP.git

cd into the HTTP directory:

    $ cd HTTP

Makefile to build the application:

    $ make

Install telnet: 

    $ sudo apt update
    $ sudo apt install telnet 

### Server 
Run the HTTP Server: 

    $ sudo java http.HTTPServer

### Client
To add a new client connection, open up a new terminal and cd into the HTTP directory. 
    
    $ telnet localhost 80 
    Trying 127.0.0.1…
    Connected to localhost.
    Escape character is '^]'.

Type a HTTP request message in the terminal. For example, 

    $ GET / HTTP/1.1
    $ Host: localhost 

### Note* 
Every request must have exactly three fields on the request line: ACTION, Path, and HTTP Version. 

It only accepts Get action and version HTTP/1.1. The Path must start with a “/”. 

Header variables must be in the form of VARIABLE: VALUE, and have at least the Host header defined. 

### Firefox 

Make sure the HTTP server is running. 

Open Firefox and go to: http://localhost/

Also check the output printed on the server side. 

Try: http://localhost/test.html

Try: http://localhost/nope.html 


## Program Architecture
### HTTP Server
The server accepts new client connections on port 80. It creates a thread pool with 100 threads and each new connection is handled by one thread. The server validates all incoming request messages and responds to them. 

### HTTP Connection
HTTPConnection.java sets up input and output streams for the client. It constantly processes requests from the client, checks whether they are valid or not, and sends proper responses back to the server. 

### HTTP Request 
HTTPRequest.java reads all incoming request messages and parses header variables into a hashmap. It checks whether the request is valid or not. It returns the path and Host header variable of a valid request message.  

### HTTP Response 
HTTPResponse.java handles all incoming request messages and sends proper response messages to the server. It prints the status code and the status on the server side, and prints the response message on the client side. If the request is invalid, the client connection is closed. 

Every response has at least 4 header variables: Server, Date, Content-Length, and Content-Type. 

Valid request message causes the server to send an HTTP 200 OK response with the requested content. 

Invalid request message causes the server to send back an HTTP 400 Bad Request response with the errors/400.html page or 404 Not found response with errors/404.html page. 

If any errors are encountered, TCP connection is closed. 

### Content
content/index.html: default web page to load

content/test.html: another test page with an image embedded 

content/parrot.gif: test image for test.html 

### Errors
errors/400.html: error page to send when a bad request is encountered

errors/404.html: error page to send when a UA requests a non-existent file/page
