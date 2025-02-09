package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Purpose: Demo to show the use of Socket, ServerSocket, And how to use threads
 * To handle multiple clients
 *
 * @author: Thomas Hartmann
 */
public class ThreadedMultiClientServer {
    private ServerSocket server;

    // Purpose: To start a new ServerSocket and endlessly await client connections
    public void run(int port) {
        try {

            server = new ServerSocket(port);
            System.out.println("Server ready to recieve requests");
            while(true) {
                Socket clientSocket = server.accept();
                // Create a container for the client socket, so that we can have each end of the communication pipe (in, out).
                Runnable clientHandler = new ClientHandler(clientSocket);
                // Starting new Thread with ClientHandler as the runnable
                new Thread(clientHandler).start();
            }

        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ThreadedMultiClientServer().run(12345);
    }

    private static class ClientHandler implements Runnable{
        private Socket clientSocket;
        public ClientHandler(Socket socket){
            this.clientSocket = socket;
        }
        @Override
        public void run(){
            try {
                System.out.println("Client registered");
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String msg;
                while ((msg = in.readLine()) != null) { // Read messages in a loop
                    System.out.println("Message from client: " + msg);
                    out.println("We received this message: " + msg);

                    if (msg.trim().equals("Over and Out")) {
                        break; // Exit loop if termination message is received
                    }
                }

                out.close();
                in.close();
                clientSocket.close();

            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }
}
