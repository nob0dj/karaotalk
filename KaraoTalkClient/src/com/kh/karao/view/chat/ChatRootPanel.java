package com.kh.karao.view.chat;

import java.awt.Color;
import java.util.Set;

import javax.swing.JPanel;

import com.kh.karao.view.MainFrame;

public class ChatRootPanel extends JPanel {
	public static final Color mainColor = new Color(66,54,48);
	JPanel chatHeaderPanel, chatBodyPanel, chatFooterPanel;
	
	public ChatRootPanel(String chatId, Set<String> clientSet){
		
		setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		setLayout(null);
		
		chatHeaderPanel = new ChatHeaderPanel(clientSet); 
		chatBodyPanel = new ChatBodyPanel(chatId, clientSet); 
		chatFooterPanel = new ChatFooterPanel(chatId, chatBodyPanel); 
		
		chatHeaderPanel.setLocation(0, 0);
		chatBodyPanel.setLocation(0, 100);
		chatFooterPanel.setLocation(0, 750);
		
		add(chatHeaderPanel);
		add(chatBodyPanel);
		add(chatFooterPanel);
		
	}

	public JPanel getChatBodyPanel() {
		return chatBodyPanel;
	}

	

}
