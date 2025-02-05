package dk.cphbusiness.demo05ThreadServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Purpose: A simple echo server that handles multiple clients using threads.
 * The server listens for incoming client connections and creates a new thread to handle each client.
 * The client handler thread reads messages from the client and echoes them back.
 * RUN THIS SERVER FIRST, THEN RUN telnet localhost 12345 IN THE TERMINAL TO CONNECT TO THE SERVER
 *
 * @author: Thomas Hartmann
 */
public class ThreadedServer {
    private static final int PORT = 12345; // Port number for the server

    public static void main(String[] args) {
        System.out.println("Echo server is running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept new client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle client in a new thread
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Purpose: Handles a single client connection as a Runnable (Thread) using the socket input/output streams to communicate.
     */
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                String message;
                out.println("Welcome to the Echo Server! Type 'bye' to disconnect.");

                // Echo messages back to the client
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    if ("bye".equalsIgnoreCase(message)) {
                        out.println("Goodbye!");
                        break;
                    }

                    out.println("Echo: " + message);
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}
