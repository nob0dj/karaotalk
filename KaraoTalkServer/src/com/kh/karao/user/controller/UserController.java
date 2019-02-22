package com.kh.karao.user.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kh.karao.user.model.io.UserIO;
import com.kh.karao.user.model.vo.User;

public class UserController {
	public static final int LOGIN_OK = 1;
	public static final int WRONG_PASSWORD = 0;
	public static final int NON_EXIST_USERID = -1;
	
	private UserIO userIO = new UserIO();
	
	public static User LOGIN_USER = null;//로그인한 사용자정보
	
	public int loginCheck(User u){
		int result = -1;
		//현재 등록된 User목록을 리턴함.
		Map<String,User> userMap = userIO.getUser();
		
		if(userMap.containsKey(u.getUserId())) {
			User userFromIO = userMap.get(u.getUserId());
			if(u.getPassword().equals(userFromIO.getPassword())){
				result = 1;
				//로그인한 회원정보 유지를 위해서 static필드에 저장함.
				LOGIN_USER = userFromIO;
			}
			else result = 0;
		}
		
		System.out.println("result="+result);
		return result;
	}
	
	public User getUser(String userId){
		return userIO.getUser(userId);
	}

	public Map<String, User> getUser(){
		return userIO.getUser();
	}

	public List<User> findUsersByUserId(String s) {
		Map<String,User> userMap = userIO.getUser();
		Set<String> keySet = userMap.keySet();
		List<User> resultList = new ArrayList<>();
		
		for(String userId: keySet){
			if(userId.contains(s)){
				resultList.add(userMap.get(userId));
			}
		}
		
		return resultList;
	}
}
