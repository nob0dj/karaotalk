package com.kh.karao.run;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kh.karao.chat.controller.ChatController;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserController;
import com.kh.karao.user.model.vo.User;

public class KaraoTalkServerMain {
	static int karaoMainServerPort = 8888;
	static int karaoChatServerPort = 7777;
	
	private ChatController chatController = new ChatController();
	private UserController userController = new UserController();
	private ServerSocket serverSocket = null;
	private Socket socket = null;
	
	public KaraoTalkServerMain(int port) throws IOException{
		serverSocket = new ServerSocket(port);
		init();
	}
	
	public void init(){
		System.out.println("===================== karaotalk MainServer Start =====================");
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
		
		//매번 접속요청 발생시마다 생성함.
		public ServerReceiver(Socket socket){
			this.socket = socket;
			try {
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
	 		
		}
		
		@Override
		public void run() {
			ObjectInputStream in = null;
			ObjectOutputStream out = null;
			BufferedInputStream bufferIn = null;
			BufferedOutputStream bufferOut = null;
			BufferedReader br = null;
			BufferedWriter bw = null;
			
			String[] request = null;
			String requestId = "";//사용자요청 문자열
			
			try(
			   DataInputStream dis = new DataInputStream(socket.getInputStream());
			   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
			   ) {
				
				request = dis.readUTF().split("§");
				requestId = request[0];
				
				System.out.println(Utils.convertToTime()+" "+socket.getInetAddress()+":"+socket.getPort()+"에서 karaoServer : "+requestId+" 요청");
				
				switch (requestId) {
				case "loginCheck":
					in = new ObjectInputStream(socket.getInputStream());
					User u = Utils.getObject(in);
					int result = userController.loginCheck(u);
					dos.writeUTF(requestId+"§"+result);
					break;
					
				case "existChatroom":
					dos.writeUTF(requestId+"§"+chatController.existChatroom(request[1]));
					break;
					
				case "addChat":
					in = new ObjectInputStream(socket.getInputStream());
					Set<String> clientSet = Utils.getObject(in);
					chatController.addChat(clientSet);

					//새로갱신된 LOGIN_USER정보 전송
					out = new ObjectOutputStream(socket.getOutputStream());
					String userId = dis.readUTF();//LOGIN_USER.userId
					System.out.println("사용자가 보낸 LOGIN_USER.userId="+userId);
					out.writeObject(userController.getUser(userId));
					break;

				case "getUser":
					out = new ObjectOutputStream(socket.getOutputStream());
					
					//전체회원목록조회
					if(request.length==1){//요청아이디가 없는 경우
						Map<String, User> users = userController.getUser();
						out.writeObject(users);
						System.out.println("getUser()="+users);
					}
					//회원한명 조회
					else{
						u = userController.getUser(request[1]);
						out.writeObject(u);
						System.out.println("getUser("+request[1]+")="+u);
					}
					break;
					
				case "getChatMap"://사용자로 부터 chatList:List<String>를 받아서 chatMap:Map<String,Chat>을 리턴함.
					in = new ObjectInputStream(socket.getInputStream());
					List<String> chatList = Utils.getObject(in);
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(chatController.getChatMap(chatList));
					System.out.println("chatList@mainServer.getChatMap="+chatList);
					System.out.println("chatMap@mainServer.getChatMap="+chatController.getChatMap(chatList));
					break;
					
				case "getProfileImage":
					String pathToProfileImage = Utils.getProperty("user.profileImage");
					String profileImage = userController.getUser(request[1]).getProfileImage();
					//1.profileImage파일명 전송
					dos.writeUTF(profileImage);
					//2.파일전송
					bufferIn = new BufferedInputStream(new FileInputStream(pathToProfileImage+profileImage));
					int len;
		            int size = 4096;
		            byte[] data = new byte[size];
		            while ((len = bufferIn.read(data)) != -1) {
		                dos.write(data, 0, len);
		            }
					System.out.println("["+profileImage+"] 파일전송완료!");
					break;
					
				case "getMessages":
					br = new BufferedReader(new InputStreamReader(new FileInputStream("chat/"+request[1]+".txt")));
					bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					String line = "";
					while((line=br.readLine())!=null){
						bw.write(line+"\n");

						//마지막 auto flush기능을 해주지 못해서 수동으로 호출함.
						//bw.close()실행중 Socket Closed 예외가 발생하면서, 
						bw.flush();
					}
					System.out.println("["+request[1]+".txt] 파일전송완료!");
					break;
					
				case "connect":
					//1.socket을 map객체에 보관
					KaraoTalkChatServer.sessionMap.put(request[1], socket);
					//2.현제쓰레드는 ClientSession을 담당하는 쓰레드로 사용대기한다.
					//Socket의 활성화여부는 isConnected()가 아닌 read()메소드로 한다.
					while(true)
						//소켓이 종료되면 IOException을 던진다.
						if(socket.getInputStream().read()== -1) break;
					break;
					
				/*case "getLastConnectionMap"://ChatFrame 처음 오픈시 요청
					out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(chatController.getLastConnectionMap(request[1]));
					break;
				*/
				case "updateLastConnectionMap"://ChatFrame이 활성화되어서 읽음처리함.
					in = new ObjectInputStream(socket.getInputStream());
					Date d = Utils.getObject(in);
					chatController.updateLastConnectionMap(request[1], request[2], d);//chatId, userId
					
					//채팅방의 모든 사용자에게 알림
					clientSet = chatController.getChat(request[1]).getClientSet();
					for(String userId_ : clientSet){
						System.out.println("userid_@KaraMainServer.updateLastConnectionMap="+userId_);
						Socket s_ = KaraoTalkChatServer.sessionMap.get(userId_);

						//요청사용자는 ChatFrame관련작업
						if(userId_.equals(request[2])){
							//본인인 경우. 현재 연결socket에 lastConnectionMap:Map<String,Date>을 전송.
							//세션소켓이 아니므로 주의!
							out = new ObjectOutputStream(socket.getOutputStream());
							out.writeObject(chatController.getLastConnectionMap(request[1]));
						}
						
						//접속session이 있는 경우
						if(s_!=null){
							//sessionMap에서 가져온 socket을 사용하는 스트림은 닫지 않는다.
							new DataOutputStream(s_.getOutputStream()).writeUTF("updateLastConnectionMap§"+request[1]);
							new ObjectOutputStream(s_.getOutputStream()).writeObject(chatController.getLastConnectionMap(request[1]));								
						}
					}
					
					break;
				case "findUsersByUserId":
					out = new ObjectOutputStream(socket.getOutputStream());
					List<User> resultList = userController.findUsersByUserId(request[1]);
					out.writeObject(resultList);
					break;
				default:
					break;
				}
				
			} catch (IOException e) {
				if("connect".equals(requestId)){
					KaraoTalkChatServer.sessionMap.remove(request[1]);
					System.out.println(request[1]+"의 세션이 종료되었습니다.");
				} 
				else e.printStackTrace();
				
			}  finally {
				if(in != null)
					try { in.close(); } catch (IOException e) { e.printStackTrace(); }
				if(out != null)
					try { out.close(); } catch (IOException e) { e.printStackTrace(); }
				if(bufferIn != null)
					try { bufferIn.close(); } catch (IOException e) { e.printStackTrace(); }
				if(bufferOut != null)
					try { bufferOut.close(); } catch (IOException e) { e.printStackTrace(); }
				if(br != null)
					try { br.close(); } catch (IOException e) { e.printStackTrace(); }
				if(bw != null)
					try { bw.close(); } catch (IOException e) { e.printStackTrace(); }
				if(socket != null)
					try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		try {
			Thread chatServer = new KaraoTalkChatServer(karaoChatServerPort);
			chatServer.start();
			
			KaraoTalkServerMain mainServer = new KaraoTalkServerMain(karaoMainServerPort);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
