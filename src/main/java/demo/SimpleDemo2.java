package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class SimpleDemo2 {
    private ServerSocket server;
    private Socket clientHandler;
    private PrintWriter out;
    private BufferedReader in;

    public void run(int port) {
        try {
            server = new ServerSocket(port);
            clientHandler = server.accept();
            out = new PrintWriter(clientHandler.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientHandler.getInputStream()));
            System.out.println("Server ready to recieve requests");
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while((inputLine = in.readLine()) != null){
                System.out.println(inputLine);
                out.println(inputLine);
                sb.append(inputLine);

            }
//            String request = in.readLine();
//            System.out.println("Message from client: "+request);
//            out.println("Response from server");

        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SimpleDemo2().run(8080);
    }
}
