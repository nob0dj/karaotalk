package com.kh.karao.run;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.kh.karao.chat.model.vo.Chat;

public class Test {
	Map<String, String> a = null;
	
	public static void main(String[] args) {
		Test t = new Test();
		
		Map<String, String> b = t.a;
		//a
		if(b==null)
			t.a = new HashMap<>();
		System.out.println(b);//null

		if(b==null)
			b = t.a = new HashMap<>();
		System.out.println(b);//{}
		
		joptionpaneTest();
	}

	private static void joptionpaneTest() {
		int selected = JOptionPane.showConfirmDialog(null, "test");
		System.out.println("selected="+selected);
		//yes = 0
		//no = 1
		//cancel = 2
	}
	
	

}
