package dk.cphbusiness.demo06ChatServer;

public interface IChatServer {
    void addClient(IClientObserver client); // add a client to the server
    void removeClient(IClientObserver client); // remove a client from the server
    void removeAllClients(); // remove all clients from the server
    void broadcast(String message); // call the update method on all clients
    void run(int port);
    void stop();
    void start(int port);

//    void removeClient(IClientObserver client); // remove a client from the server
}
