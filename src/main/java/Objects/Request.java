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
public class Request implements Serializable {
    
    private Object object;
    private RequestType type;
    
    public Request(Object object, RequestType type) {
        
        this.object = object;
        this.type = type;
    }
        
    public RequestType getType(){
        return this.type;
    }
    
    public Object getObject(){
        return this.object;
    }
}
