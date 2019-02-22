package com.kh.karao.run;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.kh.karao.chat.controller.ChatController;
import com.kh.karao.chat.model.vo.Chat;
import com.kh.karao.common.Utils;

public class KaraoTalkChatServer extends Thread {
	private ChatController chatController = new ChatController();
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	public static Map<String, Socket> sessionMap = new HashMap<>();
	
	//DataOutputStream은 직렬화할 수 없기때문에 파일에 기록하거나, 네트워크를 통해 전송할 수 없다.
	//사용자의 DataOutputStream은 어플리케이션 lifecycle에 맞추어 별도롤 관리한다.
	public static Map<String, Map<String, DataOutputStream>> chatManager = new HashMap<>();
	
	public KaraoTalkChatServer(int port) throws IOException{
		serverSocket = new ServerSocket(port);
	}
	
	@Override
	public void run(){
		System.out.println("===================== karaotalk ChatServer Start =====================");
		//접속자가 생길때마다 새 쓰레드를 생성한다.
		while(true){
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Thread serverReceiver = new Thread(new ServerReceiver(socket));
			serverReceiver.start();
			
			
		}
	}
	
	
	
	
	/**
	 * Receiver역할을 할 쓰레드클래스를 내부에 정의함.
	 * @author shqkel1863
	 *
	 */
	class ServerReceiver implements Runnable {
		private Socket socket;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		//매번 접속자가 생길때 마다 생성함.
		public ServerReceiver(Socket socket){
			this.socket = socket;
			try {
				dis = new DataInputStream(socket.getInputStream());
				//socket에서 얻은 OutputStream으로 DataOutputStream객체생성후 client 맵에 저장  
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
			String userId = "";
			String chatId = "";
			Map<String,DataOutputStream> clientMap = null;
			Map<String,Chat> chatroomMap = chatController.getChatroomMap();
			
			try {
				//클라이언트가 최초 접속시, 메세지를 parsing해서 userId를 clientMap의 key로 쓰겠음.
				String m = dis.readUTF();
				System.out.println("접속msg@ServerReceiver.run="+m);
				String[] connectionMade = m.split("§");
				chatId = connectionMade[0];
				userId = connectionMade[1]; 
				System.out.println(Utils.convertToTime(connectionMade[2])+"접속 "+socket.getInetAddress()+":"+socket.getPort()+" chatId="+chatId+", userId="+userId);

				System.out.println("chatroomMap@KaraChatServer="+chatroomMap);
				
				//clientMap:userId=dos에 사용자dos담기(clientMap이 존재하지 않으면 새로 생성함)
				clientMap = chatManager.get(chatId);
				if(clientMap == null) 
					chatManager.put(chatId,clientMap = new HashMap<>());
				clientMap.put(userId, dos);
				
				System.out.println("clientMap="+clientMap);
				
				//글을 쓰면 접속한 모든 클라이언트에게 전송함. 
				while(true){
					String msg = dis.readUTF();
					String[] temp = msg.split("§");
					//서버로그
					System.out.println(Utils.convertToTime(temp[2])+"메세지 chatId="+temp[0]+", userId="+temp[1]+", message="+temp[3]);
					
					//채팅방의 다른 사용자에게 전송함.
					sendToAll(chatId, userId, msg);
					
				}
				
			//클라이언트 종료로 발생하는 Exception은 무시한다.
			} catch(SocketException | EOFException e){
				//ignore
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				//이 클라이언트의 dos를 맵에서 삭제함. 
				clientMap.put(userId, null);
				//서버로그
				System.out.println(Utils.convertToTime(String.valueOf(new Date().getTime()))+"종료 "+socket.getInetAddress()+":"+socket.getPort()+", chatId="+chatId+", userId="+userId);
				
				if(dis != null)
					try { dis.close(); } catch (IOException e) { e.printStackTrace(); }
				if(dos != null)
					try { dos.close(); } catch (IOException e) { e.printStackTrace(); }
				if(socket != null)
					try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		
		
	}
	
	/**
	 * 접속한 모든 클라이언트에게 메세지를 쓰는 메소드.
	 * @param msg
	 */
	public void sendToAll(String chatId, String userId, String msg){
		//DataOutputStream은 직렬화 할 수 없으므로, 어플리케이션 라이프사이클에 맞추어 현재클래스에서 관리함.
		Map<String,DataOutputStream> clientMap = KaraoTalkChatServer.chatManager.get(chatId);
		System.out.println("clientMap@sendToAll="+clientMap);
		
		
		//1.append모드로 채팅로그파일 쓰기
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("chat/"+chatId+".txt"), true));){
			bw.write(msg+"\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//2. 현재메세지를 charoomMap에 저장된 Chat객체의 recentMessage로 등록함.
		Map<String,Chat> chatroomMap = chatController.getChatroomMap();
		Chat currentChat = chatroomMap.get(chatId);
		currentChat.setRecentMessage(msg);
		
		chatController.writeChatroomMap(chatroomMap);
		System.out.println("chatroomMap@sendToAll="+chatController.getChatroomMap());
		
		//3. 현재채팅방의 clientSet의 userId순회
		//clientMap은 현재 접속하지 않은 사용자가 제외되므로 사용금지.
		Set<String> clientSet = currentChat.getClientSet();
//		System.out.println("clientMap@sendToAll="+clientMap);
		//맵 clients의 키타입은 String이다.
//		Iterator<String> it = clientMap.keySet().iterator();
		Iterator<String> it = clientSet.iterator();
		DataOutputStream eachDOS= null;
		
		while(it.hasNext()){
			String key = it.next();//userId
			System.out.println("userId@KaraoChatServer.sendToAll="+key);
			
			//1.사용자의 ChatListPanel의 Chat정보에 전달해서 갱신함(사용자가 접속해 있는 경우)
			try {
				Socket s_ = KaraoTalkChatServer.sessionMap.get(key);
				System.out.println("userId@sendToAll="+key+", "+s_);
				if(s_ != null){
					DataOutputStream dos = new DataOutputStream(s_.getOutputStream());
					dos.writeUTF("message");
					dos.writeUTF(msg);					
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			//2.채팅방의 사용자에게 각각 전송한다.
			//1.message를 생성한 User에게는 전송하지 않는다.
			if(key.equals(userId)) continue;
			
			//iterator에 담아둔 키값을 이용해서 맵에 저장된 dos를 리턴한다.
		
			Optional<DataOutputStream> maybeDos = Optional.ofNullable(clientMap.get(key));
			maybeDos.map(dos->(DataOutputStream)dos)
					.ifPresent(dos->{
						try {
							dos.writeUTF(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
			eachDOS = clientMap.get(key);
//			try {
//				eachDOS.writeUTF(msg);
//			} catch (IOException e) {
//				System.out.println("["+userId+"]에게 전송중 예외발생!!!");
//				e.printStackTrace();
//			}
		}
		
	}
	
}
