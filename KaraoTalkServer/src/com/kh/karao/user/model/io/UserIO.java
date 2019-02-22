package com.kh.karao.user.model.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kh.karao.common.Utils;
import com.kh.karao.user.model.vo.User;

public class UserIO {
	
	public synchronized Map<String, User> getUser(){
		Map<String, User> userMap = null;
		
		try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("karao/user.ser")));){
			
			userMap = (Map<String, User>)ois.readObject();
			
		} catch (EOFException e){
			//처리코드 없음.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return userMap;
	}
	
	public User getUser(String uesrId){
		
		return getUser().get(uesrId);
	}

	public void addChat(Set<String> clientSet) {
		System.out.println("================================");
		System.out.println("addChat("+clientSet+")@UserIO 처리!");
		System.out.println("----------------------------------------------");
		
		Map<String, User> userMap = getUser();
		
		String chatId = Utils.getEncodedChatId(clientSet);
		for(String userId : clientSet){
			User u = userMap.get(userId);
			List<String> chatList = u.getChatList();
			if(chatList == null) u.setChatList(new ArrayList<>());
			if(!chatList.contains(chatId)) chatList.add(chatId);

			System.out.println(userId+"의 chatList="+chatList);
		}
		
		writeUserMap(userMap);
		
		System.out.println("----------------------------------------------");
	}
	
		/**
		 * 최신화된 userMap 쓰기작업
		 */
		public synchronized void writeUserMap(Map<String, User> userMap){
			try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("karao/user.ser")))){
				oos.writeObject(userMap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	/**
	 * 최초개발환경 setting메소드
	 * 
	 */
	public void init() {
		
		Map<String, User> userMap = new HashMap<>();
		
		List<String> friendsList = new ArrayList<>();
		friendsList.add("khpi");
		friendsList.add("hoon");
		friendsList.add("dongxuan09");
		friendsList.add("usb_teacher");
		friendsList.add("honggd");
		List<String> chatList = new ArrayList<>();
		
		userMap.put("shqkel1863", new User("shqkel1863", "1234", "김동현", "hyunta.jpg", "몸과 마음은 하나다", friendsList, chatList));
		
		friendsList = new ArrayList<>();
		friendsList.add("shqkel1863");		
		chatList = new ArrayList<>();
		userMap.put("khpi", new User("khpi", "1234", "김현", "khpi.jpg", "무사귀환", friendsList, chatList));

		chatList = new ArrayList<>();
		userMap.put("hoon", new User("hoon", "1234", "훈쿤", "user.png", "삽삽삽", new ArrayList<>(), chatList));
		
		chatList = new ArrayList<>();
		userMap.put("honggd", new User("honggd", "1234","홍길동","user.png", "아버지~", new ArrayList<>(), new ArrayList<>()));
		
		userMap.put("sinsa", new User("sinsa", "1234", "신사임당", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		userMap.put("dongxuan09", new User("dongxuan09", "1234", "찐똥구리", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		userMap.put("usb_teacher", new User("usb_teacher", "1234", "유병승", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		userMap.put("sinwoo_park", new User("sinwoo_park", "1234", "박신우", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		userMap.put("junghut", new User("junghut", "1234", "유정훈", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		userMap.put("monte", new User("monte", "1234", "델몬트", "user.png", "", new ArrayList<>(), new ArrayList<>()));
		
		writeUserMap(userMap);
	}
}
