/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author maybeitsmica
 */
public class MessageInterface {
    
    private File file;
    private JPanel panel;
    
    public MessageInterface(Color color, String user, File file) {
        
        this.file = file;
        
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(60,40));
        panel.setMinimumSize(new Dimension(60,40));
        panel.setMaximumSize(new Dimension(500,40));
        panel.setBorder(new EmptyBorder(10, 15, 0, 0));
        
        JLabel label = new JLabel(user + ": " + file.getName(), SwingConstants.LEFT);
        label.setFont(new Font("arial", Font.PLAIN, 16));
        label.setBackground(color);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(3, 3, 3, 3));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    public File getFile(){
        return this.file;
    }
       
    public JPanel getMessage(){
        return this.panel;
    }
    
}
