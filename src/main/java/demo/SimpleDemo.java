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
public class SimpleDemo {
    private ServerSocket server;
    private Socket clientHandler;
    private PrintWriter out;
    private BufferedReader in;


    public void start(){
        try {
            server = new ServerSocket(8080);
            clientHandler = server.accept();
            out = new PrintWriter(clientHandler.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientHandler.getInputStream()));
            System.out.println("Server started");
            out.println("Connection established");
            String inputLine;
            while((inputLine = in.readLine()) != null){
                System.out.println("Message from client: "+inputLine);
                if("Stop".equals(inputLine)){
                    break;
                }
            }
        } catch(IOException ex){
            //TODO: remember production ready
           ex.printStackTrace();
        } finally {
            try {
                stop();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void stop() throws IOException {
        in.close();
        out.close();
        clientHandler.close();
        server.close();
    }

    public static void main(String[] args) {
        new SimpleDemo().start();
    }
}
