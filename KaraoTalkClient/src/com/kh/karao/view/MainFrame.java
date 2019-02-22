package com.kh.karao.view;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.kh.karao.view.login.LoginPanel;

public class MainFrame extends JFrame {
	public static String serverHost;
	public static final int mainServerPort = 8888;
	public static final int chatServerPort = 7777;
	
	public static final int WIDTH = 500;
	public static final int HEIGHT = 900;
	public static JLayeredPane pane;
	
	public MainFrame(String karaoServerHost){
		MainFrame.serverHost = karaoServerHost;
		
		setTitle("KaraoTalk");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setIconImage(new ImageIcon("images/tube.png").getImage());
		
		//setLocaitionRelativeTo메소드를 이용한 간편한 가운데배치
		setSize(WIDTH+20,HEIGHT+5);//??
		setLocationRelativeTo(null);
		
		//Pane을 통한 처리
		pane = new JLayeredPane();
        pane.setBounds(0, 0, getWidth(), getHeight());
        pane.setLayout(null);
		
		//로그인패널 생성
		JPanel loginPanel = new LoginPanel();
		
		pane.add(loginPanel);
		add(pane);
		setVisible(true);
	}
}
