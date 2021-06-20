package Client;

import Objects.Chat;
import Objects.FileMessage;
import Objects.Request;
import Objects.RequestType;
import Objects.Response;
import java.io.IOException;
import java.net.*;
import Objects.User;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;

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
    
    public boolean checkUserConnected(String username){
        
        boolean isAvailable = false;
        try {
            Request request = new Request(username, RequestType.CHECK_USER_AVAILABLE);
            out.writeObject(request);
            
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            isAvailable = (boolean)response.getObject();
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isAvailable;
    }
    
    public User getCurrentUser(){
        
        User user = null;
        try {
            out.writeObject(new Request(RequestType.GET_CURRENT_USER));
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            user = (User)response.getObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    public User getUser(String username){
        
        User user = null;
        try {
            out.writeObject(new Request(username, RequestType.GET_USER));
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            user = (User)response.getObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    public User getUser(int id){
        
        User user = null;
        try {
            out.writeObject(new Request(id, RequestType.GET_USER_BY_ID));
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            user = (User)response.getObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }
    
    public FileMessage sendFile(File file, int chatId){
        
        FileMessage fileMessage = null;
        try {
            // gets current user
            Request getUserRequest = new Request(RequestType.GET_CURRENT_USER);
            out.writeObject(getUserRequest);
            Response getUserResponse = (Response)in.readObject();
            User user = (User)getUserResponse.getObject();
                        
            // sends the file
            fileMessage = new FileMessage(file, chatId, user.getId(), file.getName());
            Request request = new Request(fileMessage, RequestType.SEND_FILE);
            out.writeObject(request);
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileMessage;
    }
    
    public List<FileMessage> getFilesFromChat(int chatId){
        
        ArrayList<FileMessage> files = new ArrayList<>();
        try {
            Request request = new Request(chatId, RequestType.GET_FILES);
            out.writeObject(request);
           
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            files = (ArrayList<FileMessage>)response.getObject();
                        
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return files;
    }
    
    public int changeAcceptedExtensions(String extensions){
        
        int code = 400;
        try {
            out.writeObject(new Request(extensions, RequestType.CHANGE_EXTENSIONS));
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            code = response.getCode();
        } catch (IOException ex) {
            code = 404;
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            code = 404;
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return code;
    }
    
    public String getAcceptedExtensions(){
        
        User user = getCurrentUser();
        if(user == null){
            return "";
        }
        return user.getExtensions();
    }
    
    public String getAcceptedExtensions(String username){
        
        User user = null;
        try {
            out.writeObject(new Request(username, RequestType.GET_USER));
            Response response = (Response)in.readObject();
            System.out.println(response.printMessage());
            user = (User)response.getObject();
        } catch (Exception e) {
        } 
        if(user == null){
            return "";
        }
        return user.getExtensions();
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
                        
        } catch (IOException ex){
            System.err.println(ex);
            code = 404;
        }catch (ClassNotFoundException ex) {
            System.err.println(ex);
            code = 404;
        }
        return code;
    }
    
    public Chat newChat(String friendUsername) throws IOException, ClassNotFoundException{
        
        // checks if the user exists
        if(!checkUserExists(friendUsername)){
            return null;
        }
        // creates the chat
        Request request = new Request(friendUsername, RequestType.CREATE_CHAT);
        out.writeObject(request);

        // gets the chat
        Response response = (Response)in.readObject();
        System.out.println(response.printMessage());
        Chat chat = (Chat)response.getObject();
        
        return chat;
    }
    
    public List<Chat> getChats(){
        
        Request request = new Request(RequestType.GET_CHATS);
        try {
            out.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response response;
        ArrayList<Chat> chats = new ArrayList<>();
        try {
            response = (Response)in.readObject();
            System.out.println(response.printMessage());
            chats = (ArrayList<Chat>)response.getObject();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chats;
    }
    
    public boolean checkUserExists(String username) throws IOException, ClassNotFoundException{
                
        Request request = new Request(username, RequestType.GET_USER);
        try {
            out.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        Response response = (Response)in.readObject();
        System.out.println(response.printMessage());
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
