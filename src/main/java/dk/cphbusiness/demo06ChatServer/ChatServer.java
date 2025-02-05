package dk.cphbusiness.demo06ChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class ChatServer implements IChatServer {

    private Set<IClientObserver> clients = Collections.synchronizedSet(new HashSet<>()); // Use synchronizedSet to make the set thread-safe
    private static volatile ChatServer instance; // Use 'volatile' to ensure visibility of changes across threads.
    private ChatServer() {}  // Private constructor
    private static boolean running = false;
    private ServerSocket serverSocket;

    public static ChatServer getInstance() {
        if (instance == null) { // First check (no locking)
            synchronized (ChatServer.class) { // Lock only the first time
                if (instance == null) { // Second check inside synchronized block
                    instance = new ChatServer();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        ChatServer server = ChatServer.getInstance();
        server.start(12345);
    }

    @Override
    public void run(int port) {
        try( ServerSocket serverSocket = new ServerSocket(port)){
            this.serverSocket = serverSocket;
            while(running){
                ChatClientHandler client = new ChatClientHandler(serverSocket.accept());
                addClient(client);
                new Thread(client).start();
            }
        } catch (IOException ex) {
            System.err.println("Server encountered an error: " + ex.getMessage());
        }
    }

    @Override
    public void addClient(IClientObserver client) {
        clients.add(client);
    }

    @Override
    public void removeClient(IClientObserver client) {
        clients.remove(client);
    }

    @Override
    public void removeAllClients() {
        synchronized (clients) {
            for (IClientObserver client : clients) {
                if (client instanceof ChatClientHandler) {
                    ((ChatClientHandler) client).closeConnection();
                }
            }
            clients.clear(); // Clear the set after closing all connections
        }
    }

    @Override
    public void broadcast(String message) {
        synchronized (clients) { // Lock the set while iterating over it in case another thread tries to modify the set symoultaneously
            for (IClientObserver client : clients) {
                client.update(message);
            }
        }
    }

    @Override
    public void start(int port) {
        if (!running) {
            running = true; // Set the running flag to true
            new Thread(() -> run(port)).start(); // Start the server in a new thread
        }
    }

    @Override
    public synchronized void stop() {
        removeAllClients();
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Close the ServerSocket to unblock accept()
            }
        } catch (IOException ex) {
            System.err.println("Error while stopping the server: " + ex.getMessage());
        }
        System.out.println("Server has been stopped.");
    }


}
