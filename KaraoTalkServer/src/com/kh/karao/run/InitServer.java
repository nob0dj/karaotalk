package com.kh.karao.run;

import com.kh.karao.chat.model.io.ChatIO;
import com.kh.karao.user.model.io.UserIO;

public class InitServer {

	public static void main(String[] args){
		//user reset
		new UserIO().init();

		//chatrooms reset
		new ChatIO().init();
		
	}



}
