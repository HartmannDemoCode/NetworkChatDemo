package dk.cphbusiness.demo06ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Purpose: To handle a single client in the chat server as a thread
 *
 * @author: Thomas Hartmann
 */
public class ChatClientHandler implements IClientObserver, Runnable {
    private Socket socket;
    private PrintWriter out;
    private String name;

    public ChatClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(),true);
    }

    @Override
    public void update(String message) {
        System.out.println(" received: " + message);
        out.println(message);
    }

    @Override
    public void run() {
        ChatServer server = ChatServer.getInstance();
        server.addClient(this);
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            // Request client name upon connection
            out.println("Welcome to the chat! Please enter your name:");
            this.name = in.readLine();
            server.broadcast(name + " has joined the chat!");

            String message;
            while((message = in.readLine()) != null){
                out.println("Echo: " + message);
                if("exit".equalsIgnoreCase(message)){
                    server.removeClient(this);
                    break;
                }
                if("stop".equalsIgnoreCase(message)){
                    server.stop();
                    break;
                }
                server.broadcast(name + ": " + message);
            }
        } catch (IOException ex) {
            System.err.println("Error handling client: " + ex.getMessage());
        } finally {
            server.removeClient(this);
            closeConnection();
        }
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}
