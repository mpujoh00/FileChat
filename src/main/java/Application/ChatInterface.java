/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Application;

import Objects.Chat;
import Objects.User;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 *
 * @author maybeitsmica
 */
public class ChatInterface {
    
    private JPanel panel;
    private User friend;
    private Chat chat;
    
    public ChatInterface(User user, Chat chat){
        
        this.friend = user;
        this.chat = chat;
        
        panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setPreferredSize(new Dimension(250,40));
        panel.setMinimumSize(new Dimension(250,40));
        panel.setMaximumSize(new Dimension(250,40));
        panel.setBorder(new MatteBorder(0, 0, 2, 0, new Color(249,200,221)));
        
        JLabel label = new JLabel(friend.getUsername(), SwingConstants.LEFT);
        label.setFont(new Font("arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setBorder(new EmptyBorder(5, 10, 5, 5));
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    public JPanel getChatPanel(){
        return this.panel;
    }
}
