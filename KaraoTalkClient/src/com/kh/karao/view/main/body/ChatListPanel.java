package com.kh.karao.view.main.body;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.chat.model.vo.Chat;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.chat.ChatFrame;

public class ChatListPanel extends JPanel {
	private ChatClientController chatClientController = new ChatClientController();
	private UserClientController userClientController = new UserClientController();
	
	private final int COL1WIDTH = 100;
	private final int COL2WIDTH = 120;
	private final int COL3WIDTH = 180;
	private final int COL4WIDTH = 80;
	private final int COL5WIDTH = 20;

	String[] columns = {"Profile", "chatId", "clientSet", "Users", "Message", "Time", "Unread"};
	private Object[][] data;
	private List<String> chatList;
	private Map<String, Chat> chatMap;
	
	String pathToProfileImage = Utils.getProperty("user.profileImage");
	
	public JTable table;//매번 갱신할 용도의 테이블컴포넌트 
	JScrollPane scrPane;
	
	public ChatListPanel(){
		setSize(new Dimension(MainFrame.WIDTH, BodyRootPanel.HEIGHT));
		addChatList();//객체생성시 chatList테이블생성완료!
	}
	
	public void addChatList() {
		//chatList, ChatMap, userMap 최신화!
		upToDateChatInfo();
		
		System.out.println("chatList@ChatListPanel="+chatList);
		System.out.println("chatMap@ChatListPanel="+chatMap);
		
		data = new Object[chatList.size()][columns.length];
		for(int i=0; i<chatList.size(); i++){
			//데이터생성후 추가
			data[i] = getData(chatList.get(i));;
		}
		
		
		DefaultTableModel model = new DefaultTableModel(data, columns){
			
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
		
		table = new JTable(model);
		
		//hidden컬럼 만들기 - chatId, clientSet을 차례로 hiddent처리함
        table.removeColumn(table.getColumnModel().getColumn(1));
        table.removeColumn(table.getColumnModel().getColumn(1));
        
		table.setRowHeight(80);
		table.getColumnModel().getColumn(0).setPreferredWidth(COL1WIDTH);
		table.getColumnModel().getColumn(1).setPreferredWidth(COL2WIDTH);
		table.getColumnModel().getColumn(2).setPreferredWidth(COL3WIDTH);
		table.getColumnModel().getColumn(3).setPreferredWidth(COL4WIDTH);
		table.getColumnModel().getColumn(4).setPreferredWidth(COL5WIDTH);
		
		table.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				//더블클릭시
				if(e.getClickCount()==2){
					
					int rowIndexFromView = table.getSelectedRow();
					int rowIndexToModel = table.convertRowIndexToModel(rowIndexFromView);
					System.out.println("현재선택된 row="+rowIndexFromView);
					System.out.println("실제model의 row="+rowIndexToModel);
					
					if(rowIndexFromView==-1 && rowIndexToModel==-1) return; 
					
					String chatId = (String)(table.getModel().getValueAt(rowIndexToModel,1));
					//table에서는 보이는 rowIndex를 적용한다.
					Set<String> clientSet = (Set<String>)(table.getModel().getValueAt(rowIndexToModel,2));
					
					System.out.println("chatFrame요청={chatId="+chatId+", clientSet="+clientSet+"}");
					ChatFrame.manageChatFrame(chatId, clientSet);
				}
			}
			
		});
		
		//Unread수는 가운데 정렬함.
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		
		//최근메세지순으로 정렬함.
		//java.lang.ArrayIndexOutOfBoundsException: 0 >= 0 방지를 위해 rowCount체크
		if(table.getRowCount()>0) tableSorter(table);
		
		//기존 컴포넌트가 존재한다면 삭제
		if(scrPane!=null) remove(scrPane);
		
		scrPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
//		table.setTableHeader(null);//테이블헤더 숨기기
		table.setShowVerticalLines(false);//tableCellBorder 숨기기
		
		scrPane.setPreferredSize(new Dimension(MainFrame.WIDTH,BodyRootPanel.HEIGHT));
		
		add(scrPane);
		System.out.println("---------------addChatList메소드완료!!---------------");
		
		
	}
	
	public void upToDateChatInfo() {
		chatList = UserClientController.LOGIN_USER.getChatList();
		chatMap = chatClientController.getChatMap(chatList);//chatId=Chat 타입의 chatMap을 최신화
	}

	public Object[] getData(String chatId) {
		Chat chat = chatMap.get(chatId);
		System.out.println("chatMap@ChatListPanel.getData="+chatMap);
		
		//채팅참여자의 사진
		//2명인 경우 : 채팅상대
		//3명인 경우 : ?
		//4명인 경우 : ?
		Set<String> clientSet = chat.getClientSet();
		String userProfileImage = "";
		for(String userId : clientSet){
			//self채팅방인 경우가 아니면, LOGIN_USER는 skip
			if(clientSet.size()!=1 && userId.equals(UserClientController.LOGIN_USER.getUserId())) continue;
			
			userProfileImage = userClientController.getUser(userId).getProfileImage();
			//이미지가 존재하지 않는 경우, 서버에 이미지 요청함.
			if(!new File(pathToProfileImage+userProfileImage).exists())
				userClientController.getProfileImage(userId);
		}
		
		Image scaledImage = new ImageIcon(pathToProfileImage+userProfileImage).getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT);
		Icon profileImage = new ImageIcon(scaledImage);
		
		String recentMessage = "";
		String[] temp = chat.getRecentMessage().split("§");
		if(temp.length>1)
			recentMessage = Utils.html2text(temp[3]);
			
		Object[] data = {profileImage, 		//Icon객체
					chat.getChatId(), 
					chat.getClientSet(),
					ChatClientController.chatWith(chat.getClientSet(),UserClientController.LOGIN_USER.getUserId()), 
					recentMessage,		//JLabel객체 
					chat.getRecentMessageTime(), 
					0};//최근메세지개수를 어떻게 계산할 것인가?
		return data;
	}

	public void tableSorter(JTable table){
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(sorter);
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		 
		//최근메세지 역순으로 정렬
		int columnIndexToSort = 5;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		 
		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}
	
}
