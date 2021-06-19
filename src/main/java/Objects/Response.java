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
public class Response implements Serializable {
    
    private int code;
    private String message;
    private Object object;
    
    public Response(int code){
        this.code = code;
    }
    
    public Response(int code, Object object){
        this.code = code;
        this.object = object;
    }
    
    public int getCode(){
        return this.code;
    }
    
    public Object getObject(){
        return this.object;
    }
    
    public String printMessage(){
        switch(code){
            case 200:
                return code + " - OK";
            case 400:
                return code + " - Bad request";
            case 401:
                return code + " - Incorrect password";
            case 402:
                return code + " - Chat already existed";
            case 404:
                return code + " - Not found";
            default:
                return Integer.toString(code);
        }
    }
}
