package com.kh.karao.view.login;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.common.Utils;
import com.kh.karao.session.ClientSession;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.main.MainMenuPanel;
import com.kh.karao.view.main.body.BodyRootPanel;
import com.kh.karao.view.main.body.ChatListPanel;



public class LoginPanel extends JPanel {
	private UserClientController userClientController = new UserClientController();
	private ChatClientController chatClientController = new ChatClientController();
	private BufferedImage img = null;
	
	JTextField userId;
	JTextField password;//JPasswordField에 대한 다형성적용
	JButton btnLogin;
	
	int offsetX;
	int offsetY;
	
	
	public LoginPanel(){
		setSize(new Dimension(MainFrame.WIDTH, MainFrame.HEIGHT));
		
		try {
			img = ImageIO.read(new File("images/loginBG.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//NullLayout적용
		setLayout(null);
		
		offsetX = MainFrame.WIDTH/2-150;
		offsetY = (int)(MainFrame.HEIGHT*0.6);
		
		//1. id입력
		userId = new JTextField(15);
		userId.setBounds(offsetX, offsetY, 300, 50);
//		userId.setText("아이디를 입력하세요.");
		userId.setText("shqkel1863");
		
		//2. password입력
		password = new JPasswordField(15);
		password.setText("1234");
		password.setBounds(offsetX, offsetY+50, 300, 50);
		
		//3. login버튼
		btnLogin = new JButton("로그인");
		btnLogin.setBounds(offsetX, offsetY+100, 300, 50);
		btnLogin.requestFocus();
		
		add(userId);
		add(password);
		add(btnLogin);
		
		userId.addFocusListener(new MyFocusListener());
		password.addFocusListener(new MyFocusListener());
		btnLogin.addActionListener(new MyActionListener());
		
	}
	
	
	/**
	 * paint메소드를 오버라이드 해두면, 패널생성시 자동으로 호출되면서, bg-image를 그림.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//이미지 그리기
		//boolean java.awt.Graphics.drawImage(Image img, int x, int y, ImageObserver observer)
		g.drawImage(img, 0, 0, null);
		
		//JButton이 안보이는 현상 해결용.
		paintComponents(g);
	}
	
	class MyFocusListener extends FocusAdapter{

		@Override
		public void focusGained(FocusEvent e) {
			((JTextField)e.getSource()).selectAll();
		}
		
	}
	
	
	/**
	 * 로그인 처리를 위한 리스너클래스
	 * @author nobodj
	 *
	 */
	class MyActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String _userId = userId.getText();
			String _password = password.getText();
			
			//로그인검사후 성공시
			int result = userClientController.loginCheck(new User(_userId, _password));
			
			
			if(result == UserClientController.LOGIN_OK){
				//1. LOGIN_USER
				UserClientController.LOGIN_USER = UserClientController.getUser(_userId);
				//2. MainMenuPanel객체 생성
				Utils.changePanel(MainFrame.pane, LoginPanel.this, new MainMenuPanel());
				//3. 세션유지를 위한 소켓 요청
				new ClientSession();
				
				
			}
			else if(result == UserClientController.WRONG_PASSWORD){
				JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
				password.requestFocus();
			}
			else {//result == UserCOntroller.NON_EXIST_USERID
				JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.");	
				password.setText("");
				userId.requestFocus();
			}
		}
		
	}
}
