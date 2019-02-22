package com.kh.karao.session;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.view.MainFrame;
import com.kh.karao.view.chat.ChatBodyPanel;
import com.kh.karao.view.chat.ChatFrame;
import com.kh.karao.view.chat.ChatRootPanel;
import com.kh.karao.view.main.body.BodyRootPanel;
import com.kh.karao.view.main.body.ChatListPanel;

public class ClientSession implements Runnable {
	Socket socket;
	
	public ClientSession(){
		//세션유지를 위한 소켓 요청
		try {
			this.socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
			Thread clientSession = new Thread(this);
			clientSession.start();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void run() {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try(
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			dos.writeUTF("connect§"+UserClientController.LOGIN_USER.getUserId());
			
			while(true){
				String[] temp = dis.readUTF().split("§");
				String key = temp[0];
				
				switch (key) {
				case "message":
					String msg = dis.readUTF();//1030803050§shqkel1863§1538913910733§<html>ggg</html>
					String[] temp_ = msg.split("§");
					
					ChatListPanel chatListPanel = ((ChatListPanel)BodyRootPanel.panelArr[1]);
					JTable table = chatListPanel.table;
					DefaultTableModel model = (DefaultTableModel)table.getModel();
					
					
					
					//모델에서 해당하는 row를 찾아서 해당값을 갱신한다.
					boolean isExist = false;
					for(int i=0; i<model.getRowCount(); i++){
						String chatId = (String)model.getValueAt(i, 1);
						if(temp_[0].equals(chatId)){
							isExist = true;
							
							model.setValueAt(Utils.html2text(temp_[3]), i, 4);//recentMessage
							model.setValueAt(Utils.convertToChatTime(temp_[2]), i, 5);//recentMessageTime
							
							//현재 focus된 창이 해당chatId가 아니라면 +1한다.(현재chatframe이 존재하지 않거나, 존재하지만 focus가 없는 경우)
							//	getFocusOwner : 자식객체중 focus를 가진객체를 리턴, 없으면 null
							//	isFocusOwner : 해당객체가 focus를 가졌는지여부를 리턴
//							System.out.println("getFocusOwner@ClientSession="+ChatFrame.chatFrameMap.get(chatId).getFocusOwner());//자식객체중 focus를 가진 component리턴 => 전송JButton
//							System.out.println("isFocusOwner@ClientSession="+ChatFrame.chatFrameMap.get(chatId).isFocusOwner());
							ChatFrame chatFrame = ChatFrame.chatFrameMap.get(chatId); 
							
							if(chatFrame == null //채팅방 chatframe이 없는 경우
							|| (chatFrame != null && chatFrame.getFocusOwner()==null)){//채팅방 chatframe이 있지만, focus가 없는 경우
								model.setValueAt(((int)model.getValueAt(i, 6))+1, i, 6);//unread	
								
								//윈도우 알림기능 사용하기
								push(temp_[1], Utils.html2text(temp_[3]));
							}
							
							chatListPanel.tableSorter(table);
							
							break;
						}
					}
					
					//채팅방이 존재하지 않는경우
					//chatListPanel의 테이블에 동적으로 row를 추가한다.
					if(!isExist){
						System.out.println(temp_[0]+"row 동적추가!");
						
						UserClientController.upToDateLOGIN_USER();//사용자정보 최신화, chatList갱신
						chatListPanel.upToDateChatInfo();//chatMap
						Object[] rowData = chatListPanel.getData(temp_[0]);
						model.addRow(rowData);
						
					}
					
					break;
				case "updateLastConnectionMap":
					String chatId = temp[1];
					in = new ObjectInputStream(socket.getInputStream());
					Map<String,Date> lastConnectionMap = Utils.getObject(in);
					
					//기존채팅방의 lastConnectionMap 갱신 및 messageUnread카운트 수정
					ChatFrame chatFrame =  ChatFrame.chatFrameMap.get(chatId);
					if(chatFrame==null) break;
					
					ChatBodyPanel chatBodyPanel = (ChatBodyPanel)((ChatRootPanel)chatFrame.getChatRootPanel()).getChatBodyPanel();
					chatBodyPanel.updateMessageUnread(lastConnectionMap);
					break;

				default:
					break;
				}
				
				
			}
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try { in.close(); } catch (IOException e) { e.printStackTrace(); }
			if(out != null)
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	/**
	 * 사용자에게 os가 제공하는 알림메세지를 제공한다.
	 * 
	 * 알림창 클릭이벤트를 설정 추가할 것.
	 * 
	 * @param userId
	 * @param message
	 */
	public void push(String userId, String message) {
        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "karaotalk");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}

        trayIcon.displayMessage("karaotalk New Message", userId+" : "+message, MessageType.INFO);

        //아래코드는 동작하지 않음(windows10에서만 그런지 아닌지 확인할 것)
        trayIcon.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		System.out.println("trayIcon clicked!");
        	}
        });
    }

}
