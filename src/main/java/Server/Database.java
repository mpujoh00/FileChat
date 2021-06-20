package Server;

import Objects.Chat;
import Objects.FileMessage;
import Objects.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maybeitsmica
 */
public class Database {
    
    private final String url = "jdbc:mysql://remotemysql.com:3306/dq2fDwEP6r";
    private final String user = "dq2fDwEP6r";
    private final String password = "eCSdwdyUFv";
    private Connection connection;
    private PreparedStatement statement;
    
    public Database(){
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connected to the database!");
            
        } catch (ClassNotFoundException ex) {
            System.out.println("Couldn't connect to the database");
        } catch (SQLException ex) {
            System.out.println("Couldn't connect to the database");
        }
    }
    
    public void changeAcceptedExtensions(User user) throws SQLException{
        
        statement = connection.prepareStatement("UPDATE Users SET accepted_extensions=? WHERE id=?");
        statement.setString(1, user.getExtensions());
        statement.setInt(2, user.getId());
        statement.execute();
    }
    
    public void createFile(FileMessage file) throws SQLException, FileNotFoundException{
        
        statement = connection.prepareStatement("INSERT INTO Files (chat_id, from_id, file, file_name) VALUES (?,?,?,?)");
        statement.setInt(1, file.getChatId());
        statement.setInt(2, file.getUserFromId());            
        FileInputStream fis = new FileInputStream(file.getFile());            
        statement.setBinaryStream(3, fis);
        statement.setString(4, file.getFilename());
        statement.execute();
    }
    
    public List<FileMessage> getFilesFromChat(int chatId) throws SQLException, FileNotFoundException, IOException{
        
        ArrayList<FileMessage> files = new ArrayList<FileMessage>();
        statement = connection.prepareStatement("SELECT * FROM Files WHERE chat_id=?");
        statement.setInt(1, chatId);
        ResultSet result = statement.executeQuery();

        while(result.next()){
            int id = result.getInt("id");
            int fromId = result.getInt("from_id");
            String filename = result.getString("file_name");

            // reads file
            File file = new File("dowloadedFiles/" + filename);
            FileOutputStream output = new FileOutputStream(file);

            InputStream input = result.getBinaryStream("file");
            byte[] buffer = new byte[1024];
            while (input.read(buffer) > 0) {
                output.write(buffer);
            }
            files.add(new FileMessage(id, file, chatId, fromId, filename));
        }
        return files;
    }
    
    public void registerUser(User user) throws SQLException{
        
        statement = connection.prepareStatement("INSERT INTO Users (username, password, accepted_extensions, is_available)"
                + " VALUES (?,?,?,?)");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getExtensions());
        statement.setBoolean(4, user.isAvailable());
        statement.execute();
    }
    
    public User findUser(String username){
        
        try {
            statement = connection.prepareStatement("SELECT * FROM Users WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            
            if(result.next()){  // user exists
                
                int id = result.getInt("id");
                String password = result.getString("password");
                String extensions = result.getString("accepted_extensions");
                boolean isAvailable = result.getBoolean("is_available");
                return new User(id, username, password, extensions, isAvailable);
                
            }else{  // user doesn't exist
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
    public void connectUser(User user) throws SQLException{
        
        statement = connection.prepareStatement("UPDATE Users SET is_available=? WHERE id=?");
        statement.setBoolean(1, true);
        statement.setInt(2, user.getId());
        statement.execute();
    }
    
    public void disconnectUser(User user) throws SQLException{
        
        statement = connection.prepareStatement("UPDATE Users SET is_available=? WHERE id=?");
        statement.setBoolean(1, false);
        statement.setInt(2, user.getId());
        statement.execute();
    }
    
    public void createChat(Chat chat) throws SQLException{
        
        statement = connection.prepareStatement("INSERT INTO Chats (user1_id, user2_id) VALUES (?,?)");
        statement.setInt(1, chat.getUser1Id());
        statement.setInt(2, chat.getUser2Id());
        statement.execute();
    }
    
    public Chat getChat(Chat chat){
        
        try {
            statement = connection.prepareStatement("SELECT * FROM Chats WHERE (user1_id, user2_id) = (?,?)");
            statement.setInt(1, chat.getUser1Id());
            statement.setInt(2, chat.getUser2Id());            
            ResultSet result = statement.executeQuery();
            
            if(result.next()){
                return new Chat(result.getInt("id"), chat.getUser1Id(), chat.getUser2Id());                
            }else{
                return null;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return null;
    }
    
    public void closeConnection(){
        try {
            connection.close();
            System.out.println("Successfully disconnected from the database!");
        } catch (SQLException ex) {
            System.out.println("Couldn't disconnect from the database");
        }
    }
}
