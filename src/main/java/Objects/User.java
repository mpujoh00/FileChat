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
    private String extensions;
    private boolean isAvailable;
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.extensions = "jpg,png,txt,pdf";  //default
        this.isAvailable = true;
    }
    
    public User(int id, String username, String password, String extensions, boolean available){
        this.id = id;
        this.username = username;
        this.password = password;
        this.extensions = extensions;
        this.isAvailable = available;
    }
    
    public void setAcceptedExtensions(String extensions){
        this.extensions = extensions;
    }
    public boolean isAvailable(){
        return this.isAvailable;
    }
    
    public String getExtensions(){
        return this.extensions;
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
