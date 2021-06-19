package Client;

import Objects.Chat;
import Objects.Request;
import Objects.RequestType;
import Objects.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import Objects.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Client {

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // starts a connection with the Linux server
    public void startConnection(String ip, int port) throws UnknownHostException, IOException {
        clientSocket = new Socket(ip, port);
        
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        
        System.out.println("Connected to the server!");
        try {
            sendMessage("Starting connection!");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // stops the connection with the server
    public void stopConnection() throws IOException, ClassNotFoundException {
        
        out.writeObject(new Request("Client - Shutting down...", RequestType.SHUTDOWN));      
        String response = (String)in.readObject();
        System.out.println(response);
        
        out.close();
        in.close();
        clientSocket.close();
    }
    
    public boolean register(String username, String password){
        
        boolean answer = false;
        try {
            Request request = new Request(new User(username, password), RequestType.REGISTER_USER);
            out.writeObject(request);
            
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            if(response.getCode() == 200){  // user created correctly
                answer = true;
            }                                      
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    } 
    
    public int login(String username, String password){
        
        int code = 400;
        try {
            Request request = new Request(new User(username, password), RequestType.LOGIN_USER);
            out.writeObject(request);
            
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            code = response.getCode();
                        
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code;
    }
    
    public int newChat(String friendUsername) throws IOException, ClassNotFoundException{
        
        int code = 400;
        
        // checks if the user exists
        if(!checkUserExists(friendUsername)){
            return code;
        }
        // creates the chat
        Request request = new Request(friendUsername, RequestType.CREATE_CHAT);
        out.writeObject(request);

        // gets the chat
        Response response = (Response)in.readObject();
        System.out.println(response.printMessage());
        Chat chat = (Chat)response.getObject();
        
        return code;
    }
    
    public boolean checkUserExists(String username) throws IOException, ClassNotFoundException{
                
        Request request = new Request(username, RequestType.GET_USER);
        try {
            out.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response response = (Response)in.readObject();
        if(response.getCode() == 200){
            return true;
        }else{
            return false;
        }

    }
    
    public void sendMessage(String message) throws IOException, ClassNotFoundException{
        
        Request request = new Request("Client - " + message, RequestType.MESSAGE);
        out.writeObject(request);
        String response = (String)in.readObject();
        System.out.println(response);
    }
}
