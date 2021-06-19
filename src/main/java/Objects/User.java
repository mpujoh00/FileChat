/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.io.Serializable;

/**
 *
 * @author maybeitsmica
 */
public class User implements Serializable {
    
    private int id;
    private String username;
    private String password;
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public User(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }
    
    public int getId(){
        return this.id;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getPassword(){
        return this.password;
    }
}
