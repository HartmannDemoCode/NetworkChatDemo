package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class TCPClient {
    private Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new TCPClient().startConnection("localhost", 8080);
            out.println("Besked fra klienten som vi har kodet");
            String firstResonse = in.readLine();
            System.out.println(firstResonse);
            out.println("Second message to server");
            String secondResponse = in.readLine();
            System.out.println(secondResponse);
//        List<String> response = in.lines().collect(Collectors.toList());
//        for(String str: response){
//            System.out.println(str);
//        }
            String response = in.readLine();
            System.out.println(response);
        } catch(IOException ex){ex.printStackTrace();}
    }
}
