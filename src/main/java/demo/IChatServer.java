package demo;

import dk.cphbusiness.demo06ChatServer.ChatServer;

public interface IChatServer {
    void startServer(int port);
    void stopServer();
}
