package com.kh.karao.view.main.body;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.chat.ChatFrame;


public class FriendsListPanel extends JPanel {
	private ChatClientController chatClientController = new ChatClientController();
	private UserClientController userClientController = new UserClientController();
	
	private final int COL1WIDTH = 100;
	private final int COL2WIDTH = 150;
	private final int COL3WIDTH = 250;

	String[] columns = {"프로필사진", "아이디", "이름", "상태메세지"};
	private Object[][] data1, data2, data3;
	private List<String> friendsList = UserClientController.LOGIN_USER.getFriendsList();
	
	String pathToProfileImage = Utils.getProperty("user.profileImage");

	private List<JTable> tableList = new ArrayList<>();
	
	{
		
		//1. 본인정보
		data1 = new Object[1][columns.length];
		
		//이미지가 존재하지 않는 경우, 서버에 이미지 요청함.
		if(!new File(pathToProfileImage+UserClientController.LOGIN_USER.getProfileImage()).exists())
			userClientController.getProfileImage(UserClientController.LOGIN_USER.getUserId());
//		userClientController.existUserProfileImage(userMap.get(UserClientController.LOGIN_USER.getUserId()));
		
		Image loginUserScaledImage = new ImageIcon(pathToProfileImage+UserClientController.LOGIN_USER.getProfileImage()).getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		Icon loginUserProfileImage = new ImageIcon(loginUserScaledImage);
		
		Object[] LOGIN_USER = {loginUserProfileImage, 
							  UserClientController.LOGIN_USER.getUserId(), 
							  UserClientController.LOGIN_USER.getUserName(), 
							  UserClientController.LOGIN_USER.getStatusMessage()};
		data1[0] = LOGIN_USER;
		
		//2. 친구목록정보
		data2 = new Object[friendsList.size()][columns.length];
		for(int i=0; i<friendsList.size(); i++){
			User u = UserClientController.getUser(friendsList.get(i));

			//이미지가 존재하지 않는 경우, 서버에 이미지 요청함.
//			if(!new File(pathToProfileImage+u.getProfileImage()).exists())
//				userClientController.getProfileImage(u.getUserId());
			userClientController.existUserProfileImage(u);//무조건 갱신
			
			Image scaledImage = new ImageIcon(pathToProfileImage+u.getProfileImage()).getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
			Icon profileImage = new ImageIcon(scaledImage);
			Object[] u_ = {profileImage, u.getUserId(), u.getUserName(), u.getStatusMessage()};
			data2[i] = u_;
		}
		
	};
	
	public FriendsListPanel(){
		setSize(new Dimension(MainFrame.WIDTH, BodyRootPanel.HEIGHT));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(Color.YELLOW);
		
		
		addMeAndFriendsList();
		
	}

	/**
	 * Me목록과  Freinds목록을 현재 패널에 추가한다.
	 */
	public void addMeAndFriendsList(){
		removeAll();
		addTable("Me", data1);
		addTable("Friends", data2);
		revalidate();//자식컴포넌트 재배치
		repaint();
	}
	
	
	/**
	 * 검색결과를 현재패널에 출력한다.
	 * @param list
	 */
	public void addSearchList(List<User> list) {
		
		//LOGIN_USER이거나, LOGIN_USER의 friendsList
		list = list.stream()
					.filter(u -> !friendsList.contains(u.getUserId()) && !UserClientController.LOGIN_USER.getUserId().equals(u.getUserId()))
					.collect(Collectors.toList());

		
		data3 = new Object[list.size()][columns.length];
		int index = 0;
		
		for(User u: list){
			Image scaledImage = new ImageIcon(pathToProfileImage+u.getProfileImage()).getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
			Icon profileImage = new ImageIcon(scaledImage);
			Object[] u_ = {profileImage, u.getUserId(), u.getUserName(), u.getStatusMessage()};
			data3[index++] = u_;
		}		
		
		addTable("Search", data3);
		
		revalidate();//자식컴포넌트 재배치
		repaint();
	}
	
	private void addTable(String label, Object[][] data){
		//1.테이블헤더 노란 라벨 추가
		addLabel(label);
		
		//2.테이블 생성
		DefaultTableModel model = new DefaultTableModel(data, columns){
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            @Override
			public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }

            /**
             * 해당테이블은 readonly로 처리함.
             */
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
            
            
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(80);
        table.removeColumn(table.getColumnModel().getColumn(1));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(COL1WIDTH);
		table.getColumnModel().getColumn(1).setPreferredWidth(COL2WIDTH);
		table.getColumnModel().getColumn(2).setPreferredWidth(COL3WIDTH);
		
		
		//사용자이름은 가운데 정렬함.
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
		//Search인 경우, 이전에 쓰인 컴포넌트를 모두 지우고 새로 쓴다.
		if("Search".equals(label)) removeAll();
		
		if("Me".equals(label)){
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			add(table);
		}
		else{
			
			JScrollPane scrPane = new JScrollPane(table);
			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			table.setTableHeader(null);//테이블헤더 숨기기
			table.setShowVerticalLines(false);//tableCellBorder 숨기기			
			add(scrPane);
		}
			
		//채팅할 user를 관리하기위해 리스트에 담는다.
		tableList.add(table);
		
		//이벤트 리스너 등록
		addMouseListenerForOther(table);
		
	}
	
	private JLabel addLabel(String text){
		JLabel label = new JLabel(text);
		label.setFont(new Font(null, Font.BOLD, 14));
		add(label);
		return label;
	}
	

	/**
	 * 로그인한 사용자 테이블용 마우스리스너 등록
	 * @param table
	 */
	private void addMouseListenerForLoginUser(JTable table) {
		
		table.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				//select row programatically
//				table.setRowSelectionInterval(0, 0);
				
				if(e.getClickCount()==2){
					doubleClickEventHandler(table);
				}
			}

			
			
		});
	}
	

	private void addMouseListenerForOther(JTable table) {
		table.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					doubleClickEventHandler(table);
					return;
				}
				
				
				int selectedRow = 0;
				for(int i=0; i<tableList.size(); i++){
					selectedRow += tableList.get(i).getSelectedRowCount();
				}
				
				if(e.getButton() == MouseEvent.BUTTON3 && selectedRow>0) {
					String promptMessage = "";
					Set<String> clientSet = new HashSet<>();
					clientSet.add(UserClientController.LOGIN_USER.getUserId());
					
					int cnt = 0;//콤마찍기 체크용변수
					
					for(int i=0; i<tableList.size(); i++){
						JTable t = tableList.get(i);
						//getSelectedRows() : 현재선택된 row의 인덱스를 int배열로 리턴한다.
						for(int j=0; j<t.getSelectedRows().length; j++){
							String userId = (String)(t.getModel().getValueAt(t.getSelectedRows()[j],1));
							String userName = (String)(t.getValueAt(t.getSelectedRows()[j],1));						
							
							promptMessage += userName+"["+userId+"]";
							
							if(cnt++ !=selectedRow-1){
								promptMessage +=", ";
							}
								
							clientSet.add(userId);
						}
						
					}
					int selected = JOptionPane.showConfirmDialog(null, promptMessage+"("+(clientSet.size()-1)+"명)과 채팅하시겠습니까?", "karaotalk", JOptionPane.OK_CANCEL_OPTION);
//					System.out.println("selected="+selected);//yes=0, no=1, cancel=2
					
					if(selected==0){
						String chatId = Utils.getEncodedChatId(clientSet);
						
						boolean existChatroom = chatClientController.existChatroom(chatId);
						System.out.println("existChatroom["+chatId+"]="+existChatroom); 
						
						//clientMap에 chatId를 가진 Chat객체가 존재하지 않는 경우
						//clientMap에 Chat객체추가, 채팅로그파일 chatId.txt 추가
						if(!existChatroom)
							chatClientController.addChat(clientSet);
						
						ChatFrame.manageChatFrame(chatId, clientSet);	
						
					}
					
				}
			}
			
		});
	}
	
	/**
	 * 해당user를 더블클릭한 경우처리
	 * 
	 * loginuser, friendsList, userList 공통
	 * 
	 * @param table
	 */
	private void doubleClickEventHandler(JTable table) {
		String userId = (String)(table.getModel().getValueAt(table.getSelectedRow(),1));
		String userName = (String)(table.getValueAt(table.getSelectedRow(),1));
		int selected = JOptionPane.showConfirmDialog(null, userName+"["+userId+"]과 채팅하시겠습니까?", "karaotalk", JOptionPane.OK_CANCEL_OPTION);
		
		if(selected==0){
			Set<String> clientSet = new HashSet<>();
			clientSet.add(userId);
			clientSet.add(UserClientController.LOGIN_USER.getUserId());
			String chatId = Utils.getEncodedChatId(clientSet);
			
			boolean existChatroom = chatClientController.existChatroom(chatId);
			System.out.println("existChatroom["+chatId+"]="+existChatroom); 
			
			//clientMap에 chatId를 가진 Chat객체가 존재하지 않는 경우
			//clientMap에 Chat객체추가, 채팅로그파일 chatId.txt 추가
			if(!existChatroom)
				chatClientController.addChat(clientSet);
			
			ChatFrame.manageChatFrame(chatId, clientSet);	
			
		}
	}
	
	
}
