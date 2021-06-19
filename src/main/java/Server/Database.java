package Server;

import Objects.Chat;
import Objects.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    
    public void registerUser(User user){
        
        try {
            statement = connection.prepareStatement("INSERT INTO Users (username, password) VALUES (?,?)");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public User findUser(String username){
        
        try {
            statement = connection.prepareStatement("SELECT * FROM Users WHERE username=?");
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            
            if(result.next()){  // user exists
                
                int id = result.getInt("id");
                String password = result.getString("password");
                return new User(id, username, password);
                
            }else{  // user doesn't exist
                return null;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }        
    }
    
    public void createChat(Chat chat){
        
        try {
            statement = connection.prepareStatement("INSERT INTO Chats (user1_id, user2_id) VALUES (?,?)");
            statement.setInt(1, chat.getUser1Id());
            statement.setInt(2, chat.getUser2Id());
            statement.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
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
