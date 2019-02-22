package com.kh.karao.user.model.vo;

import java.io.Serializable;
import java.util.List;

import com.kh.karao.chat.model.vo.Chat;

/**
 * User클래스는 동일한 userId필드를 가질 경우, 같은 객체로 간주하도록 equals/hashCode메소드를 오버라이딩함.
 * 
 * @author shqkel1863
 *
 */
public class User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String userId;
	private String password;
	private String userName;
	private String profileImage;
	private String statusMessage;
	private volatile List<String> friendsList;
	private volatile List<String> chatList;
	
	
	public User() {}

	
	public User(String userId) {
		super();
		this.userId = userId;
	}


	public User(String userId, String password) {
		super();
		this.userId = userId;
		this.password = password;
	}
	

	public User(String userId, String password, String userName, String profileImage, String statusMessage,
			List<String> friendsList, List<String> chatList) {
		super();
		this.userId = userId;
		this.password = password;
		this.userName = userName;
		this.profileImage = profileImage;
		this.statusMessage = statusMessage;
		this.friendsList = friendsList;
		this.chatList = chatList;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public List<String> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<String> friendsList) {
		this.friendsList = friendsList;
	}

	public List<String> getChatList() {
		return chatList;
	}

	public void setChatList(List<String> chatList) {
		this.chatList = chatList;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", userName=" + userName + ", profileImage="
				+ profileImage + ", statusMessage=" + statusMessage + ", friendsList=" + friendsList + ", chatList="
				+ chatList + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
	
	

	
	
	
	
	
}
