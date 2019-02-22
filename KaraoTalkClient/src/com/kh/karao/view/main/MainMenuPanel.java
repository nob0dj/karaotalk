package com.kh.karao.view.main;

import java.awt.Color;

import javax.swing.JPanel;

import com.kh.karao.view.MainFrame;
import com.kh.karao.view.main.body.BodyRootPanel;
import com.kh.karao.view.main.body.FriendsListPanel;
import com.kh.karao.view.main.foot.FootMenuPanel;
import com.kh.karao.view.main.head.HeadMenuPanel;

public class MainMenuPanel extends JPanel {
	public static final Color mainColor = new Color(66,54,48);
	
	public static BodyRootPanel bodyRootPanel;
	
	public MainMenuPanel(){
		
		setSize(MainFrame.WIDTH, MainFrame.HEIGHT);
		setLayout(null);
		
		JPanel headPanel = new HeadMenuPanel(); 
		bodyRootPanel = new BodyRootPanel(); 
		JPanel footPanel = new FootMenuPanel(); 
		
		headPanel.setLocation(0, 0);
		bodyRootPanel.setLocation(0, 100);
		footPanel.setLocation(0, 750);
		
		add(headPanel);
		add(bodyRootPanel);
		add(footPanel);
		
		
	}
}
