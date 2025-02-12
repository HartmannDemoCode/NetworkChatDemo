package demo;

import dk.cphbusiness.demo06ChatServer.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Purpose: To make a server, that can handle references to multiple clients and register a new client with name
 *
 * @author: Thomas Hartmann
 */
public class Chat1Demo implements IChatServer, IObservable {

    private static volatile Chat1Demo instance; // Use 'volatile' to ensure visibility of changes across threads.
    private ServerSocket server;
    private static List<ClientHandler> clientHandlers = new ArrayList();

    private Chat1Demo() {}  // Private constructor

    public static Chat1Demo getInstance() {
        if (instance == null) {
            synchronized (Chat1Demo.class) {
                if (instance == null) {
                    instance = new Chat1Demo();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        getInstance().startServer(12345);
    }

    // Purpose: To start a new ServerSocket and endlessly await client connections
    @Override
    public void startServer(int port) {
        try {

            server = new ServerSocket(port);
            System.out.println("Server ready to recieve requests");
            while (true) {
                Socket clientSocket = server.accept();
                // Create a container for the client socket, so that we can have each end of the communication pipe (in, out).
                Runnable clientHandler = new ClientHandler(clientSocket, this);
                addObserver((IObserver) clientHandler);
                new Thread(clientHandler).start();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            stopServer();
        }
    }

    @Override
    public void addObserver(IObserver observer) {
        clientHandlers.add((ClientHandler) observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        clientHandlers.remove((ClientHandler) observer);
    }

    @Override
    public synchronized void broadcast(String msg) { // No other thread should add or remove clients while notifying
        for (ClientHandler clientHandler : clientHandlers) {
            if(!clientHandler.equals(this)) {
                clientHandler.notify(msg);
            }
        }
    }

    @Override
    public void stopServer() {
        try {
            broadcast("#MESSAGE Server is shutting down");
            clientHandlers.forEach(ClientHandler::cleanup);
            clientHandlers.clear();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable, IObserver {
        private IObservable chatServer;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String name;

        public ClientHandler(Socket socket, IObservable chatServer) {
            try {
                this.chatServer = chatServer;
                this.clientSocket = socket;
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                System.out.println("Client registered");
                out.println("Hello new client. Please join by entering your name like this: \"JOIN: <your name>\"");
                String msg;
                while ((msg = in.readLine()) != null) { // Read each line comming from the client
                    if (msg.trim().equals("#STOP")) {
                        ((IChatServer) chatServer).stopServer();
                        break; // Exit loop if termination message is received
                    }
                    if (msg.trim().startsWith("#JOIN")) {
                        this.name = msg.split(" ")[1];
                        out.println("Hello " + this.name + " pleased to meet you. You can always end this conversation by entering: \"Over and Out\"");
                        chatServer.broadcast("#MESSAGE " + this.name + " has joined the chat");
                    }
                    if (msg.trim().startsWith("#MESSAGE ")) {
                        chatServer.broadcast(msg.substring(9)); // Remove the #Message from the message
                    }
                    if (msg.trim().startsWith("#PRIVATE ")) {
                        String[] parts = msg.split(" ");
                        String receiver = parts[1];
                        String message = msg.substring(9 + receiver.length()+1); // Remove the #PRIVATE <receiver> from the message
                        for (ClientHandler clientHandler : clientHandlers) {
                            if (clientHandler.name.equals(receiver)) {
                                clientHandler.notify("Private message from " + this.name + ": " + message);
                            }
                        }
                    }
                    out.println("We received this message: '" + msg + "' from " + this.name);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                cleanup();
            }
        }

        @Override
        public void notify(String msg) {
            if (out != null)
                out.println(msg);
        }


        private void cleanup() {
            try {
                chatServer.removeObserver(this);
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}