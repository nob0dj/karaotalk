package com.kh.karao.chat.model.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.kh.karao.chat.model.vo.Chat;
import com.kh.karao.common.Utils;

public class ChatIO {
	
	public synchronized Map<String, Chat> getChatroomMap(){
		Map<String, Chat> chatroomMap = null;
		
		try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("karao/chatroom.ser")));){
			chatroomMap = (Map<String, Chat>)ois.readObject();
		} catch (EOFException e){
			//처리코드 없음.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return chatroomMap;
	}
	
	
	public void addChat(Set<String> clientSet) {
		System.out.println("================================");
		System.out.println("addChat("+clientSet+")@ChatIO 처리 시작!");
		System.out.println("----------------------------------------------");
		
		Map<String, Chat> chatroomMap = getChatroomMap();
		String chatId = Utils.getEncodedChatId(clientSet);
		chatroomMap.put(chatId, new Chat(clientSet));
		
		try(ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("karao/chatroom.ser")))){
			
			//1.chatroomMap필드에 추가후에 chatrooms.ser파일에 쓰기작업함.
			out.writeObject(chatroomMap);
			System.out.println("추가된 chatroomMap@ChatIO="+chatroomMap);
			
			//2.채팅로그파일 생성
			File f = new File("chat/"+chatId+".txt");
			f.createNewFile();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("----------------------------------------------");

	}
	
	public synchronized void writeChatroomMap(Map<String,Chat> chatroomMap){
		
		try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("karao/chatroom.ser")))){
			oos.writeObject(chatroomMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Dev모드
	 * 최초세팅용 메소드
	 */
	public void init() {
		Map<String, Chat> chatroomMap = new HashMap<>();
		writeChatroomMap(chatroomMap);
		
//		Set<String> clientSet = new HashSet<>();
//		clientSet.add("shqkel1863");
//		clientSet.add("khpi");
//		addChat(clientSet);
//		
//		clientSet = new HashSet<>();
//		clientSet.add("shqkel1863");
//		clientSet.add("hoon");
//		addChat(clientSet);
//
//		clientSet = new HashSet<>();
//		clientSet.add("shqkel1863");
//		clientSet.add("khpi");
//		clientSet.add("hoon");
//		addChat(clientSet);
//		
//		clientSet = new HashSet<>();
//		clientSet.add("shqkel1863");
//		clientSet.add("khpi");
//		clientSet.add("hoon");
//		clientSet.add("honggd");
//		addChat(clientSet);
		
	}

}
