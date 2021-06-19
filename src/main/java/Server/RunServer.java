/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;

/**
 *
 * @author maybeitsmica
 */
public class RunServer {
    
    private static Server server;
    private static int portServer = 666;
    
    public static void main(String[] args) throws IOException{
        // turns the server on
        server = new Server();
        server.start(portServer);
    }
    
}
