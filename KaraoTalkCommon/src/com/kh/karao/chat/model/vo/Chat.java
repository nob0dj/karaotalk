package com.kh.karao.chat.model.vo;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kh.karao.common.Utils;

public class Chat implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String chatId;
	private transient Map<String,DataOutputStream> clientMap;//userId와 socket의 outputStream객체
	//DataOutputStream은 Serializable을 구현한 객체가 아니라, java.io.NotSerializableException 유발하므로, transient처리함.
	private Set<String> clientSet;
	private String recentMessage = "";//Chat객체를 기반으로 JTableModel작성시 NPE방지용 초기화
	
	private Map<String, Date> lastConnectionMap;//최근접속시각정보
	
	/**
	 * Chat의 clientSet을 입력받아 chatId생성함.
	 * @param clientSet
	 */
	public Chat(Set<String> clientSet) {
		this.chatId = Utils.getEncodedChatId(clientSet);
		this.clientSet = clientSet;
		this.lastConnectionMap = new HashMap<>();
	}
	

	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public Map<String, DataOutputStream> getClientMap() {
		return clientMap;
	}
	public void setClientMap(Map<String, DataOutputStream> clientMap) {
		this.clientMap = clientMap;
	}
	public Set<String> getClientSet() {
		return clientSet;
	}
	public void setClientSet(Set<String> clientSet) {
		this.clientSet = clientSet;
	}
	public String getRecentMessage() {
		return recentMessage;
	}
	public void setRecentMessage(String recentMessage) {
		this.recentMessage = recentMessage;
	}
	public String getRecentMessageTime() {
		if("".equals(recentMessage)) return "";
		return Utils.convertToChatTime(recentMessage.split("§")[2]);
	}
	public Map<String, Date> getLastConnectedMap() {
		return lastConnectionMap;
	}
	public void setLastConnectedMap(Map<String, Date> lastConnectionMap) {
		this.lastConnectionMap = lastConnectionMap;
	}


	@Override
	public String toString() {
		return "Chat [chatId=" + chatId + ", clientSet=" + clientSet + ", recentMessage=" + recentMessage
				+ ", lastConnectionMap=" + lastConnectionMap + "]";
	}

	/**
	 * userId의 최근접속정보를 Map객체에 저장함. 
	 * unread카운팅에 사용한다.
	 * 
	 * @param userId
	 */
	public void updateLastConnectionMap(String userId, Date d) {
		lastConnectionMap.put(userId, d);
	}
	
	
	
	
	
}
