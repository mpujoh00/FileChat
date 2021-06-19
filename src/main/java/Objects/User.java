/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author maybeitsmica
 */
public class User implements Serializable {
    
    private int id;
    private String username;
    private String password;
    private ArrayList<FileFilter> acceptedExtensions;
    private String extensionsString;
    
    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.extensionsString = "jpg,png,txt,pdf";  //default
        setAcceptedExtensions(this.extensionsString);
    }
    
    public User(int id, String username, String password, String extensions){
        this.id = id;
        this.username = username;
        this.password = password;
        this.extensionsString = extensions;
        setAcceptedExtensions(extensions);
    }
    
    public void setAcceptedExtensions(ArrayList<FileFilter> acceptedExtensions){
        this.acceptedExtensions = acceptedExtensions;
    }
    
    public void setAcceptedExtensions(String acceptedExtensions){
        
        List<String> extensionsString = Arrays.asList(acceptedExtensions.split(","));
        this.acceptedExtensions = new ArrayList<>();
        
        if(extensionsString.contains("jpg")){
            this.acceptedExtensions.add(new FileNameExtensionFilter("JPG image", "jpg"));
        }
        else if(extensionsString.contains("png")){
            this.acceptedExtensions.add(new FileNameExtensionFilter("PNG image", "png"));
        }
        else if(extensionsString.contains("txt")){
            this.acceptedExtensions.add(new FileNameExtensionFilter("TXT file", "txt"));
        }
        else if(extensionsString.contains("pdf")){
            this.acceptedExtensions.add(new FileNameExtensionFilter("PDF file", "pdf"));
        }
    }
    
    public List<FileFilter> getAcceptedExtensions(){
        return this.acceptedExtensions;
    }
    
    public String getExtensionsString(){
        return this.extensionsString;
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
