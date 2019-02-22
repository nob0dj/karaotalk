package com.kh.karao.view.chat;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.view.MainFrame;

public class ChatFrame extends JFrame {
	public static Map<String, ChatFrame> chatFrameMap = new HashMap<>();
	
	public final int WIDTH = MainFrame.WIDTH;
	public final int HEIGHT = MainFrame.HEIGHT;
	public static JLayeredPane pane;
	private String chatId;
	private JPanel chatRootPanel;//채팅루트패널
	
	
	private ChatClientController chatClientController = new ChatClientController();
	
	private ChatFrame(String chatId, Set<String> clientSet){
		this.chatId = chatId;
		
		String title = "";
		for(Object o : clientSet){
			String u = (String)o;
			if(!u.equals(UserClientController.LOGIN_USER.getUserId()))
				title += u+" ";
		}
		
		setTitle(title);
//		setResizable(false);
		
		//사용자이미지를 아이콘으로 사용한다.
		setIconImage(new ImageIcon("images/tube.png").getImage());
		
		//setLocaitionRelativeTo메소드를 이용해서 화면가운데 배치
		setSize(WIDTH+20,HEIGHT+5);//??
		setLocationRelativeTo(null);
		//메인프레임 우측에 배치
		setLocation(getLocation().x+WIDTH,getLocation().y);
		
		//Pane을 통한 처리
		pane = new JLayeredPane();
        pane.setBounds(0, 0, getWidth(), getHeight());
        pane.setLayout(null);
		
		//채팅루트패널 생성
		chatRootPanel = new ChatRootPanel(chatId, clientSet);
		
		
		//채팅프레임이 닫히면 해당소켓통신을 닫음.
		addWindowListener(new WindowAdapter(){
			/**
			 * windowClosed메소들 사용하지 말것.
			 * 
			 * @param e
			 */
			@Override
			public void windowClosing(WindowEvent e) {
//				System.out.println("ChatFrame Closing!!");
				ChatFrame.chatFrameMap.remove(chatId);
			}

			/**
			 * 현재윈도우가 활성화되면 호출됨. 
			 * focus관련메소드가 아니라 windowActivated메소드를 사용할 것.
			 * 
			 * @param e
			 */
			@Override
			public void windowActivated(WindowEvent e) {
				
//				System.out.println("windowActivated");
				ChatBodyPanel p = ((ChatBodyPanel)((ChatRootPanel)chatRootPanel).chatBodyPanel);
				
				//마지막 접속시각보다 나중에 도착한 메세지가 있는 경우에만 서버에 접속시간을 갱신함.
				if(true){
					//서버접속시간 갱신요청
					chatClientController.updateLastConnectionMap(chatId);
				}
			}
			
			
			
		});
	
		
		pane.add(chatRootPanel);
		add(pane);
		setVisible(true);
	}

	public JPanel getChatRootPanel() {
		return chatRootPanel;
	}

	
	
	/**
	 * ChatFrame객체를 관리하는 static메소드
	 * 별도의 ChatFrameManage클래스 없이 생성자를 관리함.
	 * 
	 * @param chatId
	 * @param clientSet
	 */
	public static void manageChatFrame(String chatId, Set<String> clientSet) {
		if(ChatFrame.chatFrameMap.containsKey(chatId)){
			//기존에 있던 프레임을 맨앞창으로 가져온다.
//			ChatFrame.chatFrameMap.get(chatId).requestFocus();
			((ChatFooterPanel)((ChatRootPanel)ChatFrame.chatFrameMap.get(chatId).chatRootPanel).chatFooterPanel).textArea.grabFocus();
		}
		else{	
			ChatFrame.chatFrameMap.put(chatId, new ChatFrame(chatId, clientSet));
		}
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chatId == null) ? 0 : chatId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatFrame other = (ChatFrame) obj;
		if (chatId == null) {
			if (other.chatId != null)
				return false;
		} else if (!chatId.equals(other.chatId))
			return false;
		return true;
	}
	
	
}
