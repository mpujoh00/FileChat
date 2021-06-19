package Server;

import Objects.Chat;
import Objects.FileMessage;
import Objects.Request;
import Objects.RequestType;
import Objects.Response;
import Objects.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
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
                }
            }
            catch(IOException e){
            }
            
        }
        
        public void receiveFile(FileMessage fileMessage){
            
            database.createFile(fileMessage);
            try {   
                out.writeObject(new Response(200));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void getFilesFromChat(int chatId){
            
            ArrayList<FileMessage> files = (ArrayList<FileMessage>) database.getFilesFromChat(chatId);
            try {
                out.writeObject(new Response(200, files));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void createChat(User friend){
                                    
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

                    // creates the chat
                    database.createChat(chat);
                    Chat newChat = getChat(friend);

                    response = new Response(200, newChat);
                }
                else{  // chat already existed
                    response = new Response(402, chat);
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
            
            Response response;
            boolean alreadyExists = checkUsernameExists(user.getUsername());

            if(alreadyExists){
                response = new Response(400);
            }else{
                response = new Response(200);
                database.registerUser(user);
                this.currentUser = getUser(user.getUsername());
            }
            // sends response
            try {
                out.writeObject(response);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void loginUser(User user){
            
            Response response;
            User databaseUser = getUser(user.getUsername());
            
            if(databaseUser == null){  // user doesn't exist
                response = new Response(400);
            }
            else if(!databaseUser.getPassword().equals(user.getPassword())){  // incorrect password
                response = new Response(401);                
            }
            else{  // success
                response = new Response(200);
                this.currentUser = databaseUser;
            }
            // sends response
            try {
                out.writeObject(response);
            } catch (IOException ex) {
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