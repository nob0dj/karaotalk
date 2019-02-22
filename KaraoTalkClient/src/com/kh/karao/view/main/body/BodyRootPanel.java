package com.kh.karao.view.main.body;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.kh.karao.view.MainFrame;

public class BodyRootPanel extends JPanel {
	//생성된 패널을 배열로 보관한다.
	public static JPanel[] panelArr = new JPanel[4];
	
	public static Container bodyRootPane;
	public static JPanel currentPanel;
	public static final int HEIGHT = 650;
	
	public BodyRootPanel() {
		setSize(new Dimension(MainFrame.WIDTH, HEIGHT));
		
		bodyRootPane = new MyPane();
		bodyRootPane.setPreferredSize(new Dimension(MainFrame.WIDTH, HEIGHT));
		
		//초기화면은 친구목록이다.
		panelArr[0] = new FriendsListPanel();
		panelArr[1] = new ChatListPanel();
		
		bodyRootPane.add(panelArr[0]);
		currentPanel = panelArr[0];
		
		add(bodyRootPane);
	}
		
}

/**
 * add메소드 overriding을 위해서 JLayeredPane을 상속한 클래스 작성
 * @author shqkel1863
 *
 */
class MyPane extends JLayeredPane{

	@Override
	public Component add(Component nextPanel) {
		super.add(nextPanel);
		BodyRootPanel.currentPanel = (JPanel)nextPanel;
		return nextPanel;
	}
	
}
