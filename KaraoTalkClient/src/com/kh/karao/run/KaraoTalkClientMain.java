package com.kh.karao.run;

import com.kh.karao.view.MainFrame;

public class KaraoTalkClientMain {

	public static void main(String[] args) {
		//serverHostName입력할 것(serverIp는 내부적으로 처리)
		//Run Configuration - arguments - string_prompt 선택.
		new MainFrame(args[0]);
	}

}
