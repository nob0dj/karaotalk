package com.kh.karao.view.main.foot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kh.karao.common.Utils;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.main.body.BodyRootPanel;
import com.kh.karao.view.main.body.ChatListPanel;
import com.kh.karao.view.main.body.FriendsListPanel;
import com.kh.karao.view.main.head.HeadMenuPanel;

public class FootMenuPanel extends JPanel {
	private Color footBackgrounColor = new Color(248,248,248);
	
	private JButton btn1;
	private JButton btn2;
	private JButton btn3;
	private JButton btn4;
	
	public FootMenuPanel() {
//		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setSize(MainFrame.WIDTH, 100);
		setBackground(footBackgrounColor);
		
		//사이즈 조정
		Image icon1 = new ImageIcon("images/tube_friends.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		Image icon2 = new ImageIcon("images/tube_chat.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		Image icon3 = new ImageIcon("images/tube.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		Image icon4 = new ImageIcon("images/tube.png").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		
		//label컴포넌트에 이미지추가
		JLabel label1 = new JLabel(new ImageIcon(icon1));
		JLabel label2 = new JLabel(new ImageIcon(icon2));
		JLabel label3 = new JLabel(new ImageIcon(icon3));
		JLabel label4 = new JLabel(new ImageIcon(icon4));
		
		//탭버튼추가
		btn1 = new JButton("Friends", label1.getIcon());
		btn2 = new JButton("Chat", label2.getIcon());
		btn3 = new JButton("Me",label3.getIcon());
		btn4 = new JButton(label4.getIcon());
		
		//버튼배경색 지정
		btn1.setBackground(footBackgrounColor);
		btn2.setBackground(footBackgrounColor);
		btn3.setBackground(footBackgrounColor);
		btn4.setBackground(footBackgrounColor);
		
		btn1.setRolloverIcon(label1.getIcon());
		
		//이벤트리스너등록
		ActionListener listener = new FootBtnListener();
		btn1.addActionListener(listener);
		btn2.addActionListener(listener);
		btn3.addActionListener(listener);
		btn4.addActionListener(listener);
		
		add(btn1);
		add(btn2);
		add(btn3);
//		add(btn4);
		
		
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		//JButton이 안보이는 현상 해결용.
		paintComponents(g);
	}
	
	class FootBtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//1.Friends
			if(e.getSource() == btn1){
				if(BodyRootPanel.panelArr[0]==null) BodyRootPanel.panelArr[0] = new FriendsListPanel();
				
				Utils.changePanel(BodyRootPanel.bodyRootPane, BodyRootPanel.currentPanel, BodyRootPanel.panelArr[0]);
				Utils.changeComponent(HeadMenuPanel.headUpperPanel, HeadMenuPanel.currentComp, HeadMenuPanel.headerTitleArr[0]);
				
			}
			//2.Chats.
			else if(e.getSource() == btn2){
				if(BodyRootPanel.panelArr[1]==null) BodyRootPanel.panelArr[1] = new ChatListPanel();
				
				Utils.changePanel(BodyRootPanel.bodyRootPane, BodyRootPanel.currentPanel, BodyRootPanel.panelArr[1]);
				Utils.changeComponent(HeadMenuPanel.headUpperPanel, HeadMenuPanel.currentComp, HeadMenuPanel.headerTitleArr[1]);
				
				System.out.println("clientSession.message : "+BodyRootPanel.panelArr[1]+", "+(BodyRootPanel.panelArr[1]==null));
				//ChatList의 최신화 정보를 server로부터 가져온다.
				((ChatListPanel)BodyRootPanel.panelArr[1]).addChatList();
			}
			else if(e.getSource() == btn3){
				
			}
			else if(e.getSource() == btn4){
	
			}
		}
		
	}
	
}
