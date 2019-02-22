package com.kh.karao.chat.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.kh.karao.chat.model.vo.Chat;
import com.kh.karao.common.Utils;
import com.kh.karao.user.controller.UserClientController;
import com.kh.karao.view.MainFrame;

/**
 * 클라이언트용 Chat컨트롤러 (서버통신용)
 * 
 * @author shqkel1863
 *
 */
public class ChatClientController {

	public boolean existChatroom(String chatId) {
		boolean result = false;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			System.out.println(Utils.convertToTime()+" "+MainFrame.serverHost+":"+MainFrame.mainServerPort+"서버에 접속");
			
			dos.writeUTF("existChatroom§"+chatId);
			String[] res = dis.readUTF().split("§");
			if("existChatroom".equals(res[0]))
				result = Boolean.parseBoolean(res[1]);
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return result;
	}

	public void addChat(Set<String> clientSet) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());		
		   ) {
			
			dos.writeUTF("addChat");
			out = new ObjectOutputStream(socket.getOutputStream());	
			out.writeObject(clientSet);

			//현재 LOGIN_USER최신화 
			//다른 소켓을 만들어 요청하지 않고, 하나의 소켓에서 처리한다. - 쓰레드가 나뉘면, LOGIN_USER 업데이트가 먼저 일어날 수 있다.
			dos.writeUTF(UserClientController.LOGIN_USER.getUserId());
			in = new ObjectInputStream(socket.getInputStream());
			UserClientController.LOGIN_USER = Utils.getObject(in);
			System.out.println("LOGIN_USER updated!!! : "+UserClientController.LOGIN_USER);
			
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

	public Map<String, Chat> getChatMap(List<String> chatList) {
		Map<String, Chat> chatMap = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());		
		   ) {
			
			dos.writeUTF("getChatMap");
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(chatList);
			
			in = new ObjectInputStream(socket.getInputStream());
			chatMap = Utils.getObject(in);	
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try { in.close(); } catch (IOException e) { e.printStackTrace(); }
			if(out != null)
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return chatMap;
	}

	public String getMessages(String chatId) {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			System.out.println(Utils.convertToTime()+" "+MainFrame.serverHost+":"+MainFrame.mainServerPort+"서버에 접속");
			
			dos.writeUTF("getMessages§"+chatId);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = "";
			while((line=br.readLine())!=null){
				sb.append(line+"\n");
			}
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
//		System.out.println("getMessages@ChatClentsController="+sb.toString());
		return sb.toString();
	}

	public Map<String, Date> getLastConnectionMap(String chatId) {
		Map<String, Date> lastConnectionMap = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());		
		   ) {
			
			dos.writeUTF("getLastConnectionMap§"+chatId);
			
			in = new ObjectInputStream(socket.getInputStream());
			lastConnectionMap = Utils.getObject(in);	
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try { in.close(); } catch (IOException e) { e.printStackTrace(); }
			if(out != null)
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return lastConnectionMap;
	}

	public Map<String, Date> updateLastConnectionMap(String chatId) {
		Map<String, Date> lastConnectionMap = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());		
		   ) {
			
			dos.writeUTF("updateLastConnectionMap§"+chatId+"§"+UserClientController.LOGIN_USER.getUserId());
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new Date());//클라이언트기준 현재시각을 서버에 전송함.
			
			//chatframe생성시 관련작업
			in = new ObjectInputStream(socket.getInputStream());
			lastConnectionMap = Utils.getObject(in);	
			
		} catch (EOFException e) {

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(in != null)
				try { in.close(); } catch (IOException e) { e.printStackTrace(); }
			if(out != null)
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return lastConnectionMap;
	}
	
	/**
     * 채팅참여자중에 본인을 제외하고, 사전순으로 정렬된 목록 리턴
     * 
     * @param clientSet
     * @param loginUser
     * @return
     */
    public static String chatWith(Set<String> clientSet, String loginUser){
        String chatWith = "";
         
        //본인self채팅방인경우
        if(clientSet.size()==1 && clientSet.contains(loginUser)) return UserClientController.getUser(loginUser).getUserName();
         
        Set<String> clientSortedSet = new TreeSet<>(clientSet);
        clientSortedSet.remove(loginUser);
         
        int cnt = 0;
        for(String s : clientSortedSet){
            chatWith += UserClientController.getUser(s).getUserName();
            if(cnt!=clientSortedSet.size()-1) chatWith += ", ";
            cnt++;
        }
         
        return chatWith;
    }
	
	
	
}
