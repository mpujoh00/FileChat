package Server;

import Objects.Chat;
import Objects.FileMessage;
import Objects.Request;
import Objects.RequestType;
import Objects.Response;
import Objects.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private ServerSocket serverSocket;
    private Database database;

    // starts running the server
    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port " + port);
        connectToDatabase();

        // waits until a client requests connection with the server
        while(true) {
                // accepts the request and creates a thread for that client
                System.out.println("Waiting for a client connection request");
                new ClientHandler(serverSocket.accept(), database).start();  
        }
    }

    // stops running the server
    public void stop() throws IOException {
        // clientSocket.close();
        serverSocket.close();
    }
    
    public void connectToDatabase(){
        database = new Database();
    }
    
    public void disconnectDatabase() {
        database.closeConnection();
    }


    // each thread represents a client
    private static class ClientHandler extends Thread {

        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private Database database;
        private boolean exit;
        private User currentUser;

        public ClientHandler(Socket socket, Database database) {

            this.clientSocket = socket;
            this.database = database;
            System.out.println("New client connected!");
            this.exit = false;
        }

        public void run() {
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                               
                while(!exit){
                    // gets client's requests
                    Request request;
                    while ((request = (Request)in.readObject()) != null) {
                        attendRequest(request);
                    }
                }
                disconnectUser();
                in.close();
                out.close();
                clientSocket.close();
                System.out.println("Client disconnected :(");
                
            } catch (IOException | ClassNotFoundException ex) {}
        }

        public void attendRequest(Request request){
            User user;
            Response response;
            String input;
            FileMessage fileMessage;
            try{
                RequestType type = request.getType();
                switch(type){                    
                    case MESSAGE:
                        // reads client's message and responds
                        input = (String)request.getObject();
                        System.out.println(input);
                        out.writeObject("Server - Message received");
                        break;
                    
                    case REGISTER_USER:
                        registerUser((User)request.getObject());
                        break;
                        
                    case LOGIN_USER:
                        loginUser((User)request.getObject());                       
                        break;
                    
                    case SHUTDOWN:
                        input = (String)request.getObject();
                        System.out.println(input);
                        out.writeObject("Server - Bye!");
                        exit = true;
                        break;
                        
                    case GET_CURRENT_USER:
                        out.writeObject(new Response(200, currentUser));
                        break;
                    
                    case GET_USER:
                        input = (String)request.getObject();
                        user = getUser(input);
                        if(user != null){
                            response = new Response(200, user);
                        }else{
                            response = new Response(400);
                        }
                        out.writeObject(response);
                        break;
                    
                    case CREATE_CHAT:
                        input = (String)request.getObject();
                        user = getUser(input);
                        createChat(user);
                        break;
                        
                    case SEND_FILE:
                        fileMessage = (FileMessage)request.getObject();
                        receiveFile(fileMessage);
                        break;
                        
                    case GET_FILES:
                        int chat = (Integer)request.getObject();
                        getFilesFromChat(chat);
                        break;
                        
                    case CHANGE_EXTENSIONS:
                        input = (String)request.getObject();
                        changeExtensions(input);
                        break;
                        
                    case CHECK_USER_AVAILABLE:
                        input = (String)request.getObject();
                        checkUserAvailable(input);
                }
            }
            catch(IOException e){
            }
            
        }
        
        public void receiveFile(FileMessage fileMessage){
            
            int code = 200;
            try {
                database.createFile(fileMessage);
            } catch (SQLException ex) {
                code = 500;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                code = 404;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                out.writeObject(new Response(code));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void getFilesFromChat(int chatId){
            
            int code = 200;
            ArrayList<FileMessage> files = new ArrayList<>();
            try {
                files = (ArrayList<FileMessage>) database.getFilesFromChat(chatId);
            } catch (SQLException ex) {
                code = 500;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                code = 400;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                out.writeObject(new Response(code, files));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void changeExtensions(String extensions){
            
            int code = 200;
            currentUser.setAcceptedExtensions(extensions);
            try {
                database.changeAcceptedExtensions(currentUser);
            } catch (SQLException ex) {
                code = 500;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                out.writeObject(new Response(code));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void createChat(User friend){
                     
            int code = 200;
            Response response;
            
            // checks if the user exists
            if(friend == null){
                response = new Response(404);
            }
            else{
                // checks if the chat already exists
                Chat chat = getChat(friend);
                if(chat == null){  // chat doesn't exist
                    chat = new Chat(currentUser.getId(), friend.getId());
                    try {
                        // creates the chat
                        database.createChat(chat);
                    } catch (SQLException ex) {
                        code = 500;
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Chat newChat = getChat(friend);

                    response = new Response(code, newChat);
                }
                else{  // chat already existed
                    code = 402;
                    response = new Response(code, chat);
                }  
            }
            // sends the response
            try {
                out.writeObject(response);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        
        public Chat getChat(User friend){
            
            return database.getChat(new Chat(currentUser.getId(), friend.getId()));
        }
        
        public void registerUser(User user){
            
            int code = 200;
            boolean alreadyExists = checkUsernameExists(user.getUsername());

            if(alreadyExists){
                code = 400;
            }else{
                try {
                    database.registerUser(user);
                } catch (SQLException ex) {
                    code = 500;
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.currentUser = getUser(user.getUsername());
            }
            // sends response
            try {
                out.writeObject(new Response(code));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void loginUser(User user){
            
            int code = 200;
            User databaseUser = getUser(user.getUsername());
            
            if(databaseUser == null){  // user doesn't exist
                code = 400;
            }
            else if(!databaseUser.getPassword().equals(user.getPassword())){  // incorrect password
                code = 401;               
            }
            else{  // success
                this.currentUser = databaseUser;
                connectUser();
            }
            // sends response
            try {
                out.writeObject(new Response(code));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void checkUserAvailable(String username){
            
            User user = getUser(username);
            try {
                out.writeObject(new Response(200, user.isAvailable()));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void connectUser(){
            
            try {
                database.connectUser(currentUser);
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void disconnectUser(){
            
            try {
                database.disconnectUser(currentUser);
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public boolean checkUsernameExists(String username){
            
            User user = getUser(username);            
            if(user != null){
                return true;
            }
            else{
                return false;
            }
        }
        
        public User getUser(String username){
            return database.findUser(username);
        }
        
    }	
	
}