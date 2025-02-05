package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Purpose: Video tutorial on TCP
 * @author: Thomas Hartmann
 */
public class TCPdemo {
    private PrintWriter out;
    private BufferedReader in;
    private ServerSocket server;
    private Socket clientHandler;

    public void start(int port){
        try{
            server = new ServerSocket(port);
            System.out.println("Server is startet and running on port "+port);
            clientHandler = server.accept();
            out = new PrintWriter(clientHandler.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientHandler.getInputStream()));
            out.println("Hello new client. Greetings from the server");
            String inputLine;
            while((inputLine = in.readLine()) != null){
                System.out.println("Message from client: "+inputLine);
                if("bye".equals(inputLine)){
                    out.println("Good Bye. I am shutting down");
                    break;
                }
                out.println("Echo: "+inputLine);
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TCPdemo().start(12345);

    }
}
