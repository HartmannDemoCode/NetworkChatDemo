package dk.cphbusiness.demo02MultipleRequests;

import dk.cphbusiness.demo01Simple.SimpleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

/*
* Purpose of this demo is to show how to read the client request and send it back from the server and to show how to accept multiple requests from the client while keeping the connection open
* This Server can only accept ONE client at a time
* This is a TCP server example (not HTTP, wchich means it is not a web server and cannot work with a browser)
* Author: Thomas Hartmann
**/
public class EchoServer extends SimpleServer {
    /*
    * Purpose of this demo is
    */
    @Override
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port);
            clientSocket = serverSocket.accept(); // blocking call
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("Client connected: " + clientSocket.getInetAddress());
            // Send initial greeting
            out.println("Hello client, greetings from EchoServer");

            String inputLine;
            // Keep connection open until client sends a "!"
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from client: " + inputLine);
                if ("!".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }
                out.println(inputLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        EchoServer server = new EchoServer();
        server.start(8080);
        server.stop();
    }
}
