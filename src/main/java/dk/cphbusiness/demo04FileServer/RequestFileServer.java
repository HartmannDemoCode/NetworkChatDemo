package dk.cphbusiness.demo04FileServer;

import dk.cphbusiness.demo03RequestDataServer.RequestDataServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Purpose of this demo is to show how to get the data from the request headers etc.
 * Author: Thomas Hartmann
 */
public class RequestFileServer extends RequestDataServer {
    public static void main(String[] args) {
        RequestFileServer server = new RequestFileServer();
        server.start(8080);
    }

    @Override
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
            while(true) { // keep listening (as long as the server is running)

                try {
                    clientSocket = serverSocket.accept(); // blocking call
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                    // read the request from the client
                    RequestDTO requestDTO = generateRequestObject(in);
                    String requestLine = requestDTO.getRequestLine();
                    String ressource = requestLine.split(" ")[1];
                    System.out.println(ressource);
                    if(ressource.endsWith(".ico")) { //browser sends request for favicon.ico
//                        clientSocket.close();
                        continue;
                    }



                    // Get the file from the ressource

                    //write the response to the client
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println("Connection: close");
                    out.println(); // blank line between headers and content, very important !

                    System.out.println("Ressource: " + ressource);
                    if(ressource.equals("/input/handleForm")){
                        out.println(requestDTO.getRequestBody());
                    }
                    else if(!ressource.startsWith("/pages")) {
                        clientSocket.close();
                        continue;
                    } else {
                        String response = getFile(ressource);
                        out.println("Content-Length: " + response.length());
                        out.println(response);
                    }


//                    clientSocket.getOutputStream().write(response.getBytes());

                    // Close the socket
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
    }

    private String getFile(String ressource) {
        ressource = reformatRessource(ressource); // remove leading / and add .html if not present

        String response = "";
        Path path = Path.of(ressource);

        try {
            // Get the URI of the resource using getResource
            URI resourceUri = RequestFileServer.class.getClassLoader().getResource(ressource).toURI();
            // Use Paths.get with the URI to create a Path
            Path resourcePath = Paths.get(resourceUri);

            // Read the content of the resource using Files.readString
            response = Files.readString(resourcePath);

//            String content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File not found");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
    private String reformatRessource(String ressource) {
        if (!ressource.endsWith(".html")) {
            ressource += "/index.html";
        }
        if (ressource.equals("/")) {
            ressource = "index.html";
        }
        if (ressource.startsWith("/")) {
            ressource = ressource.substring(1);
        }
        return ressource;
    }
}