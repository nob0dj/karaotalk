package com.kh.karao.view.main.head;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.main.MainMenuPanel;
import com.kh.karao.view.main.body.BodyRootPanel;
import com.kh.karao.view.main.body.FriendsListPanel;

public class HeadMenuPanel extends JPanel {
	public static JComponent[] headerTitleArr = new JComponent[4];
	public static JComponent currentComp;
	public static JPanel headUpperPanel;
	
	//resize해야 라벨이 바뀌는 버그해결할 것.
	static {
		//1.FriendsList
		headerTitleArr[0] = new JLabel("Friends");
		headerTitleArr[0].setFont(new Font("Verdana",Font.PLAIN,15));
		headerTitleArr[0].setForeground(Color.WHITE);
		headerTitleArr[0].setBorder(BorderFactory.createEmptyBorder(10,0,10,0)); 

		//2.ChatList
		headerTitleArr[1] = new JLabel("Chats");
		headerTitleArr[1].setFont(new Font("Verdana",Font.PLAIN,15));
		headerTitleArr[1].setForeground(Color.WHITE);
		headerTitleArr[1].setBorder(BorderFactory.createEmptyBorder(10,0,10,0)); 
	}

	public HeadMenuPanel() {
		setSize(new Dimension(MainFrame.WIDTH, 100));
		
		setBackground(MainMenuPanel.mainColor);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//1.상단
		headUpperPanel = new JPanel(){
			@Override
			public Component add(Component next) {
				super.add(next);
				HeadMenuPanel.currentComp = (JComponent)next;
				return next;
			}
		};
		headUpperPanel.setSize(MainFrame.WIDTH, 50);
		headUpperPanel.setBackground(MainMenuPanel.mainColor);
		
		headUpperPanel.add(headerTitleArr[0]);
		add(headUpperPanel);
		
		
		//2.하단
		JTextField searchTextField  = new JTextField(15);
		searchTextField.setMaximumSize(new Dimension(MainFrame.WIDTH-50, 50));
		searchTextField.setBackground(new Color(64,0,64));
		searchTextField.setForeground(Color.WHITE);

		searchTextField.setBorder(BorderFactory.createMatteBorder(0,0,20,0, MainMenuPanel.mainColor));
		//padding을 추가하기 위해 insideBorder로 emptyBorder추가
		searchTextField.setBorder(BorderFactory.createCompoundBorder(searchTextField.getBorder(), BorderFactory.createEmptyBorder(6, 6, 6, 6)));

		searchTextField.setText("search");
		searchTextField.setFont(new Font("Verdana",Font.PLAIN,20));
		
		searchTextField.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				searchTextField.selectAll();
			}
		});
		
		searchTextField.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				JOptionPane.showMessageDialog(null, "서비스 준비중입니다.");
				
				String search = searchTextField.getText();
				if(!search.isEmpty()){
					List<User> resultList = new UserClientController().findUsersByUserId(search);
					System.out.println("resultList@HeadMunuPanel="+resultList);
					((FriendsListPanel)BodyRootPanel.panelArr[0]).addSearchList(resultList);					
				}
				else {
					((FriendsListPanel)BodyRootPanel.panelArr[0]).addMeAndFriendsList();
					searchTextField.setText("search");
				}
			}
		});
		add(searchTextField);
	}

}
