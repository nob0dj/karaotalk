package com.kh.karao.view.chat;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.view.MainFrame;

public class ChatBodyPanel extends JPanel {
	
	private String chatId;
	private Set<String> clientSet;
	private String pathToProfileImage = Utils.getProperty("user.profileImage");
	private Map<String,String> userProfileImageMap = new HashMap<>();//사용자(상대) 프로필이미지주소를 보관한다.
	private JPanel bodyContainerPanel;
	private JScrollPane scrollPane;
	private ChatClientController chatClientController = new ChatClientController();
	private UserClientController userClientController = new UserClientController();
	
	private Map<String, Date> lastConnectionMap;//ChatFrame객체마다 사용할 사용자최근접속시각 맵
	
	
	private List<OtherMessagePanel> otherMessagePanelList = new ArrayList<>();
	private List<MyMessagePanel> myMessagePanelList = new ArrayList<>();
	
	//이벤트핸들러 재사용을 위해 필드로 선언함.
	AdjustmentListener downScroller = new AdjustmentListener() {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			Adjustable adjustable = e.getAdjustable();
			adjustable.setValue(adjustable.getMaximum());
			adjustable.removeAdjustmentListener(this);
		}
	};
	
	private static final int HEIGHT = 650;
	
	public ChatBodyPanel(String chatId, Set<String> clientSet){
		this.chatId = chatId;
		this.clientSet = clientSet;
		
		setSize(new Dimension(MainFrame.WIDTH, HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		bodyContainerPanel = new JPanel();
		bodyContainerPanel.setSize(new Dimension(MainFrame.WIDTH, HEIGHT));
		bodyContainerPanel.setLayout(new BoxLayout(bodyContainerPanel, BoxLayout.PAGE_AXIS));
		scrollPane = new JScrollPane(bodyContainerPanel);
		scrollPane.setSize(new Dimension(MainFrame.WIDTH, HEIGHT));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
		//메세지 추가후에 스크롤을 가장 밑으로 이동시키는 이벤트핸들러 
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.addAdjustmentListener(downScroller);
		
		add(scrollPane);
		
		//1. Chat객체의 lastConnectionMap을 가져와 세팅한다.
//		lastConnectionMap = chatClientController.getLastConnectionMap(chatId);
		lastConnectionMap = chatClientController.updateLastConnectionMap(chatId);
		System.out.println("lastConnectionMap@ChatBodyPanel="+lastConnectionMap);
		
		//2. 처음ChatFrame생성시, 기존의 대화내역을 가져와서 뿌린다.
		String messages = chatClientController.getMessages(chatId);
		StringTokenizer messageToken = new StringTokenizer(messages, "\n");
		while(messageToken.hasMoreTokens()){
			String msg = messageToken.nextToken();
			if(msg.split("§")[1].equals(UserClientController.LOGIN_USER.getUserId()))
				addMessageFromMe(msg);
			else 
				addMessageFromOther(msg);
		}
		
		
		
		//테스트용메세지
//		Date d = new Date();
//		addMessageFromOther("42323§khpi§"+d.getTime()+"§"+Utils.convertToMsg("msgFromOther Test"));
//		addMessageFromOther("42323§khpi§"+d.getTime()+"§"+Utils.convertToMsg("msgFromOther\nmsgFromOtherOther\n한글한글한글한글 한글\nmsgFromOther\nmsgFromOther"));
//		
//		addMessageFromMe("42323§shqkel1863§"+d.getTime()+"§"+Utils.convertToMsg("msgFromMe Test"));
		
	}

	
	
	
	public void addMessageFromOther(String msg) {
		//1.msg parsing
		String[] temp = msg.split("§");
		String userId = temp[1];
		long time = Long.parseLong(temp[2]);
		//개행문자 처리를 jlabel내부적으로 처리하지 못하므로, html코드로 변환
		msg = temp[3];

		new OtherMessagePanel(userId, time, msg);
		
	}
	
	public class OtherMessagePanel extends JPanel{
		String userId;
		Long time;
		String msg;
		int unread;
		JLabel unreadLabel;
		
		public OtherMessagePanel(String userId, Long time, String msg) {
			this.userId = userId;
			this.time = time;
			this.msg = msg;
			
			init();
		}

		public int getUnread() {
			return unread;
		}
		public void setUnread(int unread) {
			unreadLabel.setText(String.valueOf(unread));
			this.unread = unread;
		}
		
		public void init(){
			Box horizontalBox = Box.createHorizontalBox();//BoxLayout을 효율적으로 사용할 수 있는 객체. glue, rigidArea등 이용가능

			//2.1.이미지아이콘 : 컨테이너 패널 없음
			String userProfileImage = userProfileImageMap.get(userId);
			if(userProfileImage==null) 
				userProfileImageMap.put(userId, userProfileImage=UserClientController.getUser(userId).getProfileImage());
			
//			System.out.println("userProfileImageMap@ChatBodyPanel="+userProfileImageMap);
			String path = pathToProfileImage+userProfileImage;
			Image scaledImage = new ImageIcon(path).getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT);
			JLabel imageOfOther = new JLabel(new ImageIcon(scaledImage));

			//2.2.userId, 메세지
			JPanel innerPanel2 = new JPanel();//userId, msgFromOther컴포넌트용
			innerPanel2.setLayout(new BoxLayout(innerPanel2, BoxLayout.PAGE_AXIS));
			JLabel otherName = new JLabel(UserClientController.getUser(userId).getUserName());
			otherName.setMaximumSize(otherName.getPreferredSize());
			otherName.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			JLabel msgFromOther = new JLabel(msg);
			
			//2.3.안읽은 메세지, 메세지시각
			JPanel innerPanel3 = new JPanel();//msgTime컴포넌트용
			Box verticalBox3 = Box.createVerticalBox();
			unreadLabel = new JLabel();
			//unread값 계산후 세팅
			this.unread = calcUnread(userId, time);//메세지 작성자
			unreadLabel.setText(String.valueOf(this.unread));
			JLabel msgTime = new JLabel(Utils.convertToChatTime(String.valueOf(time)));
			
			
			//좌우로 정렬된 컴포넌트의 수직정렬을 위해서 모든 컴포넌트의 정렬값을 일치시킨다.
			imageOfOther.setAlignmentY(TOP_ALIGNMENT);
			innerPanel2.setAlignmentY(TOP_ALIGNMENT);
			msgTime.setAlignmentY(TOP_ALIGNMENT);
			
			//2.2.innerPanel2 : userId, msgFromOther
//			System.out.println("msgFromOther.getPreferredSize()="+msgFromOther.getPreferredSize());//java.awt.Dimension[width=86,height=16]
//			System.out.println("msgFromOther.getSize()="+msgFromOther.getSize());//java.awt.Dimension[width=0,height=0]
			
			//maximumSize로 지정하면, 외부container인 JPanel관계때문에 jabel안에서 글자가 모두 표시되지 못한다.
//			msgFromOther.setMaximumSize(msgFromOther.getPreferredSize());
			msgFromOther.setMinimumSize(new Dimension(msgFromOther.getPreferredSize().width, msgFromOther.getPreferredSize().height));
			
			msgFromOther.setBackground(Color.WHITE);
			msgFromOther.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));//테두리 border적용
			msgFromOther.setBorder(BorderFactory.createCompoundBorder(msgFromOther.getBorder(), BorderFactory.createEmptyBorder(6,6,6,6)));
			msgFromOther.setOpaque(true);//불투명여부, true여야 background색상이 입혀진다.
			
			innerPanel2.add(otherName);
			innerPanel2.add(msgFromOther);
			
			//innerPanel의 preferreedSize는 컴포넌트를 추가한 후에 계산해야한다. 그렇지 않으면, 0.
			innerPanel2.setMaximumSize(new Dimension(MainFrame.WIDTH, innerPanel2.getPreferredSize().height+20));//이 설정으로 메세지가 옆으로 무한정 길어지는 것 방지
			innerPanel2.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));//padding으로 처리됨.
			innerPanel2.setOpaque(false);//투명하게 처리
			
			//2.3.innerPanel3 : unread, msgTime
			unreadLabel.setFont(new Font(null, Font.BOLD, 12));
			unreadLabel.setForeground(Color.YELLOW);
			unreadLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			msgTime.setFont(new Font(null, Font.PLAIN, 10));
			msgTime.setForeground(Color.gray);
			msgTime.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
			
			verticalBox3.add(Box.createVerticalGlue());
			verticalBox3.add(unreadLabel);
			verticalBox3.add(msgTime);
			innerPanel3.add(verticalBox3);
			innerPanel3.setPreferredSize(innerPanel3.getPreferredSize());
			innerPanel3.setLayout(new BoxLayout(innerPanel3, BoxLayout.PAGE_AXIS));
			innerPanel3.setOpaque(false);
			
			//box객체에 컴포넌트추가(left-to-right)
			horizontalBox.add(imageOfOther);
			horizontalBox.add(innerPanel2);
			horizontalBox.add(innerPanel3);
			horizontalBox.add(Box.createGlue());//메세지 우측에 공간
			
			add(horizontalBox);
			
			setBackground(Color.PINK);
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));//padding으로 처리됨.
			setMaximumSize(new Dimension(MainFrame.WIDTH, getPreferredSize().height));
//			System.out.println("panel.getPreferredSize()="+panel.getPreferredSize());//java.awt.Dimension[width=120,height=50] => 10+(1+6)+86+(6+1)+10, 10+(1+6)+16+(6+1)+10 
			
			bodyContainerPanel.add(this);
			bodyContainerPanel.revalidate();
			addAdjustmentListener();
			
			//unread관리를 위한 otherPanelList에 추가
			otherMessagePanelList.add(this);
		}
	}
	
	
	
	
	public void addMessageFromMe(String msg) {
		//1.전달받은 메세지 parsing
		String[] temp = msg.split("§");
		String userId = temp[1];
		long time = Long.parseLong(temp[2]);
		msg = temp[3];
		
		new MyMessagePanel(userId, time, msg);
		
	}
	public class MyMessagePanel extends JPanel{
		String userId;
		Long time;
		String msg;
		int unread;
		JLabel unreadLabel;
		
		public MyMessagePanel(String userId, Long time, String msg) {
			this.userId = userId;
			this.time = time;
			this.msg = msg;
			this.unread = clientSet.size()-1;//자신을 제외한 user카운트 세팅
			init();
		}

		public Long getTime() {
			return time;
		}
		public int getUnread() {
			return unread;
		}
		public void setUnread(int unread) {
			unreadLabel.setText(String.valueOf(unread));
			this.unread = unread;
		}
		
		public void init(){
			Box horizontalBox = Box.createHorizontalBox();//BoxLayout을 효율적으로 사용할 수 있는 객체. glue, rigidArea등 이용가능
			
			JPanel innerPanel1 = new JPanel();
			Box verticalBox = Box.createVerticalBox();
			JLabel msgTime = new JLabel(Utils.convertToChatTime(String.valueOf(time)));
			unreadLabel = new JLabel();
			//unread값 계산후 세팅
			this.unread = calcUnread(userId, time);//메세지 작성자
			unreadLabel.setText(String.valueOf(this.unread));
			
			JPanel innerPanel2 = new JPanel();
			JLabel msgFromMe = new JLabel(msg);
			
			
			//2.1.unread, msgTime컴포넌트
			unreadLabel.setFont(new Font(null, Font.BOLD, 12));
			unreadLabel.setForeground(Color.YELLOW);
			unreadLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			msgTime.setFont(new Font(null, Font.PLAIN, 10));
			msgTime.setForeground(Color.gray);
			msgTime.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
			
			//msgTime JLabel컴포넌트를 하단에 배치하기 위해, BoxLayout의 Box객체, glue사용
			verticalBox.add(Box.createVerticalGlue());
			verticalBox.add(unreadLabel);
			verticalBox.add(msgTime);
			
			innerPanel1.add(verticalBox);
			innerPanel1.setPreferredSize(innerPanel1.getPreferredSize());
			innerPanel1.setLayout(new BoxLayout(innerPanel1, BoxLayout.PAGE_AXIS));
			innerPanel1.setOpaque(false);
			
			//2.2.msgFromMe컴포넌트 : JLabel컴포넌트의 emptyBorder는 JPanel컨테이너 안에 있을 경우에 먹고 있다.
//			msgFromMe.setMaximumSize(msgFromMe.getPreferredSize());
			msgFromMe.setMinimumSize(new Dimension(msgFromMe.getPreferredSize().width, msgFromMe.getPreferredSize().height));
			
			msgFromMe.setBackground(Color.YELLOW);
			msgFromMe.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			msgFromMe.setBorder(BorderFactory.createCompoundBorder(msgFromMe.getBorder(), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			msgFromMe.setOpaque(true);//불투명여부, true여야 background색상이 입혀진다.
			
			innerPanel2.add(msgFromMe);
			innerPanel2.setMaximumSize(new Dimension(MainFrame.WIDTH, innerPanel2.getPreferredSize().height+20));//이 설정으로 메세지가 옆으로 무한정 길어지는 것 방지
			innerPanel2.setLayout(new BoxLayout(innerPanel2, BoxLayout.PAGE_AXIS));
			innerPanel2.setOpaque(false);//투명하게 처리
		
			//2.3.outerPanel
			horizontalBox.add(Box.createGlue());//메세지 좌측에 공간
			horizontalBox.add(innerPanel1);
			horizontalBox.add(innerPanel2);
			
			setBackground(Color.GREEN);
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			add(horizontalBox);
			setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));//padding으로 처리됨.
			setMaximumSize(new Dimension(MainFrame.WIDTH, getPreferredSize().height));
			
			bodyContainerPanel.add(this);
			bodyContainerPanel.revalidate();
			addAdjustmentListener();
			
			//unread관리를 위한 otherPanelList에 추가
			myMessagePanelList.add(this);
		}

	}
	
	public int calcUnread(String writer, long time) {
		int unread = 0;
		
		//1.채팅방에 아직 접속하지 않은 User가 있는 경우
		//LOGIN_USER를 제외한 상태에서 크기비교
		if(clientSet.size()>lastConnectionMap.size()){
			unread += clientSet.size()-lastConnectionMap.size();
		}
		
		//2.메세지작성시각(time)보다 최근접속시각이 빠른 경우
		//	a. 최초 chatframe 생성시
		//	b. 생성된 chatframe에 상대방 메세지전달시
		Set<String> keySet = lastConnectionMap.keySet();
		for(String key : keySet){
			//a.로그인한 사용자는 무조건 읽음, b.포커스를 가지고 있는 경우만 읽음.
			if(key.equals(UserClientController.LOGIN_USER.getUserId())) {
				//chatframe생성시 : ChatFrame객체가 만들어지면서 호출되기 때문에 chatFrameMap에 아직 추가되지 않은 상태
				if(ChatFrame.chatFrameMap.get(chatId)==null) 
					continue;
				else if(ChatFrame.chatFrameMap.get(chatId).getFocusOwner()!=null)
					continue;
			}
			if(key.equals(writer)) continue;
			
			if(time>lastConnectionMap.get(key).getTime())
				unread++;
//			System.out.println("userId="+writer+", 안읽음여부="+(time>lastConnectionMap.get(key).getTime()));
		}
//		System.out.println("clientSet.size()="+clientSet.size()+", lastConnectionMap.size()="+lastConnectionMap.size()+", unread="+unread);
		
		return unread;
	}
	
	public  void addAdjustmentListener() {
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		//다시 리스너를 추가해서 다음 메세지추가를 기다린다.
		//listener를 제거하지 않으면, 수동스크롤이 불가하다.
		vertical.addAdjustmentListener(downScroller);
	}


	/**
	 * 세션소켓으로 전달된 
	 * @param lastConnectionMap
	 */
	public void updateMessageUnread(Map<String, Date> lastConnectionMap) {
		this.lastConnectionMap = lastConnectionMap;
		
		//1.messageFromOther
		for(int i=0; i<otherMessagePanelList.size(); i++){
			OtherMessagePanel otherMessagePanel = otherMessagePanelList.get(i);
			if(otherMessagePanel.getUnread()==0) 
				continue;
			else 
				otherMessagePanel.setUnread(calcUnread(otherMessagePanel.userId, otherMessagePanel.time));
		}
		
		//2.messageFromMe
		for(int i=0; i<myMessagePanelList.size(); i++){
			MyMessagePanel myMessagePanel = myMessagePanelList.get(i);
			if(myMessagePanel.getUnread()==0) 
				continue;
			else 
				myMessagePanel.setUnread(calcUnread(myMessagePanel.userId, myMessagePanel.time));
		}
		
		
	}
	
	

	
	


}
