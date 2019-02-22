package com.kh.karao.view.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;

public class ChatHeaderPanel extends JPanel {
	private Set<String> clientSet;
	
	public ChatHeaderPanel(Set<String> clientSet) {
		this.clientSet = clientSet;
		setSize(new Dimension(MainFrame.WIDTH, 100));
		
		setBackground(Color.YELLOW);
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		Box horizontalBox = Box.createHorizontalBox();//BoxLayout을 효율적으로 사용할 수 있는 객체. glue, rigidArea등 이용가능
		JLabel clientList = new JLabel(ChatClientController.chatWith(clientSet, UserClientController.LOGIN_USER.getUserId()));
		
		horizontalBox.add(Box.createGlue());//메세지 우측에 공간
		horizontalBox.add(clientList);
		horizontalBox.add(Box.createGlue());//메세지 우측에 공간
		add(horizontalBox);
		
	}
}
