package com.kh.karao.user.controller;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.kh.karao.chat.controller.ChatClientController;
import com.kh.karao.common.Utils;
import com.kh.karao.user.model.vo.User;
import com.kh.karao.view.MainFrame;

public class UserClientController {
	public static final int LOGIN_OK = 1;
	public static final int WRONG_PASSWORD = 0;
	public static final int NON_EXIST_USERID = -1;
	
	
	public static User LOGIN_USER = null;//로그인한 사용자정보
	
	public static void upToDateLOGIN_USER(){
		LOGIN_USER = getUser(LOGIN_USER.getUserId());
	}
	
	/**
	 * 이미지가 존재하지 않는 경우, 서버에 이미지 요청함.
	 * 
	 * @param userId
	 */
	public void existUserProfileImage(User u){
		if(!new File(Utils.getProperty("user.profileImage")+u.getProfileImage()).exists())
			getProfileImage(u.getUserId());
	}
	
	public int loginCheck(User u){
		int result = -1;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			//요청
			dos.writeUTF("loginCheck");
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(u);
			
			//응답처리
			String[] res = dis.readUTF().split("§");
			if("loginCheck".equals(res[0])){
				result = Integer.parseInt(res[1]);
			}
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
			if(ois != null)
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		
		return result;
	}
	
	
	public static User getUser(String userId){
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		User u = null;
		
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			//회원전체목록을 리턴함.
			dos.writeUTF("getUser§"+userId);
			
			ois = new ObjectInputStream(socket.getInputStream());
			u = Utils.getObject(ois);
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
			if(ois != null)
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return u;
	}


	/*public static Map<String, User> getUser() {
		ObjectOutputStream oos = null;
		ObjectInputStream in = null;
		Map<String, User> userMap = null;
		
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			//회원전체목록을 리턴함.
			dos.writeUTF("getUser");
			
			in = new ObjectInputStream(socket.getInputStream());
			userMap = Utils.getObject(in);
			System.out.println("userMap@client="+userMap);
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
			if(in != null)
				try { in.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return userMap;
	}*/


	public void getProfileImage(String userId) {
		BufferedOutputStream out = null;
		String path = Utils.getProperty("user.profileImage");
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			//해당회원의 ProfileImage요청
			dos.writeUTF("getProfileImage§"+userId);
			
			//서버로부터 전송데이터처리
			//1.파일이름
			String userProfileImage = dis.readUTF();

			//2.파일
			out = new BufferedOutputStream(new FileOutputStream(path+userProfileImage));
			// 바이트 데이터를 전송받으면서 기록
            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while ((len = dis.read(data)) != -1) {
                out.write(data, 0, len);
            }
            
            System.out.println("["+userProfileImage+"] 파일수신완료!");
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null)
				try { out.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}


	public void connect() {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			dos.writeUTF("connect§"+UserClientController.LOGIN_USER.getUserId());
			
			while(true){
				String msg = dis.readUTF();
				System.out.println("msg@UserClientController.connect="+msg);
			}
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
			if(ois != null)
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
		}
	}

	public List<User> findUsersByUserId(String search) {
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		List<User> resultList = null;
		
		try(
		   Socket socket = new Socket(MainFrame.serverHost, MainFrame.mainServerPort);
		   DataInputStream dis = new DataInputStream(socket.getInputStream());
		   DataOutputStream dos = new DataOutputStream(socket.getOutputStream());	
		   ) {
			//회원전체목록을 리턴함.
			dos.writeUTF("findUsersByUserId§"+search);
			
			ois = new ObjectInputStream(socket.getInputStream());
			resultList = Utils.getObject(ois);
			
		} catch (EOFException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(oos != null)
				try { oos.close(); } catch (IOException e) { e.printStackTrace(); }
			if(ois != null)
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
		}
		return resultList;
	}
	
	
}
