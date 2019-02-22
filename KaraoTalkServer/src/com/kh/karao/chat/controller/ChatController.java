package com.kh.karao.chat.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kh.karao.chat.model.io.ChatIO;
import com.kh.karao.chat.model.vo.Chat;
import com.kh.karao.user.model.io.UserIO;

public class ChatController {
	private ChatIO chatIO = new ChatIO();
	private UserIO userIO = new UserIO();
	
	public ChatController(){
		getChatroomMap();
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Chat> getChatroomMap() {
		Map<String, Chat> chatroomMap = chatIO.getChatroomMap();
		System.out.println("ChatroomMap UPDATED!\nChatController.chatroomMap@ChatController="+chatroomMap);
		return chatroomMap;
	}

	public boolean existChatroom(String chatId) {
		return chatIO.getChatroomMap().containsKey(chatId);
	}
	
	public Chat getChat(String chatId){
		return chatIO.getChatroomMap().get(chatId);
	}

	public void addChat(Set<String> clientSet) {
		//1.chatroomMap에 추가
		chatIO.addChat(clientSet);
		getChatroomMap();
		
		//2.사용자의 chatList에 각각추가
		userIO.addChat(clientSet);
	}
	

	/**
	 * 특정사용자의 chatId:String으로 이루어진 List을 매개인자로 받아서
	 * chatId:String=Chat객체로 이루어진 chatMap을 리턴함.
	 * 
	 * @param chatList
	 * @return
	 */
	public Map<String, Chat> getChatMap(List<String> chatList) {
		//전체 chatroom정보
		Map<String, Chat> chatroomMap = chatIO.getChatroomMap();
		//chatList에 해당하는 chatroom정보
		Map<String, Chat> chatMap = new HashMap<>();

		for(String chatId : chatList){
			chatMap.put(chatId, chatroomMap.get(chatId));
		}
		return chatMap;
	}

	public void writeChatroomMap(Map<String, Chat> chatroomMap) {
		chatIO.writeChatroomMap(chatroomMap);
	}

	public Map<String, Date> getLastConnectionMap(String chatId) {
		System.out.println("lastConnectionMap@ChatController.getLastConnectionMap="+getChat(chatId).getLastConnectedMap());
		return getChat(chatId).getLastConnectedMap();
	}

	public void updateLastConnectionMap(String chatId, String userId, Date d) {
		Map<String, Chat> chatroomMap = chatIO.getChatroomMap();
		chatroomMap.get(chatId).updateLastConnectionMap(userId, d);
		//새로 쓰기작업한다.
		chatIO.writeChatroomMap(chatroomMap);
		System.out.println("lastConnectionMap@ChatController.updateLastConnectionMap="+chatroomMap.get(chatId).getLastConnectedMap());
	}
	
}
