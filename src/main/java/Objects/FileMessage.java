/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.io.Serializable;
import java.io.File;

/**
 *
 * @author maybeitsmica
 */
public class FileMessage implements Serializable {
    
    private int id;
    private File file;
    private int chat_id;
    private int from_id;
    private String filename;
    
    public FileMessage(File file){
        this.file = file;
    }
    
    public FileMessage(File file, int chat, int from, String filename){
        this.file = file;
        this.chat_id = chat;
        this.from_id = from;
        this.filename = filename;
    }
    
    public FileMessage(int id, File file, int chat, int from, String filename){
        this.id = id;
        this.file = file;
        this.chat_id = chat;
        this.from_id = from;
        this.filename = filename;
    }
    
    public int getId(){
        return this.id;
    }
    
    public File getFile(){
        return this.file;
    }
    
    public int getChatId(){
        return this.chat_id;
    }
    
    public int getUserFromId(){
        return this.from_id;
    }
    
    public String getFilename(){
        return this.filename;
    }
}
