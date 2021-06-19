/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.io.Serializable;
import static java.lang.Integer.max;
import static java.lang.Integer.min;

/**
 *
 * @author maybeitsmica
 */
public class Chat implements Serializable {
    
    private int id;
    private int user1_id;
    private int user2_id;
    
    public Chat(int user1, int user2){
        setUsers(user1, user2);
    }
    
    public Chat(int id, int user1, int user2){
        this.id = id;
        setUsers(user1, user2);
    }
       
    public void setUsers(int user1, int user2){
        
        this.user1_id = min(user1, user2);
        this.user2_id = max(user1, user2);
    }
    
    public int getId(){
        return this.id;
    }
    
    public int getUser1Id(){
        return this.user1_id;
    }
    
    public int getUser2Id(){
        return this.user2_id;
    }
}
