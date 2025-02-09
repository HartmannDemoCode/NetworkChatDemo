package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Purpose: Building on MultiClientServer we now identify each client with a name
 *
 * @author: Thomas Hartmann
 */
public class ProtocolDemo {
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
        new ProtocolDemo().run(12345);
    }

    private static class ClientHandler implements Runnable{
        private Socket clientSocket;
        private String name;
        public ClientHandler(Socket socket){
            this.clientSocket = socket;
        }
        @Override
        public void run(){
            try {
                System.out.println("Client registered");
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println("Hello new client. Please enter your name like this: \"#name: <your name>\"");
                String msg;
                while ((msg = in.readLine()) != null) { // Read each line comming from the client
                    if (msg.trim().equals("Over and Out")) {
                        break; // Exit loop if termination message is received
                    }
                    if(msg.trim().startsWith("#name:")){
                        this.name = msg.split(" ")[1];
                        out.println("Hello "+this.name+" pleased to meet you. You can always end this conversation by entering: \"Over and Out\"");
                    }
//                    System.out.println("Message from client: " + msg);
                    out.println("We received this message: " + msg + " from "+this.name);
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
