package demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class ChatServerDemo implements IObservable {
    private List<IObserver> clients = new ArrayList<>();

    private static volatile ChatServerDemo instance;

    private ChatServerDemo(){}

    public static ChatServerDemo getInstance(){
        if(instance == null){
            instance = new ChatServerDemo();
        }
        return instance;
    }

    public static void main(String[] args) {
        new ChatServerDemo().startServer(12345);
    }

    public void startServer(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket client = server.accept();
                Runnable runnable = new ClientHandler(client, this);
                new Thread(runnable).start();
                IObserver clientHandler = (IObserver) runnable;
                clients.add(clientHandler);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addObserver(IObserver observer) {
        clients.add(observer);
    }

    @Override
    public void removeObserver(IObserver observer) {
        clients.remove(observer);
    }

    @Override
    public void broadcast(String msg){
        // TODO: Implement method
        for(IObserver observer : clients){
            observer.notify(msg);
        }
    }

    private static class ClientHandler implements Runnable, IObserver {

        private Socket client;
        private PrintWriter out;
        private BufferedReader in;
        private ChatServerDemo server;
        private String name = "Guest";
        private List<String> inappropriateWords =  Arrays.asList("Lort", "Dumme svin", "Idiot");

        public ClientHandler(Socket client, ChatServerDemo server) throws IOException {
            this.client = client;
            this.server = server;
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.out = new PrintWriter(client.getOutputStream(),true);
        }

        @Override
        public void run() {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
//                    out.println(msg);
                    if(msg.startsWith("#JOIN ")){
                        this.name = msg.split(" ")[1];
                        server.broadcast("New client joined the chat. Welcome to "+name);
                    }
                    if(msg.startsWith("#MESSAGE ")){
                        server.broadcast(name+": "+msg.substring(9));
                    }
                    if(msg.startsWith("#PRIVATE ")){
                        String[] strs = msg.split(" ");
                        String name = strs[1];
                        String message = msg.substring(strs[0].length()+strs[1].length()+2);
                        message = replaceInappropriateWords(message);
                        directMessage(name, message);
                    }
                    if (msg.trim().equals("#LEAVE")) {
                        server.broadcast("Client " + name + " has left the chat");
                        client.close();
                        in.close();
                        out.close();
                        server.clients.remove(this);
                        break;
                    }
                    if(msg.trim().equals("#LIST")){
                        out.println("List of clients currently in the chat:");
                        for(IObserver obs: server.clients){
                            ClientHandler ch = (ClientHandler) obs;
                            out.println(ch.name);
                        }
                    }
                    if(msg.trim().equals("#STOP")){
                        server.broadcast("Server is shutting down. Goodbye");
                        server.clients.clear();
                        server.removeObserver(this);
                        server = null;
                        client.close();
                        in.close();
                        out.close();
                        break;
                    }
                    if(msg.trim().equals("#PRIVATESUBLIST")){
                        String[] parts = msg.split(" ");
                        String[] names = parts[1].split(",");
                        Arrays.stream(names).forEach((name)->directMessage(name,parts[2]));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private String replaceInappropriateWords(String msg){
            for(String word: inappropriateWords){
                if(msg.contains(word)){
                    msg = msg.replace(word, "****");
                }
            }
            return msg;
        }

        @Override
        public void notify(String msg){
            out.println(msg);
        }

        public void directMessage(String name, String msg){
            for(IObserver obs: server.clients){
                ClientHandler ch = (ClientHandler) obs;
                if(ch.name.equals(name)){
                    ch.notify(this.name+": "+msg);
                }
            }
        }
    }
}
