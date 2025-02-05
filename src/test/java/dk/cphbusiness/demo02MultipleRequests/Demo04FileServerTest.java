package dk.cphbusiness.demo02MultipleRequests;

import dk.cphbusiness.demo03RequestDataServer.RequestDataClient;
import dk.cphbusiness.demo03RequestDataServer.RequestDataServer;
import dk.cphbusiness.demo04FileServer.RequestFileClient;
import dk.cphbusiness.demo04FileServer.RequestFileServer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * Purpose of this demo is to show how to get a html file from the server.
 * Author: Thomas Hartmann
 */
class Demo04FileServerTest {
    private static RequestFileServer rfs = new RequestFileServer();
    private static Thread serverThread = null;

    @BeforeAll
    public static void setup() {
        System.out.println("setup");

    }

    @BeforeEach
    public void setupEach() {
        System.out.println("setupEach");
        serverThread = new Thread(() -> rfs.start(6669));
        serverThread.start();
    }

    @AfterEach
    public void tearDown() {
        System.out.println("tearDownEach");
//        rfs.stop();
        serverThread.interrupt();
//        rfs.stop(); // this will make the server throw an SocketException, but it is ok.
    }

    @Test
    @DisplayName("Test getting a file from the server")
    public void testGettingFileFromServer() {
        String httpRequest = "GET /pages/index.html HTTP/1.1\r\n" +
                "Host: " + "localhost";
        String response = new RequestFileClient().sendRequest(httpRequest, "localhost", 6669);
        String expected = "HTTP/1.1 200 OKContent-Type: text/htmlContent-Length: 90Connection: close<html><head><title>File Server Demo</title></head><body><h1>Hello World</h1></body></html>";
        assertEquals(expected, response);
    }

    @Test
    @DisplayName("Test getting a file from the server without index.html")
    public void testGettingFileFromServerNoHtml() {
        String httpRequest = "GET /pages HTTP/1.1\r\n" +
                "Host: " + "localhost";
        String response = new RequestFileClient().sendRequest(httpRequest, "localhost", 6669);
        System.out.println(response);
        String expected = "HTTP/1.1 200 OKContent-Type: text/htmlContent-Length: 90Connection: close<html><head><title>File Server Demo</title></head><body><h1>Hello World</h1></body></html>";
        assertEquals(expected, response);
    }
}