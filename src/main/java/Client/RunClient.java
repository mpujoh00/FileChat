/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Application.LoginInterface;
import java.io.IOException;

/**
 *
 * @author maybeitsmica
 */
public class RunClient {
   
    private static Client client;
    private static String ipLinuxServer = "127.0.0.1";
    private static int portLinuxServer = 666;
    
    public static void main(String[] args) throws IOException {

        client = new Client();
        
        // Login interface
        new LoginInterface(client).setVisible(true);
        
        // connects the client to the Linux server
        client.startConnection(ipLinuxServer, portLinuxServer);
    }
}
