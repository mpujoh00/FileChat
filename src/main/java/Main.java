import Client.Client;
import Server.Server;
import Application.ChatInterface;
import java.io.IOException;

public class Main {

    private static Server server;
    private static Client client;
    private static int portServer = 666;
    private static String ipLinuxServer = "127.0.0.1";
    private static int portLinuxServer = 666;

    public static void main(String[] args) throws IOException {

        // connects a client to the Linux server
        client = new Client();
        
        // interface
        //new Chat(server, client).setVisible(true);
        
        client.startConnection(ipLinuxServer, portLinuxServer);
        
        

    }

}