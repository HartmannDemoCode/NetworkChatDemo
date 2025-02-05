package dk.cphbusiness.demo02MultipleRequests;

import dk.cphbusiness.demo01Simple.SimpleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoClient extends SimpleClient {

    @Override
    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Read and display the server's greeting message
            String serverGreeting = in.readLine();
            System.out.println("Server says: " + serverGreeting);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public static void main(String[] args) {
            EchoClient client = new EchoClient();
            client.startConnection("localhost", 8080);
            String response = client.sendMessage("Hello Echo Server");
            System.out.println("response: " + response);
            client.stopConnection();
        }
}
